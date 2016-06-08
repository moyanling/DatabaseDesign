package org.mo39.fmbh.databasedesign.executor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;

public interface Executable {

  public void execute() throws BadUsageException;

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

  @Retention(RetentionPolicy.RUNTIME)
  public static @interface ReadOnly {
    boolean isReadyOnly();
  }
}
