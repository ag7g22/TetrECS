package uk.ac.soton.comp1206.event;

/**
 * The Channel Created Listener is needed to call the display the Channel screen AFTER it has been
 * set up from the communicator messages
 */
public interface ChannelCreatedListener {

  /**
   * Handle a channel created event
   */
  void ChannelCreated();

}
