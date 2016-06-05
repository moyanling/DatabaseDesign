package org.mo39.fmbh.databasedesign.utils;

import java.util.Map;
import java.util.Scanner;

import org.mo39.fmbh.databasedesign.dao.SupportedCmds;

public class CliHandler {

  private static String prompt;
  private static String welcomeInfo;
  private static Map<String, String> cmdDescriptionMap;

  private static Scanner scan = new Scanner(System.in);

  public static void showCommandList() {
    print("Supported commands: \n");
    for (Object key : cmdDescriptionMap.keySet()) {
      print("\t" + key + ": \n\t\t" + cmdDescriptionMap.get(key) + "\n\n");
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
        args.append(scan.nextLine() + " ");
        if ((query = args.toString().trim()).endsWith(";")) {
          break;
        }
      }
      if (SupportedCmds.supports(query)) {
        SupportedCmds.runCmd();
      } else {
        throw new UnsupportedOperationException();
      }
    }
  }


  public void setWelcomeInfo(String welcomeInfo) {
    CliHandler.welcomeInfo = welcomeInfo;
  }

  public void setPrompt(String prompt) {
    CliHandler.prompt = prompt;
  }

  public void setCmdDescriptionMap(Map<String, String> cmdDescriptionMap) {
    CliHandler.cmdDescriptionMap = cmdDescriptionMap;
  }

  private static void print(Object obj) {
    System.out.print(obj);
  }
}
