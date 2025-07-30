/**
 * Assassin unit - attacks enemy units.
 */
class Assassin extends Unit {
    private Unit currentTarget;

    public Assassin(Position position, Team team) {
        super(position, team, UnitType.ASSASSIN);
        this.currentTarget = null;
    }

    @Override
    protected void performUnitUpdate(GameMap gameMap) {
        // Find nearest enemy if we don't have a target
        if (currentTarget == null || !currentTarget.isAlive()) {
            findNewTarget(gameMap);
        }

        // Combat is handled by CombatSystem, not here
        // Assassins just move towards enemies
    }

    /**
     * Finds a new enemy target using priority: Assassins > Collectors > Sowers.
     */
    private void findNewTarget(GameMap gameMap) {
        currentTarget = null;
        double minDistance = Double.MAX_VALUE;

        // Priority 1: Enemy assassins
        for (Unit unit : gameMap.getUnits()) {
            if (unit.getTeam() != team && unit.isAlive() && unit instanceof Assassin) {
                double distance = position.distanceTo(unit.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    currentTarget = unit;
                }
            }
        }

        // Priority 2: Enemy collectors (if no assassins found)
        if (currentTarget == null) {
            for (Unit unit : gameMap.getUnits()) {
                if (unit.getTeam() != team && unit.isAlive() && unit instanceof Collector) {
                    double distance = position.distanceTo(unit.getPosition());
                    if (distance < minDistance) {
                        minDistance = distance;
                        currentTarget = unit;
                    }
                }
            }
        }

        // Priority 3: Enemy sowers (if no assassins or collectors found)
        if (currentTarget == null) {
            for (Unit unit : gameMap.getUnits()) {
                if (unit.getTeam() != team && unit.isAlive() && unit instanceof Sower) {
                    double distance = position.distanceTo(unit.getPosition());
                    if (distance < minDistance) {
                        minDistance = distance;
                        currentTarget = unit;
                    }
                }
            }
        }
    }

    @Override
    protected MovementStrategy createDefaultMovementStrategy() {
        return new AssassinMovement();
    }

    // Getters
    public Unit getCurrentTarget() { return currentTarget; }
}
