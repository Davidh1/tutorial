package gamestate;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Entity.Enemy;
import Entity.HUD;
import Entity.Player;
import Entity.Enemies.Slugger;
import main.GamePanel;
import tilemap.Background;
import tilemap.TileMap;

public class Level1State extends GameState {

	private TileMap tileMap;
	private Background background;
	private Player player;
	private ArrayList<Enemy> enemies;
	private HUD hud;
	
	public Level1State(GameStateManager gameStateManager) {
		this.gameStateManager = gameStateManager;
		init();
	}

	public void init() {

		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1-1.map");
		tileMap.setPosition(0, 0);

		background = new Background("/Backgrounds/grassbg1.gif", 0.1);

		player = new Player(tileMap);
		player.setPosition(100, 100);
		
		enemies = new ArrayList<Enemy>();
		Slugger slugger = new Slugger(tileMap);
		slugger.setPosition(100, 100);
		enemies.add(slugger);
		
		hud = new HUD(player);
		
	}

	public void update() {
		player.update();
		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(), GamePanel.HEIGHT / 2 - player.getY());
		
		// scroll background
		background.setPosition(tileMap.getX(), tileMap.getY());
		
		// update all enemies
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update();
		}
	}

	public void draw(Graphics2D g) {

		// draw background
		background.draw(g);

		// draw tile map
		tileMap.draw(g);

		// draw player
		player.draw(g);
		
		// draw enemies
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}
		
		// draw hud
		hud.draw(g);
	}

	public void keyPressed(int k) {
		if (k == KeyEvent.VK_LEFT) {
			player.setLeft(true);
		}
		if (k == KeyEvent.VK_RIGHT) {
			player.setRight(true);
		}
		if (k == KeyEvent.VK_UP) {
			player.setUp(true);
		}
		if (k == KeyEvent.VK_DOWN) {
			player.setDown(true);
		}
		if (k == KeyEvent.VK_W) {
			player.setJumping(true);
		}
		if (k == KeyEvent.VK_E) {
			player.setGliding(true);
		}
		if (k == KeyEvent.VK_R) {
			player.setScratching();
		}
		if (k == KeyEvent.VK_F) {
			player.setFiring();
		}
	}

	public void keyReleased(int k) {
		if (k == KeyEvent.VK_LEFT) {
			player.setLeft(false);
		}
		if (k == KeyEvent.VK_RIGHT) {
			player.setRight(false);
		}
		if (k == KeyEvent.VK_UP) {
			player.setUp(false);
		}
		if (k == KeyEvent.VK_DOWN) {
			player.setDown(false);
		}
		if (k == KeyEvent.VK_W) {
			player.setJumping(false);
		}
		if (k == KeyEvent.VK_E) {
			player.setGliding(false);
		}
	}
}
