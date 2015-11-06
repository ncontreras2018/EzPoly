package util;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("serial")
public class MyPoly extends Polygon {

	public static final short CIRCLE_LOW_DEF = 1, CIRCLE = 2,
			CIRCLE_HIGH_DEF = 3, CRECENT_MOON = 4, FIVE_POINT_STAR = 5;

	private MyPoint[] points;

	public MyPoly() {

	}

	public MyPoly(int x, int y, short PREDEFINED_SHAPE, double radius) {

		this();

		switch (PREDEFINED_SHAPE) {

		case (CIRCLE_LOW_DEF):
			setUpPoly(x, y, (int) radius / 4, radius);
			break;

		case (CIRCLE):
			setUpPoly(x, y, (int) radius, radius);
			break;

		case (CIRCLE_HIGH_DEF):
			setUpPoly(x, y, (int) radius * 4, radius);
			break;

		case (CRECENT_MOON):

			int moonSides = 100;

			setUpPoly(x, y, moonSides, radius);

			MyPoint center = getCenter();

			MyPoint[] newPoints = new MyPoint[moonSides];

			for (int i = 0; i < moonSides; i++) {

				MyPoint cur = points[i];

				if (cur.getY() > center.getY()) {

					double yDistToCenter = center.getY() - cur.getY();

					cur.translate(0, 1.5 * yDistToCenter);

					newPoints[i] = cur;
				}
				newPoints[i] = cur;
			}
			setPoints(newPoints);
			break;

		case (FIVE_POINT_STAR):
			setUpPoly(x, y, 10, radius);

			MyPoint[] starPoints = new MyPoint[npoints];

			MyPoint starCenter = getCenter();

			for (int i = 0; i < npoints; i++) {

				MyPoint curPoint = points[i];

				if (i % 2 == 0) {

					double xDist = curPoint.getX() - starCenter.getX();
					double yDist = curPoint.getY() - starCenter.getY();

					curPoint.translate(xDist, yDist);
				}
				starPoints[i] = curPoint;
			}
			setPoints(starPoints);
			break;
		}
	}

	public MyPoly(MyPoint[] points) {
		this.points = points;

		adjustSuperPoints();
	}

	public MyPoly(int sides, double radius) {
		this(0, 0, sides, radius);
	}

	public MyPoly(int xPos, int yPos, int sides, double radius) {
		setUpPoly(xPos, yPos, sides, radius);
	}

	private void setUpPoly(int x, int y, int sides, double radius) {
		points = new MyPoint[sides];

		double fullCircle = Math.PI * 2;

		double interval = fullCircle / sides;

		double curAngle = 0;

		for (int i = 0; i < sides; i++) {

			points[i] = new MyPoint(Math.cos(curAngle) * radius,
					Math.sin(curAngle) * radius);

			curAngle += interval;

		}

		adjustSuperPoints();

		translate(x, y);

		adjustSuperPoints();
	}

	public MyPoint getCenter() {

		MyPoint center = new MyPoint(0, 0);

		for (MyPoint p : points) {
			center.translate(p.getX(), p.getY());
		}

		center.setLocation(center.getX() / npoints, center.getY() / npoints);

		return center;
	}

	private MyPoint getCenter(Polygon p) {

		MyPoint[] otherPoints = getPoints(p);

		MyPoint center = new MyPoint(0, 0);

		for (MyPoint cur : otherPoints) {
			center.translate(cur.getX(), cur.getY());
		}

		center.setLocation(center.getX() / p.npoints, center.getY() / p.npoints);

		return center;
	}

	@Override
	public void translate(int deltaX, int deltaY) {

		for (int i = 0; i < npoints; i++) {
			points[i].translate(deltaX, deltaY);
		}

		adjustSuperPoints();

	}

	public void rotateDegrees(double angle) {
		rotateRadians(Math.toRadians(angle));
	}

	public void rotateRadians(double angle) {

		MyPoint center = getCenter();

		AffineTransform at = new AffineTransform();

		at.rotate(angle, center.getX(), center.getY());

		Point2D[] newPoints = new Point2D[npoints];

		at.transform(points, 0, newPoints, 0, npoints);

		for (int i = 0; i < npoints; i++) {
			Point2D cur = newPoints[i];

			points[i] = new MyPoint(cur.getX(), cur.getY());
		}

		adjustSuperPoints();

	}

	public void scale(double sx, double sy) {

		MyPoint center = getCenter();

		AffineTransform at = new AffineTransform();

		at.translate(center.getX(), center.getY());

		at.scale(sx, sy);

		at.translate(-center.getX(), -center.getY());

		Point2D[] newPoints = new Point2D[npoints];

		at.transform(points, 0, newPoints, 0, npoints);

		for (int i = 0; i < npoints; i++) {
			Point2D cur = newPoints[i];

			points[i] = new MyPoint(cur.getX(), cur.getY());
		}

		adjustSuperPoints();
	}

	private void adjustSuperPoints() {
		reset();

		npoints = points.length;

		xpoints = new int[npoints];
		ypoints = new int[npoints];

		for (int i = 0; i < npoints; i++) {
			xpoints[i] = points[i].x();
			ypoints[i] = points[i].y();

		}

		invalidate();

	}

	private MyPoint[] getPoints(Polygon p) {
		if (p instanceof MyPoly) {
			return ((MyPoly) p).getPoints();
		}

		MyPoint[] toReturn = new MyPoint[p.npoints];

		for (int i = 0; i < p.npoints; i++) {
			toReturn[i] = new MyPoint(p.xpoints[i], p.ypoints[i]);
		}
		return toReturn;
	}

	public MyPoint[] getPoints() {
		return points;
	}

	public void setPoints(MyPoint[] points) {
		this.points = points;

		adjustSuperPoints();
	}

	public boolean intersects(Polygon p) {

		if (intersectsSimple(p)) {
			return true;
		}

		return intersetsEdges(p);
	}

	@SuppressWarnings("unused")
	private boolean intersectsCenter(Polygon p) {

		MyPoint otherCenter = getCenter(p);

		return contains(otherCenter) || p.contains(getCenter());
	}

	private boolean intersetsEdges(Polygon p) {

		MyPoint[][] myEdges = new MyPoint[npoints][2];

		for (int i = 0; i < npoints; i++) {

			myEdges[i][0] = points[i];

			myEdges[i][1] = points[(i + 1) % npoints];

		}

		MyPoint[][] otherEdges = new MyPoint[p.npoints][2];

		MyPoint[] otherPoints = getPoints(p);

		for (int i = 0; i < p.npoints; i++) {

			otherEdges[i][0] = otherPoints[i];

			otherEdges[i][1] = otherPoints[(i + 1) % p.npoints];

		}

		for (int i = 0; i < myEdges.length; i++) {
			for (int j = 0; j < otherEdges.length; j++) {

				boolean intersects = Line2D.linesIntersect(
						myEdges[i][0].getX(), myEdges[i][0].getY(),
						myEdges[i][1].getX(), myEdges[i][1].getY(),
						otherEdges[j][0].getX(), otherEdges[j][0].getY(),
						otherEdges[j][1].getX(), otherEdges[j][1].getY());

				if (intersects) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private boolean intersectsRandom(Polygon p) {

		if (intersectsSimple(p)) {
			return true;
		}

		Rectangle2D bounding = getBounds2D();

		int area = (int) (bounding.getWidth() * bounding.getHeight());

		double largestRadius = 0;

		MyPoint center = getCenter();

		for (MyPoint curPoint : points) {
			if (center.distance(curPoint) > largestRadius) {
				largestRadius = center.distance(curPoint);
			}
		}

		for (int i = 0; i < area / 10; i++) {
			double ranX = center.getX() - largestRadius
					+ (Math.random() * 2 * largestRadius);
			double ranY = center.getY() - largestRadius
					+ (Math.random() * 2 * largestRadius);

			MyPoint ranPoint = new MyPoint(ranX, ranY);

			if (this.contains(ranPoint)) {
				if (p.contains(ranPoint)) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unused")
	private boolean intersectsComplex(Polygon p) {

		if (intersectsSimple(p)) {
			return true;
		}

		MyPoint center = getCenter();

		for (int i = 0; i < npoints; i++) {

			double xStep = points[i].getX() - center.getX();
			double yStep = points[i].getY() - center.getY();

			xStep /= center.distance(points[i]);
			yStep /= center.distance(points[i]);

			MyPoint testPoint = center;

			while (true) {

				testPoint.translate(xStep, yStep);

				if (this.contains(testPoint)) {

					if (p.contains(testPoint)) {
						return true;
					}
				} else {
					break;
				}
			}
		}

		return false;
	}

	private boolean intersectsSimple(Polygon p) {

		double[] cur = new double[2];

		PathIterator myPI = getPathIterator(null);

		myPI.currentSegment(cur);

		while (!myPI.isDone()) {

			MyPoint point = new MyPoint(cur[0], cur[1]);

			if (p.contains(point)) {
				return true;
			}

			myPI.next();

			myPI.currentSegment(cur);
		}

		PathIterator otherPI = p.getPathIterator(null);

		otherPI.currentSegment(cur);

		while (!otherPI.isDone()) {

			MyPoint point = new MyPoint(cur[0], cur[1]);

			if (this.contains(point)) {
				return true;
			}

			otherPI.next();

			otherPI.currentSegment(cur);
		}

		return false;

	}

	@Override
	public String toString() {
		String toReturn = super.toString();

		toReturn += " Points:[ ";

		for (MyPoint p : points) {
			toReturn += p.toString() + ", ";
		}

		toReturn = toReturn.substring(0, toReturn.length() - 2);

		toReturn += " ]";

		return toReturn;
	}
}
