package vroth.towergame.gobject;

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

public class GPlayer extends GCreature {
	private BitmapFont itemFont;
	private GlyphLayout itemLayout;
	
	protected GPlayer(Fixture fixture, Body body, Sprite duck, Sprite front, Sprite hurt, Sprite dead, Sprite jump, Sprite stand, Animation walk, Animation swim, Animation climb, Animation fly, Sprite badge1, Sprite badge2, Vector2 dimension) {
		super(fixture, body, duck, front, hurt, jump, stand, dead, walk, swim, climb, fly, badge1, badge2, dimension, GConfig.PLAYER_HEALTH, GConfig.PLAYER_HEALTH);

		itemFont = GResourcesLoader.getInstance().getFont("kenpixel_blocks.fnt");
		itemFont.getData().setScale(GConfig.PLAYER_ITEM_FONT_SIZE);
		itemFont.setColor(new Color(0, 0, 0, 1));
	}
	
	public void render(SpriteBatch batch, float stateTime, Vector2 drawReference) {
		//draw player
		super.render(batch, stateTime, drawReference);
		
		//draw resources
		int amountDraw = 0;
		GDatabase gDatabase = GDatabase.getInstance();
		Vector2 basePosition = new Vector2(GConfig.SCREEN_WIDTH-50, 2*GConfig.SCREEN_HEIGHT/3);
		for(GConfig.TYPES type : gDatabase.getItemsToFileRegister()) {
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
		
		//draw health
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
	
	public void resetHealth() {
		this.health = maxHealth;
	}
	
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
			case Input.Keys.A:
				keyLeft = true;
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				keyRight = true;
				break;
			case Input.Keys.UP:
			case Input.Keys.SPACE:
			case Input.Keys.W:
				keyUp = true;
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
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
			case Input.Keys.A:
				keyLeft = false;
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				keyRight = false;
				break;
			case Input.Keys.UP:
			case Input.Keys.SPACE:
			case Input.Keys.W:
				keyUp = false;
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
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
}
