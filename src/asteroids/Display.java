package asteroids;

import javax.swing.*;
import java.awt.*;
import static asteroids.Constants.*;

/**
 * Defines the top-level appearance of an Asteroids game.
 */
@SuppressWarnings("serial")
public class Display extends JFrame {
	// The area where the action takes place
	private Screen screen;

	// -VinC added, store the highest score for enhanced mode
	private static int highestScore;

	// -M -VinC added, this JLabel is scoreLabel
	private JLabel scoreLabel;

	// -VinC store the controller
	private Controller controller;

	/**
	 * Lays out the game and creates the controller
	 */
	public Display(Controller controller) {
		this.controller = controller; // -VinC added, store the controller

		// Title at the top
		setTitle(TITLE);
		setLocation(1000, 200); // VinC added, better testing position

		// Default behavior on closing
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// The main playing area and the controller
		screen = new Screen(controller);

		// This panel contains the screen to prevent the screen from being
		// resized
		JPanel screenPanel = new JPanel();
		screenPanel.setLayout(new GridBagLayout());
		screenPanel.add(screen);

		// This panel contains buttons and labels
		JPanel controls = new JPanel();

		// The button that starts the game
		JButton startGame = new JButton(START_LABEL);
		controls.add(startGame);

		// -M; the label that displays scores, lives and current level
		scoreLabel = new JLabel();
		controls.add(scoreLabel);

		// Organize everything
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(screenPanel, "Center");
		mainPanel.add(controls, "North");
		setContentPane(mainPanel);
		pack();

		// Connect the controller to the start button
		startGame.addActionListener(controller);
	}

	/**
	 * Called when it is time to update the screen display. This is what drives
	 * the animation.
	 */
	public void refresh() {
		screen.repaint();
	}

	/**
	 * Sets the large legend
	 */
	public void setLegend(String s) {
		screen.setLegend(s);
	}

	/**
	 * -VinC added, refresh the scoreLabel
	 */
	public void refreshScoreLabel() {
		int tmp = controller.getScore();
		String scoreBoard = " Level: " + controller.getLevel() + "  Lives: " + controller.getLives() + "  Score: "
				+ tmp;
		if (enhancedMode) {
			highestScore = tmp > highestScore ? tmp : highestScore;
			scoreBoard += " HighestScore: " + highestScore;
		}
		scoreLabel.setText(scoreBoard);
	}
}
