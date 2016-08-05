package org.mo39.fmbh.databasedesign.test.database;

import java.io.IOException;
import java.io.PrintStream;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.Cmd;

public class TestRunExample {


  public static void main(String[] args) throws IOException {
    runExample();
  }

  /**
   * An example to run database. Since it's calling runCmd functions directly, other than input from
   * the cli, the prompt, cmd arg and other info will not be shown and all cmds are <b>NOT</b>
   * justified before execution. Make sure they are valid if they are modified.
   * <p>
   * Running these cmds will create a new table and insert the record. After that, the newly created
   * schema will be deleted.
   *
   * @param args
   */
  public static void runExample(PrintStream p) {
    String[] cmdArr = {
        "show schemas;",
        "create schema Zoo_Schema;",
        "use Zoo_Schema;",
        "CREATE TABLE Zoo (Animal_ID INT PRIMARY KEY, Name VARCHAR(20), Sector BYTE);",
        "insert into table Zoo values (1, tiger, 11);",
        "insert into table Zoo values (2, elephant, 11);",
        "insert into table Zoo values (3, monkey, 10);",
        "insert into table Zoo values (4, rabbit, 10);",
        "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo where name = tiger;",
        "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo where animal_ID = 1;",
        "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo where name = nope;",
        "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo where sector = 10;",
        "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo;",
//        "delete from Zoo where name = tiger;",
//        "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo;",
        "delete schema Zoo_Schema;",
        "show schemas;",
        "exit;"};
    DatabaseDesign dbDesign = new DatabaseDesign();
    DatabaseDesign.setPrinterToView(p);
    for (String cmd: cmdArr) {
      dbDesign.runCmd(Cmd.valueOf(cmd));
      p.println("\n===============================");
    }
  }

  public static void runExample() {
    runExample(System.out);
  }

}
