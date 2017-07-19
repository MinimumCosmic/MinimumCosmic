package org.minimumcosmic.game.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import org.minimumcosmic.game.InventoryCell;
import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.exception.XmlLoadException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kostin on 06.07.2017.
 */

public class SettingsLoader {
    private final static int HEAD = 0;
    private final static int BODY = 1;
    private final static int FINS = 2;
    private final static int ENGINE = 3;

    public static int loadResearchPointTest(){
        try {
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element root;
            int point;
            FileHandle fileHandle = new FileHandle("C:/Users/Kostin/Desktop/Android/summer/android/assets/xml/researchpoint.xml");
            //root = xmlReader.parse("C:/Users/Kostin/Desktop/Android/summer/android/assets/xml/researchpoint.xml");
            root = xmlReader.parse(fileHandle);
            point = root.getChildByName("Point").getInt("point");
            return point;
        }catch (RuntimeException e){
            return -1;
        }catch (IOException e){
            return -1;
        }
    }

    public static int loadResearchPoint() throws XmlLoadException{
        try {
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element root;
            int point;
            FileHandle fileHandle = Gdx.files.local("xml/researchpoint.xml");
            if (fileHandle.exists()) {
                root = xmlReader.parse(Gdx.files.local("xml/researchpoint.xml"));
            } else {
                root = xmlReader.parse(Gdx.files.internal("xml/researchpoint.xml"));
            }
            point = root.getChildByName("Point").getInt("point");
            return point;
        }catch (IOException e){
            throw new XmlLoadException(2);
        }
        catch (RuntimeException e){
            throw new XmlLoadException(2);
        }
    }

    public static Array<String> loadModuleCharacteristicsTest(int module, int id){
        Array<String> characteristics = new Array<String>();
        try{
            XmlReader xmlReader = new XmlReader();
            FileHandle fileHandle = new FileHandle("C:/Users/Kostin/Desktop/Android/summer/android/assets/xml/modules.xml");
            XmlReader.Element root = xmlReader.parse(fileHandle);
            XmlReader.Element m;
            switch(module){
                case HEAD:
                    m = root.getChildByName("HeadModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("hp").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("power").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("fuel").toString());
                    break;
                case BODY:
                    m = root.getChildByName("BodyModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("power").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("fuel").toString());
                    break;
                case FINS:
                    m = root.getChildByName("FinsModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("maneuver").toString());
                    break;
                case ENGINE:
                    m = root.getChildByName("EngineModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("power").toString());
                    break;
            }
        }catch(IOException e){
            return null;
        }catch (RuntimeException e){
            return null;
        }
        return characteristics;
    }

    public static Array<String> loadModuleCharacteristics(int module, int id) throws XmlLoadException{
        Array<String> characteristics = new Array<String>();
        try{
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/modules.xml"));
            XmlReader.Element m;
            switch(module){
                case HEAD:
                    m = root.getChildByName("HeadModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("hp").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("power").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("fuel").toString());
                    break;
                case BODY:
                    m = root.getChildByName("BodyModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("power").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("fuel").toString());
                    break;
                case FINS:
                    m = root.getChildByName("FinsModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("maneuver").toString());
                    break;
                case ENGINE:
                    m = root.getChildByName("EngineModules").getChild(id - 1);
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("weight").toString());
                    characteristics.add(m.getChildByName("BasicProperties").getAttribute("cost").toString());
                    characteristics.add(m.getChildByName("AdvancedProperties").getAttribute("power").toString());
                    break;
            }
        }catch(IOException e){
            throw new XmlLoadException(1);
        }catch (RuntimeException e){
            throw new XmlLoadException(1);
        }
        return characteristics;
    }

    public static Array<ArrayList<InventoryCell>> loadInventoryTest(){
        Array<ArrayList<InventoryCell>> inventory = new Array<ArrayList<InventoryCell>>();
        for(int i = 0; i < MinimumCosmic.NUMBEROFMODULES; ++i){
            ArrayList<InventoryCell> tmp = new ArrayList<InventoryCell>();
            inventory.add(tmp);
        }
        try{
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element root;
            FileHandle fileHandle = new FileHandle("C:/Users/Kostin/Desktop/Android/summer/android/assets/xml/inventory.xml");
            root = xmlReader.parse(fileHandle);

            XmlReader.Element elements = root.getChildByName("HeadModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(0).add(tmp);
            }

            elements = root.getChildByName("BodyModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(1).add(tmp);
            }

            elements = root.getChildByName("FinsModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(2).add(tmp);
            }

            elements = root.getChildByName("EngineModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(3).add(tmp);
            }

            return inventory;
        }
        catch (IOException e){
            return null;
        } catch (RuntimeException e){
            return null;
        }
    }

    public static Array<ArrayList<InventoryCell>> loadInventory() throws XmlLoadException{
        Array<ArrayList<InventoryCell>> inventory = new Array<ArrayList<InventoryCell>>();
        for(int i = 0; i < MinimumCosmic.NUMBEROFMODULES; ++i){
            ArrayList<InventoryCell> tmp = new ArrayList<InventoryCell>();
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
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(0).add(tmp);
            }

            elements = root.getChildByName("BodyModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(1).add(tmp);
            }

            elements = root.getChildByName("FinsModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(2).add(tmp);
            }

            elements = root.getChildByName("EngineModules");
            for(XmlReader.Element modules : elements.getChildrenByName("Module")){
                InventoryCell tmp = new InventoryCell();
                tmp.id = modules.getInt("id");
                tmp.amount = modules.getInt("amount");
                inventory.get(3).add(tmp);
            }

            return inventory;
        }
        catch (IOException e){
            throw new XmlLoadException(0);
        } catch (RuntimeException e){
            throw new XmlLoadException(0);
        }
    }
}
