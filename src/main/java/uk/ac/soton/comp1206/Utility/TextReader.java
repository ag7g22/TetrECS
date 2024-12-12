package uk.ac.soton.comp1206.Utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * TextReader class is needed for reading from and writing to the scores.txt file and does the
 * Exception handling to be able to be used by the ScoresScene
 */
public class TextReader {

  /**
   * BufferedReader object stored here
   */
  protected final BufferedReader reader;

  /**
   * Creates a new TextReader object and exception handling if file doesn't exist
   *
   * @param name file name
   */
  public TextReader(String name) {
    try {
      this.reader = new BufferedReader(new FileReader(name));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the next line in the file
   *
   * @return current line being read
   */
  public String getLine() {
    try {
      return reader.readLine();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  /**
   * Checks if the file is ready to be read from
   *
   * @return if file is ready or not
   */
  public boolean fileIsReady() {
    boolean ready = false;
    try {
      if (reader.ready()) {
        ready = true;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ready;
  }
}
