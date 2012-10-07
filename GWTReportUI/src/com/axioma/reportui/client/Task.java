package com.axioma.reportui.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author rkannappan
 */
class Task extends JavaScriptObject {
  // Overlay types always have protected, zero argument constructors.
   protected Task() {
   }

   // JSNI methods to get report task data.
   public final native String getTaskName() /*-{
		return this.name;
   }-*/;

   public final native TaskParams getParams() /*-{
		return this.params;
   }-*/;
}

