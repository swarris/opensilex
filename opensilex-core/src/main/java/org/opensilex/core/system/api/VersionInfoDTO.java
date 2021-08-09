//******************************************************************************
//                        VersionInfoDTO.java
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
import java.util.List;

/**
 *
 * @author Arnaud Charleroy
 */
@JsonPropertyOrder({"title", "version", "description", "contact", "license", "modulesVersion"})
public class VersionInfoDTO {

//         
//title: Opensilex
//API version. You can use semantic versioning like 1.0.0,
//or an arbitrary string like 0.99-beta. Required.
//version: 1.0.0
//
//modulesVersion: [
//opensilex-core : '1.0.0b+2',
//inrae-sunagri : '1.0.0',
//]
//
//API description. Arbitrary text in CommonMark or HTML.
//description: This is a sample server for a pet store.
//
//Link to the page that describes the terms of service.
//Must be in the URL format.
//Contact information: name, email, URL.
//contact:
//name: API Support
//email: support@example.com
//url: http://example.com/support
//
//Name of the license and a URL to the license description.
//license:
//name: Apache 2.0
//url: Apache License, Version 2.0
//Link to the external documentation (if any).
//Code or documentation
//
//externalDocs:
//description: Find out more
//url: http://example.com
//
//}
    
    private String title;

    private String version;

    private List<ApiModulesInfo> modulesVersion;

    private String description;

    private ApiContactInfoDTO contact;

    private ApiLicenseInfoDTO license;

    private ApiExternalDocsDTO externalDocs;
 
    private ApiExternalDocsDTO apiDocs;

    private ApiGitCommitDTO gitCommit;
    
    
    @ApiModelProperty(value = "Opensilex instance name", example = "PHIS") 
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ApiModelProperty(value = "Opensilex API version", example = "1.0.0beta+2") 
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ApiModulesInfo> getModulesVersion() {
        return modulesVersion;
    }

    public void setModulesVersion(List<ApiModulesInfo> modulesVersion) {
        this.modulesVersion = modulesVersion;
    }

    @ApiModelProperty(value = "Opensilex description", example = "OpenSILEX is an ontology-driven Information System designed for life science data.") 
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiContactInfoDTO getContact() {
        return contact;
    }

    public void setContact(ApiContactInfoDTO contact) {
        this.contact = contact;
    }

    public ApiLicenseInfoDTO getLicense() {
        return license;
    }

    public void setLicense(ApiLicenseInfoDTO license) {
        this.license = license;
    }

    public ApiExternalDocsDTO getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ApiExternalDocsDTO externalDocs) {
        this.externalDocs = externalDocs;
    }

    public ApiExternalDocsDTO getApiDocs() {
        return apiDocs;
    }

    public void setApiDocs(ApiExternalDocsDTO apiDocs) {
        this.apiDocs = apiDocs;
    }

    public ApiGitCommitDTO getGitCommit() {
        return gitCommit;
    }

    public void setGitCommit(ApiGitCommitDTO gitCommit) {
        this.gitCommit = gitCommit;
    }

    
}
