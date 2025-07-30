import java.util.*;

/**
 * Manages the game map, including all elements positioned on it.
 * Central hub for spatial queries and collision detection.
 */
public class GameMap {
    private final int width;
    private final int height;
    private final GameElement[][] grid;

    // Collections for different types of elements
    private final List<Unit> units;
    private final List<Resource> resources;
    private final List<City> cities;
    private final List<Collectable> collectables;

    /**
     * Creates a new game map with specified dimensions.
     * @param width Map width in grid units
     * @param height Map height in grid units
     */
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new GameElement[width][height];

        this.units = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.collectables = new ArrayList<>();

        initializeCities();
    }

    /**
     * Initializes the two cities at opposite corners.
     */
    private void initializeCities() {
        // North city at (0,0)
        City northCity = new City(new Position(0, 0), Team.NORTH);
        cities.add(northCity);
        Team.NORTH.setCityPosition(northCity.getPosition());

        // South city at bottom-right corner
        City southCity = new City(new Position(width - 1, height - 1), Team.SOUTH);
        cities.add(southCity);
        Team.SOUTH.setCityPosition(southCity.getPosition());
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Unit> getUnits() { return new ArrayList<>(units); }
    public List<Resource> getResources() { return new ArrayList<>(resources); }
    public List<City> getCities() { return new ArrayList<>(cities); }
    public List<Collectable> getCollectables() { return new ArrayList<>(collectables); }

    /**
     * Checks if a position is within map bounds.
     * @param pos Position to check
     * @return True if position is valid
     */
    public boolean isValidPosition(Position pos) {
        return pos.getX() >= 0 && pos.getX() < width &&
                pos.getY() >= 0 && pos.getY() < height;
    }

    /**
     * Checks if a position is free (no blocking elements).
     * @param pos Position to check
     * @return True if position is free
     */
    public boolean isPositionFree(Position pos) {
        if (!isValidPosition(pos)) {
            return false;
        }

        // Check for units at this position
        for (Unit unit : units) {
            if (unit.getPosition().equals(pos)) {
                return false;
            }
        }

        // Check for resources that block this position
        for (Resource resource : resources) {
            if (resource.occupiesPosition(pos)) {
                return false;
            }
        }

        // Check for cities
        for (City city : cities) {
            if (city.getPosition().equals(pos)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the element at a specific position.
     * @param pos Position to check
     * @return GameElement at position, or null if empty
     */
    public GameElement getElementAt(Position pos) {
        if (!isValidPosition(pos)) {
            return null;
        }

        // Check units first (they're on top)
        for (Unit unit : units) {
            if (unit.getPosition().equals(pos)) {
                return unit;
            }
        }

        // Check resources
        for (Resource resource : resources) {
            if (resource.occupiesPosition(pos)) {
                return resource;
            }
        }

        // Check cities
        for (City city : cities) {
            if (city.getPosition().equals(pos)) {
                return city;
            }
        }

        // Check collectables
        for (Collectable collectable : collectables) {
            if (collectable.getPosition().equals(pos)) {
                return collectable;
            }
        }

        return null;
    }

    /**
     * Gets all units at a specific position.
     * @param pos Position to check
     * @return List of units at position
     */
    public List<Unit> getUnitsAt(Position pos) {
        List<Unit> unitsAtPos = new ArrayList<>();
        for (Unit unit : units) {
            if (unit.getPosition().equals(pos)) {
                unitsAtPos.add(unit);
            }
        }
        return unitsAtPos;
    }

    /**
     * Gets all units adjacent to a position.
     * @param pos Center position
     * @return List of adjacent units
     */
    public List<Unit> getAdjacentUnits(Position pos) {
        List<Unit> adjacentUnits = new ArrayList<>();
        List<Position> adjacentPositions = pos.getAdjacentPositions();

        for (Position adjPos : adjacentPositions) {
            if (isValidPosition(adjPos)) {
                adjacentUnits.addAll(getUnitsAt(adjPos));
            }
        }

        return adjacentUnits;
    }

    /**
     * Finds the nearest resource of a specific type to a position.
     * @param pos Starting position
     * @param type Resource type to find
     * @return Nearest resource, or null if none found
     */
    public Resource findNearestResource(Position pos, ResourceType type) {
        Resource nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Resource resource : resources) {
            if (resource.getResourceType() == type && resource.canBeCollected()) {
                double distance = pos.distanceTo(resource.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = resource;
                }
            }
        }

        return nearest;
    }

    /**
     * Finds the nearest collectable resource (any type with collection spots available).
     * @param pos Starting position
     * @return Nearest collectable resource, or null if none
     */
    public Resource findNearestCollectableResource(Position pos) {
        Resource nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Resource resource : resources) {
            if (resource.canBeCollected() && resource.hasAvailableCollectionSpots(this)) {
                double distance = pos.distanceTo(resource.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = resource;
                }
            }
        }

        return nearest;
    }

    /**
     * Finds the nearest enemy unit to a position.
     * @param pos Starting position
     * @param team Team to find enemies for
     * @return Nearest enemy unit, or null if none
     */
    public Unit findNearestEnemy(Position pos, Team team) {
        Unit nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Unit unit : units) {
            if (unit.getTeam() != team && unit.isAlive()) {
                double distance = pos.distanceTo(unit.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = unit;
                }
            }
        }

        return nearest;
    }

    /**
     * Adds a unit to the map.
     * @param unit Unit to add
     */
    public void addUnit(Unit unit) {
        if (unit != null && !units.contains(unit)) {
            units.add(unit);
        }
    }

    /**
     * Removes a unit from the map.
     * @param unit Unit to remove
     */
    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    /**
     * Adds a resource to the map.
     * @param resource Resource to add
     */
    public void addResource(Resource resource) {
        if (resource != null && !resources.contains(resource)) {
            resources.add(resource);
        }
    }

    /**
     * Removes a resource from the map.
     * @param resource Resource to remove
     */
    public void removeResource(Resource resource) {
        resources.remove(resource);
    }

    /**
     * Adds a collectable to the map.
     * @param collectable Collectable to add
     */
    public void addCollectable(Collectable collectable) {
        if (collectable != null && !collectables.contains(collectable)) {
            collectables.add(collectable);
        }
    }

    /**
     * Removes a collectable from the map.
     * @param collectable Collectable to remove
     */
    public void removeCollectable(Collectable collectable) {
        collectables.remove(collectable);
    }

    /**
     * Gets the city for a specific team.
     * @param team Team to get city for
     * @return City for the team
     */
    public City getCityForTeam(Team team) {
        for (City city : cities) {
            if (city.getTeam() == team) {
                return city;
            }
        }
        return null;
    }

    /**
     * Finds a random free position on the map.
     * @return Random free position, or null if map is full
     */
    public Position findRandomFreePosition() {
        List<Position> freePositions = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position pos = new Position(x, y);
                if (isPositionFree(pos)) {
                    freePositions.add(pos);
                }
            }
        }

        if (freePositions.isEmpty()) {
            return null;
        }

        return freePositions.get((int) (Math.random() * freePositions.size()));
    }

    /**
     * Checks if a 2x2 area is free for rock placement.
     * @param topLeft Top-left corner of the area
     * @return True if 2x2 area is free
     */
    public boolean canPlace2x2(Position topLeft) {
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                Position pos = new Position(topLeft.getX() + dx, topLeft.getY() + dy);
                if (!isPositionFree(pos)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Updates all elements on the map.
     * Should be called once per game tick.
     */
    public void updateAll() {
        // Update all units
        for (Unit unit : new ArrayList<>(units)) {
            if (unit.isAlive()) {
                unit.update(this);
            } else {
                removeUnit(unit);
            }
        }

        // Update all resources
        for (Resource resource : new ArrayList<>(resources)) {
            resource.update(this);
            if (resource.isEmpty()) {
                removeResource(resource);
            }
        }

        // Update all collectables
        for (Collectable collectable : new ArrayList<>(collectables)) {
            if (collectable.shouldDisappear()) {
                removeCollectable(collectable);
            }
        }

        // Update cities (generate units)
        for (City city : cities) {
            city.update(this);
        }
    }
}