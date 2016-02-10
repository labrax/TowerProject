package vroth.towergame.gobject;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import vroth.towergame.GConfig;
import vroth.towergame.gutil.ResourcesLoader;

public class GPlayer extends GObject {
	public enum STATE {STOP, WALK, JUMP, FALL, DUCK, DAMAGE};
	public STATE currState = STATE.STOP;
	float stateTime;
	boolean isDucking, isHurt;
	
	boolean keyUp, keyLeft, keyRight, keyDown;
	
	boolean goRight;
	Sprite duck, front, hurt, jump, stand, badge1, badge2;
	Animation walk, swim, climb;
	
	protected GPlayer(Fixture fixture, Body body, Sprite duck, Sprite front, Sprite hurt, Sprite jump, Sprite stand, Animation walk, Animation swim, Animation climb, Sprite badge1, Sprite badge2, Vector2 dimension) {
		super(fixture, body, front, dimension, 100);
		isDucking = false;
		isHurt = false;
		goRight = true;
		this.front = front;
		this.duck = duck;
		this.hurt = hurt;
		this.jump = jump;
		this.stand = stand;
		this.walk = walk;
		this.swim = swim;
		this.climb = climb;
		this.badge1 = badge1;
		this.badge2 = badge2;
		
		keyUp = false;
		keyLeft = false;
		keyRight = false;
		keyDown = false;
	}

	public Sprite getSpriteAux(float stateTime) {
		//System.out.println("printing: " + currState);
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
		}
		return null;
	}
	
	public Sprite getSprite(float stateTime) {
		//System.out.println(currState + " " + body.getLinearVelocity().x + ", " + body.getLinearVelocity().y);
		Sprite willReturn = getSpriteAux(stateTime);
		
		if(willReturn == null) {
			System.out.println("is null with " + currState);
			return ResourcesLoader.getResourcesLoader().getErrSprite();
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
	
	public boolean keyDown(int keycode) {
		//System.out.println("keydown: " + keycode);
		switch (keycode) {
			case Input.Keys.LEFT:
				keyLeft = true;
				break;
			case Input.Keys.RIGHT:
				keyRight = true;
				break;
			case Input.Keys.UP:
			case Input.Keys.SPACE:
				keyUp = true;
				break;
			case Input.Keys.DOWN:
				keyDown = true;
				isDucking = true;
				break;
			default:
				break;
		}
		return true;
	}
	
	public boolean keyUp(int keycode) {
		//System.out.println("keyup: " + keycode);
		switch (keycode) {
			case Input.Keys.LEFT:
				keyLeft = false;
				break;
			case Input.Keys.RIGHT:
				keyRight = false;
				break;
			case Input.Keys.UP:
			case Input.Keys.SPACE:
				keyUp = false;
				break;
			case Input.Keys.DOWN:
				keyDown = false;
				isDucking = false;
				break;
			case Input.Keys.H:
				hurt();
				break;
			default:
				break;
		}
		return true;
	}
	
	private void movePlayer(Vector2 velocity) {
		//System.out.println("Setting " + velocity + " (" + body.getLinearVelocity() + ")");
		body.setLinearVelocity(velocity);
	}
	
	public void hurt() {
		isHurt = true;
	}
	
	public void setState(float stateTime, STATE newState) {
		/*if(newState != currState) {
			System.out.println(currState + " -> " + newState + " (" + this.stateTime + ")");
			System.out.println(body.getLinearVelocity());
		}*/
		this.stateTime = stateTime;
		this.currState = newState;
	}
	
	private void applyHurt() {
		setState(stateTime, STATE.DAMAGE);
		if(goRight)
			movePlayer(new Vector2(-50f, 50f));
		else
			movePlayer(new Vector2(50f, 50f));
	}
	
	private boolean lessThanZero(float val) {
		if(val < -GConfig.EPSILON)
			return true;
		return false;
	}
	
	private boolean greaterThanZero(float val) {
		if(val > GConfig.EPSILON)
			return true;
		return false;
	}
	
	private boolean equalZero(float val) {
		if(val < GConfig.EPSILON && val > -GConfig.EPSILON)
			return true;
		return false;
	}
	
	public void update(float stateTime) {
		Vector2 velocity = body.getLinearVelocity();
		
		//change the state if the conditions are valid; otherwise make player movement
		switch (currState) {
			case DAMAGE:
				if(equalZero(velocity.y))
					setState(stateTime, STATE.STOP);
				break;
			case DUCK:
				if(isHurt)
					applyHurt();
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
					applyHurt();
				else if(equalZero(velocity.y) && !equalZero(velocity.x))
					setState(stateTime, STATE.WALK);
				else if(equalZero(velocity.y) && equalZero(velocity.x))
					setState(stateTime, STATE.STOP);
				else {
					if(keyLeft && stateTime - this.stateTime > 3*GConfig.ANIMATION_FRAME_TIME)
						movePlayer(new Vector2((body.getLinearVelocity().x < -GConfig.SPEED_WALK/2 ? body.getLinearVelocity().x : -GConfig.SPEED_WALK/2), body.getLinearVelocity().y));
					else if(keyRight && stateTime - this.stateTime > 3*GConfig.ANIMATION_FRAME_TIME)
						movePlayer(new Vector2((body.getLinearVelocity().x > GConfig.SPEED_WALK/2 ? body.getLinearVelocity().x : GConfig.SPEED_WALK/2), body.getLinearVelocity().y));
					if(keyUp) {
						body.applyLinearImpulse(new Vector2(0, GConfig.SPEED_UP), new Vector2(body.getPosition().x, body.getPosition().y), true);
						movePlayer(new Vector2(body.getLinearVelocity().x, GConfig.SPEED_UP));
						keyUp = false;
					}
				}
				break;
			case JUMP:
				if(isHurt)
					applyHurt();
				else if(equalZero(velocity.y) || lessThanZero(velocity.y))
					setState(stateTime, STATE.FALL);
				else if(keyLeft && stateTime - this.stateTime > 5*GConfig.ANIMATION_FRAME_TIME)
					movePlayer(new Vector2((body.getLinearVelocity().x < -GConfig.SPEED_WALK/2 ? body.getLinearVelocity().x : -GConfig.SPEED_WALK/2), body.getLinearVelocity().y));
				else if(keyRight && stateTime - this.stateTime > 5*GConfig.ANIMATION_FRAME_TIME)
					movePlayer(new Vector2((body.getLinearVelocity().x > GConfig.SPEED_WALK/2 ? body.getLinearVelocity().x : GConfig.SPEED_WALK/2), body.getLinearVelocity().y));
				break;
			case STOP:
				if(isHurt)
					applyHurt();
				else if(lessThanZero(velocity.y))
					setState(stateTime, STATE.FALL);
				else if(!equalZero(velocity.x))
					setState(stateTime, STATE.WALK);
				else if(greaterThanZero(velocity.y))
					setState(stateTime, STATE.JUMP);
				else if(isDucking)
					setState(stateTime, STATE.DUCK);
				else if(keyLeft)
						movePlayer(new Vector2(-GConfig.SPEED_WALK, body.getLinearVelocity().y));
					else if(keyRight)
						movePlayer(new Vector2(GConfig.SPEED_WALK, body.getLinearVelocity().y));
					if(keyUp) {
						body.applyLinearImpulse(new Vector2(0, GConfig.SPEED_UP), new Vector2(body.getPosition().x, body.getPosition().y), true);
						movePlayer(new Vector2(body.getLinearVelocity().x, GConfig.SPEED_UP));
						keyUp = false;
					}
				break;
			case WALK:
				if(isHurt)
					applyHurt();
				else if(lessThanZero(velocity.y))
					setState(stateTime, STATE.FALL);
				else if(greaterThanZero(velocity.y))
					setState(stateTime, STATE.JUMP);
				else if(equalZero(velocity.x))
					setState(stateTime, STATE.STOP);
				else {
					if(keyLeft)
						movePlayer(new Vector2(-GConfig.SPEED_WALK, body.getLinearVelocity().y));
					else if(keyRight)
						movePlayer(new Vector2(GConfig.SPEED_WALK, body.getLinearVelocity().y));
					if(keyUp) {
						body.applyLinearImpulse(new Vector2(0, GConfig.SPEED_UP), new Vector2(body.getPosition().x, body.getPosition().y), true);
						movePlayer(new Vector2(body.getLinearVelocity().x, GConfig.SPEED_UP));
						keyUp = false;
					}
						//movePlayer(new Vector2(body.getLinearVelocity().x, GConfig.SPEED_UP));
				}
				break;
			default:
				break;
		}
		
		isHurt = false;
		
		//if the player is not damaged change its sprite
		if(currState != STATE.DAMAGE) {
			if(lessThanZero(velocity.x))
				goRight = false;
			else if(greaterThanZero(velocity.x))
				goRight = true;
		}
	}
}
