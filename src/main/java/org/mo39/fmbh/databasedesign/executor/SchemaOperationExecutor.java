package org.mo39.fmbh.databasedesign.executor;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.utils.DbChecker;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.InfoSchemaUtils;

/**
 * A abstract class that collects schema operations.
 *
 * @author Jihan Chen
 *
 */
public abstract class SchemaOperationExecutor {

  /**
   * Show all schemas in the database.
   *
   * @author Jihan Chen
   *
   */
  public static class ShowSchemas implements Executable, Viewable {

    private String endMessage;
    private String tab = SystemProperties.get("tab");
    private String lineBreak = SystemProperties.get("lineBreak");

    @Override
    public void execute() {
      Set<String> schemaSet = InfoSchemaUtils.getSchemas();
      String currentSchema = Status.getCurrentSchema();
      StringBuilder sb = new StringBuilder("Show Schemas: ");
      if (schemaSet.size() == 0) {
        sb.append(lineBreak + tab + "None  ");
      } else {
        for (String schema : schemaSet) {
          sb.append(lineBreak + tab + schema + lineBreak);
        }
        sb.append(lineBreak + "Currently activated schema: ");
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
    private Matcher m = Pattern.compile("^USE\\s(.*?)\\;$", Pattern.CASE_INSENSITIVE)
        .matcher(Status.getCurrentCmdStr());

    @Override
    public void execute() throws DBExceptions {
      DbChecker.checkSyntax(m);
      String schema = DbChecker.checkName(m, 1);
      Set<String> schemaSet = InfoSchemaUtils.getSchemas();
      if (schemaSet.contains(schema)) {
        Status.setCurrentSchema(schema);
        endMessage = "Schema - '" + schema + "' is activated.";
      } else {
        endMessage = "Schema - '" + schema + "' is not found in the archive.";
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
    private Matcher m = Pattern.compile("^CREATE\\s*?SCHEMA\\s(.*?)\\;$", Pattern.CASE_INSENSITIVE)
        .matcher(Status.getCurrentCmdStr());

    @Override
    public void execute() throws DBExceptions, IOException {
      DbChecker.checkSyntax(m);
      String schema = DbChecker.checkName(m, 1);
      if (InfoSchemaUtils.isReserved(schema)) {
        throw new BadUsageException(
            "Schema - '" + schema + "' is resevered. Please use others instead.");
      }
      if (InfoSchemaUtils.getSchemas().contains(schema)) {
        endMessage = "Schema - '" + schema + "' already exists.";
        return;
      }
      if (IOUtils.createSchema(schema)) {
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
    private Matcher m = Pattern.compile("^DELETE\\s*?SCHEMA\\s(.*?)\\;$", Pattern.CASE_INSENSITIVE)
        .matcher(Status.getCurrentCmdStr());


    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    public void execute() throws DBExceptions {
      DbChecker.checkSyntax(m);
      String schema = DbChecker.checkName(m, 1);
      Set<String> schemaSet = InfoSchemaUtils.getSchemas();
      if (schemaSet.contains(schema)) {
        if (IOUtils.deleteSchema(schema)) {
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


