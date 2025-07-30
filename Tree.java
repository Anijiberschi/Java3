import java.util.List;
import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 * Tree resource - provides wood, occupies single position.
 */
class Tree extends Resource {

    /**
     * Creates a tree with default wood quantity (50).
     * @param position Position of the tree
     */
    public Tree(Position position) {
        super(position, ResourceType.WOOD);
    }

    /**
     * Creates a tree with specified wood quantity.
     * @param position Position of the tree
     * @param quantity Initial wood quantity
     */
    public Tree(Position position, int quantity) {
        super(position, ResourceType.WOOD, quantity, ResourceType.WOOD.getMaxQuantity());
    }

    @Override
    public List<Position> getCollectionPositions() {
        // Trees can be collected from all 8 adjacent positions
        return position.getAdjacentPositions();
    }

    @Override
    public boolean occupiesPosition(Position pos) {
        // Trees occupy only their main position
        return position.equals(pos);
    }

    @Override
    protected void performResourceUpdate(GameMap gameMap) {
        // Trees don't grow or change over time in basic implementation
        // Could add growth logic here if desired
    }
}