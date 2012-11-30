/**
 * 
 */
package gov.cdc.irdu.samwise.shared;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Joel M. Rives
 * Feb 23, 2011
 */
public interface DocumentRating extends Serializable {

	Document getDocument();
	
	String getTitle();
	
	Float getScore();
	
	Float getPossibleScore();
	
	String getScoreString();
	
	Date getLastRun();
	
	void setLastRun(Date lastRun);
	
}
