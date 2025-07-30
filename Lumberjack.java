/**
 * Lumberjack unit - collects wood from trees at rate 2/second.
 */
class Lumberjack extends Collector {
    public Lumberjack(Position position, Team team) {
        super(position, team, UnitType.LUMBERJACK, ResourceType.WOOD);
    }
}