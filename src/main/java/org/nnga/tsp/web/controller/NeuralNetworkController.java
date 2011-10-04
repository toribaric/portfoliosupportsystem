package org.nnga.tsp.web.controller;

import org.apache.commons.io.IOUtils;
import org.nnga.tsp.algorithms.neuralnetwork.types.SupervisedTrainingAlgorithmType;
import org.nnga.tsp.executor.neuralnetwork.TrainingExecutor;
import org.nnga.tsp.observer.NeuralNetworkTrainingObserver;
import org.nnga.tsp.persistence.entity.NeuralNetwork;
import org.nnga.tsp.persistence.processor.NeuralNetworkDataProcessor;
import org.nnga.tsp.processor.NeuralNetworkProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ann")
public class NeuralNetworkController {

    private NeuralNetworkDataProcessor neuralNetworkDataProcessor;
    private TrainingExecutor trainingExecutor;
    private NeuralNetworkTrainingObserver neuralNetworkTrainingObserver;
    private NeuralNetworkProcessor<Double> neuralNetworkProcessor;

    @RequestMapping( value = "list", method = RequestMethod.GET )
    public String list( Model model ) {
        model.addAttribute("success", true);
        List<NeuralNetwork> neuralNetworks = neuralNetworkDataProcessor.getAll();
        model.addAttribute("neuralNetworks", neuralNetworks);
        return "jsonView";
    }

    @RequestMapping( value = "saveNew", method = RequestMethod.POST )
    public String saveNew( Model model, @RequestParam("name") String name, @RequestParam("description") String description,
                           @RequestParam("structure") String structure, @RequestParam("activationFunction") String activationFunction) {
        try {
            neuralNetworkDataProcessor.processNewStructuralData(name, description, structure, activationFunction);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "jsonView";
    }

    @RequestMapping( value = "updateStructure", method = RequestMethod.POST )
    public String updateStructure( Model model, @RequestParam("id") int id, @RequestParam("structure") String structure) {
        try {
            neuralNetworkDataProcessor.processUpdatedStructuralData(id, structure);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "jsonView";
    }

    @RequestMapping( value = "deleteNetwork", method = RequestMethod.POST )
    public String deleteNetwork( Model model, HttpServletRequest httpServletRequest ) throws Exception {
        String json = IOUtils.toString(httpServletRequest.getInputStream());

        neuralNetworkDataProcessor.deleteNetwork(json);

        model.addAttribute("success", true);
        return "jsonView";
    }

    @RequestMapping( value = "updateConfiguration", method = RequestMethod.POST )
    public String updateConfiguration( Model model,
                                       @RequestParam("id") int id,
                                       @RequestParam(value = "activationFunction", required = false) String activationFunction,
                                       @RequestParam(value = "trained", required = false) Boolean trained ) {
        try {
            neuralNetworkDataProcessor.updateConfigurationData(id, activationFunction, trained);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "jsonView";
    }

    @RequestMapping( value = "train", method = RequestMethod.POST )
    public String train( Model model,
                         @RequestParam("neuralNetworkId") int neuralNetworkId,
                         @RequestParam("learningRate") double learningRate,
                         @RequestParam("errorThreshold") double errorThreshold,
                         @RequestParam("learningAlgorithm") String learningAlgorithm,
                         @RequestParam(value = "maxIterations", required = false) Integer maxIterations,
                         @RequestParam("trainingSet") int trainingSetId,
                         @RequestParam(value = "validationSet", required = false) Integer validationSetId,
                         @RequestParam(value = "validationFrequency", required = false) Integer validationFrequency ) {
        try {
            /*
             * TODO: maybe would be wiser to clear observer map when stopping training to clean memory
             */
            neuralNetworkTrainingObserver.clearTrainingData(neuralNetworkId);
            neuralNetworkDataProcessor.randomizeWeights(neuralNetworkId);
            trainingExecutor.train(neuralNetworkId, trainingSetId, validationSetId, validationFrequency, SupervisedTrainingAlgorithmType.valueOf(learningAlgorithm), learningRate, errorThreshold, maxIterations);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "jsonView";
    }

    @RequestMapping( value = "getTrainingData", method = {RequestMethod.POST, RequestMethod.GET} )
    public String getTrainingData( Model model, @RequestParam("neuralNetworkId") int neuralNetworkId ) {
        Map<String, Object> trainingData = neuralNetworkTrainingObserver.getTrainingData(neuralNetworkId);
        // these params are needed by extJS polling mechanism
        model.addAttribute("type", "event");
        model.addAttribute("name", "message");
        model.addAttribute("data", trainingData);
        return "jsonView";
    }

    @RequestMapping( value = "stopTraining", method = RequestMethod.POST )
    public String stopTraining( Model model, @RequestParam("neuralNetworkId") int neuralNetworkId ) {
        try {
            trainingExecutor.stopTraining(neuralNetworkId);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "jsonView";
    }

    @RequestMapping( value = "test", method = RequestMethod.POST )
    public String test( Model model, @RequestParam("neuralNetworkId") int neuralNetworkId, @RequestParam("networkInput") String networkInput ) {
        List<Double> inputs = new ArrayList<Double>();
        String[] netInputs = networkInput.split("\\s");
        try {
            for( String netInput : netInputs ) {
                inputs.add(Double.parseDouble(netInput));
            }
            List<Double> outputs = neuralNetworkProcessor.process(neuralNetworkId, inputs, true);
            model.addAttribute("success", true);
            model.addAttribute("outputs", outputs);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "jsonView";
    }

    @Autowired
    public void setNeuralNetworkDataProcessor(NeuralNetworkDataProcessor neuralNetworkDataProcessor) {
        this.neuralNetworkDataProcessor = neuralNetworkDataProcessor;
    }

    @Autowired
    public void setTrainingExecutor(TrainingExecutor trainingExecutor) {
        this.trainingExecutor = trainingExecutor;
    }

    @Autowired
    public void setNeuralNetworkTrainingObserver(NeuralNetworkTrainingObserver neuralNetworkTrainingObserver) {
        this.neuralNetworkTrainingObserver = neuralNetworkTrainingObserver;
    }

    @Autowired
    public void setNeuralNetworkProcessor(NeuralNetworkProcessor<Double> neuralNetworkProcessor) {
        this.neuralNetworkProcessor = neuralNetworkProcessor;
    }

}
