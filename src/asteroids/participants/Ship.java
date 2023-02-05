package asteroids.participants;

import static asteroids.Constants.BANGSHIP;
import static asteroids.Constants.BEAT1;
import static asteroids.Constants.BEAT2;
import static asteroids.Constants.FASTEST_BEAT;
import static asteroids.Constants.INITIAL_BEAT;
import static asteroids.Constants.RANDOM;
import static asteroids.Constants.SHIP_ACCELERATION;
import static asteroids.Constants.SHIP_FRICTION;
import static asteroids.Constants.SPEED_LIMIT;
import static asteroids.Constants.THRUST;
import static asteroids.Constants.isUndeadShip;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import asteroids.Controller;
import asteroids.Participant;
import asteroids.ParticipantCountdownTimer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer {
	// The outline of the ship
	private Shape outline;

	// The outline with thrust of the ship
	private Shape outlineWithThrust;

	// The outline without thrust of the ship
	private Shape outlineWithoutThrust;

	// Game controller
	private Controller controller;

	// VinC added, ship's thrust indicator
	private boolean isThrustOn;

	// -VinC added, beat switch used for switch the play beat between beat1 with
	// beat2
	private static boolean beatSwitch;

	// -VinC added, integer used for storing the speed of beat
	private static int beatSpeed;

	// -VinC added, boolean to show if the background beat should be play
	private static boolean isBeatShouldBePlay;

	// Constructs a ship at the specified coordinates
	// that is pointed in the given direction.
	public Ship(int x, int y, double direction, Controller controller) {
		this.controller = controller;
		setPosition(x, y);
		setRotation(direction);

		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(20, 0);
		poly.lineTo(-20, 12);
		poly.lineTo(-13, 10);
		poly.lineTo(-13, -10);
		poly.lineTo(-20, -12);
		poly.closePath();
		outlineWithoutThrust = poly;

		// -VinC added, outlineWithThrust used to show thrust when up key is
		// pressed
		Path2D.Double poly1 = new Path2D.Double();
		poly1.moveTo(-13, 5);
		poly1.lineTo(-13, -5);
		poly1.lineTo(-25, 0);
		poly1.closePath();
		poly1.append(poly, false);
		outlineWithThrust = poly1;

		// -VinC added, initial the isThrustOn to be false
		isThrustOn = false;

		outline = outlineWithoutThrust;
	}

	/**
	 * -VinC added, use to show the thrust of ship
	 */
	public void refreshThrust() {
		outline = controller.isThrustShow() ? outlineWithThrust : outlineWithoutThrust;
	}

	/**
	 * Returns the x-coordinate of the point on the screen where the ship's nose
	 * is located.
	 */
	public double getXNose() {
		Point2D.Double point = new Point2D.Double(20, 0);
		transformPoint(point);
		return point.getX();
	}

	/**
	 * Returns the x-coordinate of the point on the screen where the ship's nose
	 * is located.
	 */
	public double getYNose() {
		Point2D.Double point = new Point2D.Double(20, 0);
		transformPoint(point);
		return point.getY();
	}

	@Override
	protected Shape getOutline() {
		return outline;
	}

	/**
	 * Customizes the base move method by imposing friction
	 */
	@Override
	public void move() {
		applyFriction(SHIP_FRICTION);
		super.move();
	}

	/**
	 * Turns right by Pi/16 radians
	 */
	public void turnRight() {
		rotate(Math.PI / 16);
	}

	/**
	 * Turns left by Pi/16 radians
	 */
	public void turnLeft() {
		rotate(-Math.PI / 16);
	}

	/**
	 * VinC added, Accelerates by SHIP_ACCELERATION if under limit speed
	 */
	public void accelerate() {
		if (getSpeed() <= SPEED_LIMIT) {
			accelerate(SHIP_ACCELERATION);
		}
	}

	/**
	 * When a Ship collides with a ShipKiller, it expires
	 */
	@Override
	public void collidedWith(Participant p) {
		if (p instanceof ShipDestroyer) {
			controller.addParticipant(new Debris(RANDOM.nextInt(4), getX(), getY(), 3, controller));
			controller.addParticipant(new Debris(RANDOM.nextInt(4), getX(), getY(), 1, controller));
			controller.addParticipant(new Debris(RANDOM.nextInt(4), getX(), getY(), 2, controller));

			// VinC added, if-statement used for switch on-off undeadShip
			if (!isUndeadShip) { // VinC added, check if it is undead ship
				// -VinC added, play ship bang and stop ship thrust sound effect
				// after ship is destroyed
				controller.playSound(BANGSHIP);
				controller.stopSound(THRUST);

				// Expire the ship from the game
				Participant.expire(this);

				// Tell the controller the ship was destroyed
				controller.shipDestroyed();
			}
		}
	}

	/**
	 * -VinC added, This method is invoked when a ParticipantCountdownTimer completes its
	 * countdown.
	 */
	@Override
	public void countdownComplete(Object payload) {
		if (((String)payload).equals("beat") && isBeatShouldBePlay) { // -VinC added, if beat should be play, play it
			int beatAcce = 30; // -VinC added, the accelerate of beat, which is
								// the
			// changing velocity of beatSpeed
			// -VinC added, play the background beat under the speed of
			// beatSpeed
			if (beatSwitch) {
				controller.playSound(BEAT2);
			} else {
				controller.playSound(BEAT1);

				if (beatSpeed > FASTEST_BEAT + beatAcce) {
					beatSpeed -= beatAcce;
					if (beatSpeed < FASTEST_BEAT + beatAcce) {
						beatSpeed = FASTEST_BEAT;
					}
				}
			}
			beatSwitch = !beatSwitch;
			new ParticipantCountdownTimer(this, "beat", beatSpeed); 
		}else if(((String)payload).equals("placeAlien")){
//			controller.addParticipant(new Alien(1, 400, 400, controller)); -m use placeAlien() instead
			controller.placeAlien(1);
		}
	}

	/**
	 * initial the BeatSpeed to the initial_beat speed
	 */
	public void initialBeatSpeed() {
		beatSpeed = INITIAL_BEAT;
		beatSwitch = false;
		isBeatShouldBePlay = true;
	}

	/**
	 * set the value of isBeatShouldBePlay
	 */
	public void setBeatShouldBePlay(boolean isBeatShouldBePlay) {
		this.isBeatShouldBePlay = isBeatShouldBePlay;
	}
}
