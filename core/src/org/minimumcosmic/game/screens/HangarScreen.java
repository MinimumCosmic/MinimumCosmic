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
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.TextureComponent;
import org.minimumcosmic.game.entity.components.TransformComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.components.modules.EngineModuleComponent;
import org.minimumcosmic.game.entity.components.modules.FinsModuleComponent;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;
import org.minimumcosmic.game.entity.systems.RenderingSystem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

class MyActor extends Image {
    public Entity entity;
    private final int id;

    public MyActor(TextureAtlas.AtlasRegion atlasRegion, Entity entity, int id) {
        super(atlasRegion);
        this.entity = entity;
        this.id = id;
    }

    int getId() {
        return this.id;
    }

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

        rocketAtlas = game.AssetManager.assetManager.get("images/rocket.atlas");

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());


        camera = new OrthographicCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();

        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = new OrthographicCamera();
        spriteBatch.setProjectionMatrix(camera.combined);


        engine.addSystem(renderingSystem);

        rocketPosition = new Vector2((Gdx.graphics.getWidth() * 0.75f) * RenderingSystem.PIXELS_TO_METRES, Gdx.graphics.getHeight() / 2 * RenderingSystem.PIXELS_TO_METRES);
        pe = new ParticleEffect();
        pe = game.AssetManager.assetManager.get("smoke.p");
        rocket = objectFactory.createRocket(rocketAtlas, camera, pe, "xml/rocket.xml", rocketPosition);
        scaleRocketSize(Gdx.graphics.getWidth() * 0.005f);

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
        for (int i = 1; i <= moduleNumbers; ++i) {
            if (i != 1 && i % 2 == 1) {
                table.row();
            }

            Entity head = objectFactory.createHeadModule(rocketPosition, rocketAtlas, i, false);
            //engine.removeEntity(head);

            final MyActor headModule = new MyActor(rocketAtlas.findRegion("head_" + i), head, i);
            headModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            saveRocket(1, headModule.getId());

                            engine.removeEntity(rocket.getComponent(RocketComponent.class).bodyModule);
                            engine.removeEntity(rocket.getComponent(RocketComponent.class).headModule);
                            engine.removeEntity(rocket.getComponent(RocketComponent.class).finsModule);
                            engine.removeEntity(rocket.getComponent(RocketComponent.class).engineModule);
                            rocket.removeAll();

                            rocket = objectFactory.createRocket(rocketAtlas, camera,
                                    pe, "xml/rocket.xml", rocketPosition);

                            System.out.println("It's confirmed");
                        }
                    });
                    Image img = new Image((headModule.entity.getComponent(TextureComponent.class).region));
                    confirmChooseTable.add(img);
                    confirmChooseTable.add(new Label(
                            "cost: " + headModule.entity.getComponent(HeadModuleComponent.class).cost + "\n" +
                                    "weight: " + headModule.entity.getComponent(HeadModuleComponent.class).weight + "\n" +
                                    "power: " + headModule.entity.getComponent(HeadModuleComponent.class).power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(headModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createBodyTableArea(Table table) {
        for (int i = 1; i <= moduleNumbers; ++i) {
            if (i != 1 && i % 2 == 1) {
                table.row();
            }

            Entity body = objectFactory.createBodyModule(rocketPosition, rocketAtlas, i, false);
            //engine.removeEntity(body);

            final MyActor bodyModule = new MyActor(rocketAtlas.findRegion("body_" + i), body, i);
            bodyModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            saveRocket(2, bodyModule.getId());

                            engine.removeEntity(rocket);
                            rocket = objectFactory.createRocket(rocketAtlas, camera, pe, "xml/rocket.xml", rocketPosition);

                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((bodyModule.entity.getComponent(TextureComponent.class).region)));
                    confirmChooseTable.add(new Label(
                            "cost: " + bodyModule.entity.getComponent(BodyModuleComponent.class).cost + "\n" +
                                    "weight: " + bodyModule.entity.getComponent(BodyModuleComponent.class).weight + "\n" +
                                    "power: " + bodyModule.entity.getComponent(BodyModuleComponent.class).power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(bodyModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createFinsTableArea(Table table) {
        for (int i = 1; i <= moduleNumbers; ++i) {
            if (i != 1 && i % 2 == 1) {
                table.row();
            }

            Entity fins = objectFactory.createFinsModule(rocketPosition, rocketAtlas, i, false);
            //engine.removeEntity(fins);

            final MyActor finsModule = new MyActor(rocketAtlas.findRegion("fins_" + i), fins, i);
            finsModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            saveRocket(3, finsModule.getId());

                            engine.removeEntity(rocket);
                            rocket = objectFactory.createRocket(rocketAtlas, camera, pe, "xml/rocket.xml", rocketPosition);

                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((finsModule.entity.getComponent(TextureComponent.class).region)));
                    confirmChooseTable.add(new Label(
                            "cost: " + finsModule.entity.getComponent(FinsModuleComponent.class).cost + "\n" +
                                    "weight: " + finsModule.entity.getComponent(FinsModuleComponent.class).weight + "\n" +
                                    "maneuver: " + finsModule.entity.getComponent(FinsModuleComponent.class).maneuver
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(finsModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createEngineTableArea(Table table) {
        for (int i = 1; i <= moduleNumbers; ++i) {
            if (i != 1 && i % 2 == 1) {
                table.row();
            }

            Entity eng = objectFactory.createEngineModule(rocketPosition, rocketAtlas, i, false);
            //engine.removeEntity(eng);

            final MyActor engineModule = new MyActor(rocketAtlas.findRegion("engine_" + i), eng, i);
            engineModule.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    confirmChooseTable.clear();
                    TextButton confirmButton = new TextButton("Confirm", skin);
                    confirmButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            saveRocket(4, engineModule.getId());

                            engine.removeEntity(rocket);

                            rocket = objectFactory.createRocket(rocketAtlas, camera, pe, "xml/rocket.xml", rocketPosition);

                            System.out.println("It's confirmed");
                        }
                    });
                    confirmChooseTable.add(new Image((engineModule.entity.getComponent(TextureComponent.class).region)));
                    confirmChooseTable.add(new Label(
                            "cost: " + engineModule.entity.getComponent(EngineModuleComponent.class).cost + "\n" +
                                    "weight: " + engineModule.entity.getComponent(EngineModuleComponent.class).weight + "\n" +
                                    "power: " + engineModule.entity.getComponent(EngineModuleComponent.class).power
                            , skin)).width(Gdx.graphics.getWidth() / 2);
                    confirmChooseTable.add(confirmButton).uniformX().height(confirmChooseTable.getHeight() / 2);
                    stage.addActor(confirmChooseTable);
                    return false;
                }
            });

            table.add(engineModule).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.2f);
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }


    public void scaleRocketSize(float scale) {
        rocket.getComponent(RocketComponent.class).headModule.getComponent(TransformComponent.class).scale.x = scale;
        rocket.getComponent(RocketComponent.class).headModule.getComponent(TransformComponent.class).scale.y = scale;

        rocket.getComponent(RocketComponent.class).engineModule.getComponent(TransformComponent.class).scale.x = scale;
        rocket.getComponent(RocketComponent.class).engineModule.getComponent(TransformComponent.class).scale.y = scale;

        rocket.getComponent(RocketComponent.class).finsModule.getComponent(TransformComponent.class).scale.x = scale;
        rocket.getComponent(RocketComponent.class).finsModule.getComponent(TransformComponent.class).scale.y = scale;

        rocket.getComponent(RocketComponent.class).bodyModule.getComponent(TransformComponent.class).scale.x = scale;
        rocket.getComponent(RocketComponent.class).bodyModule.getComponent(TransformComponent.class).scale.y = scale;
    }

    public void saveRocket(int moduleNumber, int idOfModule) {
        XmlReader xmlReader = new XmlReader();
        BufferedWriter out = null;
        try {
            XmlReader.Element root = xmlReader.parse(Gdx.files.local("xml/rocket.xml"));
            out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local("xml/rocket.xml").write(false)));
            XmlWriter xmlWriter = new XmlWriter(out);


            xmlWriter.element("Rocket");
            xmlWriter.element("Position").attribute("x", root.getChildByName("Position").getFloat("x"))
                    .attribute("y", root.getChildByName("Position").getFloat("y")).pop();
            xmlWriter.element("Scale").attribute("factor", root.getChildByName("Scale").getInt("factor")).pop();
            switch (moduleNumber) {
                case 1:
                    xmlWriter.element("HeadModule").attribute("id", idOfModule).pop();
                    xmlWriter.element("BodyModule").attribute("id", root.getChildByName("BodyModule").getInt("id")).pop();
                    xmlWriter.element("FinsModule").attribute("id", root.getChildByName("FinsModule").getInt("id")).pop();
                    xmlWriter.element("EngineModule").attribute("id", root.getChildByName("EngineModule").getInt("id")).pop();
                    break;
                case 2:
                    xmlWriter.element("HeadModule").attribute("id", root.getChildByName("HeadModule").getInt("id")).pop();
                    xmlWriter.element("BodyModule").attribute("id", idOfModule).pop();
                    xmlWriter.element("FinsModule").attribute("id", root.getChildByName("FinsModule").getInt("id")).pop();
                    xmlWriter.element("EngineModule").attribute("id", root.getChildByName("EngineModule").getInt("id")).pop();
                    break;
                case 3:
                    xmlWriter.element("HeadModule").attribute("id", root.getChildByName("HeadModule").getInt("id")).pop();
                    xmlWriter.element("BodyModule").attribute("id", root.getChildByName("BodyModule").getInt("id")).pop();
                    xmlWriter.element("FinsModule").attribute("id", idOfModule).pop();
                    xmlWriter.element("EngineModule").attribute("id", root.getChildByName("EngineModule").getInt("id")).pop();
                    break;
                case 4:
                    xmlWriter.element("HeadModule").attribute("id", root.getChildByName("HeadModule").getInt("id")).pop();
                    xmlWriter.element("BodyModule").attribute("id", root.getChildByName("BodyModule").getInt("id")).pop();
                    xmlWriter.element("FinsModule").attribute("id", root.getChildByName("FinsModule").getInt("id")).pop();
                    xmlWriter.element("EngineModule").attribute("id", idOfModule).pop();
                    break;
            }
            xmlWriter.flush();
            xmlWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {

                }
            }
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
        objectFactory.dispose();
        stage.dispose();
        objectFactory.dispose();
    }
}