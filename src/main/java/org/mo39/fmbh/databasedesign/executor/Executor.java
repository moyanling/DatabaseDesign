package org.mo39.fmbh.databasedesign.executor;

import org.mo39.fmbh.databasedesign.model.Status;

public abstract class Executor {
  
  private Status statusInstance;
  
  Status getStatus() {
    return this.statusInstance;
  }
  
  void setStatus(Status statusInstance) {
    this.statusInstance = statusInstance;
  }
  
  abstract void execute();

}
