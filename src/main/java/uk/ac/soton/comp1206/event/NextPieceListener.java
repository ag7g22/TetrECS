package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The NextPieceListener is needed to update the UI the queued pieces to be played
 */
public interface NextPieceListener {

  /**
   * Handle the Piece is placed event
   *
   * @param piece1 current Piece
   * @param piece2 following Piece
   */
  void nextPiece(GamePiece piece1, GamePiece piece2);

}
