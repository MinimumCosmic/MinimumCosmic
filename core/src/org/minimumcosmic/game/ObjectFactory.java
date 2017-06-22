package org.minimumcosmic.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import org.minimumcosmic.game.entity.components.*;
import org.minimumcosmic.game.entity.components.modules.*;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;

import java.util.ArrayList;

public class ObjectFactory {
    private BodyFactory bodyFactory;
    public World world;
    private PooledEngine engine;


    public ObjectFactory(PooledEngine engine) {
        this.engine = engine;

        System.out.println("Created world");
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new B2dContactListener());
        bodyFactory = BodyFactory.getInstance(world);
    }

    public void dispose() {
        bodyFactory.deleteAllBodies();
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

    public Entity createRocket(TextureAtlas textureAtlas, OrthographicCamera orthographicCamera, ParticleEffect particleEffect) {
        // Create an empty entity
        Entity entity = engine.createEntity();

        // Add components
        B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        CameraComponent player = engine.createComponent(CameraComponent.class);
        RocketComponent rocketComponent = engine.createComponent(RocketComponent.class);
        ParticleEffectComponent partEffComponent = engine.createComponent(ParticleEffectComponent.class);

        // Empty texture to render the particle effect
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        particleEffect.scaleEffect(0.025f);
        particleEffect.getEmitters().first().setPosition(10, 0);
        partEffComponent.particleEffect = particleEffect;
        partEffComponent.particleEffect.start();

        rocketComponent.headModule =
                createHeadModule(15 ,5, textureAtlas.findRegion("head_3"),
                        50, 30, 15, 50, 25);

        rocketComponent.bodyModule =
                createBodyModule(15 ,5, textureAtlas.findRegion("body_3"),
                        150, 20, 100, 75);

        rocketComponent.finsModule =
                createFinsModule(15,5, textureAtlas.findRegion("fins_3"),
                        5, 5, 15);

        rocketComponent.engineModule =
                createEngineModule(15, 5, textureAtlas.findRegion("engine_3"),
                        35, 10, 35);

        player.camera = orthographicCamera;

        Vector2 []vertices = new Vector2[4];
        vertices[0] = new Vector2(-2,-3);
        vertices[1] = new Vector2(2,-3);
        vertices[2] = new Vector2(1,2);
        vertices[3] = new Vector2(-1,2);

        b2dBody.body = bodyFactory.makePolygonBody(15, 5, vertices, BodyFactory.STEEL, BodyDef.BodyType.DynamicBody, false);
        position.position.set(15, 5, 0);
        b2dBody.body.setUserData(entity);

        entity.add(b2dBody);
        entity.add(position);
        entity.add(player);
        entity.add(rocketComponent);
        entity.add(partEffComponent);
        entity.add(textureComponent);

        //add entity to engine
        engine.addEntity(entity);
        return entity;
    }

    public Entity createHeadModule(int x, int y, TextureRegion texture, int weight, int hp, int power, int fuel, int cost) {
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

        engine.addEntity(entity);

        return entity;
    }

    public Entity createBodyModule(int x, int y, TextureRegion texture, int weight, int power, int fuel, int cost) {
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

        engine.addEntity(entity);

        return entity;
    }

    public Entity createFinsModule(int x, int y, TextureRegion texture, int weight, int maneuver, int cost) {
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

        engine.addEntity(entity);

        return entity;
    }

    public Entity createEngineModule(int x, int y, TextureRegion texture, int weight, int power, int cost) {
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

        engine.addEntity(entity);

        return entity;
    }

    // Create the player entity
    public void createPlayer(TextureRegion texture, OrthographicCamera camera) {
        // Create an empty entity
        Entity entity = engine.createEntity();

        // Create a Box2dBody, transform, player, collision, type and state component
        B2dBodyComponent b2dBody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        CameraComponent player = engine.createComponent(CameraComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);
        TextureComponent textComp = engine.createComponent(TextureComponent.class);

        player.camera = camera;
        //b2dbody.body = bodyFactory.makeBoxBody(10, 5, 2.5f, 7.5f, BodyFactory.STEEL, BodyDef.BodyType.DynamicBody, false);
        b2dBody.body = bodyFactory.makeTriangleBody(10, 5, 2.5f, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, false);
        position.position.set(10, 5, 0); // Used in RenderingSystem
        textComp.region = texture;
        type.type = TypeComponent.PLAYER;
        stateCom.set(StateComponent.STATE_NORMAL);
        b2dBody.body.setUserData(entity);

        // add components to entity
        entity.add(b2dBody);
        entity.add(position);
        entity.add(textComp);
        entity.add(player);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);

        //add entity to engine
        engine.addEntity(entity);

    }
}
