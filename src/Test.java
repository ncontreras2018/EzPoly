import util.MyPanel;


public class Test extends Thread {

	MyPanel p;

	public static void main(String[] args) {
		new Test();
	}

	public Test() {

		p = new MyPanel(1400, 600);

		start();
	}

	@Override
	public void run() {
		while (true) {
			p.repaint();
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
