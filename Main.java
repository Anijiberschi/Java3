import javafx.application.Application;

/**
 * Main entry point for the HELBArmy game.
 * This class serves as a bootstrap to launch the actual game.
 */
public class Main extends Application {

    /**
     * JavaFX entry point.
     * Delegates to HELBArmy for the actual game implementation.
     */
    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {
        // Create and start the HELBArmy game
        HELBArmy game = new HELBArmy();
        game.start(primaryStage);
    }

    /**
     * Program entry point.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting HELBArmy - Military Simulation");
        System.out.println("========================================");

        // Launch JavaFX application
        launch(args);
    }
}