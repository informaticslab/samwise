/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.Document;
import gov.cdc.irdu.samwise.shared.DocumentRating;
import gov.cdc.irdu.samwise.shared.sam.SAM;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author joel
 *
 */
public class SamDialog extends EvaluationDialog {
	
	private SAM sam;
	private HorizontalPanel mainPanel;
	private Button completeButton;

	public SamDialog() {
		setText("Suitability Assessment of Materials");
		setGlassEnabled(true);
	}
	
	@Override
	public void setDocumentRating(DocumentRating rating) {
		super.setDocumentRating(rating);

		this.sam = (SAM) rating;
		this.sam.setLastRun(new Date());
		
		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.add(new WizardPanel(sam));
		Panel buttonsPanel = createButtonsPanel();
		leftPanel.add(buttonsPanel);
		leftPanel.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
		mainPanel = new HorizontalPanel();
		mainPanel.add(leftPanel);
		mainPanel.add(createDocumentPanel(sam.getDocument()));
		
		setWidget(mainPanel);
	}

	private Widget createDocumentPanel(Document document) {
		if (null != document.getHtmlUrl()) {
			Frame frame = new Frame(document.getHtmlUrl());
			frame.addStyleName("documentPanel");
			return frame;
		}
		
		VerticalPanel docPanel = new VerticalPanel();
		docPanel.addStyleName("documentPanel");
		for (String imageUrl: document.getImageUrls()) {
			Image image = new Image(imageUrl);
			image.addStyleName("pdfImage");
			docPanel.add(image);
		}
		
		ScrollPanel panel = new ScrollPanel(docPanel);
		panel.addStyleName("documentScrollPanel");
		return panel;
	}
	
	private Panel createButtonsPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		
		Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				cancel();
			}
		});
		
		completeButton = new Button("Complete");
		completeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				complete();
			}
		});
		
		panel.add(completeButton);
		panel.add(new HTML("&nbsp;"));
		panel.add(cancelButton);
		
		panel.addStyleName("buttonsPanel");
		
		return panel;
	}

}
