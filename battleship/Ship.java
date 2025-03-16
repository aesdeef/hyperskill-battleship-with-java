package battleship;

public class Ship {
    private final CoordinatesPair coordinates;
    private final ShipType type;

    Ship(String coordinatesString, ShipType type)
            throws InvalidShipCoordinatesException, InvalidShipLengthException {
        CoordinatesPair coordinatesPair = Ship.stringToCoordinatesPair(coordinatesString);
        if (coordinatesPair.getDistance() + 1 != type.getLength()) {
            throw new InvalidShipLengthException("Invalid coordinates: " + coordinatesString);
        }
        this.coordinates = coordinatesPair;
        this.type = type;
    }

    private static CoordinatesPair stringToCoordinatesPair(String coordinatesString)
            throws InvalidShipCoordinatesException {
        String[] coordinates = coordinatesString.split(" ");
        if (coordinates.length != 2) {
            throw new InvalidShipCoordinatesException("Invalid coordinates: " + coordinatesString);
        }
        Coordinates start;
        Coordinates end;
        try {
            start = new Coordinates(coordinates[0]);
            end = new Coordinates(coordinates[1]);
        } catch (InvalidCoordinatesException e) {
            throw new InvalidShipCoordinatesException("Invalid coordinates: " + coordinatesString);
        }
        CoordinatesPair coordinatesPair;
        try {
            coordinatesPair = new CoordinatesPair(start, end);
        } catch (InvalidCoordinatesPairException e) {
            throw new InvalidShipCoordinatesException("Invalid coordinates: " + coordinatesString);
        }
        return coordinatesPair;
    }

    public CoordinatesPair getCoordinatesPair() {
        return this.coordinates;
    }

    @SuppressWarnings("unused")
    public ShipType getType() {
        return this.type;
    }
}

class InvalidShipLengthException extends Exception {
    InvalidShipLengthException(String message) {
        super(message);
    }
}

class InvalidShipCoordinatesException extends Exception {
    InvalidShipCoordinatesException(String message) {
        super(message);
    }
}