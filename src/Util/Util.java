package Util;

import Models.*;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Util {
    /**
     * https://stackoverflow.com/a/27461352
     * <p>
     * <p>
     * Draw an arrow line between two points.
     *
     * @param g  the graphics component.
     * @param x1 x-position of first point.
     * @param y1 y-position of first point.
     * @param x2 x-position of second point.
     * @param y2 y-position of second point.
     * @param d  the width of the arrow.
     * @param h  the height of the arrow.
     */
    public static void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(xpoints, ypoints, 3);
    }

    /**
     * Makes sure that 'val' doesn't exceed or either the minimum or maximum value.
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static int clamp(int val, int min, int max) {
        return (val < min) ? min : ((val > max) ? max : val);
    }

    /**
     * https://www.arduino.cc/reference/en/language/functions/math/map/
     *
     * Re-maps a number from one range to another. That is, a value of fromLow would get mapped to toLow, a value of fromHigh to toHigh, values in-between to values in-between, etc.
     * @param x
     * @param in_min
     * @param in_max
     * @param out_min
     * @param out_max
     * @return
     */
    public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static <T> void swap(java.util.List<T> lst, int x, int y) {
        T temp = lst.get(x);
        lst.set(x, lst.get(y));
        lst.set(y, temp);
    }

    public static void CreateDeliveryNote(List<Box> boxList, Order order) { //HTML print methode
        int orderId = order.getOrderId();
        Customer customer = order.getCustomer();
        Address address = order.getAddress();
        String template = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>td {text-align: center} th {background-color: lightgrey;}</style>" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<div style='display:flex;justify-content: space-around;width: 400px;'>\n" +
                "<table>\n" +
                "\t<tr><th>Bezorgadres</th></tr>\n" +
                "\t<tr><td>" + customer.getName() + "</td></tr>\n" +
                "\t<tr><td>" + address.getAddress() + "</td></tr>\n" +
                "\t<tr><td>" + address.getPostalCode() + " " + address.getCity() + "</td></tr>\n" +
                "</table>\n" +
                "<table>\n" +
                "\t<tr><th>Verstuuradres</th></tr>\n" +
                "\t<tr><td>Windesheim</td></tr>\n" +
                "\t<tr><td>Campus 2</td></tr>\n" +
                "\t<tr><td>8000 AA Zwolle</td></tr>\n" +
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "<table style='width: 400px'>\n" +
                "\t<tr><th style='width:50%'>Product Nummer</th><th style='width:50%'>Grootte</th></tr>\n";
        for (Box box : boxList) {
            template += "<tr><th colspan='2'>Box " + box.getBoxId() + "</th></tr>";
            for (Product product : box.getProducts()) {
                template += "<tr><td style='width:50%'>" + product.getProductId() + "</td><td style='width:50%'>" + product.getSize() + "</td></tr>";
            }
        }
            template +=
                "</table>\n" +
                "</body>\n" +
                "</html>";
        String file = "order_" + orderId + ".html";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(template);
            bw.close();

            // Open the html file in the default program of this computer.
            Desktop.getDesktop().open(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
