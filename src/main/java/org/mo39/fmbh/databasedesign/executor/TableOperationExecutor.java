
package org.mo39.fmbh.databasedesign.executor;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.utils.DBChecker;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.InfoSchemaUtils;

import com.google.common.collect.Lists;

/**
 * A abstract class that collects table operations.
 *
 * @author Jihan Chen
 *
 */
public abstract class TableOperationExecutor {

  /**
   * Show all tables in current active schema.
   *
   * @author Jihan Chen
   *
   */
  public static class ShowTables implements Executable, Viewable {

    private String tab = SystemProperties.get("tab");
    private String lineBreak = SystemProperties.get("lineBreak");
    private String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() {
      Set<String> tableSet = InfoSchemaUtils.getTables(Status.getCurrentSchema());
      StringBuilder sb = new StringBuilder("Show Tables: ");
      if (tableSet.size() == 0) {
        sb.append(lineBreak + tab + "None");
        endMessage = sb.toString();
        return;
      }
      for (String table : tableSet) {
        sb.append(lineBreak + tab + table + lineBreak);
      }
      endMessage = sb.substring(0, sb.length() - 1).toString();
    }

  }

  /**
   * Drop the specified table in this schema.
   *
   * @author Jihan Chen
   *
   */
  public static class DropTable implements Executable, Viewable {

    private String endMessage;
    private Matcher m = Pattern.compile("^DROP\\s*?TABLE\\s(.*?)\\;$", Pattern.CASE_INSENSITIVE)
        .matcher(Status.getCurrentCmdStr());

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions {
      DBChecker.checkSyntax(m);
      String table = DBChecker.checkName(m, 1);
      String schema = Status.getCurrentSchema();
      Set<String> tableSet = InfoSchemaUtils.getTables(schema);
      if (!tableSet.contains(table)) {
        throw new BadUsageException("The table does not exist in schema - '" + schema + "'.");
      }
      if (IOUtils.deleteTable(schema, table)) {
        endMessage = "Table - '" + table + "' in schema -'" + schema + "' is deleted.";
      } else {
        endMessage = "Fails to delete Table - '" + table + "' in schema - '" + schema + "'";
      }

    }
  }

  /**
   * Create a table in current active schema.
   *
   * @author Jihan Chen
   *
   */
  public static class CreateTable implements Executable, Viewable {

    private Matcher m =
        Pattern.compile("^CREATE\\s*?TABLE\\s(.*?)\\s\\((.*)\\)\\s*?\\;$", Pattern.CASE_INSENSITIVE)
            .matcher(Status.getCurrentCmdStr());

    private String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions, IOException {
      List<Column> columns = Lists.newArrayList();
      DBChecker.checkSyntax(m);
      String content = m.group(2);
      String table = DBChecker.checkName(m, 1);
      columns = Column.newColumnDefinition(content);
      String schema = Status.getCurrentSchema();
      if (InfoSchemaUtils.getTables(schema).contains(table)) {
        endMessage = "Table - '" + table + "' already exists.";
        return;
      }
      if (IOUtils.createtblFile(schema, table, columns)) {
        endMessage = "Table - '" + table + "' is Created.";
      } else {
        endMessage = "Fails to create Table - '" + table + "'.";
      }
    }

  }



}
