//******************************************************************************
//                           OpenSilexModule.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: vincent.migot@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex;

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
import org.apache.tika.Tika;
import org.opensilex.module.ModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * This abstract class provide entry point for OpenSILEX modules inside application.
 * Simply extends this class to create a module.
 * </pre>
 *
 * @author Vincent Migot
 */
public abstract class OpenSilexModule {

    private final static Logger LOGGER = LoggerFactory.getLogger(OpenSilexModule.class);

    /**
     * <pre>
     * Main application mavenProperties file generated by maven plugins.
     * It contains all mavenProperties defined in pom.xml during build
     * </pre>
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

    /**
     * Utility function to determine if a file exists within a module JAR
     *
     * @param fileName File name to check
     * @return true if the file exists and false otherwise
     * @throws IOException In case of file access issues
     * @throws URISyntaxException In case of bad file URI (should never append)
     */
    public boolean fileExists(String fileName) throws IOException, URISyntaxException {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        LOGGER.debug(getClass().getCanonicalName() + " - Check file from: " + sourceLocationString + " looking for: " + fileName);

        if (sourceLocationString.endsWith(".jar")) {
            // In case if module is a JAR
            File jarFile = Paths.get(sourceLocation.toURI()).toFile();

            // Create pseudo-filesystem from JAR as a ZIP file
            ZipFile zipFile = new ZipFile(jarFile);
            FileSystem fs = FileSystems.newFileSystem(Paths.get(jarFile.getAbsolutePath()), getClass().getClassLoader());

            // Check file with pseudo-filesystem
            Path file = fs.getPath(fileName);
            if (Files.isRegularFile(file)) {
                return true;
            }
            zipFile.close();

        } else {
            // In case if module is a folder (DEV MODE) directly check file in folder
            File file = Paths.get(sourceLocation.toURI().resolve(fileName)).toFile();
            if (file.exists() && file.isFile()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return last modified date of a file in the module
     *
     * @param fileName File to check
     * @return Last modification date
     */
    public Date getLastModified(String fileName) {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        long lastModified = 0;
        if (sourceLocationString.endsWith(".jar")) {
            // If module is a JAR file return last modified date of the JAR file
            try {
                lastModified = Paths.get(sourceLocation.toURI()).toFile().lastModified();
            } catch (URISyntaxException ex) {
                LOGGER.warn("Unexpected exception while getting module last modified date", ex);
            }
        } else {
            // If module is a folder (DEV MODE) return last modified date of the given file inside it
            try {
                lastModified = Paths.get(sourceLocation.toURI().resolve(fileName)).toFile().lastModified();
            } catch (URISyntaxException ex) {
                LOGGER.warn("Unexpected exception while getting module file last modified date", ex);
            }
        }

        return new Date(lastModified);
    }

    /**
     * Return file inside module as a {@code java.io.InputStream}
     *
     * @param fileName File to get
     * @return Input stream of the file
     * @throws IOException In case of file access issues
     * @throws URISyntaxException In case of bad file URI (should never append)
     */
    public InputStream getFileInputStream(String fileName) throws IOException, URISyntaxException {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        LOGGER.debug(getClass().getCanonicalName() + " - Load file: " + fileName + " from: " + sourceLocationString);

        if (sourceLocationString.endsWith(".jar")) {
            // In case if module is a JAR
            File jarFile = Paths.get(sourceLocation.toURI()).toFile();

            // Create pseudo-filesystem from JAR as a ZIP file
            ZipFile zipFile = new ZipFile(jarFile);
            FileSystem fs = FileSystems.newFileSystem(Paths.get(jarFile.getAbsolutePath()), getClass().getClassLoader());
            Path file = fs.getPath(fileName);

            InputStream stream = null;
            if (Files.isRegularFile(file)) {
                // If file exists in JAR get it as a byte array
                ZipEntry entry = zipFile.getEntry(fileName);
                byte[] byteArray = IOUtils.toByteArray(zipFile.getInputStream(entry));
                // Convert this byte arry in input stram
                stream = new ByteArrayInputStream(byteArray);
            }

            // Close zip and return stream
            zipFile.close();
            return stream;

        } else {
            // In case if module is a folder (DEV MODE) directly return input stream
            File file = Paths.get(sourceLocation.toURI().resolve(fileName)).toFile();
            if (file.exists() && file.isFile()) {
                return new FileInputStream(file);
            }
        }

        return null;
    }

    public String getFileMimeType(String filePath) throws IOException, URISyntaxException {
        Tika tika = new Tika();
        return tika.detect(getFileInputStream(filePath));
    }

    /**
     * List a directory inside a module
     *
     * @param directoryName Directory to list
     * @return List of found filenames
     * @throws IOException In case of file access issues
     * @throws URISyntaxException In case of bad file URI (should never append)
     */
    public List<String> listResourceDirectory(String directoryName) throws IOException, URISyntaxException {
        URL sourceLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String sourceLocationString = sourceLocation.toString();
        LOGGER.debug(getClass().getCanonicalName() + " - List directory from: " + sourceLocationString + " looking for: " + directoryName);

        List<String> files = new ArrayList<>();

        if (sourceLocationString.endsWith(".jar")) {
            // In case if module is a JAR
            File jarFile = Paths.get(sourceLocation.toURI()).toFile();

            // Create pseudo-filesystem from JAR as a ZIP file
            ZipFile zipFile = new ZipFile(jarFile);
            FileSystem fs = FileSystems.newFileSystem(Paths.get(jarFile.getAbsolutePath()), getClass().getClassLoader());
            Path directoryPath = fs.getPath(directoryName);

            // Parse directory files inside zip
            if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
                Stream<Path> walk = Files.walk(directoryPath);
                files = walk
                        .filter(Files::isRegularFile)
                        .map(f -> f.getFileName().toString()).collect(Collectors.toList());
            }
            zipFile.close();

        } else {
            // In case if module is a folder (DEV MODE) directly parse directory files
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

    /**
     * Return configuration file of given profile in module
     *
     * @param profileId Profile idnetifier
     * @return Inputstream of the configuration file
     */
    public InputStream getConfigFile(String profileId) {
        String yamlPath = "config/" + profileId + "/opensilex.yml";

        try {
            return getFileInputStream(yamlPath);
        } catch (Exception ex) {
            LOGGER.warn(getClass().getCanonicalName() + " - Can't load config file: " + yamlPath, ex);
        }

        return null;
    }

    /**
     * Module configuration interface
     */
    private ModuleConfig config;

    /**
     * Setter for module configuration
     *
     * @param config Module configuration instance
     */
    public void setConfig(ModuleConfig config) {
        this.config = config;
    }

    /**
     * Getter for module configuration
     *
     * @return Module configuration instance
     */
    public ModuleConfig getConfig() {
        return config;
    }

    /**
     * Getter for module configuration converted in given subclass of
     * ModuleConfig
     *
     * @param <T> Class configuration real implementation parameter
     * @param configClass Class configuration real implementation
     * @return Module configuration instance converted in T
     */
    @SuppressWarnings("unchecked")
    public <T extends ModuleConfig> T getConfig(Class<T> configClass) {
        return (T) getConfig();
    }

    /**
     * Return module configuration identifier (index in main configuration)
     *
     * @return Configuration identifier
     */
    public String getConfigId() {
        return null;
    }

    /**
     * Return module configuration class
     *
     * @return Module configuration class
     */
    public Class<? extends ModuleConfig> getConfigClass() {
        return null;
    }

    /**
     * Default method for module installation, to be implemented by module
     * implementations for installation logic
     *
     * @throws Exception Can throw anything
     */
    public void install() throws Exception {
        LOGGER.debug("Nothing to install for module class: " + getClass().getCanonicalName());
    }

    /**
     * Default method for module startup, to be implemented by module
     * implementations for starting logic
     *
     * @throws Exception Can throw anything
     */
    public void startup() throws Exception {
        LOGGER.debug("Nothing to init for module class: " + getClass().getCanonicalName());
    }

    /**
     * Default method for module shutdown, to be implemented by module
     * implementations for stopping logic
     */
    public void shutdown() {
        LOGGER.debug("Nothing to shutdown for module class: " + getClass().getCanonicalName());
    }

}