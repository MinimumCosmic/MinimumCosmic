package org.minimumcosmic.game;

import com.badlogic.gdx.physics.box2d.*;

public class BodyFactory {
    public static final int STEEL = 0;
    public static final int WOOD = 1;
    public static final int RUBBER = 2;
    public static final int STONE = 3;

    private static BodyFactory instance;
    private World world;
    private final float DEGTORAD = 0.0174533f;

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getInstance(World world) {
        if (instance == null) {
            instance = new BodyFactory(world);
        }
        return instance;
    }

    public Body makeBoxBody(float x, float y, float width, float height, int material, BodyDef.BodyType bodyType) {
        return makeBoxBody(x, y, width, height, material, bodyType, false);
    }

    public Body makeBoxBody(float x, float y, float width, float height, int material, BodyDef.BodyType bodyType, boolean fixedRotation) {
        // Create body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        bodyDef.fixedRotation = fixedRotation;

        // Create actual body

        Body body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        body.createFixture(makeFixture(material, polygonShape));
        polygonShape.dispose();

        return body;
    }

    public Body makeCircleBody(float x, float y, float radius, int material) {
        return makeCircleBody( x,  y,  radius,  material,  BodyDef.BodyType.DynamicBody,  false);
    }

    public Body makeCircleBody(float x, float y, float radius, int material, BodyDef.BodyType bodyType) {
        return makeCircleBody( x,  y,  radius,  material,  bodyType,  false);
    }

    public Body makeCircleBody(float x, float y, float radius, int material, BodyDef.BodyType bodyType, boolean fixedRotation) {
        // Create body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        bodyDef.fixedRotation = fixedRotation;

        // Create actual body

        Body body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(makeFixture(material, circleShape));
        circleShape.dispose();

        return body;
    }

    // TODO::ADD SENSOR BODIES

    static public FixtureDef makeFixture(int material, Shape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        switch (material) {
            case STEEL:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.3f;
                fixtureDef.restitution = 0.1f;
                break;
            case WOOD:
                fixtureDef.density = 0.5f;
                fixtureDef.friction = 0.7f;
                fixtureDef.restitution = 0.3f;
                break;
            case RUBBER:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 1f;
                break;
            case STONE:
                fixtureDef.density = 10f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0f;
                break;
            default:
                fixtureDef.density = 7f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0.3f;
                break;
        }

        return fixtureDef;
    }
}
