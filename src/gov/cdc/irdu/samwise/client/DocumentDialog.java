/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.Document;
import gov.cdc.irdu.samwise.shared.DocumentRating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author joel
 *
 */
public class DocumentDialog extends DialogBox {

	private NumberFormat numberFormatter = NumberFormat.getFormat("0.00");
	private Document document;
	private Map<String, Integer> ratingRows = new HashMap<String, Integer>();
	private DocumentServiceAsync documentService = GWT.create(DocumentService.class);
	
	private FlexTable wizardTable;

	public DocumentDialog(Document document) {
		this.document = document;
		
		registerEvaluationDialogs();
		
		setText("Document Evaluation");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		Label title = new Label(document.getName());
		title.addStyleName("documentTitle");
		String timestamp = DateTimeFormat.getFormat("MMMM dd, yyyy HH:mm:ss").format(document.getTimestamp());
		Label modified = new Label("Last modified: " + timestamp);
		modified.addStyleName("documentModified");
		Label size = new Label("Size: " + sizeToString(document.getSize()));
		size.addStyleName("documentSize");
		
		VerticalPanel topPanel = new VerticalPanel();
		topPanel.addStyleName("documentTopPanel");
		topPanel.add(title);
		topPanel.add(modified);
		topPanel.add(size);
		
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.addStyleName("documentMainPanel");
		mainPanel.add(topPanel);
		mainPanel.add(createWizardTable());
		Panel buttonPanel = createButtonPanel();
		mainPanel.add(buttonPanel);
		mainPanel.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
		setWidget(mainPanel);
	}
	
	private Panel createButtonPanel() {
		final DialogBox dialog = this;
		Button done = new Button("Done");
		done.setStyleName("doneButton");
		done.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final ProgressPopup popup = new ProgressPopup("Updating");
				popup.center();
				popup.show();
				
				documentService.updateDocument(document, new AsyncCallback<Document>() {
					public void onFailure(Throwable caught) {
						popup.cancel();
						Window.alert("Failed to update document on the server: " + caught.getMessage());
						dialog.hide();
					}

					public void onSuccess(Document result) {
						popup.cancel();
						dialog.hide();
					}
				});
			}
		});
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(done);
		buttonPanel.setCellHorizontalAlignment(done, HasHorizontalAlignment.ALIGN_RIGHT);
		
		return buttonPanel;
	}
	
	private Button createWizardButton(final DocumentRating rating) {
		Button button = new Button(rating.getTitle());
		button.setStyleName("wizardButton");
		
		final EvaluationDialog dialog = EvaluationDialog.getDialog(rating.getTitle());
		
		if (null == dialog) {
			button.setEnabled(false);
			return button;
		}
		
		dialog.setEvaluationCallback(new EvaluationDialogCallback() {
			public void onCancel(EvaluationDialog dialog) {
				// TODO Auto-generated method stub
			}

			public void onComplete(EvaluationDialog dialog) {
				DocumentRating rating = dialog.getDocumentRating();	
				populateRating(rating);
			}
		});
		
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ProgressPopup popup = new ProgressPopup("Loading");
				popup.center();
				popup.show();
				
				dialog.setDocumentRating(rating);
				dialog.center();
				dialog.show();
				
				popup.cancel();
			}
		});
		
		return button;
	}
	
	private FlexTable createWizardTable() {
		wizardTable = new FlexTable();
		wizardTable.addStyleName("wizardTable");
		
		wizardTable.setText(0, 0, "Evalutation Wizard");
		wizardTable.setText(0, 1, "Score");
		wizardTable.setText(0, 2, "Possible Score");
		wizardTable.setText(0, 3, "Evaluation");
		wizardTable.setText(0, 4, "Last Run");
		wizardTable.getRowFormatter().addStyleName(0, "wizardTableHeader");
		
		for (int i = 1; i < 5; i++)
			wizardTable.getCellFormatter().addStyleName(0, i, "tableNumericColumn");
		
		populateWizardTable();
		
		return wizardTable;
	}
	
	private void populateRating(DocumentRating rating) {
		Integer row = ratingRows.get(rating.getTitle());
		
		if (null == rating.getScore())
			wizardTable.setText(row, 1, "----");
		else
			wizardTable.setText(row, 1, numberFormatter.format(rating.getScore()));
		wizardTable.getCellFormatter().addStyleName(row, 1, "tableNumericColumn");
		
		if (null == rating.getPossibleScore())
			wizardTable.setText(row, 2, "----");
		else
			wizardTable.setText(row, 2, numberFormatter.format(rating.getPossibleScore()));
		wizardTable.getCellFormatter().addStyleName(row, 2, "tableNumericColumn");
		
		if (null == rating.getScoreString())
			wizardTable.setText(row, 3, "----");
		else
			wizardTable.setText(row, 3, rating.getScoreString());
		wizardTable.getCellFormatter().addStyleName(row, 3, "tableNumericColumn");

		if (null == rating.getLastRun())
			wizardTable.setText(row, 4, "Never");
		else
			wizardTable.setText(row, 4, DateTimeFormat.getFormat("MM/dd/yy HH:mm:ss").format(rating.getLastRun()));
		wizardTable.getCellFormatter().addStyleName(row, 4, "tableNumericColumn");
	}
	
	private void populateWizardTable() {
		List<DocumentRating> ratings = document.getRatings();
		
		if (null == ratings || ratings.size() == 0) {
			Window.alert("No evaluation information available for this document");
			this.hide();
		}
		
		for (int i = 0; i < document.getRatings().size(); i++) {
			DocumentRating rating = document.getRatings().get(i);
			int row = i + 1;
			ratingRows.put(rating.getTitle(), new Integer(row));
			
			wizardTable.setWidget(row, 0, createWizardButton(rating));
			wizardTable.getCellFormatter().addStyleName(row, 0, "tableButtonColumn");
			
			populateRating(rating);
		}
		
	}
	
	private void registerEvaluationDialogs() {
		EvaluationDialog.registerDialog("SAM", new SamDialog());
	}
	
	private String sizeToString(long size) {
		NumberFormat sizeFormatter = NumberFormat.getFormat("0.0000");

		double kilobytes = ((double)size) / 1000;
		if (kilobytes < 1000)
			return sizeFormatter.format(kilobytes) + "K";
		double megabytes = kilobytes / 1000;
		if (megabytes < 1000)
			return sizeFormatter.format(megabytes) + "M";
		double gigabytes = megabytes / 1000;
		return sizeFormatter.format(gigabytes) + "G";
	}
	
}
