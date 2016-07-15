package org.mo39.fmbh.databasedesign.test.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.util.Calendar;

public class TestDBLocker {

  public static class Thread_writeFile extends Thread {
    @Override
    public void run() {
      Calendar calstart = Calendar.getInstance();
      File file = Paths.get(".\\archive", "f.txt").toFile();
      try {
        if (!file.exists()) {
          file.createNewFile();
        }

        // 对该文件加锁
        RandomAccessFile out = new RandomAccessFile(file, "rw");
        FileChannel fcout = out.getChannel();
        FileLock flout = null;
        while (true) {
          try {
            flout = fcout.tryLock();
            break;
          } catch (Exception e) {
            System.out.println("有其他线程正在操作该文件，当前线程休眠1000毫秒");
            sleep(1000);
          }

        }

        for (int i = 1; i <= 1000; i++) {
          sleep(10);
          StringBuffer sb = new StringBuffer();
          sb.append("这是第" + i + "行，应该没啥错哈 ");
          out.write(sb.toString().getBytes("utf-8"));
        }

        flout.release();
        fcout.close();
        out.close();
        out = null;
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      Calendar calend = Calendar.getInstance();
      System.out.println("写文件共花了" + (calend.getTimeInMillis() - calstart.getTimeInMillis()) + "秒");
    }
  }

  public static class Thread_readFile extends Thread {
    @Override
    public void run() {
      try {
        Calendar calstart = Calendar.getInstance();
        sleep(5000);
        File file = Paths.get(".\\archive", "f.txt").toFile();

        // 给该文件加锁
        RandomAccessFile fis = new RandomAccessFile(file, "rw");
        FileChannel fcin = fis.getChannel();
        FileLock flin = null;
        while (true) {
          try {
            flin = fcin.tryLock();
            break;
          } catch (Exception e) {
            System.out.println("有其他线程正在操作该文件，当前线程休眠1000毫秒");
            sleep(1000);
          }

        }
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while (fis.read(buf) != -1) {
          sb.append(new String(buf, "utf-8"));
          buf = new byte[1024];
        }

        System.out.println(sb.toString());

        flin.release();
        fcin.close();
        fis.close();
        fis = null;

        Calendar calend = Calendar.getInstance();
        System.out
            .println("读文件共花了" + (calend.getTimeInMillis() - calstart.getTimeInMillis()) + "秒");
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    // Path p = Paths.get(".\\archive", "f.txt");
    // @SuppressWarnings("resource")
    // RandomAccessFile raf = new RandomAccessFile(p.toFile(), "rw");
    // FileChannel fc = raf.getChannel();
    //
    // new Thread(() -> {
    // FileLock fl = null;
    // while (true) {
    // try {
    // fl = fc.tryLock();
    // if (fl != null) {
    // break;
    // }
    // } catch (OverlappingFileLockException e1) {
    // try {
    // Thread.sleep(100);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // try {
    // raf.writeChars("This is the line written by thread 1");
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // try {
    // if (fl != null) {
    // fl.release();
    // }
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }).start();
    //
    //
    // new Thread(() -> {
    // FileLock fl = null;
    // while (true) {
    // try {
    // fl = fc.tryLock();
    // if (fl != null) {
    // break;
    // }
    // } catch (OverlappingFileLockException e1) {
    // try {
    // Thread.sleep(100);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // try {
    // raf.writeChars("This is the line written by thread 2");
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // try {
    // if (fl != null) {
    // fl.release();
    // }
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }).start();
    Thread_writeFile thf3 = new Thread_writeFile();
    Thread_readFile thf4 = new Thread_readFile();
    thf3.start();
    thf4.start();

  }

}
