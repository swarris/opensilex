//******************************************************************************
//                                StudySearchDTO.java
// SILEX-PHIS
// Copyright © INRA 2019
// Creation date: 1 mai 2019
// Contact: Expression userEmail is undefined on line 6, column 15 in file:///home/boizetal/OpenSilex/phis-ws/phis2-ws/licenseheader.txt., anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package opensilex.service.resource.brapi;

import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import opensilex.service.documentation.DocumentationAnnotation;

/**
 *
 * @author boizetal
 */
public class StudySearchDTO {
    private ArrayList<String> commonCropNames;
    private ArrayList<String> germplasmDbIds;
    private ArrayList<String> locationDbIds;
    private ArrayList<String> observationVariableDbIds;
    private ArrayList<String> programDbIds;
    private ArrayList<String> programNames;
    private ArrayList<String> seasonDbIds;
    private ArrayList<String> studyDbIds;
    private ArrayList<String> studyNames;
    private ArrayList<String> studyTypeDbIds;
    private ArrayList<String> studyTypeNames;
    private ArrayList<String> trialDbIds;
    private String sortBy;
    private String sortOrder;
    private Integer page;
    private Integer pageSize;

    public StudySearchDTO() {
    }

    public ArrayList<String> getCommonCropNames() {
        return commonCropNames;
    }

    public void setCommonCropNames(ArrayList<String> commonCropNames) {
        this.commonCropNames = commonCropNames;
    }

    public ArrayList<String> getGermplasmDbIds() {
        return germplasmDbIds;
    }

    public void setGermplasmDbIds(ArrayList<String> germplasmDbIds) {
        this.germplasmDbIds = germplasmDbIds;
    }

    public ArrayList<String> getLocationDbIds() {
        return locationDbIds;
    }

    public void setLocationDbIds(ArrayList<String> locationDbIds) {
        this.locationDbIds = locationDbIds;
    }

    public ArrayList<String> getObservationVariableDbIds() {
        return observationVariableDbIds;
    }

    public void setObservationVariableDbIds(ArrayList<String> observationVariableDbIds) {
        this.observationVariableDbIds = observationVariableDbIds;
    }

    public ArrayList<String> getProgramDbIds() {
        return programDbIds;
    }

    public void setProgramDbIds(ArrayList<String> programDbIds) {
        this.programDbIds = programDbIds;
    }

    public ArrayList<String> getProgramNames() {
        return programNames;
    }

    public void setProgramNames(ArrayList<String> programNames) {
        this.programNames = programNames;
    }

    public ArrayList<String> getSeasonDbIds() {
        return seasonDbIds;
    }

    public void setSeasonDbIds(ArrayList<String> seasonDbIds) {
        this.seasonDbIds = seasonDbIds;
    }

    public ArrayList<String> getStudyDbIds() {
        return studyDbIds;
    }

    public void setStudyDbIds(ArrayList<String> studyDbIds) {
        this.studyDbIds = studyDbIds;
    }

    public ArrayList<String> getStudyNames() {
        return studyNames;
    }

    public void setStudyNames(ArrayList<String> studyNames) {
        this.studyNames = studyNames;
    }

    public ArrayList<String> getStudyTypeDbIds() {
        return studyTypeDbIds;
    }

    public void setStudyTypeDbIds(ArrayList<String> studyTypeDbIds) {
        this.studyTypeDbIds = studyTypeDbIds;
    }

    public ArrayList<String> getStudyTypeNames() {
        return studyTypeNames;
    }

    public void setStudyTypeNames(ArrayList<String> studyTypeNames) {
        this.studyTypeNames = studyTypeNames;
    }

    public ArrayList<String> getTrialDbIds() {
        return trialDbIds;
    }

    public void setTrialDbIds(ArrayList<String> trialDbIds) {
        this.trialDbIds = trialDbIds;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
}
