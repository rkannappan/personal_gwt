package com.axioma.reportui.client;

import com.google.gwt.core.client.JavaScriptObject;

class Task extends JavaScriptObject {                              // (1)
  // Overlay types always have protected, zero argument constructors.
  protected Task() {}                                              // (2)

  // JSNI methods to get report task data.
  public final native String getTaskName() /*-{ return this.name; }-*/;
  public final native TaskParams getParams() /*-{ return this.params; }-*/; // (3)
}

