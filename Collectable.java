/**
 * Interface for special collectable items.
 * These items have special effects when contacted by units.
 */
interface Collectable extends GameElement {
    /**
     * Called when a unit makes contact with this collectable.
     * @param unit The unit that made contact
     * @param gameMap The current game map
     */
    void onContact(Unit unit, GameMap gameMap);

    /**
     * Determines if this collectable should be removed from the map.
     * @return True if this collectable should disappear
     */
    boolean shouldDisappear();

    /**
     * Gets the remaining lifetime of this collectable in seconds.
     * @return Lifetime in seconds, or -1 if permanent
     */
    int getRemainingLifetime();
}