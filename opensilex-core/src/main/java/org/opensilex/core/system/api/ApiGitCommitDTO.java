//******************************************************************************
//                        ApiGitCommitDTO.java
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

/**
 *
 * @author Arnaud Charleroy
 */
public class ApiGitCommitDTO {
    
    private String commitId;
    
    private String commitMessage;

    public ApiGitCommitDTO(String commitId, String commitMessage) {
        this.commitId = commitId;
        this.commitMessage = commitMessage;
    }
    
    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }
}
