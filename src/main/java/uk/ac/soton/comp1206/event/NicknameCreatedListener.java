package uk.ac.soton.comp1206.event;

/**
 * Nickname Listener is needed to send the user's nickname to the server
 */
public interface NicknameCreatedListener {

  /**
   * Handle Nickname created event
   *
   * @param nickname user's nickname
   */
  void setOnNicknameCreated(String nickname);

}
