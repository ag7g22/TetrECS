package uk.ac.soton.comp1206.component;

import javafx.beans.NamedArg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a row and column representation of a block in the grid. Holds the x (column) and y
 * (row).
 * <p>
 * Useful for use in a set or list or other form of collection.
 */
public class GameBlockCoordinate {

  private static final Logger logger = LogManager.getLogger(GameBlockCoordinate.class);
  /**
   * Represents the column
   */
  private final int x;

  /**
   * Represents the row
   */
  private final int y;

  /**
   * A hash is computed to enable comparisons between this and other GameBlockCoordinates.
   */
  private int hash = 0;


  /**
   * Create a new GameBlockCoordinate which stores a row and column reference to a block
   *
   * @param x column
   * @param y row
   */
  public GameBlockCoordinate(@NamedArg("x") int x, @NamedArg("y") int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Return the column (x)
   *
   * @return column number
   */
  public int getX() {
    return x;
  }

  /**
   * Return the row (y)
   *
   * @return the row number
   */
  public int getY() {
    return y;
  }

  /**
   * Add a row and column reference to this one and return a new GameBlockCoordinate
   *
   * @param x additional columns
   * @param y additional rows
   * @return a new GameBlockCoordinate with the result of the addition
   */
  public GameBlockCoordinate add(int x, int y) {
    return new GameBlockCoordinate(
        getX() + x,
        getY() + y);
  }

  /**
   * Add another GameBlockCoordinate to this one, returning a new GameBlockCoordinate
   *
   * @param point point to add
   * @return a new GameBlockCoordinate with the result of the addition
   */
  public GameBlockCoordinate add(GameBlockCoordinate point) {
    return add(point.getX(), point.getY());
  }

  /**
   * Compare this GameBlockCoordinate to another GameBlockCoordinate
   *
   * @param obj other object to compare to
   * @return true if equal, otherwise false
   */
  @Override
  public boolean equals(Object obj) {
      if (obj == this) {
          return true;
      }
      if (obj instanceof GameBlockCoordinate other) {
          return getX() == other.getX() && getY() == other.getY();
      } else {
          return false;
      }
  }

  /**
   * Calculate a hash code of this GameBlockCoordinate, used for comparisons
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    if (hash == 0) {
      long bits = 7L;
      bits = 31L * bits + Double.doubleToLongBits(getX());
      bits = 31L * bits + Double.doubleToLongBits(getY());
      hash = (int) (bits ^ (bits >> 32));
    }
    return hash;
  }

  /**
   * Return a string representation of this GameBlockCoordinate
   *
   * @return string representation
   */
  @Override
  public String toString() {
    return "GameBlockCoordinate [x = " + getX() + ", y = " + getY() + "]";
  }

}
