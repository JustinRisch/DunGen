package dungen.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import dungen.pojos.Room;
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
	private static ArrayList<ImageIcon> NSdoorImages = new ArrayList<ImageIcon>();
	private static ArrayList<ImageIcon> EWdoorImages = new ArrayList<ImageIcon>();
	static {
		NSdoorImages.add(ResourceLoader.getImage("WoodenDoor.png"));
		EWdoorImages.add(ResourceLoader.getImage("WoodenDoor2.png"));
		NSdoorImages.add(ResourceLoader.getImage("StoneDoor.png"));
		EWdoorImages.add(ResourceLoader.getImage("StoneDoor2.png"));
		NSdoorImages.add(ResourceLoader.getImage("SDoor.png"));
		EWdoorImages.add(ResourceLoader.getImage("SDoor2.png"));
		NSdoorImages.add(ResourceLoader.getImage("Portcullis.png"));
		EWdoorImages.add(ResourceLoader.getImage("Portcullis.png"));
	}

	private static ImageIcon partyImage = ResourceLoader.getImage("Party.png");
	private static ImageIcon lock = ResourceLoader.getImage("Lock.png");
	private static ImageIcon lootImage = ResourceLoader.getImage("Loot.png");
	private static ImageIcon encounterImage = ResourceLoader
			.getImage("Encounter.png");
	private static ImageIcon otherPartyImage = ResourceLoader
			.getImage("npcs.png");
	public JLayeredPane contentPane = new JLayeredPane();
	private static Dimension imagesize = new Dimension(20, 20);

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

	public void redraw(boolean showSecrets) {
		contentPane.removeAll();
		roomsLocations = new ArrayList<>();
		rooms = new ArrayList<>();
		halls = new ArrayList<>();
		Controls.rooms.forEach((p, r) -> addRoom((int) p.getX(),
				(int) p.getY(), r, showSecrets));
		contentPane.add(star);
		contentPane.revalidate();
		contentPane.repaint();
	}

	public void addEventOnRoom(Point p, String encounter, boolean showSecrets) {
		JLabel encounterLbl = new JLabel();
		encounter = encounter.toLowerCase();
		if (encounter.contains("npc"))
			encounterLbl.setIcon(otherPartyImage);
		else if (encounter.contains("trap"))
			encounterLbl.setIcon(trapImage);
		else if (encounter.contains("hazard"))
			encounterLbl.setIcon(hazardImage);
		else if (encounter.contains("trick"))
			encounterLbl.setIcon(trickImage);

		else if (encounter.contains("ncounter") || encounter.contains("boss")) {
			if (encounter.contains("deadly") || encounter.contains("boss")) {
				encounterLbl.setIcon(deadlyImage);
			} else if (encounter.contains("hard")) {
				encounterLbl.setIcon(hardImage);
			} else {
				encounterLbl.setIcon(encounterImage);
			}
		} else if (encounter.contains("hoard") || encounter.contains("item")
				|| encounter.contains("treasure")) {
			encounterLbl.setIcon(lootImage);
		} else {
			return;
		}
		encounterLbl.setSize(imagesize);
		encounterLbl.setLocation(p);
		encounterLbl.setVisible(showSecrets);
		JLabel room = rooms.parallelStream()
				.filter(e -> e.getLocation().equals(p)).findFirst().get();
		MouseListener[] parentListeners = room.getMouseListeners();
		if (parentListeners.length > 0)
			encounterLbl
					.addMouseListener(parentListeners[parentListeners.length - 1]);
		contentPane.add(encounterLbl, contentPane.highestLayer());
		contentPane.repaint();
	}

	public void addEventOnRoom(int x, int y, String details) {
		addEventOnRoom(getPosition(x, y), details, Controls.showSecrets);
	}

	public void addRoom(Integer x, Integer y, Room r) {
		addRoom(x, y, r, Controls.showSecrets);
	}

	public void addRoom(Integer x, Integer y, Room r, boolean showSecret) {
		Point p = getPosition(x, y);
		if (roomsLocations != null && !roomsLocations.contains(p)) {
			JLabel room = new JLabel();
			room.setIcon(roomImage);
			room.setSize(imagesize);
			room.setLocation((int) p.getX(), (int) p.getY());
			room.setVisible(true);
			room.addMouseListener((PressListener) e -> {
				if (e.getButton() == MouseEvent.BUTTON1) {
					Controls.hideRoom();
					Controls.thisRoom = Controls.rooms.get(new Point(x, y));
					Controls.showX = x;
					Controls.showY = y;
					Controls.showRoom();
					this.moveStar(p);
				}
			});
			contentPane.add(room);
			if (!rooms.contains(room)) {
				rooms.add(room);
			}
			if (!roomsLocations.contains(p)) {
				roomsLocations.add(p);
			}
			if (!r.details.isEmpty())
				addEventOnRoom(p, r.details, showSecret);
			for (Entry<String, String> direction : r.getDoors())
				if (r.hasDoor(direction.getKey()))
					addHall(x, y, direction.getKey(), r, showSecret);
		}
		contentPane.repaint();

	}

	public void moveStar(int x, int y) {
		Point p = getPosition(x, y);
		moveStar(p);
	}

	public void moveStar(Point p) {
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

	public void addHall(Integer x, Integer y, final String direction, Room r,
			boolean showSecrets) {
		Point p = getPosition(x, y);
		String doorScription = r.getDoor(direction);
		final boolean isNorthSouth = direction.equalsIgnoreCase("north")
				|| direction.equalsIgnoreCase("south");
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

		if (isNorthSouth) {
			if (doorScription.contains("ecret")) {
				if (showSecrets)
					hall.setIcon(NSdoorImages.get(2));
			} else if (doorScription.contains("cullis"))
				hall.setIcon(NSdoorImages.get(3));
			else if (doorScription.contains("Stone")
					|| doorScription.contains("Iron"))
				hall.setIcon(NSdoorImages.get(1));
			else
				hall.setIcon(NSdoorImages.get(0));
		} else {
			if (doorScription.contains("ecret")) {
				if (showSecrets)
					hall.setIcon(EWdoorImages.get(2));
			} else if (doorScription.contains("cullis"))
				hall.setIcon(EWdoorImages.get(3));
			else if (doorScription.contains("Stone")
					|| doorScription.contains("Iron"))
				hall.setIcon(EWdoorImages.get(1));
			else
				hall.setIcon(EWdoorImages.get(0));
		}
		hall.setLocation(modX, modY);
		hall.setVisible(true);

		JLabel locked = new JLabel();
		locked.setIcon(lock);
		locked.setSize(imagesize);
		locked.setLocation(modX, modY);
		if (doorScription.contains("lock")
				&& (!doorScription.contains("ecret") || showSecrets)) {
			locked.setVisible(true);
		} else
			locked.setVisible(false);
		locked.addMouseListener((PressListener) e -> {
			if (e.getButton() == MouseEvent.BUTTON3) {
				locked.setVisible(false);
				r.setDoor(direction, doorScription.replaceAll("lock", "~"));
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				int i = 0;
				if (isNorthSouth) {
					i = NSdoorImages.indexOf(hall.getIcon()) + 1;
					i %= NSdoorImages.size();
					hall.setIcon(NSdoorImages.get(i));
				} else {
					i = EWdoorImages.indexOf(hall.getIcon()) + 1;
					i %= EWdoorImages.size();
					hall.setIcon(EWdoorImages.get(i));
				}
				StringBuilder description = new StringBuilder();
				if (i == 0) {
					description.append("wooden");
				} else if (i == 1) {
					description.append("stone or iron");
				} else if (i == 2) {
					description.append("secret");
				} else if (i == 3) {
					description.append("portcullis");
				}
				if (doorScription.contains("lock")
						&& (!doorScription.contains("ecret") || showSecrets)) {
					description.append("lock");
				}
				r.setDoor(direction, description.toString());
			}
		});

		hall.addMouseListener((PressListener) e -> {
			if (e.getButton() == MouseEvent.BUTTON3) {
				locked.setVisible(true);
				if (doorScription.contains("~"))
					r.setDoor(direction, doorScription.replaceAll("~", "lock"));
				else
					r.setDoor(direction, doorScription + " locked");
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				int i = 0;
				if (isNorthSouth) {
					i = NSdoorImages.indexOf(hall.getIcon()) + 1;
					i %= NSdoorImages.size();
					hall.setIcon(NSdoorImages.get(i));
				} else {
					i = EWdoorImages.indexOf(hall.getIcon()) + 1;
					i %= EWdoorImages.size();
					hall.setIcon(EWdoorImages.get(i));
				}
				StringBuilder description = new StringBuilder();
				if (i == 0) {
					description.append("wooden");
				} else if (i == 1) {
					description.append("stone or iron");
				} else if (i == 2) {
					description.append("secret");
				} else if (i == 3) {
					description.append("portcullis");
				}
				if (doorScription.contains("lock")
						&& (!doorScription.contains("ecret") || showSecrets)) {
					description.append("lock");
				}
				r.setDoor(direction, description.toString());
			}
		});
		contentPane.add(hall, contentPane.lowestLayer());
		contentPane.add(locked, contentPane.highestLayer());
		halls.add(locked);
		halls.add(hall);
		contentPane.repaint();
	}

	public Map() {
		setTitle("Dungeon Map");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(290, 50, 580, 580);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setBackground(Color.BLACK);
		star.setSize(imagesize);
		star.setIcon(partyImage);
	}

}

@FunctionalInterface
interface PressListener extends MouseListener {

	@Override
	default public void mouseClicked(MouseEvent e) {
	}

	@Override
	default public void mouseReleased(MouseEvent e) {
	}

	@Override
	default public void mouseEntered(MouseEvent e) {
	}

	@Override
	default public void mouseExited(MouseEvent e) {
	}

}