package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import tilemap.TileMap;

public class FireBall extends MapObject {

	private boolean hit;
	private boolean remove;
	private BufferedImage[] sprites;
	private BufferedImage[] hitSprites;

	public FireBall(TileMap tileMap, boolean right) {
		super(tileMap);

		moveSpeed = 3.8;
		if (right) {
			dx = moveSpeed;
		} else {
			dx = -moveSpeed;
		}
		// for sprites
		width = 30;
		height = 30;
		// for collision
		colWidth = 14;
		colHeight = 14;

		// load sprites
		try {
			// get the sprite sheet
			BufferedImage spriteSheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fireball.gif"));
			// store "regular" sprites
			sprites = new BufferedImage[4];
			for(int i = 0; i < sprites.length; i++) {
				sprites[i] = spriteSheet.getSubimage(i * width, 0, width, height);
			}
			
			// store the "hit" sprites
			hitSprites = new BufferedImage[3];
			for(int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spriteSheet.getSubimage(i * width, height, width, height);
			}
			
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(70);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setHit() {
		if(hit) {
			return;
		}
		hit = true;
		animation.setFrames(hitSprites);
		animation.setDelay(70);
		dx = 0;
	}
	
	public boolean shouldRemove() {
		return remove;
	}
	
	public void update() {
		checkTileMapCollision();
		setPosition(xTemp, yTemp);
		
//		if(dx == 0 && !hit) { // why check for not it?
		if(dx == 0) {
			setHit();
		}
		
		animation.update();
		if(hit && animation.hasPlayedOnce()) {
			remove = true;
		}
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		
		if (facingRight) {
			g.drawImage(animation.getImage(), (int) (x + xMap - width / 2), (int) (y + yMap - height / 2), null);
			g.setColor(Color.RED);
			g.drawRect((int) (x + xMap - width / 2), (int) (y + yMap - height / 2), animation.getImage().getWidth(), animation.getImage().getHeight());
		} else {
			g.drawImage(animation.getImage(), (int) (x + xMap - width / 2 + width), (int) (y + yMap - height / 2), -width, height, null);
			g.setColor(Color.RED);
			g.drawRect((int) (x + xMap - width / 2), (int) (y + yMap - height / 2), width, height);
		}
	}
}
