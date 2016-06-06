package org.mo39.fmbh.databasedesign.test;

import java.util.Scanner;

import javax.annotation.Resource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mo39.fmbh.databasedesign.model.SupportedCmds;
import org.mo39.fmbh.databasedesign.view.View;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DatabaseDesign {

  private String prompt;
  private String welcomeInfo;

  @Resource(name = "supportedCmds")
  private SupportedCmds supportedCmds;
  private static Scanner scan = new Scanner(System.in);

  public static void giveItAShot(String[] args) {
    @SuppressWarnings("resource")
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    DatabaseDesign dbDesign = ctx.getBean(DatabaseDesign.class);

    Options opts = new Options();
    CommandLineParser parser = new DefaultParser();
    opts.addOption("h", "help", false, "Help description.");
    opts.addOption("r", "run", false, "Start to run database.");
    opts.addOption("a", "all", false, "Show all supported SQL commands. All must end with ';'");

    CommandLine cl = null;
    try {
      cl = parser.parse(opts, args);
    } catch (ParseException e) {
    }

    if (cl.hasOption('r')) {
      dbDesign.optionRun();
    } else if (cl.hasOption('a')) {
      dbDesign.optionAll();
    } else {
      new HelpFormatter().printHelp("Show Options.", opts);
    }
  }

  private void optionAll() {
    View.newView(supportedCmds);
  }

  private void optionRun() {
    View.newView(welcomeInfo);
    new Thread(() -> {
      while (true) {
        View.newView(prompt);
        StringBuilder arg = new StringBuilder();
        String query = null;
        for (int i = 0; i <= 10; i++) {
          arg.append(scan.nextLine() + " ");
          if ((query = arg.toString().trim()).endsWith(";")) {
            break;
          }
        }
        if (supportedCmds.supports(query)) {
          supportedCmds.runCmd();
        } else {
          View.newView("UnsupportedOperationException()");
        }
      }
    }).start();
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }

  public void setWelcomInfo(String welcomeInfo) {
    this.welcomeInfo = welcomeInfo;
  }

  public static void main(String[] args) {
    giveItAShot(args);
  }

}


