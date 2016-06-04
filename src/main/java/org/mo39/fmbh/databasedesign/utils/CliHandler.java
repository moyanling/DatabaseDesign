package org.mo39.fmbh.databasedesign.utils;

import java.util.Map;
import java.util.Scanner;

public class CliHandler {

  private static String prompt;
  private static String welcomeInfo;
  private static Map<String, String> cmdRegxMap;
  private static Map<String, String> cmdDescriptionMap;

  private static Scanner scan = new Scanner(System.in);

  public static void showCommandList() {
    for (Object key : cmdDescriptionMap.keySet()) {
      print("\t" + key + ": \t\t" + cmdDescriptionMap.get(key));
    }
  }



  public static void welcomeToDb() {
    // ---------------
    print(welcomeInfo);
    // ---------------
    while (true) {
      // ---------------
      print(prompt);
      // ---------------
      StringBuilder args = new StringBuilder();
      String query = null;
      for (int i = 0; i <= 10; i++) {
        args.append(scan.nextLine());
        if ((query = args.toString().trim()).endsWith(";")) {
          break;
        }
      }
      parseAndExecuteSQLCommand(query);
    }
  }

  /**
   * Parse the input arg according to regular expression in cmdRegxMap.
   * 
   * @param args
   */
  private static void parseAndExecuteSQLCommand(String args) {

    if (args.equalsIgnoreCase("EXIT")) {
      System.exit(0);
    } else if (args.equalsIgnoreCase("SHOW SCHEMAS")) {
      print("I'am showing schemas");
    } else if (args.startsWith("USE")) {
      print("I'm choosing a schema");
    } else if (args == "SHOW TABLES") {
      print("I'm showing tables");
    } else if (args.startsWith("CREATE SCHEMA")) {
    } else {
      throw new UnsupportedOperationException();
    }
  }



  public void setWelcomeInfo(String welcomeInfo) {
    CliHandler.welcomeInfo = welcomeInfo;
  }

  public void setPrompt(String prompt) {
    CliHandler.prompt = prompt;
  }

  public void setSupportedCmdMap(Map<String, String> supportedCmdMap) {
    CliHandler.cmdDescriptionMap = supportedCmdMap;
  }

  public void setCmdRegxMap(Map<String, String> cmdRegxMap) {
    CliHandler.cmdRegxMap = cmdRegxMap;
  }

  private static void print(Object obj) {
    System.out.print(obj);
  }
}
