/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.DocumentRating;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.DialogBox;

/**
 * This is the base class for all evaluation dialog boxes.
 * 
 * @author Joel M. Rives
 * Feb 23, 2011
 */
public abstract class EvaluationDialog extends DialogBox {
	private static Map<String, EvaluationDialog> registry = new HashMap<String, EvaluationDialog>();

	private DocumentRating rating;
	private EvaluationDialogCallback callback;

	public static EvaluationDialog getDialog(String name) {
		if (!registry.containsKey(name))
			return null;
		
		return registry.get(name);	
	}
	
	public static void registerDialog(String name, EvaluationDialog dialog) {
		registry.put(name, dialog);
	}
	
	public EvaluationDialog() {
	}
	
	public EvaluationDialog(DocumentRating rating) {
		this(rating, null);
	}
	
	public EvaluationDialog(DocumentRating rating, EvaluationDialogCallback callback) {
		setDocumentRating(rating);
		this.callback = callback;
	}
	
	public DocumentRating getDocumentRating() {
		return this.rating;
	}
	
	public void setDocumentRating(DocumentRating rating) {
		this.rating = rating;
	}
	
	public void setEvaluationCallback(EvaluationDialogCallback callback) {
		this.callback = callback;
	}
	
	protected void cancel() {
		this.hide();
		if (null != callback)
			callback.onCancel(this);
	}
	
	protected void complete() {
		this.hide();
		if (null != callback)
			callback.onComplete(this);
	}
	
}
