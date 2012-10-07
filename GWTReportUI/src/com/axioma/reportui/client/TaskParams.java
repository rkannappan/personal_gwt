package com.axioma.reportui.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author rkannappan
 */
class TaskParams extends JavaScriptObject {                              // (1)
  // Overlay types always have protected, zero argument constructors.
  protected TaskParams() {}                                              // (2)

  // JSNI methods to get report task data.
   public final native String getPortfolio() /*-{
		return this.portfolioName[0];
   }-*/; // (3)

   public final native String getBenchmark() /*-{
		return (this.benchmarkName != null ? this.benchmarkName[0] : "None");
   }-*/;

   public final native String getRiskModel() /*-{
		return (this.riskModelName != null ? this.riskModelName[0] : "None");
   }-*/;

   public final native String getClassification() /*-{
		return (this.classificationName != null ? this.classificationName[0]
				: "None");
   }-*/;

   public final native String getFactorClassificationType() /*-{
		return (this.factorClassificationType != null ? this.factorClassificationType[0]
				: "None");
   }-*/;

   public final native String getMainAssetClassificationName() /*-{
		return (this.mainAssetClassificationName != null ? this.mainAssetClassificationName[0]
				: "None");
   }-*/;

   public final native String getSamplingFrequency() /*-{
		return (this.samplingFrequency != null ? this.samplingFrequency[0]
				: "None");
   }-*/;

   public final native String getReportingFrequency() /*-{
		return (this.reportingFrequency != null ? this.reportingFrequency[0]
				: "None");
   }-*/;

   public final native String getPreDefinedTimePeriod() /*-{
		return (this.preDefinedTimePeriod != null ? this.preDefinedTimePeriod[0]
				: "None");
   }-*/;

   public final native String getCustomStartDate() /*-{
		return (this.customStartDate != null ? this.customStartDate[0] : "None");
   }-*/;

   public final native String getStartDate() /*-{
		return (this.startDate != null ? this.startDate[0] : "None");
   }-*/;

   public final native String getMostRecentDate() /*-{
		return (this.mostRecentDate != null ? this.mostRecentDate[0] : "false");
   }-*/;

   public final native String getCustomEndDate() /*-{
		return (this.customEndDate != null ? this.customEndDate[0] : "None");
   }-*/;

   public final native String getEndDate() /*-{
		return (this.endDate != null ? this.endDate[0] : "None");
   }-*/;

   public final native String getReportOutputPath() /*-{
		return (this.reportOutputPath != null ? this.reportOutputPath[0]
				: "None");
   }-*/;
}