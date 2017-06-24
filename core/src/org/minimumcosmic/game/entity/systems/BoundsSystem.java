package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.BoundsComponent;

public class BoundsSystem extends IteratingSystem {
    private ComponentMapper<B2dBodyComponent> bodyMapper;
    private ComponentMapper<BoundsComponent> boundMapper;

    public BoundsSystem() {
        super(Family.all(B2dBodyComponent.class, BoundsComponent.class).get());

        bodyMapper = ComponentMapper.getFor(B2dBodyComponent.class);
        boundMapper = ComponentMapper.getFor(BoundsComponent.class);
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2dBody = bodyMapper.get(entity);
        BoundsComponent bounds = boundMapper.get(entity);

        float x = b2dBody.body.getPosition().x;

        if (x < 0.05f * RenderingSystem.WORLD_WIDTH) {
            float newX = 0.95f * RenderingSystem.WORLD_WIDTH;
            b2dBody.body.setTransform(new Vector2(newX, b2dBody.body.getPosition().y), b2dBody.body.getAngle());

        } else if (x > 0.95f * RenderingSystem.WORLD_WIDTH) {
            float newX = 0.05f * RenderingSystem.WORLD_WIDTH;
            b2dBody.body.setTransform(new Vector2(newX, b2dBody.body.getPosition().y), b2dBody.body.getAngle());
        }
    }
}
