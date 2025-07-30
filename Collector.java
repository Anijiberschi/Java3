/**
 * Abstract base class for collector units (Lumberjack and Miner).
 * Handles common collection behavior.
 */
abstract class Collector extends Unit {
    protected Resource currentTarget;
    protected boolean isCollecting;
    protected int collectionRate;
    protected ResourceType preferredResourceType;

    public Collector(Position position, Team team, UnitType unitType, ResourceType preferredType) {
        super(position, team, unitType);
        this.preferredResourceType = preferredType;
        this.collectionRate = unitType.getCollectionRate();
        this.isCollecting = false;
        this.currentTarget = null;
    }

    @Override
    protected void performUnitUpdate(GameMap gameMap) {
        // If we have a target, try to collect from it
        if (currentTarget != null) {
            if (currentTarget.isEmpty() || !gameMap.getResources().contains(currentTarget)) {
                // Target is gone, find a new one
                currentTarget = null;
                isCollecting = false;
            } else if (position.isAdjacent(currentTarget.getPosition())) {
                // We're adjacent to target, start collecting
                collectFromTarget(gameMap);
            } else {
                // Not adjacent, stop collecting
                isCollecting = false;
            }
        }

        // If we don't have a target, find one
        if (currentTarget == null) {
            findNewTarget(gameMap);
        }
    }

    /**
     * Collects resources from the current target.
     */
    private void collectFromTarget(GameMap gameMap) {
        if (currentTarget != null && currentTarget.canBeCollected()) {
            isCollecting = true;
            int collected = currentTarget.reduceQuantity(collectionRate);

            // Add resources to team's city
            City teamCity = gameMap.getCityForTeam(team);
            if (teamCity != null) {
                teamCity.addResources(preferredResourceType, collected);
            }

            // If target is empty, clear it
            if (currentTarget.isEmpty()) {
                currentTarget = null;
                isCollecting = false;
            }
        }
    }

    /**
     * Finds a new target resource to collect from.
     */
    private void findNewTarget(GameMap gameMap) {
        currentTarget = gameMap.findNearestResource(position, preferredResourceType);
        if (currentTarget == null) {
            // No preferred resource, try any collectable resource
            currentTarget = gameMap.findNearestCollectableResource(position);
        }
    }

    @Override
    protected MovementStrategy createDefaultMovementStrategy() {
        return new CollectorMovement();
    }

    // Getters
    public Resource getCurrentTarget() { return currentTarget; }
    public boolean isCollecting() { return isCollecting; }
    public ResourceType getPreferredResourceType() { return preferredResourceType; }
}
