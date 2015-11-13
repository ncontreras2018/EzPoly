package util;
import java.awt.geom.Point2D;


public class PrecisePoint extends Point2D {
	
	private double x, y;
	
	public PrecisePoint() {
		
	}
	
	public PrecisePoint(double x, double y) {
		setLocation(x, y);
	}
	
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}
	
	public int x() {
		return (int) Math.round(x);
	}

	public int y() {
		return (int) Math.round(y);
	}
	
	public void translate(double deltaX, double deltaY) {
		x += deltaX;
		y += deltaY;
	}
	
	@Override
	public String toString() {
		String toReturn = super.toString();
		
		toReturn += " (" + x + ", " + y + ")";
		
		return toReturn;
	}
}
