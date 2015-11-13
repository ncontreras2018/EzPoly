package util;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("serial")
public class EzPoly extends Polygon {

	public static final short CIRCLE_LOW_DEF = 1, CIRCLE = 2,
			CIRCLE_HIGH_DEF = 3, CRECENT_MOON = 4, FIVE_POINT_STAR = 5;

	private PrecisePoint[] points;

	private double angle;

	public EzPoly() {

	}

	public EzPoly(int x, int y, short PREDEFINED_SHAPE, double radius) {

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

			PrecisePoint center = getCenter();

			PrecisePoint[] newPoints = new PrecisePoint[moonSides];

			for (int i = 0; i < moonSides; i++) {

				PrecisePoint cur = points[i];

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

			PrecisePoint[] starPoints = new PrecisePoint[npoints];

			PrecisePoint starCenter = getCenter();

			for (int i = 0; i < npoints; i++) {

				PrecisePoint curPoint = points[i];

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

	public EzPoly(PrecisePoint[] points) {
		this.points = points;

		adjustSuperPoints();
	}

	public EzPoly(int sides, double radius) {
		this(0, 0, sides, radius);
	}

	public EzPoly(int xPos, int yPos, int sides, double radius) {
		setUpPoly(xPos, yPos, sides, radius);
	}

	private void setUpPoly(int x, int y, int sides, double radius) {
		points = new PrecisePoint[sides];

		double fullCircle = Math.PI * 2;

		double interval = fullCircle / sides;

		double curAngle = 0;

		for (int i = 0; i < sides; i++) {

			points[i] = new PrecisePoint(Math.cos(curAngle) * radius,
					Math.sin(curAngle) * radius);

			curAngle += interval;

		}

		adjustSuperPoints();

		translate(x, y);

		adjustSuperPoints();
	}

	public PrecisePoint getCenter() {

		PrecisePoint center = new PrecisePoint(0, 0);

		for (PrecisePoint p : points) {
			center.translate(p.getX(), p.getY());
		}

		center.setLocation(center.getX() / npoints, center.getY() / npoints);

		return center;
	}
	
	public double getAngle() {
		return angle;
	}

	private PrecisePoint getCenter(Polygon p) {

		PrecisePoint[] otherPoints = getPoints(p);

		PrecisePoint center = new PrecisePoint(0, 0);

		for (PrecisePoint cur : otherPoints) {
			center.translate(cur.getX(), cur.getY());
		}

		center.setLocation(center.getX() / p.npoints, center.getY() / p.npoints);

		return center;
	}

	public void moveTo(double x, double y) {

		PrecisePoint loc = getCenter();

		translate(x - loc.getX(), y - loc.getY());
	}

	@Override
	public void translate(int deltaX, int deltaY) {
		translate(deltaX + 0.0, deltaY + 0.0);
	}

	public void translate(double deltaX, double deltaY) {

		for (int i = 0; i < npoints; i++) {
			points[i].translate(deltaX, deltaY);
		}

		adjustSuperPoints();

	}

	public void setAngle(double angle) {

		rotateRadians(-this.angle + angle);
		
		this.angle = angle;

	}

	public void rotateDegrees(double angle) {
		rotateRadians(Math.toRadians(angle));
	}

	public void rotateRadians(double angle) {

		PrecisePoint center = getCenter();

		AffineTransform at = new AffineTransform();

		at.rotate(angle, center.getX(), center.getY());

		Point2D[] newPoints = new Point2D[npoints];

		at.transform(points, 0, newPoints, 0, npoints);

		for (int i = 0; i < npoints; i++) {
			Point2D cur = newPoints[i];

			points[i] = new PrecisePoint(cur.getX(), cur.getY());
		}

		adjustSuperPoints();

		angle += angle;

	}

	public void scale(double sx, double sy) {

		PrecisePoint center = getCenter();

		AffineTransform at = new AffineTransform();

		at.translate(center.getX(), center.getY());

		at.scale(sx, sy);

		at.translate(-center.getX(), -center.getY());

		Point2D[] newPoints = new Point2D[npoints];

		at.transform(points, 0, newPoints, 0, npoints);

		for (int i = 0; i < npoints; i++) {
			Point2D cur = newPoints[i];

			points[i] = new PrecisePoint(cur.getX(), cur.getY());
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

	private PrecisePoint[] getPoints(Polygon p) {
		if (p instanceof EzPoly) {
			return ((EzPoly) p).getPoints();
		}

		PrecisePoint[] toReturn = new PrecisePoint[p.npoints];

		for (int i = 0; i < p.npoints; i++) {
			toReturn[i] = new PrecisePoint(p.xpoints[i], p.ypoints[i]);
		}
		return toReturn;
	}

	public PrecisePoint[] getPoints() {
		return points;
	}

	public void setPoints(PrecisePoint[] points) {
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

		PrecisePoint otherCenter = getCenter(p);

		return contains(otherCenter) || p.contains(getCenter());
	}

	private boolean intersetsEdges(Polygon p) {

		PrecisePoint[][] myEdges = new PrecisePoint[npoints][2];

		for (int i = 0; i < npoints; i++) {

			myEdges[i][0] = points[i];

			myEdges[i][1] = points[(i + 1) % npoints];

		}

		PrecisePoint[][] otherEdges = new PrecisePoint[p.npoints][2];

		PrecisePoint[] otherPoints = getPoints(p);

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

		PrecisePoint center = getCenter();

		for (PrecisePoint curPoint : points) {
			if (center.distance(curPoint) > largestRadius) {
				largestRadius = center.distance(curPoint);
			}
		}

		for (int i = 0; i < area / 10; i++) {
			double ranX = center.getX() - largestRadius
					+ (Math.random() * 2 * largestRadius);
			double ranY = center.getY() - largestRadius
					+ (Math.random() * 2 * largestRadius);

			PrecisePoint ranPoint = new PrecisePoint(ranX, ranY);

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

		PrecisePoint center = getCenter();

		for (int i = 0; i < npoints; i++) {

			double xStep = points[i].getX() - center.getX();
			double yStep = points[i].getY() - center.getY();

			xStep /= center.distance(points[i]);
			yStep /= center.distance(points[i]);

			PrecisePoint testPoint = center;

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

			PrecisePoint point = new PrecisePoint(cur[0], cur[1]);

			if (p.contains(point)) {
				return true;
			}

			myPI.next();

			myPI.currentSegment(cur);
		}

		PathIterator otherPI = p.getPathIterator(null);

		otherPI.currentSegment(cur);

		while (!otherPI.isDone()) {

			PrecisePoint point = new PrecisePoint(cur[0], cur[1]);

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

		for (PrecisePoint p : points) {
			toReturn += p.toString() + ", ";
		}

		toReturn = toReturn.substring(0, toReturn.length() - 2);

		toReturn += " ]";

		return toReturn;
	}
}
