package ControleSoftware;

import BPPSimulator.BppSimulatorVisualizer;
import BPPSimulator.Simulators.*;
import ControleSoftware.Components.HeaderPanel;
import ControleSoftware.Exceptions.TooManyProductsException;
import Database.DbProduct;
import Database.Exceptions.OrderNotFoundException;
import Models.Box;
import Models.Order;
import Models.Product;
import TSPSimulator.Simulators.*;
import TSPSimulator.TspSimulatorVisualizer;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.geometry.Point2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GUI extends JFrame implements ActionListener, SerialPortDataListener {
    private final Panel pnlUserInteraction;
    private TspSimulatorVisualizer pnlTSPSimulator;
    private BppSimulatorVisualizer pnlBPPSimulator;
    private JButton btnSelectOrder;
    private JComboBox<TspSimulator> cbTSPSimulators;
    private JComboBox<BPPAlgorithm> cbBPPSimulators;
    private JComboBox<SerialPort> cbTspComPort;
    private JComboBox<SerialPort> cbBppComPort;
    private JButton btnRefreshCommPort;

    private final int MAX_NUM_PRODUCTS = 3;
    private JButton btnSendToArduino;
    private OutputStream retrieveRobotOutputStream;

    public GUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(1, 3));
        setSize(1600, 600);


        pnlTSPSimulator = new TspSimulatorVisualizer();
        add(pnlTSPSimulator);

        pnlUserInteraction = new Panel();
        pnlUserInteraction.setLayout(new GridLayout(0, 2, 0, 0));
        pnlUserInteraction.setPreferredSize(new Dimension(500, 450));

        initializeCommPorts();
        initializeSimulatorComponents();
        initializeFileSelectionComponents();
        initializeSendToArduinoComponents();
        add(pnlUserInteraction);

        pnlBPPSimulator = new BppSimulatorVisualizer();
        add(pnlBPPSimulator);

        pnlBPPSimulator.setSimulator((BPPAlgorithm)cbBPPSimulators.getSelectedItem());
        pnlTSPSimulator.setSimulator((TspSimulator)cbTSPSimulators.getSelectedItem());


        setVisible(true);
    }

    private void initializeSendToArduinoComponents() {
        btnSendToArduino = new JButton("Start");
        btnSendToArduino.addActionListener(this);
        HeaderPanel pnlArduino = new HeaderPanel("Arduino", btnSendToArduino);
        pnlUserInteraction.add(pnlArduino);
    }

    private void initializeFileSelectionComponents() {
        btnSelectOrder = new JButton("Bestand");
        btnSelectOrder.addActionListener(this);
        pnlUserInteraction.add(new HeaderPanel("Invoeren bestand", btnSelectOrder));
    }

    private void initializeSimulatorComponents() {
        cbTSPSimulators = new JComboBox<>(new TspSimulator[] {new SimulatorGreedyForce(), new SimulatorGreedy(), new SimulatorTwoOpt(), new SimulatorBruteForce()});
        cbTSPSimulators.addActionListener(this);
        cbBPPSimulators = new JComboBox<BPPAlgorithm>(new BPPAlgorithm[] {new BestPickFit(), new BestFit(), new FirstFit(), new FirstFitDecreasing()});
        cbBPPSimulators.addActionListener(this);

        HeaderPanel simulatorPanel = new HeaderPanel("Simulators", null,
                new HeaderPanel("TSP simulator", null, cbTSPSimulators),
                new HeaderPanel("BPP simulator", null, cbBPPSimulators)
        );
        pnlUserInteraction.add(simulatorPanel);
    }

    private void initializeCommPorts() {
        cbTspComPort = new JComboBox<SerialPort>();
        cbBppComPort = new JComboBox<SerialPort>();
        btnRefreshCommPort = new JButton("Refresh");
        btnRefreshCommPort.addActionListener(this);
        HeaderPanel commPortPanel = new HeaderPanel("Selecteren COM poort", btnRefreshCommPort,
                new JLabel("TSPSimulator: "), cbTspComPort,
                new JLabel("BPPSimulator: "), cbBppComPort
        );
        pnlUserInteraction.add(commPortPanel);

        fillCommPortComboBoxes();
    }

    private void fillCommPortComboBoxes() {
        List<SerialPortWrapper> ports = Arrays.asList(SerialPort.getCommPorts()).stream().map(port -> new SerialPortWrapper(port)).collect(Collectors.toList());
        cbTspComPort.setModel(new DefaultComboBoxModel(ports.toArray()));
        cbBppComPort.setModel(new DefaultComboBoxModel(ports.toArray()));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnSelectOrder) {
            handleNewOrder();
        }
        else if (src == cbTSPSimulators) {
            handleChangeTspSimulator();
        }
        else if (src == cbBPPSimulators) {
            handleChangeBppSimulator();
        }
        else if (src == btnRefreshCommPort) {
            handleRefreshCommPort();
        }
        else if (src == btnSendToArduino) {
            if (cbBppComPort.getSelectedItem() == cbTspComPort.getSelectedItem()) {
                JOptionPane.showMessageDialog(this, "The TSP and BPP robot must use a different COM port");
            }
            else {
                new Thread(() -> {
                    handleStartArduino();
                }).start();
            }
        }
    }

    private void handleRefreshCommPort() {
        fillCommPortComboBoxes();
    }

    private void handleChangeBppSimulator() {
        pnlBPPSimulator.setSimulator((BPPAlgorithm)cbBPPSimulators.getSelectedItem());
    }

    private void handleChangeTspSimulator() {
        pnlTSPSimulator.setSimulator((TspSimulator) cbTSPSimulators.getSelectedItem());

    }

    private void handleNewOrder() {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File f = fc.getSelectedFile();
        try (FileInputStream br = new FileInputStream(f.getAbsolutePath())) {
            // Read the order file.
            String json = new String(br.readAllBytes(), "UTF-8");

            // Deserialize it from Json to an Order object.
            Gson deserializer = new Gson();
            Order order = deserializer.fromJson(json, Order.class);

            // Find all the products which are linked to the order including their location.
            List<Models.Product> products = DbProduct.Get().findProductsForOrder(order.getOrder());

            if (products.size() > 3) {
                throw new TooManyProductsException(products.size(), MAX_NUM_PRODUCTS);
            }

            runTspSimulation(products);
            runBppSimulation(products);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to retrieve the order from the database.");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "The file could not be found. Has it been removed?");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to open the order file.");
        } catch (JsonSyntaxException e) {
            JOptionPane.showMessageDialog(this, "The file is not an order file.");
        } catch (OrderNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        } catch (TooManyProductsException e) {
            JOptionPane.showMessageDialog(this, "Too many products. The maximum number of products is " + e.getMaxProducts() + " while there were " + e.getNumProducts() + " in this order.");
        }
    }

    private void runBppSimulation(List<Product> products) {
        pnlBPPSimulator.setProducts(products);
    }

    private void runTspSimulation(List<Product> products) {
        // Reset the size of the grid. This will also reset the selected squares.
        pnlTSPSimulator.setSizeX(5);
        pnlTSPSimulator.setSizeY(5);

        // Fill the products in the grid.
        for (Models.Product product : products) {
            pnlTSPSimulator.setIsClicked((int) product.getLocation().getX(), (int) product.getLocation().getY(), true);
        }
        pnlTSPSimulator.runSimulation();
    }

    private void preparePort(SerialPort port) {
        port.setBaudRate(9600);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
        port.openPort();
    }

    private void sendToSerialPort(SerialPort port, String str) {
        System.out.println("Sending \"" + str + "\" to + " +port.getPortDescription());
        byte[] bytes = str.getBytes();
        port.writeBytes(bytes, bytes.length);
    }

    private void handleStartArduino() {
        SerialPort portRetrieveRobot = ((SerialPortWrapper)cbTspComPort.getSelectedItem()).getPort();
        SerialPort portSortRobot = ((SerialPortWrapper)cbBppComPort.getSelectedItem()).getPort();
        btnSendToArduino.setEnabled(false);

        preparePort(portRetrieveRobot);
        preparePort(portSortRobot);


        try {
            // Wait for a few seconds before sending data. Otherwise the data is never received.
            Thread.sleep(2000);

            while (!portRetrieveRobot.isOpen() || !portSortRobot.isOpen()) {
                Thread.sleep(1);
            }

            List<Point2D> route = pnlTSPSimulator.getRoute();
            for (Point2D point : route) {
                String coords = (int)(point.getX() + 1) + "," + ((int)point.getY() + 1);
                sendToSerialPort(portRetrieveRobot, coords);
                waitUntilDone(portRetrieveRobot);
                pnlTSPSimulator.setIsPicked((int)point.getX(), (int)point.getY(), true);
                System.out.println(point.toString() + " has been picked up");
            }

            sendToSerialPort(portRetrieveRobot, "9,9");
            waitUntilDone(portRetrieveRobot);

            // The sorting robot will get the products in reverse
            Collections.reverse(route);
            List<Box> boxes = route.stream().map(x -> findBoxByCoordinate(x, pnlBPPSimulator.getBoxes())).collect(Collectors.toList());
            for (int i = 0; i < route.size(); i++) {
                Point2D point = route.get(i);
                Box correspondingBox = boxes.get(i);
                sendToSerialPort(portRetrieveRobot, "dropNext;");

                waitUntilDone(portRetrieveRobot);

                String direction = pnlBPPSimulator.isFirstBox(correspondingBox) ? "l" : "r";
                sendToSerialPort(portSortRobot, direction);

                waitUntilDone(portSortRobot);
                findProductByCoordinate(point, pnlBPPSimulator.getBoxes()).setIsPickedUp(true);
                pnlBPPSimulator.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            portRetrieveRobot.closePort();
            portSortRobot.closePort();
            btnSendToArduino.setEnabled(true);
        }
    }

    private void waitUntil(SerialPort port, String valueToWaitFor) throws UnsupportedEncodingException  {
        String output = "";
        while (true)
        {
            byte[] readBuffer = new byte[port.bytesAvailable()];
            if (readBuffer.length == 0) {
                continue;
            }

            int numRead = port.readBytes(readBuffer, readBuffer.length);
            String receivedString = new String(readBuffer, 0, numRead, "UTF8");
            output += receivedString;
            if (output.endsWith(valueToWaitFor)) {
                break;
            }
        }
        System.out.println(output);
    }

    private void waitUntilDone(SerialPort port) throws UnsupportedEncodingException {
        waitUntil(port, "done");
    }

    private Box findBoxByCoordinate(Point2D point, List<Models.Box> boxes) {
        for (Box box : boxes) {
            for (Product prod : box.getProducts()) {
                if (prod.getLocation().equals(point)) {
                    return box;
                }
            }
        }
        return null;
    }

    private Product findProductByCoordinate(Point2D point, List<Models.Box> boxes) {
        for (Box box : boxes) {
            for (Product prod : box.getProducts()) {
                if (prod.getLocation().equals(point)) {
                    return prod;
                }
            }
        }
        return null;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        byte[] newData = new byte[event.getSerialPort().bytesAvailable()];
        event.getSerialPort().readBytes(newData, newData.length);
        String data = new String(newData, StandardCharsets.UTF_8);
        System.out.println(data);
    }
}
