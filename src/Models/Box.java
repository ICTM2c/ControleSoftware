package Models;
import java.util.ArrayList;
import java.util.List;

public class Box implements Comparable<Box> {

    private List<Product> Products;
    private static int Capacity = 50;
    private int CapacityLeftOver;

    public Box() {
        Products = new ArrayList<Product>();
        CapacityLeftOver = Capacity;
    }

    public static void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public Box(int capacity) {
        this();
    }

    public int totalUsed() {
        int counter = 0;
        for (int i = 0; i < Products.size(); i++) {
            counter = counter + Products.get(i).getSize();
        }
        return counter;
    }

    public void addProductDirect(Product product) {
        Products.add(product);
        CapacityLeftOver -= product.getSize();
    }

    public boolean addProduct(Product product) { //Voegt een enkel product toe aan list
        if (canFit(product)) {
            Products.add(product);
            CapacityLeftOver -= product.getSize();
            return true;
        }
        return false;
    }

    public List<Product> getProducts() {
        return Products;
    }

    public void setProducts(List<Product> products) {
        Products = products;
    }

    public static int getCapacity() {
        return Capacity;
    }

    public int getCapacityLeftOver() {
        return CapacityLeftOver;
    }

    @Override
    public int compareTo(Box o) {
        return o.getCapacityLeftOver() - this.getCapacityLeftOver();
    }

    public boolean canFit(Product product) {
        return CapacityLeftOver >= product.getSize();
    }
}