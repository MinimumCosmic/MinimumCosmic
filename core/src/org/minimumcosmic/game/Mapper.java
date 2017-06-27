package org.minimumcosmic.game;

import com.badlogic.ashley.core.ComponentMapper;

import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.TransformComponent;

/**
 * Created by Kostin on 27.06.2017.
 */

public class Mapper {
    public static final ComponentMapper<RocketComponent> rocketComponentMapper = ComponentMapper.getFor(RocketComponent.class);
    public static final ComponentMapper<TransformComponent> transformComponentMapper = ComponentMapper.getFor(TransformComponent.class);
}
