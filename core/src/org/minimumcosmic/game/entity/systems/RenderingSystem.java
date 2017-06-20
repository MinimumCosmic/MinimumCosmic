package org.minimumcosmic.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import org.minimumcosmic.game.entity.components.TextureComponent;
import org.minimumcosmic.game.entity.components.TransformComponent;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {
    static final float PIXELS_PER_METER = 16.0f;

    public static final float WORLD_WIDTH = Gdx.graphics.getWidth() / PIXELS_PER_METER;
    public static final float WORLD_HEIGHT = Gdx.graphics.getHeight() / PIXELS_PER_METER;

    public static final float PIXELS_TO_METRES = 1.0f / PIXELS_PER_METER;

    private SpriteBatch spriteBatch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;
    private OrthographicCamera camera;

    private ComponentMapper<TextureComponent> cmTexture;
    private ComponentMapper<TransformComponent> cmTransform;

    public static float pixelsToMeters(float pixelValue) {
        return pixelValue * PIXELS_TO_METRES;
    }

    @SuppressWarnings("unchecked")
    public RenderingSystem(SpriteBatch spriteBatch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        this.spriteBatch = spriteBatch;
        cmTexture = ComponentMapper.getFor(TextureComponent.class);
        cmTransform = ComponentMapper.getFor(TransformComponent.class);
        renderQueue = new Array<Entity>();

        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        renderQueue.sort(new ZComparator());

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.enableBlending();
        spriteBatch.begin();

        for (Entity entity : renderQueue) {
            TextureComponent textureComponent = cmTexture.get(entity);
            TransformComponent transformComponent = cmTransform.get(entity);

            if (textureComponent.region == null || transformComponent.isHidden) {
                continue;
            }

            float width = textureComponent.region.getRegionWidth();
            float height = textureComponent.region.getRegionHeight();

            float originX = width / 2f;
            float originY = height / 2f;

            spriteBatch.draw(textureComponent.region,
                    transformComponent.position.x - originX,
                    transformComponent.position.y - originY,
                    originX, originY, width, height,
                    pixelsToMeters(transformComponent.scale.x),
                    pixelsToMeters(transformComponent.scale.y),
                    transformComponent.rotation);

        }

        spriteBatch.end();
        renderQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
