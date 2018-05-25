package Models;

import javafx.geometry.Point2D;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product implements Comparable<Product> {
    private int _productId;
    private int _size;
    private Point2D _location;
    private Color _color = new Color((int) (Math.random() * 0x1000000));
    private boolean _isPickedUp = false;

    public Product(int productId, int size) {
        _productId = productId;
        _size = size;
    }


    public void setIsPickedUp(boolean isPickedUp){
        _isPickedUp = isPickedUp;
    }

    public boolean getIsPickedUp() {
        return _isPickedUp;
    }

    public void setPosition(int x, int y) {
        _location = new Point2D(x, y);
    }

    /**
     * Creates a new Product object based on the provided ResultSet
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Product fromResultSet(ResultSet rs) throws SQLException {
        Product pr = new Product(rs.getInt("productid"), rs.getInt("size"));
        pr.setPosition(rs.getInt("X"), rs.getInt("Y"));
        return pr;
    }

    public Point2D getLocation() {
        return _location;
    }

    public int getSize() {
        return _size;
    }

    @Override
    public String toString() {
        return "ProductId " + getProductId() + ", Size: " + getSize();
    }

    private int getProductId() {
        return _productId;
    }

    @Override
    public int compareTo(Product o) {
        return o.getSize() - getSize();
    }

    public Color getColor() {
        return _color;
    }
}
