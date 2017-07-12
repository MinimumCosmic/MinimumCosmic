package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.EnemyComponent;


public class EnemySystem extends IteratingSystem {
    private ComponentMapper<B2dBodyComponent> bodyMapper;

    public EnemySystem() {
        super(Family.all(B2dBodyComponent.class, EnemyComponent.class).get());
        bodyMapper = ComponentMapper.getFor(B2dBodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2dBody = bodyMapper.get(entity);

        b2dBody.body.setTransform(new Vector2(b2dBody.body.getPosition().x + 4f * deltaTime
                , b2dBody.body.getPosition().y), b2dBody.body.getAngle());
    }
}
