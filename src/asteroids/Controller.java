package asteroids;

import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;
import com.sun.prism.shader.AlphaOne_LinearGradient_AlphaTest_Loader;

import asteroids.participants.Alien;
import asteroids.participants.AlienBullet;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet_VinC;
import asteroids.participants.Ship;
import asteroids.participants.VinC_Loot;
import javafx.scene.input.KeyCode;
import sounds.Sound_VinC;
import static asteroids.Constants.*;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener {
	// The state of all the Participants
	private ParticipantState pstate;

	// The ship (if one is active) or null (otherwise)
	private Ship ship;

	// The alien ship (if one is active) or null (otherwise)
	private Alien alien;

	// When this timer goes off, it is time to refresh the animation
	private Timer refreshTimer;

	// -VinC added, when this timer goes off, it is time to place an alien
	private Timer placeAlienTimer;

	// The time at which a transition to a new stage of the game should be made.
	// A transition is scheduled a few seconds in the future to give the user
	// time to see what has happened before doing something like going to a new
	// level or resetting the current level.
	private long transitionTime;

	// Number of lives left
	private int lives;

	// The game display
	private Display display;

	// VinC added, ship turning left indicator
	private boolean left;

	// VinC added, ship turning right indicator
	private boolean right;

	// VinC added, ship fire indicator
	private boolean isShipMove;

	// VinC added, store the boolean of if thrust allow
	private boolean isThrustAllow;

	// VinC added, boolean of freeze the frame
	private boolean isFreeze;

	// -M; indicates point earned
	private int points;

	// -M VinC added, indicates current level
	private int level;

	// -VinC added, indicates whether switch to next level
	private boolean isNextLevel;

	// -VinC added, store the sound effect object
	private Sound_VinC sound;

	// -VinC added, bolean value indicates whether or not to play thrust sound
	private boolean isThrustSoundPlay;

	/**
	 * Constructs a controller to coordinate the game and screen
	 */
	public Controller() {
		// Initialize the ParticipantState and Sound effect
		pstate = new ParticipantState();
		sound = new Sound_VinC();

		// VinC added, initial ship's booleans of controller
		left = right = isShipMove = isThrustAllow = isThrustSoundPlay = isFreeze = isMultiFire = isUndeadShip = false;

		// -M VinC added, initial level
		level = 1;

		// -M added, initial points
		points = 0;

		// -VinC added, initial lives
		lives = 3;

		// // -M, initial points
		// points = 0;

		// -M VinC added, initial level boolean
		isNextLevel = false;

		// VinC added, Set up the refresh timer and placeAlienTimer
		refreshTimer = new Timer(FRAME_INTERVAL, this);
		placeAlienTimer = new Timer(ALIEN_DELAY, this);

		// Clear the transitionTime
		transitionTime = Long.MAX_VALUE;

		// Record the display object
		display = new Display(this);

		// Bring up the splash screen and start the refresh timer
		splashScreen();
		display.setVisible(true);
		refreshTimer.start();
	}

	/**
	 * returns the boolean value of isShipMove
	 */
	public boolean isThrustShow() {
		return isShipMove && isThrustAllow;
	}

	/**
	 * Returns the ship, or null if there isn't one
	 */
	public Ship getShip() {
		return ship;
	}

	/**
	 * Configures the game screen to display the splash screen
	 */
	private void splashScreen() {
		// Clear the screen, reset the level, and display the legend
		clear();
		display.setLegend("Asteroids");
		if (enhancedMode) {// VinC added, play enhanced mode menu music
			playSound(MENUMUSIC);
		}

		// Place four asteroids near the corners of the screen.
		placeAsteroids(level); // -M
	}

	/**
	 * The game is over. Displays a message to that effect.
	 */
	private void finalScreen() {
		display.setLegend(GAME_OVER);
		display.removeKeyListener(this);
		 
		// -VinC added, stop the alien sound
		if(alien.getSize() == 0){
			stopSound(SAUCERSMALL);
		}else {
			stopSound(SAUCERBIG);
		}
		
		// VinC added, in enhanced mode, play game over sound effect when show
		// final screen
		if(enhancedMode){
			playSound(GAMEOVER);
		}
	}

	/**
	 * Place a new ship in the center of the screen. Remove any existing ship
	 * first.
	 */
	private void placeShip() {
		// Place a new ship
		Participant.expire(ship);
		ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
		new ParticipantCountdownTimer(ship, "beat", INITIAL_BEAT);

		// VinC added, initial the ship's controller' boolean
		isShipMove = isThrustSoundPlay = isThrustAllow = left = right = false;

		addParticipant(ship);
		display.setLegend("");
	}

	/**
	 * Place a new alien ship in the center of the screen. Remove any existing
	 * ship first. -M VinC
	 */
	public void placeAlien(int size) {
		// System.out.println("called placeAlien");
		// Place a new Alien ship
		Participant.expire(alien);
		if (level == 2) { // lab for testing, levle >1 to level>0
			alien = new Alien(1, 4, 100, 100, this);
		}

		else if (level > 2) { // -m
			if (RANDOM.nextInt(2) == 0) {
				alien = new Alien(1, 4, 100, 100, this);
			} else {
				alien = new Alien(0, 7, 100, 100, this);
			}
		}

		if (level > 1) {
			addParticipant(alien);
		}
	}

	/**
	 * Places four asteroids near the corners of the screen. Gives them random
	 * velocities and rotations.
	 */
	private void placeAsteroids(int level) {
		// -M; adds addition asteroids from level 1 on (level 0 has 4 asteroids)
		for (int i = 0; i < level - 1; i++) {
			double asteroidX;
			double asteroidY;
			if (RANDOM.nextBoolean()) {
				asteroidX = EDGE_OFFSET;
			} else {
				asteroidX = SIZE - EDGE_OFFSET;
			}
			if (RANDOM.nextBoolean()) {
				asteroidY = EDGE_OFFSET;
			} else {
				asteroidY = SIZE - EDGE_OFFSET;
			}

			addParticipant(new Asteroid(RANDOM.nextInt(4), 2, asteroidX, asteroidY, SLOW_ASTEROID_SPEED, this));

			// double a = RANDOM.nextDouble();
			// double b = RANDOM.nextDouble();
			// int j = RANDOM.nextInt(2);
			//
			// if (j == 0) {
			// addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE / 2 - 30 -
			// a * SIZE / 3,
			// SIZE / 2 - 30 - a * SIZE / 3, 3, this));
			// }
			//
			// else {
			// addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE / 2 + 30 +
			// a * SIZE / 3,
			// SIZE / 2 + 30 + a * SIZE / 3, 3, this));
			// }
		}

		addParticipant(new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, SLOW_ASTEROID_SPEED, this));
		addParticipant(new Asteroid(1, 2, SIZE - EDGE_OFFSET, EDGE_OFFSET, SLOW_ASTEROID_SPEED, this));
		addParticipant(new Asteroid(2, 2, EDGE_OFFSET, SIZE - EDGE_OFFSET, SLOW_ASTEROID_SPEED, this));
		addParticipant(new Asteroid(3, 2, SIZE - EDGE_OFFSET, SIZE - EDGE_OFFSET, SLOW_ASTEROID_SPEED, this));
	}

	/**
	 * Clears the screen so that nothing is displayed
	 */
	private void clear() {
		pstate.clear();
		display.setLegend("");
		ship = null;
	}

	/**
	 * Sets things up and begins a new game.
	 */
	private void initialScreen() {
		// -M VinC added, initial level
		level = 1;

		// -M added, initial points
		points = 0;

		// -VinC added, initial lives
		lives = 3;

		// -VinC added, initial "switch to next level" indicator
		isNextLevel = false;

		// Clear the screen
		clear();

		// VinC added, stop menu music and play game background music in
		// enhanced mode
		if (enhancedMode) {
			stopSound(MENUMUSIC);
			playSound(BACKGROUNDMUSIC);
		}

		// Place four asteroids.
		placeAsteroids(level); // -M; level 0

		// Place the ship and initialize beat speed
		placeShip();
		ship.initialBeatSpeed(); // -VinC added

		// -VinC added, place loot in enhanced mode
		if (enhancedMode) {
			addParticipant(new VinC_Loot(300, 300, this));
		}

		// -VinC added, refresh scorelabel when new game start
		display.refreshScoreLabel();

		// Start listening to events (but don't listen twice)
		display.removeKeyListener(this);
		display.addKeyListener(this);

		// Give focus to the game screen
		display.requestFocusInWindow();
	}

	/**
	 * Adds a new Participant
	 */
	public void addParticipant(Participant p) {
		pstate.addParticipant(p);
	}

	/**
	 * The ship has been destroyed
	 */
	public void shipDestroyed() {
		// Null out the ship
		ship = null;

		// Display a legend
		display.setLegend("Ouch!");

		// Decrement lives
		lives--;
		display.refreshScoreLabel();

		// Since the ship was destroyed, schedule a transition
		scheduleTransition(END_DELAY);
	}

	/**
	 * -M VinC, The alien ship has been destroyed
	 */
	public void alienDestroyed() {
		// Null out the alien ship
		alien = null;
		placeAlienTimer.start();
	}

	/**
	 * An asteroid of the given size has been destroyed
	 */
	public void asteroidDestroyed(int size) {
	}

	/**
	 * Schedules a transition m msecs in the future
	 */
	private void scheduleTransition(int m) {
		// System.out.println("scheduleTransition");
		transitionTime = System.currentTimeMillis() + m;
	}

	/**
	 * This method will be invoked because of button presses and timer events.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// The start button has been pressed. Stop whatever we're doing
		// and bring up the initial screen
		if (e.getSource() instanceof JButton) {
			initialScreen();
		}

		// Time to refresh the screen and deal with keyboard input
		else if (e.getSource() == refreshTimer) { // every frame

			// VinC added, freeze screen when "1" is pressed
			if (isFreeze) {
				return;
			}

			// -M VinC added, check for next level
			// If all the asteroids are gone, schedule a transition (the
			// original asteroidDestroyed code)
			// -M; asteroids of all sizes destroyed
			if (pstate.countAsteroids(0) == 0 && pstate.countAsteroids(1) == 0 && pstate.countAsteroids(2) == 0
					&& !isNextLevel) {
				// -VinC added, used for signaling switch to next level
				isNextLevel = true;
				level++; // -M
				try {// catch exception when both last asteroid and ship
						// destroyed
					ship.setBeatShouldBePlay(false); // -VinC added, set to stop
					// playing the beat
				} catch (NullPointerException e1) {
				}
				display.refreshScoreLabel();
				scheduleTransition(END_DELAY);
			}

			// VinC added, these if-statements change the action of ship by
			// responding to key pressed, or apply built-in action to ship
			if (ship != null) {

				// VinC added, apply friction to ship until it stops
				ship.applyFriction(SHIP_FRICTION);

				// -VinC added, switch the isThrustAllow to make flash thrust
				// effect
				isThrustAllow = !isThrustAllow;

				// SHIP ROTATE
				if (left) {
					ship.turnLeft();
				} else if (right) {
					ship.turnRight();
				}
				// SHIP MOVE
				if (isShipMove) {
					ship.accelerate();
					if (!isThrustSoundPlay) {
						isThrustSoundPlay = true;
						playSound(THRUST);
					}
				}
				ship.refreshThrust();
			}

			// It may be time to make a game transition
			performTransition();

			// Move the participants to their new locations
			pstate.moveParticipants();

			// Refresh screen
			display.refresh();

		} else if (e.getSource() == placeAlienTimer) {
			placeAlienTimer.stop();
			// addParticipant(new Alien(1, 600, 300, this)); //use placeAlien
			// instead -m
			placeAlien(1);
		}
	}

	/**
	 * Returns an iterator over the active participants
	 */
	public Iterator<Participant> getParticipants() {
		return pstate.getParticipants();
	}

	/**
	 * If the transition time has been reached, transition to a new state
	 */
	private void performTransition() {
		// Do something only if the time has been reached
		if (transitionTime <= System.currentTimeMillis()) {
			// Clear the transition time
			transitionTime = Long.MAX_VALUE;

//			Participant.expire(alien);  // -m; expires the alien

			// If there are no lives left, the game is over. Show the final
			// screen.
			if (lives <= 0) {
				finalScreen();
			}

			// If the ship was destroyed, place a new one and continue
			else if (ship == null) {
				placeShip();
			}

			// -VinC added, If isNextLevel true(all asteroids were destroyed),
			// switch to next level
			if (isNextLevel) {
				placeShip();
				placeAsteroids(level);
				// -VinC added, initial beat speed when switch to next level
				ship.initialBeatSpeed();
				// VinC added, place alien ship in level 2+
				placeAlienTimer.start();
				if (alien != null) {
					System.out.println("stpo aline sound");
					Participant.expire(alien);
					if (alien.getSize() == 0) {
						stopSound(SAUCERSMALL);
					} else if (alien.getSize() == 1) {
						stopSound(SAUCERBIG);
					}
				}
				isNextLevel = false; // -VinC added
			}
		}
	}

	// -M; three getter methods for game statistics
	public int getLives() {
		return this.lives;
	}

	public int getScore() {
		return this.points;
	}

	public int getLevel() {
		return this.level;
	}

	/**
	 * VinC added(original version above), If a key of interest is pressed,
	 * record that it is down, and change corresponding boolean to be true
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// VinC added, lab, restart the game by press ALT key
		if (keyCode == KeyEvent.VK_BACK_SPACE && enhancedMode) {
			initialScreen();
			return;
		}

		if (ship != null) {
			// SHIP ROTATE
			if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
				left = true;
			} else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
				right = true;
			}
			// SHIP MOVE
			if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
				isShipMove = true;
			}
			// SHIP FIRE BULLET
			if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_SPACE) {
				sound.playSound(FIRE);

				int multiFireBulletNumber = 8;
				if (isMultiFire) {// VinC added, if-statement used for check
									// multi-fire
					for (int i = 0; i < 2 * multiFireBulletNumber; i++) {
						addParticipant(
								new Bullet_VinC(ship.getX(), ship.getY(), i * (Math.PI / multiFireBulletNumber), this));
					}
				} else {
					addParticipant(new Bullet_VinC(ship.getX(), ship.getY(), ship.getRotation(), this));
				}
			}
		}

	}

	/**
	 * Ignore these events.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		int keyCode = e.getKeyChar();
	}

	/**
	 * VinC added, boolean to be false when corresponding keys are released
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (ship != null) {
			// SHIP ROTATE
			if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A && ship != null) {
				left = false;
			} else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D && ship != null) {
				right = false;
			}
			// SHIP MOVE
			if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W && ship != null) {
				isShipMove = isThrustSoundPlay = false;
				stopSound(THRUST);
			}
		}

		// VinC added, used to freeze frame, exit game\system, multi-fire
		if (keyCode == KeyEvent.VK_1 && enhancedMode) {
			isFreeze = !isFreeze;
		} else if (keyCode == KeyEvent.VK_ESCAPE && enhancedMode) {
			System.exit(0);
		} else if (keyCode == KeyEvent.VK_2 && enhancedMode) {
			isMultiFire = !isMultiFire;
		} else if (keyCode == KeyEvent.VK_3 && enhancedMode) {
			isUndeadShip = !isUndeadShip;
		}
	}

	/**
	 * VinC added, gain points for destroyed enemy
	 */
	public void gainPoint(int pnts) { // pnts is gained point for destroyed
										// enemy
		this.points += pnts;
		display.refreshScoreLabel();
	}

	/**
	 * VinC added, play sound effect by using sound class
	 */
	public void playSound(int soundType) {
		sound.playSound(soundType);
	}

	/**
	 * VinC added, stop play sound of soundType
	 */
	public void stopSound(int soundType) {
		sound.stopSound(soundType);
	}

	/**
	 * -M, adds an alien bullets
	 */
	public void alienFired() {
		if (ship != null) {
			if (alien.getSize() == 0)  {
				double radian = Math.atan((ship.getY() - alien.getY()) / (ship.getX() - alien.getX()));
				if (ship.getX() < alien.getX()) {
					radian += Math.PI;
					addParticipant(new AlienBullet(alien.getX(), alien.getY(), radian, this));
				}
			}else{
				addParticipant(new AlienBullet(alien.getX(), alien.getY(), 2 * RANDOM.nextDouble() * Math.PI, this));
			}
		}
	}
}
