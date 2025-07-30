import javafx.scene.paint.Color;

enum ResourceType {
    WOOD(50, 100, Color.GREEN, "Tree"),
    ORE(100, 200, Color.GRAY, "Rock");

    private final int initialQuantity;
    private final int maxQuantity;
    private final Color displayColor;
    private final String displayName;

    /**
     * Constructor for ResourceType enum.
     * @param initialQuantity Starting quantity when resource is created
     * @param maxQuantity Maximum quantity this resource can hold
     * @param displayColor Color to display this resource type
     * @param displayName Display name for this resource
     */
    ResourceType(int initialQuantity, int maxQuantity, Color displayColor, String displayName) {
        this.initialQuantity = initialQuantity;
        this.maxQuantity = maxQuantity;
        this.displayColor = displayColor;
        this.displayName = displayName;
    }

    public int getInitialQuantity() { return initialQuantity; }
    public int getMaxQuantity() { return maxQuantity; }
    public Color getDisplayColor() { return displayColor; }
    public String getDisplayName() { return displayName; }
}