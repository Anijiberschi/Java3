import javafx.scene.paint.Color;
import java.util.List;

/**
 * Flag collectable that makes all units move randomly.
 * Appears every 2 minutes, lasts 10 seconds.
 * Effect timing is independent of game speed.
 */
class Flag implements Collectable {
    private Position position;
    private int remainingLifetime; // In real seconds, not game ticks
    private boolean isActive;
    private long creationTime; // Real world time

    private static final int FLAG_LIFETIME = 10; // 10 real seconds
    private static final Color FLAG_COLOR = Color.YELLOW;

    /**
     * Creates a new flag at the specified position.
     * @param position Position where flag appears
     */
    public Flag(Position position) {
        this.position = new Position(position);
        this.remainingLifetime = FLAG_LIFETIME;
        this.isActive = true;
        this.creationTime = System.currentTimeMillis();

        System.out.println("🏴 Flag appeared at " + position + " for " + FLAG_LIFETIME + " seconds");
    }

    @Override
    public void onContact(Unit unit, GameMap gameMap) {
        // Flag doesn't need to be collected, just affects all units by being present
        // The effect is handled by checking if any flag exists on the map
    }

    @Override
    public boolean shouldDisappear() {
        // Check real world time instead of game ticks (flag timing is independent of game speed)
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - creationTime) / 1000;

        if (elapsedSeconds >= FLAG_LIFETIME) {
            if (isActive) {
                System.out.println("🏴 Flag disappeared after " + elapsedSeconds + " seconds");
                isActive = false;
            }
            return true;
        }

        return false;
    }

    @Override
    public int getRemainingLifetime() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - creationTime) / 1000;
        return Math.max(0, FLAG_LIFETIME - (int)elapsedSeconds);
    }

    /**
     * Activates random movement for all units on the map.
     * @param gameMap Current game map
     */
    public static void activateRandomMovement(GameMap gameMap) {
        RandomMovement randomStrategy = new RandomMovement();

        for (Unit unit : gameMap.getUnits()) {
            unit.setMovementStrategy(randomStrategy);
        }
    }

    /**
     * Deactivates random movement, restoring normal behavior for all units.
     * @param gameMap Current game map
     */
    public static void deactivateRandomMovement(GameMap gameMap) {
        for (Unit unit : gameMap.getUnits()) {
            // Restore default movement strategy based on unit type
            MovementStrategy defaultStrategy = createDefaultStrategyForUnit(unit);
            unit.setMovementStrategy(defaultStrategy);
        }
    }

    /**
     * Creates the appropriate default movement strategy for a unit.
     */
    private static MovementStrategy createDefaultStrategyForUnit(Unit unit) {
        if (unit instanceof Collector) {
            return new CollectorMovement();
        } else if (unit instanceof Assassin) {
            return new AssassinMovement();
        } else if (unit instanceof Sower) {
            return new SowerMovement();
        } else {
            return new RandomMovement(); // Fallback
        }
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
        return false; // Units can walk through flags
    }

    @Override
    public Color getDisplayColor() {
        return FLAG_COLOR;
    }

    @Override
    public char getDisplaySymbol() {
        return 'F';
    }

    public boolean isActive() {
        return isActive;
    }
}