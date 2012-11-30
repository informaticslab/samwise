package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.Document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Samwise implements EntryPoint {
	private static final int REFRESH_INTERVAL = 1000 * 15; // 15 seconds
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable documentFlexTable = new FlexTable();
	private FormPanel uploadFormPanel = new FormPanel();
	private FileUpload uploadElement = new FileUpload();
	private Button uploadButton = new Button("Upload");
	private Label pickOne = new Label("Select a document to work with.");
	private List<Document> documents = new ArrayList<Document>();
	private DocumentServiceAsync documentService = GWT.create(DocumentService.class);
	
	final Element element = RootPanel.get().getElement();
	final String originalCursor = DOM.getStyleAttribute(element, "cursor");
	final ProgressPopup loadingPopup = new ProgressPopup("Loading");
	final ProgressPopup uploadingPopup = new ProgressPopup("Uploading");


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		mainPanel.add(createDocumentFlexTable());
		mainPanel.add(pickOne);
		pickOne.addStyleName("selectLabel");
		mainPanel.add(createUploadFormPanel());
		
		RootPanel panel = RootPanel.get("documentList");
		DOM.setInnerHTML(panel.getElement(), "");
		panel.add(mainPanel);
		
		// Setup timer to refresh list automatically
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshDocumentList();
			}
		  };
		  refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
		  	
		  refreshDocumentList();
	}
	
	private boolean contains(String fileName) {
		for (Document document: documents) {
			if (document.getName().equals(fileName))
				return true;
		}
		
		return false;
	}
	
	private FlexTable createDocumentFlexTable() {
		documentFlexTable.setTitle("Documents");
		documentFlexTable.setText(0, 1, "Title");
		documentFlexTable.setText(0, 2, "Size (bytes)");
		documentFlexTable.setText(0, 3, "Suitability (%)");
		documentFlexTable.setText(0, 4, "Delete");
		documentFlexTable.setCellPadding(6);
		documentFlexTable.addStyleName("listTable");
		documentFlexTable.getRowFormatter().addStyleName(0, "listHeader");
		documentFlexTable.getCellFormatter().addStyleName(0, 0, "tableButtonColumn");
		documentFlexTable.getCellFormatter().setStyleName(0, 2, "tableNumericColumn");
		documentFlexTable.getCellFormatter().addStyleName(0, 3, "tableNumericColumn");
		documentFlexTable.getCellFormatter().addStyleName(0, 4, "tableButtonColumn");
		
		return documentFlexTable;
	}
	
	private Button createEvaluateButton(final Document document) {
		Button button = new Button("Evaluate");
		button.setStyleName("evaluateButton");
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				loadingPopup.center();
				loadingPopup.show();

				Scheduler scheduler = Scheduler.get();
				scheduler.scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						documentService.getDocument(document.getName(), new AsyncCallback<Document>() {
							public void onFailure(Throwable caught) {
								loadingPopup.cancel();
								Window.alert("Failed to retrieve document evaluation information from the server: " + caught.getMessage());
							}

							public void onSuccess(Document result) {
								loadingPopup.cancel();
								DocumentDialog dialog = new DocumentDialog(result);
								dialog.center();
								dialog.show();
							}			
						});
					}
				});
				
			}
		});
		
		return button;
	}
	
	private Button createRemoveButton(final String name) {
	    Button removeButton = new Button("x");
	    removeButton.setStyleName("removeButton");
	    removeButton.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			// Set up callback object
    			AsyncCallback<String> callback = new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						Window.alert("Attempt to delete document failed.\n" + caught.getMessage());
					}

					public void onSuccess(String name) {
		    			int removedIndex = indexOf(name);
		    			documents.remove(removedIndex);
		    			documentFlexTable.removeRow(removedIndex + 1);
					}
    			};
    			
    			documentService.deleteDocument(name, callback);
    		}
	    });
	    
	    return removeButton;
	}
	
	private Button createUploadButton() {
		uploadButton.setEnabled(false);
		uploadButton.setStyleName("uploadButton");
		
		// Handle the event when a user clicks on the submit button
		uploadButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
    			DOM.setStyleAttribute(element, "cursor", "wait");
    			uploadingPopup.center();
    			uploadingPopup.show();
				uploadFormPanel.submit();
			}
		});
		
		return uploadButton;
	}
	
	private FileUpload createUploadElement() {
		uploadElement.setName("uploadFormElement");

		// Handle the event when the user chooses a file.
		uploadElement.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				String fileName = uploadElement.getFilename();
				boolean valid = !(null == fileName || fileName.trim().length() == 0);
				
				uploadButton.setEnabled(false);

				if (valid) {
					if (contains(fileName)) {
						Window.alert("A document with that name already exists on the server");
					} else if (!(fileName.toLowerCase().endsWith(".pdf") || fileName.toLowerCase().endsWith(".docx"))) {
						Window.alert("Currently, only PDF and OpenXML (.docx) files are accepted.");
					} else {
						uploadButton.setEnabled(true);
					}
				}

				event.preventDefault();
			}
		});
		
		return uploadElement;
	}
	
	private FormPanel createUploadFormPanel() {
		uploadFormPanel.setAction(GWT.getModuleBaseURL() + "uploadFile");
		uploadFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadFormPanel.setMethod(FormPanel.METHOD_POST);
		uploadFormPanel.addStyleName("uploadPanel");

		// Create a panel to hold the form elements.
		HorizontalPanel panel = new HorizontalPanel();
		uploadFormPanel.setWidget(panel);
		
		// Configure the FileUpload widget and add it to the panel.
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(new Label("Upload a document (PDF only):"));
		hPanel.add(createUploadElement());
		panel.add(hPanel);
		
		// Create a submit button and add it to the panel.
		panel.add(createUploadButton());
		
		// Handle the submit event
		uploadFormPanel.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(FormPanel.SubmitEvent event) {
				String fileName = uploadElement.getFilename();
				
				if (null == fileName || fileName.trim().length() == 0) {
					Window.alert("You must select a file to upload");
					event.cancel();
				}				
			}
		});
		
		// Handle the submit complete event
		uploadFormPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
				String result = event.getResults();
				
				if ("NO-SCRIPT-DATA".equalsIgnoreCase(result)) {
					Window.alert("There is an error in communication with the server");
				} else if ("NO-FILE-DATA".equalsIgnoreCase(result)) {
					Window.alert("The uploaded file is empty");
				} else if ("NO-FILE-NAME".equalsIgnoreCase(result)) {
					Window.alert("The file selected for upload has no name");
				} else if ("DOCUMENT-ALREADY-EXISTS".equalsIgnoreCase(result)) {
					Window.alert("There is already a document uploaded with that name");
				} else if ("SAVE-FILE-FAILED".equalsIgnoreCase(result)) {
					Window.alert("An error occurred on the server while trying to save the file");
				} else {
					// If there were no errors, the result should be the name of the uploaded file
				}
				
				uploadingPopup.cancel();
    			DOM.setStyleAttribute(element, "cursor", originalCursor);
    			uploadButton.setEnabled(false);
    			refreshDocumentList();
			}
		});
		
		return uploadFormPanel;
	}
	
	private int indexOf(String fileName) {
		for (int i = 0; i < documents.size(); i++) {
			Document document = documents.get(i);
			if (document.getName().equals(fileName))
				return i;
		}
		return -1;
	}
	
	private void refreshDocumentList() {
		// Set up callback object
		AsyncCallback<List<Document>> callback = new AsyncCallback<List<Document>>() {

			public void onFailure(Throwable caught) {
				Window.alert("An error occurred while requesting the list of documents from the server.\n" +
						      caught.getMessage());
			}

			public void onSuccess(List<Document> result) {
				updateTable(result);
			}
		};
		
		documentService.getDocuments(callback);
	}
	
	private void updateTable(List<Document> documents) {
		for (Document document: documents) {
			updateTable(document);
		}
	}
	
	private void updateTable(Document document) {
		final String name = document.getName();
		
		if (contains(name))
			return;
		
		documents.add(document);
		int row = indexOf(name) + 1;
		
		// Add a button to launch Document dialog		
		documentFlexTable.setWidget(row, 0, createEvaluateButton(document));
		
		Label label = new Label(name);
		
		documentFlexTable.setWidget(row, 1, label);
		documentFlexTable.setText(row, 2, Long.toString(document.getSize()));
				
	    // Add a button to remove this document.
	    documentFlexTable.setWidget(row, 4, createRemoveButton(name));
	    
		documentFlexTable.getCellFormatter().addStyleName(row, 0, "tableButtonColumn");
		documentFlexTable.getCellFormatter().addStyleName(row, 2, "tableNumericColumn");
		documentFlexTable.getCellFormatter().addStyleName(row, 3, "tableNumericColumn");
		documentFlexTable.getCellFormatter().addStyleName(row, 4, "tableButtonColumn");
	}
}
