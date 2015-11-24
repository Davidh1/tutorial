package main;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import gamestate.GameStateManager;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener{

	// dimensions
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 2;

	// game thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;

	// image
	private BufferedImage image;
	private Graphics2D g;
	
	// game state manager
	private GameStateManager gameStateManager;
	
	public GamePanel() {
		super(); //might not be required
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
	}
	
	// I'm not really sure what this does -- "Game Panel is done loading"
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init() {
		// this must be for the whole screen
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		running = true;
		
		gameStateManager = new GameStateManager();
	}
	
	public void run() {
		init();
		
		long start;
		long elapsed;
		long wait;
		
		// game loop -- this is the easier way to do it.  Look at Game3.0 for the other way
		while(running) {
			
			start = System.nanoTime();
			
			update();
			draw();
			drawToScreen();
			
			elapsed = System.nanoTime() - start;
			wait = targetTime - elapsed / 1000000;
			
			if( wait < 0) { 
				wait = 5;
			}
			
			try {
				Thread.sleep(wait);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void update() {
		gameStateManager.update();		
	}
	
	private void draw() {
		gameStateManager.draw(g);
	}
	
	private void drawToScreen() {
		Graphics g2 = getGraphics(); // Game Panel's graphics object
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}
	
	public void keyTyped(KeyEvent key) {}
	public void keyPressed(KeyEvent key) {
		gameStateManager.keyPressed(key.getKeyCode());
	}
	public void keyReleased(KeyEvent key) {
		gameStateManager.keyReleased(key.getKeyCode());
	}

}
