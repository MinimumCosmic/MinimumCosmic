package org.minimumcosmic.game.saver;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlWriter;

import org.minimumcosmic.game.InventoryCell;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.components.modules.EngineModuleComponent;
import org.minimumcosmic.game.entity.components.modules.FinsModuleComponent;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * Created by Kostin on 06.07.2017.
 */

public class SettingsSaver {
    public static void saveResearchPoint(int researchPoint){
        BufferedWriter out = null;
        try{
            out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local("xml/researchpoint.xml").write(false)));
            XmlWriter xmlWriter = new XmlWriter(out);
            xmlWriter.element("ResearchPoint");
            xmlWriter.element("Point").attribute("point",
                     + researchPoint).pop();
            xmlWriter.flush();
            xmlWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            if(out != null){
                try {
                    out.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveRocket(Entity entity){
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

    public static void saveInventory(Array<ArrayList<InventoryCell>> inventory){
        BufferedWriter out = null;
        for(int i = 0; i < 4; ++i){
            Collections.sort(inventory.get(i));
        }
        try{
            out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local("xml/inventory.xml").write(false)));
            XmlWriter xmlWriter = new XmlWriter(out);
            xmlWriter.element("Inventory");
            xmlWriter.element("HeadModules");
            Iterator itrHead = inventory.get(0).iterator();
            while(itrHead.hasNext()){
                InventoryCell headModule = (InventoryCell) itrHead.next();
                xmlWriter.element("Module").attribute("id", headModule.id).attribute("amount", headModule.amount).pop();
            }
            xmlWriter.pop();
            xmlWriter.element("BodyModules");
            Iterator itrBody = inventory.get(1).iterator();
            while(itrBody.hasNext()){
                InventoryCell bodyModule = (InventoryCell) itrBody.next();
                xmlWriter.element("Module").attribute("id", bodyModule.id).attribute("amount", bodyModule.amount).pop();
            }
            xmlWriter.pop();
            xmlWriter.element("FinsModules");
            Iterator itrFins = inventory.get(2).iterator();
            while(itrFins.hasNext()){
                InventoryCell finsModule = (InventoryCell) itrFins.next();
                xmlWriter.element("Module").attribute("id", finsModule.id).attribute("amount", finsModule.amount).pop();
            }
            xmlWriter.pop();
            xmlWriter.element("EngineModules");
            Iterator itrEngine = inventory.get(3).iterator();
            while(itrEngine.hasNext()){
                InventoryCell engineModule = (InventoryCell) itrEngine.next();
                xmlWriter.element("Module").attribute("id", engineModule.id).attribute("amount", engineModule.amount).pop();
            }
            xmlWriter.pop();
            xmlWriter.flush();
            xmlWriter.close();
        }catch (IOException e){
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
}
