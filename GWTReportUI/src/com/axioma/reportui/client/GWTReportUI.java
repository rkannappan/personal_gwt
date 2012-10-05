package com.axioma.reportui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GWTReportUI implements EntryPoint {

  private static final int REFRESH_INTERVAL = 1000; // ms
  private VerticalPanel mainPanel = new VerticalPanel();
  private FlexTable raTasksFlexTable = new FlexTable();
  private FlexTable reportingTasksFlexTable = new FlexTable();
  private FlexTable eventsFlexTable = new FlexTable();
  private HorizontalPanel addPanel = new HorizontalPanel();
  private Button addStockButton = new Button("Add");
  private Label lastUpdatedLabel = new Label();
  private Set<String> raTaskNames = new HashSet<String>();
  private Set<String> reportTaskNames = new HashSet<String>();
  private Label errorMsgLabel = new Label();
  boolean taskRunning = false;
  
  private static final String REST_WS_URL = GWT.getModuleBaseURL() + "serverProxy?q=";
  
  private String allEventsQueueName;
  private static final String PROGRESS_EVENTS_QUEUE_NAME_PREFIX = "progressEvents";
  
  private static final int RISK_ANALYSIS = 1;
  private static final int REPORT = 2;

  /**
   * Entry point method.
   */
  public void onModuleLoad() {
	  raTasksFlexTable.setText(0, 0, "Task Name");
	  raTasksFlexTable.setText(0, 1, "Portfolio");
	  raTasksFlexTable.setText(0, 2, "Benchmark");
	  raTasksFlexTable.setText(0, 3, "Risk Model");
	  raTasksFlexTable.setText(0, 4, "Classification"); 	  
	  raTasksFlexTable.setText(0, 5, "Frequency");
	  raTasksFlexTable.setText(0, 6, "Start Date");
	  raTasksFlexTable.setText(0, 7, "End Date");
	  raTasksFlexTable.setText(0, 8, "Run");
	  
    // Create table for report tasks.
    reportingTasksFlexTable.setText(0, 0, "Task Name");
    reportingTasksFlexTable.setText(0, 1, "Portfolio");
    reportingTasksFlexTable.setText(0, 2, "Benchmark");
    reportingTasksFlexTable.setText(0, 3, "Risk Model");
    reportingTasksFlexTable.setText(0, 4, "Classification");    
    reportingTasksFlexTable.setText(0, 5, "Frequency");
    reportingTasksFlexTable.setText(0, 6, "Start Date");
    reportingTasksFlexTable.setText(0, 7, "End Date");
    reportingTasksFlexTable.setText(0, 8, "Run");
    
    eventsFlexTable.setText(0, 0, "Date");
    eventsFlexTable.setText(0, 1, "Type");
    eventsFlexTable.setText(0, 2, "Priority");
    eventsFlexTable.setText(0, 3, "Task Name");
    eventsFlexTable.setText(0, 4, "Command Name");    
    eventsFlexTable.setText(0, 5, "Message");
  

    // Add styles to elements in the stock list table.
    raTasksFlexTable.setCellPadding(6);
    raTasksFlexTable.getRowFormatter().addStyleName(0, "taskHeader");
    raTasksFlexTable.addStyleName("taskTable");    
    
    reportingTasksFlexTable.setCellPadding(6);
    reportingTasksFlexTable.getRowFormatter().addStyleName(0, "taskHeader");
    reportingTasksFlexTable.addStyleName("taskTable");
    
    eventsFlexTable.setCellPadding(6);
    eventsFlexTable.getRowFormatter().addStyleName(0, "taskHeader");
    eventsFlexTable.addStyleName("taskTable");
    
//    reportingTasksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
//    reportingTasksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
//    reportingTasksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

    // Assemble Add Stock panel.
//    addPanel.add(newSymbolTextBox);
//    addPanel.add(addStockButton);
//    addPanel.addStyleName("addPanel");
 // Assemble Main panel.
    errorMsgLabel.setStyleName("errorMessage");
    errorMsgLabel.setVisible(false);
    
    HTML raTasksLabel = new HTML();
    raTasksLabel.setHTML("<b>Risk Analysis Tasks</b>");
    
    HTML spaceLabel = new HTML();
    spaceLabel.setHTML("<br><br>");        
    
    HTML eventViewerLabel = new HTML();
    eventViewerLabel.setHTML("<b>Event Viewer</b>");  
    
    Button clearEventsButton = new Button("Clear Events");
    clearEventsButton.addStyleName("clearEventsButton");

    HTML reportTasksLabel = new HTML();
    reportTasksLabel.setHTML("<b>Report Tasks</b>");

    // Assemble Main panel.
    mainPanel.add(errorMsgLabel);
    mainPanel.add(raTasksLabel);
    mainPanel.add(new HTML("<br>"));
    mainPanel.add(raTasksFlexTable);
    mainPanel.add(spaceLabel);
    mainPanel.add(eventViewerLabel);
    mainPanel.add(new HTML("<br>"));
    mainPanel.add(clearEventsButton);
    mainPanel.add(new HTML("<br>"));
    mainPanel.add(eventsFlexTable);    
//    mainPanel.add(reportTasksLabel);
//    mainPanel.add(reportingTasksFlexTable);
    mainPanel.add(addPanel);
    mainPanel.add(lastUpdatedLabel);

    // Associate the Main panel with the HTML host page.
    RootPanel.get("tasks").add(mainPanel);
    
    this.refreshTasks();
    
    Date dt = new Date();
    this.allEventsQueueName = "allEvents" + dt.getTime();
    
    // Setup timer to refresh events automatically.
    Timer refreshTimer = new Timer() {
      @Override
      public void run() {
        refreshEvents();
      }
    };
    refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
    
    clearEventsButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
        	int totalRows = eventsFlexTable.getRowCount();
        	for (int row = 1; row<totalRows; row++) {
        		eventsFlexTable.removeRow(row);
        	}
        }
    });
  }
  
  private void refreshTasks() {
	  this.refreshTasks(RISK_ANALYSIS);
//	  this.refreshTasks(REPORT);
  }
  
  private void refreshTasks(final int taskType) {
	    String url = getUrlByTaskType(taskType);

	    this.sendRequestToServer(url, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
		          displayError("Couldn't retrieve JSON");
		        }

		        public void onResponseReceived(Request request, Response response) {
		          if (200 == response.getStatusCode()) {
		        	  System.out.println("Response from server: " + response.getText());
		            updateTable(asArrayOfTask(response.getText()), taskType);
		          } else {
		            displayError("Couldn't retrieve JSON (" + response.getStatusText()
		                + ")");
		          }
		        }
		 });
   }
  
  private void refreshEvents() {
	  final String url = REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/run/events?eventQueueName=" + allEventsQueueName;

	    this.sendRequestToServer(url, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
		          System.out.println("Couldn't retrieve JSON");
		        }

		        public void onResponseReceived(Request request, Response response) {
		          if (200 == response.getStatusCode()) {
		        	  String info = response.getText();
		        	  if (info != null & !info.trim().isEmpty()) {
		        		  updateEventsTable(info);
		        	  }
		          } else {
		            System.out.println("Couldn't retrieve JSON (" + response.getStatusText()
		                + ")");
		          }
		        }
		 });
  }  
  
  private void sendRequestToServer(final String url, final RequestCallback requestCallBack) {
	  final String encodedURL = URL.encode(url);
	  
		 // Send request to server and catch any errors.
	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, encodedURL);

	    try {
	      builder.sendRequest(null, requestCallBack);
	    } catch (RequestException e) {
	      displayError("Couldn't retrieve JSON");
	    }
  }
  
  private void showProgressBar(final int taskType, final String taskName, final String progressEventsQueueName) {	  
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Running task " + taskName);
		dialogBox.setAnimationEnabled(true);
		final HTML progressInfoLabel = new HTML();
		progressInfoLabel.setHTML("<br><br><br><br><br>");
		VerticalPanel dialogVPanel = new VerticalPanel();
//		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(progressInfoLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);	  
		dialogBox.center();
		closeButton.setFocus(true);
		
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		final String url = REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/run/events?eventQueueName=" + progressEventsQueueName;
	  
	  // Hack to wait for new task instance to be created
	    Timer waitTimer = new Timer() {
		      @Override
		      public void run() {
		      }
		    };
		    waitTimer.schedule(10000);
		  
	    Timer refreshTimer = new Timer() {
	      @Override
	      public void run() {
	    	  if (taskRunning) {
	    		  consumeProgressMessages(url, taskType, taskName, progressInfoLabel, 1);
	    	  } else {
	    		  consumeProgressMessages(url, taskType, taskName, progressInfoLabel, 10);
	    		  dialogBox.hide();	    		  
	    		  this.cancel();
	    	  }
	      }
	    };
	    refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
  }
  
  private void consumeProgressMessages(final String progressURL, final int taskType, final String taskName, final HTML progressInfoLabel, int num) {
	  for (int i = 0; i< num; i++) {
		  sendRequestToServer(progressURL, new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		        	progressInfoLabel.setHTML("<br><br>Error running task " + taskName + "<br><br><br>");
		        }
	
		        public void onResponseReceived(Request request, Response response) {
		          if (200 == response.getStatusCode()) {
		        	  String progressInfo = response.getText();
		        	  if (progressInfo != null & !progressInfo.trim().isEmpty()) {
		        		  progressInfoLabel.setHTML("<br><br>" + progressInfo + "<br><br><br>");
			        	  System.out.println("Progress info " + new Date() + progressInfo);
		        	  }
		          } else {
		        	  System.out.println("Response status code: " + response.getStatusCode());
		        	  progressInfoLabel.setHTML("<br><br>Error running task " + taskName + "<br><br><br>");
		          }
		        }
		      });
	  }
  }  

  private void updateTable(JsArray<Task> tasks, final int taskType) {
    for (int i = 0; i < tasks.length(); i++) {
      updateTable(tasks.get(i), getTableByTaskType(taskType), taskType);
    }

    // Display timestamp showing last refresh.
    lastUpdatedLabel.setText("Last update : "
        + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
    
 // Clear any errors.
    errorMsgLabel.setVisible(false);
  }
  
  private void updateEventsTable(final String info) {
	  String[] tokens = info.split(":::");
	  
    int row = eventsFlexTable.getRowCount();
    eventsFlexTable.setText(row, 0, cleanseData(tokens[0]));
    eventsFlexTable.setText(row, 1, cleanseData(tokens[1]));
    eventsFlexTable.setText(row, 2, cleanseData(tokens[2]));	  
    eventsFlexTable.setText(row, 3, cleanseData(tokens[3]));
    eventsFlexTable.setText(row, 4, cleanseData(tokens[4]));
    eventsFlexTable.setText(row, 5, cleanseData(tokens[5]));
  }
  
  private String cleanseData(final String input) {
	  if (input == null || input.equals("null")) {
		  return "";
	  }
	  
	  int index = input.lastIndexOf(".");
	  return input.substring(index + 1);
  }

  /**
   * Update a single row in the tasks table.
   *
   * @param task the task for a single row.
   */
  private void updateTable(final Task task, final FlexTable table, final int taskType) {
	 if (!isNewTask(task, taskType)) {
		 return;
	 }
	 
    int row = table.getRowCount();
    table.setText(row, 0, task.getTaskName());
    table.setText(row, 1, task.getParams().getPortfolio());
    table.setText(row, 2, task.getParams().getBenchmark());	  
    table.setText(row, 3, task.getParams().getRiskModel());
    table.setText(row, 4, task.getParams().getClassification());
    table.setText(row, 5, task.getParams().getSamplingFrequency());
    table.setText(row, 6, task.getParams().getStartDate());
    table.setText(row, 7, task.getParams().getEndDate());
    
    // Add a button to run the task.
    Button runTaskButton = new Button("<img border='0' src='images/RunTask.png'/>");
    runTaskButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
    	  // Clear any errors.
    	    errorMsgLabel.setVisible(false);
    	    
    	    final String progressEventsQueueName = PROGRESS_EVENTS_QUEUE_NAME_PREFIX + new Date().getTime();
    	  
    	  String url = REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/run/" + getTaskTypeName(taskType) + "/" + task.getTaskName() + "?allEventsQueueName=" + allEventsQueueName + "REPLACE_ME_WITH_AMPERSANDprogressEventsQueueName=" + progressEventsQueueName;
    	  System.out.println(url);
    	  sendRequestToServer(url, new RequestCallback() {
  	        public void onError(Request request, Throwable exception) {
  	        	taskRunning = false;
  	          displayError("Error running task " + task.getTaskName());
  	        }

  	        public void onResponseReceived(Request request, Response response) {
  	          if (200 == response.getStatusCode()) {
  	        	  System.out.println("Successfully ran task " + task.getTaskName());
  	        	taskRunning = false;
  	          } else {
  	        	taskRunning = false;
  	        	  System.out.println("Response status code: " + response.getStatusCode());
  	        	displayError("Error running task " + task.getTaskName());  	        	
  	          }
  	        }
  	      });
    	  taskRunning = true;
    	  showProgressBar(taskType, task.getTaskName(), progressEventsQueueName);
      }
    });
    table.setWidget(row, 8, runTaskButton);    
  }
  
  private boolean isNewTask(final Task task, final int taskType) {
	  final String taskName = task.getTaskName();
	  
	  if (taskType == RISK_ANALYSIS) {
		  if (raTaskNames.contains(taskName)) {
			  return false;
		  } else {
			  raTaskNames.add(taskName);
			  return true;
		  }
	  } else {
		  if (reportTaskNames.contains(taskName)) {
			  return false;
		  } else {
			  reportTaskNames.add(taskName);
			  return true;
		  }		  
	  }
  }
  
  private String getUrlByTaskType(final int taskType) {
	  String url = REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/";
	  if (taskType == RISK_ANALYSIS) {
		  url += "RISK_ANALYSIS";
	  } else {
		  url += "REPORT";
	  }
	  
	  return url;
  }
  
  private String getTaskTypeName(final int taskType) {
	  String taskTypeName = null;
	  if (taskType == RISK_ANALYSIS) {
		  taskTypeName = "RISK_ANALYSIS";
	  } else {
		  taskTypeName = "REPORT";
	  }
	  
	  return taskTypeName;
  }  
  
  private FlexTable getTableByTaskType(final int taskType) {
	  FlexTable table = null;
	  if (taskType == RISK_ANALYSIS) {
		  table = raTasksFlexTable;
	  } else {
		  table = reportingTasksFlexTable;
	  }
	  
	  return table;
  }  
  
  /**
   * Convert the string of JSON into JavaScript object.
   */
  private final native JsArray<Task> asArrayOfTask(String json) /*-{
    return eval(json);
  }-*/;  
  
  /**
   * If can't get JSON, display error message.
   * @param error
   */
  private void displayError(String error) {
    errorMsgLabel.setText("Error: " + error);
    errorMsgLabel.setVisible(true);
  }
}