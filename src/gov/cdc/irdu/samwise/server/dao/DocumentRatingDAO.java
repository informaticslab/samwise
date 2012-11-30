/**
 * 
 */
package gov.cdc.irdu.samwise.server.dao;

import gov.cdc.irdu.samwise.shared.DocumentRating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Joel M. Rives
 * Feb 25, 2011
 */
public class DocumentRatingDAO {

	private static Map<String, List<DocumentRating>> documentRatings = new HashMap<String, List<DocumentRating>>();
	
	public DocumentRating create(DocumentRating rating) {
		String documentName = rating.getDocument().getName();
		
		if (!documentRatings.containsKey(documentName))
			documentRatings.put(documentName, new ArrayList<DocumentRating>());
		
		List<DocumentRating> ratings = documentRatings.get(documentName);
		ratings.add(rating);
		
		return rating;
	}
	
	public void delete(String documentName) {
		if (!documentRatings.containsKey(documentName))
			return;
		
		documentRatings.remove(documentName);
	}
	
	public List<DocumentRating> find(String documentName) {
		if (!documentRatings.containsKey(documentName))
			documentRatings.put(documentName, new ArrayList<DocumentRating>());
		
		return documentRatings.get(documentName);
	}
	
	public DocumentRating update(DocumentRating rating) {
		String documentName = rating.getDocument().getName();
		
		if (!documentRatings.containsKey(documentName))
			throw new DoesNotExistException(documentName);
		
		List<DocumentRating> ratings = documentRatings.get(documentName);
		
		DocumentRating found = null;
		for (DocumentRating entry: ratings) {
			if (rating.getTitle().equals(entry.getTitle()))
				found = entry;
		}
		
		if (null == found)
			throw new DoesNotExistException(documentName + "." + rating.getTitle());
		
		ratings.remove(found);
		ratings.add(rating);
		
		return rating;
	}
}
