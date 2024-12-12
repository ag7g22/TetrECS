package uk.ac.soton.comp1206.Utility;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Multimedia class is used to handle audio SFX and music playing anytime in the application
 */
public class Multimedia {

  private static final Logger logger = LogManager.getLogger(Multimedia.class);

  /**
   * Flag for is audio is enabled
   */
  private static boolean audioEnabled = true;

  /**
   * Flag if music is currently playing
   */
  private static boolean isPlayingMusic = false;

  /**
   * The MediaPlayer object
   */
  private static MediaPlayer musicPlayer;

  /**
   * Handling playing the audio file
   *
   * @param file audio file
   */
  public static void playAudio(String file) {
    if (!audioEnabled) {
      return;
    }

    //Path to audio file
    String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
    logger.info("Playing audio: " + toPlay);

    //Exception handling for playing the audio file
    try {
      Media play = new Media(toPlay);
      MediaPlayer audioPlayer = new MediaPlayer(play);
      audioPlayer.play();
    } catch (Exception e) {
      audioEnabled = false;
      e.printStackTrace();
      logger.error("Unable to play file, disabling audio");
    }
  }

  /**
   * Handling playing music
   *
   * @param file the music file
   */
  public static void playMusic(String file) {
    boolean musicEnabled = true;
    if (!musicEnabled) {
      return;
    }

    //Stops currently playing track when method is called again by another screen
    if (isPlayingMusic) {
      musicPlayer.stop();
      isPlayingMusic = false;
      logger.info("Stopping current music track");
    }

    //Path to music file
    String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
    logger.info("Playing audio: " + toPlay);

    //Exception handling playing the music file
    try {
      Media play = new Media(toPlay);
      musicPlayer = new MediaPlayer(play);
      musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
      musicPlayer.play();
      isPlayingMusic = true;
    } catch (Exception e) {
      audioEnabled = false;
      e.printStackTrace();
      logger.error("Unable to play file, disabling audio");
    }
  }

  /**
   * Pauses the music for the pause screen
   */
  public static void pauseMusic() {
    if (isPlayingMusic) {
      musicPlayer.pause();
      isPlayingMusic = false;
    } else {
      musicPlayer.play();
      isPlayingMusic = true;
    }
  }

}
