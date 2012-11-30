/**
 * 
 */
package gov.cdc.irdu.samwise.shared.sam;

import gov.cdc.irdu.samwise.shared.BaseDocumentRating;

import java.util.List;

/**
 *
 * @author Joel M. Rives
 * Feb 23, 2011
 */
public class SAM extends BaseDocumentRating {
	private static final long serialVersionUID = -5943275211787584826L;

	private List<SamGroup> groups;
	
	public SAM() { 
		super("SAM");
	}
	
	public List<SamGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<SamGroup> groups) {
		this.groups = groups;
	}
	
	@Override
	public Float getScore() {
		Float score = null;
		
		for (SamGroup group: groups) {
			Integer groupScore = group.getScore();
			
			if (null != groupScore)
				score = null == score ? groupScore : score + groupScore;
		}
		
		return score;
	}

	@Override
	public Float getPossibleScore() {
		Float score = null;
		
		for (SamGroup group: groups) {
			Integer groupScore = group.getPossibleScore();

			if (null != groupScore)
				score = null == score ? groupScore : score + groupScore;
		}
		
		return score;
	}

	@Override
	public String getScoreString() {
		Float score = getScore();
		Float possible = getPossibleScore();
		
		if (null == score || null == possible)
			return null;
		
		int percentage = (int)((score / possible) * 100);
		return percentage + "%";
	}

}
