package org.mo39.fmbh.databasedesign.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.MissingAnnotationException;

public abstract class View {

  public static void newView(Viewable obj) {
    try {
      if (obj.getClass().getMethod("getView").getAnnotation(CliView.class) != null) {
        print(obj.getView());
      } else {
        // TODO Other views can be implemented here.
        throw new MissingAnnotationException(
            "No annotation is found in " + obj.getClass().getName() + " on getView method.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void newView(String str) {
    print(str);
  }

  public static interface Viewable {

    String getView();

  }

  private static void print(Object obj) {
    System.out.print(obj);
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface CliView {

  }

}
