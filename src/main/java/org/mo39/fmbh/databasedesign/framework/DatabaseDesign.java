package org.mo39.fmbh.databasedesign.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.executor.Executable;
import org.mo39.fmbh.databasedesign.executor.Executable.IsReadOnly;
import org.mo39.fmbh.databasedesign.executor.Executable.RequiresActiveSchema;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.ExecutionCheck.PostExecutionCheck;
import org.mo39.fmbh.databasedesign.framework.ExecutionCheck.PreExecutionCheck;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.Cmd;

public class DatabaseDesign {

  private List<Cmd> supportedCmdList;
  private Map<String, String> SystemProperties;

  /**
   * Check whether input string is a supported Cmd. If returns true, {@link DatabaseDesign#runCmd()}
   * can be called.
   *
   * @param arg
   * @return Returns true if supports. Otherwise false.
   */
  public boolean supports(String arg) {
    for (Cmd cmd : supportedCmdList) {
      Pattern regx = Pattern.compile(cmd.getRegx(), Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(arg);
      if (matcher.matches()) {
        cmd.setCmdStr(arg);
        Status.setCurrentCmd(cmd);
        return true;
      }
    }
    return false;
  }

  /**
   * Run command. The command can only be run after {@link DatabaseDesign#runCmd()} function is
   * called and returns true.
   * <p>
   * The method provides several features:<br>
   * &emsp;- check state according to annotation {@link RequiresActiveSchema}<br>
   * &emsp;- check if execute method is read only operation according to annotation
   * {@link IsReadOnly}<br>
   * &emsp;- view result according to Viewable interface (if implemented) right after the execution
   * finishes<br>
   * &emsp;- catch certain execeptions, display the message and consider the execution a failure (in
   * which case the Viewable result will not be displayed).<br>
   *
   */
  public void runCmd() {
    if (Status.getCurrentCmd() == null) {
      throw new IllegalStateException("Please check whether cmd is supported first.");
    }
    try {
      Class<?> klass = Class.forName(Status.getCurrentCmd().getExecutorClassName());
      if (Executable.class.isAssignableFrom(klass)) {
        Executable executor = Executable.class.cast(klass.newInstance());
        Method method = executor.getClass().getMethod("execute");
        if (PreExecutionCheck.check(method)) {
          method.invoke(executor);
          if (executor instanceof Viewable) {
            View.newView(Viewable.class.cast(executor));
          }
          PostExecutionCheck.check();
        }
      }
    } catch (InvocationTargetException e) {
      Throwable ex;
      if ((ex = e.getCause()) instanceof BadUsageException) {
        String message = ex.getMessage();
        if (message == null) {
          message = "";
        }
        View.newView("Bad usage for '" + Status.getCurrentCmd().getName() + "'. " + message);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    } finally {
      Status.endRunCmd();
    }
  }

  public List<Cmd> getSupportedCmdList() {
    return supportedCmdList;
  }

  public void setSupportedCmdList(List<Cmd> supportedCmdList) {
    this.supportedCmdList = supportedCmdList;
  }

  public Map<String, String> getSystemProperties() {
    return SystemProperties;
  }

  public void setSystemProperties(Map<String, String> systemProperties) {
    SystemProperties = Collections.unmodifiableMap(systemProperties);
  }

}


