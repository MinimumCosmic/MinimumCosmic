package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.PlayerComponent;
import org.minimumcosmic.game.entity.components.StateComponent;
import org.minimumcosmic.game.controller.KeyboardController;

public class PlayerControlSystem extends IteratingSystem {

    ComponentMapper<PlayerComponent> pm; // gets player component of entity
    ComponentMapper<B2dBodyComponent> bodm; // gets body component
    ComponentMapper<StateComponent> sm; //gets state component
    KeyboardController controller; // input controller


    @SuppressWarnings("unchecked")
    public PlayerControlSystem(KeyboardController keyboardController) {
        // Set this system to process all entities with the PlayerComponent
        super(Family.all(PlayerComponent.class).get());
        controller = keyboardController;

        // Create component mappers
        pm = ComponentMapper.getFor(PlayerComponent.class);
        bodm = ComponentMapper.getFor(B2dBodyComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2Body = bodm.get(entity); // Get the entity's body
        StateComponent state = sm.get(entity); // Get the state


        // If body is going down set it's state to falling
        if (b2Body.body.getLinearVelocity().y > 0) {
            state.set(StateComponent.STATE_FALLING);
        }

        // If body is stationary on y axis
        if (b2Body.body.getLinearVelocity().y == 0) {
            if (state.get() == StateComponent.STATE_FALLING) {
                state.set(StateComponent.STATE_NORMAL);
            }

            if (b2Body.body.getLinearVelocity().x != 0) {
                state.set(StateComponent.STATE_MOVING);
            }
        }


        // apply forces depending on controller input
        if (controller.left) {
            b2Body.body.setLinearVelocity(MathUtils.lerp(b2Body.body.getLinearVelocity().x, -5f, 0.2f),
                    b2Body.body.getLinearVelocity().y);
        }
        if (controller.right) {
            b2Body.body.setLinearVelocity(MathUtils.lerp(b2Body.body.getLinearVelocity().x, 5f, 0.2f),
                    b2Body.body.getLinearVelocity().y);
        }

        if (!controller.left && !controller.right) {
            b2Body.body.setLinearVelocity(MathUtils.lerp(b2Body.body.getLinearVelocity().x, 0, 0.1f),
                    b2Body.body.getLinearVelocity().y);
        }

        if (controller.up &&
                (state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_MOVING)) {
            //b2Body.body.applyForceToCenter(0, 3000,true);
            b2Body.body.applyLinearImpulse(0, 75f, b2Body.body.getWorldCenter().x,
                    b2Body.body.getWorldCenter().y, true);
            state.set(StateComponent.STATE_JUMPING);
        }

    }
}
