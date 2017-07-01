package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.minimumcosmic.game.Mapper;
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

    private Array<Array<Integer>> inventory;

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

        inventory = new Array<Array<Integer>>();
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
        loadInventory();

        scaleRocketSize(rocketScale);

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
        for (int i = 0; i < inventory.get(0).size; ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }

            final Entity head = objectFactory.createHeadModule(rocketPosition, rocketAtlas, inventory.get(0).get(i), false);

            final MyActor headModule = new MyActor(rocketAtlas.findRegion("head_" + inventory.get(0).get(i)), head, inventory.get(0).get(i));

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
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createBodyTableArea(Table table) {
        for (int i = 0; i < inventory.get(1).size; ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }

            Entity body = objectFactory.createBodyModule(rocketPosition, rocketAtlas, inventory.get(1).get(i), false);

            final MyActor bodyModule = new MyActor(rocketAtlas.findRegion("body_" + inventory.get(1).get(i)), body, inventory.get(1).get(i));
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
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createFinsTableArea(Table table) {
        for (int i = 0; i < inventory.get(2).size; ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }

            Entity fins = objectFactory.createFinsModule(rocketPosition, rocketAtlas, inventory.get(2).get(i), false);

            final MyActor finsModule = new MyActor(rocketAtlas.findRegion("fins_" + inventory.get(2).get(i)), fins, inventory.get(2).get(i));
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
            table.add(new Label("x1", skin)).bottom().uniformX();


        }
    }

    public void createEngineTableArea(Table table) {
        for (int i = 0; i < inventory.get(3).size; ++i) {
            if (i != 0 && i % 2 == 0) {
                table.row();
            }

            Entity eng = objectFactory.createEngineModule(rocketPosition, rocketAtlas, inventory.get(3).get(i), false);

            final MyActor engineModule = new MyActor(rocketAtlas.findRegion("engine_" + inventory.get(3).get(i)), eng, inventory.get(3).get(i));
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
            table.add(new Label("x1", skin)).bottom().uniformX();


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

    public void loadInventory(){
        for(int i = 0; i < MinimumCosmic.NUMBEROFMODULES; ++i){
            Array<Integer> tmp = new Array<Integer>();
            inventory.add(tmp);
        }
        try{
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element root;
            FileHandle fileHandle = Gdx.files.local("xml/inventory.xml");
            if(fileHandle.exists()){
                root = xmlReader.parse(Gdx.files.local("xml/inventory.xml"));
            }
            else{
                root = xmlReader.parse(Gdx.files.internal("xml/inventory.xml"));
            }

            XmlReader.Element elements = root.getChildByName("HeadModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                   inventory.get(0).add(modules.getInt("id"));
            }

            elements = root.getChildByName("BodyModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                inventory.get(1).add(modules.getInt("id"));
            }

            elements = root.getChildByName("FinsModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                inventory.get(2).add(modules.getInt("id"));
            }

            elements = root.getChildByName("EngineModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                inventory.get(3).add(modules.getInt("id"));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveRocket(Entity entity){
        BufferedWriter out = null;
        try{
            ComponentMapper<RocketComponent> rc = ComponentMapper.getFor(RocketComponent.class);
            RocketComponent saveRocketComponent = rc.get(entity);


            out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local("xml/rocket.xml").write(false)));
            XmlWriter xmlWriter = new XmlWriter(out);

            xmlWriter.element("Rocket");
            xmlWriter.element("Position").attribute("x", 15f).attribute("y", 5f).pop();
            xmlWriter.element("Scale").attribute("factor", 1).pop();
            xmlWriter.element("HeadModule").attribute("id", saveRocketComponent.headModule.getComponent(HeadModuleComponent.class).id).pop();
            xmlWriter.element("BodyModule").attribute("id", saveRocketComponent.bodyModule.getComponent(BodyModuleComponent.class).id).pop();
            xmlWriter.element("FinsModule").attribute("id", saveRocketComponent.finsModule.getComponent(FinsModuleComponent.class).id).pop();
            xmlWriter.element("EngineModule").attribute("id", saveRocketComponent.engineModule.getComponent(EngineModuleComponent.class).id).pop();

            xmlWriter.flush();
            xmlWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(out != null){
                try {
                    out.close();
                }catch(IOException e){

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

        if (stage.keyDown(Input.Keys.BACK)) {
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