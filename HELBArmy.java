import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * HELBArmy Game Implementation with Sprite Graphics.
 * Military simulation where two armies fight for territorial control.
 *
 * This class contains all the game logic, separated from the main entry point.
 * Now uses sprite-based graphics according to the official HELBArmy charter.
 */
public class HELBArmy {

    // Game constants
    private static final String TITLE = "HELBArmy - Military Simulation";
    private static final int WIDTH = 800;
    private static final int HEIGHT = WIDTH;
    private static final int ROWS = 20;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;

    // Game state
    private GraphicsContext gc;
    private GameMap gameMap;
    private Timeline gameLoop;
    private boolean gameRunning = true;
    private double gameSpeed = 1.0;
    private FlagManager flagManager;

    /**
     * Initializes and starts the HELBArmy game.
     * Called by Main.java after JavaFX setup.
     *
     * @param primaryStage The primary JavaFX stage
     * @throws Exception If initialization fails
     */
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Initializing HELBArmy game with sprite graphics...");

        // Preload all sprites before starting the game
        SpriteRenderer.preloadAllImages();

        // Setup JavaFX window
        setupWindow(primaryStage);

        // Initialize game components
        initializeGame();

        // Start game loop
        startGameLoop();

        System.out.println("HELBArmy game started successfully!");
        printGameInstructions();
    }

    /**
     * Sets up the JavaFX window and canvas.
     */
    private void setupWindow(Stage primaryStage) {
        primaryStage.setTitle(TITLE);
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        gc = canvas.getGraphicsContext2D();

        // Set up keyboard event handling
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                handleKeyPress(event.getCode());
            }
        });

        // Handle window close event
        primaryStage.setOnCloseRequest(e -> {
            endGame();
        });
    }

    /**
     * Initializes all game components.
     */
    private void initializeGame() {
        // Create game map
        gameMap = new GameMap(ROWS, COLUMNS);

        // Initialize flag manager
        flagManager = new FlagManager();

        // Add initial resources and units
        generateInitialResources();
        createTestUnits();

        System.out.println("Game initialized:");
        System.out.println("- Cities: " + gameMap.getCities().size());
        System.out.println("- Resources: " + gameMap.getResources().size());
        System.out.println("- Units: " + gameMap.getUnits().size());
    }

    /**
     * Starts the main game loop with current game speed.
     */
    private void startGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / gameSpeed), e -> updateGame()));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();
    }

    /**
     * Prints game instructions to console.
     */
    private void printGameInstructions() {
        System.out.println("\n=== GAME CONTROLS ===");
        System.out.println("North Team (Cities with black stars):");
        System.out.println("  A - Generate Lumberjack");
        System.out.println("  Z - Generate Sower");
        System.out.println("  E - Generate Assassin");
        System.out.println("  R - Generate Random Unit");
        System.out.println("\nSouth Team (Cities with white stars):");
        System.out.println("  W - Generate Lumberjack");
        System.out.println("  X - Generate Sower");
        System.out.println("  C - Generate Assassin");
        System.out.println("  V - Generate Random Unit");
        System.out.println("\nOther Commands:");
        System.out.println("  J - Kill all Collectors");
        System.out.println("  K - Kill all Sowers");
        System.out.println("  L - Kill all Assassins");
        System.out.println("  M - Kill all Units");
        System.out.println("  U - Clear all Resources");
        System.out.println("  I - Spawn Flag");
        System.out.println("  P - Spawn Philosopher Stone");
        System.out.println("  + - Increase Speed (faster game)");
        System.out.println("  - - Decrease Speed (slower game)");
        System.out.println("  0 - Reset Speed to normal");
        System.out.println("  O - End Game");
        System.out.println("=====================\n");
    }

    /**
     * Generates initial resources on the map.
     */
    private void generateInitialResources() {
        // Add some trees randomly
        for (int i = 0; i < 5; i++) {
            Position pos = gameMap.findRandomFreePosition();
            if (pos != null) {
                gameMap.addResource(new Tree(pos));
            }
        }

        // Add some rocks randomly
        for (int i = 0; i < 3; i++) {
            Position pos = gameMap.findRandomFreePosition();
            if (pos != null && gameMap.canPlace2x2(pos)) {
                gameMap.addResource(new Rock(pos));
            }
        }
    }

    /**
     * Creates initial test units.
     */
    private void createTestUnits() {
        // Create one unit near each city for testing
        Position northStart = new Position(1, 1);
        Position southStart = new Position(ROWS - 2, COLUMNS - 2);

        if (gameMap.isPositionFree(northStart)) {
            gameMap.addUnit(new Lumberjack(northStart, Team.NORTH));
        }
        if (gameMap.isPositionFree(southStart)) {
            gameMap.addUnit(new Assassin(southStart, Team.SOUTH));
        }
    }

    /**
     * Main game update loop.
     */
    private void updateGame() {
        if (!gameRunning) {
            drawGameOver();
            return;
        }

        // Update flag manager (handles flag spawning and effects)
        flagManager.update(gameMap);

        // Update all game elements
        gameMap.updateAll();

        // Process combat after all movements
        CombatSystem.processCombats(gameMap);

        // Draw everything using sprites
        drawBackground();
        drawResourcesWithSprites();
        drawUnitsWithSprites();
        drawCitiesWithSprites();
        drawCollectablesWithSprites();
        drawUI();
    }

    /**
     * Handles keyboard input for cheat codes and game controls.
     */
    private void handleKeyPress(KeyCode keyCode) {
        switch (keyCode) {
            // North team generation
            case A: generateUnit(UnitType.LUMBERJACK, Team.NORTH); break;
            case Z: generateUnit(UnitType.SOWER, Team.NORTH); break;
            case E: generateUnit(UnitType.ASSASSIN, Team.NORTH); break;
            case R: generateRandomUnit(Team.NORTH); break;

            // South team generation
            case W: generateUnit(UnitType.LUMBERJACK, Team.SOUTH); break;
            case X: generateUnit(UnitType.SOWER, Team.SOUTH); break;
            case C: generateUnit(UnitType.ASSASSIN, Team.SOUTH); break;
            case V: generateRandomUnit(Team.SOUTH); break;

            // Elimination commands
            case J: killCollectors(); break;
            case K: killSowers(); break;
            case L: killAssassins(); break;
            case M: killAllUnits(); break;

            // Special commands
            case O: endGame(); break;
            case U: clearAllResources(); break;
            case I: spawnFlag(); break;
            case P: spawnPhilosopherStone(); break;

            // Speed controls
            case PLUS:
            case EQUALS: // + key (with or without shift)
                setGameSpeed(gameSpeed * 1.2); // 20% faster
                break;
            case MINUS: // - key
                setGameSpeed(gameSpeed * 0.8); // 25% slower
                break;
            case DIGIT0: // 0 key to reset
                setGameSpeed(1.0);
                break;

            default: break;
        }
    }

    /**
     * Updates the game speed and restarts the game loop with new timing.
     * @param newSpeed New speed multiplier (0.5 = twice as fast, 2.0 = twice as slow)
     */
    private void setGameSpeed(double newSpeed) {
        this.gameSpeed = Math.max(0.1, Math.min(10.0, newSpeed));

        // Restart the game loop with new speed
        if (gameLoop != null) {
            gameLoop.stop();
            startGameLoop();
        }

        System.out.println("Game speed set to: " + String.format("%.1f", this.gameSpeed) + "x (" + getSpeedDescription() + ")");
    }

    /**
     * Gets a human-readable description of current game speed.
     * @return Speed description for UI display
     */
    private String getSpeedDescription() {
        if (gameSpeed < 0.5) {
            return String.format("%.1fx Fast", 1.0 / gameSpeed);
        } else if (gameSpeed < 1.0) {
            return String.format("%.1fx Fast", 1.0 / gameSpeed);
        } else if (gameSpeed > 2.0) {
            return String.format("%.1fx Slow", gameSpeed);
        } else if (gameSpeed > 1.0) {
            return String.format("%.1fx Slow", gameSpeed);
        } else {
            return "Normal";
        }
    }

    // ================================
    // DRAWING METHODS WITH SPRITES
    // ================================

    /**
     * Draws the checkered background pattern.
     */
    private void drawBackground() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.web("AAD751"));
                } else {
                    gc.setFill(Color.web("A2D149"));
                }
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    /**
     * Draws all cities using sprites according to team.
     */
    private void drawCitiesWithSprites() {
        for (City city : gameMap.getCities()) {
            Position pos = city.getPosition();
            boolean isNorth = city.getTeam() == Team.NORTH;
            SpriteRenderer.drawCity(gc, pos.getX(), pos.getY(), isNorth);
        }
    }

    /**
     * Draws all resources (trees and rocks) using sprites.
     */
    private void drawResourcesWithSprites() {
        for (Resource resource : gameMap.getResources()) {
            Position pos = resource.getPosition();

            if (resource instanceof Tree) {
                SpriteRenderer.drawTree(gc, pos.getX(), pos.getY());
            } else if (resource instanceof Rock) {
                SpriteRenderer.drawRock(gc, pos.getX(), pos.getY());
            }
        }
    }

    /**
     * Draws all units using appropriate sprites based on type and team.
     */
    private void drawUnitsWithSprites() {
        for (Unit unit : gameMap.getUnits()) {
            if (!unit.isAlive()) continue;

            Position pos = unit.getPosition();
            boolean isNorth = unit.getTeam() == Team.NORTH;

            if (unit instanceof Lumberjack || unit instanceof Miner) {
                // Both lumberjacks and miners are collectors
                SpriteRenderer.drawCollector(gc, pos.getX(), pos.getY(), isNorth);
            } else if (unit instanceof Sower) {
                SpriteRenderer.drawSower(gc, pos.getX(), pos.getY(), isNorth);
            } else if (unit instanceof Assassin) {
                SpriteRenderer.drawAssassin(gc, pos.getX(), pos.getY(), isNorth);
            }
        }
    }

    /**
     * Draws all collectables (flags and philosopher stones) using sprites.
     */
    private void drawCollectablesWithSprites() {
        for (Collectable collectable : gameMap.getCollectables()) {
            Position pos = collectable.getPosition();

            if (collectable instanceof Flag) {
                SpriteRenderer.drawFlag(gc, pos.getX(), pos.getY());

                // Show remaining time on flag
                Flag flag = (Flag) collectable;
                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Arial", 12));
                gc.fillText(String.valueOf(flag.getRemainingLifetime()),
                        pos.getX() * SQUARE_SIZE + 5, pos.getY() * SQUARE_SIZE + 15);

            } else if (collectable instanceof PhilosopherStone) {
                SpriteRenderer.drawPhilosopherStone(gc, pos.getX(), pos.getY());
            }
        }
    }

    /**
     * Draws the user interface including statistics and controls info.
     */
    private void drawUI() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 16));

        int northUnits = 0, southUnits = 0;
        for (Unit unit : gameMap.getUnits()) {
            if (unit.getTeam() == Team.NORTH) northUnits++;
            else southUnits++;
        }

        gc.fillText("North: " + northUnits + " units | South: " + southUnits + " units", 10, 20);
        gc.fillText("Resources: " + gameMap.getResources().size() + " | Collectables: " + gameMap.getCollectables().size(), 10, 40);
        gc.fillText("Press O to end game", 10, HEIGHT - 10);

        // Show flag status
        boolean hasFlag = gameMap.getCollectables().stream().anyMatch(c -> c instanceof Flag);
        if (hasFlag) {
            gc.setFill(Color.YELLOW);
            gc.fillText("🏴 FLAG ACTIVE - Random Movement!", 10, 60);
        }

        // Show city resources
        City northCity = gameMap.getCityForTeam(Team.NORTH);
        City southCity = gameMap.getCityForTeam(Team.SOUTH);

        gc.setFill(Color.WHITE);
        if (northCity != null) {
            gc.fillText("North - Wood: " + northCity.getWoodResources() +
                    " Ore: " + northCity.getOreResources(), WIDTH - 200, 20);
        }
        if (southCity != null) {
            gc.fillText("South - Wood: " + southCity.getWoodResources() +
                    " Ore: " + southCity.getOreResources(), WIDTH - 200, 40);
        }

        // Show game speed info
        gc.setFill(Color.CYAN);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("Speed: " + getSpeedDescription(), WIDTH - 200, 60);
        gc.fillText("Controls: +/- speed, 0 reset", WIDTH - 200, 80);

        // Show sprite cache info
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(SpriteRenderer.getCacheInfo(), 10, HEIGHT - 30);
    }

    /**
     * Draws the game over screen with final scores and winner.
     */
    private void drawGameOver() {
        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 40));
        gc.fillText("Game Over", WIDTH / 3, HEIGHT / 2);

        // Show final scores
        City northCity = gameMap.getCityForTeam(Team.NORTH);
        City southCity = gameMap.getCityForTeam(Team.SOUTH);

        if (northCity != null && southCity != null) {
            gc.setFont(new Font("Arial", 20));
            gc.fillText("Final Scores:", WIDTH / 3, HEIGHT / 2 + 50);
            gc.fillText("North - Wood: " + northCity.getWoodResources() +
                    " Ore: " + northCity.getOreResources(), WIDTH / 3, HEIGHT / 2 + 80);
            gc.fillText("South - Wood: " + southCity.getWoodResources() +
                    " Ore: " + southCity.getOreResources(), WIDTH / 3, HEIGHT / 2 + 110);

            // Determine winner
            String winner = determineWinner(northCity, southCity);
            gc.fillText("Result: " + winner, WIDTH / 3, HEIGHT / 2 + 140);
        }
    }

    // ================================
    // UTILITY METHODS
    // ================================

    /**
     * Generates a unit of specified type for specified team.
     * @param type Type of unit to generate
     * @param team Team to generate unit for
     */
    private void generateUnit(UnitType type, Team team) {
        City teamCity = gameMap.getCityForTeam(team);
        if (teamCity == null) return;

        Position spawnPos = findSpawnPosition(team);
        if (spawnPos != null) {
            Unit newUnit = createUnit(type, spawnPos, team);
            if (newUnit != null) {
                gameMap.addUnit(newUnit);
                System.out.println("Generated " + type + " for " + team + " at " + spawnPos);
            }
        } else {
            System.out.println("No spawn position available for " + team);
        }
    }

    /**
     * Creates a unit instance based on type.
     * @param type Type of unit to create
     * @param position Position for the new unit
     * @param team Team the unit belongs to
     * @return New unit instance
     */
    private Unit createUnit(UnitType type, Position position, Team team) {
        switch (type) {
            case LUMBERJACK: return new Lumberjack(position, team);
            case MINER: return new Miner(position, team);
            case SOWER: return new Sower(position, team);
            case ASSASSIN: return new Assassin(position, team);
            default: return null;
        }
    }

    /**
     * Finds a spawn position near a team's city.
     * @param team Team to find spawn position for
     * @return Free position near city, or null if none available
     */
    private Position findSpawnPosition(Team team) {
        Position cityPos = team.getCityPosition();

        // Try positions in expanding radius
        for (int radius = 1; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    Position candidate = new Position(cityPos.getX() + dx, cityPos.getY() + dy);
                    if (gameMap.isValidPosition(candidate) && gameMap.isPositionFree(candidate)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines the winner based on resource comparison.
     * @param northCity North team's city
     * @param southCity South team's city
     * @return Winner description string
     */
    private String determineWinner(City northCity, City southCity) {
        boolean northWood = northCity.getWoodResources() > southCity.getWoodResources();
        boolean northOre = northCity.getOreResources() > southCity.getOreResources();

        if (northWood && northOre) {
            return "NORTH WINS!";
        } else if (!northWood && !northOre) {
            return "SOUTH WINS!";
        } else {
            return "TIE!";
        }
    }

    /**
     * Generates a random unit type for the specified team.
     * @param team Team to generate random unit for
     */
    private void generateRandomUnit(Team team) {
        UnitType[] types = UnitType.values();
        UnitType randomType = types[(int) (Math.random() * types.length)];
        generateUnit(randomType, team);
    }

    /**
     * Removes all collector units from the map.
     */
    private void killCollectors() {
        List<Unit> toRemove = new ArrayList<>();
        for (Unit unit : gameMap.getUnits()) {
            if (unit instanceof Collector) {
                toRemove.add(unit);
            }
        }
        for (Unit unit : toRemove) {
            gameMap.removeUnit(unit);
        }
        System.out.println("All collectors eliminated (" + toRemove.size() + " units)");
    }

    /**
     * Removes all sower units from the map.
     */
    private void killSowers() {
        List<Unit> toRemove = new ArrayList<>();
        for (Unit unit : gameMap.getUnits()) {
            if (unit instanceof Sower) {
                toRemove.add(unit);
            }
        }
        for (Unit unit : toRemove) {
            gameMap.removeUnit(unit);
        }
        System.out.println("All sowers eliminated (" + toRemove.size() + " units)");
    }

    /**
     * Removes all assassin units from the map.
     */
    private void killAssassins() {
        List<Unit> toRemove = new ArrayList<>();
        for (Unit unit : gameMap.getUnits()) {
            if (unit instanceof Assassin) {
                toRemove.add(unit);
            }
        }
        for (Unit unit : toRemove) {
            gameMap.removeUnit(unit);
        }
        System.out.println("All assassins eliminated (" + toRemove.size() + " units)");
    }

    /**
     * Removes all units from the map.
     */
    private void killAllUnits() {
        int count = gameMap.getUnits().size();
        gameMap.getUnits().clear();
        System.out.println("All units eliminated (" + count + " units)");
    }

    /**
     * Removes all resources from the map.
     */
    private void clearAllResources() {
        int count = gameMap.getResources().size();
        gameMap.getResources().clear();
        System.out.println("All resources cleared (" + count + " resources)");
    }

    /**
     * Forces a flag to spawn immediately.
     */
    private void spawnFlag() {
        flagManager.forceSpawnFlag(gameMap);
    }

    /**
     * Spawns a philosopher stone at a random position.
     */
    private void spawnPhilosopherStone() {
        Position pos = gameMap.findRandomFreePosition();
        if (pos != null) {
            PhilosopherStone stone = new PhilosopherStone(pos);
            gameMap.addCollectable(stone);
            System.out.println("Philosopher Stone spawned at " + pos);
        } else {
            System.out.println("No free position for Philosopher Stone");
        }
    }

    /**
     * Ends the game, shows final results, and cleans up resources.
     */
    private void endGame() {
        gameRunning = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Print final results to console
        City northCity = gameMap.getCityForTeam(Team.NORTH);
        City southCity = gameMap.getCityForTeam(Team.SOUTH);

        System.out.println("\n=== GAME ENDED ===");
        if (northCity != null) {
            System.out.println("North - Wood: " + northCity.getWoodResources() +
                    " Ore: " + northCity.getOreResources());
        }
        if (southCity != null) {
            System.out.println("South - Wood: " + southCity.getWoodResources() +
                    " Ore: " + southCity.getOreResources());
        }

        if (northCity != null && southCity != null) {
            System.out.println("Winner: " + determineWinner(northCity, southCity));
        }

        // Clean up sprite cache
        SpriteRenderer.clearImageCache();
        System.out.println("==================\n");
    }
}