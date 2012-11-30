/**
 * 
 */
package gov.cdc.irdu.samwise.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Joel M. Rives
 * Feb 23, 2011
 */
public class Document implements Serializable {

	private static final long serialVersionUID = 5534317795149658080L;

	private String name;
	private String url;
	private String htmlUrl = null;
	private Date timestamp;
	private long size;
	private List<String> imageUrls = new ArrayList<String>();
	private List<DocumentRating> ratings = new ArrayList<DocumentRating>();
	private ReadabilityScores readabilityScores = null;
	
	public Document() {	}
	
	public Document(String name, String url, Date timestamp, long size) {
		this.name = name;
		this.url = url;
		this.timestamp = timestamp;
		this.size = size;
	}
	
	public void addImageUrl(String url) {
		this.imageUrls.add(url);
	}
	
	public void addRating(DocumentRating rating) {
		this.ratings.add(rating);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public List<DocumentRating> getRatings() {
		return this.ratings;
	}

	public void setRatings(List<DocumentRating> ratings) {
		this.ratings = ratings;
	}

	public ReadabilityScores getReadabilityScores() {
		return readabilityScores;
	}

	public void setReadabilityScores(ReadabilityScores readabilityScores) {
		this.readabilityScores = readabilityScores;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}
	
}
