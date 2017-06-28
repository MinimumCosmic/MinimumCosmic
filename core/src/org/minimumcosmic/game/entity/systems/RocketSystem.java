package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.components.*;
import org.minimumcosmic.game.entity.components.modules.*;

public class RocketSystem extends IteratingSystem {
    ComponentMapper<B2dBodyComponent> bodm; // gets body component
    ComponentMapper<RocketComponent> rockm; // gets rocket component
    ComponentMapper<TransformComponent> tm;

    ComponentMapper<BodyModuleComponent> bmm;
    ComponentMapper<EngineModuleComponent> emm;
    ComponentMapper<FinsModuleComponent> fmm;
    ComponentMapper<ParticleEffectComponent> pem;

    KeyboardController controller; // input controller

    private final static float ROTATION = 5.0f;
    private final static float START_DELAY = 400.0f;
    private final static float BLINK_DELAY = 25.0f;
    private final static float THUMP_DELAY = 65.0f;
    private final static float THURST_DELAY = 20.0f;

    @SuppressWarnings("unchecked")
    public RocketSystem(KeyboardController keyboardController) {
        // Set this system to process all entities with the CameraComponent
        super(Family.all(RocketComponent.class).get());
        controller = keyboardController;

        // Create component mappers
        bodm = ComponentMapper.getFor(B2dBodyComponent.class);
        rockm = ComponentMapper.getFor(RocketComponent.class);
        tm = ComponentMapper.getFor(TransformComponent.class);
        bmm = ComponentMapper.getFor(BodyModuleComponent.class);
        emm = ComponentMapper.getFor(EngineModuleComponent.class);
        fmm = ComponentMapper.getFor(FinsModuleComponent.class);
        pem = ComponentMapper.getFor(ParticleEffectComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2Body = bodm.get(entity); // Get the entity's body
        RocketComponent rocket = rockm.get(entity);
        ParticleEffectComponent smoke = pem.get(entity);

        TransformComponent posHead = tm.get(rocket.headModule);
        TransformComponent posBody = tm.get(rocket.bodyModule);
        TransformComponent posFins = tm.get(rocket.finsModule);
        TransformComponent posEngine = tm.get(rocket.engineModule);

        BodyModuleComponent bodyModule = bmm.get(rocket.bodyModule);
        EngineModuleComponent engineModule = emm.get(rocket.engineModule);
        FinsModuleComponent finsModule = fmm.get(rocket.finsModule);


        if ((controller.up || controller.isMouse1Down) && bodyModule.fuel > 0) {
            smoke.particleEffect.start();
            bodyModule.fuel -= 0.25 * deltaTime;
            float velocityX = b2Body.body.getMass() * (bodyModule.power + engineModule.power) * -MathUtils.sin(b2Body.body.getAngle());
            float velocityY = b2Body.body.getMass() * (bodyModule.power + engineModule.power) * MathUtils.cos(b2Body.body.getAngle());
            b2Body.body.applyForceToCenter(new Vector2(velocityX, velocityY), true);
        } else {

            if (bodyModule.fuel <= 0) {
                smoke.isVisible = false;
            } else {
                smoke.particleEffect.reset();
            }

            b2Body.body.setLinearVelocity(MathUtils.lerp(b2Body.body.getLinearVelocity().x, 0, 0.1f),
                    MathUtils.lerp(b2Body.body.getLinearVelocity().y, -125, 0.01f));
        }

        // Rotate CCW (left)
        if (controller.left) {
            b2Body.body.applyTorque(finsModule.maneuver * b2Body.body.getMass(), true);
        }
        if (Gdx.input.getAccelerometerX() != 0) {
            b2Body.body.applyTorque(Gdx.input.getAccelerometerX() * finsModule.maneuver * b2Body.body.getMass(), true);
        }

        // Rotate CW (right)
        if (controller.right) {
            b2Body.body.applyTorque(-finsModule.maneuver * b2Body.body.getMass(), true);
        }

        if (!controller.left && !controller.right) {
            b2Body.body.setAngularVelocity(MathUtils.lerp(b2Body.body.getAngularVelocity(), 0, 0.1f));
        }

        Vector2 bodyPosition = new Vector2(b2Body.body.getPosition().x, b2Body.body.getPosition().y);
        float bodyRotation = b2Body.body.getAngle();

        posHead.position.set(bodyPosition, 0);
        posBody.position.set(bodyPosition, 0);
        posFins.position.set(bodyPosition, 1);
        posEngine.position.set(bodyPosition, 2);
        smoke.particleEffect.setPosition(bodyPosition.x, bodyPosition.y - 2.5f);
        posEngine.rotation = posFins.rotation = posBody.rotation = posHead.rotation = bodyRotation;
    }
}
