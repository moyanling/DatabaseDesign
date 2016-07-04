package org.mo39.fmbh.databasedesign.executor;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;


/**
 * To provide a supported command and run by {@link DatabaseDesign#runCmd()}, a executor class must:
 * <br>
 * &emsp;- implement Executable;<br>
 * &emsp;- Provide a public non-arg constructor.<br>
 * &emsp;- Be declared and configured properly in SupportedCmds.xml;<br>
 * <br>
 * Also, some annotations are provided to help declare some functions for efficiency such as
 * {@link IsReadOnly} or State check {@link RequiresActiveSchema}.
 * <p>
 * Viewable is also encouraged, but not necessary, to be implemented to provide a view on the result
 * of the execution. If it is urgent to show information in a execute method, it can be displayed
 * through methods in View class, but it is not encouraged to do so. If the executor implements
 * Viewable interface, the result of the execution will be displayed as soon as the execution
 * finishes.
 * <p>
 * Another way to show message is to throw checked Exceptions claimed by
 * {@link Executable#execute()} during the execution. The message of the exception will be caught
 * and displayed. In this case, the execution is considered interrupted by the exception and will
 * not show the result from Viewable interface.
 *
 *
 * @author Jihan Chen
 *
 */
public interface Executable {

  /**
   * This is a abstract method claimed by Executable interface. This method will be invoked by the
   * {@link DatabaseDesign#runCmd()} using reflection. The Executable instance which will be invoked
   * on is created by newInstance on executor class.
   *
   * @throws BadUsageException A bad usage exception for current command. Usually it indicates a
   *         <strong>supported</strong> command is invoked but it fails to be used correctly. Such
   *         as a command trys to create a new schema but is not following naming convention. See
   *         naming conventions in {@link NamingUtils}.
   * @throws IOException
   */
  public abstract void execute() throws DBExceptions, IOException;


  /**
   * Annotate on the execute method where the execution requires an active schema to operate. State
   * check in execute method is valid to omit provided this annotation.
   *
   * @author Jihan Chen
   *
   */
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface RequiresActiveSchema {

  }

  /**
   * //TODO Not implemented yet. Maybe useful for operating in a multi-threads env.<br>
   * This annotation indicates whether the execute method is read only to database, i.e archive. If
   * this annotation is not provided, the execute method would operate in a thread-safe manner. If
   * the method is read-only, multi read-only access can happen concurrently. Use this annotation on
   * execute method properly to improve the efficiency.
   *
   * @author Jihan Chen
   *
   */
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface IsReadOnly {

  }



}


