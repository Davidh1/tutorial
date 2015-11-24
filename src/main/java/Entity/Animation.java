package Entity;

import java.awt.image.BufferedImage;

public class Animation {
	
	private BufferedImage[] frames;
	private int currentFrame;
	
	private long startTime;
	private long delay;
	
	private boolean playedOnce;// good for attacking animations
	
	public Animation() {
		playedOnce = false;
	}
	
	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
		currentFrame = 0;
		startTime = System.nanoTime();
		playedOnce = false;
	}
	
	public void setDelay(long l) {
		delay = l;
	}
	
	public void setFrame(int i) {// might not need
		currentFrame = i;
	}
	
	public void update() {
		if(delay == -1) {// -1 means no animation
			return;
		}
		long elapsed = (System.nanoTime() - startTime) / 1000000;// not sure why we need to divide by 1000000 since they're both nanotime
		if(elapsed > delay) {
			currentFrame++;
			startTime = System.nanoTime();
		}
		if(currentFrame == frames.length) {
			currentFrame = 0;
			playedOnce = true;
		}
	}
	
	public int getFrame() {
		return currentFrame;
	}
	
	public BufferedImage getImage() {
		return frames[currentFrame];
	}
	
	public boolean hasPlayedOnce() {
		return playedOnce;
	}
}
