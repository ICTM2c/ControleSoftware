package BPPSimulator.Simulators;

import Models.Box;
import Models.Product;

import java.util.ArrayList;
import java.util.List;

public class FirstFit implements BPPAlgorithm {

    @Override
    public List<Box> simulate(int capacity, List<Product> products) {
        ArrayList<Box> ListBox = new ArrayList<Box>();
        Box box1 = new Box(capacity);       //De begin doos
        ListBox.add(box1);

        for (Product product : products) {
            boolean didAdd = false;
            for (int j = 0; j < ListBox.size() ; j++) {
                Box box = ListBox.get(j);
                if (box.addProduct(product)) {
                    didAdd = true;
                    break;
                }
            }

            if (!didAdd) {
                Box newBox = new Box();     //Maakt nieuwe doos met capacity en haal er de size van het het product vanaf
                newBox.addProduct(product); //Voegt Product toe aan box
                ListBox.add(newBox);        //Voegt box toe aan list
            }
        }
        return ListBox;
    }


    @Override
    public String toString() {
        return "First fit";
    }
}


