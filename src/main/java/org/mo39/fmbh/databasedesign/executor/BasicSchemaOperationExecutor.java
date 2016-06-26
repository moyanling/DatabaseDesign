package org.mo39.fmbh.databasedesign.executor;

import java.io.IOException;
import java.util.Set;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
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
      if (schemaSet.size() == 0) {
        sb.append("\n\tNone");
      } else {
        for (String schema : schemaSet) {
          sb.append("\n\t" + schema + "\n");
        }
        sb.append("\nCurrently activated schema: ");
        sb.append(currentSchema == null ? "None" : currentSchema);
      }
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

    private static final String REGEX = "^USE\\s(.*?)\\;$";

    @Override
    @IsReadOnly
    public void execute() {
      String schema = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (schema != null) {
        Set<String> schemaSet = FileUtils.getSchemas();
        if (schemaSet.contains(schema)) {
          Status.setCurrentSchema(schema);
          endMessage = "Schema - '" + schema + "' is activated.";
        } else {
          endMessage = "Schema - '" + schema + "' is not found in the archive.";
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
   * Create a specified schema.
   * 
   * @author Jihan Chen
   *
   */
  public static class CreateSchema implements Executable, Viewable {

    private String endMessage;
    private static final String REGEX = "^CREATE\\s*?SCHEMA\\s(.*?)\\;$";

    @Override
    public void execute() throws IOException {
      String schema = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (schema == null) {
        throw new BadUsageException();
      }
      if (SystemProperties.INFO_SCHEMA.equals(schema)) {
        throw new BadUsageException(
            SystemProperties.INFO_SCHEMA + " is resevered. Please use others instead.");
      }
      if (FileUtils.getSchemas().contains(schema)) {
        endMessage = "Schema - '" + schema + "' already exists.";
        return;
      }
      if (FileUtils.createSchema(schema)) {
        endMessage = "Schema - '" + schema + "' is created.";
      } else {
        endMessage = "Fails to create Schema - '" + schema + "'.";
      }
    }

    @Override
    public String getView() {
      return endMessage;
    }
  }

  /**
   * Delete the currently activated schema, including all tables belong to this schema.
   *
   * @author Jihan Chen
   *
   */
  public static class DeleteSchema implements Executable, Viewable {

    private String endMessage;
    private static final String REGEX = "^DELETE\\s*?SCHEMA\\s(.*?)\\;$";


    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    public void execute() {
      String schema = NamingUtils.extractAndCheckName(Status.getCurrentCmdStr(), REGEX, 1);
      if (schema == null) {
        throw new BadUsageException();
      }
      Set<String> schemaSet = FileUtils.getSchemas();
      if (schemaSet.contains(schema)) {
        if (FileUtils.deleteSchema(schema)) {
          if (schema.equals(Status.getCurrentSchema())) {
            Status.setCurrentSchema(null);
          }
          endMessage = "Schema - '" + schema + "' and it's including tables are deleted";
        } else {
          endMessage = "Fails to delete Schema - '" + schema + "'";
        }
      } else {
        endMessage = "The schema - '" + schema + "' does not exist in archive";
      }
    }

  }

}


