package com.tfkfan.gui;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.math.Vector2D;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;


public class MainView extends View {
    private final GeometryFactory factory = new GeometryFactory();

    private Polygon first;
    private Coordinate viewPoint;
    private Coordinate playerPoint;
    private Coordinate mousePoint = new Coordinate();
    private double len = 0.4d;
    private double rentgenR = 0.1d;
    private double dx = 0.01d;
    private double dy = 0.01d;
    private double R = len;
    private double phi0 = Math.toRadians(40);
    private int N = 20;

    private int W = 87;
    private int A = 65;
    private int D = 68;
    private int S = 83;

    public MainView() {
        first = factory.createPolygon(new Coordinate[]{
                new Coordinate(-0.5, -0.5),
                new Coordinate(-0.5, 0.0),
                new Coordinate(0.0, 0.0),
                new Coordinate(0.0, -0.5),
                new Coordinate(-0.5, -0.5)
        });
        viewPoint = new Coordinate(0.5, 0.5);
        playerPoint = new Coordinate(-0.6,-0.3);
    }

    @Override
    public void onMouseMove(double x, double y) {
        mousePoint.x = x;
        mousePoint.y = y;
    }

    @Override
    public void onMouseClick(double x, double y) {
    }

    @Override
    public void onKeyboardInput(int code) {
        if(code == W)
            viewPoint.y += dy;
        else if(code == S)
            viewPoint.y -= dy;
        else if(code == A)
            viewPoint.x -= dx;
        else if(code == D)
            viewPoint.x += dx;
    }

    @Override
    protected void clearColor() {
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    @Override
    protected void partialDisplay() {
        Vector2D mouseVector = new Vector2D(mousePoint.x - viewPoint.x, mousePoint.y - viewPoint.y).normalize();
        Vector2D lineVector = new Vector2D(playerPoint.x - viewPoint.x, playerPoint.y - viewPoint.y);
        LineString line = factory.createLineString(new Coordinate[]{viewPoint, playerPoint});
        Geometry intersection = line.intersection(first);
        Coordinate iCoord = intersection.getCoordinate();

        glColor3f(1.0f, 0.0f, 0.0f);
        glBegin(GL_POLYGON);
        Arrays.stream(first.getCoordinates()).forEach(coordinate -> {
            glVertex2d(coordinate.x, coordinate.y);
        });
        glEnd();

        glColor3f(0.2f, 0.2f, 0.2f);
        glBegin(GL_LINE_STRIP);
        glVertex2d(viewPoint.x, viewPoint.y);
        for (int i = 0; i <= N; ++i) {
            double angle = mouseVector.angle() - phi0 / 2 + phi0 * (double) i / (double) N;
            glVertex2d(viewPoint.x + R * Math.cos(angle), viewPoint.y + R * Math.sin(angle));
        }
        glVertex2d(viewPoint.x, viewPoint.y);
        glEnd();

        glBegin(GL_LINE_STRIP);
        for (double phi = 0; phi <= 2*Math.PI; phi+=Math.PI/N)
            glVertex2d(viewPoint.x + rentgenR * Math.cos(phi), viewPoint.y + rentgenR * Math.sin(phi));
        glEnd();

        glBegin(GL_LINES);
        glVertex2d(viewPoint.x, viewPoint.y);
        if(iCoord != null)
            glVertex2d(iCoord.x, iCoord.y);
        else
            glVertex2d(playerPoint.x, playerPoint.y);
        glEnd();

        double angle = mouseVector.angleTo(lineVector);
        if(iCoord == null && playerPoint.distance(viewPoint) <= len && (angle < phi0/2 && angle > -phi0/2)
                || playerPoint.distance(viewPoint) < rentgenR) {
            glColor3f(0.0f, 1.0f, 0.0f);
            glPointSize(10);
            glBegin(GL_POINTS);
            glVertex2d(playerPoint.x, playerPoint.y);
            glEnd();
        }
    }
}
