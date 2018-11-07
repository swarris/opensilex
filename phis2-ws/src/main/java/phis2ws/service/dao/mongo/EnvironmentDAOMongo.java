//******************************************************************************
//                                       EnvironmentDAOMongo.java
// SILEX-PHIS
// Copyright © INRA 2018
// Creation date: 30 oct. 2018
// Contact: morgane.vidal@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package phis2ws.service.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis2ws.service.configuration.DateFormat;
import phis2ws.service.configuration.DateFormats;
import phis2ws.service.dao.manager.DAOMongo;
import phis2ws.service.dao.sesame.SensorDAOSesame;
import phis2ws.service.dao.sesame.VariableDaoSesame;
import phis2ws.service.documentation.StatusCodeMsg;
import phis2ws.service.utils.POSTResultsReturn;
import phis2ws.service.view.brapi.Status;
import phis2ws.service.view.model.phis.EnvironmentMeasure;

/**
 * Represents the MongoDB Data Access Object for the environment.
 * @author Morgane Vidal <morgane.vidal@inra.fr>
 */
public class EnvironmentDAOMongo extends DAOMongo<EnvironmentMeasure> {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(EnvironmentDAOMongo.class);
    
    //MongoFields labels, used to query (CRUD) the environment mongo data
    private final static String DB_FIELD_SENSOR = "sensor";
    private final static String DB_FIELD_VARIABLE = "variable";
    private final static String DB_FIELD_DATE = "date";
    private final static String DB_FIELD_VALUE = "value";
    
    // Variable URI when querying for environment measures (required)
    // e.g. http://www.phenome-fppn.fr/diaphen/id/variable/ev000070
    public String variableUri;
    // End date filter when querying for environment measures (optional)
    // e.g. 2017-06-07 13:14:32+0200
    public String endDate;
    // Start date filter when querying for environment measures (optional)
    // e.g. 2017-06-07 13:14:32+0200
    public String startDate;
    // Sensor URI filter when querying for environment measures (optional)
    // e.g. http://www.phenome-fppn.fr/mauguio/diaphen/2013/sb140227
    public String sensorUri;

    /**
     * Get document count according to the prepareSearchQuery
     * @return the document count
     */
    public int count() {
        // Get the collection corresponding to variable uri
        String variableCollection = this.getEnvironmentCollectionFromVariable(variableUri);
        MongoCollection<Document> environmentMeasureVariableCollection = database.getCollection(variableCollection);

        // Get the filter query
        BasicDBObject query = prepareSearchQuery();
        
        // Return the document count
        return (int)environmentMeasureVariableCollection.count(query);
    }

    @Override
    protected BasicDBObject prepareSearchQuery() {
        BasicDBObject query = new BasicDBObject();
        
        try {
            SimpleDateFormat df = new SimpleDateFormat(DateFormat.YMDTHMSZ.toString());

            // Define date filter depending if start date and/or end date are defined
            if (startDate != null) {
                Date start = df.parse(startDate);

                if (endDate != null) {
                    // In case of start date AND end date defined
                    Date end = df.parse(endDate);
                    query.append(DB_FIELD_DATE, BasicDBObjectBuilder.start("$gte", start).add("$lte", end).get());
                } else {
                    // In case of start date ONLY is defined
                    query.append(DB_FIELD_DATE, BasicDBObjectBuilder.start("$gte", start).get());
                }
            } else if (endDate != null) {
                // In case of end date ONLY is defined
                Date end = df.parse(endDate);
                query.append(DB_FIELD_DATE, BasicDBObjectBuilder.start("$lte", end).get());
            }
        } catch (ParseException ex) {
            LOGGER.error("Invalid date format", ex);
        }
        
        // Add filter if a sensor uri is defined
        if (sensorUri != null) {
            query.append(DB_FIELD_SENSOR, sensorUri);
        }
        
        LOGGER.trace(getTraceabilityLogs() + " query : " + query.toString());
        
        return query;
    }

    @Override
    public ArrayList<EnvironmentMeasure> allPaginate() {
        // Get the collection corresponding to variable uri
        String variableCollection = this.getEnvironmentCollectionFromVariable(variableUri);
        MongoCollection<Document> environmentMeasureVariableCollection = database.getCollection(variableCollection);

        // Get the filter query
        BasicDBObject query = prepareSearchQuery();
        
        // Get paginated documents
        FindIterable<Document> measuresMongo = environmentMeasureVariableCollection
                .find(query)
                .sort(Sorts.ascending(DB_FIELD_DATE))
                .skip(page * pageSize)
                .limit(pageSize);

        ArrayList<EnvironmentMeasure> measures = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat(DateFormats.YMDHMSZ_FORMAT);
        
        // For each document, create a EnvironmentMeasure Instance
        try (MongoCursor<Document> measuresCursor = measuresMongo.iterator()) {
            while (measuresCursor.hasNext()) {
                Document measureDocument = measuresCursor.next();
                
                // Create and define the EnvironmentMeasure
                EnvironmentMeasure measure = new EnvironmentMeasure();
                measure.setVariableUri(variableUri);
                measure.setDate(measureDocument.getDate(DB_FIELD_DATE));
                measure.setValue(Float.parseFloat(measureDocument.get(DB_FIELD_VALUE).toString()));
                measure.setSensorUri(measureDocument.getString(DB_FIELD_SENSOR));
                measures.add(measure);
            }
        }
        
        return measures;
    }
    
    /**
     * Check the given list of environment measures.
     * @param environmentMeasures
     * @return the check result with the founded errors
     */
    private POSTResultsReturn check(List<EnvironmentMeasure> environmentMeasures) {
        POSTResultsReturn checkResult = new POSTResultsReturn();
        List<Status> checkStatus = new ArrayList<>();
        
        boolean dataOk = true;
        
        SensorDAOSesame sensorDAO = new SensorDAOSesame();
        VariableDaoSesame variableDAO = new VariableDaoSesame();
        for (EnvironmentMeasure environmentMeasure : environmentMeasures) {
            //1. Check if the sensorUri exist and is a sensor
            if (sensorDAO.existAndIsSensor(environmentMeasure.getSensorUri())) {
                //2. Check if the variableUri exist and is a variable
                if (variableDAO.existAndIsVariable(environmentMeasure.getVariableUri())) {
                    //3. Check if the given sensor measures the given variable. 
                    if (!sensorDAO.isSensorMeasuringVariable(environmentMeasure.getSensorUri(), environmentMeasure.getVariableUri())) {
                        dataOk = false;
                        checkStatus.add(new Status(StatusCodeMsg.WRONG_VALUE, StatusCodeMsg.ERR, 
                            "The given sensor (" + environmentMeasure.getSensorUri() + ") "
                          + "does not measure the given variable (" + environmentMeasure.getVariableUri() + ")."));
                    }
                } else {
                    dataOk = false;
                    checkStatus.add(new Status(StatusCodeMsg.WRONG_VALUE, StatusCodeMsg.ERR, 
                        "Unknwon variable : " + environmentMeasure.getVariableUri()));
                }
            } else {
                dataOk = false;
                    checkStatus.add(new Status(StatusCodeMsg.WRONG_VALUE, StatusCodeMsg.ERR, 
                        "Unknwon sensor : " + environmentMeasure.getSensorUri()));
            }
        }
        
        checkResult = new POSTResultsReturn(dataOk, null, dataOk);
        checkResult.statusList = checkStatus;
        return checkResult;
    }
    
    /**
     * Generates the query to insert a new environment measure in the mongodb database.
     * @param environmentMeasure
     * @example
     * { 
     *      "sensor" : "http://www.phenome-fppn.fr/diaphen/2018/s18521", 
     *      "variable" : "http://www.phenome-fppn.fr/id/variables/v001", 
     *      "value" : 0.5, 
     *      "date" : { "$date" : 1497516660000 } 
     * }
     * @return the document to insert, representing the given environment measure
     * @throws ParseException 
     */
    private Document prepareInsertEnvironmentDocument(EnvironmentMeasure environmentMeasure) {
        Document environmentDocument = new Document();
        
        environmentDocument.append(DB_FIELD_SENSOR, environmentMeasure.getSensorUri());
        environmentDocument.append(DB_FIELD_VARIABLE, environmentMeasure.getVariableUri());
        environmentDocument.append(DB_FIELD_VALUE, environmentMeasure.getValue());
        environmentDocument.append(DB_FIELD_DATE, environmentMeasure.getDate());
        
        LOGGER.debug(environmentDocument.toJson());
        
        return environmentDocument;
    }
    
    /**
     * Get the environment collection name from the given variable. 
     * 
     * @param variableUri
     * @example variableUri http://www.phenome-fppn.fr/id/variables/v001
     * @return the collection name. It corresponds to the last part of the uri.
     * @example collection name : v001
     */
    private String getEnvironmentCollectionFromVariable(String variableUri) {
        String[] split = variableUri.split("/");
        return split[split.length-1];
    }
    
    /**
     * Insert the given envoronment measures in the mongodb database
     * @param environmentMeasures
     * @return the insertion result
     */
    private POSTResultsReturn insert(List<EnvironmentMeasure> environmentMeasures) {
        //SILEX:information
        //We create a collection for each variable. The environment measures are sorted by variable
        //\SILEX:information
        
        //SILEX:todo
        //Transactions
        //\SILEX:todo
        POSTResultsReturn result = null;
        List<Status> status = new ArrayList<>();
        List<String> createdResources = new ArrayList<>(); 
        
        boolean insert = true;
        
        HashMap<String, List<Document>> environmentsToInsertByVariable = new HashMap<>();
        
        //1. Prepare all the documents to insert (we will do one insert by variable)
        for (EnvironmentMeasure environmentMeasure : environmentMeasures) {
            Document createEnvironmentMeasure = prepareInsertEnvironmentDocument(environmentMeasure);

            List<Document> environmentsByVariable;
            if (environmentsToInsertByVariable.containsKey(environmentMeasure.getVariableUri())) {
                environmentsByVariable = environmentsToInsertByVariable.get(environmentMeasure.getVariableUri());
            } else {
                environmentsByVariable = new ArrayList<>();
            }

            environmentsByVariable.add(createEnvironmentMeasure);
            environmentsToInsertByVariable.put(environmentMeasure.getVariableUri(), environmentsByVariable);
        }

        //2. Insert all the environment measures
        if (insert) {
            environmentsToInsertByVariable.entrySet().forEach((environmentToInsert) -> {
                MongoCollection<Document> environmentMeasureVariableCollection = database.getCollection(getEnvironmentCollectionFromVariable(environmentToInsert.getKey()));
                environmentMeasureVariableCollection.insertMany(environmentToInsert.getValue());

                status.add(new Status(StatusCodeMsg.RESOURCES_CREATED, StatusCodeMsg.INFO, StatusCodeMsg.DATA_INSERTED + " for the variable " + environmentToInsert.getKey()));
                createdResources.add(environmentToInsert.getKey());
            });
        }
        
        //3. Prepare result to return
        result = new POSTResultsReturn(insert);
        result.statusList = status;
        if (insert) {
            result.setHttpStatus(Response.Status.CREATED);
            result.createdResources = createdResources;
        } else {
            result.setHttpStatus(Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        return result;
    }
    
    /**
     * Check the given environment measures and insert them if no errors founded.
     * @param environmentMeasures
     * @return the insertion result, with the errors if some have been found.
     */
    public POSTResultsReturn checkAndInsert(List<EnvironmentMeasure> environmentMeasures) {
        POSTResultsReturn checkResult = check(environmentMeasures);
        if (checkResult.getDataState()) {
            return insert(environmentMeasures);
        } else { //Errors in the data
            return checkResult;
        }
    }
}
