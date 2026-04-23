package tr.edu.ozyegin.cs101.blackbox;

public class Laser {
    private Location current;
    private Location next;
    private Direction direction;
    private final EdgeLocation firedFrom;

    public Laser(EdgeLocation firedFrom) {
        this.firedFrom = firedFrom;
        this.current = null;
        this.next = firedFrom.getAdjacentLocation();
        this.direction = firedFrom.getDirectionToAdjacentLocation();
    }

    public EdgeLocation findExitEdgeLocation(BlackBox blackBox) {
        while (true) {
            // If laser is about to exit the grid
            if (this.next == null) {
                return EdgeLocation.getEdgeLocationAdjacentTo(this.current, this.direction);
            }

            // Check for ball interactions
            boolean ballDeadAhead = blackBox.ballExistsInLocation(this.next);
            boolean ballToRight = blackBox.ballExistsInLocation(this.next.locationTowards(this.direction.right()));
            boolean ballToLeft = blackBox.ballExistsInLocation(this.next.locationTowards(this.direction.left()));

            if (ballDeadAhead) {
                // Direct hit: laser is absorbed
                return null;
            } else if (ballToLeft && ballToRight) {
                // Reflection due to balls on both diagonal sides
                return this.firedFrom;
            } else if (ballToRight) {
                // Deflection: turn left
                if (this.current == null) {
                    return this.firedFrom;
                }
                this.direction = this.direction.left();
                this.next = this.current.locationTowards(this.direction);
            } else if (ballToLeft) {
                // Deflection: turn right
                if (this.current == null) {
                    return this.firedFrom;
                }
                this.direction = this.direction.right();
                this.next = this.current.locationTowards(this.direction);
            } else {
                // No balls nearby: keep moving forward
                this.current = this.next;
                this.next = this.next.locationTowards(this.direction);
            }
        }
    }
}