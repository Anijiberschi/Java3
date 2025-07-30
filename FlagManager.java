/**
 * Manager class for handling flag spawning timing.
 * Flags appear every 2 minutes (real time, independent of game speed).
 */
class FlagManager {
    private long lastFlagTime;
    private static final long FLAG_SPAWN_INTERVAL = 120000; // 2 minutes in milliseconds

    public FlagManager() {
        this.lastFlagTime = System.currentTimeMillis();
    }

    /**
     * Checks if it's time to spawn a new flag and spawns one if needed.
     * @param gameMap Current game map
     */
    public void update(GameMap gameMap) {
        long currentTime = System.currentTimeMillis();

        // Check if 2 minutes have passed and no flag is currently active
        if (currentTime - lastFlagTime >= FLAG_SPAWN_INTERVAL && !hasActiveFlag(gameMap)) {
            spawnFlag(gameMap);
            lastFlagTime = currentTime;
        }

        // Handle flag effects
        if (hasActiveFlag(gameMap)) {
            Flag.activateRandomMovement(gameMap);
        } else {
            Flag.deactivateRandomMovement(gameMap);
        }
    }

    /**
     * Checks if there's an active flag on the map.
     */
    private boolean hasActiveFlag(GameMap gameMap) {
        for (Collectable collectable : gameMap.getCollectables()) {
            if (collectable instanceof Flag && ((Flag) collectable).isActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Spawns a new flag at a random position.
     */
    private void spawnFlag(GameMap gameMap) {
        Position flagPos = gameMap.findRandomFreePosition();
        if (flagPos != null) {
            Flag flag = new Flag(flagPos);
            gameMap.addCollectable(flag);
        }
    }

    /**
     * Forces a flag to spawn immediately (for cheat code).
     */
    public void forceSpawnFlag(GameMap gameMap) {
        if (!hasActiveFlag(gameMap)) {
            spawnFlag(gameMap);
            System.out.println("Flag force-spawned via cheat code");
        } else {
            System.out.println("Flag already active, cannot spawn another");
        }
    }
}