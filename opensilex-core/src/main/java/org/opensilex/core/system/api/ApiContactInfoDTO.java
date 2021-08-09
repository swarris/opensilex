//******************************************************************************
//                        ApiContactInfoDTO.java
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
import java.net.URL;

/**
 *
 * @author Arnaud Charleroy
 */
@JsonPropertyOrder({"name", "email", "url"})
public class ApiContactInfoDTO {

   
    private String name;
    
   
    private String email;
     
    private URL url;

    public ApiContactInfoDTO() {
    }

    public ApiContactInfoDTO(URL url) {
        this.url = url;
    }

    public ApiContactInfoDTO(String name, String email, URL url) {
        this.name = name;
        this.email = email;
        this.url = url;
    }
    
    @ApiModelProperty(value = "Opensilex Team", example = "Opensilex Team")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @ApiModelProperty(value = "opensilex@gmail.com", example = "opensilex@gmail.com")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

}
