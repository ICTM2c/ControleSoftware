package TSPSimulator;

import TSPSimulator.Simulators.TspSimulator;
import javafx.geometry.Point2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TspSimulatorVisualizer extends JPanel {
    //region Fields
    private int _sizeX = 5;
    private int _sizeY = 5;
    private boolean[] _clicked;
    private boolean[] _pickedUp;

    private TspSimulator _simulator;
    private List<Point2D> _route;
    //endregion

    public TspSimulatorVisualizer() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);
        fillClicked();
    }


    //region GridClicked
    private void fillClicked() {
        _clicked = new boolean[_sizeX * _sizeY];
        _pickedUp = new boolean[_sizeX * _sizeY];
    }

    public void clearClicked() {
        fillClicked();
    }

    public void setIsClicked(int x, int y, boolean clicked) {
        int index = makeIndex(x, y);
        _clicked[index] = clicked;
    }

    public void setIsPicked(int x, int y, boolean pickedUp) {
        int index = makeIndex(x, y);
        _pickedUp[index] = pickedUp;
        drawPanel();
    }

    public int makeIndex(int x, int y) {
        return x + y * _sizeX;
    }

    public boolean isSelected(int x, int y) {
        int index = makeIndex(x, y);
        return _clicked[index];
    }

    public void toggleIsSelected(int x, int y) {
        int index = makeIndex(x, y);
        _clicked[index] = !_clicked[index];
    }
    //endregion


    //region Painting
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawPanel(g);

        drawSelectedSquares(g);

        drawRoute(g, _route, _simulator);
    }

    /**
     * Paints the resulting route of the provided simulator.
     * @param g
     * @param route
     * @param simulator
     */
    private void drawRoute(Graphics g, List<Point2D> route, TspSimulator simulator) {
        g.setColor(Color.RED);
        double squareWidth = ((double) getWidth() / (double) _sizeX);
        double squareHeight = ((double) getHeight() / (double) _sizeY);


        int numSimulators = 1;
        double offsetWidth = squareWidth / (numSimulators + 1);
        double offsetHeight = squareHeight / (numSimulators + 1);

        g.setColor(simulator.getColor());

        int currentSimulatorIndex = 1;
        for (int i = 0; i < route.size() - 1; i++) {
            Point2D current = route.get(i);
            Point2D next = route.get(i + 1);
            Util.drawArrowLine(g,
                    (int) (current.getX() * squareWidth + (currentSimulatorIndex * offsetWidth)),
                    (int) (current.getY() * squareHeight + (currentSimulatorIndex * offsetHeight)),
                    (int) (next.getX() * squareWidth + (currentSimulatorIndex * offsetWidth)),
                    (int) (next.getY() * squareHeight + (currentSimulatorIndex * offsetHeight)),
                    8, 8
            );
        }
    }


    private void drawSelectedSquares(Graphics g) {
        int squareWidth = (int) Math.ceil((double) getWidth() / (double) _sizeX);
        int squareHeight = (int) Math.ceil((double) getHeight() / (double) _sizeY);

        for (int x = 0; x < _sizeX; x++) {
            int coordX = (int) ((double) x / (double) _sizeX * (double) getWidth());
            for (int y = 0; y < _sizeY; y++) {
                int coordY = (int) ((double) y / (double) _sizeY * (double) getHeight());
                if (isSelected(x, y)) {
                    if (isPickedUp(x, y)) {
                        g.setColor(new Color(33, 170, 0));
                    }
                    else {
                        g.setColor(Color.BLACK);
                    }
                    g.fillRect(coordX, coordY, squareWidth, squareHeight);
                }
            }
        }

        // Create a green square at the start/end point
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect((int) ((double)(_sizeX - 1) / (double) _sizeX * (double) getWidth()), (int) ((double) 3.0 / (double) _sizeY * (double) getHeight()), squareWidth, squareHeight);
    }

    private boolean isPickedUp(int x, int y) {
        return _pickedUp[makeIndex(x, y)];
    }

    private void drawPanel(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < _sizeX; x++) {
            int absoluteX = (int) ((double) getWidth() / (double) _sizeX * (double) x);
            g.drawLine(absoluteX, 0, absoluteX, getHeight());
        }
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

        for (int y = 0; y < _sizeY; y++) {
            int absoluteY = (int) ((double) getHeight() / (double) _sizeY * (double) y);
            g.drawLine(0, absoluteY, getWidth(), absoluteY);
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void drawPanel() {
        repaint();
    }

    //endregion


    public List<Point2D> setSimulator(TspSimulator selectedItem) {
        _simulator = selectedItem;

        runSimulation();

        return _route;
    }

    public List<Point2D> runSimulation() {
        List<Point2D> points = getPoints();
        Point2D startEndPoint = new Point2D(_sizeX - 1, 3);
        _route =_simulator.simulate(startEndPoint, points);
        drawPanel();
        return _route;
    }


    /**
     * Return a list of all the squares on the grid which are selected.
     * @return
     */
    public List<Point2D> getPoints() {
        List<Point2D> list = new ArrayList<>();
        for (int x = 0; x < _sizeX; x++) {
            for (int y = 0; y < _sizeY; y++) {
                if (isSelected(x, y)) {
                    list.add(new Point2D(x, y));
                }
            }
        }
        return list;
    }

    /**
     * Set the X size (width) of the grid
     * @param integer
     */
    public void setSizeX(Integer integer) {
        _sizeX = integer;
        fillClicked();
        drawPanel();
    }

    /**
     * Set they Y size (height) of the grid.
     * @param integer
     */
    public void setSizeY(Integer integer) {
        _sizeY = integer;
        fillClicked();
        drawPanel();
    }

    public List<Point2D> getRoute() {
        ArrayList<Point2D> copy = new ArrayList<>(_route);
        copy.remove(0);
        copy.remove(copy.size() - 1);
        return copy;
    }
}
