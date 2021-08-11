//******************************************************************************
//                        ServerConfig.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: vincent.migot@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex.server;

import java.util.List;
import java.util.Map;
import org.opensilex.OpenSilex;
import org.opensilex.config.ConfigDescription;
import org.opensilex.server.rest.cache.ApiCacheService;

/**
 * Default configuration for OpenSilex base module.
 *
 * @author Vincent Migot
 */
public interface ServerConfig {

    @ConfigDescription(
            value = "Server public URI",
            defaultString = "http://localhost:8666/"
    )
    public String publicURI();
    
    @ConfigDescription(
            value = "Available application language list",
            defaultList = {OpenSilex.DEFAULT_LANGUAGE, "fr"}
    )
    public List<String> availableLanguages();

    @ConfigDescription(
            value = "Tomcat system properties"
    )
    public Map<String, String> tomcatSystemProperties();

    @ConfigDescription(
            value = "Enable Tomcat anti-thread lock mechanism with StuckThreadDetectionValve",
            defaultBoolean = true
    )
    public boolean enableAntiThreadLock();

    @ConfigDescription(
            value = "REST API cache management"
    )
    public ApiCacheService cache();

    @ConfigDescription(
            value = "Application path prefix, must start with '/' and do not end with '/' or be an empty string",
            defaultString = ""
    )
    String pathPrefix();
    
    @ConfigDescription(
            value = "Instance title",
            defaultString = "OpenSILEX"
    )
    public String instanceTitle();
    
    @ConfigDescription(
            value = "Instance description",
            defaultString = "OpenSILEX is an ontology-driven Information System designed for life science data."
    )
    public String instanceDescription();
    
    
    @ConfigDescription(
            value = "Contact name",
            defaultString = "OpenSILEX Team"
    )
    public String contactName();
    
    @ConfigDescription(
            value = "Contact e-mail",
            defaultString = "opensilex-help@groupes.renater.fr"
    )
    public String contactEmail();
    
    @ConfigDescription(
            value = "Project homepage",
            defaultString = "http://www.opensilex.org/"
    )
    public String projectHomepage();
}