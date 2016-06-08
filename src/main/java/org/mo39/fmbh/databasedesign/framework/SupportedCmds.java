package org.mo39.fmbh.databasedesign.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.executor.Executable;
import org.mo39.fmbh.databasedesign.executor.Executable.ExitOperation;
import org.mo39.fmbh.databasedesign.executor.Executable.SchemaOperation;
import org.mo39.fmbh.databasedesign.executor.Executable.SqlOperation;
import org.mo39.fmbh.databasedesign.executor.Executable.TableOperation;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.MissingAnnotationException;
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
        cmd.setSqlStr(arg);
        currCmd = cmd;
        return true;
      }
    }
    return false;
  }

  public void runCmd() {
    if (currCmd == null) {
      throw new IllegalStateException("Please check whether cmd is supported first.");
    }
    Status.INSTANCE.setCurrentSql(currCmd.getSqlStr());
    try {
      Class<?> klass = Class.forName(currCmd.getExecutorClassName());
      if (Executable.class.isAssignableFrom(klass)) {
        Executable executor = Executable.class.cast(klass.newInstance());
        Method method = executor.getClass().getMethod("execute");
        CheckAnnotation(method);
        method.invoke(executor);
        if (executor instanceof Viewable) {
          currCmd = null;
          View.newView(Viewable.class.cast(executor));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Check some constraints according to Annotation. If check fails, an IllegalStateException is
   * thrown. Not all annotations have constraints And all methods in dao must be annotated. If the
   * method is a SqlOperation, the currentSql in Status will be injected.
   *
   * @param method
   */
  private void CheckAnnotation(Method method) {
    Annotation annotation = null;
    if ((annotation = method.getAnnotation(SqlOperation.class)) != null) {
      SqlOperation sqlAnnotation = SqlOperation.class.cast(annotation);
      if (sqlAnnotation.requireActiveSchema() == true) {
        if (!Status.INSTANCE.hasActiveSchema()) {
          throw new IllegalStateException("No schema is found on sql operation.");
        }
      }
      if (sqlAnnotation.requireActiveTable() == true) {
        if (!Status.INSTANCE.hasActiveTable()) {
          throw new IllegalStateException("No table is found on sql operation.");
        }
      }
      Status.INSTANCE.setCurrentSql(currCmd.getSqlStr());
      // -------------------------------------------------
    } else if ((annotation = method.getAnnotation(TableOperation.class)) != null) {
      TableOperation tableAnnotation = TableOperation.class.cast(annotation);
      if (tableAnnotation.requiresActiveSchema() == true) {
        if (!Status.INSTANCE.hasActiveSchema()) {
          throw new IllegalStateException("No schema is found on table operation.");
        }
      }
      // -------------------------------------------------
    } else if ((annotation = method.getAnnotation(SchemaOperation.class)) != null) {
      // -------------------------------------------------
    } else if ((annotation = method.getAnnotation(ExitOperation.class)) != null) {
      // -------------------------------------------------
    } else {
      throw new MissingAnnotationException("No annotation is found on execute method.");
    }
  }

  @Override
  public String getView() {
    StringBuilder sb = new StringBuilder("Supported commands: \n\n");
    for (Cmd cmd : supportedCmdList) {
      sb.append("\t" + cmd.getName() + ": \n\t\t" + cmd.getDescription() + "\n\n");
    }
    return sb.toString();
  }


  public void setSupportedCmdList(List<Cmd> supportedCmdList) {
    this.supportedCmdList = supportedCmdList;
  }

  /**
   * Command class. The regx and daoMethodName is injected by applicationContext. String sql should
   * not be injected. It should be set when the input matches regx.
   *
   * @author Jihan Chen
   *
   */
  public static class Cmd {

    private String sql;
    private String name;
    private String regx;
    private String description;
    private String executorClassName;

    public String getSqlStr() {
      return sql;
    }

    public void setSqlStr(String sqlStr) {
      sql = sqlStr;
    }

    public String getRegx() {
      return regx;
    }

    public void setRegx(String regx) {
      this.regx = regx;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getExecutorClassName() {
      return executorClassName;
    }

    public void setExecutorClassName(String executorClassName) {
      this.executorClassName = executorClassName;
    }
  }

}
