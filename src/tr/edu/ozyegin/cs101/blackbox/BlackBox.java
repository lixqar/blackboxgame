package tr.edu.ozyegin.cs101.blackbox;

import java.util.Objects;

public class BlackBox {
    private final Location[] ballLocations;

    public BlackBox(Location ballOne, Location ballTwo, Location ballThree, Location ballFour) {
        this.ballLocations = new Location[]{ballOne, ballTwo, ballThree, ballFour};
    }

    public boolean ballExistsInLocation(Location location) {
        for (Location ball : ballLocations) {
            if (ball.equals(location)) return true;
        }
        return false;
    }

    public boolean laserEntryExitMatches(EdgeLocation entry, EdgeLocation exit) {
        Laser laser = new Laser(entry);
        EdgeLocation actualExit = laser.findExitEdgeLocation(this);
        return Objects.equals(exit, actualExit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            Location loc = Location.locationFromLinearPosition(i);
            sb.append(ballExistsInLocation(loc) ? "*" : ".");
            if (i % 8 == 7) sb.append("\n");
        }
        return sb.toString();
    }
}
