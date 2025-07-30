import javafx.scene.paint.Color;

/**
 * City - generates units and stores resources for a team.
 */
class City implements GameElement, Updatable {
    private Position position;
    private Team team;
    private int woodResources;
    private int oreResources;
    private int unitGenerationTimer;
    private static final int GENERATION_INTERVAL = 2; // Generate unit every 2 seconds

    /**
     * Creates a city for the specified team at the specified position.
     * @param position City position
     * @param team Team this city belongs to
     */
    public City(Position position, Team team) {
        this.position = new Position(position);
        this.team = team;
        this.woodResources = 0;
        this.oreResources = 0;
        this.unitGenerationTimer = 0;
    }

    @Override
    public void update(GameMap gameMap) {
        unitGenerationTimer++;

        if (unitGenerationTimer >= GENERATION_INTERVAL) {
            generateUnit(gameMap);
            unitGenerationTimer = 0;
        }
    }

    /**
     * Generates a new unit based on current game state.
     * @param gameMap Current game map
     */
    private void generateUnit(GameMap gameMap) {
        UnitType typeToGenerate = decideUnitType(gameMap);
        Position spawnPos = findSpawnPosition(gameMap);

        if (spawnPos != null) {
            Unit newUnit = createUnit(typeToGenerate, spawnPos);
            if (newUnit != null) {
                gameMap.addUnit(newUnit);
            }
        }
    }

    /**
     * Decides what type of unit to generate based on game state.
     * Influenced by available resources and enemy units.
     * @param gameMap Current game map
     * @return Unit type to generate
     */
    private UnitType decideUnitType(GameMap gameMap) {
        int treeCount = 0;
        int rockCount = 0;
        int enemyAssassinCount = 0;

        // Count resources and enemy assassins
        for (Resource resource : gameMap.getResources()) {
            if (resource.getResourceType() == ResourceType.WOOD) {
                treeCount++;
            } else if (resource.getResourceType() == ResourceType.ORE) {
                rockCount++;
            }
        }

        for (Unit unit : gameMap.getUnits()) {
            if (unit.getTeam() != team && unit instanceof Assassin && unit.isAlive()) {
                enemyAssassinCount++;
            }
        }

        // Calculate probabilities based on influences
        double lumberjackWeight = Math.max(1, treeCount * 2);
        double minerWeight = Math.max(1, rockCount * 2);
        double assassinWeight = Math.max(1, enemyAssassinCount * 3);
        double sowerWeight = 1; // Base weight for sowers

        double totalWeight = lumberjackWeight + minerWeight + assassinWeight + sowerWeight;
        double random = Math.random() * totalWeight;

        if (random < lumberjackWeight) {
            return UnitType.LUMBERJACK;
        } else if (random < lumberjackWeight + minerWeight) {
            return UnitType.MINER;
        } else if (random < lumberjackWeight + minerWeight + assassinWeight) {
            return UnitType.ASSASSIN;
        } else {
            return UnitType.SOWER;
        }
    }

    /**
     * Finds a spawn position near the city.
     * @param gameMap Current game map
     * @return Free position near city, or null if none available
     */
    private Position findSpawnPosition(GameMap gameMap) {
        // Try positions in expanding radius from city
        for (int radius = 1; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    if (Math.abs(dx) == radius || Math.abs(dy) == radius) { // Only check perimeter
                        Position candidate = new Position(position.getX() + dx, position.getY() + dy);
                        if (gameMap.isValidPosition(candidate) && gameMap.isPositionFree(candidate)) {
                            return candidate;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Creates a unit of the specified type.
     * @param type Unit type to create
     * @param position Position for the new unit
     * @return New unit instance
     */
    private Unit createUnit(UnitType type, Position position) {
        switch (type) {
            case LUMBERJACK:
                return new Lumberjack(position, team);
            case MINER:
                return new Miner(position, team);
            case SOWER:
                return new Sower(position, team);
            case ASSASSIN:
                return new Assassin(position, team);
            default:
                return null;
        }
    }

    /**
     * Adds resources to the city's stockpile.
     * @param type Type of resource to add
     * @param amount Amount to add
     */
    public void addResources(ResourceType type, int amount) {
        if (type == ResourceType.WOOD) {
            woodResources += amount;
        } else if (type == ResourceType.ORE) {
            oreResources += amount;
        }
    }

    // Getters
    public Position getPosition() { return new Position(position); }
    public Team getTeam() { return team; }
    public int getWoodResources() { return woodResources; }
    public int getOreResources() { return oreResources; }

    // GameElement interface implementations
    @Override
    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    @Override
    public boolean blocksMovement() {
        return true; // Cities block movement
    }

    @Override
    public Color getDisplayColor() {
        return team.getTeamColor();
    }

    @Override
    public char getDisplaySymbol() {
        return team == Team.NORTH ? 'N' : 'S';
    }

    @Override
    public String toString() {
        return String.format("%s City at %s (Wood: %d, Ore: %d)",
                team, position, woodResources, oreResources);
    }
}
