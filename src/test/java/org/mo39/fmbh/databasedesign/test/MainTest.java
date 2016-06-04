package org.mo39.fmbh.databasedesign.test;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mo39.fmbh.databasedesign.utils.CliHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {
  
  public static void main(String[] args) throws ParseException {
    
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    
    Options opts = new Options();
    CommandLineParser parser = new DefaultParser();
    opts.addOption("h", "help", false, "Help description.");
    opts.addOption("r", "run", false, "Start to run database.");
    opts.addOption("a", "all", false, "Show all supported SQL commands.");

    CommandLine cl;
    cl = parser.parse(opts, args);
    if (cl.hasOption('r')) {
      CliHandler.welcomeToDb();
    } else if (cl.hasOption('a')) {
      CliHandler.showCommandList();
    } else {
      new HelpFormatter().printHelp("Show Options", opts);
    }
  }
  
}
