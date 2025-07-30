
    /**
     * Movement strategy for collector units.
     * Moves towards the nearest collectable resource.
     */
    class CollectorMovement implements MovementStrategy {

        @Override
        public Position getNextMove(Unit unit, GameMap gameMap) {
            if (!(unit instanceof Collector)) {
                return unit.getPosition(); // Don't move if not a collector
            }

            Collector collector = (Collector) unit;
            Resource target = collector.getCurrentTarget();

            if (target == null) {
                // No target, don't move
                return unit.getPosition();
            }

            // Move towards the target
            return moveTowards(unit.getPosition(), target.getPosition(), gameMap);
        }

        @Override
        public String getStrategyName() {
            return "CollectorMovement";
        }

        /**
         * Moves one step towards a target position.
         * @param from Starting position
         * @param to Target position
         * @param gameMap Game map for collision checking
         * @return Next position to move to
         */
        private Position moveTowards(Position from, Position to, GameMap gameMap) {
            if (from.equals(to)) {
                return from;
            }

            Position nextPos = from.moveTowards(to);

            // Check if the next position is valid and free
            if (gameMap.isValidPosition(nextPos) && gameMap.isPositionFree(nextPos)) {
                return nextPos;
            } else {
                // Can't move directly, stay in place (basic pathfinding)
                // In a more advanced implementation, we could try alternative paths
                return from;
            }
        }
    }

    /**
     * Movement strategy for assassin units.
     * Moves towards the nearest enemy with priority: Assassins > Collectors > Sowers.
     */
    class AssassinMovement implements MovementStrategy {

        @Override
        public Position getNextMove(Unit unit, GameMap gameMap) {
            if (!(unit instanceof Assassin)) {
                return unit.getPosition();
            }

            Assassin assassin = (Assassin) unit;
            Unit target = assassin.getCurrentTarget();

            if (target == null || !target.isAlive()) {
                // No valid target, move randomly or stay put
                return findRandomPosition(unit.getPosition(), gameMap);
            }

            // Move towards the target
            return moveTowards(unit.getPosition(), target.getPosition(), gameMap);
        }

        @Override
        public String getStrategyName() {
            return "AssassinMovement";
        }

        /**
         * Moves one step towards a target position.
         */
        private Position moveTowards(Position from, Position to, GameMap gameMap) {
            if (from.equals(to)) {
                return from;
            }

            Position nextPos = from.moveTowards(to);

            if (gameMap.isValidPosition(nextPos) && gameMap.isPositionFree(nextPos)) {
                return nextPos;
            } else {
                return from; // Stay in place if blocked
            }
        }

        /**
         * Finds a random position to move to when no target is available.
         */
        private Position findRandomPosition(Position current, GameMap gameMap) {
            java.util.List<Position> adjacent = current.getAdjacentPositions();
            java.util.Collections.shuffle(adjacent);

            for (Position pos : adjacent) {
                if (gameMap.isValidPosition(pos) && gameMap.isPositionFree(pos)) {
                    return pos;
                }
            }

            return current; // Stay in place if no valid moves
        }
    }

    /**
     * Movement strategy for sower units.
     * Moves towards sowing positions based on resource type being sown.
     */
    class SowerMovement implements MovementStrategy {

        @Override
        public Position getNextMove(Unit unit, GameMap gameMap) {
            if (!(unit instanceof Sower)) {
                return unit.getPosition();
            }

            Sower sower = (Sower) unit;

            if (sower.isSowing()) {
                // Currently sowing, don't move (should stay adjacent to sowing target)
                return unit.getPosition();
            }

            // Not sowing, find a position to sow
            Position sowingTarget = findSowingTarget(unit.getPosition(), gameMap);
            if (sowingTarget != null) {
                return moveTowards(unit.getPosition(), sowingTarget, gameMap);
            }

            // No sowing target found, move randomly
            return findRandomPosition(unit.getPosition(), gameMap);
        }

        @Override
        public String getStrategyName() {
            return "SowerMovement";
        }

        /**
         * Finds a target position for sowing.
         */
        private Position findSowingTarget(Position current, GameMap gameMap) {
            // Randomly decide what to sow
            ResourceType typeToSow = Math.random() < 0.5 ? ResourceType.WOOD : ResourceType.ORE;

            if (typeToSow == ResourceType.WOOD) {
                return findTreeSowingTarget(gameMap);
            } else {
                return findRockSowingTarget(gameMap);
            }
        }

        /**
         * Finds a target for sowing trees (near existing trees).
         */
        private Position findTreeSowingTarget(GameMap gameMap) {
            for (Resource resource : gameMap.getResources()) {
                if (resource.getResourceType() == ResourceType.WOOD) {
                    java.util.List<Position> adjacent = resource.getPosition().getAdjacentPositions();
                    for (Position pos : adjacent) {
                        if (gameMap.isValidPosition(pos) && gameMap.isPositionFree(pos)) {
                            return pos;
                        }
                    }
                }
            }
            // No trees found, return random position
            return gameMap.findRandomFreePosition();
        }

        /**
         * Finds a target for sowing rocks (far from other rocks).
         */
        private Position findRockSowingTarget(GameMap gameMap) {
            Position bestPos = null;
            double maxMinDistance = 0;

            // Try several random positions
            for (int attempt = 0; attempt < 10; attempt++) {
                Position candidate = gameMap.findRandomFreePosition();
                if (candidate != null && gameMap.canPlace2x2(candidate)) {
                    double minDistanceToRock = Double.MAX_VALUE;

                    for (Resource resource : gameMap.getResources()) {
                        if (resource.getResourceType() == ResourceType.ORE) {
                            double distance = candidate.distanceTo(resource.getPosition());
                            minDistanceToRock = Math.min(minDistanceToRock, distance);
                        }
                    }

                    if (minDistanceToRock > maxMinDistance) {
                        maxMinDistance = minDistanceToRock;
                        bestPos = candidate;
                    }
                }
            }

            return bestPos;
        }

        /**
         * Moves one step towards a target position.
         */
        private Position moveTowards(Position from, Position to, GameMap gameMap) {
            if (from.equals(to)) {
                return from;
            }

            Position nextPos = from.moveTowards(to);

            if (gameMap.isValidPosition(nextPos) && gameMap.isPositionFree(nextPos)) {
                return nextPos;
            } else {
                return from;
            }
        }

        /**
         * Finds a random adjacent position to move to.
         */
        private Position findRandomPosition(Position current, GameMap gameMap) {
            java.util.List<Position> adjacent = current.getAdjacentPositions();
            java.util.Collections.shuffle(adjacent);

            for (Position pos : adjacent) {
                if (gameMap.isValidPosition(pos) && gameMap.isPositionFree(pos)) {
                    return pos;
                }
            }

            return current;
        }
    }

    /**
     * Random movement strategy.
     * Used when flag is active, making all units move randomly.
     */
    class RandomMovement implements MovementStrategy {

        @Override
        public Position getNextMove(Unit unit, GameMap gameMap) {
            java.util.List<Position> adjacent = unit.getPosition().getAdjacentPositions();

            // Shuffle for randomness
            java.util.Collections.shuffle(adjacent);

            // Try each adjacent position
            for (Position pos : adjacent) {
                if (gameMap.isValidPosition(pos) && gameMap.isPositionFree(pos)) {
                    return pos;
                }
            }

            // No valid moves, stay in place
            return unit.getPosition();
        }

        @Override
        public String getStrategyName() {
            return "RandomMovement";
        }
    }