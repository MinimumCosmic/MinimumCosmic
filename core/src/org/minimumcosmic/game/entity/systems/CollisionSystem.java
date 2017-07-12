package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.entity.components.*;

public class CollisionSystem extends IteratingSystem {
    ComponentMapper<CollisionComponent> cm;
    ComponentMapper<CameraComponent> pm;
    ObjectFactory objectFactory;
    Engine engine;

    @SuppressWarnings("unchecked")
    public CollisionSystem(ObjectFactory objectFactory, Engine engine) {
        super(Family.all(CollisionComponent.class, CameraComponent.class).get());

        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(CameraComponent.class);
        this.objectFactory = objectFactory;
        this.engine = engine;
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = cm.get(entity);

        Entity collidedEntity = cc.collisionEntity;
        if (collidedEntity != null) {
            TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
            float speed = cc.speed;
            if (type != null) {
                switch (type.type) {
                    case TypeComponent.ENEMY:
                        if (speed > 200.0f) {
                        entity.getComponent(RocketComponent.class).health -= speed;
                        System.out.println("Inertia: " + entity.getComponent(B2dBodyComponent.class).body.getInertia()
                                + " Speed: " + speed
                                + " Health left: " + entity.getComponent(RocketComponent.class).health);
                        }
                        break;
                    case TypeComponent.SCENERY:
                        if (speed > 200.0f) {
                            entity.getComponent(RocketComponent.class).health -= speed;
                            System.out.println("Inertia: " + entity.getComponent(B2dBodyComponent.class).body.getInertia()
                                    + " Speed: " + speed
                                    + " Health left: " + entity.getComponent(RocketComponent.class).health);
                        }
                        break;
                    case TypeComponent.PICKUP:
                        objectFactory.deleteBody(collidedEntity.getComponent(B2dBodyComponent.class).body);
                        engine.removeEntity(collidedEntity);
                        entity.getComponent(PickupComponent.class).count++;
                        break;
                    case TypeComponent.OTHER:
                        break;

                }
                cc.collisionEntity = null; // Collision handled reset component
            }
        }
    }
}
