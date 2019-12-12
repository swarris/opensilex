//******************************************************************************
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: vincent.migot@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex.module;

import com.auth0.jwt.JWTCreator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.opensilex.server.user.dal.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This abstract class provide entry point for OpenSILEX modules inside
 * application Simply extends this class to create a module.
 */
public abstract class OpenSilexModule {

    private final static Logger LOGGER = LoggerFactory.getLogger(OpenSilexModule.class);

    /**
     * Main application mavenProperties file generated by maven plugins It
     * contains all mavenProperties defined in pom.xml during build
     */
    private final static String APP_PROPERTIES = "app.properties";

    /**
     * Properties wich will be filled with those generated during maven build
     */
    private Properties mavenProperties = new Properties();

    /**
     * Read Maven build properties
     *
     * @return Return maven properties as an instance of
     * {@code java.util.Properties}
     */
    public Properties getMavenProperties() {
        if (mavenProperties.isEmpty()) {
            try {
                // Read build mavenProperties generated by maven plugin
                final InputStream stream = getClass().getClassLoader().getResourceAsStream(APP_PROPERTIES);
                if (stream != null) {
                    mavenProperties.load(stream);
                    stream.close();
                }
            } catch (IOException ex) {
                LOGGER.error("Can't load maven properties for module: " + getClass().getCanonicalName(), ex);
            }
        }

        return mavenProperties;
    }

    /**
     * Return OpenSilex version
     *
     * @return OpenSilex version
     */
    public String getOpenSilexVersion() {
        return getMavenProperties().getProperty("revision", "");
    }

    public boolean fileExists(String fileName) throws Exception {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        LOGGER.debug(getClass().getCanonicalName() + " - Check file from: " + sourceLocationString + " looking for: " + fileName);

        if (sourceLocationString.endsWith(".jar")) {

            File jarFile = Paths.get(sourceLocation.toURI()).toFile();
            ZipFile zipFile = new ZipFile(jarFile);
            FileSystem fs = FileSystems.newFileSystem(Paths.get(jarFile.getAbsolutePath()), getClass().getClassLoader());
            Path file = fs.getPath(fileName);
            if (Files.isRegularFile(file)) {
                return true;
            }
            zipFile.close();

        } else {
            File file = Paths.get(sourceLocation.toURI().resolve(fileName)).toFile();
            if (file.exists() && file.isFile()) {
                return true;
            }
        }

        return false;
    }

    public Date getLastModified(String fileName) {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        long lastModified = 0;
        if (sourceLocationString.endsWith(".jar")) {
            try {
                lastModified = Paths.get(sourceLocation.toURI()).toFile().lastModified();
            } catch (URISyntaxException ex) {
                LOGGER.warn("Unexpected exception while getting module last modified date", ex);
            }
        } else {
            try {
                lastModified = Paths.get(sourceLocation.toURI().resolve(fileName)).toFile().lastModified();
            } catch (URISyntaxException ex) {
                LOGGER.warn("Unexpected exception while getting module file last modified date", ex);
            }
        }

        return new Date(lastModified);
    }

    public InputStream getFileInputStream(String fileName) throws Exception {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        LOGGER.debug(getClass().getCanonicalName() + " - Load file: " + fileName + " from: " + sourceLocationString);

        if (sourceLocationString.endsWith(".jar")) {

            File jarFile = Paths.get(sourceLocation.toURI()).toFile();
            ZipFile zipFile = new ZipFile(jarFile);
            FileSystem fs = FileSystems.newFileSystem(Paths.get(jarFile.getAbsolutePath()), getClass().getClassLoader());
            Path file = fs.getPath(fileName);
            InputStream stream = null;
            if (Files.isRegularFile(file)) {
                ZipEntry entry = zipFile.getEntry(fileName);
                byte[] byteArray = IOUtils.toByteArray(zipFile.getInputStream(entry));
                stream = new ByteArrayInputStream(byteArray);
            }
            zipFile.close();
            return stream;

        } else {
            File file = Paths.get(sourceLocation.toURI().resolve(fileName)).toFile();
            if (file.exists() && file.isFile()) {
                return new FileInputStream(file);
            }
        }

        return null;
    }

    public List<String> listResourceDirectory(String directoryName) throws Exception {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        LOGGER.debug(getClass().getCanonicalName() + " - List directory from: " + sourceLocationString + " looking for: " + directoryName);

        List<String> files = new ArrayList<>();

        if (sourceLocationString.endsWith(".jar")) {

            File jarFile = Paths.get(sourceLocation.toURI()).toFile();
            ZipFile zipFile = new ZipFile(jarFile);
            FileSystem fs = FileSystems.newFileSystem(Paths.get(jarFile.getAbsolutePath()), getClass().getClassLoader());
            Path directoryPath = fs.getPath(directoryName);

            if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
                Stream<Path> walk = Files.walk(directoryPath);
                files = walk
                        .filter(Files::isRegularFile)
                        .map(f -> f.getFileName().toString()).collect(Collectors.toList());
            }
            zipFile.close();

        } else {
            File directory = Paths.get(sourceLocation.toURI().resolve(directoryName)).toFile();
            if (directory.exists() && directory.isDirectory()) {
                Stream<Path> walk = Files.walk(directory.toPath());
                files = walk
                        .filter(Files::isRegularFile)
                        .map(f -> f.getFileName().toString()).collect(Collectors.toList());
            }
        }

        return files;
    }

    public InputStream getYamlFile(String profileId) {
        String yamlPath = "config/" + profileId + "/opensilex.yml";

        try {
            return getFileInputStream(yamlPath);
        } catch (Exception ex) {
            LOGGER.warn(getClass().getCanonicalName() + " - Can't load config file: " + yamlPath, ex);
        }

        return null;
    }

    private ModuleConfig config;

    public void setConfig(ModuleConfig config) {
        this.config = config;
    }

    public ModuleConfig getConfig() {
        return config;
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(Class<T> configClass) {
        return (T) getConfig();
    }

    public String getConfigId() {
        return null;
    }

    public Class<? extends ModuleConfig> getConfigClass() {
        return null;
    }

    public void install() throws Exception {
        LOGGER.info("Nothing to install for module class: " + getClass().getCanonicalName());
    }

    public void init() throws Exception {

    }

    public void clean() {

    }

    public void addLoginClaims(UserModel user, JWTCreator.Builder tokenBuilder) {
        
    }
}
