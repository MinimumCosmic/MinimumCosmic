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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.XmlReader;
import org.minimumcosmic.game.entity.components.*;
import org.minimumcosmic.game.entity.components.modules.*;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ObjectFactory {
    private BodyFactory bodyFactory;
    public World world;
    private PooledEngine engine;


    public ObjectFactory(PooledEngine engine) {
        this.engine = engine;

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

            // Empty texture to render the particle effect
            TextureComponent textureComponent = engine.createComponent(TextureComponent.class);


            XmlReader.Element headElement = root.getChildByName("HeadModule");
            XmlReader.Element bodyElement = root.getChildByName("BodyModule");
            XmlReader.Element finsElement = root.getChildByName("FinsModule");
            XmlReader.Element engineElement = root.getChildByName("EngineModule");

            rocketComponent.headModule =
                    createHeadModule(
                            rocketPosition.x,
                            rocketPosition.y,
                            atlas.findRegion("head_" + headElement.getInt("textureID")),
                            headElement.getInt("weight"),
                            headElement.getInt("hp"),
                            headElement.getInt("power"),
                            headElement.getInt("fuel"),
                            headElement.getInt("cost"));

            rocketComponent.bodyModule =
                    createBodyModule(
                            rocketPosition.x,
                            rocketPosition.y,
                            atlas.findRegion("body_" + bodyElement.getInt("textureID")),
                            bodyElement.getInt("weight"),
                            bodyElement.getInt("power"),
                            bodyElement.getInt("fuel"),
                            bodyElement.getInt("cost"));

            rocketComponent.finsModule =
                    createFinsModule(
                            rocketPosition.x,
                            rocketPosition.y,
                            atlas.findRegion("fins_" + finsElement.getInt("textureID")),
                            finsElement.getInt("weight"),
                            finsElement.getInt("maneuver"),
                            finsElement.getInt("cost"));

            rocketComponent.engineModule =
                    createEngineModule(
                            rocketPosition.x,
                            rocketPosition.y,
                            atlas.findRegion("engine_" + engineElement.getInt("textureID")),
                            engineElement.getInt("weight"),
                            engineElement.getInt("power"),
                            engineElement.getInt("cost"));

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

            //add entity to engine
            engine.addEntity(entity);
            return entity;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Entity createRocket(TextureAtlas atlas, OrthographicCamera camera, ParticleEffect pe) {
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

        pe.scaleEffect(0.025f);
        pe.getEmitters().first().setPosition(10, 0);
        partEffComponent.particleEffect = pe;
        partEffComponent.particleEffect.start();

        rocketComponent.headModule =
                createHeadModule(15, 5, atlas.findRegion("head_3"),
                        50, 30, 15, 50, 25);

        rocketComponent.bodyModule =
                createBodyModule(15, 5, atlas.findRegion("body_3"),
                        150, 20, 100, 75);

        rocketComponent.finsModule =
                createFinsModule(15, 5, atlas.findRegion("fins_3"),
                        5, 5, 15);

        rocketComponent.engineModule =
                createEngineModule(15, 5, atlas.findRegion("engine_3"),
                        35, 10, 35);

        player.camera = camera;

        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-2, -3);
        vertices[1] = new Vector2(2, -3);
        vertices[2] = new Vector2(1, 2);
        vertices[3] = new Vector2(-1, 2);

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

    public Entity createHeadModule(float x, float y, TextureRegion texture, int weight, int hp, int power, int fuel, int cost) {
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

    public Entity createBodyModule(float x, float y, TextureRegion texture, int weight, int power, int fuel, int cost) {
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

    public Entity createFinsModule(float x, float y, TextureRegion texture, int weight, int maneuver, int cost) {
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

    public Entity createEngineModule(float x, float y, TextureRegion texture, int weight, int power, int cost) {
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
}
