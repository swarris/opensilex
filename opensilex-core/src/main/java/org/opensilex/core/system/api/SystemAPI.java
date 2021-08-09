//******************************************************************************
//                          SystemAPI.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRAE 2020
// Contact: renaud.colin@inrae.fr, anne.tireau@inrae.fr, pascal.neveu@inrae.fr
//******************************************************************************
package org.opensilex.core.system.api;

import io.swagger.annotations.*;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import org.opensilex.server.response.ErrorResponse;
import org.opensilex.sparql.service.SPARQLService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Properties;
import org.opensilex.OpenSilex;
import org.opensilex.OpenSilexModule;
import org.opensilex.core.CoreModule;
import org.opensilex.security.authentication.injection.CurrentUser;
import org.opensilex.security.user.dal.UserModel;
import org.opensilex.server.ServerConfig;
import org.opensilex.server.ServerModule;
import static org.opensilex.server.extensions.APIExtension.LOGGER;
import org.opensilex.server.response.SingleObjectResponse;
import org.opensilex.utils.ClassUtils;

/**
 * @author Renaud COLIN
 */
@Api(SystemAPI.CREDENTIAL_SYSTEM_GROUP_ID)
@Path("/core/system")
public class SystemAPI {


    public static final String CREDENTIAL_SYSTEM_GROUP_ID = "System";

    @Inject
    private SPARQLService sparql;

    
    @Inject
    private CoreModule coreModule;
    
    @Inject
    private ServerModule serverModule;
    
    @CurrentUser
    UserModel user;

    @GET
    @Path("/versionInfo")
    @ApiOperation("get version informations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Return API version info", response = VersionInfoDTO.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    public Response getVersionInfo() throws Exception { 
        VersionInfoDTO versionInfoDTO = new VersionInfoDTO();   
        
        // title
        versionInfoDTO.setTitle(this.serverModule.getConfig(ServerConfig.class).instanceTitle());

        // version
        versionInfoDTO.setVersion(this.coreModule.getOpenSilexVersion()); 
        
         // Add version in list for all modules
        List<ApiModulesInfo> modulesVersion = new ArrayList<>();
        this.coreModule.getOpenSilex().getModules().forEach((OpenSilexModule module) -> { 
            modulesVersion.add(new ApiModulesInfo(module.getClass().getCanonicalName(),module.getOpenSilexVersion()));
        });
        versionInfoDTO.setModulesVersion(modulesVersion); 
        
        // description
        versionInfoDTO.setDescription(this.serverModule.getConfig(ServerConfig.class).instanceDescription());
        
        // contact
        versionInfoDTO.setContact(
            new ApiContactInfoDTO(
                this.serverModule.getConfig(ServerConfig.class).contactName(), 
                this.serverModule.getConfig(ServerConfig.class).contactEmail(),
                new URL(this.serverModule.getConfig(ServerConfig.class).contactUrl())
            )
        );
        
        // license
        versionInfoDTO.setLicense(
                new ApiLicenseInfoDTO(
                    "GNU Affero General Public License v3",
                    "https://www.gnu.org/licenses/agpl-3.0.fr.html"
                )
        );
        
        // external Docs
        versionInfoDTO.setExternalDocs(
            new ApiExternalDocsDTO(
                "Opensilex external docs",
                "https://github.com/OpenSILEX/opensilex/blob/master/opensilex-doc/src/main/resources/index.md"
            )
        );
        
        // Api Docs
        versionInfoDTO.setApiDocs( 
            new ApiExternalDocsDTO(
                "Opensilex api docs",
                this.serverModule.getBaseURL() + "api-docs"
            )
        );
        
        ApiGitCommitDTO gitInfo = null;
        
        try {
            File gitPropertiesFile = ClassUtils.getFileFromClassArtifact(OpenSilex.class, "git.properties");

            Properties gitProperties = new Properties();
            gitProperties.load(new FileReader(gitPropertiesFile));

            String gitCommitFull = gitProperties.getProperty("git.commit.id.full", null);
            String gitCommitMessage = gitProperties.getProperty("git.commit.message.full", null);  

            
            gitInfo = new ApiGitCommitDTO(gitCommitFull,gitCommitMessage);
        } catch (Exception ex) {
            System.out.println("No git commit information found");
            LOGGER.debug("Exception raised:", ex);
        }
        
        versionInfoDTO.setGitCommit(gitInfo);
        
        
        return new SingleObjectResponse<>(versionInfoDTO).getResponse();
    }
    
}
