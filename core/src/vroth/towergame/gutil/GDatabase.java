package vroth.towergame.gutil;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;
import vroth.towergame.gobject.GPlayer;

public class GDatabase {
	private Random random;
	
	public static GDatabase instance = null;
	private HashMap<GConfig.TYPES, String> itemsToFile;
	private Array<GConfig.TYPES> itemsToFileRegister;
	
	private HashMap<GConfig.TYPES, String> itemsToCursor;
	private Array<GConfig.TYPES> itemsToCursorRegister;
	
	private HashMap<String, String> elementToFile;
	
	private HashMap<String, Vector2> stateToRefVector;
	
	private GDatabase() {
		random = new Random();
		createItemToFile();
		createElementToFile();
		createStateToRefVector();
		createItemsToCursor();
	}
	
	public static GDatabase getInstance() {
		if(instance == null)
			instance = new GDatabase();
		return instance;
	}
	
	public Random getRandom() {
		return random;
	}
	
	private void createItemToFileAux(GConfig.TYPES item, String file) {
		itemsToFileRegister.add(item);
		itemsToFile.put(item, file);
	}
	
	private void createItemToFile() {
		itemsToFile = new HashMap<GConfig.TYPES,String>();
		itemsToFileRegister = new Array<GConfig.TYPES>();
		createItemToFileAux(GConfig.TYPES.PARTICLE_DIRT, "items/particleDirt.png");
		createItemToFileAux(GConfig.TYPES.PARTICLE_IRON, "items/particleIron.png");
		createItemToFileAux(GConfig.TYPES.BRONZE_COIN, "items/coinBronze.png");
		createItemToFileAux(GConfig.TYPES.SILVER_COIN, "items/coinSilver.png");
		createItemToFileAux(GConfig.TYPES.GOLD_COIN, "items/coinGold.png");
		createItemToFileAux(GConfig.TYPES.GRASS, "items/grass.png");
		createItemToFileAux(GConfig.TYPES.CASTLE, "items/castle.png");
		createItemToFileAux(GConfig.TYPES.HOUSE, "items/houseBeige.png");
		createItemToFileAux(GConfig.TYPES.LADDER, "items/ladder.png");
	}
	
	public String getItemToFile(GConfig.TYPES type) {
		return itemsToFile.get(type);
	}
	
	public Array<GConfig.TYPES> getItemsToFileRegister() {
		return itemsToFileRegister;
	}
	
	private void createElementToFile() {
		elementToFile = new HashMap<String, String>();
		elementToFile.put("heartFull", "hud/heartFull.png");
		elementToFile.put("heartHalf", "hud/heartHalf.png");
		elementToFile.put("heartEmpty", "hud/heartEmpty.png");
	}
	
	public String getElementToFile(String name) {
		return elementToFile.get(name);
	}
	
	//TODO: implement
	private void createStateToRefVectorAux(GPlayer.STATE state, int index, int x, int y) {
		stateToRefVector.put(new String(state.toString()+index), new Vector2(x, y));
	}
	
	private void createStateToRefVector() {
		stateToRefVector = new HashMap<String, Vector2>();
		createStateToRefVectorAux(GPlayer.STATE.DUCK, 0, 53, 4);
	}
	
	public Vector2 getStateToRefVector(GPlayer.STATE currState, int frameIndex) {
		return stateToRefVector.get(new String(currState.toString()+frameIndex));
	}
	
	private void createItemToCursorAux(GConfig.TYPES key, String value) {
		itemsToCursor.put(key, value);
		itemsToCursorRegister.add(key);
	}
	
	private void createItemsToCursor() {
		itemsToCursor = new HashMap<GConfig.TYPES, String>();
		itemsToCursorRegister = new Array<GConfig.TYPES>();
		createItemToCursorAux(GConfig.TYPES.PICKAXE, "cursor/pickaxeCursor.png");
		//createItemToCursorAux(0x1, "cursor/grass.png");
		//createItemToCursorAux(0x10, "cursor/houseBeige.png");
		createItemToCursorAux(GConfig.TYPES.GRASS, "cursor/grass.png");
		createItemToCursorAux(GConfig.TYPES.CASTLE, "cursor/castle.png");
		createItemToCursorAux(GConfig.TYPES.HOUSE, "cursor/houseBeige.png");
		//createItemToCursorAux(0xFE, "cursor/swordCursor.png");
		createItemToCursorAux(GConfig.TYPES.LADDER, "cursor/ladder.png");
		createItemToCursorAux(GConfig.TYPES.ERR, "cursor/farCursor.png");
		createItemToCursorAux(GConfig.TYPES.DEAD, "cursor/dead.png");
	}
	
	public String getItemsToCursor(GConfig.TYPES type) {
		return itemsToCursor.get(type);
	}
	
	public GConfig.TYPES getItemsFromCursorIndex(int id) {
		return itemsToCursorRegister.get(id);
	}
}
