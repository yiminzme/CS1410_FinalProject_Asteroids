package asteroids;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import asteroids.participants.AlienBullet;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet_VinC;
import asteroids.participants.Debris;
import static asteroids.Constants.*;

/**
 * Keeps track of the Participants, their motions, and their collisions.
 */
public class ParticipantState {
	// The participants (asteroids, ships, etc.) that are involved in the game
	private LinkedList<Participant> participants;

	// Participants that are waiting to be added to the game
	private Set<Participant> pendingAdds;

	// VinC added, the number of bullets on screen
	private int bulletsCount;

	/**
	 * Creates an empty ParticipantState.
	 */
	public ParticipantState() {
		// No participants at the start
		participants = new LinkedList<Participant>();
		pendingAdds = new HashSet<Participant>();

		// VinC added, No bulletsCount at the start
		bulletsCount = 0;
	}

	/**
	 * Clears out the state.
	 */
	public void clear() {
		pendingAdds.clear();
		for (Participant p : participants) {
			Participant.expire(p);
		}
		participants.clear();

		// VinC added, clear the bulletsCount
		bulletsCount = 0;
	}

	/**
	 * Adds a new Participant
	 */
	public void addParticipant(Participant p) {
		// VinC added, this if-statement used to limit the number of bullets on
		// the screen
		if (p instanceof Bullet_VinC && bulletsCount >= BULLET_LIMIT && !isMultiFire) {
			return;
		}

		pendingAdds.add(p);
	}

	/**
	 * Returns an iterator over the active participants
	 */
	public Iterator<Participant> getParticipants() {
		return participants.iterator();
	}

	/**
	 * Returns the number of asteroids that are active participants
	 */
	public int countAsteroids(int size) // -M; adds size parameter
	{
		int count = 0;
		for (Participant p : participants) { 
			// -M; additional criterion (size) added
			if (p instanceof Asteroid && !p.isExpired() && ((Asteroid) p).getSize() == size)
			{
				count++;
			}
		}
		for (Participant p : pendingAdds) {
			// -M; additional criterion (size) added
			if (p instanceof Asteroid && !p.isExpired() && ((Asteroid) p).getSize() == size) 
			{
				count++;
			}
		}
		return count;
	}

	/**
	 * Moves each of the active participants to simulate the passage of time.
	 */
	public void moveParticipants() {
		// Move all of the active participants
		for (Participant p : participants) {
			if (!p.isExpired()) {
				p.move();
			}
		}

		// If there have been any collisions, deal with them. This may result
		// in new participants being added or old ones expiring. We save those
		// changes until after all of the collisions have been processed.
		checkForCollisions();

		// Deal with pending adds and expirations
		completeAddsAndRemoves();
	}

	/**
	 * Completes any adds and removes that have been requested.
	 */
	private void completeAddsAndRemoves() {

		// Note: These updates are saved up and done later to avoid modifying
		// the participants list while it is being iterated over
		for (Participant p : pendingAdds) {
			// VinC added, when bullet add, add 1 to bulletsCount and set timer
			// for bullet duration
			if (p instanceof Bullet_VinC) { // -m
				bulletsCount++;
				new ParticipantCountdownTimer(p, BULLET_DURATION);
			}

			// -M
			else if (p instanceof AlienBullet) {
				new ParticipantCountdownTimer(p, BULLET_DURATION);
			}

			// -M
			else if (p instanceof Debris) {
				new ParticipantCountdownTimer(p, DEBRIS_DURATION);
			}

			participants.add(p);
		}
		pendingAdds.clear();

		Iterator<Participant> iter = participants.iterator();
		while (iter.hasNext()) {
			Participant p = iter.next();
			if (p.isExpired()) {
				iter.remove();

				// VinC added, remove the number of bullets that is expired
				if (p instanceof Bullet_VinC) {
					bulletsCount--;
				}
			}
		}
	}

	/**
	 * Compares each pair of elements to detect collisions, then notifies all
	 * listeners of any found. Deals with each pair only once. Never deals with
	 * (p1,p2) and then again with (p2,p1).
	 */
	private void checkForCollisions() {
		for (Participant p1 : participants) {
			if (!p1.isExpired()) {
				Iterator<Participant> iter = participants.descendingIterator();
				while (iter.hasNext()) {
					Participant p2 = iter.next();
					if (p1 == p2)
						break;
					if (!p2.isExpired() && p1.overlaps(p2)) {
						p1.collidedWith(p2);
						p2.collidedWith(p1);
					}
					if (p1.isExpired())
						break;
				}
			}
		}
	}
}
