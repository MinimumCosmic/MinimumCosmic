package org.minimumcosmic.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.Iterator;

public class BodyFactory {
    public static final int STEEL = 0;
    public static final int WOOD = 1;
    public static final int RUBBER = 2;
    public static final int STONE = 3;

    private static BodyFactory instance;
    public World world;
    private final float DEGTORAD = 0.0174533f;
    private ArrayList<Body> bodies;

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getInstance(World world) {
        if (instance == null) {
            instance = new BodyFactory(world);
            instance.bodies = new ArrayList<Body>();
        }
        return instance;
    }

    public void deleteAllBodies() {
        for (Body body : bodies) {
            world.destroyBody(body);
        }
        bodies.clear();
    }

    public void deleteBody(Body body) {
        Iterator<Body> itr = bodies.iterator();
        while (itr.hasNext()) {
            Body curBody = itr.next();
            if (curBody == body) {
                itr.remove();
                world.destroyBody(body);
                return;
            }
        }
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


        bodies.add(body);
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


        bodies.add(body);
        return body;
    }

    public Body makeTriangleBody(float x, float y, float size, int material, BodyDef.BodyType bodyType,  boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        bodyDef.fixedRotation = fixedRotation;

        Body body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        Vector2 []vertices = new Vector2[3];
        vertices[0] = new Vector2(-1 * size, -1 * size);
        vertices[1] = new Vector2(0, 1  * size);
        vertices[2] = new Vector2(1 * size , -1 * size);
        polygonShape.set(vertices);
        body.createFixture(makeFixture(material, polygonShape));
        polygonShape.dispose();


        bodies.add(body);
        return body;
    }

    public Body makePolygonBody(float x, float y, Vector2 []vertices, int material, BodyDef.BodyType bodyType,  boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = x;
        bodyDef.position.y = y;
        bodyDef.fixedRotation = fixedRotation;

        Body body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        body.createFixture(makeFixture(material, polygonShape));
        polygonShape.dispose();

        bodies.add(body);
        return body;
    }

    public Body makeSensorBody(float x, float y, float radius, BodyDef.BodyType bodyType, boolean fixedRotation) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        this.makeSensorFixture(boxBody, radius);

        bodies.add(boxBody);
        return boxBody;
    }

    public void makeSensorFixture(Body body, float size){
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(size);
        fixtureDef.shape = circleShape;
        body.createFixture(fixtureDef);
        circleShape.dispose();

    }

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
