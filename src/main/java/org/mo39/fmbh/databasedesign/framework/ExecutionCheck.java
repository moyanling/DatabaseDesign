package org.mo39.fmbh.databasedesign.framework;

import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.executor.Executable.RequiresActiveSchema;
import org.mo39.fmbh.databasedesign.executor.Executable.RequiresConfirm;

/**
 * Collects PreExecutionCheck and PostExecutionCheck.
 *
 * @author Jihan Chen
 *
 */
public abstract class ExecutionCheck {

  /**
   * Check before the execution.
   *
   * @author Jihan Chen
   *
   */
  public static class PreExecutionCheck {

    /**
     * Checks:<br>
     * &emsp;- annotation<br>
     * before execution.
     *
     * @param method
     * @return True if pass. False if fail.
     */
    public static boolean check(Method method) {
      checkAnnotation(method);
      if (Status.executionShouldStop()) {
        if (Status.isAborted) {
          View.newView("Abort operation. ");
        }
        if (Status.isIllegalState) {
          View.newView("Illegal state. ");
        }
        Status.clearInternalFlag();
        return false;
      }
      return true;
    }

    private static void checkAnnotation(Method method) {
      if (method.getAnnotation(RequiresActiveSchema.class) != null) {
        if (!Status.hasActiveSchema()) {
          Status.isIllegalState = true;
        }
      }
      if (method.getAnnotation(RequiresConfirm.class) != null) {
        View.newView("Are you sure to " + Status.getCurrentCmd().getName() + " ? (y/n)");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        scan.close();
        Pattern regx = Pattern.compile("^\\s.*?y\\s.*?$", Pattern.CASE_INSENSITIVE);
        if (!regx.matcher(input).matches()) {
          Status.isAborted = true;
        }
      }
    }

  }

  /**
   * Not implemented.
   *
   * @author Jihan Chen
   *
   */
  public static class PostExecutionCheck {

    /**
     * Do nothing.
     */
    public static void check() {}

  }

}
