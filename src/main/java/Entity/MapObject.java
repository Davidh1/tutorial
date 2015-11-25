package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;
import tilemap.Tile;
import tilemap.TileMap;

//TODO: Make this an interface (if possible)
public abstract class MapObject {

	// tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xMap;
	protected double yMap;

	// position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;

	// dimensions - for reading in sprite sheets
	protected int width;
	protected int height;

	// collistion box
	protected int colWidth;
	protected int colHeight;

	// collision
	protected int currRow;
	protected int currCol;
	protected double xDest;// next positions
	protected double yDest;
	protected double yTemp;
	protected double xTemp;
	protected boolean topLeftBlocked;
	protected boolean topRightBlocked;
	protected boolean bottomLeftBlocked;
	protected boolean bottomRightBlocked;

	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight; // for sprite direction

	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;

	// movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;// deceleration
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed;

	// constructor
	public MapObject(TileMap tileMap) {
		this.tileMap = tileMap;
		tileSize = tileMap.getTileSize();
	}

	public boolean intersects(MapObject object) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = object.getRectangle();
		return r1.intersects(r2);
	}

	// TODO: Rewrite the calculate corners method (maybe)
	public void checkTileMapCollision() {
		currCol = (int) x / tileSize;
		currRow = (int) y / tileSize;

		xDest = x += dx;
		yDest = y += dy;

		xTemp = x;
		yTemp = y;

		calculateCorners(x, yDest);// where we are and where we are going
		// if dy != 0, it means we are either moving up or down
		if (dy < 0) {
			if (topLeftBlocked || topRightBlocked) {
				dy = 0;
				yTemp = currRow * tileSize + colHeight / 2; // set right below tile we just bumped into
			} else {
				yTemp += dy;
			}
		} else if (dy > 0) {
			if (bottomLeftBlocked || bottomRightBlocked) {
				dy = 0;
				falling = false;
				yTemp = (currRow + 1) * tileSize - colHeight / 2; // set right above tile we just bumped into
			} else {
				yTemp += dy;
			}
		}

		calculateCorners(xDest, y);// where we are going and where we are
		// if dx != 0, it means we are either moving left or right
		if (dx < 0) {
			if (topLeftBlocked || bottomLeftBlocked) {
				dx = 0;
				xTemp = currCol * tileSize + colWidth / 2;
			} else {
				xTemp += dx;
			}
		} else if (dx > 0) {
			if (topRightBlocked || bottomRightBlocked) {
				dx = 0;
				xTemp = (currCol + 1) * tileSize - colWidth / 2;
			} else {
				xTemp += dx;
			}
		}

		if (!falling) {
			calculateCorners(x, yDest + 1); // check ground 1 pixel below our feet
			if (!bottomLeftBlocked && !bottomRightBlocked) {
				falling = true;
			}
		}
	}

	protected void calculateCorners(double x, double y) {
		int leftTile = (int) (x - colWidth / 2) / tileSize;
		int rightTile = (int) (x + colWidth / 2 - 1) / tileSize;
		int topTile = (int) (y - colHeight / 2) / tileSize;
		int bottomTile = (int) (y + colHeight / 2 - 1) / tileSize;

		// if the object is out of the bounds of the mmap
		if(topTile < 0 || bottomTile >= tileMap.getNumRows() || leftTile < 0 || rightTile >= tileMap.getNumCols()) {
			// top left is blocked if none of the others are blocked????????????;
//			top left is blocked if
//				top right is blocked if
//					bottom left is blocked if
//						bottom right is not blocked
			topLeftBlocked = topRightBlocked = bottomLeftBlocked = bottomRightBlocked = false;
			return;
		}
		int topLeft = tileMap.getType(topTile, leftTile);
		int topRight = tileMap.getType(topTile, rightTile);
		int bottomLeft = tileMap.getType(bottomTile, leftTile);
		int bottomRight = tileMap.getType(bottomTile, rightTile);

		// set if corners are blocked
		topLeftBlocked = topLeft == Tile.BLOCKED;
		topRightBlocked = topRight == Tile.BLOCKED;
		bottomLeftBlocked = bottomLeft == Tile.BLOCKED;
		bottomRightBlocked = bottomRight == Tile.BLOCKED;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	// very important
	public void setMapPosition() {
		// global and local positions - global is on the map, local is on the map relative to the map locatoin
		xMap = tileMap.getX();
		yMap = tileMap.getY();
	}
	
	protected Rectangle getRectangle() {// hmmmm
		return new Rectangle((int) x - colWidth, (int) y - colHeight, colWidth, colHeight);
	}
	
	//TODO: understand this.
	public boolean notOnScreen() {
		return x + xMap + width < 0 || 
				x + xMap - width > GamePanel.WIDTH || 
				y + yMap + height < 0 ||
				y + yMap - height > GamePanel.HEIGHT;
				
	}
	
	public void draw(Graphics2D g) {

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
	
	public void setLeft(boolean b) {
		left = b;
	}

	public void setRight(boolean b) {
		right = b;
	}

	public void setUp(boolean b) {
		up = b;
	}

	public void setDown(boolean b) {
		down = b;
	}

	public void setJumping(boolean b) {
		jumping = b;
	}
	
	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getColWidth() {
		return colWidth;
	}

	public int getColHeight() {
		return colHeight;
	}
}
