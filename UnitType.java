import javafx.scene.paint.Color;

/**
 * Represents the different types of units in the game.
 * Each type has specific characteristics and display properties.
 */
enum UnitType {
    LUMBERJACK(Color.ORANGE, 2, ResourceType.WOOD, 1),
    MINER(Color.ORANGE, 3, ResourceType.ORE, 2),
    SOWER(Color.PURPLE, 0, null, 3),
    ASSASSIN(Color.BLUE, 0, null, 4);

    private final Color displayColor;
    private final int collectionRate;
    private final ResourceType preferredResource;
    private final int generationPriority;

    /**
     * Constructor for UnitType enum.
     * @param displayColor Color to display this unit type
     * @param collectionRate Rate of resource collection (units per second)
     * @param preferredResource Type of resource this unit prefers
     * @param generationPriority Priority for automatic generation (higher = more likely)
     */
    UnitType(Color displayColor, int collectionRate, ResourceType preferredResource, int generationPriority) {
        this.displayColor = displayColor;
        this.collectionRate = collectionRate;
        this.preferredResource = preferredResource;
        this.generationPriority = generationPriority;
    }

    public Color getDisplayColor() { return displayColor; }
    public int getCollectionRate() { return collectionRate; }
    public ResourceType getPreferredResource() { return preferredResource; }
    public int getGenerationPriority() { return generationPriority; }

    /**
     * Checks if this unit type can collect resources.
     * @return True if this is a collector type
     */
    public boolean isCollector() {
        return this == LUMBERJACK || this == MINER;
    }
}
