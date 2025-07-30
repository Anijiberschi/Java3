import javafx.scene.paint.Color;
import java.util.List;
import java.util.ArrayList;

/**
 * Abstract base class for all resources in the game.
 * Resources can be collected by units and provide materials for cities.
 */
public abstract class Resource implements GameElement, Updatable {
    protected Position position;
    protected ResourceType resourceType;
    protected int currentQuantity;
    protected int maxQuantity;
    protected boolean isBeingCollected;

    /**
     * Creates a new resource at the specified position.
     * @param position Position of the resource
     * @param resourceType Type of resource
     * @param initialQuantity Starting quantity
     * @param maxQuantity Maximum quantity this resource can hold
     */
    public Resource(Position position, ResourceType resourceType, int initialQuantity, int maxQuantity) {
        this.position = new Position(position);
        this.resourceType = resourceType;
        this.currentQuantity = initialQuantity;
        this.maxQuantity = maxQuantity;
        this.isBeingCollected = false;
    }

    /**
     * Creates a resource with default quantities based on type.
     * @param position Position of the resource
     * @param resourceType Type of resource
     */
    public Resource(Position position, ResourceType resourceType) {
        this(position, resourceType,
                resourceType.getInitialQuantity(),
                resourceType.getMaxQuantity());
    }

    /**
     * Updates the resource state each game tick.
     * @param gameMap Current game map
     */
    @Override
    public void update(GameMap gameMap) {
        // Check if any collectors are adjacent and collecting
        updateCollectionStatus(gameMap);

        // Perform resource-specific updates
        performResourceUpdate(gameMap);
    }

    /**
     * Performs resource-specific update logic.
     * Override in concrete classes for specialized behavior.
     * @param gameMap Current game map
     */
    protected void performResourceUpdate(GameMap gameMap) {
        // Default implementation does nothing
        // Override in subclasses for specific behavior (e.g., growth over time)
    }

    /**
     * Updates the collection status based on adjacent collectors.
     * @param gameMap Current game map
     */
    private void updateCollectionStatus(GameMap gameMap) {
        isBeingCollected = false;

        // Check all positions where collection can occur
        List<Position> collectionPositions = getCollectionPositions();
        for (Position collectionPos : collectionPositions) {
            List<Unit> unitsAtPos = gameMap.getUnitsAt(collectionPos);
            for (Unit unit : unitsAtPos) {
                if (unit instanceof Collector && unit.isAlive()) {
                    Collector collector = (Collector) unit;
                    if (collector.isCollecting() && collector.getCurrentTarget() == this) {
                        isBeingCollected = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Reduces the resource quantity by the specified amount.
     * @param amount Amount to reduce
     * @return Actual amount reduced (may be less if insufficient quantity)
     */
    public int reduceQuantity(int amount) {
        int actualReduction = Math.min(amount, currentQuantity);
        currentQuantity -= actualReduction;
        return actualReduction;
    }

    /**
     * Increases the resource quantity by the specified amount.
     * Used when sowing new resources.
     * @param amount Amount to add
     * @return Actual amount added (may be less if at max capacity)
     */
    public int addQuantity(int amount) {
        int actualAddition = Math.min(amount, maxQuantity - currentQuantity);
        currentQuantity += actualAddition;
        return actualAddition;
    }

    /**
     * Checks if this resource can be collected from.
     * @return True if resource has quantity and is not empty
     */
    public boolean canBeCollected() {
        return currentQuantity > 0;
    }

    /**
     * Checks if this resource is empty and should be removed.
     * @return True if resource has no quantity left
     */
    public boolean isEmpty() {
        return currentQuantity <= 0;
    }

    /**
     * Checks if this resource has available collection spots.
     * @param gameMap Current game map to check adjacent positions
     * @return True if at least one collection position is free
     */
    public boolean hasAvailableCollectionSpots(GameMap gameMap) {
        List<Position> collectionPositions = getCollectionPositions();
        for (Position pos : collectionPositions) {
            if (gameMap.isValidPosition(pos) && gameMap.isPositionFree(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all positions from which this resource can be collected.
     * Must be implemented by concrete resource classes.
     * @return List of collection positions
     */
    public abstract List<Position> getCollectionPositions();

    /**
     * Checks if this resource occupies a specific position.
     * Most resources occupy only their main position, but rocks occupy 2x2.
     * @param pos Position to check
     * @return True if resource occupies this position
     */
    public abstract boolean occupiesPosition(Position pos);

    // Getters
    public Position getPosition() { return new Position(position); }
    public ResourceType getResourceType() { return resourceType; }
    public int getCurrentQuantity() { return currentQuantity; }
    public int getMaxQuantity() { return maxQuantity; }
    public boolean isBeingCollected() { return isBeingCollected; }

    /**
     * Gets the percentage of resource remaining.
     * @return Percentage from 0.0 to 1.0
     */
    public double getQuantityPercentage() {
        return maxQuantity > 0 ? (double) currentQuantity / maxQuantity : 0.0;
    }

    // GameElement interface implementations
    @Override
    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    @Override
    public boolean blocksMovement() {
        return true; // Resources always block movement
    }

    @Override
    public Color getDisplayColor() {
        Color baseColor = resourceType.getDisplayColor();

        // Fade color based on remaining quantity
        double intensity = Math.max(0.3, getQuantityPercentage()); // Min 30% intensity

        return Color.color(
                baseColor.getRed() * intensity,
                baseColor.getGreen() * intensity,
                baseColor.getBlue() * intensity
        );
    }

    @Override
    public char getDisplaySymbol() {
        switch (resourceType) {
            case WOOD:
                return 'T'; // Tree
            case ORE:
                return 'R'; // Rock
            default:
                return '?';
        }
    }

    /**
     * Gets a string representation of this resource for debugging.
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format("%s at %s (%d/%d %s)",
                resourceType.getDisplayName(), position,
                currentQuantity, maxQuantity,
                resourceType.name().toLowerCase());
    }

    /**
     * Checks equality based on position and type.
     * @param obj Object to compare
     * @return True if resources are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Resource resource = (Resource) obj;
        return position.equals(resource.position) &&
                resourceType == resource.resourceType;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(position, resourceType);
    }
}