package org.minimumcosmic.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class CollisionComponent implements Component {
    public Entity collisionEntity;
    public float speed = 0.0f;
}
