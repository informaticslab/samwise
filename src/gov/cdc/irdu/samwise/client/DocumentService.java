/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.Document;
import gov.cdc.irdu.samwise.shared.ReadabilityScores;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author joel
 *
 */
@RemoteServiceRelativePath("document")
public interface DocumentService extends RemoteService {

	String deleteDocument(String documentName);
	
	Document getDocument(String documentName);
	
	List<Document> getDocuments();
	
	ReadabilityScores getReadability(String documentName);
	
	Document updateDocument(Document document);
	
}
