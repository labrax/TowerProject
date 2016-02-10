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
	protected float health;
	protected int type = 0;
	protected float damage = 30;
	
	public GObject(Fixture fixture, Body body, Sprite staticSprite, Vector2 dimension, float health) {
		this.fixture = fixture;
		this.body = body;
		this.staticSprite = staticSprite;
		this.dimension = dimension;
		this.health = health;
	}
	
	public float getDamage() {
		return damage;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public float hit(float damage) {
		return (this.health -= damage);
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
