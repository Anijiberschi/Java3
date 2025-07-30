import javafx.scene.paint.Color;

/**
 * Base interface for all elements that can be placed on the game map.
 * Provides common functionality for positioning and rendering.
 */
interface GameElement {
    /**
     * Gets the current position of this element.
     * @return The position
     */
    Position getPosition();

    /**
     * Sets the position of this element.
     * @param position The new position
     */
    void setPosition(Position position);

    /**
     * Determines if this element blocks movement of units.
     * @return True if units cannot pass through this element
     */
    boolean blocksMovement();

    /**
     * Gets the color to display this element.
     * @return The display color
     */
    Color getDisplayColor();

    /**
     * Gets a symbol representing this element (for debugging/text display).
     * @return A character representing this element
     */
    char getDisplaySymbol();
}