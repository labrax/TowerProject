package vroth.towergame.gutil;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

public class GDatabase {
	public static GDatabase instance = null;
	private HashMap<Integer, String> itemsToFile;
	private Array<Integer> itemsToFileRegister;
	
	private GDatabase() {
		createItemToFile();
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
	
	public String getFileFromItem(Integer type) {
		return itemsToFile.get(type);
	}
	
	public Array<Integer> getItemsToFileRegister() {
		return itemsToFileRegister;
	}
}
