package vroth.towergame.gutil;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

import vroth.towergame.GConfig;

public class GResourcesLoader {
	private static GResourcesLoader instance = null;
	
	private HashMap<String, Sprite> cache = new HashMap<String, Sprite>();
	private HashMap<String, Texture> textureCache = new HashMap<String, Texture>();
	private HashMap<String, Pixmap> pixmapCache = new HashMap<String, Pixmap>();
	private Sprite errSprite = null;
	private Texture errTexture = null;
	private Pixmap errPixmap = null;
	
	private GResourcesLoader() {
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
		this.errTexture = errTexture;
		errPixmap = errTexture.getTextureData().consumePixmap();
		errSprite = new Sprite(errTexture);
	}
	
	public static GResourcesLoader getInstance() {
		if(instance == null)
			instance = new GResourcesLoader();
		return instance;
	}
	
	private Sprite loadSpriteFile(String file) {
		try {
			Texture t = new Texture(GConfig.ASSET_PATH + file);
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
	
	private Texture loadTextureFile(String file) {
		try {
			Texture t = new Texture(GConfig.ASSET_PATH + file);
			return t;
		}
		catch(Exception e) {
			System.err.println("Error loading file " + file);
			return errTexture;
		}
	}
	
	public Texture loadTexture(String file) {
		if(textureCache.containsKey(file)) {
			return textureCache.get(file);
		}
		else {
			Texture loaded = loadTextureFile(file);
			textureCache.put(file, loaded);
			return loaded;
		}
	}
	
	private Pixmap loadPixmapFile(String file) {
		try {
			Pixmap t = new Pixmap(Gdx.files.internal(GConfig.ASSET_PATH + file));
			return t;
		}
		catch(Exception e) {
			System.err.println("Error loading file " + file);
			return errPixmap;
		}
	}
	
	public Pixmap loadPixmap(String file) {
		if(pixmapCache.containsKey(file)) {
			return pixmapCache.get(file);
		}
		else {
			Pixmap loaded = loadPixmapFile(file);
			pixmapCache.put(file, loaded);
			return loaded;
		}
	}
	
	public Sprite getErrSprite() {
		return errSprite;
	}
	
	public BitmapFont getFont(String file) {
		BitmapFont font;
		try {
			font = new BitmapFont(Gdx.files.internal(GConfig.ASSET_PATH + file));
		}
		catch (Exception e) {
			e.printStackTrace();
			font = new BitmapFont();
		}
		return font;
	}
}
