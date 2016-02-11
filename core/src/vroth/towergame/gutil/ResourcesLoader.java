package vroth.towergame.gutil;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ResourcesLoader {
	private HashMap<String, Sprite> cache = new HashMap<String, Sprite>();
	private static ResourcesLoader instance = null;
	private Sprite errSprite = null;
	
	private ResourcesLoader() {
		Pixmap p = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
		Texture errTexture = new Texture(p);
		p.setColor(Color.WHITE);
		p.fillRectangle(0, 0, 63, 63);
		p.setColor(Color.RED);
		p.fillRectangle(1, 1, 61, 61);
		p.setColor(Color.BLACK);
		p.drawLine(0, 0, 63, 63);
		p.drawLine(0, 63, 63, 0);
		errTexture.draw(p, 0, 0);
		errSprite = new Sprite(errTexture);
	}

	public static ResourcesLoader getResourcesLoader() {
		if(instance == null)
			instance = new ResourcesLoader();
		return instance;
	}
	
	
	private Sprite loadSpriteFile(String file) {
		try {
			Texture t = new Texture("assets/" + file);
			return new Sprite(t);
		}
		catch(Exception e) {
			System.err.println("Error loading file " + file);
			return errSprite;
		}
	}
	
	public Sprite loadSprite(String file) {
		if(cache.containsKey(file)) {
			return cache.get(file);
		}
		else {
			Sprite loaded = loadSpriteFile(file);
			cache.put(file, loaded);
			return loaded;
		}
	}
	
	public Sprite getErrSprite() {
		return errSprite;
	}
	
	public BitmapFont getFont(String file) {
		BitmapFont font;
		try {
			font = new BitmapFont(Gdx.files.internal(file));
		}
		catch (Exception e) {
			e.printStackTrace();
			font = new BitmapFont();
		}
		return font;
	}
}
