package org.minimumcosmic.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
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

import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.BoundsComponent;
import org.minimumcosmic.game.entity.components.CameraComponent;
import org.minimumcosmic.game.entity.components.CollisionComponent;
import org.minimumcosmic.game.entity.components.ParticleEffectComponent;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.TextureComponent;
import org.minimumcosmic.game.entity.components.TransformComponent;
import org.minimumcosmic.game.entity.components.TypeComponent;
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
    public static final float WORLD_HEIGHT = RenderingSystem.WORLD_HEIGHT * 100;

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

    public void deleteBody(Body body) {bodyFactory.deleteBody(body);}


    public void generateWorld(TextureAtlas atlas) {
        int y = 0;
        while (y < WORLD_HEIGHT) {
            float x = rand.nextFloat() * WORLD_WIDTH;
            if (rand.nextFloat() > 0.6f) {
                createMoney(x + MathUtils.random(-0.5f, 0.5f), y + rand.nextFloat() * 3, atlas);
            }
            y += rand.nextFloat() * 10 ;
        }
    }

    public void generateParallaxBackground(TextureAtlas atlas, Parallax parallax) {
        TextureRegion groundRegion = atlas.findRegion("ground");
        TexturedParallaxLayer groundLayer =
                new TexturedParallaxLayer(groundRegion, WORLD_WIDTH,
                        new Vector2(.6f, .6f), TexturedParallaxLayer.WH.width);

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

        TextureRegion bush7Region = atlas.findRegion("bush7");
        TexturedParallaxLayer bush7Layer =
                new TexturedParallaxLayer(bush7Region, WORLD_WIDTH,
                        new Vector2(.25f, .25f), TexturedParallaxLayer.WH.width);

        TextureRegion gradient1Region = atlas.findRegion("gradient1");
        TexturedParallaxLayer gradient1Layer =
                new TexturedParallaxLayer(gradient1Region, WORLD_WIDTH,
                        new Vector2(.025f, .025f), TexturedParallaxLayer.WH.width);

        parallax.addLayers(gradient1Layer, bush7Layer, bush6Layer, bush5Layer, bush4Layer,
                bush3Layer, bush2Layer, bush1Layer, startLayer, groundLayer);

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

    // Create a floor entity
    public void createFloor(TextureRegion texture) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
        b2dBody.body = bodyFactory.makeBoxBody(0, 0, 100, 0.2f, BodyFactory.STONE, BodyDef.BodyType.StaticBody);

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
        b2dBody.body = bodyFactory.makeSensorBody(x, y, 1, BodyDef.BodyType.StaticBody, true);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.PICKUP;

        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.collisionEntity = entity;

        //TextureComponent textComp = engine.createComponent(TextureComponent.class);
        //textComp.region = atlas.findRegion("shadow_orb");

        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(0, 0, 0);

        b2dBody.body.setUserData(entity);

        entity.add(b2dBody);
        entity.add(position);
        //entity.add(textComp);
        entity.add(type);
        entity.add(collision);

        engine.addEntity(entity);

        return entity;
    }

    public Entity createRocket(TextureAtlas atlas, OrthographicCamera camera, ParticleEffect pe, String filePath) {
        XmlReader xmlReader = new XmlReader();
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal(filePath));

            Vector2 rocketPosition = new Vector2(root.getChildByName("Position").getFloat("x"),
                    root.getChildByName("Position").getFloat("y"));

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

            type.type = TypeComponent.PLAYER;
            collision.collisionEntity = entity;

            // Empty texture to render the particle effect
            TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

            rocketComponent.headModule = createHeadModule(rocketPosition, atlas, root.getChildByName("HeadModule").getInt("id"), true);

            rocketComponent.bodyModule = createBodyModule(rocketPosition, atlas, root.getChildByName("BodyModule").getInt("id"), true);

            rocketComponent.finsModule = createFinsModule(rocketPosition, atlas, root.getChildByName("FinsModule").getInt("id"), true);

            rocketComponent.engineModule = createEngineModule(rocketPosition, atlas, root.getChildByName("EngineModule").getInt("id"), true);

            player.camera = camera;

            float scale = root.getChildByName("Scale").getInt("factor");

            partEffComponent.particleEffect = pe;
            pe.getEmitters().first().setPosition(rocketPosition.x, rocketPosition.y - 3 * scale);
            pe.scaleEffect(0.025f);
            partEffComponent.particleEffect.start();

            Vector2[] vertices = new Vector2[4];
            vertices[0] = new Vector2(-2 * scale, -3 * scale);
            vertices[1] = new Vector2(2 * scale, -3 * scale);
            vertices[2] = new Vector2(1 * scale, 2 * scale);
            vertices[3] = new Vector2(-1 * scale, 2 * scale);

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
            entity.add(partEffComponent);
            entity.add(textureComponent);
            entity.add(boundsComponent);
            entity.add(collision);
            entity.add(type);

            //add entity to engine
            engine.addEntity(entity);
            return entity;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity createHeadModule(Vector2 position, TextureAtlas atlas, int id, boolean engineAdding) {
        XmlReader xmlReader = new XmlReader();
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/modules.xml"));
            XmlReader.Element module =  root.getChildByName("HeadModules").getChild(id - 1);
            Entity headModule = createHeadModule(position.x, position.y,
                    atlas.findRegion("head_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("hp"),
                    module.getChildByName("AdvancedProperties").getInt("power"),
                    module.getChildByName("AdvancedProperties").getInt("fuel"),
                    engineAdding);

            return headModule;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity createHeadModule(float x, float y, TextureRegion texture, int weight, int cost, int hp, int power, int fuel, boolean engineAdding)
    {
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
            XmlReader.Element module =  root.getChildByName("BodyModules").getChild(id - 1);
            Entity bodyModule = createBodyModule(position.x, position.y,
                    atlas.findRegion("body_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("power"),
                    module.getChildByName("AdvancedProperties").getInt("fuel"),
                    engineAdding);

            return bodyModule;
        }
        catch (IOException e) {
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
            XmlReader.Element module =  root.getChildByName("FinsModules").getChild(id - 1);
            Entity finsModule = createFinsModule(position.x, position.y,
                    atlas.findRegion("fins_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("maneuver"),
                    engineAdding);

            return finsModule;
        }
        catch (IOException e) {
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

        position.position.set(x, y, 0);
        textureComponent.region = texture;

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
            XmlReader.Element module =  root.getChildByName("EngineModules").getChild(id - 1);
            Entity engineModule = createEngineModule(position.x, position.y,
                    atlas.findRegion("engine_" + id),
                    module.getChildByName("BasicProperties").getInt("weight"),
                    module.getChildByName("BasicProperties").getInt("cost"),
                    module.getChildByName("AdvancedProperties").getInt("power"),
                    engineAdding);

            return engineModule;
        }
        catch (IOException e) {
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

        position.position.set(x, y, 0);
        textureComponent.region = texture;

        entity.add(position);
        entity.add(engineModuleComponent);
        entity.add(textureComponent);

        if (engineAdding) {
            engine.addEntity(entity);
        }
        return entity;
    }

    public Entity createRocket(TextureAtlas atlas, OrthographicCamera camera, ParticleEffect pe, String filePath, Vector2 rp) {
        XmlReader xmlReader = new XmlReader();
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal(filePath));

            Vector2 rocketPosition = rp;

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

            type.type = TypeComponent.PLAYER;
            collision.collisionEntity = entity;

            // Empty texture to render the particle effect
            TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

            rocketComponent.headModule = createHeadModule(rocketPosition, atlas, root.getChildByName("HeadModule").getInt("id"), true);

            rocketComponent.bodyModule = createBodyModule(rocketPosition, atlas, root.getChildByName("BodyModule").getInt("id"), true);

            rocketComponent.finsModule = createFinsModule(rocketPosition, atlas, root.getChildByName("FinsModule").getInt("id"), true);

            rocketComponent.engineModule = createEngineModule(rocketPosition, atlas, root.getChildByName("EngineModule").getInt("id"), true);

            player.camera = camera;

            float scale = 2;//root.getChildByName("Scale").getInt("factor");

            partEffComponent.particleEffect = pe;
            pe.getEmitters().first().setPosition(rocketPosition.x, rocketPosition.y - 3 * scale);
            pe.scaleEffect(0.025f);
            partEffComponent.particleEffect.start();

            Vector2[] vertices = new Vector2[4];
            vertices[0] = new Vector2(-2 * scale, -3 * scale);
            vertices[1] = new Vector2(2 * scale, -3 * scale);
            vertices[2] = new Vector2(1 * scale, 2 * scale);
            vertices[3] = new Vector2(-1 * scale, 2 * scale);

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
            entity.add(partEffComponent);
            entity.add(textureComponent);
            entity.add(boundsComponent);
            entity.add(collision);
            entity.add(type);

            //add entity to engine
            engine.addEntity(entity);
            return entity;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
