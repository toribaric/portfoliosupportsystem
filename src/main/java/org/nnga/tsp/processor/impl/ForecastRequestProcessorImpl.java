package org.nnga.tsp.processor.impl;

import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.entity.SharePrice;
import org.nnga.tsp.persistence.provider.ShareDataProvider;
import org.nnga.tsp.processor.ForecastRequestProcessor;
import org.nnga.tsp.processor.NeuralNetworkProcessor;
import org.nnga.tsp.utility.DateUtility;
import org.springframework.beans.factory.annotation.Required;
import java.util.*;

/*
 * TODO: extend forecast results and forecasting of missing historical data to support more than 1 output
 */
public class ForecastRequestProcessorImpl implements ForecastRequestProcessor {

    private ShareDataProvider shareDataProvider;
    private NeuralNetworkProcessor<Double> neuralNetworkProcessor;

    @Override
    public List<Map<String, Object>> getResults(int shareId, String date) {
        List<Map<String, Object>> forecastResults = new ArrayList<Map<String, Object>>();

        try {
            Date forecastDate = DateUtility.getDateFromString(date, "dd-MM-yyyy");

            List<SharePrice> sharePrices = shareDataProvider.getSharePrices(shareId);
            if( sharePrices == null ) {
                return processorError("No historical prices defined for submitted share (shareId=" + shareId + ")", forecastResults);
            }

            List<NeuralNetwork> neuralNetworks = shareDataProvider.getNeuralNetworks(shareId);
            if( neuralNetworks == null ) {
                return processorError("No neural networks defined for submitted share (shareId=" + shareId + ")", forecastResults);
            }

            for( NeuralNetwork neuralNetwork : neuralNetworks ) {
                List<Double> networkInputs = getNetworkInputs(sharePrices, neuralNetwork, forecastDate);
                List<Double> networkOutputs = neuralNetworkProcessor.process(neuralNetwork, networkInputs, false);

                Map<String, Object> networkResults = new HashMap<String, Object>();
                networkResults.put("networkId", neuralNetwork.getId());
                networkResults.put("validationError", neuralNetwork.getValidationError());
                networkResults.put("rSquared", neuralNetwork.getR2());
                networkResults.put("errorEnergy", neuralNetwork.getErrorEnergy());
                networkResults.put("output", networkOutputs.get(0));

                forecastResults.add(networkResults);
            }

        } catch( Exception e ) {
            return processorError(e.getMessage(), forecastResults);
        }

        return forecastResults;
    }

    private List<Double> getNetworkInputs(List<SharePrice> sharePrices, NeuralNetwork neuralNetwork, Date forecastDate) throws Exception {
        List<Double> networkInputs = new ArrayList<Double>();

        int numInputs = neuralNetwork.getNumInputNeurons();
        if( numInputs > sharePrices.size() ) {
            throw new IllegalAccessException("Not enough historical prices for current neural network forecast for current share (neural network id: '" + neuralNetwork.getId() + "', required inputs: '" + numInputs + "', available inputs: '" + sharePrices.size() + "')");
        }

        Date lastStoredDate = sharePrices.get(sharePrices.size() - 1).getDate();
        long differenceDays = DateUtility.getDaysDifferenceBetweenDates(lastStoredDate, forecastDate);
        if( differenceDays <= 0 ) {
            throw new IllegalArgumentException("Forecast date must be greater than historical prices dates (forecast date: '" + DateUtility.getSimpleDate(forecastDate, "dd-MM-yyyy") + "', last available price date: '" + DateUtility.getSimpleDate(lastStoredDate, "dd-MM-yyyy") + "'");
        }

        // extract historical prices from database in quantity that corresponds to neural network's number of input neurons
        for( int i = sharePrices.size() - numInputs; i < sharePrices.size(); i++ ) {
            networkInputs.add(sharePrices.get(i).getPrice());
        }

        // if we're forecasting for more than one day ahead, or there is no historical price in database for yesterday (or prices for n days backwards),
        // input data is constructed of forecasts for all those missing days to the day for which we're forecasting - of course, forecast quality
        // falls as more days need to be forecast
        // -> so, the final input data set (made of historical data) can contain:
        //    1. the mix of true and forecast historical data (for example if last historical price is available for 20.10.2011, we're forecasting
        //       for 25.10.2011 and the network has 8 input neurons; than 4 input prices would be true historical prices, and 4 would be "forecast historical" prices -
        //       from 21. to 24.)
        //    2. only forecast historical data (for example if last historical price is available for 20.10.2011, we're forecasting for 30.10.2011 and the
        //       network has 8 input neurons; than all 8 inputs would be forecast - from 22. to 29.)
        if( differenceDays > 1 ) {
            while( differenceDays > 1 ) {
                List<Double> networkOutputs = neuralNetworkProcessor.process(neuralNetwork, networkInputs, false);
                networkInputs.remove(0);
                networkInputs.add(networkOutputs.get(0));
                differenceDays--;
            }
        }

        return networkInputs;
    }

    private List<Map<String, Object>> processorError(String errorMessage, List<Map<String, Object>> result) {
        Map<String, Object> error = new HashMap<String, Object>();
        error.put("error", errorMessage);
        result.add(error);
        return result;
    }

    @Required
    public void setShareDataProvider(ShareDataProvider shareDataProvider) {
        this.shareDataProvider = shareDataProvider;
    }

    @Required
    public void setNeuralNetworkProcessor(NeuralNetworkProcessor<Double> neuralNetworkProcessor) {
        this.neuralNetworkProcessor = neuralNetworkProcessor;
    }
}
