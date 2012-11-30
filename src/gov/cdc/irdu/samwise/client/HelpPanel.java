/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.sam.SamExplanation;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author joel
 *
 */
public class HelpPanel extends PopupPanel {

	public HelpPanel(String subject, SamExplanation explanation) {
		Label subjectLabel = new Label(subject);
		subjectLabel.addStyleName("helpSubjectLabel");
		
		FlexTable table = new FlexTable();
		table.addStyleName("helpTable");
		int row = 0;
		
		table.setText(row, 0, "Explanation:");
		table.getCellFormatter().addStyleName(row, 0, "helpTableHeader");
		table.setText(row++, 1, explanation.getText());
		
		table.setText(row, 0, "Superior:");
		table.getCellFormatter().addStyleName(row, 0, "helpTableHeader");
		if (null != explanation.getSuperior() && explanation.getSuperior().trim().length() > 0) 
			table.setText(row++, 1, explanation.getSuperior());
		if (null != explanation.getSuperiorList())
			table.setWidget(row++, 1, createList(explanation.getSuperiorList()));		
		
		table.setText(row, 0, "Adequate:");
		table.getCellFormatter().addStyleName(row, 0, "helpTableHeader");
		if (null != explanation.getAdequate() && explanation.getAdequate().trim().length() > 0) 
			table.setText(row++, 1, explanation.getAdequate());
		if (null != explanation.getAdequateList())
			table.setWidget(row++, 1, createList(explanation.getAdequateList()));
		
		table.setText(row, 0, "Not Suitable:");
		table.getCellFormatter().addStyleName(row, 0, "helpTableHeader");
		if (null != explanation.getNotSuitable() && explanation.getNotSuitable().trim().length() > 0) 
			table.setText(row++, 1, explanation.getNotSuitable());
		if (null != explanation.getNotSuitableList())
			table.setWidget(row++, 1, createList(explanation.getNotSuitableList()));
		
		Button okButton = new Button("OK");
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(subjectLabel);
		mainPanel.add(table);
		mainPanel.add(okButton);
		mainPanel.setCellHorizontalAlignment(okButton, HasHorizontalAlignment.ALIGN_CENTER);
		
		this.add(mainPanel);
		this.addStyleName("helpPanel");
		this.setGlassEnabled(true);
	}
	
	private Widget createList(List<String> listItems) {
		FlexTable list = new FlexTable();
		
		for (int i = 0; i < listItems.size(); i++) {
			list.setText(i, 0, Integer.toString(i + 1) + ".");
			list.getCellFormatter().addStyleName(i, 0, "listTableHeader");
			list.setText(i, 1, listItems.get(i));
		}
		
		return list;
	}
	
}
