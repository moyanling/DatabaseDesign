package org.mo39.fmbh.databasedesign.executor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.CliView;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

public abstract class BasicSchemaOperationExecutor {

  public static class ShowSchemas implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {}

    @Override
    @CliView
    public String getView() {
      return "showSchemas";
    }
  }

  public static class Use implements Executable, Viewable {

    private static String schemaName;
    private static final String REGX = "^USE(.*?)\\;$";

    @Override
    @SchemaOperation
    public void execute() {
      String cmd = Status.getInstance().getCurrentCmd();
      Pattern regx = Pattern.compile(REGX, Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(cmd);
      if (matcher.matches()) {
        schemaName = matcher.group(1).trim();
        if (NamingUtils.checkNamingConventions(schemaName)) {
          // TODO Utils.getSchemaList();
          Status.getInstance().setCurrentSchema(schemaName);
        }
      } else {
        throw new BadUsageException();
      }
    }

    @Override
    @CliView
    public String getView() {
      return "Schema - " + schemaName + " is activated.";
    }
  }

  public static class CreateSchema implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {}

    @Override
    @CliView
    public String getView() {
      return Status.getInstance().getCurrentSchema();
    }
  }

}


