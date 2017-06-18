package org.minimumcosmic.game;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.components.*;
import org.minimumcosmic.game.entity.systems.*;

public class GameScreen implements Screen {
    private KeyboardController controller;
    private World world;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private PooledEngine engine;


    @Override
    public void show() {
        controller = new KeyboardController();
        world = new World(new Vector2(0, -10f), false);
        world.setContactListener(new B2dContactListener());

        spriteBatch = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = renderingSystem.getCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        engine = new PooledEngine();

        // Add required systems to the engine
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, camera));
        engine.addSystem(renderingSystem);
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));

        // Create some entites
        createPlayer();
        createPlatform(2, 2);
        createPlatform(3, 4);
        createPlatform(5, 2);
        createPlatform(6, 4);

        createFloor();

        Gdx.input.setInputProcessor(controller);
    }

    //create a platform
    private void createPlatform(float x, float y) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = createBox(x, y, 3, 0.2f, false);

        TextureComponent textComp = engine.createComponent(TextureComponent.class);
        textComp.region = this.createTexture(Color.BLUE, false, 2 * 32, (int) (0.2f * 32));
        entity.add(textComp);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(x, y, 2);
        entity.add(position);

        b2dbody.body.setUserData(entity);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        entity.add(b2dbody);
        entity.add(type);

        engine.addEntity(entity);

    }

    // create a floor entity
    private void createFloor() {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = createBox(0, 0, 100, 0.2f, false);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;
        TextureComponent textComp = engine.createComponent(TextureComponent.class);
        textComp.region = this.createTexture(Color.GREEN, false, 100 * 32, (int) (0.2f * 32));
        entity.add(textComp);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        position.position.set(0, 0, 0);
        entity.add(position);
        b2dbody.body.setUserData(entity);


        entity.add(b2dbody);
        entity.add(type);

        engine.addEntity(entity);
    }

    // create the player entity
    private void createPlayer() {
        //create an empty entity
        Entity entity = engine.createEntity();
        //create a Box2dBody, transform, player, collision, type and state component
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);

        TextureComponent textComp = engine.createComponent(TextureComponent.class);
        textComp.region = this.createTexture(Color.RED, true, 32, 32);
        entity.add(textComp);

        // set the components data
        b2dbody.body = createOval(10, 1, 1, true);
        // set object position (x,y,z) z used to define draw order 0 first drawn
        position.position.set(10, 10, 1);
        type.type = TypeComponent.PLAYER;
        stateCom.set(StateComponent.STATE_NORMAL);
        b2dbody.body.setUserData(entity);

        // add components to entity

        entity.add(b2dbody);
        entity.add(position);
        entity.add(player);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);

        //add entity to engine
        engine.addEntity(entity);

    }

    // create a coloured TextureRegion
    private TextureRegion createTexture(Color color, boolean circle, int w, int h) {
        Pixmap pmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pmap.setColor(color);
        if (circle) {
            pmap.fillCircle(15, 15, 15);
        } else {
            pmap.fill();
        }
        TextureRegion texr = new TextureRegion(new Texture(pmap));
        pmap.dispose();
        return texr;

    }

    // create a rectangular body
    private Body createBox(float x, float y, float w, float h, boolean dynamic) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        if (dynamic) {
            boxBodyDef.type = BodyDef.BodyType.DynamicBody;
        } else {
            boxBodyDef.type = BodyDef.BodyType.StaticBody;
        }

        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        boxBodyDef.fixedRotation = true;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(w / 2, h / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = poly;
        fixtureDef.density = 10f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;

        boxBody.createFixture(fixtureDef);
        poly.dispose();

        return boxBody;

    }

    // create a Circular body
    private Body createOval(float x, float y, float w, boolean dynamic) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        if (dynamic) {
            boxBodyDef.type = BodyDef.BodyType.DynamicBody;
        } else {
            boxBodyDef.type = BodyDef.BodyType.StaticBody;
        }

        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        boxBodyDef.fixedRotation = true;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(w / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 10f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;

        boxBody.createFixture(fixtureDef);
        circleShape.dispose();

        return boxBody;

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.update(delta);
        System.out.println(Gdx.graphics.getFramesPerSecond());
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
