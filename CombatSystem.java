import java.util.*;

/**
 * Handles combat resolution between units.
 * Combat occurs when enemy units are adjacent and at least one is an assassin.
 */
public class CombatSystem {

    /**
     * Processes all possible combats on the map.
     * Called once per game tick after unit movements.
     * @param gameMap Current game map
     */
    public static void processCombats(GameMap gameMap) {
        List<Unit> allUnits = gameMap.getUnits();
        Set<Unit> unitsInCombat = new HashSet<>();

        // Find all combat groups
        List<List<Unit>> combatGroups = findCombatGroups(allUnits);

        // Resolve each combat group
        for (List<Unit> combatGroup : combatGroups) {
            if (combatGroup.size() > 1) {
                resolveCombat(combatGroup, gameMap);
                unitsInCombat.addAll(combatGroup);
            }
        }

        if (!combatGroups.isEmpty()) {
            System.out.println("Resolved " + combatGroups.size() + " combat(s)");
        }
    }

    /**
     * Finds all groups of units that should engage in combat.
     * Combat occurs when enemy units are adjacent and at least one is an assassin.
     * @param allUnits All units on the map
     * @return List of combat groups
     */
    private static List<List<Unit>> findCombatGroups(List<Unit> allUnits) {
        List<List<Unit>> combatGroups = new ArrayList<>();
        Set<Unit> processedUnits = new HashSet<>();

        for (Unit unit : allUnits) {
            if (!unit.isAlive() || processedUnits.contains(unit)) {
                continue;
            }

            // Find all units this unit should fight with
            List<Unit> combatGroup = findCombatGroupForUnit(unit, allUnits);

            if (combatGroup.size() > 1) {
                combatGroups.add(combatGroup);
                processedUnits.addAll(combatGroup);
            }
        }

        return combatGroups;
    }

    /**
     * Finds all units that should be in combat with the given unit.
     * @param unit The unit to find combat partners for
     * @param allUnits All units on the map
     * @return List of units in combat (including the original unit)
     */
    private static List<Unit> findCombatGroupForUnit(Unit unit, List<Unit> allUnits) {
        List<Unit> combatGroup = new ArrayList<>();
        combatGroup.add(unit);

        for (Unit otherUnit : allUnits) {
            if (otherUnit == unit || !otherUnit.isAlive()) {
                continue;
            }

            // Check if units should fight
            if (shouldFight(unit, otherUnit)) {
                combatGroup.add(otherUnit);
            }
        }

        return combatGroup;
    }

    /**
     * Determines if two units should engage in combat.
     * Combat occurs when:
     * 1. Units are from different teams
     * 2. Units are adjacent to each other
     * 3. At least one unit is an assassin
     *
     * @param unit1 First unit
     * @param unit2 Second unit
     * @return True if units should fight
     */
    private static boolean shouldFight(Unit unit1, Unit unit2) {
        // Must be from different teams
        if (unit1.getTeam() == unit2.getTeam()) {
            return false;
        }

        // Must be adjacent
        if (!unit1.isAdjacentTo(unit2)) {
            return false;
        }

        // At least one must be an assassin
        return (unit1 instanceof Assassin) || (unit2 instanceof Assassin);
    }

    /**
     * Resolves combat between a group of units.
     * Uses dice rolling to determine the loser.
     * Combat continues until only units from one team remain.
     *
     * @param combatants List of units in combat
     * @param gameMap Current game map
     */
    private static void resolveCombat(List<Unit> combatants, GameMap gameMap) {
        System.out.println("Combat started between " + combatants.size() + " units");

        // Continue fighting until only one team remains
        while (hasMultipleTeams(combatants)) {
            // Roll dice to determine loser
            Unit loser = rollDice(combatants);

            // Remove loser from combat and from map
            combatants.remove(loser);
            loser.die();
            gameMap.removeUnit(loser);

            System.out.println("  " + loser.getUnitType() + " (" + loser.getTeam() + ") eliminated");

            // Remove any other dead units from combat
            combatants.removeIf(unit -> !unit.isAlive());
        }

        if (!combatants.isEmpty()) {
            Team winner = combatants.get(0).getTeam();
            System.out.println("Combat ended - " + winner + " team victorious");
        }
    }

    /**
     * Checks if the combat group contains units from multiple teams.
     * @param combatants List of units in combat
     * @return True if multiple teams are present
     */
    private static boolean hasMultipleTeams(List<Unit> combatants) {
        if (combatants.size() <= 1) {
            return false;
        }

        Team firstTeam = combatants.get(0).getTeam();
        for (Unit unit : combatants) {
            if (unit.getTeam() != firstTeam) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rolls a dice to determine which unit loses in combat.
     * Each unit has an equal chance of being selected.
     *
     * @param combatants List of units in combat
     * @return The unit that loses and should be eliminated
     */
    private static Unit rollDice(List<Unit> combatants) {
        if (combatants.isEmpty()) {
            return null;
        }

        // Each unit has equal probability
        int diceRoll = (int) (Math.random() * combatants.size());
        Unit loser = combatants.get(diceRoll);

        System.out.println("  Dice rolled: " + (diceRoll + 1) + "/" + combatants.size() +
                " - " + loser.getUnitType() + " (" + loser.getTeam() + ") loses");

        return loser;
    }

    /**
     * Gets a summary of combat statistics for debugging.
     * @param gameMap Current game map
     * @return Combat statistics string
     */
    public static String getCombatStats(GameMap gameMap) {
        int totalUnits = gameMap.getUnits().size();
        int assassins = 0;
        int adjacentEnemies = 0;

        for (Unit unit : gameMap.getUnits()) {
            if (unit instanceof Assassin) {
                assassins++;
            }

            // Count adjacent enemies
            for (Unit other : gameMap.getUnits()) {
                if (other != unit && other.getTeam() != unit.getTeam() &&
                        unit.isAdjacentTo(other)) {
                    adjacentEnemies++;
                    break; // Count each unit only once
                }
            }
        }

        return String.format("Units: %d, Assassins: %d, Adjacent enemies: %d",
                totalUnits, assassins, adjacentEnemies);
    }
}