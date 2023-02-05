package asteroids.participants;

import static asteroids.Constants.ALIENSHIP_SCALE;
import static asteroids.Constants.RANDOM;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import asteroids.Controller;
import asteroids.Participant;
import asteroids.ParticipantCountdownTimer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import static asteroids.Constants.*;

public class Alien extends Participant implements AsteroidDestroyer,
		ShipDestroyer {

	// The size of the asteroid (0 = small, 1 = medium, 2 = large)
	private int size;

	// The outline of the asteroid
	private Shape outline;

	// The game controller
	private Controller controller;

	/**
	 * Throws an IllegalArgumentException if size or variety is out of range.
	 * 
	 * Creates an alien ship of the specified variety (0 through 3) and size (0
	 * = small, 1 = medium, 2 = large) and positions it at the provided
	 * coordinates with a random rotation. Its velocity has the given speed but
	 * is in a random direction.
	 */
	public Alien(int size, int speed,double x, double y, Controller controller) {
		// Make sure size and variety are valid
		if (size < 0 || size > 1) {
			throw new IllegalArgumentException("Invalid asteroid size: " + size);
		}

		// Create the alien ship
		this.controller = controller;
		this.size = size;
		setPosition(x, y);
		setVelocity(speed,  Math.PI * RANDOM.nextInt(2));
		setRotation(Math.PI);
		createAlienOutline(size);

		alienZigZag();
		alienFire();
	}

	/**
	 * This method is invoked when a ParticipantCountdownTimer completes its
	 * countdown. -m VinC 
	 */
	@Override
	public void countdownComplete(Object payload) {
		// Give a burst of acceleration, then schedule another
		// burst for 200 msecs from now.
		if (payload.equals("changeDirection")) {
			setDirection(Math.PI/6 * ((-1 + RANDOM.nextInt(3))));
			alienZigZag();
		}		
		// -m; informs controller that alien has fired. 
		if (payload.equals("fire")) {
			controller.alienFired();
			alienFire();
		}
	}
	 /**
	  * Changes alien direction every 5 seconds; -m 
	  */
	private void alienZigZag() {
		new ParticipantCountdownTimer(this, "changeDirection", 800);
	}
	
	private void alienFire() {
		new ParticipantCountdownTimer(this, "fire", 1000);
	}

	@Override
	protected Shape getOutline() {
		return outline;
	}

	/**
	 * Creates the outline of the asteroid based on its variety and size.
	 */
	private void createAlienOutline(int size) {
		// This will contain the outline
		Path2D.Double poly = new Path2D.Double();
		poly.moveTo(-10, 9);
		poly.lineTo(10, 9);
		poly.lineTo(20, 0);
		poly.lineTo(-20, 0);
		poly.lineTo(-10, 9);
		poly.lineTo(-5, 16);
		poly.lineTo(5, 16);
		poly.lineTo(10,9);
		poly.moveTo(-20, 0);
		poly.lineTo(-10, -8);
		poly.lineTo(10, -8);
		poly.lineTo(20, 0);
		poly.closePath();

		// Scale to the desired size
		double scale = ALIENSHIP_SCALE[size];
		poly.transform(AffineTransform.getScaleInstance(scale, scale));

		// Save the outline
		outline = poly;
		
		// -VinC added, play alien ship sound effect
		if(size == 1){
			controller.playSound(SAUCERBIG);
		}else if(size == 0){
			controller.playSound(SAUCERSMALL);
		}else {
			System.out.println("Alien class, cann't find the clip type");
		}
	}

	/**
	 * Returns the size of the ALIEN
	 */
	public int getSize() {
		return size;
	}

	// -M VinC added, triggered when asteroid is destroyed
	public void aliendDebris() {
		controller.addParticipant(new Debris(4, getX(), getY(), 3, controller));
		controller.addParticipant(new Debris(4, getX(), getY(), 1, controller));
		controller.addParticipant(new Debris(4, getX(), getY(), 2, controller));
		controller.addParticipant(new Debris(4, getX(), getY(), 1, controller));
		controller.addParticipant(new Debris(4, getX(), getY(), 5, controller));
	}

	/**
	 * When an alien collides with anything except itself, it expires.
	 */ 
	@Override
	public void collidedWith(Participant p) {
		if (p instanceof Ship || p instanceof Asteroid || p instanceof Bullet_VinC) { // || p instanceof Ship || p instanceof ShipDestroyer
			// -M VinC added, debris animation
			aliendDebris();

			// stop the alien sound effect and play bang sound
			if(size == 0){
				controller.stopSound(SAUCERSMALL);
			}else if(size == 1){
				controller.stopSound(SAUCERBIG);
			}else {
				System.out.println("Alien class, can't find the size");
			}
			controller.playSound(BANGALIENSHIP);
			
			// Expire the asteroid
			Participant.expire(this);
        	controller.gainPoint((size==1?200:1000));
			controller.alienDestroyed();
		}
	}
}
