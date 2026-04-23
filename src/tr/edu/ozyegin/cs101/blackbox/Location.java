package tr.edu.ozyegin.cs101.blackbox;

import java.util.Objects;

public class Location {
    private final int n;
    private final static int MAX = 63;

    private Location(int n) {
        this.n = n;
    }

    public static Location locationFromLinearPosition(int n) {
        if (n < 0 || n > MAX) return null;
        return new Location(n);
    }

    public static Location locationFromString(String location) {
        int row = location.charAt(0) - 'A';
        int column = location.charAt(1) - '1';
        return locationFromLinearPosition(8 * row + column);
    }

    public static Location locationFromRowAndColumn(int row, int column) {
        if (row < 0 || row > 7 || column < 0 || column > 7) return null;
        return locationFromLinearPosition(8 * row + column);
    }

    public Location locationTowards(Direction dir) {
        return locationFromRowAndColumn(this.getRow() + dir.getDeltaRow(), this.getColumn() + dir.getDeltaColumn());
    }

    public int getRow() {
        return this.n / 8;
    }

    public int getColumn() {
        return this.n % 8;
    }

    public int getN() {
        return n;
    }

    @Override
    public String toString() {
        return "" + (char)(getRow() + 'A') + (char)(getColumn() + '1');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return n == location.n;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n);
    }
}