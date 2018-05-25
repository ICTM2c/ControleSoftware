package BPPSimulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import BPPSimulator.Simulators.BPPAlgorithm;
import Models.Box;
import Models.Product;

import java.util.List;

public class BppSimulatorVisualizer extends JPanel {
    private int SizeProduct;
    private List<Box> _boxes = new ArrayList<>();
    private final int boxWidth = 50;
    private final int boxHeight = 404;
    private final int doosYas = 100;
    private final int StringHeight = 83;
    BPPAlgorithm _simulator;
    private List<Product> _products = new ArrayList<>();

    public BppSimulatorVisualizer() {
        this.setPreferredSize(new Dimension(400, 500));

    }

    public void runSimulation() {
        final int CAPACITY = 50;
        _boxes = _simulator.simulate(CAPACITY, _products);
        repaint();
    }

    public void setSimulator(BPPAlgorithm simulator) {
        _simulator = simulator;
        runSimulation();
    }

    public void setProducts(List<Product> products) {
        _products = products;
        runSimulation();
    }

    public void set_SizeProduct(int Size) {
        this.SizeProduct = Size;
    }

    public void setBoxes(List<Box> boxes) {
        _boxes = boxes;
    }

    public void tekenProduct(Graphics g) {
        super.paintComponent(g);
        //    ArrayList<Color> Kleuren = new ArrayList<>();
        setBackground(Color.WHITE);
        int boxHeight = 400;

        for (int i = 0; i < _boxes.size(); i++) {                           //loopt de verschillende dozen
            int Xas = 75 + (150 * i);                                       //Xas pos berekening
            int PixelCounter = 0;                                           //counter om begin hoogte weer te geven
            int Yas = 500 + PixelCounter;                                   //yas pos berekening
            int pixelsPerSlot = 400 / Box.getCapacity();                    //pixels per 1 size
            int remainingSize = 400;
            int DoosCounter = i + 1;
            String doostekst = "doos" + DoosCounter;
            g.setColor(Color.BLACK);
            g.drawRect(Xas- 1, doosYas - 1, boxWidth + 1, boxHeight);

            g.drawString(doostekst, Xas + 10, StringHeight + 12);
            if(_boxes.get(i).getCapacityLeftOver() == 0){
                g.drawString("Vol", Xas + 10, StringHeight);
            }else {
                g.drawString(_boxes.get(i).getCapacityLeftOver() + " over", Xas + 5, StringHeight);
            }
            for (int j = 0; j < _boxes.get(i).getProducts().size(); j++) {                              //loopt producten van de doos door
                int size = _boxes.get(i).getProducts().get(j).getSize();                                //size = size van product in doos
                Color kleur1 = _boxes.get(i).getProducts().get(j).getColor();
                g.setColor(kleur1);
                g.fillRect(Xas,  (doosYas + remainingSize) - (size * pixelsPerSlot), boxWidth, size * pixelsPerSlot);        //teken rect
                if (_boxes.get(i).getProducts().get(j).getIsPickedUp()) {
//                    g.setColor(getContrastColor(kleur1));
                    g.fillRect(Xas - 20,  (doosYas + remainingSize) - (size * pixelsPerSlot) + 10, 20, size * pixelsPerSlot - 20);
//                    g.drawRect(Xas,(doosYas + remainingSize) - (size * pixelsPerSlot), boxWidth, size * pixelsPerSlot);
                }
                remainingSize -= size * pixelsPerSlot;
                PixelCounter = PixelCounter + size * pixelsPerSlot;                                    //geeft pixelcounter nieuwe waarde
            }
        }
    }

    private static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    @Override
    public void paintComponent(Graphics g) {
        tekenProduct(g);
        if (true){
            return;
        }
    }

    public List<Models.Box> getBoxes() {
        return _boxes;
    }

    public boolean isFirstBox(Box correspondingBox) {
        return _boxes.indexOf(correspondingBox) == 0;
    }

    public void refresh() {
        repaint();
    }
}
