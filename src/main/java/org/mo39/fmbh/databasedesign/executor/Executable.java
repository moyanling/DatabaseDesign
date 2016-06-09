package org.mo39.fmbh.databasedesign.executor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.SupportedCmds;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;


/**
 * To provide a supported command and run by {@link SupportedCmds#runCmd}, a executor class must:
 * <br>
 * - implement Executable;<br>
 * - Provide a public non-arg constructor.<br>
 * - Provide a operation annotation on execute method<br>
 * - Be declared and configured properly in SupportedCmds.xml;<br>
 * <p>
 * Viewable is also encouraged, but not necessary, to be implemented to provide a view on the result
 * of the execution. If it is urgent to show information in a execute method, it can be displayed
 * through methods in View class, but it is not encouraged to do so. If the executor implements
 * Viewable interface, the result of the execution will be displayed as soon as the execution
 * finishes.
 * <p>
 * Another way to show message is to throw checked Exception claimed by {@link Executable#execute}
 * during the execution. The message of the exception will be caught and displayed. In this case,
 * the execution is considered interrupted by the exception and will not show the result from
 * Viewable interface.
 *
 *
 * @author Jihan Chen
 *
 */
public interface Executable {

  /**
   * This is a abstract method claimed by Executable interface. This method will be invoked by the
   * {@link SupportedCmds#runCmd} using reflection. The Executable instance which will be invoked on
   * is created by newInstance on executor class.
   *
   * @throws BadUsageException A bad usage exception for current command. Usually it indicates a
   *         <strong>supported</strong> command is invoked but it fails to be used correctly. Such
   *         as a command trys to create a new schema but is not following naming convention. See
   *         naming conventions in {@link NamingUtils}.
   */
  public void execute() throws BadUsageException;

  /**
   * This annotation indicates the execute method is operating on a schema scope.
   *
   * @author Jihan Chen
   *
   */
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface SchemaOperation {

  }


  /**
   * This annotation indicates the execute method is operating on a table scope.
   *
   * @author Jihan Chen
   *
   */
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface TableOperation {

    public boolean requiresActiveSchema() default true;
  }

  /**
   * This annotation indicates the execute method is operating the record within a table.
   *
   * @author Jihan Chen
   *
   */
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface RecordOperation {

    public boolean requireActiveTable() default true;

    public boolean requireActiveSchema() default true;
  }

  /**
   * Exit the database
   *
   * @author Jihan Chen
   *
   */
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface ExitOperation {

  }

  /**
   * This has not been implemented yet. It a preparation for the operation in a multi-thread
   * environment. It will be a mendentary annotaion on execute method.
   * <p>
   * This annotation indicates whether the execute method will write to database, i.e archive. If
   * the method is read-only, it would execute normally. Otherwise, it would need to achieve the
   * lock on the database first to write to the database.
   *
   * @author Jihan Chen
   *
   */
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface IsReadOnly {
    public boolean value();
  }



}


