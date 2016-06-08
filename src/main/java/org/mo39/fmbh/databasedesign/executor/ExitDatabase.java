package org.mo39.fmbh.databasedesign.executor;

import org.mo39.fmbh.databasedesign.framework.View;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;

public class ExitDatabase implements Executable, Viewable {

  @Override
  public String getView() {
    return "Exit database...";
  }

  @Override
  @ExitOperation
  public void execute() {
    View.newView(this);
    // TODO maybe I need to do something when exiting database.
    System.exit(0);
  }



}
