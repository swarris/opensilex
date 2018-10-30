//******************************************************************************
//                                       EnvironmentPostDTO.java
// SILEX-PHIS
// Copyright © INRA 2018
// Creation date: 29 oct. 2018
// Contact: morgane.vidal@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package phis2ws.service.resources.dto.environment;

import io.swagger.annotations.ApiModelProperty;
import phis2ws.service.configuration.DateFormat;
import phis2ws.service.documentation.DocumentationAnnotation;
import phis2ws.service.resources.dto.manager.AbstractVerifiedClass;
import phis2ws.service.resources.validation.interfaces.Date;
import phis2ws.service.resources.validation.interfaces.Required;
import phis2ws.service.resources.validation.interfaces.URL;

/**
 * Represents the exchange format used to insert environment in the environment post service.
 * @author Morgane Vidal <morgane.vidal@inra.fr>
 */
public class EnvironmentPostDTO extends AbstractVerifiedClass {
    //The uri of the sensor which has provide the measured value.
    //e.g. http://www.phenome-fppn.fr/mtp/2018/s18003
    protected String sensorUri;
    //The uri of the measured variable
    //e.g. http://www.phenome-fppn.fr/mtp/id/variables/v002
    protected String variableUri;
    //The date corresponding to the given value. The format should be yyyy-MM-ddTHH:mm:ssZ
    //e.g. 2018-06-25T15:13:59+0200
    protected String date;
    //The measured value.
    protected float value;
    
    @Override
    public Object createObjectFromDTO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @URL
    @Required
    @ApiModelProperty(example = DocumentationAnnotation.EXAMPLE_SENSOR_URI)
    public String getSensorUri() {
        return sensorUri;
    }

    public void setSensorUri(String sensorUri) {
        this.sensorUri = sensorUri;
    }

    @URL
    @Required
    @ApiModelProperty(example = DocumentationAnnotation.EXAMPLE_VARIABLE_URI)
    public String getVariableUri() {
        return variableUri;
    }

    public void setVariableUri(String variableUri) {
        this.variableUri = variableUri;
    }

    @Date(DateFormat.YMDTHMSZ)
    @Required
    @ApiModelProperty(example = DocumentationAnnotation.EXAMPLE_XSDDATETIME)
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
