public class Position {
    private int x;
    private int y;

    /**
     * Creates a new position.
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     * @param other Position to copy
     */
    public Position(Position other) {
        this.x = other.x;
        this.y = other.y;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    /**
     * Calculates the distance to another position.
     * @param other The other position
     * @return The Euclidean distance
     */
    public double distanceTo(Position other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    /**
     * Calculates the Manhattan distance to another position.
     * @param other The other position
     * @return The Manhattan distance
     */
    public int manhattanDistanceTo(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    /**
     * Checks if this position is adjacent to another (including diagonals).
     * @param other The other position
     * @return True if positions are adjacent
     */
    public boolean isAdjacent(Position other) {
        return Math.abs(x - other.x) <= 1 && Math.abs(y - other.y) <= 1 && !equals(other);
    }

    /**
     * Gets all adjacent positions (8 directions).
     * @return List of adjacent positions
     */
    public java.util.List<Position> getAdjacentPositions() {
        java.util.List<Position> adjacent = new java.util.ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    adjacent.add(new Position(x + dx, y + dy));
                }
            }
        }
        return adjacent;
    }

    /**
     * Moves one step towards a target position.
     * @param target The target position
     * @return New position one step closer to target
     */
    public Position moveTowards(Position target) {
        int newX = x;
        int newY = y;

        if (target.x > x) newX++;
        else if (target.x < x) newX--;

        if (target.y > y) newY++;
        else if (target.y < y) newY--;

        return new Position(newX, newY);
    }

    /**
     * Checks if position is within bounds.
     * @param width Map width
     * @param height Map height
     * @return True if position is valid
     */
    public boolean isValid(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Position(%d, %d)", x, y);
    }
}