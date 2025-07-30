import javafx.scene.paint.Color;

/**
 * Philosopher Stone that teleports units to random positions.
 * Remains on map permanently after being placed.
 */
class PhilosopherStone implements Collectable {
    private Position position;
    private static final Color STONE_COLOR = Color.PURPLE;

    /**
     * Creates a new philosopher stone at the specified position.
     * @param position Position where stone is placed
     */
    public PhilosopherStone(Position position) {
        this.position = new Position(position);
        System.out.println("💎 Philosopher Stone created at " + position);
    }

    @Override
    public void onContact(Unit unit, GameMap gameMap) {
        // Teleport the unit to a random position
        Position randomPos = gameMap.findRandomFreePosition();

        if (randomPos != null) {
            Position oldPos = unit.getPosition();
            unit.setPosition(randomPos);
            System.out.println("✨ " + unit.getUnitType() + " (" + unit.getTeam() +
                    ") teleported from " + oldPos + " to " + randomPos);
        } else {
            System.out.println("⚠️ No free position for teleportation!");
        }
    }

    @Override
    public boolean shouldDisappear() {
        return false; // Philosopher stones are permanent
    }

    @Override
    public int getRemainingLifetime() {
        return -1; // Permanent
    }

    // GameElement interface implementation
    @Override
    public Position getPosition() {
        return new Position(position);
    }

    @Override
    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    @Override
    public boolean blocksMovement() {
        return false; // Units can walk through stones (and get teleported)
    }

    @Override
    public Color getDisplayColor() {
        return STONE_COLOR;
    }

    @Override
    public char getDisplaySymbol() {
        return 'P';
    }
}