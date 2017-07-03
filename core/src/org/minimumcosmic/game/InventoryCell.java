package org.minimumcosmic.game;

/**
 * Created by Kostin on 02.07.2017.
 */

public class InventoryCell implements Comparable<InventoryCell>{
    public int id;
    public int amount;
    @Override
    public int compareTo(InventoryCell inventoryCell){
        int returnValue = 1;
        if(id < inventoryCell.id){
            returnValue = -1;
        }
        return returnValue;
    }
}
