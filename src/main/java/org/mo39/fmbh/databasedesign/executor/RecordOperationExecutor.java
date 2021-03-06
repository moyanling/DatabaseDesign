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
import org.mo39.fmbh.databasedesign.utils.FileUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;
import org.mo39.fmbh.databasedesign.utils.TblUtils;

public abstract class RecordOperationExecutor {

  public static class InsertIntoTable implements Executable, Viewable {

    private String endMessage;
    private static final String REGEX =
        "^INSERT\\s+INTO\\s+TABLE\\s+(.*?)\\s+VALUES\\s+\\((.*?)\\)\\s*\\;$";

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions, IOException {
      String cmd = Status.getCurrentCmdStr();
      String schema = Status.getCurrentSchema();
      Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
      Matcher matcher = p.matcher(cmd);
      if (!matcher.matches()) {
        throw new BadUsageException("Bad insertion command");
      }
      String table = matcher.group(1);
      String values = matcher.group(2);
      if (!NamingUtils.checkNamingConventions(table)) {
        throw new BadUsageException("Bad table name: " + table);
      }
      if (!FileUtils.getTableSet(schema).contains(table)) {
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
    private static Pattern regex =
        Pattern.compile("^SELECT\\s+(.*?)\\s+FROM\\s+(.*?)(\\s*?|\\s+WHERE\\s(.*=.*))\\;$",
            Pattern.CASE_INSENSITIVE);

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions {
      String cmd = Status.getCurrentCmdStr();
      Matcher m = regex.matcher(cmd);
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
      if (!NamingUtils.checkNamingConventions(table)) {
        throw new BadUsageException("Bad table name");
      }
      // ----------------------
      String whereClause = m.group(3).trim();
      List<Object> resultSet = null;
      try {
        resultSet = TblUtils.selectFromDB(Status.getCurrentSchema(), table, beanClass, whereClause);
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
