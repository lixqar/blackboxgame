package tr.edu.ozyegin.cs101.blackbox;

import java.util.*;

public class Main {
    private static final Set<Integer> usedEntryLasers = new HashSet<>();
    private static final Set<Integer> usedExitLasers = new HashSet<>();
    private static final List<Integer> availableLasers = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BallGuesser guesser = new BallGuesser();

        // Initialize all possible lasers (1-32)
        for (int i = 1; i <= 32; i++) {
            availableLasers.add(i);
        }
        Collections.shuffle(availableLasers);

        System.out.println(" BLACK BOX SOLVER ");

        while (guesser.remainingCandidates() > 1 && !availableLasers.isEmpty()) {
            System.out.println("\n Remaining possibilities: " + guesser.remainingCandidates());

            int laserId = getNextSmartLaser(guesser);
            usedEntryLasers.add(laserId);

            System.out.println("\n I fired laser from edge: " + laserId);
            System.out.print("What happened? (H or exit position): ");
            String result = scanner.next().trim();

            if (!result.equalsIgnoreCase("H")) {
                try {
                    int exitId = Integer.parseInt(result);

                    // Allow reflections (laserId == exitId)
                    if (exitId != laserId && (usedEntryLasers.contains(exitId) || usedExitLasers.contains(exitId))) {
                        System.out.println(" Position " + exitId + " was already used, try again.");
                        availableLasers.add(laserId);
                        usedEntryLasers.remove(laserId);
                        continue;
                    }

                    usedExitLasers.add(exitId);

                    if (exitId == laserId) {
                        System.out.println(" Reflection detected at position: " + exitId);
                    }

                } catch (NumberFormatException e) {
                    System.out.println(" Invalid input! Enter 'H' or a number (1-32).");
                    availableLasers.add(laserId);
                    usedEntryLasers.remove(laserId);
                    continue;
                }
            }

            try {
                guesser.applyFeedback(laserId, result);
                System.out.println("Remaining after feedback: " + guesser.remainingCandidates());
                if (guesser.remainingCandidates() == 0) {
                    System.out.println("No candidates left! Are you sure the feedback was correct?");
                    return;
                }
            } catch (IllegalStateException e) {
                System.out.println("\n Inconsistent feedback detected! Game over.");
                return;
            }
        }

        if (guesser.remainingCandidates() > 1) {
            System.out.println("\n Couldn't find unique solution with given info.");
            System.out.println("Possible reasons:");
            System.out.println("- Not enough lasers fired");
            System.out.println("- Inconsistent feedback was given");
            return;
        }

        BlackBox solution = guesser.getSolutionIfUnique();
        System.out.println("\n SOLVED! ");
        displayFinalGridWithBalls(solution);
    }

    private static int getNextSmartLaser(BallGuesser guesser) {
        int bestLaser = -1;
        int bestScore = Integer.MAX_VALUE;

        for (Integer laser : availableLasers) {
            // allow reuse for reflections only
            if (usedExitLasers.contains(laser) && !usedEntryLasers.contains(laser)) continue;

            Map<String, Integer> feedbackCounts = new HashMap<>();

            for (BlackBox candidate : guesser.getCandidates()) {
                EdgeLocation entry = EdgeLocation.getEdgeLocationWithId(laser);
                EdgeLocation exit = new Laser(entry).findExitEdgeLocation(candidate);
                String feedback = exit == null ? "H" : String.valueOf(exit.getEdgeLocationId());
                feedbackCounts.put(feedback, feedbackCounts.getOrDefault(feedback, 0) + 1);
            }

            int worstCase = feedbackCounts.values().stream().max(Integer::compare).orElse(0);

            if (worstCase < bestScore) {
                bestScore = worstCase;
                bestLaser = laser;
            }
        }

        // fallback random laser if nothing found
        if (bestLaser == -1) {
            for (Integer laser : availableLasers) {
                if (!usedExitLasers.contains(laser) || usedEntryLasers.contains(laser)) {
                    bestLaser = laser;
                    break;
                }
            }
        }

        if (bestLaser == -1) {
            throw new IllegalStateException("No valid lasers available");
        }

        availableLasers.remove((Integer) bestLaser);
        return bestLaser;
    }

    private static void displayFinalGridWithBalls(BlackBox solution) {
        if (solution == null) {
            System.out.println("No solution found");
            return;
        }

        System.out.print("     ");
        for (int i = 1; i <= 8; i++) System.out.printf("%2d ", i);
        System.out.println();

        for (int row = 0; row < 8; row++) {
            System.out.printf("%2d [ ", 32 - row);
            for (int col = 0; col < 8; col++) {
                Location loc = Location.locationFromRowAndColumn(row, col);
                System.out.print(solution.ballExistsInLocation(loc) ? "● " : "· ");
            }
            System.out.printf("] %2d\n", 9 + row);
        }

        System.out.print("     ");
        for (int i = 24; i >= 17; i--) System.out.printf("%2d ", i);
        System.out.println("\n");

        System.out.println("Ball locations:");
        for (int i = 0; i < 64; i++) {
            Location loc = Location.locationFromLinearPosition(i);
            if (solution.ballExistsInLocation(loc)) {
                System.out.print(loc + " ");
            }
        }
        System.out.println();
    }
}