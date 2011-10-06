package org.nnga.tsp.processor;

import java.util.List;
import java.util.Map;

public interface ForecastRequestProcessor {
    List<Map<String, Object>> getResults(int shareId, String date);
}
