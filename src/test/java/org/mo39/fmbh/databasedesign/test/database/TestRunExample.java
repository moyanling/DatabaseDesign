package org.mo39.fmbh.databasedesign.test.database;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;
import org.mo39.fmbh.databasedesign.model.Cmd;

import com.google.common.collect.Lists;

public class TestRunExample {

  @Before
  public void setUp() throws Exception {
    bytes = new ByteArrayOutputStream();
    console = System.out;
    System.setOut(new PrintStream(bytes));
  }

  @After
  public void tearDown() throws Exception {
    System.setOut(console);
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
    DatabaseDesign dbDesign = new DatabaseDesign();
    for (String cmd : cmdList) {
      dbDesign.runCmd(Cmd.valueOf(cmd));
    }
  }

  public void runExample() {
    runExample(System.out);
  }


  PrintStream console = null;
  ByteArrayOutputStream bytes = null;
  DatabaseDesign dbDesign = new DatabaseDesign();

  @Test
  public void testRunExample() {
    bytes.reset();
    for (int i = 0; i < cmdList.size(); i++) {
      dbDesign.runCmd(Cmd.valueOf(cmdList.get(i)));
      String result = bytes.toString();
      bytes.reset();
      assertEquals(resultList.get(i), result.substring(0, result.length() - 2));
    }
    dbDesign.runCmd(Cmd.valueOf("delete schema Zoo_Schema;"));
  }


  public static List<String> cmdList = Lists.newArrayList(
    //@formatter:off
      "show schemas;", 
      "create schema Zoo_Schema;", 
      "use Zoo_Schema;",
      "CREATE TABLE Zoo (Animal_ID INT PRIMARY KEY, Name VARCHAR(20), Sector BYTE);",
      "insert into table Zoo values (1, tiger, 9);",
      "insert into table Zoo values (2, elephant, 10);",
      "insert into table Zoo values (3, monkey, 11);",
      "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo where animal_ID = 1;",
      "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo where name = elephant;",
      "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo;",
      "delete from Zoo where name = tiger;",
      "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo;",
      "update Zoo set sector = 10 where sector = 11;",
      "select org.mo39.fmbh.databasedesign.test.database.Animal from Zoo where sector = 10;"
    //@formatter:on
  );
  public static List<String> resultList = Lists.newArrayList(
    //@formatter:off    
      "Show Schemas: \n\tNone",
      "Schema - 'Zoo_Schema' is created.",
      "Schema - 'Zoo_Schema' is activated.",
      "Table - 'Zoo' is Created.",
      "Insertion done.",
      "Insertion done.",
      "Insertion done.",
      "Result: \n\tAnimal[animal_ID=1,name=tiger,sector=9]",
      "Result: \n\tAnimal[animal_ID=2,name=elephant,sector=10]",
      "Result: \n\tAnimal[animal_ID=1,name=tiger,sector=9]\n\tAnimal[animal_ID=2,name=elephant,sector=10]\n\tAnimal[animal_ID=3,name=monkey,sector=11]",
      "Record is deleted",
      "Result: \n\tAnimal[animal_ID=2,name=elephant,sector=10]\n\tAnimal[animal_ID=3,name=monkey,sector=11]",
      "Record is updated",
      "Result: \n\tAnimal[animal_ID=2,name=elephant,sector=10]\n\tAnimal[animal_ID=3,name=monkey,sector=10]"
    //@formatter:on
  );

}
