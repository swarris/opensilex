/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.core.data.api;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.validation.constraints.NotNull;
import org.opensilex.core.data.dal.DataModel;
import org.opensilex.server.rest.validation.ValidURI;

/**
 *
 * @author sammy
 */
public class DataGetDTO {
    @NotNull
    @ValidURI
    protected URI uri;
    
    private URI object;
    private URI variable;
    private URI provenance;
    
    private String date;
    
    private Object value;
    private Integer confidence = null;
    
    public void setUri(URI uri) {
        this.uri = uri;
    }
    
    public void setObject(URI object){
        this.object = object;
    }
    
    public void setVariable(URI variable){
        this.variable = variable;
    }
    
    public void setProvenance(URI provenance){
        this.provenance = provenance;
    }
    
    public void setDate( String date){
        this.date = date;
    }
    
    public void setDate( ZonedDateTime date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXX");
        this.date = dtf.format(date);
    }
    
    public void setValue(Object value){
        this.value = value;
    }
    
    public void setConfidence(Integer c){
        this.confidence = c;
    }

    public URI getUri() {
        return uri;
    }
    
    public URI getObject(){
        return object;
    }
    
    public URI getVariable(){
        return variable;
    }
    
    public URI getProvenance(){
        return provenance;
    }
    
    public String getDate(){
        return date;
    }
    
    public Object getValue(){
        return value;
    }

    public Integer getConfidence(){
        return confidence;
    }
    
    public static DataGetDTO fromModel(DataModel model){
        DataGetDTO dto = new DataGetDTO();
        
        dto.setUri(model.getUri());
        dto.setObject(model.getObject());
        dto.setVariable(model.getVariable());
        dto.setProvenance(model.getProvenance());
        
        dto.setDate(model.getDate());
        
        dto.setConfidence(model.getConfidence());
        dto.setValue(model.getValue());
        
        return dto;
    }
}
