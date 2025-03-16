package battleship;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = new Board();

        board.print();
        Ship[] ships = new Ship[ShipType.values().length];
        for (int i = 0; i < ShipType.values().length; i++) {
            ShipType shipType = ShipType.values()[i];
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
            ships[i] = ship;

            board.print();
        }
        board.setShips(ships);

        System.out.println("The game starts!");
        board.setMasked(true);
        System.out.println();
        board.print();
        System.out.println();

        gameLoop:
        while (true) {
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
            HitType hit = board.hit(coordinates);
            board.print();
            System.out.println();
            switch (hit) {
                case MISS -> {
                    System.out.println("You missed. Try again:");
                }
                case HIT_SHIP -> {
                    System.out.println("You hit a ship! Try again:");
                }
                case SANK_SHIP -> {
                    System.out.println("You sank a ship! Specify a new target:");
                }
                case SANK_LAST_SHIP -> {
                    System.out.println("You sank the last ship. You won. Congratulations!");
                    break gameLoop;
                }
            }
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
    private final CellType[][] grid;
    private Ship[] ships;
    private boolean masked;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 10;
    public static final String ROW_LABELS = "ABCDEFGHIJ";

    Board() {
        this.grid = new CellType[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(this.grid[i], CellType.FOG_OF_WAR);
        }
        this.ships = new Ship[ShipType.values().length];
        this.masked = false;
    }

    public void setShips(Ship[] ships) {
        this.ships = ships;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }

    public void print() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < HEIGHT; i++) {
            StringBuilder row = new StringBuilder();
            row.append(ROW_LABELS.charAt(i));
            for (int j = 0; j < WIDTH; j++) {
                row.append(" ");
                row.append(grid[i][j].getSymbol(this.masked));
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

    public HitType hit(Coordinates coordinates) {
        int row = coordinates.getRow();
        int column = coordinates.getColumn();
        Ship hitShip = null;
        boolean allSunk = true;
        for (Ship ship : this.ships) {
            if (ship.includes(coordinates)) {
                hitShip = ship;
                this.grid[row][column] = CellType.HIT_SHIP;
                boolean sankShip = true;
                for (Coordinates step : hitShip.getCoordinatesPair().getSteps()) {
                    if (this.grid[step.getRow()][step.getColumn()] != CellType.HIT_SHIP) {
                        sankShip = false;
                        break;
                    }
                }
                if (sankShip) {
                    hitShip.sink();
                }
            }
            if (!ship.hasSunk()) {
                allSunk = false;
            }
        }
        if (hitShip == null) {
            this.grid[row][column] = CellType.MISS;
            return HitType.MISS;
        }
        if (allSunk) {
            return HitType.SANK_LAST_SHIP;
        }
        if (hitShip.hasSunk()) {
            return HitType.SANK_SHIP;
        }
        return HitType.HIT_SHIP;
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

        char getSymbol(boolean masked) {
            if (masked && this.symbol == 'O') {
                return '~';
            } else {
                return this.symbol;
            }
        }
    }
}

class ShipSurroundingsNotEmptyException extends Exception {
    public ShipSurroundingsNotEmptyException(String message) {
        super(message);
    }
}

// HIT_TYPE
enum HitType {
    MISS,
    HIT_SHIP,
    SANK_SHIP,
    SANK_LAST_SHIP
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

    public boolean equals(Coordinates other) {
        return this.row == other.row && this.column == other.column;
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
    private boolean hasSunk;

    Ship(String coordinatesString, ShipType type)
            throws InvalidShipCoordinatesException, InvalidShipLengthException {
        CoordinatesPair coordinatesPair = Ship.stringToCoordinatesPair(coordinatesString);
        if (coordinatesPair.getDistance() + 1 != type.getLength()) {
            throw new InvalidShipLengthException("Invalid coordinates: " + coordinatesString);
        }
        this.coordinates = coordinatesPair;
        this.type = type;
        this.hasSunk = false;
    }

    public void sink() {
        this.hasSunk = true;
    }

    public boolean hasSunk() {
        return this.hasSunk;
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

    public boolean includes(Coordinates coordinates) {
        for (Coordinates step : this.getCoordinatesPair().getSteps()) {
            if (step.equals(coordinates)) {
                return true;
            }
        }
        return false;
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

