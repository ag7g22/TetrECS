package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.Game;

/**
 * The Game Listener is used to pass the final game state after a game has finished Ready to be used
 * by the ScoresScene
 */
public interface GameListener {

  /**
   * Handle when Game is finished
   *
   * @param game game object
   */
  void onGameCreated(Game game);

}
