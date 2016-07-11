package org.mo39.fmbh.databasedesign.executor;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ClassNotFound;
import org.mo39.fmbh.databasedesign.model.Table;
import org.mo39.fmbh.databasedesign.utils.BeanUtils;
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
      if (!m.matches()) {
        throw new BadUsageException("Bad insertion command");
      }
      String table = m.group(1);
      String values = m.group(2);
      if (!IOUtils.checkNamingConventions(table)) {
        throw new BadUsageException("Bad table name: " + table);
      }
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
        Pattern.compile("^SELECT\\s+(.*?)\\s+FROM\\s+(.*?)(\\s*?|\\s+WHERE\\s(.*=.*))\\;$",
            Pattern.CASE_INSENSITIVE).matcher(Status.getCurrentCmdStr());

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @IsReadOnly
    @RequiresActiveSchema
    public void execute() throws DBExceptions {
      if (!m.matches()) {
        throw new BadUsageException("SELECT syntax not match.");
      }
      // ----------------------
      String className = m.group(1);
      Class<?> beanClass;
      try {
        beanClass = Class.forName(className);
      } catch (ClassNotFoundException e) {
        throw new ClassNotFound("Class '" + e.getMessage() + "' is not Found.");
      }
      // ----------------------
      String table = m.group(2);
      if (!IOUtils.checkNamingConventions(table)) {
        throw new BadUsageException("Bad table name");
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
          sb.append(tab + BeanUtils.beanToString(beanClass.cast(result)) + lineBreak);
        }
      }
      endMessage = sb.substring(0, sb.length() - 1);
    }
  }


}
