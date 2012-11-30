/**
 * 
 */
package gov.cdc.irdu.samwise.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 *
 * @author Joel M. Rives
 * Feb 24, 2011
 */
public class ProgressPopup extends PopupPanel {
	private ProgressTimer progressTimer;
	
	public ProgressPopup(final String label) {
        setGlassEnabled(true);
        
        final Label progressLabel = new Label(label);
        progressLabel.setWidth("400px");
        
        Button cancelButton = new Button("X");
        cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				cancel();				
			}
        });

        setWidget(progressLabel);
        
        progressTimer = new ProgressTimer(progressLabel);
    }
    
    public void cancel() {
        progressTimer.cancel();
        hide();
    }

	@Override
	public void show() {
		progressTimer.reset();
        progressTimer.scheduleRepeating(1000); // update every second
		super.show();
	}
    
    public class ProgressTimer extends Timer {
    	private static final int MAX_COUNT = 60;
    	
    	private Label progressLabel;
    	private String label;
        private StringBuffer buffer;
        private int count = 0;
        
        public ProgressTimer(Label progressLabel) {
        	this.progressLabel = progressLabel;
        	this.label = progressLabel.getText();
        	reset();
        }
        
        public void reset() {
        	this.buffer = new StringBuffer(label);
        	progressLabel.setText(buffer.toString());
        	this.count = 0;
        }
        
        public void run() {
            buffer.append('.');
            progressLabel.setText(buffer.toString());
            if (++count >= MAX_COUNT) {
            	reset();
            }
        }
    }
    
}
