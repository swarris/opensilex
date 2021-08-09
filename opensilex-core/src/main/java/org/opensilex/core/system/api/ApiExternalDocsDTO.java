//******************************************************************************
//                        externalDocs.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2021
// Contact: arnaud.charleroy@inrae.fr, anne.tireau@inrae.fr, pascal.neveu@inrae.fr
//******************************************************************************
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensilex.core.system.api;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Arnaud Charleroy
 */
@JsonPropertyOrder({"description", "url"})
public class ApiExternalDocsDTO {
    
    @ApiModelProperty(value = "Opensilex api docs", example = "Opensilex api docs")
    private String description;
    
    @ApiModelProperty(value = "https://github.com/OpenSILEX/opensilex/blob/master/opensilex-doc/src/main/resources/index.md",
                      example = "https://github.com/OpenSILEX/opensilex/blob/master/opensilex-doc/src/main/resources/index.md")
    private String url;

    public ApiExternalDocsDTO() {
    }

    public ApiExternalDocsDTO(String description, String url) {
        this.description = description;
        this.url = url;
    }

    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    

}
