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
import java.util.Iterator;

public class GWTReportUI implements EntryPoint {

  private static final int REFRESH_INTERVAL = 5000; // ms
  private VerticalPanel mainPanel = new VerticalPanel();
  private FlexTable raTasksFlexTable = new FlexTable();
  private FlexTable reportingTasksFlexTable = new FlexTable();
  private HorizontalPanel addPanel = new HorizontalPanel();
  private Button addStockButton = new Button("Add");
  private Label lastUpdatedLabel = new Label();
  private ArrayList<String> tasks = new ArrayList<String>();
  private Label errorMsgLabel = new Label();
  
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
	  
    // Create table for report tasks.
    reportingTasksFlexTable.setText(0, 0, "Task Name");
    reportingTasksFlexTable.setText(0, 1, "Portfolio");
    reportingTasksFlexTable.setText(0, 2, "Benchmark");
    reportingTasksFlexTable.setText(0, 3, "Risk Model");
    reportingTasksFlexTable.setText(0, 4, "Classification");    

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

    mainPanel.add(errorMsgLabel);

    // Assemble Main panel.
    mainPanel.add(raTasksFlexTable);
    mainPanel.add(reportingTasksFlexTable);
    mainPanel.add(addPanel);
    mainPanel.add(lastUpdatedLabel);

    // Associate the Main panel with the HTML host page.
    RootPanel.get("tasks").add(mainPanel);

    // Setup timer to refresh list automatically.
    Timer refreshTimer = new Timer() {
      @Override
      public void run() {
        refreshTasks();
      }
    };
    refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

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

	    url = URL.encode(url);

	 // Send request to server and catch any errors.
	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

	    try {
	      builder.sendRequest(null, new RequestCallback() {
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
	    } catch (RequestException e) {
	      displayError("Couldn't retrieve JSON");
	    }
	  }  

  /**
   * Update the Price and Change fields all the rows in the stock table.
   *
   * @param tasks Stock data for all rows.
   */
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
   * Update a single row in the report tasks table.
   *
   * @param reportTask report task for a single row.
   */
  private void updateTable(Task reportTask, final FlexTable table, final int taskType) {
    int row = table.getRowCount();
    table.setText(row, 0, reportTask.getTaskName());
    table.setText(row, 1, reportTask.getParams().getPortfolio());
    table.setText(row, 2, reportTask.getParams().getBenchmark());	  
    table.setText(row, 3, reportTask.getParams().getRiskModel());
    if (taskType == REPORT) {
    	table.setText(row, 4, reportTask.getParams().getClassification());
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