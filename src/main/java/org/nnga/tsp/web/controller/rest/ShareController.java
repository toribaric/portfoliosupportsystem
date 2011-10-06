package org.nnga.tsp.web.controller.rest;


import org.nnga.tsp.persistence.entity.Share;
import org.nnga.tsp.persistence.provider.ShareDataProvider;
import org.nnga.tsp.processor.ForecastRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(value="/shares")
public class ShareController {

    private ShareDataProvider shareDataProvider;
    private ForecastRequestProcessor forecastRequestProcessor;

    @RequestMapping( value = "list", method = RequestMethod.GET )
    @ResponseBody
    public List<Share> getAll() {
        return shareDataProvider.getAll();
    }

    @RequestMapping( value = "list/{shareId}", method = RequestMethod.GET )
    @ResponseBody
    public Share getById(@PathVariable int shareId) {
        return shareDataProvider.getById(shareId);
    }

    @RequestMapping( value = "forecast/{shareId}/{forecastDate}", method = RequestMethod.GET )
    @ResponseBody
    public List<Map<String, Object>> getForecast(@PathVariable int shareId, @PathVariable String forecastDate) {
        return forecastRequestProcessor.getResults(shareId, forecastDate);
    }

    @Autowired
    public void setShareDataProvider(ShareDataProvider shareDataProvider) {
        this.shareDataProvider = shareDataProvider;
    }

    @Autowired
    public void setForecastRequestProcessor(ForecastRequestProcessor forecastRequestProcessor) {
        this.forecastRequestProcessor = forecastRequestProcessor;
    }

}
