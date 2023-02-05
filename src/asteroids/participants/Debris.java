package asteroids.participants;

import static asteroids.Constants.DEBRIS_SPEED;

import java.awt.Shape;
import java.awt.geom.Path2D;

import asteroids.Controller;
import asteroids.Participant;
import asteroids.destroyers.ShipDestroyer;

public class Debris extends Participant {

	// The outline of the asteroid
	private Shape outline;

	// The game controller
	private Controller controller;

	/**
	 * Throws an IllegalArgumentException if size or variety is out of range.
	 * 
	 * Creates debris of the specified variety (0 through 1) and positions of it
	 * at the provided coordinates with a random rotation. Its velocity has the
	 * given speed but is in a random direction.
	 */
	public Debris(int variety, double x, double y,
			double rotation, Controller controller) {
		
		// Make sure size variety is valid
		if (variety < 0 || variety > 4) {
			throw new IllegalArgumentException("Invalid debris variety: " + variety);
		} 
		
		// Create the debris
		this.controller = controller;
		setPosition(x, y);
		setVelocity(DEBRIS_SPEED, rotation);
		createdEBRISOutline(variety);
	}

	@Override
	protected Shape getOutline() {
		return outline;
	}

	/**
	 * Creates the outline of the asteroid based on its variety and size.
	 */
	private void createdEBRISOutline(int variety) {
		// This will contain the outline
		Path2D.Double poly = new Path2D.Double();

		// Fill out according to variety -M
		if (variety == 0) { // ship debris1
			poly.moveTo(2, 0);
			poly.lineTo(1, 8);
		}
		else if (variety == 1) { // ship debris2
			poly.moveTo(2, 0);
			poly.lineTo(3, 8);
		}
		else if (variety == 2) { // ship debris3
			poly.moveTo(0, 0);
			poly.lineTo(0, 3);

			poly.closePath();
		}
		else if (variety == 3) { // ship debris4
			poly.moveTo(1, 0);
			poly.lineTo(2, 7);
		}

		else if (variety == 4) { // asteroid debris
			poly.moveTo(0, 0);
			poly.lineTo(0, 0.4);

		}

		// Save the outline
		outline = poly;
	}

	/**
	 * VinC added, When the bullet collides with a ShipDestroyer, it expires.
	 */
	@Override
	public void collidedWith(Participant p) {
		if (p instanceof ShipDestroyer) {
			// Expire the bullet
			Participant.expire(this);
		}
	}

	/**
	 * VinC added, This method is called when a
	 * {@link asteroids.ParticipantCountdownTimer} that was constructed for this
	 * Participant completes its countdown, so long as this Participant is not
	 * expired. This method makes the ship to be expire
	 */
	public void countdownComplete(Object payload) {
		Participant.expire(this);
	}

}
