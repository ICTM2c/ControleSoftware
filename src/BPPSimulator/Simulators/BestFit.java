package BPPSimulator.Simulators;

import Models.Box;
import Models.Product;

import java.util.ArrayList;
import java.util.List;

public class BestFit implements BPPAlgorithm {
    //Het best fit algoritme vult de doos met de minste capaciteit over. Waar het product wel in past.

    @Override
    public List<Box> simulate(int capacity, List<Product> products) {
        ArrayList<Box> ListBox = new ArrayList<Box>();
        ArrayList<Box> ListBoxFit = new ArrayList<Box>();   //List met passende dozen
        Box box1 = new Box(capacity);
        ListBox.add(box1);
        boolean noFit = false;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            List<Box> boxCandidates = new ArrayList();

            for (Box box : ListBox) {
                if (box.canFit(product)) {
                    boxCandidates.add(box);
                }
            }

            if (boxCandidates.isEmpty()) {
                Box box2 = new Box(capacity);
                box2.addProductDirect(product);
                ListBox.add(box2);
            }
            else {
                Box smallestCapacityLeftOver = boxCandidates.stream().min((b1, b2) -> b1.compareTo(b2)).get();
                smallestCapacityLeftOver.addProductDirect(product);
            }
        }
        return ListBox;
    }


    @Override
    public String toString() {
        return "Best Fit";
    }
}
