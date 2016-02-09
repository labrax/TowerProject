package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class GAnimation {
	private Sprite[] sprites = null;
	
	private long timeEach = 1; 
	private long lastUpdate = 0;
	private int currSprite = 0;
	
	/**
	 * Create an animation passing the sprites and the time for each
	 * @param sprites are the sprites
	 * @param timeEach is the time for each sprite in milliseconds
	 */
	public GAnimation(Sprite[] sprites, long timeEach) {
		this.sprites = sprites;
		this.timeEach = timeEach;
	}

	public Sprite getSprite() {
		//update block for the animation
		long currTime = System.currentTimeMillis();
		if(currTime - lastUpdate > timeEach) {
			lastUpdate = currTime;
			currSprite++;
			if(currSprite >= sprites.length)
				currSprite = 0;
		}
		return sprites[currSprite];
	}
}
