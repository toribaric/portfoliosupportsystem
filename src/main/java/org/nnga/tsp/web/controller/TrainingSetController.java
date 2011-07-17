package org.nnga.tsp.web.controller;

import org.apache.commons.io.IOUtils;
import org.nnga.tsp.persistence.entity.TrainingSet;
import org.nnga.tsp.persistence.processor.TrainingSetDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ts")
public class TrainingSetController {

    private TrainingSetDataProcessor trainingSetDataProcessor;

    @RequestMapping( value = "list", method = RequestMethod.GET )
    public String list( Model model ) {
        List<TrainingSet> trainingSets = trainingSetDataProcessor.getTrainingSets();
        model.addAttribute("success", true);
        model.addAttribute("trainingSets", trainingSets);
        return "jsonView";
    }

    @RequestMapping( value = "create", method = {RequestMethod.POST} )
    public String create( Model model, HttpServletRequest httpServletRequest ) throws Exception {
        String json = IOUtils.toString(httpServletRequest.getInputStream());
        trainingSetDataProcessor.createNewTrainingSet(json);

        model.addAttribute("success", true);
        return "jsonView";
    }

    @RequestMapping( value = "delete", method = {RequestMethod.POST} )
    public String delete( Model model, HttpServletRequest httpServletRequest ) throws Exception {
        String json = IOUtils.toString(httpServletRequest.getInputStream());
        trainingSetDataProcessor.deleteTrainingSet(json);

        model.addAttribute("success", true);
        return "jsonView";
    }

    @RequestMapping( value = "upload", method = {RequestMethod.POST} )
    public void upload( Model model, HttpServletResponse httpServletResponse,
                        @RequestParam("uploadSetName") String uploadSetName,
                        @RequestParam("numInputs") int numInputs,
                        @RequestParam("numOutputs") int numOutputs,
                        @RequestParam("fieldsDelimiter") String fieldsDelimiter,
                        @RequestParam("rowsDelimiter") String rowsDelimiter,
                        @RequestParam("trainingSetFile") MultipartFile trainingSetFile ) throws Exception {

        // here we must return "text/html" content type because extJS uploads through generated iframe and worites response
        // to it - this content type tells it to write response as it was, without "decoration" - or pure JSON in this case
        httpServletResponse.setContentType("text/html");

        // for now only "space" and "newline" special chars are supported in these input formats
        fieldsDelimiter = fieldsDelimiter.replace("<space>", "\\s").replace("<newline>", "\n");
        rowsDelimiter = rowsDelimiter.replace("<space>", "\\s").replace("<newline>", "\n");

        // get data from uploaded file
        String fileData = IOUtils.toString(trainingSetFile.getInputStream());

        try {
            trainingSetDataProcessor.createTrainingSetFromUpload(uploadSetName, numInputs, numOutputs, fieldsDelimiter, rowsDelimiter, fileData);
            httpServletResponse.getWriter().write("{success:true}");
        }
        catch( Exception e ) {
            httpServletResponse.getWriter().write("{success:false, error:\"" + e.getMessage() + "\"}");
        }

    }

    @RequestMapping( value = "io/list", method = RequestMethod.GET )
    public String ioList( Model model, @RequestParam("trainingSetId") Integer trainingSetId ) throws Exception {

        model.addAttribute("success", true);

        // ignore this exception as it may occur from time to time because of extJS grid panel proxy functioning
        try {
            List<Map<String, Object>> trainingSetIOs = trainingSetDataProcessor.getTrainingSetIOs(trainingSetId);
            model.addAttribute("trainingSetIOs", trainingSetIOs);
        }
        catch( IllegalArgumentException e ) {}

        return "jsonView";
    }

    @RequestMapping( value = "io/update", method = RequestMethod.POST )
    public String ioUpdate( Model model, HttpServletRequest httpServletRequest ) throws Exception {

        String json = IOUtils.toString(httpServletRequest.getInputStream());

        // if only one record was sent from extJS, it hasn't god brackets (it isn't and array of 1 member), so we
        // must add it for jackson ObjectMapper to be able to dserialize it
        if( !json.contains("[") ) {
            json = "[".concat(json).concat("]");
        }

        trainingSetDataProcessor.updateTrainingSetIOs(json);

        model.addAttribute("success", true);
        return "jsonView";
    }

    @RequestMapping( value = "uploadTimeSeries", method = {RequestMethod.POST} )
    public void uploadTimeSeries( Model model, HttpServletResponse httpServletResponse,
                                  @RequestParam("fromDate") String fromDate,
                                  @RequestParam("dataDelimiter") String dataDelimiter,
                                  @RequestParam("timeSeriesFile") MultipartFile timeSeriesFile ) throws Exception {

        httpServletResponse.setContentType("text/html");

        // for now only "space" and "newline" special chars are supported in these input formats
        dataDelimiter = dataDelimiter.replace("<space>", "\\s").replace("<newline>", "\n");

        String fileData = IOUtils.toString(timeSeriesFile.getInputStream());

        try {
            String timeSeries = trainingSetDataProcessor.processAndReturnTimesSeries(fromDate, dataDelimiter, fileData);
            httpServletResponse.getWriter().write("{success:true, timeSeries:" + timeSeries + "}");
        }
        catch( Exception e ) {
            httpServletResponse.getWriter().write("{success:false, error:\"" + e.getMessage() + "\"}");
        }

    }

    @RequestMapping( value = "generateTrainingSetFromTimeSeries", method = {RequestMethod.POST} )
    public String generateTrainingSetFromTimeSeries( Model model,
                                                   @RequestParam("trainingSetId") int trainingSetId,
                                                   @RequestParam("timeSeries") String[] timeSeries,
                                                   @RequestParam("numRecords") int numRecords,
                                                   @RequestParam("frequency") int frequency) {
        try {
            trainingSetDataProcessor.generateTrainingSetFromTimeSeries(trainingSetId, timeSeries, numRecords, frequency);
            model.addAttribute("success", true);
        }
        catch( Exception e ) {
            model.addAttribute("success", false);
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "jsonView";
    }

    @Autowired
    public void setTrainingSetDataProcessor(TrainingSetDataProcessor trainingSetDataProcessor) {
        this.trainingSetDataProcessor = trainingSetDataProcessor;
    }

}
