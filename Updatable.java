/**
 * Interface for elements that change over time.
 * These elements need to be updated each game tick.
 */
interface Updatable {
    /**
     * Updates this element's state.
     * Called once per game tick.
     * @param gameMap The current game map for context
     */
    void update(GameMap gameMap);
}