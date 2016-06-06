package org.mo39.fmbh.databasedesign.dao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.ExitOperation;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.SchemaOperation;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.SqlOperation;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.TableOperation;
import org.mo39.fmbh.databasedesign.model.DatabaseDesignExceptions.MissingAnnotationException;
import org.mo39.fmbh.databasedesign.model.Status;
import org.mo39.fmbh.databasedesign.utils.View.Viewable;

public class SupportedCmds implements Viewable {

  private static Cmd currCmd;
  private static List<Cmd> supportedCmdList;
  private static Map<String, String> cmdDescriptionMap;

  /**
   * Check whether input string is supported. If it's supported, set the sqlStr and add to currCmd
   * 
   * @param sql
   * @return
   */
  public static boolean supports(String sql) {
    for (Cmd cmd : supportedCmdList) {
      Pattern regx = Pattern.compile(cmd.getRegx(), Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(sql);
      if (matcher.matches()) {
        cmd.setSqlStr(sql);
        currCmd = cmd;
        return true;
      }
    }
    return false;
  }

  public static void runCmd() {
    if (currCmd == null) {
      throw new IllegalStateException("Please check whether cmd is supported first.");
    }
    Status.INSTANCE.setCurrentSql(currCmd.getSqlStr());
    try {
      
      Method method = DatabaseDao.class.getMethod(currCmd.getDaoMethodName());
      CheckAnnotation(method);
      method.invoke(DatabaseDaoImpl.INSTANCE);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      currCmd = null;
    }
  }

  /**
   * Check some constraints according to Annotation. If check fails, an IllegalStateException is
   * thrown. Not all annotations have constraints And all methods in dao must be annotated. If the
   * method is a SqlOperation, the currentSql in Status will be injected.
   * 
   * @param method
   */
  private static void CheckAnnotation(Method method) {
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
      throw new MissingAnnotationException("No annotation is found on dao method.");
    }
  }
  
  @Override
  public String getCliView() {
    StringBuilder sb = new StringBuilder("Supported commands: \n");
    for (Object key : cmdDescriptionMap.keySet()) {
      sb.append("\t" + key + ": \n\t\t" + cmdDescriptionMap.get(key) + "\n\n");
    }
    return sb.toString();
  }  
  
  public void setCmdDescriptionMap(Map<String, String> cmdDescriptionMap) {
    SupportedCmds.cmdDescriptionMap = cmdDescriptionMap;
  }

  public void setSupportedCmdList(List<Cmd> supportedCmdList) {
    SupportedCmds.supportedCmdList = supportedCmdList;
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
    private String regx;
    private String daoMethodName;

    public String getRegx() {
      return regx;
    }

    public void setRegx(String regx) {
      this.regx = regx;
    }

    public String getDaoMethodName() {
      return daoMethodName;
    }

    public void setDaoMethodName(String daoMethodName) {
      this.daoMethodName = daoMethodName;
    }

    public String getSqlStr() {
      return sql;
    }

    public void setSqlStr(String sqlStr) {
      this.sql = sqlStr;
    }
  }

}
