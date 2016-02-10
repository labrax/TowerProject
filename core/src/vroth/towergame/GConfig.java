package vroth.towergame;

import com.badlogic.gdx.graphics.Color;

public class GConfig {
	public static String GAME_NAME = "TowerProject";
	
	public static int MAP_WIDTH = 30, INITIAL_HEIGHT = 20;

	public static int GENERATION_HEIGHT = 10, GENERATION_TRIES = 20, MAX_AMOUNT_IRON = 8;
	
	public static int SCREEN_HEIGHT, SCREEN_WIDTH;
	
	public static Color BACKGROUND_COLOR = new Color(0.70f, 0.85f, 1.0f, 1.0f);
	
	public static float SCALING = 0.8f;
	
	public static float HURT_TIME = 0.8f;
	public static float ANIMATION_FRAME_TIME = 0.1f;
	public static float SPEED_UP = 10000f;
	public static float SPEED_WALK = 500f;
	public static float EPSILON = 0.05f;
	public static float PLAYER_DENSITY = 50000f;
	public static float PLAYER_FRICTION = 50f;
	
	//collision tests
	public static final short CATEGORY_FTILE = 0x0001,
			CATEGORY_BTILE = 0x0002,
			CATEGORY_PLAYER = 0x0004, 
			CATEGORY_MONSTER = 0x0008;
	
	public static final short MASK_NO_TOUCH = 0,
			MASK_FTILE = -1 & (~CATEGORY_BTILE),
			MASK_PLAYER = CATEGORY_FTILE | CATEGORY_MONSTER,
			MASK_MONSTER = CATEGORY_FTILE | CATEGORY_PLAYER,
			MASK_ALL = -1;
}
