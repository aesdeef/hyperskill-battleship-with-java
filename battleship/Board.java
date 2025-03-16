package battleship;

import java.util.Arrays;

public class Board {
    private final CellType[][] grid;
    private Ship[] ships;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 10;
    public static final String ROW_LABELS = "ABCDEFGHIJ";

    Board() {
        this.grid = new CellType[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(this.grid[i], CellType.FOG_OF_WAR);
        }
        this.ships = new Ship[ShipType.values().length];
    }

    public void setShips(Ship[] ships) {
        this.ships = ships;
    }

    public void print(boolean masked) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < HEIGHT; i++) {
            StringBuilder row = new StringBuilder();
            row.append(ROW_LABELS.charAt(i));
            for (int j = 0; j < WIDTH; j++) {
                row.append(" ");
                row.append(grid[i][j].getSymbol(masked));
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

