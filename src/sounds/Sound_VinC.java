package sounds;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import asteroids.Controller;
import static asteroids.Constants.*;

/**
 * Demonstrates how to put sound files into a project so that they will be
 * included when the project is exported, and demonstrates how to plan sounds.
 * 
 * @author Vincent Cheng
 */
@SuppressWarnings("serial")
public class Sound_VinC {

	/** A Clip that, when played, sounds like a AlienShip Crashed */
	private Clip bangAlienShipClip;

	/** A Clip that, when played, sounds like a large asteroid destroyed */
	private Clip bangLargeClip;

	/** A Clip that, when played, sounds like a medium asteroid destroyed */
	private Clip bangMediumClip;

	/** A Clip that, when played, sounds like a ship destroyed */
	private Clip bangShipClip;

	/** A Clip that, when played, sounds like a small asteroid destroyed */
	private Clip bangSmallClip;

	/** A Clip that, when played repeatedly, sounds like background music */
	private Clip beat1Clip;

	/** A Clip that, when played repeatedly, sounds like background music */
	private Clip beat2Clip;

	/** A Clip that, when played, sounds like a weapon being fired */
	private Clip fireClip;

	/** A Clip that, when played, sounds like a big saucer flying */
	private Clip saucerBigClip;

	/** A Clip that, when played, sounds like a small saucer flying */
	private Clip saucerSmallClip;

	/** A Clip that, when played, sounds like a ship is thrusting */
	private Clip thrustClip;

	/** VinC added, A Clip of background music */
	private Clip backgroundMusic;

	/** VinC added, A Clip of menu music */
	private Clip menuClip;

	/** VinC added, A Clip of game over sound */
	private Clip gameOverClip;

	// clip holder which can hold all kinds of Clip
	private Clip clipHolder;

	/**
	 * Creates the demo.
	 */
	public Sound_VinC() {
		// We create the clips in advance so that there will be no delay
		// when we need to play them back. Note that the actual wav
		// files are stored in the "sounds" project.
		bangAlienShipClip = createClip("/sounds/bangAlienShip.wav");
		bangLargeClip = createClip("/sounds/bangLarge.wav");
		bangMediumClip = createClip("/sounds/bangMedium.wav");
		bangShipClip = createClip("/sounds/bangShip.wav");
		bangSmallClip = createClip("/sounds/bangSmall.wav");
		beat1Clip = createClip("/sounds/beat1.wav");
		beat2Clip = createClip("/sounds/beat2.wav");
		fireClip = createClip("/sounds/fire.wav");
		saucerBigClip = createClip("/sounds/saucerBig.wav");
		saucerSmallClip = createClip("/sounds/saucerSmall.wav");
		thrustClip = createClip("/sounds/thrust.wav");
		if (enhancedMode) {
			backgroundMusic = createClip("/sounds/VinC_backgroundMusic1.wav");
			menuClip = createClip("/sounds/VinC_menuMusic2.wav");
			gameOverClip = createClip("/sounds/VinC_gameOver.wav");
			fireClip = createClip("/sounds/VinC_fire2.wav");
		}
	}

	/**
	 * Creates an audio clip from a sound file.
	 */
	public Clip createClip(String soundFile) {
		// Opening the sound file this way will work no matter how the
		// project is exported. The only restriction is that the
		// sound files must be stored in a package.
		try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile))) {
			// Create and return a Clip that will play a sound file. There are
			// various reasons that the creation attempt could fail. If it
			// fails, return null.
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			return clip;
		} catch (LineUnavailableException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (UnsupportedAudioFileException e) {
			return null;
		}
	}

	/**
	 * set the clipHolder to correspond soundType
	 */
	public int setClipHolder(int soundType) {
		int loopTimes = 0;
		if (soundType == FIRE) {
			clipHolder = fireClip;
		} else if (soundType == BANGSMALL) {
			clipHolder = bangSmallClip;
		} else if (soundType == BANGMEDIUM) {
			clipHolder = bangMediumClip;
		} else if (soundType == BANGLARGE) {
			clipHolder = bangLargeClip;
		} else if (soundType == THRUST) {
			clipHolder = thrustClip;
			loopTimes = Integer.MAX_VALUE;
		} else if (soundType == BEAT1) {
			clipHolder = beat1Clip;
		} else if (soundType == BEAT2) {
			clipHolder = beat2Clip;
		} else if (soundType == SAUCERBIG) {
			clipHolder = saucerBigClip;
			loopTimes = Integer.MAX_VALUE;
		} else if (soundType == SAUCERSMALL) {
			clipHolder = saucerSmallClip;
			loopTimes = Integer.MAX_VALUE;
		} else if (soundType == BANGALIENSHIP) {
			clipHolder = bangAlienShipClip;
		} else if (soundType == BANGSHIP) {
			clipHolder = bangShipClip;
		} else if (enhancedMode) { // -VinC added, enhanced mode
			if (soundType == BACKGROUNDMUSIC) {
				clipHolder = backgroundMusic;
				loopTimes = 10;
			} else if (soundType == MENUMUSIC) {
				clipHolder = menuClip;
			} else if (soundType == GAMEOVER) {
				clipHolder = gameOverClip;
			} else {
				System.out.println("int soundType cann't found:34 " + soundType);
			}
		} else {
			System.out.println("int soundType cann't found:12 " + soundType);
		}
		return loopTimes;
	}

	/**
	 * Plays sound depending on the argument input
	 */
	public void playSound(int soundType) {
		try {
			int loopTimes = setClipHolder(soundType);

			if (clipHolder.isRunning()) {
				clipHolder.stop();
			}

			clipHolder.setFramePosition(0);
			clipHolder.loop(loopTimes);
		} catch (Exception e) {
		}

	}

	/**
	 * stop the sound of soundType
	 */
	public void stopSound(int soundType) {
		try {
			setClipHolder(soundType);

			if (clipHolder.isRunning()) {
				clipHolder.stop();
			}
		} catch (Exception e) {
		}
	}
}
