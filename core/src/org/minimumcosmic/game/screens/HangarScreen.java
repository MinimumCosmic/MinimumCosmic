package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.entity.components.TextureComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.components.modules.EngineModuleComponent;
import org.minimumcosmic.game.entity.components.modules.FinsModuleComponent;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;
import org.minimumcosmic.game.entity.systems.RenderingSystem;

class MyActor extends Image{
    public Entity entity;


    public MyActor(TextureAtlas.AtlasRegion atlasRegion, Entity entity){
        super(atlasRegion);
        this.entity = entity;
    }

    /*public void draw(Batch batch, float parentAlpha){
        //super.draw(batch, parentAlpha);
        batch.draw();

    }

    public void act(float delta){
        super.act(delta);
    }*/

}

public class HangarScreen implements Screen {
    final static int moduleNumbers = 3;

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

    Vector2 rocketPosition = new Vector2(120, 120);

    final Table confirmChooseTable = new Table(skin);

    public HangarScreen(MinimumCosmic game) {
        this.game = game;
        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
        textureAtlas = game.AssetManager.assetManager.get("images/loading_screen.atlas");
        rocketAtlas = game.AssetManager.assetManager.get("images/game_screen.atlas");

        stage = new Stage(new ScreenViewport());

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


    }

    @Override
    public void show(){
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();



        camera = new OrthographicCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();

        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = new OrthographicCamera();
        spriteBatch.setProjectionMatrix(camera.combined);


        engine.addSystem(renderingSystem);

        rocketPosition = new Vector2((Gdx.graphics.getWidth() * 0.6f) * RenderingSystem.PIXELS_TO_METRES, Gdx.graphics.getHeight() / 2 * RenderingSystem.PIXELS_TO_METRES);
        pe = new ParticleEffect();
        pe = game.AssetManager.assetManager.get("smoke.p");
        rocket = objectFactory.createRocket(rocketAtlas, camera, pe, "xml/rocket.xml", rocketPosition);

        engine.addSystem(renderingSystem);

        Gdx.input.setInputProcessor(stage);

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
        listTable.setBounds(0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);
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
               /* Image image = new Image(rocketAtlas.findRegion("head_1"));
                image.addListener(new InputListener() {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        confirmChooseTable.clear();
                        TextButton confirmButton = new TextButton("Confirm", skin);
                        confirmButton.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                System.out.println("It's confirmed");
                            }
                        });
                        confirmChooseTable.add(new Image(rocketAtlas.findRegion("head_1"))).uniformX();
                        confirmChooseTable.add(new Label("There \n will be \n something", skin)).uniformX();
                        confirmChooseTable.add(confirmButton).uniformX();
                        stage.addActor(confirmChooseTable);
                        return false;
                    }
                });
                tmpTable.add(image).uniformX();
                tmpTable.add(new Label("x1", skin)).bottom().uniformX();*/

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
                /*Image image = new Image(rocketAtlas.findRegion("fins_1"));
                image.addListener(new InputListener() {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        confirmChooseTable.clear();
                        TextButton confirmButton = new TextButton("Confirm", skin);
                        confirmButton.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                System.out.println("It's confirmed");
                            }
                        });
                        confirmChooseTable.add(new Image(rocketAtlas.findRegion("fins_1"))).uniformX();
                        confirmChooseTable.add(new Label("There \n will be \n something", skin)).uniformX();
                        confirmChooseTable.add(confirmButton).uniformX();
                        stage.addActor(confirmChooseTable);
                        return false;
                    }
                });
                tmpTable.add(image).uniformX();
                tmpTable.add(new Label("x1", skin)).bottom().uniformX();*/

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

                /*Image image = new Image(rocketAtlas.findRegion("engine_1"));
                image.addListener(new InputListener() {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        confirmChooseTable.clear();
                        TextButton confirmButton = new TextButton("Confirm", skin);
                        confirmButton.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                System.out.println("It's confirmed");
                            }
                        });
                        confirmChooseTable.add(new Image(rocketAtlas.findRegion("engine_1"))).uniformX();
                        confirmChooseTable.add(new Label("There \n will be \n something", skin)).uniformX();
                        confirmChooseTable.add(confirmButton).uniformX();
                        stage.addActor(confirmChooseTable);
                        return false;
                    }
                });
                tmpTable.add(image);
                tmpTable.add(new Label("x1", skin)).bottom().uniformX();*/

                listTable.add(new ScrollPane(tmpTable)).uniformX();
            }
        });

        modules.add(headButton).fillX().uniformX();
        modules.add(bodyButton).fillX().uniformX();
        modules.add(finsButton).fillX().uniformX();
        modules.add(engineButton).fillX().uniformX();
        modules.row();

        //adding actors
        stage.addActor(listTable);
        stage.addActor(rocketTable);
        stage.addActor(modules);

        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        pe.scaleEffect(0.25f);
        pe.start();
    }

    public void createEngineTableArea(Table table){
        for(int i = 1; i <= moduleNumbers; ++i){
            if(i != 1 && i % 2 == 1){
                table.row();
            }

            final MyActor engineModule = new MyActor(rocketAtlas.findRegion("engine_" + i), objectFactory.createEngineModule(rocketPosition, rocketAtlas, i));
            engineModule.addListener(new InputListener(){
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((engineModule.entity.getComponent(TextureComponent.class).region)));
                    confirmChooseTable.add(new Label(
                            "cost: " + engineModule.entity.getComponent(EngineModuleComponent.class).cost + "\n" +
                                    "weight: " + engineModule.entity.getComponent(EngineModuleComponent.class).weight + "\n" +
                                    "power: " + engineModule.entity.getComponent(EngineModuleComponent.class).power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).width(Gdx.graphics.getWidth() / 3);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(engineModule).uniformX();
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createFinsTableArea(Table table){
        for(int i = 1; i <= moduleNumbers; ++i){
            if(i != 1 && i % 2 == 1){
                table.row();
            }

            final MyActor finsModule = new MyActor(rocketAtlas.findRegion("fins_" + i), objectFactory.createFinsModule(rocketPosition, rocketAtlas, i));
            finsModule.addListener(new InputListener(){
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((finsModule.entity.getComponent(TextureComponent.class).region)));
                    confirmChooseTable.add(new Label(
                            "cost: " + finsModule.entity.getComponent(FinsModuleComponent.class).cost + "\n" +
                                    "weight: " + finsModule.entity.getComponent(FinsModuleComponent.class).weight + "\n" +
                                    "maneuver: " + finsModule.entity.getComponent(FinsModuleComponent.class).maneuver
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).width(Gdx.graphics.getWidth() / 3);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(finsModule).uniformX();
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createBodyTableArea(Table table){
        for(int i = 1; i <= moduleNumbers; ++i){
            if(i != 1 && i % 2 == 1){
                table.row();
            }

            final MyActor bodyModule = new MyActor(rocketAtlas.findRegion("body_" + i), objectFactory.createBodyModule(rocketPosition, rocketAtlas, i));
            bodyModule.addListener(new InputListener(){
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((bodyModule.entity.getComponent(TextureComponent.class).region)));
                    confirmChooseTable.add(new Label(
                            "cost: " + bodyModule.entity.getComponent(BodyModuleComponent.class).cost + "\n" +
                                    "weight: " + bodyModule.entity.getComponent(BodyModuleComponent.class).weight + "\n" +
                                    "power: " + bodyModule.entity.getComponent(BodyModuleComponent.class).power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).width(Gdx.graphics.getWidth() / 3);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(bodyModule).uniformX();
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createHeadTableArea(Table table){
        for(int i = 1; i <= moduleNumbers; ++i){
            if(i != 1 && i % 2 == 1){
                table.row();
            }

            final MyActor headModule = new MyActor(rocketAtlas.findRegion("head_" + i), objectFactory.createHeadModule(rocketPosition, rocketAtlas, i));
            headModule.addListener(new InputListener(){
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((headModule.entity.getComponent(TextureComponent.class).region)));
                    confirmChooseTable.add(new Label(
                            "cost: " + headModule.entity.getComponent(HeadModuleComponent.class).cost + "\n" +
                            "weight: " + headModule.entity.getComponent(HeadModuleComponent.class).weight + "\n" +
                                    "power: " + headModule.entity.getComponent(HeadModuleComponent.class).power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).width(Gdx.graphics.getWidth() / 3);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(headModule).uniformX();
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }


    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        //pe.update(Gdx.graphics.getDeltaTime());

        stage.getBatch().begin();
        //backSprite.draw(stage.getBatch());
        backSprite.draw(stage.getBatch());
        //pe.draw(stage.getBatch());
        stage.getBatch().end();

        /*if (pe.isComplete()) {
            pe.reset();
        }*/

        stage.draw();

        engine.update(delta);
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
        stage.dispose();
    }
}