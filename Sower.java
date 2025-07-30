import java.util.List;

class Sower extends Unit {
    private ResourceType resourceBeingSown;
    private int sowingProgress;
    private Position sowingTarget;
    private Resource resourceInProgress;

    public Sower(Position position, Team team) {
        super(position, team, UnitType.SOWER);
        this.resourceBeingSown = null;
        this.sowingProgress = 0;
        this.sowingTarget = null;
        this.resourceInProgress = null;
    }

    @Override
    protected void performUnitUpdate(GameMap gameMap) {
        if (isSowing()) {
            continueSowing(gameMap);
        } else {
            // Not sowing, decide what to sow and where
            decideSowingAction(gameMap);
        }
    }

    /**
     * Continues the sowing process.
     */
    private void continueSowing(GameMap gameMap) {
        if (sowingTarget != null && position.isAdjacent(sowingTarget)) {
            // We're adjacent to sowing target, continue sowing
            sowingProgress += 2; // +2 units per second

            if (resourceInProgress != null) {
                resourceInProgress.addQuantity(2);

                // Check if resource is complete
                if (resourceInProgress.getCurrentQuantity() >= resourceInProgress.getMaxQuantity()) {
                    completeSowing();
                }
            }
        } else {
            // Not adjacent to target, stop sowing
            interruptSowing();
        }
    }

    /**
     * Decides what resource to sow and where.
     */
    private void decideSowingAction(GameMap gameMap) {
        // Randomly decide resource type
        ResourceType typeToSow = Math.random() < 0.5 ? ResourceType.WOOD : ResourceType.ORE;

        Position targetPos = null;

        if (typeToSow == ResourceType.WOOD) {
            targetPos = findTreeSowingPosition(gameMap);
        } else {
            targetPos = findRockSowingPosition(gameMap);
        }

        if (targetPos != null) {
            startSowing(typeToSow, targetPos, gameMap);
        }
    }

    /**
     * Finds a position to sow a tree (adjacent to existing tree or random).
     */
    private Position findTreeSowingPosition(GameMap gameMap) {
        // Try to find position adjacent to existing tree
        for (Resource resource : gameMap.getResources()) {
            if (resource.getResourceType() == ResourceType.WOOD) {
                List<Position> adjacent = resource.getPosition().getAdjacentPositions();
                for (Position pos : adjacent) {
                    if (gameMap.isValidPosition(pos) && gameMap.isPositionFree(pos)) {
                        return pos;
                    }
                }
            }
        }

        // No existing trees or no free adjacent spots, use random position
        return gameMap.findRandomFreePosition();
    }

    /**
     * Finds a position to sow a rock (furthest from other rocks).
     */
    private Position findRockSowingPosition(GameMap gameMap) {
        Position bestPos = null;
        double maxMinDistance = 0;

        // Try multiple random positions and pick the one furthest from existing rocks
        for (int attempt = 0; attempt < 20; attempt++) {
            Position candidate = gameMap.findRandomFreePosition();
            if (candidate != null && gameMap.canPlace2x2(candidate)) {
                double minDistanceToRock = Double.MAX_VALUE;

                for (Resource resource : gameMap.getResources()) {
                    if (resource.getResourceType() == ResourceType.ORE) {
                        double distance = candidate.distanceTo(resource.getPosition());
                        minDistanceToRock = Math.min(minDistanceToRock, distance);
                    }
                }

                if (minDistanceToRock > maxMinDistance) {
                    maxMinDistance = minDistanceToRock;
                    bestPos = candidate;
                }
            }
        }

        return bestPos;
    }

    /**
     * Starts sowing a resource.
     */
    private void startSowing(ResourceType type, Position target, GameMap gameMap) {
        resourceBeingSown = type;
        sowingTarget = target;
        sowingProgress = 0;

        // Create the resource immediately but with 0 quantity
        if (type == ResourceType.WOOD) {
            resourceInProgress = new Tree(target, 0);
        } else {
            resourceInProgress = new Rock(target, 0);
        }

        gameMap.addResource(resourceInProgress);
    }

    /**
     * Completes the sowing process.
     */
    private void completeSowing() {
        resourceBeingSown = null;
        sowingTarget = null;
        sowingProgress = 0;
        resourceInProgress = null;
    }

    /**
     * Interrupts the sowing process (e.g., when attacked).
     */
    private void interruptSowing() {
        resourceBeingSown = null;
        sowingTarget = null;
        sowingProgress = 0;
        resourceInProgress = null;
    }

    @Override
    protected MovementStrategy createDefaultMovementStrategy() {
        return new SowerMovement();
    }

    // Getters
    public boolean isSowing() { return resourceBeingSown != null; }
    public ResourceType getResourceBeingSown() { return resourceBeingSown; }
    public int getSowingProgress() { return sowingProgress; }
}