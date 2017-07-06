package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.minimumcosmic.game.BoxActor;
import org.minimumcosmic.game.InventoryCell;
import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.MyActor;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.SettingsLoader;
import org.minimumcosmic.game.controller.TouchscreenController;

import java.util.HashMap;
import java.util.Random;

public class ShopScreen implements Screen {
    private final int HEAD = 0;
    private final int BODY = 1;
    private final int FINS = 2;
    private final int ENGINE = 3;

    private final int SIMPLEBOXCOST = 50;
    private final int MEDIUMBOXCOST = 200;
    private final int SUPERBOXCOST = 1000;

    private MinimumCosmic game;
    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private TextureAtlas rocketAtlas;
    private TextureAtlas boxAtlas;
    private TextureAtlas money;
    private Vector2 rocketPosition;
    private Table itemTable;
    private Table boxTable;
    private Table simpleBoxTable;
    private Table mediumBoxTable;
    private Table superBoxTable;
    private Table costTable;
    private Table currentMoney;
    private PooledEngine engine;
    private ObjectFactory objectFactory;
    private Image moneyIcon;
    private Array<Array<InventoryCell>> inventory;
    private TouchscreenController touchscreenController;
    Sprite backSprite;


    public ShopScreen(MinimumCosmic game){
        this.game = game;

        //stage.getRoot().getColor().a  = 0;
       // stage.getRoot().addAction(Actions.fadeIn(0.5f));

        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
        textureAtlas = game.AssetManager.assetManager.get("images/loading_screen.atlas");
        rocketAtlas = game.AssetManager.assetManager.get("images/rocket.atlas");
        boxAtlas = game.AssetManager.assetManager.get("boxes2/box.atlas");

        money = new TextureAtlas(Gdx.files.internal("images/items.atlas"));

        moneyIcon = new Image(money.findRegion("icon"));

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        rocketPosition = new Vector2(0, 0);

        Gdx.input.setCatchBackKey(true);
    }

    public void init(){
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);

        inventory = SettingsLoader.loadInventory();

        moneyIcon = new Image(new Texture(Gdx.files.internal("images/money_icon.png")));

        itemTable = new Table();
        itemTable.setBounds( Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.5f,
                Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.4f);
        itemTable.debug();

        if(simpleBoxTable == null)
            simpleBoxTable = new Table();
        else
            simpleBoxTable.clear();
        if(mediumBoxTable == null)
            mediumBoxTable = new Table();
        else
            mediumBoxTable.clear();
        if(superBoxTable == null)
            superBoxTable = new Table();
        else
            superBoxTable.clear();

        boxTable = new Table();
        boxTable.bottom();
        boxTable.setBounds(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.2f,
                Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.2f);
        boxTable.debug();

        costTable = new Table();
        costTable.top();
        costTable.setBounds(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.15f,
                Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.05f);
        costTable.debug();


        final Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);
        currentMoney = new Table();
        currentMoney.debug();
        currentMoney.pad(10f);
        currentMoney.add(new Image(money.findRegion("icon"))).width(boxTable.getHeight() / 6f).height(boxTable.getHeight() / 6f);
        currentMoney.add(new Label("" + SettingsLoader.loadResearchPoint(), skin));
        table.top().defaults();
        table.right().defaults();
        table.add(currentMoney);

        touchscreenController = new TouchscreenController();
    }

    public void fillBoxTable(){

        fillSimpleBoxTable(simpleBoxTable);
        fillMediumBoxTable(mediumBoxTable);
        fillSuperBoxTable(superBoxTable);
        simpleBoxTable.debug();
        mediumBoxTable.debug();
        superBoxTable.debug();
        boxTable.add(simpleBoxTable);//.width(boxTable.getWidth() / 3f).height(boxTable.getHeight());
        boxTable.add(mediumBoxTable);//.width(boxTable.getWidth() / 3f).height(boxTable.getHeight());
        boxTable.add(superBoxTable);//.width(boxTable.getWidth() / 3f).height(boxTable.getHeight());
    }

    public void setCost(Table simpleCost, Table mediumCost, Table superCost){

        simpleCost.setWidth(costTable.getWidth() / 3f);
        simpleCost.setHeight(costTable.getHeight());
        simpleCost.add(new Image(money.findRegion("icon"))).width(simpleCost.getHeight() * 0.8f).height(simpleCost.getHeight() * 0.8f);
        simpleCost.add(new Label("" + SIMPLEBOXCOST, skin));

        mediumCost.setWidth(costTable.getWidth() / 3f);
        mediumCost.setHeight(costTable.getHeight());
        mediumCost.add(new Image(money.findRegion("icon"))).width(simpleCost.getHeight() * 0.8f).height(simpleCost.getHeight() * 0.8f);
        mediumCost.add(new Label("" + MEDIUMBOXCOST, skin));

        superCost.setWidth(costTable.getWidth() / 3f);
        superCost.setHeight(costTable.getHeight());
        superCost.add(new Image(money.findRegion("icon"))).width(simpleCost.getHeight() * 0.8f).height(simpleCost.getHeight() * 0.8f);
        superCost.add(new Label("" + SUPERBOXCOST, skin));

    }

    private void fillSimpleBoxTable(Table simpleBoxTable){
        final BoxActor simpleBox = new BoxActor(boxAtlas.findRegion("box_1"), 1);
        simpleBox.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("1");
                return false;
            }
        });
        simpleBoxTable.add(simpleBox).width(boxTable.getWidth() / 3f).height(boxTable.getWidth() / 3f);
    }

    private void fillMediumBoxTable(Table mediumBoxTable){
        final BoxActor mediumBox = new BoxActor(boxAtlas.findRegion("box_2"), 2);
        mediumBox.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("2");
                return false;
            }
        });
        mediumBoxTable.add(mediumBox).width(boxTable.getWidth() / 3f).height(boxTable.getWidth() / 3f);
    }

    private void fillSuperBoxTable(Table superBoxTable){
        final BoxActor superBox = new BoxActor(boxAtlas.findRegion("box_3"), 3);
        superBox.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("3");
                return false;
            }
        });
        superBoxTable.add(superBox).width(boxTable.getWidth() / 3f).height(boxTable.getWidth() / 3f);
    }

    public Array<HashMap<Integer, Boolean>> getRandomModules(){
        Array<HashMap<Integer, Boolean>> randomModules = new Array<HashMap<Integer, Boolean>>();
        for(int i = 0; i < 4; ++i){
            randomModules.add(new HashMap<Integer, Boolean>());
        }
        int cnt = 0;
        int module;
        int id;
        Random random = new Random(System.currentTimeMillis());
        while(cnt < 8){
            module = random.nextInt(4);
            id = random.nextInt(3) + 1;
            if(!randomModules.get(module).containsKey(id)){
                randomModules.get(module).put(id, true);
                cnt++;
            }
        }
        return randomModules;
    }

    private void fillItemTable(){
        Array<HashMap<Integer, Boolean>> modules = getRandomModules();
        int count = 0;
        for(int i = 0; i < 4; ++i){
            for(HashMap.Entry<Integer, Boolean> tmp : modules.get(i).entrySet()){
                switch(i){
                    case HEAD:
                        Entity head = objectFactory.createHeadModule(rocketPosition, rocketAtlas, tmp.getKey(), false);
                        final MyActor headModule = new MyActor(rocketAtlas.findRegion("head_" + tmp.getKey()),
                                head, tmp.getKey());
                        final MyActor tmpHeadActor = new MyActor(rocketAtlas.findRegion("head_" + tmp.getKey()),
                            head, tmp.getKey());
                        headModule.addListener(new InputListener(){
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                                System.out.println("HEAD");

                                createConfirmDialog(tmpHeadActor, HEAD);
                                return false;
                            }
                        });
                        itemTable.add(headModule).width(itemTable.getWidth() * 0.25f).height(itemTable.getHeight() * 0.5f);
                        count++;
                        break;
                    case BODY:
                        Entity body = objectFactory.createBodyModule(rocketPosition, rocketAtlas, tmp.getKey(), false);
                        MyActor bodyModule = new MyActor(rocketAtlas.findRegion("body_" + tmp.getKey()),
                                body, tmp.getKey());
                        final MyActor tmpBodyActor = new MyActor(rocketAtlas.findRegion("body_" + tmp.getKey()),
                                body, tmp.getKey());
                        bodyModule.addListener(new InputListener(){
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                                System.out.println("BODY");
                                createConfirmDialog(tmpBodyActor, BODY);
                                return false;
                            }
                        });
                        itemTable.add(bodyModule).width(itemTable.getWidth() * 0.25f).height(itemTable.getHeight() * 0.5f);
                        count++;
                        break;
                    case FINS:
                        Entity fins = objectFactory.createFinsModule(rocketPosition, rocketAtlas, tmp.getKey(), false);
                        MyActor finsModule = new MyActor(rocketAtlas.findRegion("fins_" + tmp.getKey()),
                                fins, tmp.getKey());
                        final MyActor tmpFinsActor = new MyActor(rocketAtlas.findRegion("fins_" + tmp.getKey()),
                                fins, tmp.getKey());
                        finsModule.addListener(new InputListener(){
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                                System.out.println("FINS");
                                createConfirmDialog(tmpFinsActor, FINS);
                                return false;
                            }
                        });
                        itemTable.add(finsModule).width(itemTable.getWidth() * 0.25f).height(itemTable.getHeight() * 0.5f);
                        count++;
                        break;
                    case ENGINE:
                        Entity engine = objectFactory.createEngineModule(rocketPosition, rocketAtlas, tmp.getKey(), false);
                        MyActor engineModule = new MyActor(rocketAtlas.findRegion("engine_" + tmp.getKey()),
                                engine, tmp.getKey());
                        final MyActor tmpEngineActor = new MyActor(rocketAtlas.findRegion("engine_" + tmp.getKey()),
                                engine, tmp.getKey());
                        engineModule.addListener(new InputListener(){
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                                System.out.println("ENGINE");
                                createConfirmDialog(tmpEngineActor, ENGINE);
                                return false;
                            }
                        });
                        itemTable.add(engineModule).width(itemTable.getWidth() * 0.25f).height(itemTable.getHeight() * 0.5f);
                        count++;
                        break;
                }
                if(count == 4){
                    itemTable.row();
                }
            }
        }
    }

    public void createConfirmDialog(MyActor actor, int module){
        final Dialog dialog = new Dialog("Confirm", skin);
        Table moduleTable = new Table();
        Table tmpTable = new Table();
        Table buttonTable = new Table();
        Table costTable = new Table();
        costTable.add(new Label("Buy for : ", skin));
        costTable.add(new Image(money.findRegion("icon")));
        buttonTable.center();
        TextButton yes = new TextButton("Yes", skin);
        yes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        TextButton no = new TextButton("No", skin);
        no.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });

        Table settingTable = new Table(skin);
        Array<String> characteristics = SettingsLoader.loadModuleCharacteristics(module, actor.getId());
        switch(module){
            case HEAD:
                settingTable.add("weight : " + characteristics.get(0));
                settingTable.row();
                settingTable.add("hp : " + characteristics.get(2));
                settingTable.row();
                settingTable.add("power : " + characteristics.get(3));
                settingTable.row();
                settingTable.add("fuel : " + characteristics.get(4));
                settingTable.row();
                costTable.add(new Label("" + characteristics.get(1) + " ?", skin));
                break;
            case BODY:
                settingTable.add("weight : " + characteristics.get(0));
                settingTable.row();
                settingTable.add("power : " + characteristics.get(2));
                settingTable.row();
                settingTable.add("fuel : " + characteristics.get(3));
                settingTable.row();
                costTable.add(new Label("" + characteristics.get(1) + " ?", skin));
                break;
            case FINS:
                settingTable.add("weight : " + characteristics.get(0));
                settingTable.row();
                settingTable.add("maneuver : " + characteristics.get(2));
                settingTable.row();
                costTable.add(new Label("" + characteristics.get(1) + " ?", skin));
                break;
            case ENGINE:
                settingTable.add("weight : " + characteristics.get(0));
                settingTable.row();
                settingTable.add("power : " + characteristics.get(2));
                settingTable.row();
                costTable.add(new Label("" + characteristics.get(1) + " ?", skin));
                break;
        }
        moduleTable.add(actor).width(itemTable.getWidth() * 0.25f).height(itemTable.getHeight() * 0.5f).fillX();
        moduleTable.add(settingTable).fillX();
        buttonTable.add(yes).center().padRight(Gdx.graphics.getWidth() * 0.05f);
        buttonTable.add(no).center();
        tmpTable.add(moduleTable);
        tmpTable.row();
        tmpTable.add(costTable);
        tmpTable.row();
        tmpTable.add(buttonTable);
        dialog.add(tmpTable);
        dialog.show(stage);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        init();
        fillBoxTable();
        fillItemTable();

        Table simpleCost = new Table();
        simpleCost.debug();
        simpleCost.setBounds(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.15f,
                boxTable.getWidth() / 3f, Gdx.graphics.getHeight() * 0.05f);

        Table mediumCost = new Table();
        mediumCost.debug();
        mediumCost.setBounds(simpleCost.getX() + simpleCost.getWidth(), Gdx.graphics.getHeight() * 0.15f,
                boxTable.getWidth() / 3f, Gdx.graphics.getHeight() * 0.05f);

        Table superCost = new Table();
        superCost.debug();
        superCost.setBounds(mediumCost.getX() + mediumCost.getWidth(), Gdx.graphics.getHeight() * 0.15f,
                boxTable.getWidth() / 3f, Gdx.graphics.getHeight() * 0.05f);

        setCost(simpleCost, mediumCost, superCost);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(touchscreenController);
        Gdx.input.setInputProcessor(inputMultiplexer);



        stage.addActor(itemTable);
        stage.addActor(boxTable);
        stage.addActor(simpleCost);
        stage.addActor(mediumCost);
        stage.addActor(superCost);
    }

    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        stage.getBatch().begin();
        backSprite.draw(stage.getBatch());
        stage.getBatch().end();

        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
    public void dispose(){
        objectFactory.dispose();
        stage.dispose();
        objectFactory.dispose();
    }
}
