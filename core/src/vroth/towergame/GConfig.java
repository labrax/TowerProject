package vroth.towergame;

import com.badlogic.gdx.graphics.Color;

public class GConfig {
	public static final String GAME_NAME = "TowerProject";
	
	public static final String ASSET_PATH = "C:/Users/Victor/Desktop/assets/";

	public static int GENERATION_HEIGHT = 30, GENERATION_WIDTH = 100, GENERATION_TRIES = 100, MAX_AMOUNT_IRON = 35, TILE_SPACING = 70;
	/*public static final int iron = 0x10, gold = 0x11, nothing = 0x0, dirt = 0x1, water = 0x2, coin_bronze = 0x20, coin_silver = 0x21, coin_gold = 0x22;
	public static final int grass = 0xC0;
	public static final int castle = 0xD0;
	public static final int house = 0xE0;
	public static final int ladder = 0xFF;*/
	
	public static enum TYPES {ERR, NOTHING, PICKAXE, IRON, COINS, DIRT, GRASS, CASTLE, HOUSE, LADDER, WATER, BRONZE_COIN, SILVER_COIN, GOLD_COIN, PARTICLE_DIRT, PARTICLE_IRON, DEAD};
	
	public static int ON_GAME_RESOURCE_TIME = 15;
	public static int MAX_RESOURCES_ON_GAME = 200;
	public static int MAX_CREATURES_ON_GAME = 40;
	
	public static final int AMOUNT_ITEMS = 5;
	
	public static final int DISTANCE_RESOURCE_APPROACH = 10000, DISTANCE_RESOURCE_COLLECT = 2000;
	
	public static final int MIN_WATER_DAMAGE = 1, MAX_WATER_DAMAGE = 5;
	
	public static final int DAMAGE = 100;
	public static boolean DRAW_PLAYER_HEALTH = false;
	public static final int PLAYER_HEALTH = 50;
	public static int MAX_JUMP = 2;
	public static final float PLAYER_ITEM_FONT_SIZE = 0.15f;
	public static final int DEAD_TIME = 2;
	public static final float PLAYER_RANGE = 250f;
	public static boolean DRAW_RANGE = false;
	public static float MIN_HURT_TIME = 0.4f;
	public static String PLAYER_FOLDER = "p1/";
	
	public static final float CREDITS_TIME = 15f; 
	
	public static final String MAP_FILE = "map.dat";
	
	public static int MAP_WIDTH = 2*GENERATION_WIDTH;
	
	public static final int MAX_RESOURCE_RESPAWN = 10, MIN_RESOURCE_RESPAWN = 1;
	
	public static int SCREEN_HEIGHT, SCREEN_WIDTH;
	
	public static Color BACKGROUND_COLOR = new Color(0.70f, 0.85f, 1.0f, 1.0f);
	
	//TODO: implement
	//public static float SCALING = 0.8f;
	
	public static float SPEED_FLY = (float) Math.pow(10, 32);
	
	public static float CREATURE_DENSITY = 500000f;
	public static float CREATURE_FRICTION = 30f;
	
	public static float HURT_TIME = 0.8f;
	public static float ANIMATION_FRAME_TIME = 0.1f;
	public static float FORCE_UP = (float) Math.pow(10, 32);
	public static float SPEED_WALK = (float) Math.pow(10, 14);
	public static float EPSILON = 0.05f;
	public static float PLAYER_DENSITY = CREATURE_DENSITY;
	public static float PLAYER_FRICTION = CREATURE_FRICTION;
	
	public static boolean DEBUG_PHYSICS = false;
	public static boolean DEBUG_CONTROLS = false;
	
	//collision tests
	public static final short CATEGORY_FTILE = 0x0001,
			CATEGORY_BTILE = 0x0002,
			CATEGORY_PLAYER = 0x0004, 
			CATEGORY_MONSTER = 0x0008,
			CATEGORY_ITEM = 0x0016,
			CATEGORY_BUILDING = 0x032;
	
	public static final short MASK_NO_TOUCH = 0,
			MASK_FTILE = -1 & (~CATEGORY_BTILE),
			MASK_PLAYER = CATEGORY_FTILE ,
			MASK_MONSTER = CATEGORY_BUILDING | CATEGORY_FTILE,
			MASK_ITEM = CATEGORY_FTILE,
			MASK_BUILDING = CATEGORY_MONSTER,
			MASK_ALL = -1;
}
