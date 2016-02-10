package vroth.towergame.gobject;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;
import vroth.towergame.gutil.ResourcesLoader;

public class GObjectFactory {
	private static GObjectFactory instance = null;
	private World world;
	
	private GObjectFactory(World world) {
		this.world = world;
	}

	public static GObjectFactory getInstance(World world) {
		if(instance == null)
			instance = new GObjectFactory(world);
		return instance;
	}
	
	private BodyDef newBodyDef(BodyDef.BodyType bodyType, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(position);
		return bodyDef;
	}
	
	private FixtureDef newFixtureDef(Shape shape, float density, float restitution, float friction, short category, short mask) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		if(density != 0)
        	fixtureDef.density = density;
        if(restitution != 0)
        	fixtureDef.restitution = restitution;
        if(friction != 0)
        	fixtureDef.friction = friction;
        fixtureDef.filter.categoryBits = category;
        fixtureDef.filter.maskBits = mask;
		return fixtureDef;
	}
	
	
	public GPlayer newPlayer(String folder, Vector2 position) {
		Body body = world.createBody(newBodyDef(BodyDef.BodyType.DynamicBody, position));
		
		ResourcesLoader rl = ResourcesLoader.getResourcesLoader();
		Sprite front = rl.loadSprite(folder + "front.png");
		Sprite duck = rl.loadSprite(folder + "duck.png");
		Sprite hurt = rl.loadSprite(folder + "hurt.png");
		Sprite jump = rl.loadSprite(folder + "jump.png");
		Sprite stand = rl.loadSprite(folder + "stand.png");
		
		Array<Sprite> walkArray = new Array<Sprite>();
		walkArray.add(rl.loadSprite(folder + "walk01.png"));
		walkArray.add(rl.loadSprite(folder + "walk02.png"));
		walkArray.add(rl.loadSprite(folder + "walk03.png"));
		walkArray.add(rl.loadSprite(folder + "walk04.png"));
		walkArray.add(rl.loadSprite(folder + "walk05.png"));
		walkArray.add(rl.loadSprite(folder + "walk06.png"));
		walkArray.add(rl.loadSprite(folder + "walk07.png"));
		walkArray.add(rl.loadSprite(folder + "walk08.png"));
		walkArray.add(rl.loadSprite(folder + "walk09.png"));
		walkArray.add(rl.loadSprite(folder + "walk10.png"));
		walkArray.add(rl.loadSprite(folder + "walk11.png"));
		Animation walk = new Animation(GConfig.ANIMATION_FRAME_TIME, walkArray);
		
		Sprite badge1 = rl.loadSprite(folder + "badge1.png");
		Sprite badge2 = rl.loadSprite(folder + "badge2.png");
		
		Array<Sprite> swimArray = new Array<Sprite>();
		swimArray.add(rl.loadSprite(folder + "swim1.png"));
		swimArray.add(rl.loadSprite(folder + "swim2.png"));
		Animation swim = new Animation(0.2f, swimArray);
		
		Array<Sprite> climbArray = new Array<Sprite>();
		climbArray.add(rl.loadSprite(folder + "climb1.png"));
		climbArray.add(rl.loadSprite(folder + "climb2.png"));
		Animation climb = new Animation(0.2f, climbArray);
		
		//the players shape is a little smaller to enable falling in 1 tile holes
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(front.getWidth()/3, front.getHeight()/2, new Vector2(front.getWidth()/2, front.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(front.getWidth(), front.getHeight());
		FixtureDef fixtureDef = newFixtureDef(shape, GConfig.PLAYER_DENSITY, 0, GConfig.PLAYER_FRICTION, GConfig.CATEGORY_PLAYER, GConfig.MASK_PLAYER);
		Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
				
		shape.dispose();
		
		return new GPlayer(fixture, body, duck, front, hurt, jump, stand, walk, swim, climb, badge1, badge2, dimension); 
	}
	
	private GTile newTile(String filePrefix, Vector2 position, int health) {
		Body body = world.createBody(newBodyDef(BodyType.StaticBody, position));
		
		Sprite[] sprites = new Sprite[16];
		for(int i = 0; i < 16; i++){
			sprites[i] = ResourcesLoader.getResourcesLoader().loadSprite(filePrefix + i + ".png");
		}
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(sprites[0].getWidth()/2, sprites[0].getHeight()/2, new Vector2(sprites[0].getWidth()/2, sprites[0].getHeight()/2), 0);

		Vector2 dimension = new Vector2(sprites[0].getWidth(), sprites[0].getHeight());
        FixtureDef fixtureDef = newFixtureDef(shape, 0, 0, 0, GConfig.CATEGORY_FTILE, GConfig.MASK_FTILE);
        Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GTile(fixture, body, sprites, dimension, health);
	}
	
	private GObject newStaticObject(String file, Vector2 position, int health) {
		Body body = world.createBody(newBodyDef(BodyType.StaticBody, position));
		
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());
        FixtureDef fixtureDef = newFixtureDef(shape, 1f, 0, 0, GConfig.CATEGORY_FTILE, GConfig.MASK_FTILE);
        Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GObject(fixture, body, staticSprite, dimension, health);
	}
	
	private GObject newItem(String file, Vector2 position) {
		Body body = world.createBody(newBodyDef(BodyType.DynamicBody, position));
		
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);

		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());
		FixtureDef fixtureDef = newFixtureDef(shape, 500f, 0, 0, GConfig.CATEGORY_ITEM, GConfig.MASK_ITEM);
        Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GObject(fixture, body, staticSprite, dimension, 5);
	}
	
	private GObjectResource newSmallResource(String file, Vector2 position, Vector2 velocity) {
		Body body = world.createBody(newBodyDef(BodyType.DynamicBody, position));
		
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);

		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());
		FixtureDef fixtureDef = newFixtureDef(shape, 5f, 0, 0, GConfig.CATEGORY_ITEM, GConfig.MASK_ITEM);
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
        
        body.setTransform(new Vector2(body.getPosition().x - dimension.x/2, body.getPosition().y - dimension.y/2), 0);
        body.setLinearVelocity(velocity);
        body.setFixedRotation(true);
        
		return new GObjectResource(fixture, body, staticSprite, dimension, 5);
	}
	
	private GObjectResource newCoin(String file, Vector2 position, Vector2 velocity) {
		Body body = world.createBody(newBodyDef(BodyType.DynamicBody, position));
		
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(file);
		
		CircleShape shape = new CircleShape();
		shape.setPosition(new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2));
		shape.setRadius(staticSprite.getWidth()/4);

		Vector2 dimension = new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2);
		FixtureDef fixtureDef = newFixtureDef(shape, 2f, 0, 0, GConfig.CATEGORY_ITEM, GConfig.MASK_ITEM);
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
        
        body.setTransform(new Vector2(body.getPosition().x - dimension.x, body.getPosition().y - dimension.y), 0);
        body.setLinearVelocity(velocity);
        body.setFixedRotation(true);
        
		return new GObjectResource(fixture, body, staticSprite, dimension, 5);
	}
	
	private void setBackground(GObject tile) {
		Filter filter = tile.fixture.getFilterData(); 
		filter.categoryBits = GConfig.CATEGORY_BTILE;
		filter.maskBits = GConfig.MASK_NO_TOUCH;
		tile.fixture.setFilterData(filter);
	}
	
	public GTile newDirt(Vector2 position, boolean background) {
		GTile newDirt = newTile("tiles/grass", position, 30);
		if(background)
			setBackground(newDirt);
		newDirt.setType(GConfig.dirt);
		return newDirt;
	}
	
	public GObject newIron(Vector2 position, boolean background) {
		GObject newIron = newStaticObject("tiles/castleCenter.png", position, 50);
		if(background)
			setBackground(newIron);
		newIron.setType(GConfig.iron);
		return newIron;
	}
	
	public GObject newCoinBox(Vector2 position, boolean background) {
		GObject coinBox = newStaticObject("tiles/boxCoin.png", position, 80);
		if(background)
			setBackground(coinBox);
		coinBox.setType(GConfig.gold);
		return coinBox;
	}
	
	public GObject newBox(Vector2 position) {
		return newItem("tiles/boxCoin.png", position);
	}
	
	public Array<GObject> newCoins(Vector2 basePosition) {
		Random r = new Random();
		Array<GObject> objects = new Array<GObject>();
		int resources = r.nextInt(GConfig.MAX_RESOURCE_RESPAWN) + GConfig.MIN_RESOURCE_RESPAWN; 
		for(int i = 0; i < resources; i++) {
			String file;
			switch(r.nextInt(3)) {
				case 0:
					file = "items/coinBronze.png";
					break;
				case 1:
					file = "items/coinSilver.png";
					break;
				default:
					file = "items/coinGold.png";
					break;
			}
			Vector2 velocity = new Vector2(r.nextBoolean() ? r.nextFloat()*5 : r.nextFloat()*-5, r.nextBoolean() ? r.nextFloat()*5 : r.nextFloat()*-5);
			objects.add(newCoin(file, basePosition, velocity));
		}
		return objects;
	}
	
	public Array<GObject> newResource(int type, Vector2 basePosition) {
		if(type == GConfig.gold)
			return newCoins(basePosition);
		
		String file;
		switch(type) {
			case GConfig.iron:
				file = "items/particleIron.png";
				break;
			case GConfig.dirt:
				file = "items/particleDirt.png";
				break;
			default:
				file = "err.png";
				break;
		}
		Random r = new Random();
		Array<GObject> objects = new Array<GObject>();
		int resources = r.nextInt(GConfig.MAX_RESOURCE_RESPAWN) + GConfig.MIN_RESOURCE_RESPAWN;
		for(int i = 0; i < resources; i++) {
			Vector2 velocity = new Vector2(r.nextBoolean() ? r.nextFloat()*10 : r.nextFloat()*-10, r.nextBoolean() ? r.nextFloat()*10 : r.nextFloat()*-10);
			objects.add(newSmallResource(file, basePosition, velocity));
		}
		return objects;
	}
}
