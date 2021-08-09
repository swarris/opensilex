//******************************************************************************
//                        ApiModulesInfo.java
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
@JsonPropertyOrder({"name", "version"})
public class ApiModulesInfo {

    @ApiModelProperty(value = "opensilex-core", example = "opensilex-core")
    private String name;

    @ApiModelProperty(value = "1.0.0-beta+2", example = "1.0.0-beta+2")
    private String version;

    public ApiModulesInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}