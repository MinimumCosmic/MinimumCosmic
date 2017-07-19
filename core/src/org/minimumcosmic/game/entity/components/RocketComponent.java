package org.minimumcosmic.game.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class RocketComponent implements Component {
    public Entity headModule;
    public Entity bodyModule;
    public Entity finsModule;
    public Entity engineModule;
    public int health;
    public int state = 0;
}
