/**
 * 
 */
package gov.cdc.irdu.samwise.client;

/**
 * @author joel
 *
 */
public interface EvaluationDialogCallback {

	void onCancel(EvaluationDialog dialog);
	
	void onComplete(EvaluationDialog dialog);
	
}
