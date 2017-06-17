package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.TransformComponent;

public class PhysicsSystem extends IteratingSystem {
    // Speed stabilizing variables
    private static final float MAX_STEP_TIME = 1 / 60f;
    private static float accumulator = 0f;

    private World world;
    private Array<Entity> bodiesQueue;

    private ComponentMapper<B2dBodyComponent> bm = ComponentMapper.getFor(B2dBodyComponent.class);
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);

    @SuppressWarnings("unchecked")
    public PhysicsSystem(World world) {
        super(Family.all(B2dBodyComponent.class, TransformComponent.class).get());
        this.world = world;
        this.bodiesQueue = new Array<Entity>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if (accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;

            // Loop through all Entities and update their components
            for (Entity entity : bodiesQueue) {
                TransformComponent transComponent = tm.get(entity);
                B2dBodyComponent bodyComponent = bm.get(entity);

                Vector2 position = bodyComponent.body.getPosition();

                transComponent.position.x = position.x;
                transComponent.position.y = position.y;
                transComponent.rotation = bodyComponent.body.getAngle() * MathUtils.radiansToDegrees;
            }
        }
        // empty queue
        bodiesQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }

}
