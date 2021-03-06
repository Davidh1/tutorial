package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import tilemap.TileMap;

public class Player extends MapObject {

	// player stuff
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;

	private boolean dead;
	private boolean flinching;
	private long flinchTimer;

	// fire ball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;

	// scratch
	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;

	// gliding
	private boolean gliding;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { // matches the animation actions
			2, 8, 1, 2, 4, 2, 5 };

	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;

	public Player(TileMap tileMap) {
		super(tileMap);		
		
		width = 30;
		height = 30;
		colWidth = 20;
		colHeight = 20;

		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.0;
		stopJumpSpeed = 0.3;

		facingRight = true;

		health = maxHealth = 5;// weird way to do this
		fire = maxFire = 2500;

		fireCost = 200;
		fireBallDamage = 5;
		fireBalls = new ArrayList<FireBall>();

		scratchDamage = 8;
		scratchRange = 40;// in pixels

		// load sprites
		try {
			BufferedImage spriteSheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/playersprites.gif"));
			
			sprites = new ArrayList<BufferedImage[]>();
			
			for (int i = 0; i < 7; i++) {// for each animation action
				BufferedImage[] bufferedImage = new BufferedImage[numFrames[i]];
				for (int j = 0; j < numFrames[i]; j++) {
					if (i != SCRATCHING) {
						bufferedImage[j] = spriteSheet.getSubimage(j * width, i * height, width, height);
					} else {
						bufferedImage[j] = spriteSheet.getSubimage(j * width * 2, i * height, width * 2, height);// longer for the scratching animation
					}
				}

				sprites.add(bufferedImage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		width = 30;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getFire() {
		return fire;
	}

	public int getMaxFire() {
		return maxFire;
	}

	public void setFiring() {
		firing = true;
	}

	public void setScratching() {
		scratching = true;
	}

	public void setGliding(boolean b) {
		gliding = b;
	}

	public void update() {
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xTemp, yTemp);

		// check attack has stopped
		if(currentAction == SCRATCHING) {
			if(animation.hasPlayedOnce()) {
				scratching = false;
			}
		} else if (currentAction == FIREBALL) {
			if(animation.hasPlayedOnce()) {
				firing = false;
			}
		}
		
		// fire ball attack
		fire += 1;// rebuild fire energy
		if(fire > maxFire) {
			fire = maxFire;
		}
		if(firing && currentAction != FIREBALL) {
			if(fire > fireCost) {
				fire -= fireCost;
				FireBall fireBall = new FireBall(tileMap, facingRight);
				fireBall.setPosition(x, y);
				fireBalls.add(fireBall);
			}
		}
		
		// update fire balls
		for(int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).update();
			if(fireBalls.get(i).shouldRemove()) {
				fireBalls.remove(i);
				i--;
			}
		}
		// set animation
		if (scratching) {
			if (currentAction != SCRATCHING) {
				currentAction = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 60;
			}
		} else if (firing) {
			if (currentAction != FIREBALL) {
				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				width = 30;
			}
		} else if (dy > 0) {
			if (gliding) {
				if (currentAction != GLIDING) {
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;
				}
			} else if (currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		} else if (dy < 0) {
			if (currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		} else if (left || right) {
			if (currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		} else {
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}

		animation.update();

		// set direction
		if (currentAction != SCRATCHING && currentAction != FIREBALL) {
			if (right) {
				facingRight = true;
			}
			if (left) {
				facingRight = false;
			}
		}
	}

	public void draw(Graphics2D g) {
		setMapPosition();// should be first thing set in any map object draw method

		// draw fire balls
		for(int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).draw(g);
		}
		
		// draw player
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed / 100 % 2 == 0) {// don't draw the player every 100 ms. This will make him look like he's blinking
				return;
			}
		}
		super.draw(g);
	}
	
	private void getNextPosition() {
		// movement
		if(left) {
			dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if(right) {
			dx += moveSpeed;
			if(dx >= maxSpeed) {
				dx = maxSpeed;
			}
		} else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			} else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}
		
		// cannot move while attacking, except in air
		if(currentAction == SCRATCHING || currentAction == FIREBALL &&!(jumping || falling)) {
			dx = 0;
		}
		
		// jumping
		if(jumping && !falling) {
			dy = jumpStart;
			falling = true;
		}
		
		// falling
		if(falling) {
			if(dy > 0 && gliding) {
				dy += fallSpeed * 0.1;//glide has you fall at 10% of normal speed
			} else {
				dy += fallSpeed;
			}
			
			if(dy > 0) {
				jumping = false;
			}
			if(dy < 0 && !jumping) {
				dy += stopJumpSpeed;
			}
			if(dy > maxFallSpeed) {
				dy = maxFallSpeed;
			}
		}
	}
}
