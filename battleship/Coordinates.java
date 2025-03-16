package battleship;

public class Coordinates {
    private final int row;
    private final int column;

    Coordinates(int row, int column) throws InvalidCoordinatesException {
        if (row < 0 || row >= Board.HEIGHT) {
            throw new InvalidCoordinatesException("Invalid row: " + row);
        }
        if (column < 0 || column >= Board.WIDTH) {
            throw new InvalidCoordinatesException("Invalid column: " + column);
        }
        this.row = row;
        this.column = column;
    }

    Coordinates(String coordinates) throws InvalidCoordinatesException {
        String rowString = coordinates.substring(0, 1);
        String columnString = coordinates.substring(1);
        if (!Board.ROW_LABELS.contains(rowString)) {
            throw new InvalidCoordinatesException("Invalid row: " + rowString);
        }
        int column;
        try {
            column = Integer.parseInt(columnString) - 1;
        } catch (NumberFormatException e) {
            throw new InvalidCoordinatesException("Invalid column: " + columnString);
        }
        if (column < 0 || column >= Board.WIDTH) {
            throw new InvalidCoordinatesException("Invalid column: " + columnString);
        }
        this.row = Board.ROW_LABELS.indexOf(rowString);
        this.column = column;
    }

    public boolean equals(Coordinates other) {
        return this.row == other.row && this.column == other.column;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public boolean sameRow(Coordinates other) {
        return this.row == other.row;
    }

    public boolean sameColumn(Coordinates other) {
        return this.column == other.column;
    }
}

class InvalidCoordinatesException extends Exception {
    InvalidCoordinatesException(String message) {
        super(message);
    }
}
