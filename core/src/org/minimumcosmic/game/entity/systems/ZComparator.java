package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import org.minimumcosmic.game.entity.components.TransformComponent;

import java.util.Comparator;



public class ZComparator implements Comparator<Entity> {

    private ComponentMapper<TransformComponent> cmTransform;

    public ZComparator() {
        cmTransform = ComponentMapper.getFor(TransformComponent.class);
    }
    @Override
    public int compare(Entity o1, Entity o2) {
        float az = cmTransform.get(o1).position.z;
        float bz = cmTransform.get(o2).position.z;
        int res = 0;
        if (az > bz) {
            res = 1;
        } else if (az < bz) {
            res = -1;
        }
        return res;
    }
}
