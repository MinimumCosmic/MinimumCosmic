package org.minimumcosmic.game;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.components.*;
import org.minimumcosmic.game.entity.systems.CollisionSystem;
import org.minimumcosmic.game.entity.systems.PhysicsDebugSystem;
import org.minimumcosmic.game.entity.systems.PhysicsSystem;
import org.minimumcosmic.game.entity.systems.PlayerControlSystem;

public class GameScreen implements Screen {
    private KeyboardController controller;
    private World world;
    private SpriteBatch sb;
    private OrthographicCamera cam;
    private PooledEngine engine;


    @Override
    public void show() {
        controller = new KeyboardController();
        world = new World(new Vector2(0, -10f), false);
        world.setContactListener(new B2dContactListener());

        // Box2D has it's own meters and pixels conversion
        float PixelsPerMeter = 32.0f;
        float WorldWidth = Gdx.graphics.getWidth() / PixelsPerMeter;
        float WorldHeight = Gdx.graphics.getHeight() / PixelsPerMeter;

        // Setup camera and spriteBatch
        cam = new OrthographicCamera(WorldWidth, WorldHeight);
        cam.position.set(WorldWidth / 2f, WorldHeight / 2f, 0);
        cam.update();
        sb = new SpriteBatch();
        sb.setProjectionMatrix(cam.combined);

        engine = new PooledEngine();

        // Add required systems to the engine
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, cam));
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
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;
        b2dbody.body.setUserData(entity);

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

        // set the components data
        b2dbody.body = createOval(10, 1, 1, true);
        // set object position (x,y,z) z used to define draw order 0 first drawn
        position.position.set(10, 10, 0);
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

    // create a rectangular body
    private Body createBox(float x, float y, float w, float h, boolean dynamic){
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        if(dynamic){
            boxBodyDef.type = BodyDef.BodyType.DynamicBody;
        }else{
            boxBodyDef.type = BodyDef.BodyType.StaticBody;
        }

        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        boxBodyDef.fixedRotation = true;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(w/2, h/2);

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
    private Body createOval(float x, float y, float w, boolean dynamic){
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        if(dynamic){
            boxBodyDef.type = BodyDef.BodyType.DynamicBody;
        }else{
            boxBodyDef.type = BodyDef.BodyType.StaticBody;
        }

        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        boxBodyDef.fixedRotation = true;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(w /2);

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
