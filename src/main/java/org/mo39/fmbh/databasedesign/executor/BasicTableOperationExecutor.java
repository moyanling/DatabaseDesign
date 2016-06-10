package org.mo39.fmbh.databasedesign.executor;

import java.util.Set;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.utils.IOUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

public abstract class BasicTableOperationExecutor {

  public static class ShowTables implements Executable, Viewable {

    private static String endMessage;

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @IsReadOnly
    @RequiresActiveSchema
    public void execute() {
      Set<String> tableSet = IOUtils.getTables(Status.getInstance().getCurrentSchema());
      StringBuilder sb = new StringBuilder("Show Tables: ");
      for (String tableName : tableSet) {
        sb.append("\n\t" + tableName + "\n");
      }
      endMessage = sb.toString();
    }

  }

  public static class DropTable implements Executable, Viewable {

    private static String endMessage;
    private static final String REGEX = "^DROP\\s*?TABLE(.*?)\\;$";


    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    @RequiresActiveSchema
    public void execute() {
      String tableName =
          NamingUtils.extractAndCheckName(Status.getInstance().getCurrentCmd(), REGEX, 1);
      if (tableName == null) {
        throw new BadUsageException();
      }
      String schemaName = Status.getInstance().getCurrentSchema();
      Set<String> tableSet = IOUtils.getTables(schemaName);
      if (tableSet.contains(tableName)) {
        if (IOUtils.deleteTable(schemaName, tableName)) {
          endMessage = "Table - '" + tableName + "' in schema -'" + schemaName + "' is deleted.";
        } else {
          endMessage =
              "Fails to delete Table - '" + tableName + "' in schema -'" + schemaName + "'";
        }
      } else {
        endMessage = "The table does not exist in current schema - '"
            + Status.getInstance().getCurrentSchema() + "'.";
      }

    }

  }


  public static class CreateTable implements Executable, Viewable {

    private static String endMessage;
    // private static final String REGEX = "^CREATE\\s*?TABLE(.*?)\\;$";

    @Override
    public String getView() {
      return endMessage;
    }

    @Override
    public void execute() {
      // TODO

    }

  }



}
