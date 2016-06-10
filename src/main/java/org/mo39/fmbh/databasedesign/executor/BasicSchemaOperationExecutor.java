package org.mo39.fmbh.databasedesign.executor;

import java.util.Set;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;


public abstract class BasicSchemaOperationExecutor {

  public static class ShowSchemas implements Executable, Viewable {

    private static String endMessage;

    @Override
    @IsReadOnly
    public void execute() {
      Set<String> schemaSet = IOUtils.getSchemas();
      String currentSchema = Status.getInstance().getCurrentSchema();
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

  public static class Use implements Executable, Viewable {

    private static String endMessage;

    private static final String REGEX = "^USE(.*?)\\;$";

    @Override
    @IsReadOnly
    public void execute() {
      String schemaName =
          NamingUtils.extractAndCheckName(Status.getInstance().getCurrentCmd(), REGEX, 1);
      if (schemaName != null) {
        Set<String> schemaSet = IOUtils.getSchemas();
        if (schemaSet.contains(schemaName)) {
          Status.getInstance().setCurrentSchema(schemaName);
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

  public static class CreateSchema implements Executable, Viewable {

    private static String endMessage;
    private static final String REGEX = "^CREATE\\s*?SCHEMA(.*?)\\;$";

    @Override
    public void execute() {
      String schemaName =
          NamingUtils.extractAndCheckName(Status.getInstance().getCurrentCmd(), REGEX, 1);
      if (schemaName != null) {
        Set<String> schemaSet = IOUtils.getSchemas();
        if (schemaSet.contains(schemaName)) {
          endMessage = "Schema - '" + schemaName + "' already exists.";
        } else {
          Status.getInstance().setCurrentSchema(schemaName);
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

  public static class DropSchema implements Executable, Viewable {

    private static String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @IsReadOnly
    @RequiresActiveSchema
    public void execute() {
      String schemaName = Status.getInstance().getCurrentSchema();
      if (schemaName == null) {
        throw new BadUsageException();
      }
      Set<String> schemaSet = IOUtils.getSchemas();
      if (schemaSet.contains(schemaName)) {
        if (IOUtils.deleteSchema(schemaName)) {
          Status.getInstance().setCurrentSchema(null);
          endMessage = "Schema - '" + schemaName + "' and it's including tables are deleted";
        } else {
          endMessage = "Fails to delete Schema - '" + schemaName + "'";
        }
      } else {
        endMessage = "The schema -'" + schemaName + "' does not exist in archive";
      }
    }

  }

}


