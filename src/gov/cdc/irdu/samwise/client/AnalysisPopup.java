package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.ReadabilityScores;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author <a href="mailto:jmrives@spiral-soft.com">Joel M. Rives</a>
 * Created on Feb 21, 2011
 *
 */
public class AnalysisPopup extends PopupPanel
{
	private NumberFormat numberFormatter = NumberFormat.getFormat("0.00");
    private final AnalysisCallback callback;
    
    public AnalysisPopup(AnalysisCallback callback) {
        this.callback = callback;        
    }
    
    public void populateData(String heading, final ReadabilityScores scores) {
        Label topLabel = new Label("Our analysis of the document provides the following scores for");
        topLabel.addStyleName("analysisTopLabel");
        Label headingLabel = new Label(heading.toLowerCase() + ":");
        headingLabel.addStyleName("analysisHeadingLabel");
        
        HorizontalPanel topPanel = new HorizontalPanel();
        topPanel.add(topLabel);
        topPanel.add(new HTML("&nbsp;"));
        topPanel.add(headingLabel);
        topPanel.addStyleName("analysisTopPanel");
        
        FlexTable table = new FlexTable();
        table.addStyleName("analysisTable");
        table.setCellPadding(6);
        
        addHeaders(table);
        
        int row = 1;

        Grid readingEase = new Grid(3, 2);
        readingEase.addStyleName("analysisGrid");
        readingEase.setText(0, 0, "90.0-100.0");
        readingEase.setText(1, 0, "60.0-70.0");
        readingEase.setText(2, 0, "0.0-30.0");
        readingEase.setText(0, 1, "easily understandable by an average 11-year-old student");
        readingEase.setText(1, 1, "easily understandable by 13- to 15-year-old students");
        readingEase.setText(2, 1, "best understood by university graduates");
        
        addRow(table, row++, "Flesch reading ease score", scores.getFleschReadingEaseScore(), readingEase);
        addRow(table, row++, "Fog index", scores.getFogIndex(), "Approximate grade level");
        addRow(table, row++, "Kincaid grade level score", scores.getKincaidGradeLevelScore(), "Approximate grade level");
        addRow(table, row++, "Fry readability grade level", scores.getFryReadabilityScore(), "Approximate grade level (value of -1 indicates out of bounds)");
        addRow(table, row++, "Percent complex words", scores.getPercentComplexWords(), null);
        addRow(table, row++, "Average syllables per word", scores.getAverageSyllablesPerWord(), null);
        addRow(table, row++, "Average words per sentence", scores.getAverageWordsPerSentence(), null);
        addRow(table, row++, "Average syllables per 100 words", scores.getAverageSyllablesPer100Words(), null);
        addRow(table, row++, "Average sentences per 100 words", scores.getAverageSentencesPer100Words(), null);
       
        float gradeLevel = scores.getGradeLevel();
        
        String scoreImage = "not_suitable.png";
        if (gradeLevel < 6.0) {
            scoreImage = "superior.png";
        }
        else if (gradeLevel < 9.0) {
            scoreImage = "adequate.png";
        } 
        
        Label bottomLabel = new Label("Based on these scores, we recommend the following rating:");
        bottomLabel.addStyleName("analysisBottomLabel");
        Image bottomImage = new Image("images/" + scoreImage);
        bottomImage.addStyleName("analysisBottomImage");
        HorizontalPanel bottomPanel = new HorizontalPanel();
        bottomPanel.addStyleName("analysisBottomPanel");
        bottomPanel.add(bottomLabel);
        bottomPanel.add(new HTML("&nbsp;"));
        bottomPanel.add(bottomImage);

        final PopupPanel popup = this;
        Button okButton = new Button("Accept Recommendation");
        okButton.setStyleName("okButton");
        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popup.hide();
                callback.onOk(scores);
            }
        });
        
        Button cancelButton = new Button("Decline Recommendation");
        cancelButton.setStyleName("cancelButton");
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popup.hide();
                callback.onCancel();
            }
        });
        
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.addStyleName("analysisButtonPanel");
        buttons.add(okButton);
        buttons.add(new HTML("&nbsp;"));
        buttons.add(cancelButton);
        
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(topPanel);
        mainPanel.add(table);
        mainPanel.add(bottomPanel);
        mainPanel.setCellHorizontalAlignment(bottomPanel, HasHorizontalAlignment.ALIGN_RIGHT);
        mainPanel.add(buttons);
        mainPanel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);
        
        setWidget(mainPanel);
        addStyleName("analysisPopup");        
    }
    
    private void addRow(FlexTable table, int row, String title, float value, Object extra) {
    	table.setText(row, 0, title);
    	table.getCellFormatter().addStyleName(row, 0, "analysisTableLabelColumn");
    	table.setText(row, 1, numberFormatter.format(value));
    	table.getCellFormatter().addStyleName(row, 1, "analysisTableValueColumn");
    	if (null != extra) {
    		if (extra instanceof String) {
    			table.setText(row, 2, (String) extra);
    		} else {
    			table.setWidget(row, 2, (Widget) extra);
    		}
    	}
		table.getCellFormatter().addStyleName(row, 2, "analysisTableExtraColumn");
    }
    
    private void addHeaders(FlexTable table) {
    	table.setText(0, 0, "Test/Measure");
    	table.getCellFormatter().addStyleName(0, 0, "analysisTableHeader");
    	table.setText(0, 1, "Score");
    	table.getCellFormatter().addStyleName(0, 1, "analysisTableHeader");
    	table.setText(0, 2, "Interpretation");
    	table.getCellFormatter().addStyleName(0, 2, "analysisTableHeader");
    }
    
}
