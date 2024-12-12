package uk.ac.soton.comp1206.event;

import java.util.ArrayList;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * Line cleared Listener is needed when lines are cleared to be able to animate the fadeOut effect
 * on those given blocks
 */
public interface LineClearedListener {

  /**
   * Handle the line cleared event
   *
   * @param coordinates the cleared blocks
   */
  void lineCleared(ArrayList<GameBlockCoordinate> coordinates);

}
