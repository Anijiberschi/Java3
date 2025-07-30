import javafx.scene.paint.Color;

public enum Team {
    NORTH(Color.LIGHTBLUE, "North"),
    SOUTH(Color.LIGHTCORAL, "South");

    private final Color teamColor;
    private final String displayName;
    private Position cityPosition; // Will be set by GameMap

    /**
     * Constructor for Team enum.
     * @param teamColor The base color for this team
     * @param displayName The display name for this team
     */
    Team(Color teamColor, String displayName) {
        this.teamColor = teamColor;
        this.displayName = displayName;
    }

    /**
     * Gets the team's base color.
     * @return The team color
     */
    public Color getTeamColor() {
        return teamColor;
    }

    /**
     * Gets the team's display name.
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the team's city position.
     * @return The city position
     */
    public Position getCityPosition() {
        return cityPosition;
    }

    /**
     * Sets the team's city position.
     * @param position The city position
     */
    public void setCityPosition(Position position) {
        this.cityPosition = position;
    }

    /**
     * Gets the opposite team.
     * @return The enemy team
     */
    public Team getOpposite() {
        return this == NORTH ? SOUTH : NORTH;
    }

    /**
     * Gets a darker version of the team color for units.
     * @return Darker team color
     */
    public Color getDarkerColor() {
        return teamColor.darker();
    }
}