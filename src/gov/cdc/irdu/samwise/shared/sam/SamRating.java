/**
 * 
 */
package gov.cdc.irdu.samwise.shared.sam;

import java.io.Serializable;

/**
 * @author joel
 *
 */
public class SamRating implements Serializable {
	private static final long serialVersionUID = 4501118636298052138L;

	private String heading;
	private Integer score = null;
	private String comments = null;
	private SamExplanation explanation = null;
	private boolean hasAnalysis = false;
	
	public SamRating() { }
	
	public SamRating(String heading) {
		this(heading, null, null, null);
	}
	
	public SamRating(String heading, Integer score, String comments, SamExplanation explanation) {
		this.heading = heading;
		this.score = score;
		this.comments = comments;
		this.explanation = explanation;
	}
	
	public SamRating clone() {
		SamRating rating = new SamRating();
		rating.heading = this.heading;
		rating.score = null == this.score ? null : new Integer(this.score);
		rating.comments = null == this.comments ? null : new String(this.comments);
		rating.explanation = this.explanation;
		rating.hasAnalysis = this.hasAnalysis;
		return rating;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public SamExplanation getExplanation() {
		return explanation;
	}

	public void setExplanation(SamExplanation explanation) {
		this.explanation = explanation;
	}

	public boolean hasAnalysis() {
		return hasAnalysis;
	}

	public void setHasAnalysis(boolean hasAnalysis) {
		this.hasAnalysis = hasAnalysis;
	}

}
