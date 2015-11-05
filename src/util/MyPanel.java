package util;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MyPanel extends JPanel {

	private JFrame frame;

	MyPoly p, p2;

	public MyPanel(int width, int height) {
		frame = new JFrame();

		frame.add(this);

		this.setPreferredSize(new Dimension(width, height));

		frame.pack();

		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		p = new MyPoly(100, 400, 3, 140);

		p2 = new MyPoly(1300, 350, 7, 205);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		p.translate(1, 0);

		p.rotateDegrees(3.5);

		p2.translate(-2, 0);

		p2.rotateDegrees(-2);

		g2d.setColor(Color.WHITE);

		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setColor(Color.RED);
		
		if (p.intersects(p2)) {
			g2d.setColor(Color.GREEN);
		}

		g2d.fill(p);

		g2d.fill(p2);
		
		g2d.setColor(Color.YELLOW);
		
		g2d.fillOval(p.getCenter().x() - 3, p.getCenter().y() - 3, 6, 6);
		
		g2d.fillOval(p2.getCenter().x() - 3, p2.getCenter().y() - 3, 6, 6);
	}
}
