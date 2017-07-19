package org.minimumcosmic.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader;

import org.minimumcosmic.game.entity.components.*;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.components.modules.EngineModuleComponent;
import org.minimumcosmic.game.entity.components.modules.FinsModuleComponent;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;
import org.minimumcosmic.game.entity.systems.RenderingSystem;
import org.minimumcosmic.game.parallax.Parallax;
import org.minimumcosmic.game.parallax.ParallaxLayer;
import org.minimumcosmic.game.parallax.TexturedParallaxLayer;

import java.io.IOException;
import java.util.Random;

public class ObjectFactory {

    public static final float WORLD_WIDTH = RenderingSystem.WORLD_WIDTH;
    public static final float WORLD_HEIGHT = RenderingSystem.WORLD_HEIGHT * 40;

    private BodyFactory bodyFactory;
    public World world;
    private PooledEngine engine;
    public final Random rand;


    public ObjectFactory(PooledEngine engine) {
        this.engine = engine;
        rand = new Random();

        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new B2dContactListener());
        bodyFactory = BodyFactory.getInstance(world);
    }

    public void dispose() {
        bodyFactory.deleteAllBodies();
    }

    public void deleteBody(Body body) {
        bodyFactory.deleteBody(body);
    }

    public void generateWorld(TextureAtlas atlas) {
        int y = 0;
        while (y < WORLD_HEIGHT) {
            float x = rand.nextFloat() * WORLD_WIDTH;
            if (rand.nextFloat() > 0.8f) {
                createMoney(x + MathUtils.random(-0.5f, 0.5f), y + rand.nextFloat() * 3, atlas);
            }
            if (rand.nextFloat() > 0.975f) {
                createEnemy(x + MathUtils.random(-0.5f, 0.5f), y + rand.nextFloat() * 3, atlas);
            }
            y += rand.nextFloat() * (10 * (Gdx.graphics.getHeight() / 800f));

        }

        createStation(15, WORLD_HEIGHT, atlas);
    }


    public Entity createEnemy(float x, float y, TextureAtlas atlas) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
        b2dBody.body = bodyFactory.makeBoxBody(x, y, 5f, 2f, BodyFactory.STONE, BodyDef.BodyType.StaticBody);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.ENEMY;

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.collisionEntity = entity;

        TextureComponent textComp = engine.createComponent(TextureComponent.class);
        textComp.region = atlas.findRegion("plane" + (rand.nextInt((5 - 1) + 1) + 1));
        EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(0, 0, 0);
        position.scale.x = 0.5f * Gdx.graphics.getWidth() / 480.0f;
        position.scale.y = 0.5f * Gdx.graphics.getHeight() / 800.0f;

        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);

        b2dBody.body.setUserData(entity);

        entity.add(b2dBody);
        entity.add(position);
        entity.add(textComp);
        entity.add(enemyComponent);
        entity.add(boundsComponent);
        entity.add(type);
        entity.add(collision);

        engine.addEntity(entity);

        return entity;
    }

    public void generateParallaxBackground(TextureAtlas atlas, Parallax parallax) {
        TextureRegion startRegion = atlas.findRegion("startingplace");
        TexturedParallaxLayer startLayer =
                new TexturedParallaxLayer(startRegion, WORLD_WIDTH,
                        new Vector2(.5f, .5f), TexturedParallaxLayer.WH.width);

        TextureRegion bush1Region = atlas.findRegion("bush1");
        TexturedParallaxLayer bush1Layer =
                new TexturedParallaxLayer(bush1Region, WORLD_WIDTH,
                        new Vector2(.45f, .45f), TexturedParallaxLayer.WH.width);

        TextureRegion bush2Region = atlas.findRegion("bush2");
        TexturedParallaxLayer bush2Layer =
                new TexturedParallaxLayer(bush2Region, WORLD_WIDTH,
                        new Vector2(.4f, .4f), TexturedParallaxLayer.WH.width);

        TextureRegion bush3Region = atlas.findRegion("bush3");
        TexturedParallaxLayer bush3Layer =
                new TexturedParallaxLayer(bush3Region, WORLD_WIDTH,
                        new Vector2(.35f, .35f), TexturedParallaxLayer.WH.width);

        TextureRegion bush4Region = atlas.findRegion("bush4");
        TexturedParallaxLayer bush4Layer =
                new TexturedParallaxLayer(bush4Region, WORLD_WIDTH,
                        new Vector2(.3f, .3f), TexturedParallaxLayer.WH.width);

        TextureRegion bush5Region = atlas.findRegion("bush5");
        TexturedParallaxLayer bush5Layer =
                new TexturedParallaxLayer(bush5Region, WORLD_WIDTH,
                        new Vector2(.3f, .3f), TexturedParallaxLayer.WH.width);

        TextureRegion bush6Region = atlas.findRegion("bush6");
        TexturedParallaxLayer bush6Layer =
                new TexturedParallaxLayer(bush6Region, WORLD_WIDTH,
                        new Vector2(.25f, .25f), TexturedParallaxLayer.WH.width);

        TextureRegion cloud1Region = atlas.findRegion("cloud1");


        TexturedParallaxLayer cloud1Layer1 =
                new TexturedParallaxLayer(cloud1Region, WORLD_WIDTH / 2,
                        new Vector2(.15f, .15f), TexturedParallaxLayer.WH.width);
        cloud1Layer1.setPadBottom(RenderingSystem.WORLD_HEIGHT * 0.467f);
        cloud1Layer1.setPadLeft(WORLD_WIDTH * 0.267f);
        cloud1Layer1.setTileModeY(ParallaxLayer.TileMode.repeat);

        TexturedParallaxLayer cloud1Layer2 =
                new TexturedParallaxLayer(cloud1Region, WORLD_WIDTH / 2,
                        new Vector2(.075f, .075f), TexturedParallaxLayer.WH.width);
        cloud1Layer2.setPadBottom(RenderingSystem.WORLD_HEIGHT * 0.667f);
        cloud1Layer2.setPadLeft(WORLD_WIDTH * -0.167f);
        cloud1Layer2.setTileModeY(ParallaxLayer.TileMode.repeat);

        TexturedParallaxLayer cloud1Layer3 =
                new TexturedParallaxLayer(cloud1Region, WORLD_WIDTH / 2,
                        new Vector2(.125f, .125f), TexturedParallaxLayer.WH.width);
        cloud1Layer3.setPadBottom(RenderingSystem.WORLD_HEIGHT * 0.767f);
        cloud1Layer3.setPadLeft(WORLD_WIDTH * 0.767f);
        cloud1Layer3.setTileModeY(ParallaxLayer.TileMode.repeat);


        TextureRegion cloud2Region = atlas.findRegion("cloud2");

        TexturedParallaxLayer cloud2Layer1 =
                new TexturedParallaxLayer(cloud2Region, WORLD_WIDTH / 2,
                        new Vector2(.1f, .1f), TexturedParallaxLayer.WH.width);
        cloud2Layer1.setPadBottom(RenderingSystem.WORLD_HEIGHT * 0.267f);
        cloud2Layer1.setTileModeY(ParallaxLayer.TileMode.repeat);

        TexturedParallaxLayer cloud2Layer2 =
                new TexturedParallaxLayer(cloud2Region, WORLD_WIDTH / 2,
                        new Vector2(.15f, .15f), TexturedParallaxLayer.WH.width);
        cloud2Layer2.setPadBottom(RenderingSystem.WORLD_HEIGHT * 0.717f);
        cloud1Layer3.setPadLeft(WORLD_WIDTH * 0.717f);
        cloud2Layer2.setTileModeY(ParallaxLayer.TileMode.repeat);

        TextureRegion cloud3Region = atlas.findRegion("cloud3");

        TexturedParallaxLayer cloud3Layer =
                new TexturedParallaxLayer(cloud3Region, WORLD_WIDTH / 2,
                        new Vector2(.175f, .175f), TexturedParallaxLayer.WH.width);
        cloud3Layer.setPadBottom(RenderingSystem.WORLD_HEIGHT * 0.367f);
        cloud3Layer.setPadLeft(WORLD_WIDTH * 0.667f);
        cloud3Layer.setTileModeY(ParallaxLayer.TileMode.repeat);


        TextureRegion bush7Region = atlas.findRegion("bush7");
        TexturedParallaxLayer bush7Layer =
                new TexturedParallaxLayer(bush7Region, WORLD_WIDTH,
                        new Vector2(.25f, .25f), TexturedParallaxLayer.WH.width);

        TextureRegion gradient1Region = atlas.findRegion("gradient1");
        TexturedParallaxLayer gradient1Layer =
                new TexturedParallaxLayer(gradient1Region, WORLD_WIDTH,
                        new Vector2(.025f, .025f), TexturedParallaxLayer.WH.width);

        TextureRegion gradient2Region = atlas.findRegion("gradient2");
        TexturedParallaxLayer gradient2Layer =
                new TexturedParallaxLayer(gradient2Region, WORLD_WIDTH,
                        new Vector2(.025f, .025f), TexturedParallaxLayer.WH.width);
        gradient2Layer.setPadBottom(RenderingSystem.WORLD_HEIGHT * 0.9f);

        parallax.addLayers(gradient2Layer, gradient1Layer, bush7Layer,
                cloud1Layer1, cloud1Layer2, cloud1Layer3, cloud2Layer1, cloud2Layer2, cloud3Layer,
                bush6Layer, bush5Layer, bush4Layer, bush3Layer, bush2Layer, bush1Layer,
                startLayer);

    }

    public void generateParallaxForeground(TextureAtlas atlas, Parallax parallax) {
        TextureRegion groundRegion = atlas.findRegion("ground");
        TexturedParallaxLayer groundLayer =
                new TexturedParallaxLayer(groundRegion, WORLD_WIDTH,
                        new Vector2(.6f, .6f), TexturedParallaxLayer.WH.width);
        parallax.addLayers(groundLayer);

    }

    // Create a platform
    public void createPlatform(float x, float y, TextureRegion texture) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxBody(x, y, 1.5f, 0.2f, BodyFactory.STONE, BodyDef.BodyType.StaticBody);

        TextureComponent textComp = engine.createComponent(TextureComponent.class);
        textComp.region = texture;

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(x, y, 2);

        b2dbody.body.setUserData(entity);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        entity.add(b2dbody);
        entity.add(position);
        entity.add(textComp);
        entity.add(type);

        engine.addEntity(entity);
    }

    public Entity createStation(float x, float y, TextureAtlas atlas) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
        b2dBody.body = bodyFactory.makeSensorBody(x, y,
                10 * Gdx.graphics.getWidth() / 480.0f, BodyDef.BodyType.StaticBody, true);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.OTHER;

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.collisionEntity = entity;
        TextureComponent textComp = engine.createComponent(TextureComponent.class);
        textComp.region = atlas.findRegion("station");

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(0, 0, 0);
        position.scale.x = 0.5f * Gdx.graphics.getWidth() / 480.0f;
        position.scale.y = 0.5f * Gdx.graphics.getHeight() / 800.0f;

        b2dBody.body.setUserData(entity);

        entity.add(b2dBody);
        entity.add(position);
        entity.add(textComp);
        entity.add(type);
        entity.add(collision);

        engine.addEntity(entity);

        return entity;
    }

    // Create a floor entity
    public void createFloor(TextureRegion texture) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
        b2dBody.body = bodyFactory.makeBoxBody(0, 0, 100, 0.2f,
                BodyFactory.STONE, BodyDef.BodyType.StaticBody);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        //TextureComponent textComp = engine.createComponent(TextureComponent.class);
        //textComp.region = texture;

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(0, 0, 0);

        b2dBody.body.setUserData(entity);

        entity.add(b2dBody);
        entity.add(position);
        //entity.add(textComp);
        entity.add(type);

        engine.addEntity(entity);
    }

    public Entity createMoney(float x, float y, TextureAtlas atlas) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
        b2dBody.body = bodyFactory.makeSensorBody(x, y, Gdx.graphics.getWidth() / 480.0f, BodyDef.BodyType.StaticBody, true);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.PICKUP;

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.collisionEntity = entity;

        TextureComponent textComp = engine.createComponent(TextureComponent.class);
        textComp.region = atlas.findRegion("icon");

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(0, 0, 0);
        position.scale.x = Gdx.graphics.getWidth() / 480.0f;
        position.scale.y = Gdx.graphics.getHeight() / 800.0f;

        b2dBody.body.setUserData(entity);

        entity.add(b2dBody);
        entity.add(position);
        entity.add(textComp);
        entity.add(type);
        entity.add(collision);

        engine.addEntity(entity);

        return entity;
    }


    public Entity createRocket(TextureAtlas atlas, OrthographicCamera camera, String filePath, Vector2 rp) {
        return createRocket(atlas, camera, filePath, rp, null);
    }

    public Entity createRocket(TextureAtlas atlas, OrthographicCamera camera, String filePath) {
        return createRocket(atlas, camera, filePath, null, null);
    }

    public Entity createRocket(TextureAtlas atlas, OrthographicCamera camera, String filePath, ParticleEffect pe) {
        return createRocket(atlas, camera, filePath, null, pe);
    }

    public Entity createRocket(TextureAtlas atlas, OrthographicCamera camera, String filePath, Vector2 rp, ParticleEffect pe) {
        XmlReader xmlReader = new XmlReader();
        try {
            FileHandle fileHandle = Gdx.files.local(filePath);
            XmlReader.Element root;
            if (fileHandle.exists()) {
                root = xmlReader.parse(Gdx.files.local(filePath));
            } else {
                root = xmlReader.parse(Gdx.files.internal(filePath));
            }
            Vector2 rocketPosition = new Vector2();
            if (rp != null) {
                rocketPosition = rp;
            } else {
                rocketPosition.x = root.getChildByName("Position").getFloat("x");
                rocketPosition.y = root.getChildByName("Position").getFloat("y");
            }

            // Create an empty entity
            Entity entity = engine.createEntity();

            // Add components
            B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
            TransformComponent position = engine.createComponent(TransformComponent.class);
            CameraComponent player = engine.createComponent(CameraComponent.class);
            RocketComponent rocketComponent = engine.createComponent(RocketComponent.class);
            ParticleEffectComponent partEffComponent = engine.createComponent(ParticleEffectComponent.class);
            BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
            CollisionComponent collision = engine.createComponent(CollisionComponent.class);
            TypeComponent type = engine.createComponent(TypeComponent.class);
            PickupComponent pickup = engine.createComponent(PickupComponent.class);

            type.type = TypeComponent.PLAYER;
            collision.collisionEntity = entity;

            // Empty texture to render the particle effect
            TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

            rocketComponent.headModule =
                    createHeadModule(rocketPosition, atlas,
                            root.getChildByName("HeadModule").getInt("id"), true);

            rocketComponent.bodyModule =
                    createBodyModule(rocketPosition, atlas,
                            root.getChildByName("BodyModule").getInt("id"), true);

            rocketComponent.finsModule =
                    createFinsModule(rocketPosition, atlas,
                            root.getChildByName("FinsModule").getInt("id"), true);

            rocketComponent.engineModule =
                    createEngineModule(rocketPosition, atlas,
                            root.getChildByName("EngineModule").getInt("id"), true);

            rocketComponent.health = 10000;

            player.camera = camera;

            float scaleX = root.getChildByName("Scale").getInt("factor") * Gdx.graphics.getWidth() / 480f;
            float scaleY = root.getChildByName("Scale").getInt("factor") * Gdx.graphics.getHeight() / 800f;

            if (pe != null) {
                partEffComponent.particleEffect = pe;
                pe.getEmitters().first().setPosition(rocketPosition.x,
                        rocketPosition.y - 3 * root.getChildByName("Scale").getInt("factor"));
                entity.add(partEffComponent);
            }

            Vector2[] vertices = new Vector2[4];
            vertices[0] = new Vector2(-2 * scaleX, -3 * scaleY);
            vertices[1] = new Vector2(2 * scaleX, -3 * scaleY);
            vertices[2] = new Vector2(1 * scaleX, 2 * scaleY);
            vertices[3] = new Vector2(-1 * scaleX, 2 * scaleY);

            b2dBody.body = bodyFactory.makePolygonBody(
                    rocketPosition.x,
                    rocketPosition.y,
                    vertices,
                    BodyFactory.STEEL,
                    BodyDef.BodyType.DynamicBody,
                    false);
            b2dBody.body.setUserData(entity);

            entity.add(b2dBody);
            entity.add(position);
            entity.add(player);
            entity.add(rocketComponent);
            entity.add(textureComponent);
            entity.add(boundsComponent);
            entity.add(collision);
            entity.add(type);
            entity.add(pickup);

            //add entity to engine
            engine.addEntity(entity);
            return entity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity createHeadModule(Vector2 position, TextureAtlas atlas, int id, boolean engineAdding) {
        XmlReader xmlReader = new XmlReader();
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/modules.xml"));
            XmlReader.Element module = root.getChildByName("HeadModules").getChild(id - 1);
            Entity headModule = createHeadModule(position.x, position.y,
                    atlas.findRegion("head_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("hp"),
                    module.getChildByName("AdvancedProperties").getInt("power"),
                    module.getChildByName("AdvancedProperties").getInt("fuel"),
                    engineAdding);
            headModule.getComponent(HeadModuleComponent.class).id = id;

            return headModule;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity createHeadModule(float x, float y, TextureRegion texture, int weight, int cost, int hp, int power, int fuel, boolean engineAdding) {
        Entity entity = engine.createEntity();

        TransformComponent position = engine.createComponent(TransformComponent.class);
        HeadModuleComponent headModuleComponent = engine.createComponent(org.minimumcosmic.game.entity.components.modules.HeadModuleComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        headModuleComponent.weight = weight;
        headModuleComponent.cost = cost;
        headModuleComponent.fuel = fuel;
        headModuleComponent.healthPoints = hp;
        headModuleComponent.power = power;

        position.position.set(x, y, 0);
        position.scale.x = Gdx.graphics.getWidth() / 480.0f;
        position.scale.y = Gdx.graphics.getHeight() / 800.0f;
        textureComponent.region = texture;

        entity.add(position);
        entity.add(headModuleComponent);
        entity.add(textureComponent);

        if (engineAdding) {
            engine.addEntity(entity);
        }

        return entity;
    }

    public Entity createBodyModule(Vector2 position, TextureAtlas atlas, int id, boolean engineAdding) {
        XmlReader xmlReader = new XmlReader();
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/modules.xml"));
            XmlReader.Element module = root.getChildByName("BodyModules").getChild(id - 1);
            Entity bodyModule = createBodyModule(position.x, position.y,
                    atlas.findRegion("body_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("power"),
                    module.getChildByName("AdvancedProperties").getInt("fuel"),
                    engineAdding);
            bodyModule.getComponent(BodyModuleComponent.class).id = id;

            return bodyModule;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity createBodyModule(float x, float y, TextureRegion texture, int weight, int cost, int power, int fuel, boolean engineAdding) {
        Entity entity = engine.createEntity();

        TransformComponent position = engine.createComponent(TransformComponent.class);
        BodyModuleComponent bodyModuleComponent = engine.createComponent(BodyModuleComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        bodyModuleComponent.weight = weight;
        bodyModuleComponent.cost = cost;
        bodyModuleComponent.fuel = fuel;
        bodyModuleComponent.power = power;

        position.position.set(x, y, 0);
        textureComponent.region = texture;
        position.scale.x = Gdx.graphics.getWidth() / 480.0f;
        position.scale.y = Gdx.graphics.getHeight() / 800.0f;

        entity.add(position);
        entity.add(bodyModuleComponent);
        entity.add(textureComponent);

        if (engineAdding) {
            engine.addEntity(entity);
        }

        return entity;
    }

    public Entity createFinsModule(Vector2 position, TextureAtlas atlas, int id, boolean engineAdding) {
        XmlReader xmlReader = new XmlReader();
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/modules.xml"));
            XmlReader.Element module = root.getChildByName("FinsModules").getChild(id - 1);
            Entity finsModule = createFinsModule(position.x, position.y,
                    atlas.findRegion("fins_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("maneuver"),
                    engineAdding);
            finsModule.getComponent(FinsModuleComponent.class).id = id;

            return finsModule;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity createFinsModule(float x, float y, TextureRegion texture, int weight, int cost, int maneuver, boolean engineAdding) {
        Entity entity = engine.createEntity();

        TransformComponent position = engine.createComponent(TransformComponent.class);
        FinsModuleComponent finsModuleComponent = engine.createComponent(FinsModuleComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        finsModuleComponent.weight = weight;
        finsModuleComponent.cost = cost;
        finsModuleComponent.maneuver = maneuver;

        position.position.set(x, y, 1);
        textureComponent.region = texture;
        position.scale.x = Gdx.graphics.getWidth() / 480.0f;
        position.scale.y = Gdx.graphics.getHeight() / 800.0f;

        entity.add(position);
        entity.add(finsModuleComponent);
        entity.add(textureComponent);

        if (engineAdding) {
            engine.addEntity(entity);
        }

        return entity;
    }

    public Entity createEngineModule(Vector2 position, TextureAtlas atlas, int id, boolean engineAdding) {
        XmlReader xmlReader = new XmlReader();
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/modules.xml"));
            XmlReader.Element module = root.getChildByName("EngineModules").getChild(id - 1);
            Entity engineModule = createEngineModule(position.x, position.y,
                    atlas.findRegion("engine_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("power"),
                    engineAdding);
            engineModule.getComponent(EngineModuleComponent.class).id = id;

            return engineModule;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity createEngineModule(float x, float y, TextureRegion texture, int weight, int cost, int power, boolean engineAdding) {
        Entity entity = engine.createEntity();

        TransformComponent position = engine.createComponent(TransformComponent.class);
        EngineModuleComponent engineModuleComponent = engine.createComponent(EngineModuleComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        engineModuleComponent.weight = weight;
        engineModuleComponent.cost = cost;
        engineModuleComponent.power = power;

        position.position.set(x, y, 2);
        textureComponent.region = texture;
        position.scale.x = Gdx.graphics.getWidth() / 480.0f;
        position.scale.y = Gdx.graphics.getHeight() / 800.0f;

        entity.add(position);
        entity.add(engineModuleComponent);
        entity.add(textureComponent);

        if (engineAdding) {
            engine.addEntity(entity);
        }
        return entity;
    }

    public int getBodyCount() {
        return bodyFactory.world.getBodyCount();
    }
}
