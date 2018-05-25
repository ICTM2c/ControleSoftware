package BPPSimulator.Simulators;
import Models.Box;
import Models.Product;

import java.util.List;

public class FirstFitDecreasing extends FirstFit {
    //Het first fit decreasing algoritme doet hetzelfde als first fit,
    // maar voordat hij begint sorteert hij de producten op volgorde van grootte, van groot naar klein.

    @Override
    public List<Box> simulate(int capacity, List<Product> products) {
        // Sort the products first
        products.sort((p1, p2) -> p1.compareTo(p2));

        // Pass the sorted array over to the FirstFit algorithm.
        return super.simulate(capacity, products);
    }

    @Override
    public String toString() {
        return "First fit decreasing";
    }
}
