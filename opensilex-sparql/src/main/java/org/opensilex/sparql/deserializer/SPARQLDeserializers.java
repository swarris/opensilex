//******************************************************************************
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: vincent.migot@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex.sparql.deserializer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.opensilex.OpenSilex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vincent
 */
public class SPARQLDeserializers {

    private final static Logger LOGGER = LoggerFactory.getLogger(SPARQLDeserializers.class);

    private static BiMap<Class<?>, SPARQLDeserializer<?>> deserializersMap;

    private static HashMap<String, Class<?>> datatypeClassMap;

    private static Map<Class<?>, SPARQLDeserializer<?>> getDeserializerMap() {
        if (deserializersMap == null) {
            buildDeserializersMap();
        }

        return deserializersMap;
    }

    private static Map<String, Class<?>> getDatatypeClassMap() {
        if (deserializersMap == null) {
            buildDeserializersMap();
        }

        return datatypeClassMap;
    }

    public static boolean existsForClass(Class<?> clazz) {
        Map<Class<?>, SPARQLDeserializer<?>> map = getDeserializerMap();
        return map.containsKey(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> SPARQLDeserializer<T> getForClass(Class<T> clazz) throws SPARQLDeserializerNotFoundException {
        Map<Class<?>, SPARQLDeserializer<?>> map = getDeserializerMap();
        if (existsForClass(clazz)) {
            return (SPARQLDeserializer<T>) map.get(clazz);
        } else {
            throw new SPARQLDeserializerNotFoundException(clazz);
        }

    }

    public static boolean existsForDatatype(URI datatypeURI) {
        return existsForDatatype(datatypeURI.toString());
    }

    public static boolean existsForDatatype(String datatypeURI) {
        Map<String, Class<?>> map = getDatatypeClassMap();
        return map.containsKey(URIDeserializer.getExpandedURI(datatypeURI));
    }

    public static SPARQLDeserializer<?> getForDatatype(URI datatypeURI) throws SPARQLDeserializerNotFoundException {
        Map<String, Class<?>> map = getDatatypeClassMap();

        if (existsForDatatype(datatypeURI.toString())) {
            return getForClass(map.get(URIDeserializer.getExpandedURI(datatypeURI)));
        } else {
            throw new SPARQLDeserializerNotFoundException(datatypeURI);
        }
    }

    public static Class<?> getDeserializerClass(SPARQLDeserializer<?> deserializer) {
        return deserializersMap.inverse().get(deserializer);
    }

    private static void buildDeserializersMap() {

        List<SPARQLDeserializer<?>> deserializers = new ArrayList<>();
        ServiceLoader.load(SPARQLDeserializer.class, OpenSilex.getClassLoader())
                .forEach(deserializers::add);

        deserializersMap = HashBiMap.create(deserializers.size());
        datatypeClassMap = new HashMap<>(deserializers.size());

        for (SPARQLDeserializer<?> deserializer : deserializers) {
            try {
                Class<?> key = parameterizedClass(deserializer, SPARQLDeserializer.class, 0);
                deserializersMap.put(key, deserializer);
                datatypeClassMap.put(deserializer.getDataType().getURI(), key);
                for (XSDDatatype dt : deserializer.getAlternativeDataType()) {
                    datatypeClassMap.put(dt.getURI(), key);
                }

            } catch (ClassNotFoundException ex) {
                LOGGER.error("SPARQL deserializer not found (should never happend)", ex);
            }
        }
    }

    private static Class<?> parameterizedClass(final Class<?> root, final Class<?> target, final int paramIndex)
            throws ClassNotFoundException {

        final Type[] sooper = root.getGenericInterfaces();
        for (final Type t : sooper) {
            if (!(t instanceof ParameterizedType)) {
                continue;
            }
            final ParameterizedType type = ((ParameterizedType) t);
            if (type.getRawType().getTypeName().equals(target.getTypeName())) {
                return Class.forName(type.getActualTypeArguments()[paramIndex].getTypeName());
            }
        }
        for (final Class<?> parent : root.getInterfaces()) {
            final Class<?> result = parameterizedClass(parent, target, paramIndex);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static Class<?> parameterizedClass(final Object object, final Class<?> target, final int paramIndex)
            throws ClassNotFoundException {
        return parameterizedClass(object.getClass(), target, paramIndex);
    }

    public static Node nodeURI(URI uri) {
        try {
            return SPARQLDeserializers.getForClass(URI.class).getNodeFromString(uri.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    public static Node nodeURI(Property property) {
        try {
            return nodeURI(new URI(property.getURI()));
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    public static String getExpandedURI(String value) {
        return URIDeserializer.getExpandedURI(value);
    }

    public static URI formatURI(URI value) {
        return URIDeserializer.formatURI(value);
    }

    public static Collection<Node> nodeListURI(Collection<URI> uris) throws Exception {
        SPARQLDeserializer<URI> uriParser = SPARQLDeserializers.getForClass(URI.class);
        List<Node> uriNodes = new ArrayList<>(uris.size());
        for (URI uri : uris) {
            uriNodes.add(uriParser.getNodeFromString(uri.toString()));
        }

        return uriNodes;
    }

    public static boolean compareURIs(String uri1, String uri2) {
        return getExpandedURI(uri1).equals(getExpandedURI(uri2));
    }

}
