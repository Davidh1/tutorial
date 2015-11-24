package gamestate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import tilemap.Background;

public class MenuState extends GameState {

	private Background background;
	private int currentChoice = 0;
	private String[] options = { "Start", "Help", "Quit" };

	private Color titleColor;
	private Font titleFont;
	private Font font;

	public MenuState(GameStateManager gameStateManager) {
		this.gameStateManager = gameStateManager;

		try {
			background = new Background("/Backgrounds/menubg.gif", 1);
			background.setVector(-0.1, 0); // scroll speed I think

			titleColor = new Color(128, 0, 0);
			titleFont = new Font("Century Gothic", Font.PLAIN, 28);
			font = new Font("Arial", Font.PLAIN, 12);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {

	}

	public void update() {
		background.update();
	}

	public void draw(Graphics2D g) {
		background.draw(g);
		
		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Dragon Tale", 80, 70);// TODO: don't hard code this
		
		// draw menu options
		g.setFont(font);
		for(int i = 0; i < options.length; i++) {
			if(i == currentChoice) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.RED);
			}
			g.drawString(options[i], 145, 140 + i * 15);// TODO: don't hard code this
		}
	}

	private void select() {
		if(currentChoice == 0) {
			// start
			gameStateManager.setState(GameStateManager.LEVEL_1_STATE);
		} else if(currentChoice == 1) {
			// help
		} else if(currentChoice == 2) {
			// quit
			System.exit(0);
		}
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ENTER) {
			select();
		} else if(k == KeyEvent.VK_UP) {
			currentChoice--;
			if(currentChoice < 0) {
				currentChoice = options.length - 1;
			}
		} else if(k == KeyEvent.VK_DOWN) {
			currentChoice++;
			if(currentChoice > options.length - 1) {
				currentChoice = 0;
			}
		}
	}

	public void keyReleased(int k) {

	}

}
