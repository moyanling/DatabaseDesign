package org.mo39.fmbh.databasedesign.executor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;

public class ExitDatabase implements Executable, Viewable {

  private static final String REGX = "^EXIT\\s*?\\;$";

  private static String exit_message;
  private static String cmdStr;

  @Override
  public String getView() {
    return exit_message;
  }

  @Override
  @IsReadOnly
  public void execute() throws DBExceptions {
    cmdStr = Status.getCurrentCmdStr();

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
