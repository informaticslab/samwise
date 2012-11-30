/**
 * 
 */
package gov.cdc.irdu.samwise.server.dao;

import gov.cdc.irdu.samwise.Config;
import gov.cdc.irdu.samwise.server.SamFactory;
import gov.cdc.irdu.samwise.shared.Document;
import gov.cdc.irdu.samwise.shared.DocumentRating;
import gov.cdc.irdu.samwise.shared.pi.PmoseIkirsch;
import gov.cdc.irdu.samwise.shared.sam.SAM;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

/**
 *
 * @author Joel M. Rives
 * Feb 25, 2011
 */
public class DocumentDAO {
	
	private static final List<DocumentRating> EMPTY_RATINGS_LIST = new ArrayList<DocumentRating>();
	
	private static Logger LOG = Logger.getLogger(DocumentDAO.class);
	private static Map<String, Document> documentMap = new HashMap<String, Document>();
	private static Config CONFIG = Config.getConfig();
	private static DocumentRatingDAO ratingDAO = new DocumentRatingDAO();
	private static SamFactory samFactory = null;
		
	public DocumentDAO() {
		if (null == samFactory) {
			try {
				samFactory = new SamFactory();
			} catch (DocumentException e) {
				throw new RuntimeException(e);
			}
		}
				
		// Do this for now so that we do not orphan documents.
		loadExistingDocuments();
	}
	
	public Document create(File file) {
		if (!file.exists())
			return null;
		
		String fileName = file.getName();
		String url = CONFIG.getDocumentsURL()  + fileName;

		Document document = new Document(file.getName(), url, new Date(), file.length());
		
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		File imageFolder = new File(CONFIG.getDocumentFolder(), fileName);
		if (imageFolder.exists()) {
			File[] files = imageFolder.listFiles();
			for (File imageFile: files) {
				String imageUrl = CONFIG.getDocumentsURL() + fileName + "/" + imageFile.getName();
				document.addImageUrl(imageUrl);
			}
		}
		
		File htmlFile = new File(CONFIG.getDocumentFolder(), fileName + ".html");
		if (htmlFile.exists()) {
			document.setHtmlUrl(CONFIG.getDocumentsURL() + fileName + ".html");
		}
		
		documentMap.put(document.getName(), document);
		
		SAM sam = samFactory.getSamForDocument(document);
		ratingDAO.create(sam);
		
		PmoseIkirsch pi = new PmoseIkirsch();
		pi.setDocument(document);
		ratingDAO.create(pi);

		return document;
	}
	
	public void delete(String documentName) {
		if (!documentMap.containsKey(documentName))
			return;
		
		documentMap.remove(documentName);
		ratingDAO.delete(documentName);

		File file = new File(CONFIG.getDocumentFolder(), documentName);
		
		if (!file.exists())
			return;
		
		file.delete();
		
		File imageFolder = new File(CONFIG.getDocumentFolder(), documentName.substring(0, documentName.lastIndexOf('.')));
		try {
			FileUtils.deleteDirectory(imageFolder);
		} catch (IOException e) {
			LOG.error("Failed to delete image folder " + imageFolder.getName(), e);
		}
	}
	
	/** 
	 * This finder returns the unique Document specified by the document name.
	 * It also loads the associated DocumentRatings.
	 * 
	 * @param documentName
	 * @return
	 */
	public Document find(String documentName) {
		if (!documentMap.containsKey(documentName))
			throw new DoesNotExistException(documentName);
		
		Document document = documentMap.get(documentName);
		document.setRatings(ratingDAO.find(documentName));
		
		return document;
	}
	
	/**
	 * This finder returns the complete collection of Documents. In this case,
	 * the associated DocumentRatings are not loaded.
	 * @return
	 */
	public List<Document> findAll() {
		List<Document> documentList = new ArrayList<Document>();
		
		for (String key: documentMap.keySet()) 
			documentList.add(documentMap.get(key));
		
		return documentList;
	}
	
	public Document update(Document document) {
		for (DocumentRating rating: document.getRatings()) 
			ratingDAO.update(rating);
		
		document.setRatings(EMPTY_RATINGS_LIST);
		documentMap.put(document.getName(), document);
		
		return document;
	}
	
	private void loadExistingDocuments() {
		File[] files = CONFIG.getDocumentFolder().listFiles(new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName().toLowerCase();
				
				return name.endsWith(".pdf") || name.endsWith(".docx");
			}
		});
		
		for (File file: files) {
			if (!documentMap.containsKey(file.getName()))
				create(file);
		}
	}
}
