package org.mo39.fmbh.databasedesign.executor;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ClassNotFound;
import org.mo39.fmbh.databasedesign.model.Table;
import org.mo39.fmbh.databasedesign.utils.DbChecker;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.InfoSchemaUtils;

public abstract class RecordOperationExecutor {

  public static class InsertIntoTable implements Executable, Viewable {

    private String endMessage;
    private Matcher m =
        Pattern.compile("^INSERT\\s+INTO\\s+TABLE\\s+(.*?)\\s+VALUES\\s+\\((.*?)\\)\\s*\\;$",
            Pattern.CASE_INSENSITIVE).matcher(Status.getCurrentCmdStr());;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions, IOException {
      String schema = Status.getCurrentSchema();
      DbChecker.checkSyntax(m);
      String table = DbChecker.checkName(m, 1);
      String values = m.group(2);
      if (!InfoSchemaUtils.getTables(schema).contains(table)) {
        throw new BadUsageException("Table not found in current schema");
      }
      Table t = Table.init(schema, table);
      t.addRecord(values);
      t.writeToDB();
      endMessage = "Insertion done.";
    }
  }

  public static class Select implements Executable, Viewable {

    private String endMessage;
    private String lineBreak = SystemProperties.get("lineBreak");
    private String tab = SystemProperties.get("tab");
    private Matcher m =
        Pattern.compile("^SELECT\\s+(.*?)\\s+FROM\\s+(.*?)(\\s*?|\\s+WHERE\\s.*=.*)\\;$",
            Pattern.CASE_INSENSITIVE).matcher(Status.getCurrentCmdStr());

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions {
      DbChecker.checkSyntax(m);
      String table = DbChecker.checkName(m, 2);
      // ----------------------
      String className = m.group(1);
      Class<?> beanClass;
      try {
        beanClass = Class.forName(className);
      } catch (ClassNotFoundException e) {
        throw new ClassNotFound("Class '" + e.getMessage() + "' is not Found.");
      }
      // ----------------------
      String whereClause = m.group(3).trim();
      List<Object> resultSet = null;
      try {
        resultSet = IOUtils.selectFromDB(Status.getCurrentSchema(), table, beanClass, whereClause);
      } catch (IOException e) {
        DBExceptions.newError(e);
      }
      StringBuilder sb = new StringBuilder("Result: " + lineBreak);
      if (resultSet.size() == 0) {
        sb.append(tab + "None  ");
      } else {
        for (Object result : resultSet) {
          sb.append(tab + ReflectionToStringBuilder.reflectionToString(beanClass.cast(result))
              + lineBreak);
        }
      }
      endMessage = sb.substring(0, sb.length() - 1);
    }
  }

  public static class Update implements Executable, Viewable {

    private String endMessage;
    private Matcher m =
        Pattern.compile("^UPDATE\\s+(.*?)\\s+SET\\s+(.*?)(\\s*?|\\s+WHERE\\s(.*=.*))\\;$",
            Pattern.CASE_INSENSITIVE).matcher(Status.getCurrentCmdStr());

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions {
      DbChecker.checkSyntax(m);
      String table = DbChecker.checkName(m, 1);
      String setClause = m.group(2).trim();
      String whereClause = m.group(3).trim();
      Matcher setMatcher = Pattern.compile("(.*)=(.*)").matcher(setClause);
      DbChecker.checkSyntax(setMatcher);
      IOUtils.updateRecord(Status.getCurrentSchema(), table, setMatcher.group(1).trim(),
          setMatcher.group(2).trim(), whereClause);
      endMessage = "Record is updated";
    }
  }

  public static class Delete implements Executable, Viewable {

    private String endMessage;
    private Matcher m = Pattern
        .compile("^DELETE\\s+FROM\\s+(.*?)(\\s*?|\\s+WHERE\\s.*=.*)\\;$", Pattern.CASE_INSENSITIVE)
        .matcher(Status.getCurrentCmdStr());

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions {
      DbChecker.checkSyntax(m);
      String table = DbChecker.checkName(m, 1);
      String whereClause = m.group(2).trim();
      IOUtils.deleteRecord(Status.getCurrentSchema(), table, whereClause);
      endMessage = "Record is deleted";
    }

  }



}
