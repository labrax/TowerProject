package vroth.towergame.gobject;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import vroth.towergame.GConfig;
import vroth.towergame.TowerGame;
import vroth.towergame.gutil.GDatabase;
import vroth.towergame.gutil.GResourcesLoader;

public class GCreature extends GObject {
	protected Sprite duck, front, hurt, jump, stand, badge1, badge2, dead;
	protected Animation walk, swim, climb, fly;
	
	public enum STATE {STOP, WALK, JUMP, FALL, DUCK, DAMAGE, DEAD, FLY, CLIMB};

	protected boolean keyUp, keyLeft, keyRight, keyDown;
	
	protected boolean goRight;
	
	protected HashMap<GConfig.TYPES, Integer> items = new HashMap<GConfig.TYPES, Integer>();
	
	protected STATE currState = STATE.STOP;
	protected float stateTime;
	protected boolean isDucking, isHurt, noHurt = false;
	
	protected int amountJump;
	
	protected boolean flies = false;
	protected float timeHurt = -4;
	
	public GCreature(Fixture fixture, Body body, Sprite duck, Sprite front, Sprite hurt, Sprite jump, Sprite stand, Sprite dead, Animation walk, Animation swim, Animation climb, Animation fly, Sprite badge1, Sprite badge2, Vector2 dimension, int health, int maxHealth) {
		super(fixture, body, front, dimension, health, maxHealth);
		
		if(hurt == GResourcesLoader.getInstance().getErrSprite() || hurt == null)
			noHurt = true;
		
		this.isDucking = false;
		this.isHurt = false;
		goRight = true;
		this.front = front;
		this.duck = duck;
		this.hurt = hurt;
		this.jump = jump;
		this.stand = stand;
		this.dead = dead;
		this.walk = walk;
		this.swim = swim;
		this.climb = climb;
		this.fly = fly;
		this.badge1 = badge1;
		this.badge2 = badge2;
		
		keyUp = false;
		keyLeft = false;
		keyRight = false;
		keyDown = false;
	}
	
	public void setFly() {
		flies = true;
		currState = STATE.FLY;
	}
	
	private Sprite getSpriteAux(float stateTime) {
		switch(currState) {
			case DUCK:
				return duck;
			case STOP:
				return stand;
			case DAMAGE:
				return hurt;
			case JUMP:
				if(stateTime > GConfig.ANIMATION_FRAME_TIME)
					return new Sprite(walk.getKeyFrame(stateTime - this.stateTime, true));
				else
					return jump;
			case WALK:
				return new Sprite(walk.getKeyFrame(stateTime - this.stateTime, true));
			case FALL:
				return new Sprite(walk.getKeyFrame(stateTime - this.stateTime, true));
			case FLY:
				return new Sprite(fly.getKeyFrame(stateTime - this.stateTime, true));
			case DEAD:
				return dead;
			case CLIMB:
				return new Sprite(climb.getKeyFrame(stateTime - this.stateTime, true));
			default:
				return GResourcesLoader.getInstance().getErrSprite();
		}
	}
	
	public Sprite getSprite(float stateTime) {
		Sprite willReturn = getSpriteAux(stateTime);
		
		if(willReturn == null) {
			System.out.println("is null with " + currState);
			return GResourcesLoader.getInstance().getErrSprite();
		}
		else {
			willReturn = new Sprite(willReturn);
			if(goRight)
				return willReturn;
			else {
				willReturn.flip(true, false);
				return willReturn;
			}
		}
	}
	
	public void render(SpriteBatch batch, float stateTime, Vector2 drawReference) {
		//System.out.println(stateTime + " " + timeHurt);
		if(noHurt && stateTime - timeHurt < 0.3) {
			batch.draw(getSprite(stateTime), body.getPosition().x + drawReference.x, body.getPosition().y + drawReference.y);
			Color c = batch.getColor();
			batch.setColor(new Color(1f, 0f, 0f, 0.8f));
			batch.draw(getSprite(stateTime), body.getPosition().x + drawReference.x, body.getPosition().y + drawReference.y);
			batch.setColor(c);
			
		}
		else {
			batch.draw(getSprite(stateTime), body.getPosition().x + drawReference.x, body.getPosition().y + drawReference.y);
		}
	}
	
	private void setVelocity(Vector2 velocity) {
		body.setLinearVelocity(velocity);
	}
	
	protected void hurt() {
		isHurt = true;
		hit(5);
	}
	
	public float hit(float damage) {
		isHurt = true;
		return (this.health -= damage);
	}
	
	private void applyHurt(float stateTime) {
		if(timeHurt + GConfig.MIN_HURT_TIME < stateTime) {
			if(noHurt) {
				timeHurt = stateTime;
				setVelocity(new Vector2(0, 0));
			}
			else { 
				setState(stateTime, STATE.DAMAGE);
				if(goRight)
					setVelocity(new Vector2(-50f, 50f));
				else
					setVelocity(new Vector2(50f, 50f));
			}
		}
	}
	
	
	protected void setState(float stateTime, STATE newState) {
		//System.out.println(currState + " -> " + newState);
		this.stateTime = stateTime;
		this.currState = newState;
	}
	
	public void changeDirection() {
		goRight = !goRight;
	}
	
	protected boolean lessThanZero(float val) {
		if(val < -GConfig.EPSILON)
			return true;
		return false;
	}
	
	protected boolean greaterThanZero(float val) {
		if(val > GConfig.EPSILON)
			return true;
		return false;
	}
	
	protected boolean equalZero(float val) {
		if(val < GConfig.EPSILON && val > -GConfig.EPSILON)
			return true;
		return false;
	}
	
	public void addItem(GConfig.TYPES type) {
		if(!items.containsKey(type))
			items.put(type, 1);
		else
			items.put(type, items.get(type)+1);
		//System.out.println(type + ":" + items.get(type));
	}
	
	public void addItem(GConfig.TYPES type, int amount) {
		if(!items.containsKey(type))
			items.put(type, amount);
		else
			items.put(type, items.get(type)+amount);
		//System.out.println(type + ":" + items.get(type));
	}
	
	private void updateFlies(float stateTime, float deltaTime) {
		if(currState != STATE.FLY && currState != STATE.DEAD) {
			currState = STATE.FLY;
		}
		switch(currState) {
			case FLY:
				if(isHurt)
					applyHurt(stateTime);
				
				if(keyLeft)
					body.applyForceToCenter(new Vector2(body.getLinearVelocity().x > -GConfig.SPEED_FLY ? -GConfig.SPEED_FLY * deltaTime : 0, 0), true);
				else if(keyRight)
					body.applyForceToCenter(new Vector2(body.getLinearVelocity().x < GConfig.SPEED_FLY ? GConfig.SPEED_FLY * deltaTime : 0, 0), true);
				
				if(keyDown)
					body.applyForceToCenter(new Vector2(0, body.getLinearVelocity().y > -GConfig.SPEED_FLY ? -GConfig.SPEED_FLY * deltaTime : 0), true);
				else if(keyUp)
					body.applyForceToCenter(new Vector2(0, body.getLinearVelocity().y < GConfig.SPEED_FLY ? GConfig.SPEED_FLY * deltaTime : 0), true);
				break;
			case DEAD:
				if(stateTime - this.stateTime > GConfig.DEAD_TIME)
					TowerGame.currScreen.setCreatureForRemoval(this);
				break;
			default:
				break;
		}
	}
	
	public void setClimb(float stateTime, boolean climbing) {
		if(climbing) {
			if(currState != STATE.CLIMB) {
				setState(stateTime, STATE.CLIMB);
				setVelocity(new Vector2(0, 0));
			}
		}
		else {
			if(currState == STATE.CLIMB) {
				setState(stateTime, STATE.STOP);
			}
		}
	}
	
	private void updateNoFlies(float stateTime, float deltaTime) {
		Vector2 velocity = body.getLinearVelocity();
		//change the state if the conditions are valid; otherwise make player movement
		switch (currState) {
			case CLIMB:
				if(isHurt)
					applyHurt(stateTime);
				if(keyLeft)
					body.applyForceToCenter(new Vector2(body.getLinearVelocity().x > -GConfig.SPEED_WALK ? -GConfig.SPEED_WALK * deltaTime : 0, 0), true);
				else if(keyRight)
					body.applyForceToCenter(new Vector2(body.getLinearVelocity().x < GConfig.SPEED_WALK ? GConfig.SPEED_WALK * deltaTime : 0, 0), true);
				
				if(keyDown)
					body.applyForceToCenter(new Vector2(0, body.getLinearVelocity().y > -GConfig.SPEED_WALK ? -GConfig.SPEED_WALK * deltaTime : 0), true);
				else if(keyUp)
					body.applyForceToCenter(new Vector2(0, body.getLinearVelocity().y < GConfig.SPEED_WALK ? GConfig.SPEED_WALK * deltaTime : 0), true);
				
				body.setGravityScale(0);
				break;
			case DAMAGE:
				if(health > 0) {
					if(equalZero(velocity.y))
						setState(stateTime, STATE.STOP);
				}
				break;
			case DUCK:
				if(isHurt)
					applyHurt(stateTime);
				else if(lessThanZero(velocity.y))
					setState(stateTime, STATE.FALL);
				else if(!equalZero(velocity.x))
					setState(stateTime, STATE.WALK);
				else if(greaterThanZero(velocity.y))
					setState(stateTime, STATE.JUMP);
				else if(!isDucking)
					setState(stateTime, STATE.STOP);
				break;
			case FALL:
				if(isHurt)
					applyHurt(stateTime);
				else if(equalZero(velocity.y) && !equalZero(velocity.x))
					setState(stateTime, STATE.WALK);
				else if(equalZero(velocity.y) && equalZero(velocity.x))
					setState(stateTime, STATE.STOP);
				else if(greaterThanZero(velocity.y))
					setState(stateTime, STATE.JUMP);
				else {
					if(keyLeft)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x > -GConfig.SPEED_WALK ? -GConfig.SPEED_WALK * deltaTime : 0, 0), true);
					else if(keyRight)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x < GConfig.SPEED_WALK ? GConfig.SPEED_WALK * deltaTime : 0, 0), true);
					if(keyUp && amountJump > 0) {
						body.applyForceToCenter(new Vector2(0, GConfig.FORCE_UP), true);
						amountJump--;
						//keyUp = false;
					}
				}
				break;
			case JUMP:
				if(isHurt)
					applyHurt(stateTime);
				else if(equalZero(velocity.y) || lessThanZero(velocity.y))
					setState(stateTime, STATE.FALL);
				else {
					if(keyLeft)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x > -GConfig.SPEED_WALK ? -GConfig.SPEED_WALK * deltaTime : 0, 0), true);
					else if(keyRight)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x < GConfig.SPEED_WALK ? GConfig.SPEED_WALK * deltaTime : 0, 0), true);
				}
				break;
			case STOP:
				body.setGravityScale(1);
				
				amountJump = GConfig.MAX_JUMP;
				if(isHurt)
					applyHurt(stateTime);
				else if(lessThanZero(velocity.y))
					setState(stateTime, STATE.FALL);
				else if(!equalZero(velocity.x))
					setState(stateTime, STATE.WALK);
				else if(greaterThanZero(velocity.y))
					setState(stateTime, STATE.JUMP);
				else if(isDucking)
					setState(stateTime, STATE.DUCK);
				else {
					if(keyLeft)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x > -GConfig.SPEED_WALK ? -GConfig.SPEED_WALK * deltaTime : 0, 0), true);
					else if(keyRight)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x < GConfig.SPEED_WALK ? GConfig.SPEED_WALK * deltaTime : 0, 0), true);
					if(keyUp) {
						body.applyForceToCenter(new Vector2(0, GConfig.FORCE_UP), true);
					//keyUp = false;
					}
				}
				break;
			case WALK:
				amountJump = GConfig.MAX_JUMP;
				if(isHurt)
					applyHurt(stateTime);
				else if(lessThanZero(velocity.y))
					setState(stateTime, STATE.FALL);
				else if(greaterThanZero(velocity.y))
					setState(stateTime, STATE.JUMP);
				else if(equalZero(velocity.x))
					setState(stateTime, STATE.STOP);
				else {
					if(keyLeft)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x > -GConfig.SPEED_WALK ? -GConfig.SPEED_WALK * deltaTime : 0, 0), true);
					else if(keyRight)
						body.applyForceToCenter(new Vector2(body.getLinearVelocity().x < GConfig.SPEED_WALK ? GConfig.SPEED_WALK * deltaTime : 0, 0), true);
					if(keyUp) {
						body.applyForceToCenter(new Vector2(0, GConfig.FORCE_UP), true);
						//keyUp = false;
					}
						//movePlayer(new Vector2(body.getLinearVelocity().x, GConfig.SPEED_UP));
				}
				break;
			case DEAD:
				if(stateTime - this.stateTime > GConfig.DEAD_TIME)
					TowerGame.currScreen.setCreatureForRemoval(this);
				break;
			default:
				System.err.println("state invalid for " + this);
				setState(stateTime, STATE.STOP);
				break;
		}
	}
	
	public void update(float stateTime, float deltaTime) {
		if(flies)
			updateFlies(stateTime, deltaTime);
		else
			updateNoFlies(stateTime, deltaTime);
		
		//creature bellow water
		if(getBody().getPosition().y < 10) {
			hit(deltaTime*GDatabase.getInstance().getRandom().nextInt(GConfig.MAX_WATER_DAMAGE-GConfig.MIN_WATER_DAMAGE) + GConfig.MIN_WATER_DAMAGE);
		}
		
		isHurt = false;
		
		Vector2 velocity = body.getLinearVelocity();
		
		//if the player is not damaged change its sprite
		if(currState != STATE.DAMAGE) {
			if(lessThanZero(velocity.x))
				goRight = false;
			else if(greaterThanZero(velocity.x))
				goRight = true;
		}
		
		if(currState != STATE.DEAD && health <= 0)
			setState(stateTime, STATE.DEAD);
	}
	
	public void setGoal(Vector2 target) {
		Vector2 source = getCenter();
		Vector2 movement = new Vector2(target.x - source.x, target.y - source.y);
		if(greaterThanZero(movement.x/100)) {
			keyRight = true;
			keyLeft = false;
		}
		else if(lessThanZero(movement.y/100)){
			keyRight = false;
			keyLeft = true;
		}
		else {
			keyRight = false;
			keyLeft = false;
		}
		
		if(greaterThanZero(movement.y/100)) {
			keyUp = true;
			keyDown = false;
		}
		else if(lessThanZero(movement.y/100)){
			keyUp = false;
			keyDown = true;
		}
		else {
			keyUp = false;
			keyDown = false;
		}
	}
	
	public HashMap<GConfig.TYPES, Integer> getItems() {
		return items;
	}
	
	public STATE getState() {
		return currState;
	}
}
