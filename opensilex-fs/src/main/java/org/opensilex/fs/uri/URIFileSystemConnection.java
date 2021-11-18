package org.opensilex.fs.uri;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.net.URI;

import org.opensilex.fs.service.FileStorageConnection;
import org.opensilex.service.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opensilex.service.BaseService;

public class URIFileSystemConnection extends BaseService implements FileStorageConnection {

	private final static Logger LOGGER = LoggerFactory.getLogger(URIFileSystemConnection.class);
	
	public URIFileSystemConnection() {
		super(null);
	}
	
	protected URIFileSystemConnection(ServiceConfig config) {
		super(config);
	}

	private URI pathToURI(Path filePath) {
		URI uri = null;
		try {
			uri = new URI(filePath.toString());
		}
		catch (Exception e) {
			LOGGER.warn("Not a valid URI");
		}
		return uri;
		
	}
	
	@Override
	public byte[] readFileAsByteArray(Path filePath) throws IOException {
		/* Should do a HTTP GET */
		URI uri = pathToURI(filePath);
		LOGGER.warn("Reading URI, which is unsupported: " + filePath);
		return new byte[0];
	}

	@Override
	public String readFile(Path filePath) throws IOException {
		/* Should do a HTTP GET */
		URI uri = pathToURI(filePath);
		LOGGER.warn("Reading URI, which is unsupported: " + filePath);
		return "";
	}

	@Override
	public void writeFile(Path filePath, String content) throws IOException {
		/* Should do a HTTP POST */
		URI uri = pathToURI(filePath);
		LOGGER.warn("Writing URI, which is unsupported: " + filePath);
	}

	@Override
	public void writeFile(Path filePath, File file) throws IOException {
		URI uri = pathToURI(filePath);
		/* Should do a HTTP POST */
		LOGGER.warn("Writing URI, which is unsupported: " + filePath);
	}

	@Override
	public void createDirectories(Path directoryPath) throws IOException {
		/* Undefined behavior */
		LOGGER.warn("Undefined method for URIs");
	}

	@Override
	public boolean exist(Path filePath) throws IOException {
		URI uri = pathToURI(filePath);
		/* Should do a HTTP GET and check for 200/404 http codes */
		LOGGER.warn("Checking if URI exists, which is unsupported: " + filePath);
		return true;
	}

	@Override
	public boolean exist(URI uri) throws IOException {
		/* Should do a HTTP GET and check for 200/404 http codes */
		LOGGER.warn("Checking if URI exists, which is unsupported: " + uri);
		return true;
	}

	
	@Override
	public void delete(Path filePath) throws IOException {
		URI uri = pathToURI(filePath);
		/* Should do a HTTP DELETE */
		LOGGER.warn("Deleting URI, which is unsupported: " + filePath);

	}

	@Override
	public Path getAbsolutePath(Path filePath) throws IOException {
		LOGGER.warn("Checking path: " + filePath);
		URI uri = pathToURI(filePath);
		return filePath;
	}

	public Path getAbsolutePath(URI uri) throws IOException {
		LOGGER.warn("Checking path: " + uri);
		return Path.of(uri.getPath());
	}

}
