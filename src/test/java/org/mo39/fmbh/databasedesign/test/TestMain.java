package org.mo39.fmbh.databasedesign.test;

import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mo39.fmbh.databasedesign.framework.Cmd;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.framework.View;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMain {

  private static Scanner scan = new Scanner(System.in);

  public static void main(String[] args) {
    @SuppressWarnings("resource")
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    DatabaseDesign dbDesign = ctx.getBean(DatabaseDesign.class);

    Options opts = new Options();
    CommandLineParser parser = new DefaultParser();
    opts.addOption("h", "help", false, "Help description.");
    opts.addOption("r", "run", false, "Start to run database.");
    opts.addOption("a", "all", false, "Show all supported Database commands.");

    CommandLine cl = null;
    try {
      cl = parser.parse(opts, args);
    } catch (ParseException e) {
      throw new RuntimeException("ParseException.");
    }

    if (cl.hasOption('r')) {
      optionRun(dbDesign);
    } else if (cl.hasOption('a')) {
      optionAll(dbDesign);
    } else {
      new HelpFormatter().printHelp(" ", opts);
    }
  }

  public static void optionAll(DatabaseDesign dbDesign) {
    StringBuilder sb = new StringBuilder("Supported commands: \n\n");
    for (Cmd cmd : dbDesign.getSupportedCmdList()) {
      sb.append("\t" + cmd.getName() + " \n\t\t" + cmd.getDescription() + "\n\n");
    }
    sb.append(dbDesign.getSystemProperties().get("usageInstruction"));
    View.newView(sb.toString());
  }

  public static void optionRun(DatabaseDesign dbDesign) {
    View.newView(dbDesign.getSystemProperties().get("welcome"));
    while (true) {
      View.newView(dbDesign.getSystemProperties().get("prompt"));
      StringBuilder arg = new StringBuilder();
      String query = null;
      for (int i = 0; i <= 10; i++) {
        arg.append(scan.nextLine() + " ");
        if ((query = arg.toString().trim()).endsWith(";")) {
          break;
        }
      }
      if (dbDesign.supports(query)) {
        dbDesign.runCmd();
      } else {
        View.newView("Unsupported Operation.");
      }
    }
  }

}
