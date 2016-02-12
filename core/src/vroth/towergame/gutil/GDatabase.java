package vroth.towergame.gutil;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

public class GDatabase {
	public static GDatabase instance = null;
	private HashMap<Integer, String> itemsToFile;
	private Array<Integer> itemsToFileRegister;
	
	private HashMap<String, String> elementToFile;
	
	private GDatabase() {
		createItemToFile();
		createElementToFile();
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
	}
	
	public void createElementToFile() {
		elementToFile = new HashMap<String, String>();
		elementToFile.put("heartFull", "hud/heartFull.png");
		elementToFile.put("heartHalf", "hud/heartHalf.png");
		elementToFile.put("heartEmpty", "hud/heartEmpty.png");
	}
	
	public String getItemToFile(Integer type) {
		return itemsToFile.get(type);
	}
	
	public Array<Integer> getItemsToFileRegister() {
		return itemsToFileRegister;
	}
	
	public String getElementToFile(String name) {
		return elementToFile.get(name);
	}
}
