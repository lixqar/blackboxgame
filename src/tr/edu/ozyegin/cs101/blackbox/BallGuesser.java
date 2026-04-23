package tr.edu.ozyegin.cs101.blackbox;

import java.util.List;

public class BallGuesser {
    private List<BlackBox> candidates;

    public BallGuesser() {
        this.candidates = BlackBoxStream.streamBlackBoxes().toList();
    }

    public int remainingCandidates() {
        return this.candidates.size();
    }

    public void applyFeedback(int laserId, String result) {
        EdgeLocation entry = EdgeLocation.getEdgeLocationWithId(laserId);
        EdgeLocation exit = null;
        if (!result.equalsIgnoreCase("H")) {
            int exitId = Integer.parseInt(result);
            exit = EdgeLocation.getEdgeLocationWithId(exitId);
        }

        EdgeLocation finalExit = exit; // for lambda use
        this.candidates = this.candidates.stream()
                .filter(box -> box.laserEntryExitMatches(entry, finalExit))
                .toList();
    }

    public BlackBox getSolutionIfUnique() {
        return candidates.size() == 1 ? candidates.getFirst() : null;
    }

    // New method for optimization
    public List<BlackBox> getCandidates() {
        return this.candidates;
    }
}