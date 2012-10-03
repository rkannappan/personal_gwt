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
import com.google.gwt.user.client.ui.FlexTable;
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

  private static final int REFRESH_INTERVAL = 30000; // ms
  private static final int PROGRESS_REFRESH_INTERVAL = 1000; // ms
  private VerticalPanel mainPanel = new VerticalPanel();
  private FlexTable raTasksFlexTable = new FlexTable();
  private FlexTable reportingTasksFlexTable = new FlexTable();
  private HorizontalPanel addPanel = new HorizontalPanel();
  private Button addStockButton = new Button("Add");
  private Label lastUpdatedLabel = new Label();
  private Set<String> raTaskNames = new HashSet<String>();
  private Set<String> reportTaskNames = new HashSet<String>();
  private Label errorMsgLabel = new Label();
  boolean taskRunning = false;
  
  private static final String REST_WS_URL = GWT.getModuleBaseURL() + "serverProxy?q=";
  
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

    // Add styles to elements in the stock list table.
    raTasksFlexTable.setCellPadding(6);
    raTasksFlexTable.getRowFormatter().addStyleName(0, "taskHeader");
    raTasksFlexTable.addStyleName("taskTable");    
    
    reportingTasksFlexTable.setCellPadding(6);
    reportingTasksFlexTable.getRowFormatter().addStyleName(0, "taskHeader");
    reportingTasksFlexTable.addStyleName("taskTable");
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
    
    Label raTasksLabel = new Label();
    raTasksLabel.setText("Risk Analysis Tasks");

    Label reportTasksLabel = new Label();
    reportTasksLabel.setText("Report Tasks");

    // Assemble Main panel.
    mainPanel.add(errorMsgLabel);
    mainPanel.add(raTasksLabel);
    mainPanel.add(raTasksFlexTable);
    mainPanel.add(reportTasksLabel);
    mainPanel.add(reportingTasksFlexTable);
    mainPanel.add(addPanel);
    mainPanel.add(lastUpdatedLabel);

    // Associate the Main panel with the HTML host page.
    RootPanel.get("tasks").add(mainPanel);
    
    this.refreshTasks();

    // Setup timer to refresh list automatically.
//    Timer refreshTimer = new Timer() {
//      @Override
//      public void run() {
//        refreshTasks();
//      }
//    };
//    refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

//    // Listen for mouse events on the Add button.
//    addStockButton.addClickHandler(new ClickHandler() {
//      public void onClick(ClickEvent event) {
//        addStock();
//      }
//    });
//
//    // Listen for keyboard events in the input box.
//    newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
//      public void onKeyPress(KeyPressEvent event) {
//        if (event.getCharCode() == KeyCodes.KEY_ENTER) {
//          addStock();
//        }
//      }
//    });
  }

//  /**
//   * Add stock to FlexTable. Executed when the user clicks the addStockButton or
//   * presses enter in the newSymbolTextBox.
//   */
//  private void addStock() {
//    final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
//    newSymbolTextBox.setFocus(true);
//
//    // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
//    if (!symbol.matches("^[0-9a-zA-Z\\.]{1,10}$")) {
//      Window.alert("'" + symbol + "' is not a valid symbol.");
//      newSymbolTextBox.selectAll();
//      return;
//    }
//
//    newSymbolTextBox.setText("");
//
//    // Don't add the stock if it's already in the table.
//    if (tasks.contains(symbol))
//      return;
//
//    // Add the stock to the table.
//    int row = reportingTasksFlexTable.getRowCount();
//    tasks.add(symbol);
//    reportingTasksFlexTable.setText(row, 0, symbol);
//    reportingTasksFlexTable.setWidget(row, 2, new Label());
//    reportingTasksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
//    reportingTasksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
//    reportingTasksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
//
//    // Add a button to remove this stock from the table.
//    Button removeStockButton = new Button("x");
//    removeStockButton.addStyleDependentName("remove");
//    removeStockButton.addClickHandler(new ClickHandler() {
//      public void onClick(ClickEvent event) {
//        int removedIndex = tasks.indexOf(symbol);
//        tasks.remove(removedIndex);
//        reportingTasksFlexTable.removeRow(removedIndex + 1);
//      }
//    });
//    reportingTasksFlexTable.setWidget(row, 3, removeStockButton);
//
//    // Get the stock price.
//    refreshTasks();
//
//  }

//  /**
//   * Generate random stock prices.
//   */
//  private void refreshWatchList() {
//    final double MAX_PRICE = 100.0; // $100.00
//    final double MAX_PRICE_CHANGE = 0.02; // +/- 2%
//
//    StockPrice[] prices = new StockPrice[stocks.size()];
//    for (int i = 0; i < stocks.size(); i++) {
//      double price = Random.nextDouble() * MAX_PRICE;
//      double change = price * MAX_PRICE_CHANGE
//          * (Random.nextDouble() * 2.0 - 1.0);
//
//      prices[i] = new StockPrice(stocks.get(i), price, change);
//    }
//
//    updateTable(prices);
//  }
  
  private void refreshTasks() {
	  this.refreshTasks(RISK_ANALYSIS);
	  this.refreshTasks(REPORT);
  }
  
  private void refreshTasks(final int taskType) {
	    String url = getUrlByTaskType(taskType);

	    this.sendRequestToServer(url, taskType, new RequestCallback() {
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
  
  private void sendRequestToServer(final String url, final int taskType, final RequestCallback requestCallBack) {
	  final String encodedURL = URL.encode(url);
	  
		 // Send request to server and catch any errors.
	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, encodedURL);

	    try {
	      builder.sendRequest(null, requestCallBack);
	    } catch (RequestException e) {
	      displayError("Couldn't retrieve JSON");
	    }
  }
  
  private void showProgressBar(final int taskType, final String taskName) {	  
	  final String progressURL = REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/progress/" + getTaskTypeName(taskType) + "/" + taskName;
	  
	    Timer waitTimer = new Timer() {
		      @Override
		      public void run() {
		      }
		    };
		    waitTimer.schedule(5000);
		  
	    Timer refreshTimer = new Timer() {
	      @Override
	      public void run() {
	    	  if (taskRunning) {
	    		  getProgress(progressURL, taskType, taskName);
	    	  } else {
	    		  this.cancel();
	    	  }
	      }
	    };
	    refreshTimer.scheduleRepeating(PROGRESS_REFRESH_INTERVAL);
  }
  
  private void getProgress(final String url, final int taskType, final String taskName) {
	  sendRequestToServer(url, taskType, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
	          displayError("Error running task " + taskName);
	        }

	        public void onResponseReceived(Request request, Response response) {
	          if (200 == response.getStatusCode()) {
	        	  System.out.println("Progress info " + new Date() + response.getText());
	          } else {
	        	  System.out.println("Response status code: " + response.getStatusCode());
	        	displayError("Error running task " + taskName);
	          }
	        }
	      });
  }
  
  private void consumeAllProgressMessages(final String url, final int taskType, final String taskName) {
	  sendRequestToServer(url, taskType, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
	          displayError("Error running task " + taskName);
	        }

	        public void onResponseReceived(Request request, Response response) {
	          if (200 == response.getStatusCode()) {
	        	  System.out.println("Progress info " + new Date() + response.getText());
	          } else {
	        	  System.out.println("Response status code: " + response.getStatusCode());
	        	displayError("Error running task " + taskName);
	          }
	        }
	      });
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
    	  String url = REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/run/" + getTaskTypeName(taskType) + "/" + task.getTaskName();
    	  sendRequestToServer(url, taskType, new RequestCallback() {
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
    	  showProgressBar(taskType, task.getTaskName());
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