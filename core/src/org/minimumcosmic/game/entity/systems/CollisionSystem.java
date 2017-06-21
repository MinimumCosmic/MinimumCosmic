package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import org.minimumcosmic.game.entity.components.CameraComponent;
import org.minimumcosmic.game.entity.components.CollisionComponent;
import org.minimumcosmic.game.entity.components.TypeComponent;

public class CollisionSystem extends IteratingSystem {
    ComponentMapper<CollisionComponent> cm;
    ComponentMapper<CameraComponent> pm;

    @SuppressWarnings("unchecked")
    public CollisionSystem() {
        super(Family.all(CollisionComponent.class, CameraComponent.class).get());

        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(CameraComponent.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = cm.get(entity);

        Entity collidedEntity = cc.collisionEntity;
        if (collidedEntity != null) {
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
            if (type != null) {
                switch (type.type) {
                    case TypeComponent.ENEMY:
                        // Do player damage;
                        System.out.println("player hit enemy");
                        break;
                    case TypeComponent.SCENERY:
                        System.out.println("player hit scenery");
                        break;
                    case TypeComponent.OTHER:
                        System.out.println("player hit other");
                        break;

                }
                cc.collisionEntity = null; // Collision handled reset component
            }
        }
    }
}
