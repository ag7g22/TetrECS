package uk.ac.soton.comp1206.event;

/**
 * The Game Loop Listener is used for the timer UI in the Challenge/Multiplayer scene how much time
 * is left to play a piece
 */
public interface GameLoopListener {

  /**
   * Handle game Loop event
   *
   * @param time current time left
   */
  void timerListener(double time);

}
