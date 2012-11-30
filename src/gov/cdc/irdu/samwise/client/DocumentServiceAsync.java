/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.Document;
import gov.cdc.irdu.samwise.shared.ReadabilityScores;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author joel
 *
 */
public interface DocumentServiceAsync {

	void deleteDocument(String documentName, AsyncCallback<String> callback);
	
	void getDocument(String documentName, AsyncCallback<Document> callback);
	
	void getDocuments(AsyncCallback<List<Document>> callback);
	
	void getReadability(String documentName, AsyncCallback<ReadabilityScores> callback);
	
	void updateDocument(Document document, AsyncCallback<Document> callback);
	
}
