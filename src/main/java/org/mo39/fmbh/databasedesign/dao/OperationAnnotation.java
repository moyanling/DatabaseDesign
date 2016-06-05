package org.mo39.fmbh.databasedesign.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class OperationAnnotation {

  @Retention(RetentionPolicy.RUNTIME)
  public static @interface SchemaOperation {

  }


  @Retention(RetentionPolicy.RUNTIME)
  public static @interface TableOperation {
    boolean requiresActiveSchema() default true;
  }


  @Retention(RetentionPolicy.RUNTIME)
  public static @interface SqlOperation {
    boolean requireActiveTable() default true;

    boolean requireActiveSchema() default true;
  }


  @Retention(RetentionPolicy.RUNTIME)
  public static @interface ExitOperation {

  }
}


