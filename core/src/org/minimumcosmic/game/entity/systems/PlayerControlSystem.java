package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.PlayerComponent;
import org.minimumcosmic.game.entity.components.StateComponent;
import org.minimumcosmic.game.controller.KeyboardController;

public class PlayerControlSystem extends IteratingSystem {

    ComponentMapper<PlayerComponent> pm; // gets player component of entity
    ComponentMapper<B2dBodyComponent> bodm; // gets body component
    ComponentMapper<StateComponent> sm; //gets state component
    KeyboardController controller; // input controller

    int thumpTime = 0;
    boolean thumpSwitch = true;
    boolean thurstSwitch = false;
    boolean isThursing = false;
    int blink_delay = 0;
    int blink_time = 0;
    boolean isBlinking = true;


    private final static float ACCELERATION = 20.0f;
    private final static float ROTATION = 15.0f;
    private final static float START_DELAY = 400.0f;
    private final static float BLINK_DELAY = 25.0f;
    private final static float THUMP_DELAY = 65.0f;
    private final static float THURST_DELAY = 20.0f;

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

        if (controller.up) {
            isThursing = true;
            float velocityX = b2Body.body.getMass() * ACCELERATION * -MathUtils.sin(b2Body.body.getAngle());
            float velocityY = b2Body.body.getMass() * ACCELERATION * MathUtils.cos(b2Body.body.getAngle());
            b2Body.body.applyForceToCenter(new Vector2(velocityX, velocityY), true);
        }
        else {
            isThursing = false;
        }

        // Rotate CCW (left)
        if (controller.left) {
            b2Body.body.applyTorque(ROTATION * b2Body.body.getMass(), true);
        }

        // Rotate CW (right)
        if (controller.right) {
            b2Body.body.applyTorque(-ROTATION *  b2Body.body.getMass(), true);
        }

        /*// If body is going down set it's state to falling
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

        Vector2 velocity = new Vector2();
        float torque = 0;

        // apply forces depending on controller input
        if (controller.left) {
            //b2Body.body.setLinearVelocity(MathUtils.lerp(b2Body.body.getLinearVelocity().x, -5f, 0.2f),
            //        b2Body.body.getLinearVelocity().y);
            torque = 10 * b2Body.body.getMass();
        }
        if (controller.right) {
            //b2Body.body.setLinearVelocity(MathUtils.lerp(b2Body.body.getLinearVelocity().x, 5f, 0.2f),
             //       b2Body.body.getLinearVelocity().y);
            torque = -10 * b2Body.body.getMass();
        }

        if (!controller.left && !controller.right) {
            b2Body.body.setLinearVelocity(MathUtils.lerp(b2Body.body.getLinearVelocity().x, 0, 0.5f),
                    b2Body.body.getLinearVelocity().y);
            torque = 0;
        }

        if (!controller.down) {
            //b2Body.body.applyForceToCenter(0, 3000,true);
            //b2Body.body.setLinearVelocity(new Vector2(MathUtils.sin(b2Body.body.getTransform().getRotation()) * -15.0f,
            //        MathUtils.cos(b2Body.body.getTransform().getRotation()) * 15.0f));
            velocity.x = MathUtils.sin(b2Body.body.getAngle()) * -15.0f * b2Body.body.getMass();
            velocity.y = MathUtils.cos(b2Body.body.getAngle()) * 15.0f * b2Body.body.getMass();
            state.set(StateComponent.STATE_JUMPING);
        }


        b2Body.body.applyForceToCenter(velocity, true);
        b2Body.body.applyTorque(torque, true);*/
    }

}
