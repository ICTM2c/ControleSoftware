package BPPSimulator.Simulators;
import Models.Product;

import javax.swing.*;
import java.util.List;
import Models.Box;

public interface BPPAlgorithm {

    List<Box> simulate(int capacity, List<Product> products);
}
