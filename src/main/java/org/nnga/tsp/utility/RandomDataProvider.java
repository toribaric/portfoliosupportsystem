package org.nnga.tsp.utility;

import java.util.Random;

public class RandomDataProvider {
    public static double randomDoubleFromRange(double rangeStart, double rangeEnd) {
        Random random = new Random();
        double number = rangeStart + random.nextDouble() * (rangeEnd - rangeStart);
        return number;
    }
}
