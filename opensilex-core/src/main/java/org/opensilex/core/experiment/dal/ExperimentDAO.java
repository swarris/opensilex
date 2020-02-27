//******************************************************************************
//                          ExperimentDAO.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRAE 2020
// Contact: vincent.migot@inrae.fr, anne.tireau@inrae.fr, pascal.neveu@inrae.fr
//******************************************************************************

package org.opensilex.core.experiment.dal;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.vocabulary.RDF;
import org.opensilex.core.ontology.Oeso;
import org.opensilex.sparql.deserializer.SPARQLDeserializers;
import org.opensilex.sparql.exceptions.SPARQLException;
import org.opensilex.sparql.model.SPARQLResourceModel;
import org.opensilex.sparql.service.SPARQLQueryHelper;
import org.opensilex.sparql.service.SPARQLService;
import org.opensilex.sparql.utils.Ontology;
import org.opensilex.sparql.utils.OrderBy;
import org.opensilex.utils.ListWithPagination;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.apache.jena.arq.querybuilder.AbstractQueryBuilder.makeVar;


/**
 * @author Vincent MIGOT
 * @author Renaud COLIN
 */
public class ExperimentDAO {

    protected final SPARQLService sparql;

    public ExperimentDAO(SPARQLService sparql) {
        this.sparql = sparql;
    }

    public ExperimentModel create(ExperimentModel instance) throws Exception {
        checkURIs(instance);
        sparql.create(instance);
        return instance;
    }

    public ExperimentModel update(ExperimentModel instance) throws Exception {
        checkURIs(instance);
        sparql.update(instance);
        return instance;
    }

    /**
     * Check that all URI(s) which refers to a non {@link org.opensilex.sparql.annotations.SPARQLResource}-compliant model exists.
     *
     * @param model the experiment for which we check if all URI(s) exists
     * @throws SPARQLException if the SPARQL uri checking query fail
     * @throws IllegalArgumentException if the given model contains a unknown URI
     */
    protected void checkURIs(ExperimentModel model) throws SPARQLException, IllegalArgumentException {

        if (model.getSpecies() != null && !sparql.uriExists(model.getSpecies())) {
            throw new IllegalArgumentException("Trying to insert an experiment with an unknown species : " + model.getSpecies());
        }
        for (URI infraUri : model.getInfrastructures()) {
            if (!sparql.uriExists(infraUri)) {
                throw new IllegalArgumentException("Trying to insert an experiment with an unknown infrastructure : " + infraUri);
            }
        }
        // #TODO use a method to test in one query if all URI(s) exists

    }

    public void delete(URI xpUri) throws Exception {
        sparql.delete(ExperimentModel.class, xpUri);
    }

    public void delete(List<URI> xpUris) throws Exception {
        sparql.delete(ExperimentModel.class, xpUris);
    }

    public ExperimentModel get(URI xpUri) throws Exception {
        return sparql.getByURI(ExperimentModel.class, xpUri);
    }

    /**
     * Append FILTER or VALUES clause on the given {@link SelectBuilder} for each non-empty simple attribute ( not a {@link List} from the {@link ExperimentSearchDTO}
     * @param searchDTO a search DTO which contains all attributes about an {@link ExperimentModel} search
     *
     * @see SPARQLQueryHelper the utility class used to build Expr
     */
    protected void appendFilters(ExperimentSearchDTO searchDTO, SelectBuilder select) throws Exception {

        if (searchDTO == null)
            return;

        List<Expr> exprList = new ArrayList<>();

        // build equality filters
        if (searchDTO.getUri() != null) {
            exprList.add(SPARQLQueryHelper.eq(SPARQLResourceModel.URI_FIELD, searchDTO.getUri()));
        }
        if (searchDTO.getCampaign() != null) {
            exprList.add(SPARQLQueryHelper.eq(ExperimentModel.CAMPAIGN_SPARQL_VAR, searchDTO.getCampaign()));
        }
        if (searchDTO.getSpecies() != null) {
            exprList.add(SPARQLQueryHelper.eq(ExperimentModel.SPECIES_SPARQL_VAR, searchDTO.getSpecies()));
        }
        if (searchDTO.getIsPublic() != null) {
            exprList.add(SPARQLQueryHelper.eq(ExperimentModel.IS_PUBLIC_SPARQL_VAR, searchDTO.getIsPublic()));
        }

        // build regex based filter
        if (searchDTO.getObjective() != null) {
            exprList.add(SPARQLQueryHelper.regexFilter(ExperimentModel.OBJECTIVE_SPARQL_VAR, searchDTO.getObjective()));
        }
        if (searchDTO.getLabel() != null) {
            exprList.add(SPARQLQueryHelper.regexFilter(ExperimentModel.LABEL_VAR, searchDTO.getLabel()));
        }
        if (searchDTO.getComment() != null) {
            exprList.add(SPARQLQueryHelper.regexFilter(ExperimentModel.COMMENT_SPARQL_VAR, searchDTO.getComment()));
        }

        Boolean isEnded = searchDTO.isEnded();
        if (isEnded != null) {

            Node endDateVar = NodeFactory.createVariable(ExperimentModel.END_DATE_SPARQL_VAR);
            Node currentDateNode = SPARQLDeserializers.getForClass(LocalDate.class).getNode(LocalDate.now());

            // an experiment is ended if the end date is less than the the current date
            if (isEnded) {
                exprList.add(SPARQLQueryHelper.getExprFactory().le(endDateVar, currentDateNode));
            } else {
                exprList.add(SPARQLQueryHelper.getExprFactory().gt(endDateVar, currentDateNode));
            }
        }
        if (searchDTO.getStartDate() != null) {
            exprList.add(SPARQLQueryHelper.eq(ExperimentModel.START_DATE_SPARQL_VAR, LocalDate.parse(searchDTO.getStartDate())));
        }
        if (searchDTO.getEndDate() != null) {
            exprList.add(SPARQLQueryHelper.eq(ExperimentModel.END_DATE_SPARQL_VAR, LocalDate.parse(searchDTO.getEndDate())));
        }

        for (Expr filterExpr : exprList) {
            select.addFilter(filterExpr);
        }
    }

    /**
     * Append FILTER or VALUES clause on the given {@link SelectBuilder} for each non-empty {@link List} from the {@link ExperimentSearchDTO}
     * @param select the {@link SelectBuilder} to update
     * @param searchDTO a search DTO which contains all attributes about an {@link ExperimentModel} search
     */
    protected void appendListFilters(ExperimentSearchDTO searchDTO, SelectBuilder select) throws Exception {

        if (searchDTO == null)
            return;

        Map<String, List<?>> valuesByVar = new HashMap<>();

        if (!searchDTO.getKeywords().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.hasKeyword, ExperimentModel.KEYWORD_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.KEYWORD_SPARQL_VAR, searchDTO.getKeywords());
        }
        if (!searchDTO.getProjects().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.hasProject, ExperimentModel.PROJECT_URI_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.PROJECT_URI_SPARQL_VAR, searchDTO.getProjects());
        }
        if (!searchDTO.getScientificSupervisors().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.hasScientificSupervisor, ExperimentModel.SCIENTIFIC_SUPERVISOR_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.SCIENTIFIC_SUPERVISOR_SPARQL_VAR, searchDTO.getScientificSupervisors());
        }
        if (!searchDTO.getTechnicalSupervisors().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.hasTechnicalSupervisor, ExperimentModel.TECHNICAL_SUPERVISOR_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.TECHNICAL_SUPERVISOR_SPARQL_VAR, searchDTO.getTechnicalSupervisors());
        }
        if (!searchDTO.getGroups().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.hasGroup, ExperimentModel.GROUP_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.GROUP_SPARQL_VAR, searchDTO.getGroups());
        }
        if (!searchDTO.getVariables().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.measures, ExperimentModel.VARIABLES_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.VARIABLES_SPARQL_VAR, searchDTO.getVariables());
        }
        if (!searchDTO.getSensors().isEmpty()) {
            valuesByVar.put(ExperimentModel.SENSORS_SPARQL_VAR, searchDTO.getSensors());
            addWhere(select, ExperimentModel.SENSORS_SPARQL_VAR, Oeso.participatesIn, ExperimentModel.URI_FIELD);

            // append a restriction on ?sensors variable to make sure that only instance of SensingDevice are retrieved
            String SENSOR_TYPE_VARIABLE_NAME = "SensingDeviceType";
            TriplePath typePath = select.makeTriplePath(makeVar(ExperimentModel.SENSORS_SPARQL_VAR), RDF.type, makeVar(SENSOR_TYPE_VARIABLE_NAME));
            TriplePath subClassPath = select.makeTriplePath(makeVar(SENSOR_TYPE_VARIABLE_NAME), Ontology.subClassAny, Oeso.SensingDevice.asNode());
            select.addWhere(subClassPath).addWhere(typePath);
        }
        if (!searchDTO.getInfrastructures().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.hasInfrastructure, ExperimentModel.INFRASTRUCTURE_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.INFRASTRUCTURE_SPARQL_VAR, searchDTO.getInfrastructures());
        }
        if (!searchDTO.getInstallations().isEmpty()) {
            addWhere(select, ExperimentModel.URI_FIELD, Oeso.hasDevice, ExperimentModel.DEVICES_SPARQL_VAR);
            valuesByVar.put(ExperimentModel.DEVICES_SPARQL_VAR, searchDTO.getInstallations());
        }
        SPARQLQueryHelper.addWhereValues(select, valuesByVar);
    }

    /**
     * Append a triple on the WHERE clause of the given {@link SelectBuilder}
     *
     * @param select the {@link SelectBuilder} to update
     * @param subjectVar the subject variable name
     * @param property the property between the subject and the object
     * @param objectVar the object variable name
     */
    protected void addWhere(SelectBuilder select, String subjectVar, Property property, String objectVar) {
        select.getWhereHandler().getClause().addTriplePattern(new Triple( makeVar(subjectVar), property.asNode(), makeVar(objectVar)));
    }


    /**
     * @param searchDTO   a search DTO which contains all attributes about an {@link ExperimentModel} search
     * @param orderByList an OrderBy List
     * @param page        the current page
     * @param pageSize    the page size
     * @return the ExperimentModel list
     */
    public ListWithPagination<ExperimentModel> search(ExperimentSearchDTO searchDTO, List<OrderBy> orderByList, Integer page, Integer pageSize) throws Exception {

        ListWithPagination<ExperimentModel> xps =  sparql.searchWithPagination(
                ExperimentModel.class,
                (SelectBuilder select) -> {
                    appendFilters(searchDTO, select);
                    appendListFilters(searchDTO, select);
                },
                orderByList,
                page,
                pageSize
        );
        for(ExperimentModel xp : xps.getList()){
            if(xp.getSensors().isEmpty())
                continue;

            // #TODO don't fetch URI which don't represents sensors
            xp.getSensors().removeIf(sensor -> {
                try {
                    return ! sparql.uriExists(new URI(Oeso.SensingDevice.getURI()),sensor);
                } catch (SPARQLException | URISyntaxException e) { throw new RuntimeException(e); }
            });
        }
        return xps;
    }
}
