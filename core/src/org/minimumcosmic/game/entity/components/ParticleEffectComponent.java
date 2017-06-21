package org.minimumcosmic.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class ParticleEffectComponent implements Component {
    public ParticleEffect particleEffect = null;
    public boolean isVisible = true;
}
