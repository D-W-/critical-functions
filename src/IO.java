/**
 * Created by Han Wang at 6/27/18.
 */

import java.io.*;


public class IO {
  public static BufferedReader getReader(String filename){
    File inFile = new File(filename);
    BufferedReader result = null;
    try {
      result = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static BufferedWriter getWriter(String filename) {
    File outFile = new File(filename);
    BufferedWriter result = null;
    try {
      result = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return result;
  }
}