package uk.ac.soton.comp1206.event;

/**
 * The Game Over Listener is needed to let the ChallengeScene know the game is finished so that it
 * can display the gameOver Screen
 */
public interface GameOverListener {

  /**
   * Handle event when game is finished
   */
  void gameFinished();

}
