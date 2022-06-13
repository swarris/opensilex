package org.opensilex.sparql.mapping;

import org.apache.commons.lang3.StringUtils;
import org.opensilex.sparql.deserializer.SPARQLDeserializers;
import org.opensilex.sparql.deserializer.URIDeserializer;
import org.opensilex.sparql.model.SPARQLLabel;
import org.opensilex.sparql.model.SPARQLNamedResourceModel;
import org.opensilex.sparql.model.SPARQLResourceModel;
import org.opensilex.sparql.service.SPARQLResult;
import org.opensilex.sparql.service.SPARQLService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * @author rcolin
 * A class used to transform {@link SPARQLResult} to {@link SPARQLResourceModel} with no
 * proxying. The goal is to improve performance by don't using proxying which add additional cost.
 * This class can handle :
 * <ul>
 *     <li>label properties</li>
 *     <li>data properties (single valued)</li>
 *     <li>object properties( nested object uri/type/name)</li>
 * </ul>
 *
 * And can not handle :
 * <ul>
 *     <li>data properties (multi valued)</li>
 *     <li>object properties(multi valued)</li>
 * </ul>
 * @param <T>
 */
public class SparqlNoProxyFetcher<T extends SPARQLResourceModel> implements SparqlMapper<T> {

    private final Constructor<T> constructor;
    private final SPARQLClassObjectMapperIndex mapperIndex;
    private final SPARQLClassAnalyzer classAnalyzer;

    public SparqlNoProxyFetcher(Class<T> objectClass, SPARQLService sparql) throws Exception {
        this.constructor = objectClass.getConstructor();
        this.mapperIndex = sparql.getMapperIndex();
        this.classAnalyzer = mapperIndex.getForClass(objectClass).getClassAnalizer();
    }

    @Override
    public void setLabel(T instance, SPARQLResult result, String lang) throws Exception {

        String realTypeLabel = result.getStringValue(classAnalyzer.getTypeLabelFieldName());
        if (!StringUtils.isEmpty(realTypeLabel)) {
            SPARQLLabel typeLabel = new SPARQLLabel();
            typeLabel.setDefaultValue(realTypeLabel);
            typeLabel.setDefaultLang(lang);
            instance.setTypeLabel(typeLabel);
        }
    }

    public void setLabelProperties(T instance, SPARQLResult result, String lang) throws Exception {

        for (Field field : classAnalyzer.getLabelPropertyFields()) {

            String value = result.getStringValue(field.getName());

            if (!StringUtils.isEmpty(value)) {
                SPARQLLabel label = new SPARQLLabel();
                label.setDefaultValue(value);
                label.setDefaultLang(lang);

                Method setter = classAnalyzer.getSetterFromField(field);
                setter.invoke(instance, label);
            }

        }
    }

    public void setDataProperties(T instance, SPARQLResult result, String lang) throws Exception {

        for (Field field : classAnalyzer.getDataPropertyFields()) {
            String value = result.getStringValue(field.getName());

            if (!StringUtils.isEmpty(value)) {
                if (SPARQLDeserializers.existsForClass(field.getType())) {
                    Object objValue = SPARQLDeserializers.getForClass(field.getType()).fromString(value);
                    Method setter = classAnalyzer.getSetterFromField(field);
                    setter.invoke(instance, objValue);
                } else {
                    throw new IllegalArgumentException("No deserializer for field: " + field.getName());
                }
            }
        }
    }

    public void setObjectProperties(T model, SPARQLResult result, String lang) throws Exception {

        for (Field field : classAnalyzer.getObjectPropertyFields()) {
            String value = result.getStringValue(field.getName());

            if (StringUtils.isEmpty(value)) {
                continue;
            }
            SPARQLResourceModel nested = getNestedObject(result, field, value);
            if (nested != null) {
                Method setter = classAnalyzer.getSetterFromField(field);
                setter.invoke(model, nested);
            }
        }
    }

    SPARQLResourceModel getNestedObject(SPARQLResult result, Field objectField, String objectValue) throws Exception {

        URI objectURI = URIDeserializer.formatURI(objectValue);

        Class<? extends SPARQLResourceModel> fieldType = (Class<? extends SPARQLResourceModel>) objectField.getType();

        // create nested object and set uri
        SPARQLResourceModel nestedObject = fieldType.getConstructor().newInstance();
        nestedObject.setUri(objectURI);

        // set nested object name
        if (SPARQLNamedResourceModel.class.isAssignableFrom(fieldType)) {
            String fieldNameVar = SPARQLClassObjectMapper.getObjectNameVarName(objectField.getName());
            String name = result.getStringValue(fieldNameVar);
            if (!StringUtils.isEmpty(name)) {
                ((SPARQLNamedResourceModel<?>) nestedObject).setName(name);
            }
        }
        // set nested object type
        nestedObject.setType(mapperIndex.getForClass(fieldType).getClassAnalizer().getRdfTypeURI());

        return nestedObject;
    }


    @Override
    public void setDataListProperties(T instance, SPARQLResult result, String lang) throws Exception {

    }

    @Override
    public void setObjectListProperties(T model, SPARQLResult result, String lang) throws Exception {

    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }
}
