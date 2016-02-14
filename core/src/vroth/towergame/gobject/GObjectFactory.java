package vroth.towergame.gobject;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;
import vroth.towergame.gutil.GDatabase;
import vroth.towergame.gutil.GResourcesLoader;

public class GObjectFactory {
	//private static HashMap<Integer, GObject> itemsSamples;
	private static GObjectFactory instance = null; 
	private World world;
	private GDatabase gDatabase;
	
	private GObjectFactory(World world) {
		this.world = world;
		this.gDatabase = GDatabase.getInstance();
		//createItemsSamples();
	}
	
	/*private void createItemsSamples() {
		Vector2 dummyVector = new Vector2(0, 0);
		itemsSamples = new HashMap<Integer, GObject>();
		itemsSamples.put(0x1, newSmallResource(GDatabase.getInstance().getFileFromItem(0x1), dummyVector, dummyVector));
		itemsSamples.put(0x10, newSmallResource(GDatabase.getInstance().getFileFromItem(0x10), dummyVector, dummyVector));
		itemsSamples.put(0x20, newCoin(GDatabase.getInstance().getFileFromItem(0x20), dummyVector, dummyVector));
		itemsSamples.put(0x21, newCoin(GDatabase.getInstance().getFileFromItem(0x21), dummyVector, dummyVector));
		itemsSamples.put(0x22, newCoin(GDatabase.getInstance().getFileFromItem(0x22), dummyVector, dummyVector));
	}
	
	public GObject getItemSample(int type) {
		return itemsSamples.get(type);
	}*/

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
		
		GResourcesLoader rl = GResourcesLoader.getInstance();
		Sprite front = rl.loadSprite(folder + "front.png");
		Sprite duck = rl.loadSprite(folder + "duck.png");
		Sprite hurt = rl.loadSprite(folder + "hurt.png");
		Sprite jump = rl.loadSprite(folder + "jump.png");
		Sprite stand = rl.loadSprite(folder + "stand.png");
		Sprite dead = rl.loadSprite(folder + "dead.png");
		
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
		
		Animation fly = new Animation(0.2f, climbArray);
		
		//the players shape is a little smaller to enable falling in 1 tile holes
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(front.getWidth()/3, front.getHeight()/2, new Vector2(front.getWidth()/2, front.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(front.getWidth(), front.getHeight());
		FixtureDef fixtureDef = newFixtureDef(shape, GConfig.PLAYER_DENSITY, 0, GConfig.PLAYER_FRICTION, GConfig.CATEGORY_PLAYER, GConfig.MASK_PLAYER);
		Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
				
		shape.dispose();
		
		return new GPlayer(fixture, body, duck, front, hurt, dead, jump, stand, walk, swim, climb, fly, badge1, badge2, dimension); 
	}
	
	public GCreature newCreature(String folder, Vector2 position, int health, boolean walks, boolean flies) {
		Body body = world.createBody(newBodyDef(BodyDef.BodyType.DynamicBody, position));
		
		GResourcesLoader rl = GResourcesLoader.getInstance();
		Sprite dead = rl.loadSprite(folder + "dead.png");
		
		Array<Sprite> walkArray = new Array<Sprite>();
		if(walks) {
			walkArray.add(rl.loadSprite(folder + "walk1.png"));
			walkArray.add(rl.loadSprite(folder + "walk2.png"));
		}
		Animation walk = new Animation(GConfig.ANIMATION_FRAME_TIME, walkArray);
		
		Array<Sprite> flyArray = new Array<Sprite>();
		if(flies) {
			flyArray.add(rl.loadSprite(folder + "fly1.png"));
			flyArray.add(rl.loadSprite(folder + "fly2.png"));
		}
		Animation fly = new Animation(0.2f, flyArray);
		
		//the players shape is a little smaller to enable falling in 1 tile holes
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(dead.getWidth()/3, dead.getHeight()/2, new Vector2(dead.getWidth()/2, dead.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(dead.getWidth(), dead.getHeight());
		FixtureDef fixtureDef = newFixtureDef(shape, GConfig.CREATURE_DENSITY, 0, GConfig.CREATURE_FRICTION, GConfig.CATEGORY_MONSTER, GConfig.MASK_MONSTER);
		Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
				
		shape.dispose();
		
		GCreature creature = new GCreature(fixture, body, null, null, null, null, null, dead, walk, null, null, fly, null, null, dimension, health, health);
		if(flies)
			creature.setFly();
		return creature;
	}
	
	private GTile newTile(String filePrefix, Vector2 position, int health) {
		Body body = world.createBody(newBodyDef(BodyType.StaticBody, position));
		
		Sprite[] sprites = new Sprite[16];
		for(int i = 0; i < 16; i++){
			sprites[i] = GResourcesLoader.getInstance().loadSprite(filePrefix + i + ".png");
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
	
	/*private GObject newStaticObject(String file, Vector2 position, int health) {
		Body body = world.createBody(newBodyDef(BodyType.StaticBody, position));
		
		Sprite staticSprite = GResourcesLoader.getInstance().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());
        FixtureDef fixtureDef = newFixtureDef(shape, 1f, 0, 0, GConfig.CATEGORY_FTILE, GConfig.MASK_FTILE);
        Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GObject(fixture, body, staticSprite, dimension, health, health);
	}*/
	
	private GObject newItem(String file, Vector2 position, int health) {
		Body body = world.createBody(newBodyDef(BodyType.DynamicBody, position));
		
		Sprite staticSprite = GResourcesLoader.getInstance().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);

		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());
		FixtureDef fixtureDef = newFixtureDef(shape, 500f, 0, 0, GConfig.CATEGORY_ITEM, GConfig.MASK_ITEM);
        Fixture fixture = body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GObject(fixture, body, staticSprite, dimension, health, health);
	}
	
	private GObjectResource newSmallResource(String file, Vector2 position, Vector2 velocity) {
		Body body = world.createBody(newBodyDef(BodyType.DynamicBody, position));
		
		Sprite staticSprite = GResourcesLoader.getInstance().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);

		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());
		FixtureDef fixtureDef = newFixtureDef(shape, 5f, 0, 0, GConfig.CATEGORY_ITEM, GConfig.MASK_ITEM);
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
        
        body.setTransform(new Vector2(body.getPosition().x - dimension.x/2, body.getPosition().y - dimension.y/2), 0);
        body.setLinearVelocity(velocity);
        body.setFixedRotation(true);
        
		return new GObjectResource(fixture, body, staticSprite, dimension, 35);
	}
	
	private GObjectResource newCoin(String file, Vector2 position, Vector2 velocity) {
		Body body = world.createBody(newBodyDef(BodyType.DynamicBody, position));
		
		Sprite staticSprite = GResourcesLoader.getInstance().loadSprite(file);
		
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
        
		return new GObjectResource(fixture, body, staticSprite, dimension, 25);
	}
	
	private void setBackground(GObject tile) {
		Filter filter = tile.fixture.getFilterData(); 
		filter.categoryBits = GConfig.CATEGORY_BTILE;
		filter.maskBits = GConfig.MASK_NO_TOUCH;
		tile.fixture.setFilterData(filter);
	}
	
	public void setBuilding(GObject object) {
		Filter filter = object.fixture.getFilterData(); 
		filter.categoryBits = GConfig.CATEGORY_BUILDING;
		filter.maskBits = GConfig.MASK_BUILDING;
		object.fixture.setFilterData(filter);
	}
	
	public void setCreature(GObject object) {
		Filter filter = object.fixture.getFilterData(); 
		filter.categoryBits = GConfig.CATEGORY_MONSTER;
		filter.maskBits = GConfig.MASK_MONSTER;
		object.fixture.setFilterData(filter);
	}
	
	public GTile newDirt(Vector2 position, boolean background) {
		GTile newDirt = newTile("tiles/grass", position, 30);
		if(background)
			setBackground(newDirt);
		newDirt.setType(GConfig.dirt);
		return newDirt;
	}
	
	public GTile newWater() {
		GTile newWater = newTile("tiles/lava", new Vector2(0, 0), 1);
		setBackground(newWater);
		newWater.setType(GConfig.water);
		return newWater;
	}
	
	public GTile newIron(Vector2 position, boolean background) {
		GTile newIron = newTile("tiles/grass", position, 50);
		if(background)
			setBackground(newIron);
		newIron.setType(GConfig.iron);
		return newIron;
	}
	
	public GTile newHouse(Vector2 position, boolean background) {
		GTile newHouse = newTile("tiles/houseBeige", position, 100);
		if(background)
			setBackground(newHouse);
		newHouse.setType(GConfig.house);
		return newHouse;
	}
	
	public GTile newCastle(Vector2 position, boolean background) {
		GTile newHouse = newTile("tiles/castle", position, 100);
		if(background)
			setBackground(newHouse);
		newHouse.setType(GConfig.castle);
		return newHouse;
	}
	
	public GObject newLadder(Vector2 position) {
		GObject newLadder = newTile("items/ladder/ladder", position, 25);
		newLadder.setType(GConfig.ladder);
		return newLadder;
	}
	
	public GTile newCoinTile(Vector2 position, boolean background) {
		GTile coinBox = newTile("tiles/grass", position, 80);
		if(background)
			setBackground(coinBox);
		coinBox.setType(GConfig.gold);
		return coinBox;
	}
	
	public GObject newBox(Vector2 position) {
		return newItem("tiles/boxCoin.png", position, 5);
	}
	
	public Array<GObject> newCoins(Vector2 basePosition) {
		Random r = new Random();
		Array<GObject> objects = new Array<GObject>();
		int resources = r.nextInt(GConfig.MAX_RESOURCE_RESPAWN) + GConfig.MIN_RESOURCE_RESPAWN; 
		for(int i = 0; i < resources; i++) {
			int type;
			switch(r.nextInt(3)) {
				case 0:
					type = 0x20;
					break;
				case 1:
					type = 0x21;
					break;
				default:
					type = 0x22;
					break;
			}
			String file = gDatabase.getItemToFile(type);
			Vector2 velocity = new Vector2(r.nextBoolean() ? r.nextFloat()*5 : r.nextFloat()*-5, r.nextBoolean() ? r.nextFloat()*5 : r.nextFloat()*-5);
			GObjectResource resource = newCoin(file, basePosition, velocity);
			resource.setType(type);
			objects.add(resource);
		}
		return objects;
	}
	
	public Array<GObject> newResource(int type, Vector2 basePosition) {
		Array<GObject> objects = new Array<GObject>();
		Random r = new Random();
		
		if(type == GConfig.house || type == GConfig.castle)
			type = GConfig.iron;
		
		switch(type) {
			case GConfig.gold:
				return newCoins(basePosition);
			case GConfig.dirt:
			case GConfig.iron:
				String file = gDatabase.getItemToFile(type);
				int resources = r.nextInt(GConfig.MAX_RESOURCE_RESPAWN) + GConfig.MIN_RESOURCE_RESPAWN;
				for(int i = 0; i < resources; i++) {
					Vector2 velocity = new Vector2(r.nextBoolean() ? r.nextFloat()*10 : r.nextFloat()*-10, r.nextBoolean() ? r.nextFloat()*10 : r.nextFloat()*-10);
					GObjectResource resource = newSmallResource(file, basePosition, velocity);
					resource.setType(type);
					objects.add(resource);
				}
				return objects;
			case GConfig.ladder:
				Vector2 velocity = new Vector2(r.nextBoolean() ? r.nextFloat()*10 : r.nextFloat()*-10, r.nextBoolean() ? r.nextFloat()*10 : r.nextFloat()*-10);
				GObjectResource resource = newSmallResource(gDatabase.getItemToFile(type), basePosition, velocity);
				resource.setType(type);
				objects.add(resource);
				return objects;
			default:
				break;
		}
		return objects;
	}
}
