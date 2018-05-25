package ControleSoftware.Exceptions;

public class TooManyProductsException extends Throwable {
    private int _numProducts;
    private int _maxProducts;
    public TooManyProductsException(int numProducts, int maxProducts) {
        _numProducts = numProducts;
        _maxProducts = maxProducts;
    }

    public int getNumProducts() {
        return _numProducts;
    }

    public int getMaxProducts() {
        return _maxProducts;
    }
}
