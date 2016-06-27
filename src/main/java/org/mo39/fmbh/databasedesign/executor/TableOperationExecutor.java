
package org.mo39.fmbh.databasedesign.executor;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.utils.FileUtils;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

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

    private String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @IsReadOnly
    @RequiresActiveSchema
    public void execute() {
      Set<String> tableSet = FileUtils.getTables(Status.getCurrentSchema());
      StringBuilder sb = new StringBuilder("Show Tables: ");
      for (String table : tableSet) {
        sb.append("\n\t" + table + "\n");
      }
      endMessage = sb.toString();
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
    private static final String REGEX = "^DROP\\s*?TABLE(.*?)\\;$";


    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws DBExceptions {
      String table = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (table == null) {
        throw new BadUsageException();
      }
      String schema = Status.getCurrentSchema();
      Set<String> tableSet = FileUtils.getTables(schema);
      if (!tableSet.contains(table)) {
        throw new BadUsageException("The table does not exist in schema - '" + schema + "'.");
      }
      if (FileUtils.deleteTable(schema, table)) {
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

    private static final String REGEX = "^CREATE\\s*?TABLE(.*?)\\((.*)\\)\\s.*?\\;$";


    private String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    public void execute() throws IOException, DBExceptions {
      String schema = Status.getCurrentSchema();
      List<Column> columns = Lists.newArrayList();
      Matcher matcher = Pattern.compile(REGEX).matcher(Status.getCurrentCmdStr());
      // ----------------------
      if (!matcher.matches()) {
        throw new BadUsageException("No table or column definition is found.");
      }
      // ----------------------
      String table = matcher.group(1);
      if (!NamingUtils.checkNamingConventions(table)) {
        throw new BadUsageException("Table name is not following naming conventions.");
      }
      if (FileUtils.getTables(schema).contains(table)) {
        throw new BadUsageException("Table - '" + table + "' already exists.");
      }
      // ----------------------
      String content = matcher.group(2);
      for (String col : content.split("\\,")) {
        columns.add(Column.newColumnDefinition(col));
      }
      // ----------------------
      IOUtils.createNewTable(schema, table, columns);
      endMessage = "Table - '" + table + "' is Created.";

    }

  }



}
