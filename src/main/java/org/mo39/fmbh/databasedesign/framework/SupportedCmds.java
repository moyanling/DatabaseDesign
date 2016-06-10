package org.mo39.fmbh.databasedesign.framework;

import static org.mo39.fmbh.databasedesign.framework.View.newView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.executor.Executable;
import org.mo39.fmbh.databasedesign.executor.Executable.IsReadOnly;
import org.mo39.fmbh.databasedesign.executor.Executable.RequiresActiveSchema;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;

public class SupportedCmds implements Viewable {

  public SupportedCmds() {}

  private Cmd currCmd;
  private List<Cmd> supportedCmdList;

  /**
   * Check whether input string is supported. If it's supported, set the sqlStr and add to currCmd
   *
   * @param sql
   * @return
   */
  public boolean supports(String arg) {
    for (Cmd cmd : supportedCmdList) {
      Pattern regx = Pattern.compile(cmd.getRegx(), Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(arg);
      if (matcher.matches()) {
        cmd.setCmdStr(arg);
        currCmd = cmd;
        return true;
      }
    }
    currCmd = null;
    return false;
  }

  /**
   * Run command. The command can only be run after {@link SupportedCmds#supports} function is
   * called and returns true.
   * <p>
   * The method provides several features:<br>
   * &emsp;- check state according to annotation. {@link RequiresActiveSchema}<br>
   * &emsp;- check if execute method is read only operation. {@link IsReadOnly}<br>
   * &emsp;- view result according to Viewable interface (if implemented) right after the execution
   * finishes<br>
   * &emsp;- catch certain execeptions, display the message and consider the execution a failure (in which
   * case the Viewable result will not be displayed).<br>
   *
   */
  public void runCmd() {
    if (currCmd == null) {
      throw new IllegalStateException("Please check whether cmd is supported first.");
    }
    Status.getInstance().setCurrentCmd(currCmd.getCmdStr());
    try {
      Class<?> klass = Class.forName(currCmd.getExecutorClassName());
      if (Executable.class.isAssignableFrom(klass)) {
        Executable executor = Executable.class.cast(klass.newInstance());
        Method method = executor.getClass().getMethod("execute");
        checkOperationAnnotation(method);
        boolean isReadOnly = checkReadOnlyAnnotation(method);
        if (!isReadOnly) {
          // TODO Need acquire a lock first.
        }
        method.invoke(executor);
        if (executor instanceof Viewable) {
          newView(Viewable.class.cast(executor));
        }
        if (!isReadOnly) {
          // TODO Release the lock.
        }
      }
    } catch (Exception e) {
      Throwable ex;
      String message;
      if ((ex = e.getCause()) instanceof BadUsageException) {
        message = ex.getMessage();
        if (message == null) {
          message = "";
        }
        newView("Bad usage for command " + currCmd.name + ". " + message);
      } else if ((ex = e.getCause()) instanceof IllegalStateException) {
        message = ex.getMessage();
        if (message == null) {
          message = "";
        }
        newView("Illegal state. " + message);
      } else {
        e.printStackTrace();
        throw new Error();
      }
    }
    currCmd = null;
    Status.getInstance().endRunCmd();

  }

  @Override
  public String getView() {
    StringBuilder sb = new StringBuilder("Supported commands: \n\n");
    for (Cmd cmd : supportedCmdList) {
      sb.append("\t" + cmd.getName() + ": \n\t\t" + cmd.getDescription() + "\n\n");
    }
    sb.append("NOTE:\n\tPlease use letter, number, underscore and dash only. And please start with "
        + "letter and don't end with underscore or dash for naming conventions. \n\tOtherwise the "
        + "command will be consider a bad usage and won't be accepted.");
    return sb.toString();
  }

  private void checkOperationAnnotation(Method method) {
    if (method.getAnnotation(RequiresActiveSchema.class) != null) {
      if (!Status.getInstance().hasActiveSchema()) {
        throw new IllegalStateException("No schema is activated for operation.");
      }
    }
  }

  private boolean checkReadOnlyAnnotation(Method method) {
    if (method.getAnnotation(IsReadOnly.class) != null) {
      return true;
    }
    return false;
  }

  public void setSupportedCmdList(List<Cmd> supportedCmdList) {
    this.supportedCmdList = supportedCmdList;
  }

  /**
   * Command class. The regx and executorClassName is injected by applicationContext. String sql
   * should not be injected. It should be set when the input matches regx.
   *
   * @author Jihan Chen
   *
   */
  private static class Cmd {

    private String cmd;
    private String name;
    private String regx;
    private String description;
    private String executorClassName;

    public String getCmdStr() {
      return cmd;
    }

    public void setCmdStr(String sqlStr) {
      cmd = sqlStr;
    }

    public String getRegx() {
      return regx;
    }

    @SuppressWarnings("unused")
    public void setRegx(String regx) {
      this.regx = regx;
    }

    public String getDescription() {
      return description;
    }

    @SuppressWarnings("unused")
    public void setDescription(String description) {
      this.description = description;
    }

    public String getName() {
      return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
      this.name = name;
    }

    public String getExecutorClassName() {
      return executorClassName;
    }

    @SuppressWarnings("unused")
    public void setExecutorClassName(String executorClassName) {
      this.executorClassName = executorClassName;
    }
  }

}
