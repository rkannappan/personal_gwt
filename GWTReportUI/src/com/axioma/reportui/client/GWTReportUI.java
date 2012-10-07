package com.axioma.reportui.client;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author rkannappan
 */
public class GWTReportUI implements EntryPoint {

   private static final String REST_WS_URL = GWT.getModuleBaseURL() + "serverProxy?q=";

   private static final String PROGRESS_EVENTS_QUEUE_NAME_PREFIX = "progressEvents";

   private static final int RISK_ANALYSIS = 1;
   private static final int PERFORMANCE_ATTRIBUTION_RETURNS = 2;
   private static final int PERFORMANCE_ATTRIBUTION_FACTOR = 3;
   private static final int REPORT = 4;

   private static final int REFRESH_INTERVAL = 1000; // ms
   private final FlexTable raTasksFlexTable = new FlexTable();
   private final FlexTable returnsPATasksFlexTable = new FlexTable();
   private final FlexTable factorPATasksFlexTable = new FlexTable();
   private final FlexTable reportingTasksFlexTable = new FlexTable();
   private final FlexTable eventsFlexTable = new FlexTable();
   private final Set<String> raTaskNames = new HashSet<String>();
   private final Set<String> returnsPATaskNames = new HashSet<String>();
   private final Set<String> factorPATaskNames = new HashSet<String>();
   private final Set<String> reportTaskNames = new HashSet<String>();
   private boolean taskRunning = false;
   private String allEventsQueueName;
  
   /**
    * Entry point method.
    */
   @Override
   public void onModuleLoad() {
      this.createTasksTable(this.raTasksFlexTable);
      this.createTasksTable(this.returnsPATasksFlexTable);
      this.createTasksTable(this.factorPATasksFlexTable);
      this.createTasksTable(this.reportingTasksFlexTable);
      this.createEventsTable();
	      
      this.applyStyleToTable(this.raTasksFlexTable);
      this.applyStyleToTable(this.returnsPATasksFlexTable);
      this.applyStyleToTable(this.factorPATasksFlexTable);
      this.applyStyleToTable(this.reportingTasksFlexTable);
      this.applyStyleToTable(this.eventsFlexTable);
    
      //    this.errorMsgLabel.setStyleName("errorMessage");
      //    this.errorMsgLabel.setVisible(false);
    
      VerticalPanel header = new VerticalPanel();
      header.add(new HTML("<br>"));
      Image logo = new Image("images/logo.gif");
      header.add(logo);
      header.add(new HTML("<br>"));
    
      VerticalPanel raTasksPanel = this.createTasksPanel(this.raTasksFlexTable);
      VerticalPanel returnsPATasksPanel = this.createTasksPanel(this.returnsPATasksFlexTable);
      VerticalPanel factorPATasksPanel = this.createTasksPanel(this.factorPATasksFlexTable);
      VerticalPanel reportTasksPanel = this.createTasksPanel(this.reportingTasksFlexTable);

      Button clearEventsButton = this.createEventsButton();
      VerticalPanel eventViewerPanel = new VerticalPanel();
      eventViewerPanel.add(new HTML("<br>"));
      eventViewerPanel.add(clearEventsButton);
      eventViewerPanel.add(new HTML("<br>"));
      eventViewerPanel.add(this.eventsFlexTable);
      eventViewerPanel.add(new HTML("<br>"));
    
      TabLayoutPanel tabPanel = new TabLayoutPanel(25, Style.Unit.PX);
      tabPanel.add(new ScrollPanel(raTasksPanel), "Risk Analysis Tasks");
      tabPanel.add(new ScrollPanel(returnsPATasksPanel), "Returns PA Tasks");
      tabPanel.add(new ScrollPanel(factorPATasksPanel), "Factor PA Tasks");
      tabPanel.add(new ScrollPanel(reportTasksPanel), "Report Tasks");
      tabPanel.add(new ScrollPanel(eventViewerPanel), "Event Viewer");
    
      DockLayoutPanel dockPanel = new DockLayoutPanel(Style.Unit.PX);
      dockPanel.addNorth(header, 125);
      dockPanel.add(tabPanel);

      // Associate the Main panel with the HTML host page.
      RootLayoutPanel.get().add(dockPanel);
    
      Date dt = new Date();
      this.allEventsQueueName = "allEvents" + dt.getTime();

      this.refreshTasks();
    
      // Setup timer to refresh events automatically.
      Timer refreshTimer = new Timer() {
         @Override
         public void run() {
            GWTReportUI.this.refreshEvents();
         }
      };
      refreshTimer.scheduleRepeating(GWTReportUI.REFRESH_INTERVAL);
   }
  
   private void createTasksTable(final FlexTable tasksTable) {
      tasksTable.setText(0, 0, "Task Name");
      tasksTable.setText(0, 1, "Portfolio");
      tasksTable.setText(0, 2, "Benchmark");
      tasksTable.setText(0, 3, "Risk Model");
      tasksTable.setText(0, 4, "Classification");
      tasksTable.setText(0, 5, "Frequency");
      tasksTable.setText(0, 6, "Start Date");
      tasksTable.setText(0, 7, "End Date");
      tasksTable.setText(0, 8, "Run");
   }
  
   private void createEventsTable() {
      this.eventsFlexTable.setText(0, 0, "Date");
      this.eventsFlexTable.setText(0, 1, "Type");
      this.eventsFlexTable.setText(0, 2, "Priority");
      this.eventsFlexTable.setText(0, 3, "Task Name");
      this.eventsFlexTable.setText(0, 4, "Command Name");
      this.eventsFlexTable.setText(0, 5, "Message");
   }

   private void applyStyleToTable(final FlexTable table) {
      table.setCellPadding(6);
      table.getRowFormatter().addStyleName(0, "taskHeader");
      table.addStyleName("taskTable");
   }

   private Button createEventsButton() {
      Button clearEventsButton = new Button("Clear Events");
      clearEventsButton.addStyleName("clearEventsButton");
      clearEventsButton.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(final ClickEvent event) {
            int count = GWTReportUI.this.eventsFlexTable.getRowCount();
            // First row is header. Don't remove it.
            while (count > 1) {
               GWTReportUI.this.eventsFlexTable.removeRow(1);
            }
         }
      });

      return clearEventsButton;
   }
  
   private VerticalPanel createTasksPanel(final FlexTable table) {
      VerticalPanel taskPanel = new VerticalPanel();
	  
      HTML spaceLabel = new HTML("<br>");
      taskPanel.add(spaceLabel);
      taskPanel.add(table);
      taskPanel.add(spaceLabel);
	  
      return taskPanel;
   }
  
   private void refreshTasks() {
      this.refreshTasks(GWTReportUI.RISK_ANALYSIS);
      this.refreshTasks(GWTReportUI.PERFORMANCE_ATTRIBUTION_RETURNS);
      this.refreshTasks(GWTReportUI.PERFORMANCE_ATTRIBUTION_FACTOR);
      this.refreshTasks(GWTReportUI.REPORT);
   }
  
   private void refreshTasks(final int taskType) {
	    String url = this.getUrlByTaskType(taskType);

	    this.sendRequestToServer(url, new RequestCallback() {
         @Override
         public void onError(final Request request, final Throwable exception) {
            GWTReportUI.this.displayError("Couldn't retrieve task info");
         }

         @Override
         public void onResponseReceived(final Request request, final Response response) {
            if (200 == response.getStatusCode()) {
               System.out.println("Response from server: " + response.getText());
               GWTReportUI.this.updateTasksTable(GWTReportUI.this.asArrayOfTask(response.getText()), taskType);
            } else {
               GWTReportUI.this.displayError("Couldn't retrieve task info (" + response.getStatusText()
		                + ")");
            }
         }
		 });
   }
  
  private void refreshEvents() {
	  final String url = GWTReportUI.REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/run/events?eventQueueName=" + this.allEventsQueueName;

      this.sendRequestToServer(url, new RequestCallback() {
         @Override
         public void onError(final Request request, final Throwable exception) {
            System.out.println("Couldn't retrieve event info");
         }

         @Override
         public void onResponseReceived(final Request request, final Response response) {
            if (200 == response.getStatusCode()) {
               String info = response.getText();
               if ((info != null) & !info.trim().isEmpty()) {
                  GWTReportUI.this.updateEventsTable(info);
               }
            } else {
               System.out.println("Couldn't retrieve event info (" + response.getStatusText() + ")");
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
	      this.displayError("Couldn't retrieve JSON");
	    }
   }
  
  private void updateEventsTable(final String info) {
	  String[] tokens = info.split(":::");
	  
    int row = this.eventsFlexTable.getRowCount();
    this.eventsFlexTable.setText(row, 0, this.cleanseData(tokens[0]));
    this.eventsFlexTable.setText(row, 1, this.cleanseData(tokens[1]));
    this.eventsFlexTable.setText(row, 2, this.cleanseData(tokens[2]));	  
    this.eventsFlexTable.setText(row, 3, this.cleanseData(tokens[3]));
    this.eventsFlexTable.setText(row, 4, this.cleanseData(tokens[4]));
    // This is not cleansed on purpose as message would have . in the middle.
    this.eventsFlexTable.setText(row, 5, tokens[5]);
  }
  
  private String cleanseData(final String input) {
	  String cleansedInput = input;
	  
	  if ((input == null) || input.equals("null")) {
		  return "";
      }

      cleansedInput = cleansedInput.replaceAll("\"", "");
      
      cleansedInput = cleansedInput.replaceAll("\\u0026", "&");
      cleansedInput = cleansedInput.replaceAll("\\u0027", "'");
      cleansedInput = cleansedInput.replaceAll("\\u003d", "=");
	  
	  int index = input.lastIndexOf(".");
	  return input.substring(index + 1);
  }

   private void updateTasksTable(final JsArray<Task> tasks, final int taskType) {
      for (int i = 0; i < tasks.length(); i++) {
         this.updateTasksTable(tasks.get(i), this.getTableByTaskType(taskType), taskType);
      }

      //    // Display timestamp showing last refresh.
      //    this.lastUpdatedLabel.setText("Last update : "
      //        + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
      //    
      // // Clear any errors.
      //    this.errorMsgLabel.setVisible(false);
   }

   /**
    * Update a single row in the tasks table.
    *
    * @param task the task for a single row.
    */
   private void updateTasksTable(final Task task, final FlexTable table, final int taskType) {
      if (!this.isNewTask(task, taskType)) {
         return;
      }
	 
      int row = table.getRowCount();
      table.setText(row, 0, task.getTaskName());
      table.setText(row, 1, task.getParams().getPortfolio());
      table.setText(row, 2, task.getParams().getBenchmark());
      table.setText(row, 3, task.getParams().getRiskModel());
      table.setText(row, 4, this.getClassification(task, taskType));
      table.setText(row, 5, this.getFrequency(task, taskType));
      table.setText(row, 6, this.getStartDate(task, taskType));
      table.setText(row, 7, this.getEndDate(task, taskType));
    
      if (taskType == GWTReportUI.REPORT) {
         for (int col = 0; col < 8; col++) {
            DOM.setElementAttribute(table.getFlexCellFormatter().getElement(row, col), "title", task.getParams()
                     .getReportOutputPath());
         }
      }
    
      // Add a button to run the task.
      Button runTaskButton = new Button("<img border='0' src='images/RunTask.png'/>");
      runTaskButton.addClickHandler(new ClickHandler() {
         @Override
         public void onClick(final ClickEvent event) {
            // Clear any errors.
            //    	    GWTReportUI.this.errorMsgLabel.setVisible(false);
    	    
            final String progressEventsQueueName = GWTReportUI.PROGRESS_EVENTS_QUEUE_NAME_PREFIX + new Date().getTime();
    	  
            String url =
                     GWTReportUI.REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/run/"
                              + GWTReportUI.this.getTaskTypeName(taskType) + "/" + URL.encode(task.getTaskName())
                              + "?allEventsQueueName="
                              + GWTReportUI.this.allEventsQueueName + "REPLACE_ME_WITH_AMPERSANDprogressEventsQueueName="
                              + progressEventsQueueName;
            GWTReportUI.this.sendRequestToServer(url, new RequestCallback() {
               @Override
               public void onError(final Request request, final Throwable exception) {
                  GWTReportUI.this.taskRunning = false;
                  GWTReportUI.this.displayError("Error running task " + task.getTaskName());
               }

               @Override
               public void onResponseReceived(final Request request, final Response response) {
                  if (200 == response.getStatusCode()) {
                     System.out.println("Successfully ran task " + task.getTaskName());
                     GWTReportUI.this.taskRunning = false;
                  } else {
                     GWTReportUI.this.taskRunning = false;
                     System.out.println("Response status code: " + response.getStatusCode());
                     GWTReportUI.this.displayError("Error running task " + task.getTaskName());
                  }
               }
            });
            GWTReportUI.this.taskRunning = true;
            GWTReportUI.this.showProgressDialog(taskType, task.getTaskName(), progressEventsQueueName);
         }
      });
      table.setWidget(row, 8, runTaskButton);
   }
  
   private void showProgressDialog(final int taskType, final String taskName, final String progressEventsQueueName) {
      final DialogBox dialogBox = new DialogBox(false, false);
      dialogBox.setText("Running task " + taskName);
      dialogBox.setAnimationEnabled(true);
      final HTML progressInfoLabel = new HTML();
      progressInfoLabel.setHTML("<br><br>Initializing task...<br><br><br>");
      VerticalPanel dialogVPanel = new VerticalPanel();
      dialogVPanel.addStyleName("dialogVPanel");
      dialogVPanel.add(progressInfoLabel);
      dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
      dialogBox.add(dialogVPanel);
      dialogBox.center();

      ((HTML) dialogBox.getCaption()).addClickHandler(new ClickHandler() {
         @Override
         public void onClick(final ClickEvent event) {
            dialogBox.hide();
         }
      });

      final String url =
               GWTReportUI.REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/run/events?eventQueueName="
                        + progressEventsQueueName;

      Timer refreshTimer = new Timer() {
         @Override
         public void run() {
            if (GWTReportUI.this.taskRunning) {
               GWTReportUI.this.consumeProgressMessages(url, taskType, taskName, progressInfoLabel, 1);
            } else {
               GWTReportUI.this.consumeProgressMessages(url, taskType, taskName, progressInfoLabel, 10);
               dialogBox.hide();
               this.cancel();
            }
         }
      };
      refreshTimer.scheduleRepeating(GWTReportUI.REFRESH_INTERVAL);
   }

   private void consumeProgressMessages(final String progressURL, final int taskType, final String taskName,
            final HTML progressInfoLabel, final int num) {
      for (int i = 0; i < num; i++) {
         this.sendRequestToServer(progressURL, new RequestCallback() {
            @Override
            public void onError(final Request request, final Throwable exception) {
               progressInfoLabel.setHTML("<br><br>Error running task " + taskName + "<br><br><br>");
            }

            @Override
            public void onResponseReceived(final Request request, final Response response) {
               if (200 == response.getStatusCode()) {
                  String progressInfo = response.getText();
                  if ((progressInfo != null) & !progressInfo.trim().isEmpty()) {
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

   private String getUrlByTaskType(final int taskType) {
      String url = GWTReportUI.REST_WS_URL + "http://localhost:8080/DataControllerWebServices/TaskService/";
      if (taskType == GWTReportUI.RISK_ANALYSIS) {
         url += "RISK_ANALYSIS";
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_RETURNS) {
         url += "PERFORMANCE_ATTRIBUTION_RETURNS";
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_FACTOR) {
         url += "PERFORMANCE_ATTRIBUTION_FACTOR";
      } else if (taskType == GWTReportUI.REPORT) {
         url += "REPORT";
      }

      return url;
   }

   private String getTaskTypeName(final int taskType) {
      String taskTypeName = null;
      if (taskType == GWTReportUI.RISK_ANALYSIS) {
         taskTypeName = "RISK_ANALYSIS";
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_RETURNS) {
         taskTypeName = "PERFORMANCE_ATTRIBUTION_RETURNS";
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_FACTOR) {
         taskTypeName = "PERFORMANCE_ATTRIBUTION_FACTOR";
      } else if (taskType == GWTReportUI.REPORT) {
         taskTypeName = "REPORT";
      }

      return taskTypeName;
   }

   private FlexTable getTableByTaskType(final int taskType) {
      FlexTable table = null;
      if (taskType == GWTReportUI.RISK_ANALYSIS) {
         table = this.raTasksFlexTable;
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_RETURNS) {
         table = this.returnsPATasksFlexTable;
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_FACTOR) {
         table = this.factorPATasksFlexTable;
      } else if (taskType == GWTReportUI.REPORT) {
         table = this.reportingTasksFlexTable;
      }

      return table;
   }

   // Helper Methods

   private boolean isNewTask(final Task task, final int taskType) {
      final String taskName = task.getTaskName();
	  
      if (taskType == GWTReportUI.RISK_ANALYSIS) {
         if (this.raTaskNames.contains(taskName)) {
            return false;
         } else {
            this.raTaskNames.add(taskName);
            return true;
         }
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_RETURNS) {
         if (this.returnsPATaskNames.contains(taskName)) {
            return false;
         } else {
            this.returnsPATaskNames.add(taskName);
            return true;
         }
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_FACTOR) {
         if (this.factorPATaskNames.contains(taskName)) {
            return false;
         } else {
            this.factorPATaskNames.add(taskName);
            return true;
         }
      } else if (taskType == GWTReportUI.REPORT) {
         if (this.reportTaskNames.contains(taskName)) {
            return false;
         } else {
            this.reportTaskNames.add(taskName);
            return true;
         }
      }

      return false;
   }
  
   private String getClassification(final Task task, final int taskType) {
      String classification = null;
      if (taskType == GWTReportUI.RISK_ANALYSIS) {
         classification = task.getParams().getClassification();
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_RETURNS) {
         classification = task.getParams().getMainAssetClassificationName();
      } else if (taskType == GWTReportUI.PERFORMANCE_ATTRIBUTION_FACTOR) {
         classification = task.getParams().getFactorClassificationType();
      } else if (taskType == GWTReportUI.REPORT) {
         classification = task.getParams().getClassification();
      }

      return classification;
   }

   private String getFrequency(final Task task, final int taskType) {
      String frequency = null;
      if (taskType == GWTReportUI.REPORT) {
         frequency = task.getParams().getReportingFrequency();
      } else {
         frequency = task.getParams().getSamplingFrequency();
      }

      return frequency;
   }

   private String getStartDate(final Task task, final int taskType) {
      String startDate = null;

      if (taskType == GWTReportUI.REPORT) {
         if (!task.getParams().getPreDefinedTimePeriod().equals("None")) {
            startDate = task.getParams().getPreDefinedTimePeriod();
         } else if (!task.getParams().getCustomStartDate().equals("None")) {
            startDate = task.getParams().getCustomStartDate();
         }
      }

      if (startDate == null) {
         startDate = task.getParams().getStartDate();
      }

      return startDate;
   }

   private String getEndDate(final Task task, final int taskType) {
      String endDate = null;

      Boolean mostRecentDate = Boolean.valueOf(task.getParams().getMostRecentDate());
      if (mostRecentDate.equals(true)) {
         endDate = "Most Recent Date";
      }

      if ((taskType == GWTReportUI.REPORT) && !task.getParams().getCustomEndDate().equals("None")) {
         endDate = task.getParams().getCustomEndDate();
      }

      if (endDate == null) {
         endDate = task.getParams().getEndDate();
      }

      return endDate;
   }
  
   /**
    * Convert the string of JSON into JavaScript object.
    */
   private final native JsArray<Task> asArrayOfTask(String json) /*-{
		return eval(json);
   }-*/;

   private void displayError(final String error) {
      //    this.errorMsgLabel.setText("Error: " + error);
      //    this.errorMsgLabel.setVisible(true);
   }
}