package dungen.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import dungen.generators.Dice;
import dungen.generators.Tables;
import dungen.resourceLoader.ResourceLoader;

public class Map extends JFrame {

	private static final long serialVersionUID = 3925862084304805067L;
	public ArrayList<Point> roomsLocations = new ArrayList<>();
	public ArrayList<JLabel> rooms = new ArrayList<>();
	public ArrayList<JLabel> halls = new ArrayList<>();

	public JLabel star = new JLabel();
	private static ImageIcon roomImage = ResourceLoader.getImage("Room.png");
	private static ImageIcon deadlyImage = ResourceLoader
			.getImage("Deadly.png");
	private static ImageIcon hardImage = ResourceLoader.getImage("Hard.png");
	private static ImageIcon trapImage = ResourceLoader.getImage("Trap.png");
	private static ImageIcon hazardImage = ResourceLoader
			.getImage("Hazard.png");
	private static ImageIcon trickImage = ResourceLoader.getImage("Trick.png");
	private static ImageIcon hallImage = ResourceLoader.getImage("Hall.png");
	private static ImageIcon secret = ResourceLoader.getImage("SHall.png");
	private static ImageIcon secret2 = ResourceLoader.getImage("SHall2.png");
	private static ImageIcon hallImage2 = ResourceLoader.getImage("Hall2.png");
	private static ImageIcon partyImage = ResourceLoader.getImage("Party.png");
	private static ImageIcon lock = ResourceLoader.getImage("Lock.png");
	private static ImageIcon encounterImage = ResourceLoader
			.getImage("Encounter.png");
	private static ImageIcon otherPartyImage = ResourceLoader
			.getImage("npcs.png");
	public JLayeredPane contentPane = new JLayeredPane();
	private static Dimension imagesize = new Dimension(20, 20);
	{
		this.setBackground(Color.BLACK);
		star.setSize(imagesize);
		star.setIcon(partyImage);
	}

	public void clearRoom(int x, int y) {
		final Point p = getPosition(x, y);
		List<JLabel> removers = Arrays.stream(contentPane.getComponents())
				.parallel().filter(e -> e.getLocation().equals(p))
				.filter(e -> e.getClass().equals(JLabel.class))
				.map(e -> (JLabel) e).filter(e -> !rooms.contains(e))
				.filter(e -> !e.getIcon().equals(partyImage))
				.collect(Collectors.toList());
		removers.forEach(getContentPane()::remove);
		// redraws the party icon
		moveStar(x, y);
	}

	public void redraw() {
		contentPane.removeAll();
		rooms.forEach(contentPane::add);
		halls.forEach(contentPane::add);
		Controls.rooms.forEach((p, r) -> {
			addEventOnRoom(getPosition((int) p.getX(), (int) p.getY()),
					r.details);
		});
		contentPane.add(star);
		contentPane.revalidate();
		contentPane.repaint();
	}

	public void addEventOnRoom(Point p, String encounter) {
		JLabel encounterLbl = new JLabel();
		if (encounter.contains("Trap"))
			encounterLbl.setIcon(trapImage);
		else if (encounter.contains("Hazard"))
			encounterLbl.setIcon(hazardImage);
		else if (encounter.contains("Trick"))
			encounterLbl.setIcon(trickImage);
		else if (encounter.contains("NPC"))
			encounterLbl.setIcon(otherPartyImage);
		else if (encounter.contains("Encounter")) {
			if (encounter.contains("Deadly")) {
				encounterLbl.setIcon(deadlyImage);
			} else if (encounter.contains("Hard")) {
				encounterLbl.setIcon(hardImage);
			} else {
				encounterLbl.setIcon(encounterImage);
			}
		} else {
			return;
		}
		encounterLbl.setSize(imagesize);
		encounterLbl.setLocation((int) p.getX(), (int) p.getY());
		encounterLbl.setVisible(true);
		contentPane.add(encounterLbl, contentPane.highestLayer());
		contentPane.repaint();
	}

	public void addEventOnRoom(int x, int y, String encounter) {
		addEventOnRoom(getPosition(x, y), encounter);
	}

	public void addRoom(Integer x, Integer y, String hasEncounter) {
		addRoom(getPosition(x, y), hasEncounter);
	}

	public void addRoom(Point p, String details) {
		if (roomsLocations != null && !roomsLocations.contains(p)) {
			JLabel room = new JLabel();
			room.setIcon(roomImage);
			room.setSize(imagesize);
			room.setLocation((int) p.getX(), (int) p.getY());
			room.setVisible(true);
			contentPane.add(room);
			rooms.add(room);
			roomsLocations.add(p);
			if (!details.isEmpty())
				addEventOnRoom(p, details);
		}
		contentPane.repaint();

	}

	public void moveStar(int x, int y) {
		Point p = getPosition(x, y);
		star.setLocation(p);
		star.setVisible(true);
		contentPane.add(star, contentPane.highestLayer());
		star.repaint();
	}

	public Point getPosition(int x, int y) {
		return new Point((this.getWidth() / 2) + x
				* ((int) imagesize.getWidth() * 2), (this.getHeight() - 45) - y
				* ((int) imagesize.getHeight() * 2));
	}

	private static List<String> doorTypes = ResourceLoader
			.getTable("Door Type.txt");

	public void addHall(Integer x, Integer y, String direction) {
		Point p = getPosition(x, y);

		int modX = 0, modY = 0;

		JLabel room = rooms.get(roomsLocations.indexOf(p));
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
			modX = room.getX() - room.getWidth();
			modY = room.getY();
		}

		final int testX = modX, testY = modY;
		if (halls.stream().map(e -> e.getLocation())
				.anyMatch(e -> e.getX() == testX && e.getY() == testY))
			return;
		JLabel hall = new JLabel();
		hall.setSize(imagesize);
		String doorScription = Tables.getResultFromTable(Dice.d20(), doorTypes);

		if (direction.equalsIgnoreCase("north")
				|| direction.equalsIgnoreCase("south")) {
			if (doorScription.contains("ecret"))
				hall.setIcon(secret);
			else
				hall.setIcon(hallImage);
		} else {
			if (doorScription.contains("ecret"))
				hall.setIcon(secret2);
			else
				hall.setIcon(hallImage2);
		}

		hall.setLocation(modX, modY);
		hall.setVisible(true);
		contentPane.add(hall, contentPane.lowestLayer());
		if (doorScription.contains("lock")) {
			JLabel locked = new JLabel();
			locked.setIcon(lock);
			locked.setSize(imagesize);
			locked.setLocation(modX, modY);
			locked.setVisible(true);
			contentPane.add(locked, contentPane.highestLayer());
			halls.add(locked);
		}
		halls.add(hall);

		contentPane.repaint();
	}

	public Map() {
		setTitle("Dungeon Map");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(271, 50, 580, 580);
		setContentPane(contentPane);
		contentPane.setLayout(null);

	}

}
