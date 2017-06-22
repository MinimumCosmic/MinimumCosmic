package org.minimumcosmic.game;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import org.minimumcosmic.game.entity.components.CollisionComponent;

public class B2dContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        // Get fixtures
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        // check if either fixture has an Entity object stored in the body's userData
        if (fa.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fa.getBody().getUserData();
            entityCollision(ent, fb);
            return;
        } else if (fb.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fb.getBody().getUserData();
            entityCollision(ent, fa);
            return;
        }
    }

    private void entityCollision(Entity ent, Fixture fb) {
        // check the collided Entity is also an Entity
        if (fb.getBody().getUserData() instanceof Entity) {
            Entity colEnt = (Entity) fb.getBody().getUserData();
            // get the components for this entity
            CollisionComponent col = ent.getComponent(CollisionComponent.class);
            CollisionComponent colb = colEnt.getComponent(CollisionComponent.class);

            // set the CollisionEntity of the component
            if (col != null) {
                col.collisionEntity = colEnt;
            } else if (colb != null) {
                colb.collisionEntity = ent;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
