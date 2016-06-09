package org.mo39.fmbh.databasedesign.executor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;

public class ExitDatabase implements Executable, Viewable {

  private static final String REGX = "^EXIT\\s*?\\;$";

  private static String exit_message;
  private static String cmdStr;

  @Override
  public String getView() {
    return exit_message;
  }

  @Override
  @ExitOperation
  public void execute() {
    cmdStr = Status.getInstance().getCurrentCmd();

    Pattern regx = Pattern.compile(REGX, Pattern.CASE_INSENSITIVE);
    Matcher matcher = regx.matcher(cmdStr);

    if (matcher.matches()) {
      exit_message = "Exit Database...";
      View.newView(this);
      System.exit(0);
    } else {
      throw new BadUsageException();
    }
  }
}