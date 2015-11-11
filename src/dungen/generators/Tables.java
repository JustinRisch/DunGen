package dungen.generators;

import java.util.HashMap;

public class Tables {
	private static HashMap<Integer, String> alignments = new HashMap<>();
	static {
		alignments.put(1, "LN");
		alignments.put(2, "LN");
		alignments.put(3, "NG");
		alignments.put(4, "NG");
		alignments.put(5, "CG");
		alignments.put(6, "CG");
		alignments.put(7, "LN");
		alignments.put(8, "LN");
		alignments.put(9, "LN");
		alignments.put(10, "NN");
		alignments.put(11, "NN");
		alignments.put(12, "CN");
		alignments.put(13, "LE");
		alignments.put(14, "LE");
		alignments.put(15, "LE");
		alignments.put(16, "NE");
		alignments.put(17, "NE");
		alignments.put(18, "NE");
		alignments.put(19, "CE");
		alignments.put(20, "CE");
	}
	public static HashMap<Integer, String> classes = new HashMap<Integer, String>();
	static {
		classes.put(1, "Barbarian");
		classes.put(2, "Bard");
		classes.put(3, "Cleric");
		classes.put(4, "Cleric");
		classes.put(5, "Druid");
		classes.put(6, "Fighter");
		classes.put(7, "Fighter");
		classes.put(8, "Monk");
		classes.put(9, "Paladin");
		classes.put(10, "Ranger");
		classes.put(11, "Rogue");
		classes.put(12, "Rogue");
		classes.put(13, "Rogue");
		classes.put(14, "Rogue");
		classes.put(15, "Sorceror");
		classes.put(16, "Warlock");
		classes.put(17, "Wizard");
		classes.put(18, "Wizard");
		classes.put(19, "Wizard");
		classes.put(20, "Wizard");
	}

	public static String getNpcClass() {
		return classes.get(Dice.d20());
	}

	public static String getAlignment() {
		return alignments.get(Dice.d20());
	}

	private static final String[] traps = { "Spiked pit", "Wall Scythe",
			"bear traps", "Magic trap" };
	private static final String[] conditions = { "Hidden", "Poisoned",
			"Magically hidden", "Disabled" };

	public static String getTrap() {
		String trap = "";
		int trapInt = Dice.custom(traps.length) - 1, conditionInt = Dice
				.custom(conditions.length) - 1;
		trap = conditions[conditionInt] + " " + traps[trapInt];
		return trap;
	}

	public static String getEncounter() {
		String encounter = "";
		int result = Dice.custom(100);
		if (result > 60) { // 30% of rooms will have an encounter
			result = Dice.custom(10); // roll for type of encounter
			if (result > 9)
				encounter = "Overpowering or Boss";
			else if (result > 7)
				encounter = "Challenging encounter";
			else if (result >= 4)
				encounter = "Moderate Encounter";
			else
				encounter = "Trap!\n" + getTrap();
		}
		return encounter;
	}
}
