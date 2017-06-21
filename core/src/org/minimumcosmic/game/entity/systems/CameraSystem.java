package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.CameraComponent;
import org.minimumcosmic.game.entity.components.StateComponent;
import org.minimumcosmic.game.controller.KeyboardController;

public class CameraSystem extends IteratingSystem {

    ComponentMapper<CameraComponent> cm; // gets camera component of entity
    ComponentMapper<B2dBodyComponent> bodm; // gets body component

    @SuppressWarnings("unchecked")
    public CameraSystem() {
        // Set this system to process all entities with the CameraComponent
        super(Family.all(CameraComponent.class).get());

        // Create component mappers
        cm = ComponentMapper.getFor(CameraComponent.class);
        bodm = ComponentMapper.getFor(B2dBodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2Body = bodm.get(entity); // Get the entity's body
        CameraComponent player = cm.get(entity);

        player.camera.position.y = b2Body.body.getPosition().y;
    }

}
