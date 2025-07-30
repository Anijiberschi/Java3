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
 * HELBArmy Game Implementation.
 * Military simulation where two armies fight for territorial control.
 *
 * This class contains all the game logic, separated from the main entry point.
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
        System.out.println("Initializing HELBArmy game...");

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
     * Starts the main game loop.
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
        System.out.println("North Team (Blue cities):");
        System.out.println("  A - Generate Lumberjack");
        System.out.println("  Z - Generate Sower");
        System.out.println("  E - Generate Assassin");
        System.out.println("  R - Generate Random Unit");
        System.out.println("\nSouth Team (Red cities):");
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

        // Draw everything
        drawBackground();
        drawResources();
        drawUnits();
        drawCities();
        drawCollectables();
        drawUI();
    }

    /**
     * Handles keyboard input for cheat codes.
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

            default: break;
        }
    }

    /**
     * Generates a unit of specified type for specified team.
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

    // ================================
    // DRAWING METHODS
    // ================================

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

    private void drawCities() {
        for (City city : gameMap.getCities()) {
            Position pos = city.getPosition();
            gc.setFill(city.getDisplayColor());
            gc.fillRect(pos.getX() * SQUARE_SIZE, pos.getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

            // Add black center dot
            gc.setFill(Color.BLACK);
            gc.fillOval(pos.getX() * SQUARE_SIZE + 10, pos.getY() * SQUARE_SIZE + 10,
                    SQUARE_SIZE - 20, SQUARE_SIZE - 20);
        }
    }

    private void drawResources() {
        for (Resource resource : gameMap.getResources()) {
            Position pos = resource.getPosition();
            gc.setFill(resource.getDisplayColor());

            if (resource instanceof Rock) {
                // Rock occupies 2x2 area
                gc.fillRect(pos.getX() * SQUARE_SIZE, pos.getY() * SQUARE_SIZE,
                        SQUARE_SIZE * 2, SQUARE_SIZE * 2);
            } else {
                // Tree is single square
                gc.fillRect(pos.getX() * SQUARE_SIZE, pos.getY() * SQUARE_SIZE,
                        SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawUnits() {
        for (Unit unit : gameMap.getUnits()) {
            if (!unit.isAlive()) continue;

            Position pos = unit.getPosition();
            Color unitColor = unit.getDisplayColor();

            // Draw unit as colored circle
            gc.setFill(unitColor);
            gc.fillOval(pos.getX() * SQUARE_SIZE + 2, pos.getY() * SQUARE_SIZE + 2,
                    SQUARE_SIZE - 4, SQUARE_SIZE - 4);

            // Add team indicator (small dot)
            gc.setFill(unit.getTeam().getTeamColor());
            gc.fillOval(pos.getX() * SQUARE_SIZE + SQUARE_SIZE/2 - 3,
                    pos.getY() * SQUARE_SIZE + SQUARE_SIZE/2 - 3, 6, 6);
        }
    }

    private void drawCollectables() {
        for (Collectable collectable : gameMap.getCollectables()) {
            Position pos = collectable.getPosition();
            gc.setFill(collectable.getDisplayColor());

            if (collectable instanceof Flag) {
                // Draw flag as triangle
                double x = pos.getX() * SQUARE_SIZE;
                double y = pos.getY() * SQUARE_SIZE;
                double[] xPoints = {x + 5, x + SQUARE_SIZE - 5, x + SQUARE_SIZE/2};
                double[] yPoints = {y + SQUARE_SIZE - 5, y + SQUARE_SIZE - 5, y + 5};
                gc.fillPolygon(xPoints, yPoints, 3);

                // Show remaining time
                Flag flag = (Flag) collectable;
                gc.setFill(Color.WHITE);
                gc.setFont(new Font("Arial", 10));
                gc.fillText(String.valueOf(flag.getRemainingLifetime()), x, y + 15);
            } else if (collectable instanceof PhilosopherStone) {
                // Draw philosopher stone as diamond
                double x = pos.getX() * SQUARE_SIZE + SQUARE_SIZE/2;
                double y = pos.getY() * SQUARE_SIZE + SQUARE_SIZE/2;
                double size = SQUARE_SIZE/3;
                double[] xPoints = {x, x + size, x, x - size};
                double[] yPoints = {y - size, y, y + size, y};
                gc.fillPolygon(xPoints, yPoints, 4);
            }
        }
    }

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
    }

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

    private void generateRandomUnit(Team team) {
        UnitType[] types = UnitType.values();
        UnitType randomType = types[(int) (Math.random() * types.length)];
        generateUnit(randomType, team);
    }

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

    private void killAllUnits() {
        int count = gameMap.getUnits().size();
        gameMap.getUnits().clear();
        System.out.println("All units eliminated (" + count + " units)");
    }

    private void clearAllResources() {
        int count = gameMap.getResources().size();
        gameMap.getResources().clear();
        System.out.println("All resources cleared (" + count + " resources)");
    }

    private void spawnFlag() {
        flagManager.forceSpawnFlag(gameMap);
    }

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
        System.out.println("==================\n");
    }
}