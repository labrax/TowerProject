package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import vroth.towergame.GConfig;
import vroth.towergame.gutil.ResourcesLoader;

public class GPlayer extends GObject {
	enum STATE {DUCKING, FRONT, HURT, JUMPING, STANDING, WALKING};
	public STATE currState = STATE.FRONT;
	float hurtTime;
	boolean isDucking, isHurt;
	boolean goRight;
	Sprite duck, front, hurt, jump, stand, badge1, badge2;
	Animation walk, swim, climb;
	
	protected GPlayer(Body body, Sprite duck, Sprite front, Sprite hurt, Sprite jump, Sprite stand, Animation walk, Animation swim, Animation climb, Sprite badge1, Sprite badge2, Vector2 dimension) {
		super(body, front, dimension);
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
	}

	public Sprite getSpriteAux(float stateTime) {
		switch(currState) {
			case DUCKING:
				return duck;
			case FRONT:
				return front;
			case HURT:
				return hurt;
			case JUMPING:
				return jump;
			case STANDING:
				return stand;
			case WALKING:
				return new Sprite(walk.getKeyFrame(stateTime, true));
			default:
				break;
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
		if(goRight)
			return willReturn;
		else {
			willReturn.flip(true, false);
			return willReturn;
		}
	}
	
	private boolean isClose(float i, float j) {
		if(Math.abs(i-j) < GConfig.EPSILON)
			return true;
		return false;
	}
	
	public void movePlayer(Vector2 velocity) {
		if(!isHurt && !isDucking)
		{
			body.setLinearVelocity(velocity);
			if(velocity.x < 0)
				goRight = false;
			else
				goRight = true;
			if(isClose(velocity.x, 0) && isClose(velocity.y, 0)) {
				currState = STATE.STANDING;
			}
			else if(velocity.x != 0 && isClose(velocity.y, 0)) {
				currState = STATE.WALKING;
			}
			else if(velocity.y > GConfig.EPSILON || velocity.y < -GConfig.EPSILON)
				currState = STATE.JUMPING;
		}
	}
	
	public void setHurt(float stateTime) {
		hurtTime = stateTime;
		isHurt = true;
		currState = STATE.HURT;
	}
	
	public void setDucking(float stateTime) {
		isDucking = true;
	}
	
	public void update(float stateTime) {
		if(stateTime - hurtTime > GConfig.HURT_TIME) {
			isHurt = false;
		}
		
		Vector2 velocity = body.getLinearVelocity();
		if(!isHurt && !isDucking)
		{
			if(isClose(velocity.x, 0) && isClose(velocity.y, 0)) {
				currState = STATE.STANDING;
			}
			else if(velocity.x != 0 && isClose(velocity.y, 0)) {
				currState = STATE.WALKING;
			}
			else if(velocity.y > GConfig.EPSILON || velocity.y < -GConfig.EPSILON) {
				currState = STATE.JUMPING;
			}
		}
		else if(isHurt)
			currState = STATE.HURT;
		else if(isDucking)
			currState = STATE.DUCKING;
		
		if(velocity.x < 0)
			goRight = false;
		else
			goRight = true;
	}
}
