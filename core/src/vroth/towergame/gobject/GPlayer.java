package vroth.towergame.gobject;

import java.util.HashMap;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import vroth.towergame.GConfig;
import vroth.towergame.gutil.GDatabase;
import vroth.towergame.gutil.GResourcesLoader;

public class GPlayer extends GObject {
	private Sprite duck, front, hurt, jump, stand, badge1, badge2;
	private Animation walk, swim, climb;
	private boolean goRight;
	
	public enum STATE {STOP, WALK, JUMP, FALL, DUCK, DAMAGE};
	private STATE currState = STATE.STOP;
	private float stateTime;
	private boolean isDucking, isHurt;
	
	private boolean keyUp, keyLeft, keyRight, keyDown;
	
	private HashMap<Integer, Integer> items;
	
	private BitmapFont itemFont;
	private GlyphLayout itemLayout;
	
	private int amountJump;
	
	
	protected GPlayer(Fixture fixture, Body body, Sprite duck, Sprite front, Sprite hurt, Sprite jump, Sprite stand, Animation walk, Animation swim, Animation climb, Sprite badge1, Sprite badge2, Vector2 dimension) {
		super(fixture, body, front, dimension, GConfig.PLAYER_HEALTH, GConfig.PLAYER_HEALTH);
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
		
		items = new HashMap<Integer, Integer>();
		
		itemFont = GResourcesLoader.getInstance().getFont("kenpixel_blocks.fnt");
		itemFont.getData().setScale(0.1f);
		itemFont.setColor(new Color(0, 0, 0, 1));
	}
	
	public void render(SpriteBatch batch, float stateTime, Vector2 drawReference) {
		//System.out.println(currState);
		//draw player
		batch.draw(getSprite(stateTime), body.getPosition().x + drawReference.x, body.getPosition().y + drawReference.y);
		
		//draw resources
		int amountDraw = 0;
		GDatabase gDatabase = GDatabase.getInstance();
		Vector2 basePosition = new Vector2(GConfig.SCREEN_WIDTH-50, 2*GConfig.SCREEN_HEIGHT/3);
		for(Integer type : gDatabase.getItemsToFileRegister()) {
			if(items.containsKey(type)) {
				Texture texture = GResourcesLoader.getInstance().loadTexture(gDatabase.getItemToFile(type));
				
				batch.draw(texture, basePosition.x, basePosition.y - amountDraw*40, 19, texture.getWidth() > 19 ? 19*texture.getHeight()/texture.getWidth() : texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
				
				itemLayout = new GlyphLayout(itemFont, new String("x " + items.get(type)));
				
				float itemFontX = basePosition.x;
				float itemFontY = basePosition.y - amountDraw*40;
				itemFont.draw(batch, itemLayout, itemFontX, itemFontY);
				amountDraw++;
			}
		}
		
		if(GConfig.DRAW_PLAYER_HEALTH == true) {
			amountDraw = 0;
			basePosition = new Vector2(0, GConfig.SCREEN_HEIGHT-45);
			int healthMarks = Math.ceil(health/5) > 0 ? (int) Math.ceil(health/5) : 0;
			int maxMarks = (int) Math.ceil(maxHealth/5);
			for(int i = 1; i <= healthMarks; i++) {
				if(i+1 <= healthMarks) {
					Texture texture = GResourcesLoader.getInstance().loadTexture(gDatabase.getElementToFile("heartFull"));
					batch.draw(texture, basePosition.x + amountDraw*54, basePosition.y, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
					i++;
				}
				else {
					Texture texture = GResourcesLoader.getInstance().loadTexture(gDatabase.getElementToFile("heartHalf"));
					batch.draw(texture, basePosition.x + amountDraw*54, basePosition.y, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
					i++;
				}
				amountDraw++;
			}
			healthMarks++;
			for(int i = healthMarks; i < maxMarks; i+=2) {
				Texture texture = GResourcesLoader.getInstance().loadTexture(gDatabase.getElementToFile("heartEmpty"));
				batch.draw(texture, basePosition.x + amountDraw*54, basePosition.y, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
				amountDraw++;
			}
		}
	}

	public Sprite getSpriteAux(float stateTime) {
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
	
	public boolean keyDown(int keycode) {
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
		body.setLinearVelocity(velocity);
	}
	
	public void hurt() {
		isHurt = true;
		hit(5);
	}
	
	public float hit(float damage) {
		isHurt = true;
		return (this.health -= damage);
	}
	
	public void addItem(int type) {
		if(!items.containsKey(type))
			items.put(type, 1);
		else
			items.put(type, items.get(type)+1);
		//System.out.println(type + ":" + items.get(type));
	}
	
	public void changeDirection() {
		goRight = !goRight;
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
	
	public void update(float stateTime, float deltaTime) {
		Vector2 velocity = body.getLinearVelocity();
		
		//change the state if the conditions are valid; otherwise make player movement
		switch (currState) {
			case DAMAGE:
				if(health > 0) {
					if(equalZero(velocity.y))
						setState(stateTime, STATE.STOP);
				}
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
					applyHurt();
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
				amountJump = GConfig.MAX_JUMP;
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
					applyHurt();
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
