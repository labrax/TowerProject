package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class GPlayer extends GObject {
	enum STATE {DUCKING, FRONT, HURT, JUMPING, STANDING, WALKING};
	public STATE currState = STATE.FRONT;
	Sprite duck, front, hurt, jump, stand;
	Animation walk;
	
	protected GPlayer(Body body, Sprite duck, Sprite front, Sprite hurt, Sprite jump, Sprite stand, Animation walk) {
		super(body, front);
		
		this.duck = duck;
		this.front = front;
		this.hurt = hurt;
		this.jump = jump;
		this.stand = stand;
		this.walk = walk;
	}

	public Sprite getSprite(float stateTime) {
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
}
