package org.mo39.fmbh.databasedesign.executor;

import static org.mo39.fmbh.databasedesign.utils.NamingUtils.extractAndCheckName;

import java.util.Set;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.CliView;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.utils.IOUtils;

public abstract class BasicTableOperationExecutor {

  public static class ShowTables implements Executable, Viewable {

    private static String endMessage;

    @Override
    @CliView
    public String getView() {
      return endMessage;
    }

    @Override
    @TableOperation
    public void execute() {
      Set<String> tableSet = IOUtils.getTables(Status.getInstance().getCurrentSchema());
      String currentTable = Status.getInstance().getCurrentTable();
      StringBuilder sb = new StringBuilder("Show Tables: ");
      for (String tableName : tableSet) {
        sb.append("\n\t" + tableName + "\n");
      }
      sb.append("\nCurrently activated table: ");
      sb.append(currentTable == null ? "None" : currentTable);
      endMessage = sb.toString();
    }

  }

  public static class CreateTable implements Executable, Viewable {

    private static String endMessage;
    private static final String REGEX = "^CREATE\\s*?TABLE(.*?)\\;$";

    @Override
    @CliView
    public String getView() {
      return endMessage;
    }

    @Override
    @TableOperation
    public void execute() {
      // TODO

    }

  }



  public static class DropTable implements Executable, Viewable {

    private static String endMessage;
    private static final String REGEX = "^DROP\\s*?TABLE(.*?)\\;$";


    @Override
    @CliView
    public String getView() {
      // TODO Auto-generated method stub
      return endMessage;
    }

    @Override
    @TableOperation
    public void execute() {
      String tableName = extractAndCheckName(Status.getInstance().getCurrentCmd(), REGEX, 1);
      if (tableName == null) {
        throw new BadUsageException();
      }
      Set<String> tableSet = IOUtils.getTables(Status.getInstance().getCurrentSchema());
      if (tableSet.contains(tableName)) {
        // TODO IOUtils.deleteTable();
      }

    }

  }



}
