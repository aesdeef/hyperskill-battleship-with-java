package battleship;

public class CoordinatesPair {
    private final Coordinates start;
    private final Coordinates end;
    private final int distance;

    CoordinatesPair(Coordinates start, Coordinates end) throws InvalidCoordinatesPairException {
        this.start = start;
        this.end = end;
        if (start.sameRow(end)) {
            this.distance = Math.abs(start.getColumn() - end.getColumn());
        } else if (start.sameColumn(end)) {
            this.distance = Math.abs(start.getRow() - end.getRow());
        } else {
            throw new InvalidCoordinatesPairException("Start and end are not on the same line");
        }
    }

    public Coordinates getStart() {
        return this.start;
    }

    public Coordinates getEnd() {
        return this.end;
    }

    public int getDistance() {
        return this.distance;
    }

    public Coordinates[] getSteps() {
        Coordinates[] steps = new Coordinates[this.distance + 1];
        int stepX = (this.end.getRow() - this.start.getRow()) / this.distance;
        int stepY = (this.end.getColumn() - this.start.getColumn()) / this.distance;
        for (int i = 0; i < steps.length; i++) {
            try {
                steps[i] = new Coordinates(
                        this.start.getRow() + i * stepX,
                        this.start.getColumn() + i * stepY
                );
            } catch (InvalidCoordinatesException e) {
                throw new UnreachableCodeRuntimeException("CoordinatesPair.steps()");
            }
        }
        return steps;
    }
}

class InvalidCoordinatesPairException extends Exception {
    public InvalidCoordinatesPairException(String message) {
        super(message);
    }
}
