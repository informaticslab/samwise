/**
 * 
 */
package gov.cdc.irdu.samwise.shared.sam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author joel
 *
 */
public class SamGroup implements Serializable {
	private static final long serialVersionUID = 728821858998418692L;

	private String title;
	private List<SamRating> ratings;
	
	public SamGroup() { }
	
	public SamGroup(String title, List<SamRating> ratings) { 
		this.ratings = ratings;
		this.title = title;
	}
	
	public SamGroup clone() {
		SamGroup group = new SamGroup();
		group.title = this.title;
		group.ratings = new ArrayList<SamRating>();
		for (SamRating rating: this.ratings) {
			group.ratings.add(rating.clone());
		}
		return group;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SamRating> getRatings() {
		return ratings;
	}

	public void setRatings(List<SamRating> ratings) {
		this.ratings = ratings;
	}
	
	public Integer getScore() {
		Integer score = null;
		
		for (SamRating rating: ratings) {
			Integer ratingScore = rating.getScore();
			
			if (null != ratingScore) 
				score = null == score ? ratingScore : score + ratingScore;
		}
		
		return score;
	}
	
	public Integer getPossibleScore() {
		Integer score = null;
		
		for (SamRating rating: ratings) {
			Integer ratingScore = rating.getScore();
			
			if (null != ratingScore)
				score = null == score ? 2 : score + 2;
		}
		
		return score;
	}
}
