package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.minimumcosmic.game.InventoryCell;
import org.minimumcosmic.game.Mapper;
import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.MyActor;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.SettingsLoader;
import org.minimumcosmic.game.SettingsSaver;
import org.minimumcosmic.game.controller.TouchscreenController;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.TextureComponent;
import org.minimumcosmic.game.entity.components.TransformComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.components.modules.EngineModuleComponent;
import org.minimumcosmic.game.entity.components.modules.FinsModuleComponent;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;
import org.minimumcosmic.game.entity.systems.RenderingSystem;

import java.util.ArrayList;

import static org.minimumcosmic.game.SettingsSaver.saveRocket;

public class HangarScreen implements Screen {
    private final int HEAD = 0;
    private final int BODY = 1;
    private final int FINS = 2;
    private final int ENGINE = 3;

    final static int moduleNumbers = 3;
    final static float rocketScale = Gdx.graphics.getWidth() * 0.005f;

    private MinimumCosmic game;
    private SpriteBatch spriteBatch;
    private Sprite backSprite;
    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private TextureAtlas rocketAtlas;
    private Entity rocket;
    private PooledEngine engine;
    private ObjectFactory objectFactory;
    private OrthographicCamera camera;
    private ParticleEffect pe;
    private RenderingSystem renderingSystem;
    private RocketComponent rocketComponent;
    private TransformComponent headTransformComponent;
    private TransformComponent bodyTransformComponent;
    private TransformComponent finsTransformComponent;
    private TransformComponent engineTransformComponent;
    private TouchscreenController touchscreenController;

    private Array<ArrayList<InventoryCell>> inventory;

    Vector2 rocketPosition = new Vector2(120, 120);

    final Table confirmChooseTable = new Table(skin);

    public HangarScreen(MinimumCosmic game) {
        this.game = game;
        skin = game.AssetManager.assetManager.get("skin/uiskin.json");

        textureAtlas = game.AssetManager.assetManager.get("images/loading_screen.atlas");

        rocketAtlas = game.AssetManager.assetManager.get("images/rocket.atlas");

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.input.setCatchBackKey(true);

    }

    private void init(){
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        renderingSystem = new RenderingSystem(spriteBatch);
        camera = new OrthographicCamera();
        spriteBatch.setProjectionMatrix(camera.combined);


        engine.addSystem(renderingSystem);

        inventory = new Array<ArrayList<InventoryCell>>();

        touchscreenController = new TouchscreenController();
    }

    private void initMappers(){
        rocketComponent = Mapper.rocketComponentMapper.get(rocket);
        headTransformComponent = Mapper.transformComponentMapper.get(rocketComponent.headModule);
        bodyTransformComponent = Mapper.transformComponentMapper.get(rocketComponent.bodyModule);
        finsTransformComponent = Mapper.transformComponentMapper.get(rocketComponent.finsModule);
        engineTransformComponent = Mapper.transformComponentMapper.get(rocketComponent.engineModule);
    }

    @Override
    public void show(){
        init();


        rocketPosition = new Vector2((Gdx.graphics.getWidth() * 0.75f) * RenderingSystem.PIXELS_TO_METRES, Gdx.graphics.getHeight() / 2 * RenderingSystem.PIXELS_TO_METRES);
        pe = new ParticleEffect();
        pe = game.AssetManager.assetManager.get("smoke.p");
        rocket = objectFactory.createRocket(rocketAtlas, camera, pe, "xml/rocket.xml", rocketPosition);

        initMappers();
        inventory = SettingsLoader.loadInventory();

        scaleRocketSize(rocketScale);

        //provide to catch keys and stage action
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(touchscreenController);

        Gdx.input.setInputProcessor(inputMultiplexer);

        final Table table = new Table(skin);
        table.setFillParent(true);

        stage.addActor(table);

        TextButton back = new TextButton("Back", skin);
        table.top().defaults();
        table.left().defaults();
        table.add(back).fillX().uniformX().width(Gdx.graphics.getWidth() * 0.15f).height(Gdx.graphics.getHeight() * 0.07f);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveRocket(rocket);
                game.changeScreen(MinimumCosmic.MENU);
            }
        });

        //Top buttons to switch module
        Table modules = new Table();
        TextButton headButton = new TextButton("Head modules", skin);
        final Table tmpTable = new Table();
        tmpTable.setBounds(0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);

        final Table listTable = new Table(skin);
        listTable.top();
        listTable.left();
        listTable.setBounds(0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.55f, Gdx.graphics.getHeight() * 0.6f);
        listTable.debug();

        confirmChooseTable.setBounds(0, Gdx.graphics.getHeight() * 0.05f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.15f);
        confirmChooseTable.debug();

        Table rocketTable = new Table();
        rocketTable.setBounds(listTable.getWidth(), Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);
        rocketTable.debug();

        ScrollPane scrollPane = new ScrollPane(new Label("Test", skin), skin);
        listTable.add(scrollPane);


        //Top buttons to switch module
        modules = new Table();
        modules.setBounds(0, listTable.getHeight() + listTable.getY(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        modules.bottom();


        headButton = new TextButton("Head", skin);
        headButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tmpTable.clear();
                listTable.clear();

                createHeadTableArea(tmpTable);

                listTable.add(new ScrollPane(tmpTable)).uniformX();


            }
        });

        TextButton bodyButton = new TextButton("Body", skin);
        bodyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tmpTable.clear();
                listTable.clear();

                createBodyTableArea(tmpTable);

                listTable.add(new ScrollPane(tmpTable)).uniformX();
            }
        });

        TextButton finsButton = new TextButton("Fins", skin);
        finsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tmpTable.clear();
                listTable.clear();

                createFinsTableArea(tmpTable);

                listTable.add(new ScrollPane(tmpTable)).uniformX();
            }
        });

        TextButton engineButton = new TextButton("Engine", skin);
        engineButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tmpTable.clear();
                listTable.clear();

                createEngineTableArea(tmpTable);

                listTable.add(new ScrollPane(tmpTable)).uniformX();
            }
        });

        modules.add(headButton).width(Gdx.graphics.getWidth() / 4).height(Gdx.graphics.getHeight() * 0.05f);
        modules.add(bodyButton).width(Gdx.graphics.getWidth() / 4).height(Gdx.graphics.getHeight() * 0.05f);
        modules.add(finsButton).width(Gdx.graphics.getWidth() / 4).height(Gdx.graphics.getHeight() * 0.05f);
        modules.add(engineButton).width(Gdx.graphics.getWidth() / 4).height(Gdx.graphics.getHeight() * 0.05f);
        modules.row();

        //adding actors
        stage.addActor(listTable);
        stage.addActor(rocketTable);
        stage.addActor(modules);

        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        pe.scaleEffect(0.25f);
        pe.start();
    }

    public void createHeadTableArea(final Table table) {
        for (int i = 0; i < inventory.get(0).size(); ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }
            InventoryCell currentInventoryCell = inventory.get(0).get(i);

            final Entity head = objectFactory.createHeadModule(rocketPosition, rocketAtlas, currentInventoryCell.id, false);

            final MyActor headModule = new MyActor(rocketAtlas.findRegion("head_" + currentInventoryCell.id),
                    head, currentInventoryCell.id, HEAD);

            headModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {


                            engine.removeEntity(rocketComponent.headModule);

                            rocketComponent.headModule =
                                    objectFactory.createHeadModule(rocketPosition,rocketAtlas, headModule.getId(), true);
                            headTransformComponent.scale.x = rocketScale;
                            headTransformComponent.scale.y = rocketScale;
                            Mapper.rocketComponentMapper = ComponentMapper.getFor(RocketComponent.class);

                            System.out.println("It's confirmed");
                        }
                    });
                    Image img = new Image((headModule.entity.getComponent(TextureComponent.class).region));
                    confirmChooseTable.add(img);

                    ComponentMapper<HeadModuleComponent> hc = ComponentMapper.getFor(HeadModuleComponent.class);
                    HeadModuleComponent hmc = hc.get(headModule.entity);

                    confirmChooseTable.add(new Label(
                            "cost: " + hmc.cost + "\n" +
                            "weight: " + hmc.weight + "\n" +
                                    "power: " + hmc.power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(headModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x" + currentInventoryCell.amount, skin)).bottom().uniformX();


        }
    }

    public void createBodyTableArea(Table table) {
        for (int i = 0; i < inventory.get(1).size(); ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }
            InventoryCell currentInventoryCell = inventory.get(1).get(i);

            Entity body = objectFactory.createBodyModule(rocketPosition, rocketAtlas, currentInventoryCell.id, false);

            final MyActor bodyModule = new MyActor(rocketAtlas.findRegion("body_" + currentInventoryCell.id),
                    body, currentInventoryCell.id, BODY);
            bodyModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            engine.removeEntity(rocketComponent.bodyModule);

                            rocketComponent.bodyModule =
                                    objectFactory.createBodyModule(rocketPosition,rocketAtlas, bodyModule.getId(), true);
                            bodyTransformComponent.scale.x = rocketScale;
                            bodyTransformComponent.scale.y = rocketScale;
                            Mapper.rocketComponentMapper = ComponentMapper.getFor(RocketComponent.class);

                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((bodyModule.entity.getComponent(TextureComponent.class).region)));

                    ComponentMapper<BodyModuleComponent> bc = ComponentMapper.getFor(BodyModuleComponent.class);
                    BodyModuleComponent bmc = bc.get(bodyModule.entity);

                    confirmChooseTable.add(new Label(
                            "cost: " + bmc.cost + "\n" +
                                    "weight: " + bmc.weight + "\n" +
                                    "power: " + bmc.power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(bodyModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x" + currentInventoryCell.amount, skin)).bottom().uniformX();


        }
    }

    public void createFinsTableArea(Table table) {
        for (int i = 0; i < inventory.get(2).size(); ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }
            InventoryCell currentInventoryCell = inventory.get(2).get(i);

            Entity fins = objectFactory.createFinsModule(rocketPosition, rocketAtlas, currentInventoryCell.id, false);

            final MyActor finsModule = new MyActor(rocketAtlas.findRegion("fins_" + currentInventoryCell.id),
                    fins, currentInventoryCell.id, FINS);
            finsModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            engine.removeEntity(rocketComponent.finsModule);

                            rocketComponent.finsModule =
                                    objectFactory.createFinsModule(rocketPosition,rocketAtlas, finsModule.getId(), true);
                            finsTransformComponent.scale.x = rocketScale;
                            finsTransformComponent.scale.y = rocketScale;
                            Mapper.rocketComponentMapper = ComponentMapper.getFor(RocketComponent.class);


                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((finsModule.entity.getComponent(TextureComponent.class).region)));

                    ComponentMapper<FinsModuleComponent> fc = ComponentMapper.getFor(FinsModuleComponent.class);
                    FinsModuleComponent fmc = fc.get(finsModule.entity);

                    confirmChooseTable.add(new Label(
                            "cost: " + fmc.cost + "\n" +
                                    "weight: " + fmc.weight + "\n" +
                                    "maneuver: " + fmc.maneuver
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(finsModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x" + currentInventoryCell.amount, skin)).bottom().uniformX();


        }
    }

    public void createEngineTableArea(Table table) {
        for (int i = 0; i < inventory.get(3).size(); ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }
            InventoryCell currentInventoryCell = inventory.get(3).get(i);

            Entity eng = objectFactory.createEngineModule(rocketPosition, rocketAtlas, currentInventoryCell.id, false);

            final MyActor engineModule = new MyActor(rocketAtlas.findRegion("engine_" + currentInventoryCell.id),
                    eng, currentInventoryCell.id, ENGINE);
            engineModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {

                            engine.removeEntity(rocketComponent.engineModule);

                            rocketComponent.engineModule =
                                    objectFactory.createEngineModule(rocketPosition,rocketAtlas, engineModule.getId(), true);
                            engineTransformComponent.scale.x = rocketScale;
                            engineTransformComponent.scale.y = rocketScale;
                            Mapper.rocketComponentMapper = ComponentMapper.getFor(RocketComponent.class);


                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((engineModule.entity.getComponent(TextureComponent.class).region)));

                    ComponentMapper<EngineModuleComponent> ec = ComponentMapper.getFor(EngineModuleComponent.class);
                    EngineModuleComponent emc = ec.get(engineModule.entity);

                    confirmChooseTable.add(new Label(
                            "cost: " + emc.cost + "\n" +
                                    "weight: " + emc.weight + "\n" +
                                    "power: " + emc.power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(engineModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x" + currentInventoryCell.amount, skin)).bottom().uniformX();


        }
    }

    public void scaleRocketSize(float scale){
        headTransformComponent.scale.x = scale;
        headTransformComponent.scale.y = scale;

        bodyTransformComponent.scale.x = scale;
        bodyTransformComponent.scale.y = scale;

        finsTransformComponent.scale.x = scale;
        finsTransformComponent.scale.y = scale;

        engineTransformComponent.scale.x = scale;
        engineTransformComponent.scale.y = scale;
    }





    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        pe.update(Gdx.graphics.getDeltaTime());

        stage.getBatch().begin();
        //backSprite.draw(stage.getBatch());
        backSprite.draw(stage.getBatch());
        pe.draw(stage.getBatch());
        stage.getBatch().end();

        if (pe.isComplete()) {
            pe.reset();
        }

        stage.draw();

        engine.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            SettingsSaver.saveRocket(rocket);
            game.changeScreen(MinimumCosmic.MENU);
        }


    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        objectFactory.dispose();
        stage.dispose();
        objectFactory.dispose();
    }
}