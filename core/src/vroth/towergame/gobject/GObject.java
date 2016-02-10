package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class GObject {
	protected Fixture fixture;
	protected Body body;
	protected Sprite staticSprite;
	protected Vector2 dimension;
	
	public GObject(Fixture fixture, Body body, Sprite staticSprite, Vector2 dimension) {
		this.fixture = fixture;
		this.body = body;
		this.staticSprite = staticSprite;
		this.dimension = dimension;
	}
	
	public Sprite getSprite(float stateTime) {
		return staticSprite;
	}
	
	public Body getBody() {
		return body;
	}
	
	public Vector2 getDimension() {
		return dimension;
	}
}
