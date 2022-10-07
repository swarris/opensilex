package org.opensilex.fs.uri;

import org.opensilex.config.ConfigDescription;
import org.opensilex.service.ServiceConfig;

public interface URIFileSystemConfig extends ServiceConfig {
    
    @ConfigDescription(
            value = "Base path for file storage"
    )
    public String basePath();

    @ConfigDescription(
            value = "iRODS service account"
    )
    public String serviceAccount();

    @ConfigDescription(
            value = "iRODS service account password"
    )
    public String serviceAccountPassword();

    
}

