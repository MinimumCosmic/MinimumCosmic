package org.minimumcosmic.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlWriter;

import org.minimumcosmic.game.entity.components.PickupComponent;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.components.modules.EngineModuleComponent;
import org.minimumcosmic.game.entity.components.modules.FinsModuleComponent;
import org.minimumcosmic.game.entity.components.modules.HeadModuleComponent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Kostin on 06.07.2017.
 */

public class SettingsSaver {
    public static void saveResearchPoint(Entity entity){
        int point = SettingsLoader.loadResearchPoint();
        BufferedWriter out = null;
        try{
            out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local("xml/researchpoint.xml").write(false)));
            XmlWriter xmlWriter = new XmlWriter(out);
            xmlWriter.element("ResearchPoint");
            xmlWriter.element("Point").attribute("point",
                    point + entity.getComponent(PickupComponent.class).count).pop();
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
}
