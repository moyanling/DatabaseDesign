
package org.mo39.fmbh.databasedesign.executor;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.Record;
import org.mo39.fmbh.databasedesign.utils.FileUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

import com.google.common.collect.Lists;

/**
 * A abstract class that collects some basic table operations.
 *
 * @author Jihan Chen
 *
 */
public abstract class BasicTableOperationExecutor {

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
    public void execute() {
      String table = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (table == null) {
        throw new BadUsageException();
      }
      String schema = Status.getCurrentSchema();
      Set<String> tableSet = FileUtils.getTables(schema);
      if (tableSet.contains(table)) {
        if (FileUtils.deleteTable(schema, table)) {
          endMessage = "Table - '" + table + "' in schema -'" + schema + "' is deleted.";
        } else {
          endMessage = "Fails to delete Table - '" + table + "' in schema - '" + schema + "'";
        }
      } else {
        endMessage =
            "The table does not exist in current schema - '" + Status.getCurrentSchema() + "'.";
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
    public void execute() {
      List<Column> columns = Lists.newArrayList();
      Matcher matcher = Pattern.compile(REGEX).matcher(Status.getCurrentCmdStr());
      if (!matcher.matches()) {
        throw new BadUsageException("No table or column definition is found.");
      }
      String table = matcher.group(1);
      String content = matcher.group(2);
      if (!NamingUtils.checkNamingConventions(table)) {
        throw new BadUsageException("Bad table name.");
      }
      for (String col : content.split("\\,")) {
        columns.add(Column.newColumnDefinition(col));
      }
      Record record = Record.newTemplate(Status.getCurrentSchema(), table, columns);
      record.writeToTable();
      endMessage = "Table - '" + table + "' is Created.";
    }

  }



}
