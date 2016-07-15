package org.mo39.fmbh.databasedesign.framework;

import java.io.PrintStream;

public abstract class View {

  static PrintStream p = null;

  public static void newView(Viewable obj) {
    print(obj.getView());
  }

  public static void newView(String str) {
    print(str);
  }

  public static interface Viewable {
    String getView();
  }

  private static void print(Object obj) {
    if (p == null) {
      System.out.print(obj);
    } else {
      p.println(obj);
    }
  }


}
