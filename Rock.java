import java.util.List;
import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 * Rock resource - provides ore, occupies 2x2 area.
 */
class Rock extends Resource {
    private List<Position> occupiedPositions;

    /**
     * Creates a rock with default ore quantity (100).
     * @param topLeftPosition Top-left corner position of the 2x2 rock
     */
    public Rock(Position topLeftPosition) {
        super(topLeftPosition, ResourceType.ORE);
        initializeOccupiedPositions();
    }

    /**
     * Creates a rock with specified ore quantity.
     * @param topLeftPosition Top-left corner position of the 2x2 rock
     * @param quantity Initial ore quantity
     */
    public Rock(Position topLeftPosition, int quantity) {
        super(topLeftPosition, ResourceType.ORE, quantity, ResourceType.ORE.getMaxQuantity());
        initializeOccupiedPositions();
    }

    /**
     * Initializes the list of positions occupied by this 2x2 rock.
     */
    private void initializeOccupiedPositions() {
        occupiedPositions = new ArrayList<>();
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                occupiedPositions.add(new Position(position.getX() + dx, position.getY() + dy));
            }
        }
    }

    @Override
    public List<Position> getCollectionPositions() {
        // Rocks can be collected from all positions adjacent to any of the 4 occupied squares
        List<Position> collectionPositions = new ArrayList<>();

        for (Position occupiedPos : occupiedPositions) {
            List<Position> adjacent = occupiedPos.getAdjacentPositions();
            for (Position adjPos : adjacent) {
                // Only add if it's not occupied by the rock itself and not already in list
                if (!occupiesPosition(adjPos) && !collectionPositions.contains(adjPos)) {
                    collectionPositions.add(adjPos);
                }
            }
        }

        return collectionPositions;
    }

    @Override
    public boolean occupiesPosition(Position pos) {
        // Check if position is one of the 4 occupied positions
        return occupiedPositions.contains(pos);
    }

    /**
     * Gets all positions occupied by this rock.
     * @return List of occupied positions
     */
    public List<Position> getOccupiedPositions() {
        return new ArrayList<>(occupiedPositions);
    }

    @Override
    protected void performResourceUpdate(GameMap gameMap) {
        // Rocks don't change over time in basic implementation
        // Could add ore regeneration logic here if desired
    }
}