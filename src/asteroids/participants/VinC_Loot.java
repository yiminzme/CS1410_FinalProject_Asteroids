package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.RandomAccess;

import asteroids.Controller;
import asteroids.Participant;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import static asteroids.Constants.*;

/**
 * Represents asteroids
 */
public class VinC_Loot extends Participant {
	// The outline of the Loot
	private Shape outline;

	// The game controller
	private Controller controller;

	/**
	 * Throws an IllegalArgumentException if size or variety is out of range.
	 * 
	 * Creates an asteroid of the specified variety (0 through 3) and size (0 =
	 * small, 1 = medium, 2 = large) and positions it at the provided
	 * coordinates with a random rotation. Its velocity has the given speed but
	 * is in a random direction.
	 */
	public VinC_Loot(double x, double y, Controller controller) {
		// Create the asteroid
		this.controller = controller;
		setPosition(x, y);
		setVelocity(1, 3);
		
		createAsteroidOutline();
	}

	@Override
	protected Shape getOutline() {
		return outline;
	}

	/**
	 * Creates the outline of the asteroid based on its variety and size.
	 */
	private void createAsteroidOutline() {
		// This will contain the outline
		Path2D.Double poly = new Path2D.Double();

		// Fill out
		poly.moveTo(4, 20);
		poly.lineTo(-4, 20);
		poly.lineTo(-4, 25);
		poly.lineTo(4, 25);
		poly.lineTo(4, 20);
		poly.lineTo(8, 20);
		poly.lineTo(8, -20);
		poly.lineTo(-8, -20);
		poly.lineTo(-8, 20);
		poly.closePath();
		poly.moveTo(-4, 11);
		poly.lineTo(4, 11);
		poly.moveTo(0, 15);
		poly.lineTo(0, 7);
		poly.moveTo(-3, -11);
		poly.lineTo(3, -11);
		poly.moveTo(-4, -21);
		poly.lineTo(4, -21);
		

		// Scale to the desired size
		double scale = 0.6;
		poly.transform(AffineTransform.getScaleInstance(scale, scale));

		// Save the outline
		outline = poly;
	}

	// -M VinC added, triggered when asteroid is destroyed
	public void asteroidDebris() {
		controller.addParticipant(new Debris(4, getX(), getY(), 3, controller));
		controller.addParticipant(new Debris(4, getX(), getY(), 1, controller));
		controller.addParticipant(new Debris(4, getX(), getY(), 2, controller));		
		controller.addParticipant(new Debris(4, getX(), getY(), 1, controller));
		controller.addParticipant(new Debris(4, getX(), getY(), 5, controller));
	}

	/**
	 * When an loot collides with an AsteroidDestroyer, it expires.
	 */
	@Override
	public void collidedWith(Participant p) {
		if (p instanceof AsteroidDestroyer) {
			// -M VinC added, debris animation
			asteroidDebris();

			// -VinC added, add points and play sound for destroying Asteroid
			// if(size==0){
			// controller.gainPoint(100);
			// }else if(size == 1){
			// controller.gainPoint(50);
			// }else if(size == 2){
			// controller.gainPoint(20);
			// }else{
			// controller.gainPoint(1);
			// }
//			controller.gainPoint(size == 0 ? 100 : (size == 1 ? 50 : (size == 2 ? 20 : 1)));
//			controller.playSound(2 + size);

			// Expire the asteroid
			Participant.expire(this);

			// Inform the controller
		}
	}
}
