package org.mo39.fmbh.databasedesign.utils;

import org.mo39.fmbh.databasedesign.dao.SupportedCmds;

public class View {
  
  public View(Class<SupportedCmds> class1) {
    // TODO Auto-generated constructor stub
  }

  public static interface Viewable {
     
    String getCliView();
    
  }

}
