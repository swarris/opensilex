/*
 * ******************************************************************************
 *                                     FactorGetDTO.java
 *  OpenSILEX
 *  Copyright © INRA 2019
 *  Creation date:  17 December, 2019
 *  Contact: arnaud.charleroy@inrae.fr, anne.tireau@inrae.fr, pascal.neveu@inrae.fr
 * ******************************************************************************
 */
package org.opensilex.core.factor.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.opensilex.core.factor.dal.FactorModel;
import org.opensilex.core.ontology.SKOSReferencesDTO;

/**
 *
 * @author Arnaud Charleroy
 */
public class FactorDetailsGetDTO extends SKOSReferencesDTO {

    private URI uri;

    private Map<String,String> names;

    private String comment;

    List<FactorLevelGetDTO> factorLevels = new LinkedList<>();

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Map<String,String> getNames() {
        return names;
    }

    public void setNames(Map<String,String> names) {
        this.names = names;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FactorLevelGetDTO> getFactorLevels() {
        return factorLevels;
    }

    public void setFactorLevels(List<FactorLevelGetDTO> factorLevels) {
        this.factorLevels = factorLevels;
    }

    public static FactorDetailsGetDTO fromModel(FactorModel model) {
        FactorDetailsGetDTO dto = new FactorDetailsGetDTO();
        dto.setUri(model.getUri());
        dto.setNames(model.getName().getAllTranslations());
        dto.setComment(model.getComment());
        List<FactorLevelGetDTO> factorLevels = new ArrayList<>();
        model.getFactorLevels().forEach(factorLevelModel -> {
            FactorLevelGetDTO newFactorLevelDTO = FactorLevelGetDTO.fromModel(factorLevelModel);
            factorLevels.add(newFactorLevelDTO);
        });
        dto.setFactorLevels(factorLevels);
        dto.setSkosReferencesFromModel(model);
        return dto;
    }
}
