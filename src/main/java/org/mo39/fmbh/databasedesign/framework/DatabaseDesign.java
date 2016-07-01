package org.mo39.fmbh.databasedesign.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mo39.fmbh.databasedesign.executor.Executable;
import org.mo39.fmbh.databasedesign.executor.Executable.RequiresActiveSchema;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;
import org.mo39.fmbh.databasedesign.model.Cmd;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DataType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DatabaseDesign {

  /**
   * Initialize.
   *
   */
  @SuppressWarnings("unchecked")
  public DatabaseDesign() {
    // ----------------------
    @SuppressWarnings("resource")
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    // ----------------------
    Cmd.setCmdList((List<Cmd>) ctx.getBean("supportedCmdList"));
    DataType.setDataTypeList((List<DataType>) ctx.getBean("supportedDataTypeList"));
    Constraint.setConstraintList((List<Constraint>) ctx.getBean("supportedConstraintList"));
    SystemProperties.setSystemProperties((Map<String, String>) ctx.getBean("systemProperties"));
  }

  private static Scanner scan = new Scanner(System.in);

  /**
   * Run command. The method would set the current cmd in Status to the input cmd.
   * <p>
   * The method provides several features:<br>
   * &emsp;- pre-execution check<br>
   * &emsp;- view result according to Viewable interface (if implemented) right after the execution
   * finishes<br>
   * &emsp;- catch certain execeptions, display the message and consider the execution a failure (in
   * which case the Viewable result will not be displayed).<br>
   *
   */
  public void runCmd(Cmd cmd) {
    Status.setCurrentCmd(cmd);
    try {
      Class<?> klass = Class.forName(cmd.getExecutorClassName());
      if (Executable.class.isAssignableFrom(klass)) {
        // ----------------------
        Executable executor = Executable.class.cast(klass.newInstance());
        Method method = executor.getClass().getMethod("execute");
        // ----------------------
        if (checkAnnotation(method)) {
          method.invoke(executor);
          if (executor instanceof Viewable) {
            View.newView(Viewable.class.cast(executor));
          }
          InfoSchema.validate();
        }
      }
    } catch (InvocationTargetException e) {
      Throwable ex;
      if ((ex = e.getCause()) instanceof BadUsageException) {
        String message = ex.getMessage();
        if (message == null) {
          message = "";
        }
        View.newView("Bad usage for '" + cmd.getName() + "'. " + message);
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    } finally {
      Status.endRunCmd();
    }
  }

  /**
   * Response to "-a" or "--all" option
   *
   * @param dbDesign
   */
  public static void optionAll(DatabaseDesign dbDesign) {
    String lineBreak = SystemProperties.get("lineBreak");
    String tab = SystemProperties.get("tab");
    StringBuilder sb = new StringBuilder();
    sb.append("Supported commands: " + lineBreak);
    for (Cmd cmd : Cmd.getCmdList()) {
      sb.append(tab + cmd.getName() + " " + lineBreak + tab + tab + "- " + cmd.getDescription()
          + lineBreak);
    }
    sb.append("Supported Data Types: " + lineBreak);
    for (DataType type : DataType.getDataTypeList()) {
      sb.append(
          tab + type.getName() + lineBreak + tab + tab + "- " + type.getDescription() + lineBreak);
    }
    sb.append("Supported constraints: " + lineBreak);
    for (Constraint constraint : Constraint.getConstraintList()) {
      sb.append(tab + constraint.getName() + lineBreak + tab + tab + "- "
          + constraint.getDescription() + lineBreak);
    }
    sb.append(SystemProperties.get("usageInstruction"));
    View.newView(sb.toString());
  }

  /**
   * Response to "-r" or "--run" option
   *
   * @param dbDesign
   */
  public static void optionRun(DatabaseDesign dbDesign) {
    View.newView(SystemProperties.get("welcome"));
    while (true) {
      View.newView(SystemProperties.get("prompt"));
      StringBuilder arg = new StringBuilder();
      String query = null;
      for (int i = 0; i <= 10; i++) {
        arg.append(scan.nextLine() + " ");
        if ((query = arg.toString().trim()).endsWith(";")) {
          break;
        }
      }
      Cmd cmd = Cmd.supports(query);
      if (cmd != null) {
        dbDesign.runCmd(cmd);
      } else {
        View.newView("Unsupported Operation.");
      }
    }
  }

  private static boolean checkAnnotation(Method method) {
    if (method.getAnnotation(RequiresActiveSchema.class) != null) {
      if (!Status.hasActiveSchema()) {
        View.newView("Illegal state. No active schema is found");
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {
    DatabaseDesign dbDesign = new DatabaseDesign();
    InfoSchema.init();
    InfoSchema.validate();
    // ----------------------
    Options opts = new Options();
    CommandLineParser parser = new DefaultParser();
    opts.addOption("h", "help", false, "Help description.");
    opts.addOption("r", "run", false, "Start to run database.");
    opts.addOption("a", "all", false, "Show all supported Database commands.");
    // ----------------------
    CommandLine cl = null;
    try {
      cl = parser.parse(opts, args);
    } catch (ParseException e) {
      DBExceptions.newError(e);
    }
    // ----------------------
    if (cl.hasOption('r')) {
      optionRun(dbDesign);
    } else if (cl.hasOption('a')) {
      optionAll(dbDesign);
    } else {
      new HelpFormatter().printHelp(" ", opts);
    }
  }

}


