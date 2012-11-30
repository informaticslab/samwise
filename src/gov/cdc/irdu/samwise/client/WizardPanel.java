/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.ReadabilityScores;
import gov.cdc.irdu.samwise.shared.sam.SAM;
import gov.cdc.irdu.samwise.shared.sam.SamGroup;
import gov.cdc.irdu.samwise.shared.sam.SamRating;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author joel
 *
 */
public class WizardPanel extends SimplePanel {
	private static final int NOT_SUITABLE = 0;
	private static final int ADEQUATE = 1;
	private static final int SUPERIOR = 2;
	
	private SAM sam;
	private List<Panel> factorPanels = new ArrayList<Panel>();
	private DocumentServiceAsync documentService = GWT.create(DocumentService.class);

	public WizardPanel(SAM sam) {
		this.sam = sam;
		
		VerticalPanel groupsPanel = new VerticalPanel();
		groupsPanel.addStyleName("groupsPanel");

		for (SamGroup group: sam.getGroups()) {
			groupsPanel.add(createGroupPanel(group));
		}
		
		ScrollPanel scrollPanel = new ScrollPanel(groupsPanel);
		scrollPanel.addStyleName("wizardScrollPanel");
		
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.addStyleName("wizardMainPanel");
		mainPanel.add(scrollPanel);

		this.add(mainPanel);		
	}
	
	private Panel createGroupPanel(SamGroup group) {
		Label titleLabel = new Label(group.getTitle());
		titleLabel.addStyleName("wizardTitle");
		
		FlexTable factorsTable = new FlexTable();
		factorsTable.addStyleName("factorsTable");
		
		int row = 0;
		for (SamRating rating: group.getRatings()) {
			VerticalPanel factorPanel = new VerticalPanel();
			factorPanel.addStyleName("factorPanel");
			
			Label factorLabel = new Label(rating.getHeading());
			Button helpButton = createHelpButton(rating);
			HorizontalPanel factor = new HorizontalPanel();
			factor.add(factorLabel);
			factor.add(helpButton);
			factor.setCellHorizontalAlignment(helpButton, HasHorizontalAlignment.ALIGN_RIGHT);
			factor.setWidth("100%");
			
			factorPanel.add(factor);
			final Panel radioButtons = createRadioButtons(rating);
			
			if (rating.hasAnalysis()) {
				final SamRating saveRating = rating;
				final ClickHandler click = new ClickHandler() {
					public void onClick(ClickEvent event) {
						runAnalysis(saveRating, radioButtons);
					}
				};
				final PushButton runAnalysisButton = new PushButton(new Image("images/run_button.png"));
				runAnalysisButton.addClickHandler(click);
				runAnalysisButton.addMouseUpHandler(new MouseUpHandler() {
					public void onMouseUp(MouseUpEvent event) {
		            	VerticalPanel parent = (VerticalPanel) runAnalysisButton.getParent();
		            	int index = parent.getWidgetIndex(runAnalysisButton);
		            	runAnalysisButton.removeFromParent();
		                PushButton rerunButton = new PushButton(new Image("images/rerun_button.png"));
		                rerunButton.addClickHandler(click);
		                rerunButton.setStyleName("analysisButton");
		                parent.insert(rerunButton, index);
					}
				});
				
				runAnalysisButton.setStyleName("analysisButton");
				factorPanel.add(runAnalysisButton);
			}
			
			factorPanel.add(radioButtons);
			
			factorPanels.add(factorPanel);
			
			factorsTable.setWidget(row++, 0, factorPanel);
			factorsTable.setHTML(row++, 0, "<hr />");
		}

		VerticalPanel groupPanel = new VerticalPanel();
		groupPanel.addStyleName("groupPanel");
		groupPanel.add(titleLabel);
		groupPanel.add(factorsTable);

		return groupPanel;
	}
	
	private Button createHelpButton(SamRating rating) {
		Button help = new Button("?");
		final HelpPanel helpPanel = new HelpPanel(rating.getHeading(), rating.getExplanation());
		
		help.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				helpPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
					     int left = (Window.getClientWidth() - offsetWidth) / 3;
				            int top = (Window.getClientHeight() - offsetHeight) / 3;
				            helpPanel.setPopupPosition(left, top);
				    }
				});
			}
		});
		
		return help;
	}
	
	private Panel createRadioButtons(final SamRating rating) {
		final String groupName = rating.getHeading();
		HorizontalPanel buttons = new HorizontalPanel();
		
		RadioButton notApplicable = new RadioButton(groupName, "N/A");
		notApplicable.addStyleName("ratingButton");
		notApplicable.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rating.setScore(null);
			}
		});
		
		RadioButton notSuitable = new RadioButton(groupName, "Not Suitable");
		notSuitable.addStyleName("ratingButton");
		notSuitable.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rating.setScore(0);
			}
		});
		
		RadioButton adequate = new RadioButton(groupName, "Adequate");
		adequate.addStyleName("ratingButton");
		adequate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rating.setScore(1);
			}
		});
		
		RadioButton superior = new RadioButton(groupName, "Superior");
		superior.addStyleName("ratingButton");
		superior.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rating.setScore(2);
			}
		});
		
		buttons.add(notApplicable);
		buttons.add(notSuitable);
		buttons.add(adequate);
		buttons.add(superior);
		
		Integer score = rating.getScore();
		if (null == score) {
			notApplicable.setValue(true);
		} else {
			RadioButton button = (RadioButton) buttons.getWidget(score + 1);
			button.setValue(true);
		}
		
		return buttons;
	}
	
	private int calculateSuitability(float gradeLevel) {
      final int score;
      
      if (gradeLevel < 6.0) {
          score = SUPERIOR;
      }
      else if (gradeLevel < 9.0) {
          score = ADEQUATE;
      } else {
          score = NOT_SUITABLE;
      }
	   
      return score;
	}
	
	private void runAnalysis(final SamRating rating, Panel panel) {
		final HorizontalPanel radioButtons = (HorizontalPanel) panel;
	        
        final AnalysisPopup popup = new AnalysisPopup(new AnalysisCallback() {
            public void onCancel()
            {
                // TODO Auto-generated method stub                
            }
    
            public void onOk(ReadabilityScores scores)
            {
                int score = calculateSuitability(scores.getGradeLevel());
                rating.setScore(score);
                RadioButton radio = (RadioButton) radioButtons.getWidget(score + 1);
                radio.setValue(true);        
            } 
        });
        
        ProgressPopup progress = new ProgressPopup("Analyzing");
        progress.center();
        progress.show();
                
		if (rating.getHeading().equals("Reading grade level")) {
		    runReadingGradeAnalysis(rating, popup, progress);
		}
	}
	
    private void runReadingGradeAnalysis(final SamRating rating, final AnalysisPopup popup, final ProgressPopup progress) {
        documentService.getReadability(sam.getDocument().getName(), new AsyncCallback<ReadabilityScores>() {
            public void onFailure(Throwable caught) {
            	progress.cancel();
                Window.alert("Failed to get readability scores: " + caught.getMessage());
            }

            public void onSuccess(ReadabilityScores results) {              
                float gradeLevel =  results.getKincaidGradeLevelScore();
                
                progress.cancel();
                
                if (gradeLevel < 0.0) {
                    Window.alert("Sorry, unable to analyze the text of this document. " +
                                 "This can happen for several reasons. \n\n" +
                                 "One possibility is that there is no actual text in " +
                                 "the document. For instance, if the document was " +
                                 "scanned, then, what appears to be text is actually an " +
                                 "image of the text. \n\n" +
                                 "Another possibility is that the document is password protected.");
                    return;
                }
                
                popup.populateData(rating.getHeading(), results); 
                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = (Window.getClientWidth() - offsetWidth) / 6;
                        int top = 20;
                        popup.setPopupPosition(left, top);
                      }
                    });
            }
        });
    }
    
}
