/**
 * 
 */
package gov.cdc.irdu.samwise.server;

import gov.cdc.irdu.samwise.Config;
import gov.cdc.irdu.samwise.client.DocumentService;
import gov.cdc.irdu.samwise.server.dao.DocumentDAO;
import gov.cdc.irdu.samwise.shared.Document;
import gov.cdc.irdu.samwise.shared.ReadabilityScores;
import gov.cdc.irdu.samwise.util.ReadabilityScoresCalculator;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.graphics.text.PageText;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author joel
 *
 */
public class DocumentServiceImpl extends RemoteServiceServlet implements DocumentService {

	private static final long serialVersionUID = -3833368355739988903L;
	
	private static Config CONFIG = Config.getConfig();
	private static Logger LOG = Logger.getLogger(DocumentServiceImpl.class);
	
	private DocumentDAO documentDAO;
		
	public DocumentServiceImpl() {
		try {
			documentDAO = new DocumentDAO();
		} catch (Exception e) {
			LOG.error("Failed to initialize the SAM Factory", e);
		}
	}
	
	public String deleteDocument(String documentName) {
		LOG.info("Deleting document " + documentName);
		documentDAO.delete(documentName);
		return documentName;
	}
	
	/**
	 * Gets a specific document -- including its list of DocumentRating instances.
	 * 
	 * Returns null if the document does not exist.
	 * 
	 * @param documentName
	 * @return Document
	 */
	public Document getDocument(String documentName) {
		Document document = documentDAO.find(documentName);
		return document;
	}

	public List<Document> getDocuments() {
		List<Document> documents = documentDAO.findAll();
		return documents;
	}
	
	public ReadabilityScores getReadability(String documentName) {
		LOG.info("Getting readability scores for document " + documentName);
		
		Document document = documentDAO.find(documentName);
		ReadabilityScores scores = document.getReadabilityScores();
		
		// If we've already calculated and stored them, then we are done
		if (null != scores)
			return scores;
		
		// OK, this is our first time. So, we have to analyze the file.
		File file = new File(CONFIG.getDocumentFolder(), documentName);
		
		if (!file.exists()) {
			LOG.error("Specified document, " + documentName + " does not exist on this server");
			return null;
		}
		
//		PDDocument doc = null;
//		String text;
//		try {
//			doc = PDDocument.load(file);
//			LOG.debug("Loaded PDF document with " + doc.getNumberOfPages() + " pages");
//			PDFTextStripper stripper = new PDFTextStripper();
//			text = stripper.getText(doc);
//		} catch (IOException e) {
//			LOG.error("Failed to extract text from PDF document", e);
//			return null;
//		} finally {
//			if (null != doc)
//				try { doc.close(); } catch(Exception e) { }
//		}
		
		String text;
		try {
			text = extractText(file);
		} catch (Exception e) {
			LOG.error("Failed to extract text from document", e);
			throw new RuntimeException("Failed to extract text from document");
		} 
		
		if (null == text) {
			LOG.info("Extracted text is NULL");
			throw new RuntimeException("Extracted text is NULL");
		}
		
		LOG.debug("Extracted " + text.length() + " bytes of text from document");
		
		scores = ReadabilityScoresCalculator.analyze(text);
		document.setReadabilityScores(scores);
		
		return scores;
	}
	
	public Document updateDocument(Document document) {
		documentDAO.update(document);
		return document;
	}
	
	private String extractText(File file) throws PDFException, PDFSecurityException, IOException {
		org.icepdf.core.pobjects.Document document = new org.icepdf.core.pobjects.Document();
		document.setFile(file.getPath());
		StringWriter buffer = new StringWriter();
		
		LOG.debug("Loaded PDF document with " + document.getNumberOfPages() + " pages");
		
		for (int i = 0; i < document.getNumberOfPages(); i++) {
			PageText pageText = document.getPageText(i);
			buffer.write(pageText.toString());
		}
		
		String text = buffer.toString();
		buffer.close();
		
		return text;
	}

}
