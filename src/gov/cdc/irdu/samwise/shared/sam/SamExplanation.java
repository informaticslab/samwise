/**
 * 
 */
package gov.cdc.irdu.samwise.shared.sam;

import java.io.Serializable;
import java.util.List;

/**
 * @author joel
 *
 */
public class SamExplanation implements Serializable {
	private static final long serialVersionUID = 2132576462835107677L;

	private String text;
	private String superior;
	private List<String> superiorList;
	private String adequate;
	private List<String> adequateList;
	private String notSuitable;
	private List<String> notSuitableList;
	
	public SamExplanation() { }
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSuperior() {
		return superior;
	}
	public void setSuperior(String superior) {
		this.superior = superior;
	}
	public List<String> getSuperiorList() {
		return superiorList;
	}
	public void setSuperiorList(List<String> superiorList) {
		this.superiorList = superiorList;
	}
	public String getAdequate() {
		return adequate;
	}
	public void setAdequate(String adequate) {
		this.adequate = adequate;
	}
	public List<String> getAdequateList() {
		return adequateList;
	}
	public void setAdequateList(List<String> adequateList) {
		this.adequateList = adequateList;
	}
	public String getNotSuitable() {
		return notSuitable;
	}
	public void setNotSuitable(String notSuitable) {
		this.notSuitable = notSuitable;
	}
	public List<String> getNotSuitableList() {
		return notSuitableList;
	}
	public void setNotSuitableList(List<String> notSuitableList) {
		this.notSuitableList = notSuitableList;
	}
	
	
}
