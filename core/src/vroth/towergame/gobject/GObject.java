package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import vroth.towergame.GConfig;
import vroth.towergame.TowerGame;

public class GObject {
	protected Fixture fixture;
	protected Body body;
	protected Sprite staticSprite;
	protected Vector2 dimension;
	protected float maxHealth;
	protected float health;
	protected GConfig.TYPES type = GConfig.TYPES.NOTHING;
	protected float damage = GConfig.DAMAGE;
	protected float creationTime;
	protected boolean indestructible = false;
	
	public GObject(Fixture fixture, Body body, Sprite staticSprite, Vector2 dimension, float health, float maxHealth) {
		this.fixture = fixture;
		this.body = body;
		this.staticSprite = staticSprite;
		this.dimension = dimension;
		this.health = health;
		this.maxHealth = maxHealth;
		this.creationTime = TowerGame.currScreen.getStateTime();
	}
	
	public void setIndestructible() {
		indestructible = true;
	}
	
	public boolean isIndestructible() {
		return indestructible;
	}
	
	public float getCreationTime() {
		return creationTime;
	}
	
	public float getDamage() {
		return damage;
	}
	
	public void setType(GConfig.TYPES type) {
		this.type = type;
	}
	
	public GConfig.TYPES getType() {
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
	
	public Vector2 getCenter() {
		return new Vector2(body.getPosition().x + dimension.x/2, body.getPosition().y + dimension.y/2);
	}
}
