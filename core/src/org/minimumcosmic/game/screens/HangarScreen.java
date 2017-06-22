package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import org.minimumcosmic.game.entity.systems.RenderingSystem;

class HeadActor extends Image{
    Texture sprite;

    public HeadActor(Texture texture){
        super(texture);
        //setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        setBounds(0, 0, sprite.getWidth(), sprite.getHeight());
        setTouchable(Touchable.enabled);

        addListener(new InputListener(){

            public boolean keyDown(InputEvent event, int keycode){
                if(keycode == Input.Keys.RIGHT){
                    //TODO

                }
                return true;
            }
        });
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
    private MinimumCosmic game;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private TextureAtlas rocketAtlas;
    private Entity rocket;
    private PooledEngine engine;
    private ObjectFactory objectFactory;
    private OrthographicCamera camera;
    Sprite backSprite;
    ParticleEffect pe;

    public HangarScreen(MinimumCosmic game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());

        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
        textureAtlas = game.AssetManager.assetManager.get("images/loading_screen.atlas");
        rocketAtlas = game.AssetManager.assetManager.get("images/game_screen.atlas");

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();

        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = new OrthographicCamera();
        spriteBatch.setProjectionMatrix(camera.combined);


        engine.addSystem(renderingSystem);

        pe = new ParticleEffect();
        pe = game.AssetManager.assetManager.get("smoke.p");
        rocket = objectFactory.createRocket(rocketAtlas, camera, pe);

    }

    @Override
    public void show(){
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
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





        final Table listTable = new Table();
        listTable.top();
        listTable.left();
        listTable.setBounds(0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);
        listTable.debug();

        final Table confirmChooseTable = new Table();
        confirmChooseTable.setBounds(0, Gdx.graphics.getHeight() * 0.05f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.15f);
        confirmChooseTable.debug();

        Table rocketTable = new Table();
        rocketTable.setBounds(listTable.getWidth(), Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);
        rocketTable.debug();



        //Top buttons to switch module
        Table modules = new Table();
        modules.setBounds(0, listTable.getHeight() + listTable.getY(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        modules.bottom();

        TextButton headButton = new TextButton("Head", skin);
        headButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listTable.clear();
                Table tmpTable = new Table();
                tmpTable.setBounds(0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);

                Image image = new Image(rocketAtlas.findRegion("head_1"));
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
                tmpTable.add(new Label("x1", skin)).bottom().uniformX();

                listTable.add(new ScrollPane(tmpTable)).uniformX();
                /*listTable.add(new ScrollPane(new Label("head list", skin), skin));
                listTable.add(new Image(rocketAtlas.findRegion("head_1")));*/

            }
        });

        TextButton bodyButton = new TextButton("Body", skin);
        bodyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listTable.clear();
                Table tmpTable = new Table();
                tmpTable.setBounds(0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);

                Image image = new Image(rocketAtlas.findRegion("body_1"));
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
                        confirmChooseTable.add(new Image(rocketAtlas.findRegion("body_1"))).uniformX();
                        confirmChooseTable.add(new Label("There \n will be \n something", skin)).uniformX();
                        confirmChooseTable.add(confirmButton).uniformX();
                        stage.addActor(confirmChooseTable);
                        return false;
                    }
                });
                tmpTable.add(image).uniformX();
                tmpTable.add(new Label("x1", skin)).bottom().uniformX();

                listTable.add(new ScrollPane(tmpTable)).uniformX();
            }
        });

        TextButton finsButton = new TextButton("Fins", skin);
        finsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listTable.clear();
                Table tmpTable = new Table();

                Image image = new Image(rocketAtlas.findRegion("fins_1"));
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
                tmpTable.add(new Label("x1", skin)).bottom().uniformX();

                listTable.add(new ScrollPane(tmpTable)).uniformX();
            }
        });

        TextButton engineButton = new TextButton("Engine", skin);
        engineButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listTable.clear();
                Table tmpTable = new Table();

                Image image = new Image(rocketAtlas.findRegion("engine_1"));
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
                tmpTable.add(new Label("x1", skin)).bottom().uniformX();
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




    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        //pe.update(Gdx.graphics.getDeltaTime());

        stage.getBatch().begin();
        backSprite.draw(stage.getBatch());
        //pe.draw(stage.getBatch());
        stage.getBatch().end();

        /*if (pe.isComplete()) {
            pe.reset();
        }*/

        stage.draw();

        //engine.update(delta);
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

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
