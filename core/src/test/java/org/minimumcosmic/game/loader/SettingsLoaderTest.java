package org.minimumcosmic.game.loader;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import org.junit.Test;
import org.minimumcosmic.game.BodyFactory;
import org.minimumcosmic.game.InventoryCell;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


/**
 * Created by Kostin on 17.07.2017.
 */
public class SettingsLoaderTest{
    String test(int output){
        if(output >= 0){
            return "integer";
        }
        return "not integer";
    }

    String notNull(Array<String> o){
        if(o == null){
            return "null";
        }
        else{
            return "notNull";
        }
    }

    String notNullList(Array<ArrayList<InventoryCell>> o){
        if(o == null){
            return "null";
        }
        else{
            return "notNull";
        }
    }

    boolean checkFall(Body body1, Body body2){
        if(body1.getPosition().y < body2.getPosition().y){
            return false;
        }
        return true;
    }

    @Test
    public void loadResearchPoint() throws Exception {
        int output = SettingsLoader.loadResearchPointTest();
        assertEquals("must be an integer value", test(output), "integer");

    }

    @Test
    public void loadModuleCharacteristics() throws Exception {
        Array<String> characteristics = SettingsLoader.loadModuleCharacteristicsTest(1, 1);
        assertEquals("must be notNull", notNull(characteristics), "notNull");
    }

    @Test
    public void loadInventory() throws Exception {
        Array<ArrayList<InventoryCell>> inventory = SettingsLoader.loadInventoryTest();
        assertEquals("must be notNull", notNullList(inventory), "notNull");
    }

    @Test
    public void collisionObject() throws Exception{
        boolean flag = false;
        World world = new World(new Vector2(0, -10f), true);
        //world.setContactListener(new B2dContactListener());
        BodyFactory bodyFactory = BodyFactory.getInstance(world);
        Body box1 = bodyFactory.makeBoxBody(0, 0, 100f, 1f, 0, BodyDef.BodyType.StaticBody);
        Body box2 = bodyFactory.makeBoxBody(0, 0.0000001f, 10f, 10f, 0, BodyDef.BodyType.DynamicBody);
        for(int i = 0; i < 2000; ++i){
            world.step(1 / 60f, 6, 2);
            if(checkFall(box1, box2)){
                flag = true;
                break;
            }
        }
        assertEquals("Flag must be true", flag, true);
        //box1.getTransform().getPosition().x;

    }
}