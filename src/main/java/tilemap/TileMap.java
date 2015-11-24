package tilemap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileMap {

	// position
	private double x;
	private double y;
	
	// bounds
	private int xMin;
	private int yMin;
	private int xMax;
	private int yMax;
	
	private double tween;// for smoothly scrolling the camera
	
	// map
	private int[][] map;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;

	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;
	
	// drawing -- draw only tiles on the screen
	private int rowOffset;
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;
	
	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2; // extra 2 for padding
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2; // extra 2 for padding		
		tween = 1; // for smooth scrolling
	}
	
	public void loadTiles(String resource) {
		try {
			tileset = ImageIO.read(getClass().getResourceAsStream(resource));
			numTilesAcross = tileset.getWidth() / tileSize;
			tiles = new Tile[2][numTilesAcross];

			BufferedImage subimage;
			for(int col = 0; col < numTilesAcross; col++) {
				subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subimage, Tile.NORMAL);
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);// next row
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadMap(String resource) {
		// first line is number of columns, second is number of rows
		// the rest are the tiles themselves
		
		try {
			InputStream in = getClass().getResourceAsStream(resource);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			
			numCols = Integer.parseInt(bufferedReader.readLine());
			numRows = Integer.parseInt(bufferedReader.readLine());
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows* tileSize;
			
			xMin = GamePanel.WIDTH - width;
			xMax = 0;
			yMin = GamePanel.HEIGHT - height;
			yMax = 0;
			
			String delimiters = "\\s+";// regex for matching "one or many whitespaces"
			for(int row = 0; row < numRows; row++) {// loop through each row
				String line = bufferedReader.readLine();
				String[] tokens = line.split(delimiters); // split the row into an array
				for(int col = 0; col < numCols; col++) {// loop through the columns
					map[row][col] = Integer.parseInt(tokens[col]); // set the index to the current token index
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// the number is just the number of the tile you want to return
	public int getType(int row, int col) {
		int rowCol = map[row][col];
		int r = rowCol / numTilesAcross;
		int c = rowCol % numTilesAcross;// TODO: understand this
		return tiles[r][c].getType();
	}
	
	// for camera
	public void setPosition(double x, double y) {
		// normal way
		// this.x = x;
		// this.y = y;
		
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;
		
		fixBounds();
		
		// find out where to start drawing (column and row)
		// TODO: understand this
		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
	}
	
	// TODO: understand this better
	public void draw(Graphics2D g) {
		for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
			
			if(row >= numRows) {
				break;
			}
				
			for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
				
				if(col >= numCols) {
					break;
				}
				
				// 0's don't need to be drawn;
				if(map[row][col] == 0) {
					continue;
				}
				
				int rowCol = map[row][col];
				int r = rowCol / numTilesAcross;
				int c = rowCol % numTilesAcross;
				
				g.drawImage(tiles[r][c].getImage(), (int)x + col * tileSize, (int) y + row * tileSize, null);
				// for reference
				g.setColor(Color.BLACK);
				g.drawRect((int)x + col * tileSize, (int) y + row * tileSize, tiles[r][c].getImage().getWidth(), tiles[r][c].getImage().getHeight());
			}
		}
	}
	
	// keep inside of the bounds of the map
	private void fixBounds() {
		if(x < xMin) {
			x = xMin;
		}
		if(y < yMin) {
			y = yMin;
		}
		if(x > xMax) {
			x = xMax;
		}
		if(y > yMax) {
			y = yMax;
		}
	}
	
	public int getTileSize() {
		return tileSize;
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
	
	public int getNumRows() {
		return numRows;
	}
	
	public int getNumCols() {
		return numCols;
	}
}
