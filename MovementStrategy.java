/**
 * Interface for movement strategies.
 * Allows different units to have different movement behaviors.
 */
interface MovementStrategy {
    /**
     * Determines the next position for a unit to move to.
     * @param unit The unit to move
     * @param gameMap The current game map
     * @return The next position to move to, or current position if no move
     */
    Position getNextMove(Unit unit, GameMap gameMap);

    /**
     * Gets the name of this movement strategy (for debugging).
     * @return Strategy name
     */
    String getStrategyName();
}