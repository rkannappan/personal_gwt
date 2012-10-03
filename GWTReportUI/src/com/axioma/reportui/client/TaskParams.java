package com.axioma.reportui.client;

import com.google.gwt.core.client.JavaScriptObject;

class TaskParams extends JavaScriptObject {                              // (1)
  // Overlay types always have protected, zero argument constructors.
  protected TaskParams() {}                                              // (2)

  // JSNI methods to get report task data.
  public final native String getPortfolio() /*-{ return this.portfolioName[0]; }-*/; // (3)
  public final native String getBenchmark() /*-{ return (this.benchmarkName != null ? this.benchmarkName[0] : "None"); }-*/;
  public final native String getRiskModel() /*-{ return (this.riskModelName != null ? this.riskModelName[0] : "None"); }-*/;
  public final native String getClassification() /*-{ return (this.classificationName != null ? this.classificationName[0] : "None"); }-*/;  
  public final native String getSamplingFrequency() /*-{ return (this.samplingFrequency != null ? this.samplingFrequency[0] : "None"); }-*/;
  public final native String getStartDate() /*-{ return (this.startDate != null ? this.startDate[0] : "None"); }-*/;
  public final native String getEndDate() /*-{ return (this.endDate != null ? this.endDate[0] : "None"); }-*/;  
}

