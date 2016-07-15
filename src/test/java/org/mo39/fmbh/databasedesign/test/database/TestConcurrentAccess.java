package org.mo39.fmbh.databasedesign.test.database;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestConcurrentAccess {

  public static void main(String[] args) throws IOException {
    OutputStream out1 =
        Files.newOutputStream(Paths.get(".\\archive", "thread1.txt"), StandardOpenOption.CREATE);
    PrintStream ps1 = new PrintStream(out1, true);

    OutputStream out2 =
        Files.newOutputStream(Paths.get(".\\archive", "thread2.txt"), StandardOpenOption.CREATE);
    PrintStream ps2 = new PrintStream(out2, true);

    new Thread(() -> {
      TestRunExample.runExample(ps1);
    }).start();
    new Thread(() -> {
      TestRunExample.runExample(ps2);
    }).start();

  }

}
