package dungen.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class Map extends JFrame {

	private static final long serialVersionUID = 3925862084304805067L;
	public ArrayList<Point> roomsLocations = new ArrayList<>();
	public ArrayList<JLabel> rooms = new ArrayList<>();
	public ArrayList<JLabel> halls = new ArrayList<>();
	public JLabel star = new JLabel("*");
	private transient final static ImageIcon roomImage = new ImageIcon(
			"Resources/Room.png");
	private transient final static ImageIcon hallImage = new ImageIcon(
			"Resources/Hall.png");
	private transient final static ImageIcon hallImage2 = new ImageIcon(
			"Resources/Hall2.png");
	public transient JLayeredPane contentPane = new JLayeredPane();
	private transient static final Font font = new Font(Font.MONOSPACED, 0, 9);
	{
		this.setBackground(Color.BLACK);
		star.setSize(5, 5);
		star.setFont(font);
	}

	public void redraw() {
		contentPane.removeAll();
		rooms.forEach(contentPane::add);
		halls.forEach(contentPane::add);
		contentPane.add(star);
		contentPane.revalidate();
		contentPane.repaint();
	}

	public void addRoom(Integer x, Integer y) {
		Point p = new Point((contentPane.getWidth() / 2) + x * 30,
				this.getHeight() / 2 - y * 30);
		if (roomsLocations != null && !roomsLocations.contains(p)) {
			JLabel room = new JLabel();
			room.setIcon(roomImage);
			room.setSize(15, 15);
			room.setLocation((int) p.getX(), (int) p.getY());
			room.setFont(font);
			room.setVisible(true);
			contentPane.add(room);
			rooms.add(room);
			roomsLocations.add(p);
		}
		contentPane.repaint();

	}

	public void moveStar(int x, int y) {
		Point p = new Point((this.getWidth() / 2) + (x * 30) + 5,
				(this.getHeight() / 2) - (y * 30) + 5);
		star.setLocation(p);
		star.setVisible(true);
		contentPane.add(star, contentPane.highestLayer());
		star.repaint();
	}

	public void addHall(Integer x, Integer y, String direction) {
		Point p = new Point((this.getWidth() / 2) + x * 30,
				(this.getHeight() / 2) - y * 30);
		int modX = 0, modY = 0;
		JLabel room = rooms.get(roomsLocations.indexOf(p));
		JLabel hall = new JLabel();
		if (direction.equalsIgnoreCase("north")
				|| direction.equalsIgnoreCase("south")) {
			hall.setIcon(hallImage);
		} else {
			hall.setIcon(hallImage2);
		}
		hall.setFont(font);
		hall.setSize(15, 15);
		if (direction.equalsIgnoreCase("north")) {
			modX = room.getX();
			modY = room.getY() - room.getHeight();
		} else if (direction.equalsIgnoreCase("south")) {
			modX = room.getX();
			modY = room.getY() + room.getHeight();
		} else if (direction.equalsIgnoreCase("east")) {
			modX = room.getX() + room.getWidth();
			modY = room.getY();
		} else if (direction.equalsIgnoreCase("west")) {
			modX = room.getX() - hall.getWidth();
			modY = room.getY();
		}
		hall.setLocation(modX, modY);

		hall.setVisible(true);
		contentPane.add(hall);
		halls.add(hall);
		contentPane.repaint();
	}

	public Map() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 450);
		setContentPane(contentPane);
		contentPane.setLayout(null);
	}

}
