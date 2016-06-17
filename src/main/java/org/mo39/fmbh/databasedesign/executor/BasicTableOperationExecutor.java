
package org.mo39.fmbh.databasedesign.executor;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DataType;
import org.mo39.fmbh.databasedesign.utils.FileUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

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
      for (String tableName : tableSet) {
        sb.append("\n\t" + tableName + "\n");
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
      String tableName = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (tableName == null) {
        throw new BadUsageException();
      }
      String schemaName = Status.getCurrentSchema();
      Set<String> tableSet = FileUtils.getTables(schemaName);
      if (tableSet.contains(tableName)) {
        if (FileUtils.deleteTable(schemaName, tableName)) {
          endMessage = "Table - '" + tableName + "' in schema -'" + schemaName + "' is deleted.";
        } else {
          endMessage =
              "Fails to delete Table - '" + tableName + "' in schema -'" + schemaName + "'";
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
    private static final String COLUMN_REGEX = "^(.*?)\\s+(.*?)(\\s*?$|\\s+(.*?)\\s*?$)";

    private String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    public void execute() {
      Matcher matcher = Pattern.compile(REGEX).matcher(Status.getCurrentCmdStr());
      if (!matcher.matches()) {
        throw new BadUsageException("No table or column definition is found.");
      }
      String tableName = matcher.group(1);
      String content = matcher.group(2);
      if (!NamingUtils.checkNamingConventions(tableName)) {
        throw new BadUsageException("Bad table name.");
      }
      Pattern columnRegx = Pattern.compile(COLUMN_REGEX);
      for (String col : content.split("\\,")) {
        matcher = columnRegx.matcher(col);
        // ----------------------
        if (!matcher.matches()) {
          throw new BadUsageException("Bad column definition.");
        }
        // ----------------------
        String columnName = matcher.group(1).trim();
        if (!NamingUtils.checkNamingConventions(columnName)) {
          throw new BadUsageException("Bad column name.");
        }
        // ----------------------
        String dataTypeStr = matcher.group(2).trim();
        DataType dataType = DataType.supports(dataTypeStr);
        if (dataType != null) {
          throw new BadUsageException("Unsupported data type.");
        }
        // ----------------------
        String constraintStr = matcher.group(3).trim();
        Constraint constraint = Constraint.supports(constraintStr);
        if (constraint != null) {
          throw new BadUsageException("Unsupported constraint.");
        }
        // TODO
      }


    }

  }



}
