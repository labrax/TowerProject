package vroth.towergame.gutil;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.gobject.GPlayer;

public class GDatabase {
	public static GDatabase instance = null;
	private HashMap<Integer, String> itemsToFile;
	private Array<Integer> itemsToFileRegister;
	
	private HashMap<Integer, String> itemsToCursor;
	private Array<Integer> itemsToCursorRegister;
	
	private HashMap<String, String> elementToFile;
	
	private HashMap<String, Vector2> stateToRefVector;
	
	private GDatabase() {
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
	
	private void createItemToFileAux(Integer item, String file) {
		itemsToFileRegister.add(item);
		itemsToFile.put(item, file);
	}
	
	private void createItemToFile() {
		itemsToFile = new HashMap<Integer,String>();
		itemsToFileRegister = new Array<Integer>();
		createItemToFileAux(0x1, "items/particleDirt.png");
		createItemToFileAux(0x10, "items/particleIron.png");
		createItemToFileAux(0x20, "items/coinBronze.png");
		createItemToFileAux(0x21, "items/coinSilver.png");
		createItemToFileAux(0x22, "items/coinGold.png");
		createItemToFileAux(0xFF, "items/ladder/ladder.png");
	}
	
	public String getItemToFile(Integer type) {
		return itemsToFile.get(type);
	}
	
	public Array<Integer> getItemsToFileRegister() {
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
	
	private void createItemToCursorAux(int key, String value) {
		itemsToCursor.put(key, value);
		itemsToCursorRegister.add(key);
	}
	
	private void createItemsToCursor() {
		itemsToCursor = new HashMap<Integer, String>();
		itemsToCursorRegister = new Array<Integer>();
		createItemToCursorAux(0x0, "cursor/pickaxeCursor.png");
		createItemToCursorAux(0x1, "cursor/grass.png");
		//createItemToCursorAux(0x10, "cursor/houseBeige.png");
		createItemToCursorAux(0xD0, "cursor/castle.png");
		createItemToCursorAux(0xFE, "cursor/swordCursor.png");
		createItemToCursorAux(0xFF, "cursor/ladder.png");
	}
	
	public String getItemsToCursor(int type) {
		return itemsToCursor.get(type);
	}
	
	public int getItemsFromCursorIndex(int id) {
		return itemsToCursorRegister.get(id);
	}
}
