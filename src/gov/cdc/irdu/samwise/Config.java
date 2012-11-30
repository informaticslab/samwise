/**
 * 
 */
package gov.cdc.irdu.samwise;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * @author joel
 *
 */
public class Config {
	private static final String DOCUMENT_FOLDER_NAME = "documents";
	private static final String SERVER_PROPERTIES_FILE = "server.properties";
	
	private static Properties properties = new Properties();
	private static File documentFolder = null;
	private static File staticFileBase = null;
	
	private static Config SINGLETON = null;
	
	public static Config getConfig() {
		if (null == SINGLETON) {
			SINGLETON = new Config();
		}
		
		return SINGLETON;
	}
	
	public String getDocumentFolderName() {
		return DOCUMENT_FOLDER_NAME;
	}
	
	public File getDocumentFolder() {
		if (null == documentFolder) {
			documentFolder = new File(getStaticFileBase(), DOCUMENT_FOLDER_NAME);
			
			if (!documentFolder.exists())
				try {
					FileUtils.forceMkdir(documentFolder);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}

		return documentFolder;
	}
	
	public String getDocumentsURL() {
		return properties.getProperty("documents-url");
	}
	
	public InputStream getSamXml() {
		return getClass().getClassLoader().getResourceAsStream(properties.getProperty("sam-definition-file"));	
	}
	
	public File getStaticFileBase() {
		if (null == staticFileBase)
			staticFileBase = new File(properties.getProperty("static-file-base"));
		
		return staticFileBase;
	}
	
	private Config() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(SERVER_PROPERTIES_FILE);
		try {
			properties.load(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
