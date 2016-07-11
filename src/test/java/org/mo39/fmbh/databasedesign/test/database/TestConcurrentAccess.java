package org.mo39.fmbh.databasedesign.test.database;

public class TestConcurrentAccess {

  public static void main(String[] args) {
     new Thread(() -> {
     TestRunExample.runExample(System.out);
     }).start();
     new Thread(() -> {
     TestRunExample.runExample(System.out);
     }).start();

  }

}
