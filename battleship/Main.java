package battleship;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = new Board();

        board.print();
        for (ShipType shipType : ShipType.values()) {
            System.out.println();
            System.out.printf(
                    "Enter the coordinates of the %s (%d cells):%n",
                    shipType.getName(),
                    shipType.getLength()
            );
            Ship ship = null;
            do {
                String input = scanner.nextLine();
                try {
                    ship = new Ship(input, shipType);
                    try {
                        board.placeShip(ship);
                    } catch (ShipSurroundingsNotEmptyException e) {
                        ship = null;
                        System.out.println("Error! You placed it too close to another one. Try again:");
                    }
                } catch (InvalidShipCoordinatesException e) {
                    System.out.println("Error! Wrong ship location! Try again:");
                } catch (InvalidShipLengthException e) {
                    System.out.println("Error! Wrong length of the Submarine! Try again:");
                }
            } while (ship == null);

            board.print();
        }

        System.out.println("The game starts!");
        System.out.println();
        board.print();
        System.out.println();
        System.out.println("Take a shot!");
        System.out.println();
        Coordinates coordinates = null;
        do {
            String input = scanner.nextLine();
            try {
                coordinates = new Coordinates(input);
            } catch (InvalidCoordinatesException e) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                System.out.println();
            }
        } while (coordinates == null);
        boolean hit = board.hit(coordinates);
        board.print();
        System.out.println();
        if (hit) {
            System.out.println("You hit a ship!");
            System.out.println();
        } else {
            System.out.println("You missed!");
            System.out.println();
        }
    }
}

class UnreachableCodeRuntimeException extends RuntimeException {
    public UnreachableCodeRuntimeException(String message) {
        super(message);
    }
}

// BOARD
// public class Board {
class Board {
    CellType[][] grid;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 10;
    public static final String ROW_LABELS = "ABCDEFGHIJ";

    Board() {
        this.grid = new CellType[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(this.grid[i], CellType.FOG_OF_WAR);
        }
    }

    public void print() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < HEIGHT; i++) {
            StringBuilder row = new StringBuilder();
            row.append(ROW_LABELS.charAt(i));
            for (int j = 0; j < WIDTH; j++) {
                row.append(" ");
                row.append(grid[i][j].getSymbol());
            }
            System.out.println(row);
        }
    }

    public void placeShip(Ship ship) throws ShipSurroundingsNotEmptyException {
        Coordinates shipStart = ship.getCoordinatesPair().getStart();
        Coordinates shipEnd = ship.getCoordinatesPair().getEnd();
        Coordinates[] shipCells = ship.getCoordinatesPair().getSteps();

        // Check if the area is empty
        int minX = Math.min(shipStart.getRow(), shipEnd.getRow()) - 1;
        int maxX = Math.max(shipStart.getRow(), shipEnd.getRow()) + 1;
        int minY = Math.min(shipStart.getColumn(), shipEnd.getColumn()) - 1;
        int maxY = Math.max(shipStart.getColumn(), shipEnd.getColumn()) + 1;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // skip coordinates that are out of bounds
                try {
                    new Coordinates(x, y);
                } catch (InvalidCoordinatesException e) {
                    continue;
                }
                if (grid[x][y] != CellType.FOG_OF_WAR) {
                    throw new ShipSurroundingsNotEmptyException("Cell [" + x + "][" + y + "] not empty");
                }
            }
        }

        // Place the ship on the board
        for (Coordinates coordinates : shipCells) {
            this.grid[coordinates.getRow()][coordinates.getColumn()] = CellType.YOUR_SHIP;
        }
    }

    public boolean hit(Coordinates coordinates) {
        int row = coordinates.getRow();
        int column = coordinates.getColumn();
        boolean hit = this.grid[row][column] == CellType.YOUR_SHIP;
        if (hit) {
            this.grid[row][column] = CellType.HIT_SHIP;
        } else {
            this.grid[row][column] = CellType.MISS;
        }
        return hit;
    }

    private enum CellType {
        FOG_OF_WAR('~'),
        YOUR_SHIP('O'),
        HIT_SHIP('X'),
        MISS('M');

        private final char symbol;

        CellType(char symbol) {
            this.symbol = symbol;
        }

        char getSymbol() {
            return this.symbol;
        }
    }
}

class ShipSurroundingsNotEmptyException extends Exception {
    public ShipSurroundingsNotEmptyException(String message) {
        super(message);
    }
}

// COORDINATES
// public class Coordinates {
class Coordinates {
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

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public int[] getCoordinates() {
        return new int[]{row, column};
    }

    public String getCoordinatesString() {
        char row = Board.ROW_LABELS.charAt(this.row);
        int column = this.column + 1;
        return String.format("%c%d", row, column);
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

// COORDINATES_PAIR
// public class CoordinatesPair {
class CoordinatesPair {
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

// SHIP
// public class Ship {
class Ship {
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

// SHIP_TYPE
// public enum ShipType {
enum ShipType {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    private final String name;
    private final int length;

    ShipType(String name, int size) {
        this.name = name;
        this.length = size;
    }

    public String getName() {
        return this.name;
    }

    public int getLength() {
        return this.length;
    }
}

