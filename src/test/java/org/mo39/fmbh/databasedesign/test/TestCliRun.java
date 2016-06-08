package org.mo39.fmbh.databasedesign.test;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;

public class TestCliRun {
  public static void main(String[] args) {
    DatabaseDesign.giveItAShot(new String[] {"-r"});
  }
}
