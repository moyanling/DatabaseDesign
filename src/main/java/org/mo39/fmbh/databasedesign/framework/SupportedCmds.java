package org.mo39.fmbh.databasedesign.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.mo39.fmbh.databasedesign.executor.Executable;
import org.mo39.fmbh.databasedesign.executor.Executable.IsReadOnly;
import org.mo39.fmbh.databasedesign.executor.Executable.RequiresActiveSchema;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;

public class SupportedCmds implements Viewable {

  public SupportedCmds(String usageInstruction) {
    this.usageInstruction = usageInstruction;
  }

  @Resource(name = "supportedCmdList")
  private List<Cmd> supportedCmdList;

  private Cmd currCmd;
  private String usageInstruction;
  private ReadWriteLock rwLock = new ReentrantReadWriteLock();

  /**
   * Check whether input string is supported. If returns true, {@link SupportedCmds#runCmd} can be
   * called.
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
        invokeCmd(method, executor, checkReadOnlyAnnotation(method));
      }
    } catch (IllegalStateException e) {
      String message = e.getMessage();
      if (message == null) {
        message = "";
      }
      View.newView("Illegal state. " + message);
    } catch (InvocationTargetException e) {
      Throwable ex;
      if ((ex = e.getCause()) instanceof BadUsageException) {
        String message = ex.getMessage();
        if (message == null) {
          message = "";
        }
        View.newView("Bad usage for '" + currCmd.name + "'. " + message);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    } finally {
      currCmd = null;
      Status.getInstance().endRunCmd();
    }
  }

  @Override
  public String getView() {
    StringBuilder sb = new StringBuilder("Supported commands: \n\n");
    for (Cmd cmd : supportedCmdList) {
      sb.append("\t" + cmd.getName() + " \n\t\t" + cmd.getDescription() + "\n\n");
    }
    sb.append(usageInstruction);
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

  private void invokeCmd(Method method, Executable executor, boolean isReadOnly) throws Exception {
    try {
      if (isReadOnly) {
        rwLock.readLock().lock();
      } else {
        rwLock.writeLock().lock();
      }
      method.invoke(executor);
      if (executor instanceof Viewable) {
        View.newView(Viewable.class.cast(executor));
      }
    } catch (Exception e) {
      throw e;
    } finally {
      if (isReadOnly) {
        rwLock.readLock().unlock();
      } else {
        rwLock.writeLock().unlock();
      }
    }
  }


  /**
   * Command class. The fields are injected by applicationContext except cmd. String cmd is set
   * manually if the input arg matches the regx.
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
