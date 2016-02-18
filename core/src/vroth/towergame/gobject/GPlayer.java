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
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;
import vroth.towergame.gutil.GDatabase;
import vroth.towergame.gutil.GResourcesLoader;

public class GPlayer extends GCreature {
	private BitmapFont itemFont;
	private GlyphLayout itemLayout;
	
	private BitmapFont shopIdFont;
	private GlyphLayout shopIdLayout;
	
	private Sprite[] buyMenu = new Sprite[6];
	private GConfig.TYPES[] typesMenu = {GConfig.TYPES.GRASS, GConfig.TYPES.CASTLE, GConfig.TYPES.HOUSE, GConfig.TYPES.LADDER, GConfig.TYPES.BRONZE_COIN, GConfig.TYPES.SILVER_COIN};
	private int[] amount = {1, 1, 1, 2, 5, 5};
	private Array<HashMap<GConfig.TYPES, Integer>> costMenus = new Array<HashMap<GConfig.TYPES, Integer>>();
	
	private boolean shiftPressed = false;
	
	protected GPlayer(Fixture fixture, Body body, Sprite duck, Sprite front, Sprite hurt, Sprite dead, Sprite jump, Sprite stand, Animation walk, Animation swim, Animation climb, Animation fly, Sprite badge1, Sprite badge2, Vector2 dimension) {
		super(fixture, body, duck, front, hurt, jump, stand, dead, walk, swim, climb, fly, badge1, badge2, dimension, GConfig.PLAYER_HEALTH, GConfig.PLAYER_HEALTH);

		GResourcesLoader rl = GResourcesLoader.getInstance();
		itemFont = rl.getFont("kenpixel_blocks.fnt");
		itemFont.getData().setScale(GConfig.PLAYER_ITEM_FONT_SIZE);
		itemFont.setColor(new Color(0, 0, 0, 1));
		
		shopIdFont = rl.getFont("kenpixel_blocks.fnt");
		shopIdFont.getData().setScale(GConfig.PLAYER_ITEM_FONT_SIZE*2);
		shopIdFont.setColor(new Color(0, 0, 0, 1));
		
		damage = GConfig.DAMAGE;
		
		buyMenu[0] = rl.loadSprite("tiles/grass0.png");
		buyMenu[1] = rl.loadSprite("tiles/castle0.png");
		buyMenu[2] = rl.loadSprite("tiles/houseBeige0.png");
		buyMenu[3] = rl.loadSprite("items/ladder/ladder0.png");
		buyMenu[4] = rl.loadSprite("hud/coinBronze.png");
		buyMenu[5] = rl.loadSprite("hud/coinSilver.png");
		
		for(int i = 0; i < typesMenu.length; i++)
			costMenus.add(new HashMap<GConfig.TYPES, Integer>());
		
		costMenus.get(0).put(GConfig.TYPES.PARTICLE_DIRT, 3);
		costMenus.get(1).put(GConfig.TYPES.PARTICLE_DIRT, 1);
		costMenus.get(1).put(GConfig.TYPES.PARTICLE_IRON, 3);
		costMenus.get(2).put(GConfig.TYPES.BRONZE_COIN, 5);
		costMenus.get(3).put(GConfig.TYPES.SILVER_COIN, 5);
		costMenus.get(4).put(GConfig.TYPES.SILVER_COIN, 1);
		costMenus.get(5).put(GConfig.TYPES.GOLD_COIN, 1);
	}
	
	public void render(SpriteBatch batch, float stateTime, Vector2 drawReference) {
		//draw player
		super.render(batch, stateTime, drawReference);
		
		//draw resources
		int amountDraw = 0;
		GDatabase gDatabase = GDatabase.getInstance();
		Vector2 basePosition = new Vector2(GConfig.SCREEN_WIDTH-50, GConfig.SCREEN_HEIGHT-30);
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
		
		//render shop
		if(GConfig.DRAW_SHOP)
		for(int i = 0; i < buyMenu.length; i++) {
			basePosition = new Vector2(3 + i*73, 10);
			batch.draw(buyMenu[i], basePosition.x, basePosition.y);
			//draw item id
			shopIdLayout = new GlyphLayout(itemFont, new String("" + (i+1)));
			
			float shopIdFontX = basePosition.x;
			float shopIdFontY = basePosition.y + 70 - shopIdLayout.height;
			shopIdFont.draw(batch, shopIdLayout, shopIdFontX, shopIdFontY);
			
			shopIdLayout = new GlyphLayout(itemFont, new String("x " + amount[i]));
			shopIdFontX = basePosition.x + 35;
			shopIdFontY = basePosition.y + 70 - shopIdLayout.height;
			shopIdFont.draw(batch, shopIdLayout, shopIdFontX, shopIdFontY);
			
			//draw costs
			int amountShop = 0;
			for(GConfig.TYPES type : costMenus.get(i).keySet()) {
				Texture texture = GResourcesLoader.getInstance().loadTexture(gDatabase.getItemToFile(type));
				
				batch.draw(texture, basePosition.x + amountShop*20, basePosition.y, 19, texture.getWidth() > 19 ? 19*texture.getHeight()/texture.getWidth() : texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
				
				itemLayout = new GlyphLayout(itemFont, new String("x " + costMenus.get(i).get(type)));
				
				float itemFontX = basePosition.x + amountShop*20;
				float itemFontY = basePosition.y;
				itemFont.draw(batch, itemLayout, itemFontX, itemFontY);
				amountShop++;
			}
		}
	}
	
	public void resetHealth() {
		this.health = maxHealth;
	}
	
	public void tryBuy(int id) {
		boolean ok = true;
		for(GConfig.TYPES type : costMenus.get(id).keySet()) {
			if(!items.containsKey(type) || items.get(type) < costMenus.get(id).get(type)) {
				ok = false;
				break;
			}
		}
		if(ok == true) {
			for(GConfig.TYPES type : costMenus.get(id).keySet()) {
				items.put(type, items.get(type) - costMenus.get(id).get(type));
			}
			if(items.containsKey(typesMenu[id]))
				items.put(typesMenu[id], items.get(typesMenu[id]) + amount[id]);
			else
				items.put(typesMenu[id], amount[id]);
		}
	}
	
	public void trySell(int id) {
		if(items.containsKey(typesMenu[id])) {
			if(items.get(typesMenu[id]) >= amount[id]) {
				items.put(typesMenu[id], items.get(typesMenu[id]) - amount[id]);
				for(GConfig.TYPES type : costMenus.get(id).keySet()) {
					if(items.containsKey(type))
						items.put(type, items.get(type) + costMenus.get(id).get(type));
					else
						items.put(type, costMenus.get(id).get(type));
				}
			}
		}
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
			case Input.Keys.W:
				keyUp = true;
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
				keyDown = true;
				isDucking = true;
				break;
			case Input.Keys.SPACE:
				keySpace = true;
				break;
			case Input.Keys.NUM_1:
				if(shiftPressed)
					trySell(0);
				else
					tryBuy(0);
				break;
			case Input.Keys.NUM_2:
				if(shiftPressed)
					trySell(1);
				else
					tryBuy(1);
				break;
			case Input.Keys.NUM_3:
				if(shiftPressed)
					trySell(2);
				else
					tryBuy(2);
				break;
			case Input.Keys.NUM_4:
				if(shiftPressed)
					trySell(3);
				else
					tryBuy(3);
				break;
			case Input.Keys.NUM_5:
				if(shiftPressed)
					trySell(4);
				else
					tryBuy(4);
				break;
			case Input.Keys.NUM_6:
				if(shiftPressed)
					trySell(5);
				else
					tryBuy(5);
				break;
			case Input.Keys.SHIFT_LEFT:
				shiftPressed = true;
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
			case Input.Keys.W:
				keyUp = false;
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
				keyDown = false;
				isDucking = false;
				break;
			case Input.Keys.SPACE:
				keySpace = false;
				break;
			case Input.Keys.SHIFT_LEFT:
				shiftPressed = false;
				break;
			default:
				break;
		}
		return true;
	}
}
