/**
 * Miner unit - collects ore from rocks at rate 3/second.
 */
class Miner extends Collector {
    public Miner(Position position, Team team) {
        super(position, team, UnitType.MINER, ResourceType.ORE);
    }
}