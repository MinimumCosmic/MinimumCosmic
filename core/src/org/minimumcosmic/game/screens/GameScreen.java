package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import org.minimumcosmic.game.BodyFactory;
import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.SettingsSaver;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.PickupComponent;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.systems.BoundsSystem;
import org.minimumcosmic.game.entity.systems.CameraSystem;
import org.minimumcosmic.game.entity.systems.CollisionSystem;
import org.minimumcosmic.game.entity.systems.PhysicsDebugSystem;
import org.minimumcosmic.game.entity.systems.PhysicsSystem;
import org.minimumcosmic.game.entity.systems.RenderingSystem;
import org.minimumcosmic.game.entity.systems.RocketSystem;
import org.minimumcosmic.game.parallax.Parallax;

public class GameScreen implements Screen {
    private KeyboardController controller;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private PooledEngine engine;
    private ObjectFactory objectFactory;
    private MinimumCosmic game;
    private Entity rocket;
    private Stage stage;
    private ProgressBar fuelMeter;
    private TextureAtlas textureAtlas;
    private Skin skin;
    private Label speedLabel;
    private Label fpsLabel;
    private Label bodyCountLabel;
    private Label pickUpLabel;
    private Parallax parallaxBackground;
    private Parallax parallaxForeground;


    public GameScreen(MinimumCosmic game) {
        this.game = game;

        game.AssetManager.queueAddSounds();
        game.AssetManager.assetManager.finishLoading();

        textureAtlas = game.AssetManager.assetManager.get("images/rocket.atlas");
        skin = game.AssetManager.assetManager.get("skin/uiskin.json");

        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void show() {
        stage = new Stage();
        controller = new KeyboardController();
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();
        parallaxBackground = new Parallax();
        parallaxForeground = new Parallax();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);

        camera = renderingSystem.getCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Add required systems to the engine
        engine.addSystem(new PhysicsSystem(BodyFactory.getInstance(objectFactory.world).world));
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsDebugSystem(BodyFactory.getInstance(objectFactory.world).world, camera));
        engine.addSystem(new CollisionSystem(objectFactory, engine));
        engine.addSystem(new RocketSystem(controller));
        engine.addSystem(new CameraSystem());
        engine.addSystem(new BoundsSystem());

        rocket = objectFactory.createRocket(textureAtlas, camera,
                (ParticleEffect) game.AssetManager.assetManager.get("smoke.p"),
                "xml/rocket.xml");
        objectFactory.createFloor(textureAtlas.findRegion("player"));
        objectFactory.generateWorld(new TextureAtlas("images/items.atlas"));
        objectFactory.generateParallaxBackground(new TextureAtlas("images/parallaxbnd.atlas"), parallaxBackground);
        objectFactory.generateParallaxForeground(new TextureAtlas("images/parallaxbnd.atlas"), parallaxForeground);

        Gdx.input.setInputProcessor(controller);

        fuelMeter = new ProgressBar(0 ,
                rocket.getComponent(RocketComponent.class).bodyModule.getComponent(BodyModuleComponent.class).fuel,
                0.25f, true, skin);
        fuelMeter.setHeight(Gdx.graphics.getHeight() / 2);
        fuelMeter.setPosition(Gdx.graphics.getWidth() * 0.95f, Gdx.graphics.getHeight() * 0.1f);
        stage.addActor(fuelMeter);
        speedLabel = new Label(100 + "m/s", skin);
        fpsLabel = new Label(60 + " fps", skin);
        bodyCountLabel = new Label(0 + " bodies", skin);
        pickUpLabel = new Label(0 + " money", skin);
        fpsLabel.setY(Gdx.graphics.getHeight() * 0.95f);
        bodyCountLabel.setY(Gdx.graphics.getHeight() * 0.9f);
        pickUpLabel.setY(Gdx.graphics.getHeight() * 0.85f);
        stage.addActor(speedLabel);
        stage.addActor(fpsLabel);
        stage.addActor(bodyCountLabel);
        stage.addActor(pickUpLabel);
    }
    @Override
    public void render(float delta) {
        float red = 36f / 255f; float green = 34f / 255f; float blue = 56f / 255f;
        Gdx.gl.glClearColor(red, green, blue, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fuelMeter.setValue(rocket.getComponent(RocketComponent.class).bodyModule.getComponent(BodyModuleComponent.class).fuel);
        speedLabel.setText(rocket.getComponent(B2dBodyComponent.class).body.getLinearVelocity().y + "m/s");
        fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + " fps");
        bodyCountLabel.setText(objectFactory.getBodyCount() + " bodies");
        pickUpLabel.setText(rocket.getComponent(PickupComponent.class).count + " money");

        stage.act();

        spriteBatch.begin();
        parallaxBackground.draw(camera, spriteBatch);
        spriteBatch.end();

        engine.update(delta);

        spriteBatch.begin();
        parallaxForeground.draw(camera, spriteBatch);
        spriteBatch.end();

        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            SettingsSaver.saveResearchPoint(rocket);
            game.changeScreen(MinimumCosmic.MENU);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            this.dispose();
            this.show();
        }
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
        objectFactory.dispose();
        stage.dispose();
    }

    @Override
    public void dispose() {
        objectFactory.dispose();
        stage.dispose();
    }
}
