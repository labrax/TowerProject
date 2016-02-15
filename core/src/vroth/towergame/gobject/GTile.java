package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class GTile extends GObject {
	
	private Sprite[] sprites;
	
	public GTile(Fixture fixture, Body body, Sprite[] sprites, Vector2 dimension, int health) {
		super(fixture, body, sprites[0], dimension, health, health);
		this.sprites = sprites;
	}
	
	public Sprite getSprite(float stateTime) {
		/*if(body.getLinearVelocity().y != 0) {
			System.out.println("drawing tile: " + this);
			System.out.println(this.staticSprite);
		}*/
		return super.getSprite(stateTime);
	}
	
	public Sprite getSprite(boolean top, boolean down, boolean left, boolean right) {
		int val = 0;
		if(top)
			val += 1;
		if(left)
			val += 2;
		if(right)
			val += 4;
		if(down)
			val += 8;
		
		return sprites[val];
	}
}
