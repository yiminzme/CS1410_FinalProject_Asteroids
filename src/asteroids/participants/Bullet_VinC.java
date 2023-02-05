package asteroids.participants;

import static asteroids.Constants.ASTEROID_SCALE;
import static asteroids.Constants.BULLET_SPEED;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import asteroids.Controller;
import asteroids.Participant;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;

/**
 * Represents bullet
 */
public class Bullet_VinC extends Participant implements AsteroidDestroyer
{
    // The outline of the asteroid
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
    public Bullet_VinC (double x, double y, double rotation, Controller controller)
    {
        // Create the bullet
        this.controller = controller;
        setPosition(x, y);
        setVelocity(BULLET_SPEED, rotation);
        createBulletOutline();
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Creates the outline of the asteroid based on its variety and size.
     */
    private void createBulletOutline ()
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Fill out according to variety
        	poly.moveTo(1, 1);
            poly.lineTo(-1, 1);
            poly.lineTo(-1, -1);
            poly.lineTo(1, -1);
            poly.closePath();

        // Scale to the desired size
        double scale = ASTEROID_SCALE[0];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        // Save the outline
        outline = poly;
    }
    
    /**
     * VinC added, When the bullet collides with a ShipDestroyer, it expires.
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the bullet
            Participant.expire(this);
        }
    }
    
    /**
     * VinC added, This method is called when a {@link asteroids.ParticipantCountdownTimer}
     * that was constructed for this Participant completes its countdown, so
     * long as this Participant is not expired. This method makes 
     * the ship to be expire
     */
    public void countdownComplete (Object payload)
    {
    	Participant.expire(this);
    }

}
