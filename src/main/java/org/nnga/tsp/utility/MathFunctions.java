package org.nnga.tsp.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathFunctions {
    public static Map<String, List<Double>> discreteFourierTransform(List<Double> realTimeData) {

        Map<String, List<Double>> complexFrequencyData = new HashMap<String, List<Double>>();
        List<Double> realFrequencyData = new ArrayList<Double>();
        List<Double> imgFrequencyData = new ArrayList<Double>();

        int dataLength = realTimeData.size();

        for( int f = 0; f < dataLength; f++ ) {
            double realFrequency = 0;
            double imgFrequency = 0;

            for( int i = 0; i < dataLength; i++ ) {
                double phaseAngle = (2 * Math.PI * f * i) / dataLength;
                realFrequency += realTimeData.get(i) * Math.cos(phaseAngle) + 0 * Math.sin(phaseAngle);
                imgFrequency += 0 * Math.cos(phaseAngle) - realTimeData.get(i) * Math.sin(phaseAngle);
            }

            realFrequencyData.add(realFrequency);
            imgFrequencyData.add(imgFrequency);
        }

        complexFrequencyData.put("realData", realFrequencyData);
        complexFrequencyData.put("imgData", imgFrequencyData);

        return complexFrequencyData;

    }
}


