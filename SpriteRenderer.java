import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsable du rendu des sprites selon la charte graphique HELBArmy
 * Utilise les images extraites de la charte graphique officielle
 */
public class SpriteRenderer {

    private static final int SQUARE_SIZE = 40; // Taille d'une case sur la grille

    // Cache des images pour éviter de les recharger
    private static Map<String, Image> imageCache = new HashMap<>();

    /**
     * Charge une image depuis les ressources et la met en cache
     * @param imagePath Chemin vers l'image dans les ressources
     * @return L'image chargée
     */
    private static Image loadImage(String imagePath) {
        if (!imageCache.containsKey(imagePath)) {
            try {
                Image image = new Image(SpriteRenderer.class.getResourceAsStream(imagePath));
                if (image.isError()) {
                    System.err.println("Erreur lors du chargement de l'image : " + imagePath);
                    return null;
                }
                imageCache.put(imagePath, image);
                System.out.println("Image chargée avec succès : " + imagePath);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + imagePath);
                e.printStackTrace();
                return null;
            }
        }
        return imageCache.get(imagePath);
    }

    /**
     * Dessine une image à la position spécifiée sur la grille
     * @param gc Contexte graphique
     * @param imagePath Chemin vers l'image
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     */
    private static void drawSprite(GraphicsContext gc, String imagePath, int gridX, int gridY) {
        drawSprite(gc, imagePath, gridX, gridY, 1, 1);
    }

    /**
     * Dessine une image à la position spécifiée sur la grille avec une taille personnalisée
     * @param gc Contexte graphique
     * @param imagePath Chemin vers l'image
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     * @param widthInCells Largeur en nombre de cases
     * @param heightInCells Hauteur en nombre de cases
     */
    private static void drawSprite(GraphicsContext gc, String imagePath, int gridX, int gridY,
                                   int widthInCells, int heightInCells) {
        Image image = loadImage(imagePath);
        if (image != null) {
            double pixelX = gridX * SQUARE_SIZE;
            double pixelY = gridY * SQUARE_SIZE;
            double width = widthInCells * SQUARE_SIZE;
            double height = heightInCells * SQUARE_SIZE;

            gc.drawImage(image, pixelX, pixelY, width, height);
        }
    }

    // ==================== RESSOURCES ====================

    /**
     * Dessine un arbre
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     */
    public static void drawTree(GraphicsContext gc, int gridX, int gridY) {
        drawSprite(gc, "/img/resources/tree.png", gridX, gridY);
    }

    /**
     * Dessine un rocher (occupe 2x2 cases)
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     */
    public static void drawRock(GraphicsContext gc, int gridX, int gridY) {
        drawSprite(gc, "/img/resources/rock.png", gridX, gridY, 2, 2);
    }

    // ==================== COLLECTABLES ====================

    /**
     * Dessine un drapeau
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     */
    public static void drawFlag(GraphicsContext gc, int gridX, int gridY) {
        drawSprite(gc, "/img/resources/flag.png", gridX, gridY);
    }

    /**
     * Dessine une pierre philosophale
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     */
    public static void drawPhilosopherStone(GraphicsContext gc, int gridX, int gridY) {
        drawSprite(gc, "/img/resources/philosopher_stone.png", gridX, gridY);
    }

    // ==================== UNITÉS ====================

    /**
     * Dessine un collecteur
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     * @param isNorthTeam true si équipe du nord, false pour équipe du sud
     */
    public static void drawCollector(GraphicsContext gc, int gridX, int gridY, boolean isNorthTeam) {
        String imagePath = isNorthTeam ?
                "/img/units/north/collector_north.png" :
                "/img/units/south/collector_south.png";
        drawSprite(gc, imagePath, gridX, gridY);
    }

    /**
     * Dessine un semeur
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     * @param isNorthTeam true si équipe du nord, false pour équipe du sud
     */
    public static void drawSower(GraphicsContext gc, int gridX, int gridY, boolean isNorthTeam) {
        String imagePath = isNorthTeam ?
                "/img/units/north/sower_north.png" :
                "/img/units/south/sower_south.png";
        drawSprite(gc, imagePath, gridX, gridY);
    }

    /**
     * Dessine un assassin
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     * @param isNorthTeam true si équipe du nord, false pour équipe du sud
     */
    public static void drawAssassin(GraphicsContext gc, int gridX, int gridY, boolean isNorthTeam) {
        String imagePath = isNorthTeam ?
                "/img/units/north/assassin_north.png" :
                "/img/units/south/assassin_south.png";
        drawSprite(gc, imagePath, gridX, gridY);
    }

    // ==================== VILLES ====================

    /**
     * Dessine une ville
     * @param gc Contexte graphique
     * @param gridX Position X sur la grille
     * @param gridY Position Y sur la grille
     * @param isNorthTeam true si équipe du nord, false pour équipe du sud
     */
    public static void drawCity(GraphicsContext gc, int gridX, int gridY, boolean isNorthTeam) {
        String imagePath = isNorthTeam ?
                "/img/cities/city_north.png" :
                "/img/cities/city_south.png";
        drawSprite(gc, imagePath, gridX, gridY);
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Pré-charge toutes les images au démarrage de l'application
     * Recommandé pour éviter les lags pendant le jeu
     */
    public static void preloadAllImages() {
        String[] imagePaths = {
                // Ressources
                "/img/resources/tree.png",
                "/img/resources/rock.png",
                "/img/resources/flag.png",
                "/img/resources/philosopher_stone.png",

                // Unités Nord
                "/img/units/north/collector_north.png",
                "/img/units/north/sower_north.png",
                "/img/units/north/assassin_north.png",

                // Unités Sud
                "/img/units/south/collector_south.png",
                "/img/units/south/sower_south.png",
                "/img/units/south/assassin_south.png",

                // Villes
                "/img/cities/city_north.png",
                "/img/cities/city_south.png"
        };

        System.out.println("Pré-chargement des sprites...");
        int loadedCount = 0;

        for (String path : imagePaths) {
            Image img = loadImage(path);
            if (img != null) {
                loadedCount++;
            }
        }

        System.out.println("Sprites pré-chargés : " + loadedCount + "/" + imagePaths.length);
    }

    /**
     * Libère le cache des images pour économiser la mémoire
     */
    public static void clearImageCache() {
        imageCache.clear();
        System.out.println("Cache des images vidé");
    }

    /**
     * Obtient les informations sur le cache des images
     * @return Chaîne contenant les informations du cache
     */
    public static String getCacheInfo() {
        return "Images en cache : " + imageCache.size();
    }
}