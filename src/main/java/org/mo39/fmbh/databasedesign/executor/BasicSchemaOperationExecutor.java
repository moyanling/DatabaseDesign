package org.mo39.fmbh.databasedesign.executor;

import java.util.Set;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.utils.FileUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

/**
 * A abstract class that collects some basic schema operations.
 *
 * @author Jihan Chen
 *
 */
public abstract class BasicSchemaOperationExecutor {

  /**
   * Show all schemas in the database.
   *
   * @author Jihan Chen
   *
   */
  public static class ShowSchemas implements Executable, Viewable {

    private String endMessage;

    @Override
    @IsReadOnly
    public void execute() {
      Set<String> schemaSet = FileUtils.getSchemas();
      String currentSchema = Status.getCurrentSchema();
      StringBuilder sb = new StringBuilder("Show Schemas: ");
      for (String schemaName : schemaSet) {
        sb.append("\n\t" + schemaName + "\n");
      }
      sb.append("\nCurrently activated schema: ");
      sb.append(currentSchema == null ? "None" : currentSchema);
      endMessage = sb.toString();
    }

    @Override
    public String getView() {
      return endMessage;
    }
  }

  /**
   * Use a specified schema.
   *
   * @author Jihan Chen
   *
   */
  public static class Use implements Executable, Viewable {

    private String endMessage;

    private static final String REGEX = "^USE(.*?)\\;$";

    @Override
    @IsReadOnly
    public void execute() {
      String schemaName = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (schemaName != null) {
        Set<String> schemaSet = FileUtils.getSchemas();
        if (schemaSet.contains(schemaName)) {
          Status.setCurrentSchema(schemaName);
          endMessage = "Schema - '" + schemaName + "' is activated.";
        } else {
          endMessage = "Schema - '" + schemaName + "' is not found in the archive.";
        }
      } else {
        throw new BadUsageException();
      }
    }

    @Override
    public String getView() {
      return endMessage;
    }
  }

  /**
   * Create a specified schema. If no table is created in this schema, the schema will not be saved.
   *
   * @author chen39
   *
   */
  public static class CreateSchema implements Executable, Viewable {

    private String endMessage;
    private static final String REGEX = "^CREATE\\s*?SCHEMA(.*?)\\;$";

    @Override
    public void execute() {
      String schemaName = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (schemaName != null) {
        Set<String> schemaSet = FileUtils.getSchemas();
        if (schemaSet.contains(schemaName)) {
          endMessage = "Schema - '" + schemaName + "' already exists.";
        } else {
          Status.setCurrentSchema(schemaName);
          endMessage = "Schema - '" + schemaName
              + "' is activated. Create at least one table to save this schema.";
        }
      } else {
        throw new BadUsageException();
      }
    }

    @Override
    public String getView() {
      return endMessage;
    }
  }

  /**
   * Drop the currently activated schema, including all tables belong to this schema.
   *
   * @author Jihan Chen
   *
   */
  public static class DropSchema implements Executable, Viewable {

    private String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresConfirm
    @RequiresActiveSchema
    public void execute() {
      String schemaName = Status.getCurrentSchema();
      Set<String> schemaSet = FileUtils.getSchemas();
      if (schemaSet.contains(schemaName)) {
        if (FileUtils.deleteSchema(schemaName)) {
          Status.setCurrentSchema(null);
          endMessage = "Schema - '" + schemaName + "' and it's including tables are deleted";
        } else {
          endMessage = "Fails to delete Schema - '" + schemaName + "'";
        }
      } else {
        endMessage = "The schema - '" + schemaName + "' does not exist in archive";
      }
    }

  }

}


