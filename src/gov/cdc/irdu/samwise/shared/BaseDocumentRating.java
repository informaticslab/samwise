/**
 * 
 */
package gov.cdc.irdu.samwise.shared;

import java.util.Date;

/**
 *
 * @author Joel M. Rives
 * Feb 24, 2011
 */
public abstract class BaseDocumentRating implements DocumentRating {
	private static final long serialVersionUID = 8298180460046046869L;

	private Document document;
	private String title;
	private Date lastRun = null;

	public BaseDocumentRating(String title) {
		this.title = title;
	}
	
	@Override
	public Document getDocument() {
		return this.document;
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public Date getLastRun() {
		return this.lastRun;
	}

	@Override
	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}
	
}
