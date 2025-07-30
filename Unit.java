import javafx.scene.paint.Color;

/**
 * Abstract base class for all military units in the game.
 * Defines common behavior and properties for all unit types.
 */
public abstract class Unit implements GameElement, Updatable {
    protected Position position;
    protected Team team;
    protected boolean isAlive;
    protected MovementStrategy movementStrategy;
    protected UnitType unitType;

    /**
     * Creates a new unit at the specified position for the specified team.
     * @param position Starting position
     * @param team Team this unit belongs to
     * @param unitType Type of this unit
     */
    public Unit(Position position, Team team, UnitType unitType) {
        this.position = new Position(position);
        this.team = team;
        this.unitType = unitType;
        this.isAlive = true;
        this.movementStrategy = createDefaultMovementStrategy();
    }

    /**
     * Creates the default movement strategy for this unit type.
     * Must be implemented by concrete unit classes.
     * @return Default movement strategy
     */
    protected abstract MovementStrategy createDefaultMovementStrategy();

    /**
     * Updates the unit's state each game tick.
     * Template method that calls specific update behaviors.
     * @param gameMap Current game map
     */
    @Override
    public void update(GameMap gameMap) {
        if (!isAlive) {
            return;
        }

        // Perform unit-specific update logic
        performUnitUpdate(gameMap);

        // Move the unit
        move(gameMap);

        // Check for interactions with adjacent units/elements
        handleInteractions(gameMap);
    }

    /**
     * Performs unit-specific update logic.
     * Override in concrete classes for specialized behavior.
     * @param gameMap Current game map
     */
    protected void performUnitUpdate(GameMap gameMap) {
        // Default implementation does nothing
        // Override in subclasses for specific behavior
    }

    /**
     * Moves the unit according to its movement strategy.
     * @param gameMap Current game map
     */
    public void move(GameMap gameMap) {
        if (!isAlive || movementStrategy == null) {
            return;
        }

        Position nextPosition = movementStrategy.getNextMove(this, gameMap);

        // Only move if the new position is different and valid
        if (nextPosition != null && !nextPosition.equals(position) &&
                gameMap.isValidPosition(nextPosition) && gameMap.isPositionFree(nextPosition)) {
            setPosition(nextPosition);
        }
    }

    /**
     * Handles interactions with adjacent units and elements.
     * @param gameMap Current game map
     */
    protected void handleInteractions(GameMap gameMap) {
        // Check for collectables at current position
        for (Collectable collectable : gameMap.getCollectables()) {
            if (collectable.getPosition().equals(position)) {
                collectable.onContact(this, gameMap);
            }
        }

        // Combat interactions are handled by CombatSystem
        // Collection interactions are handled by specific collector types
    }

    /**
     * Checks if this unit is adjacent to another unit.
     * @param other Other unit to check
     * @return True if units are adjacent
     */
    public boolean isAdjacentTo(Unit other) {
        return position.isAdjacent(other.position);
    }

    /**
     * Kills this unit, removing it from the game.
     */
    public void die() {
        this.isAlive = false;
    }

    /**
     * Revives this unit (used for testing or special effects).
     */
    public void revive() {
        this.isAlive = true;
    }

    /**
     * Changes the movement strategy for this unit.
     * @param strategy New movement strategy
     */
    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }

    // Getters
    public Position getPosition() { return new Position(position); }
    public Team getTeam() { return team; }
    public boolean isAlive() { return isAlive; }
    public UnitType getUnitType() { return unitType; }
    public MovementStrategy getMovementStrategy() { return movementStrategy; }

    // Setters
    @Override
    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    // GameElement interface implementations
    @Override
    public boolean blocksMovement() {
        return isAlive; // Dead units don't block movement
    }

    @Override
    public Color getDisplayColor() {
        if (!isAlive) {
            return Color.GRAY;
        }

        // Return unit type color with team influence
        Color baseColor = unitType.getDisplayColor();
        Color teamColor = team.getTeamColor();

        // Blend unit color with team color
        return Color.color(
                (baseColor.getRed() + teamColor.getRed()) / 2,
                (baseColor.getGreen() + teamColor.getGreen()) / 2,
                (baseColor.getBlue() + teamColor.getBlue()) / 2
        );
    }

    @Override
    public char getDisplaySymbol() {
        if (!isAlive) {
            return 'X';
        }

        switch (unitType) {
            case LUMBERJACK:
            case MINER:
                return 'C'; // Collector
            case SOWER:
                return 'S';
            case ASSASSIN:
                return 'A';
            default:
                return 'U'; // Unknown unit
        }
    }

    /**
     * Gets a string representation of this unit for debugging.
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format("%s %s at %s (alive: %s)",
                team, unitType, position, isAlive);
    }

    /**
     * Checks equality based on position, team, and type.
     * @param obj Object to compare
     * @return True if units are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Unit unit = (Unit) obj;
        return position.equals(unit.position) &&
                team == unit.team &&
                unitType == unit.unitType;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(position, team, unitType);
    }
}