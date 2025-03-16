package battleship;

import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static int player = 1;
    static Board board1;
    static Board board2;

    public static void main(String[] args) {
        System.out.println("Player 1, place your ships on the game field");
        System.out.println();
        Main.board1 = Main.boardSetup();
        Main.switchPlayer();
        System.out.println();
        System.out.println("Player 2, place your ships to the game field");
        System.out.println();
        Main.board2 = Main.boardSetup();
        Main.switchPlayer();

        gameLoop:
        while (true) {
            Main.printBoards();
            System.out.printf("Player %d, it's your turn:%n", Main.player);
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

            HitType hit = Main.opponentsBoard().hit(coordinates);
            System.out.println();
            switch (hit) {
                case MISS -> System.out.println("You missed!");
                case HIT_SHIP -> System.out.println("You hit a ship!");
                case SANK_SHIP -> System.out.println("You sank a ship!");
                case SANK_LAST_SHIP -> {
                    System.out.println("You sank the last ship. You won. Congratulations!");
                    break gameLoop;
                }
            }
            System.out.println();
            Main.switchPlayer();
        }
    }

    private static void switchPlayer() {
        System.out.println("Press Enter and pass the move to another player");
        System.out.print("...");
        Main.scanner.nextLine();
        Main.player = Main.player == 1 ? 2 : 1;
    }

    private static Board boardSetup() {
        Scanner scanner = Main.scanner;
        Board board = new Board();
        board.print(false);
        Ship[] ships = new Ship[ShipType.values().length];
        for (int i = 0; i < ShipType.values().length; i++) {
            ShipType shipType = ShipType.values()[i];
            System.out.println();
            System.out.printf(
                    "Enter the coordinates of the %s (%d cells):%n",
                    shipType.getName(),
                    shipType.getLength()
            );
            System.out.println();
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
            board.print(false);
        }
        board.setShips(ships);
        return board;
    }

    private static void printBoards() {
        Main.opponentsBoard().print(true);
        System.out.println("---------------------");
        Main.ownBoard().print(false);
    }

    private static Board ownBoard() {
        if (Main.player == 1) {
            return Main.board1;
        } else {
            return Main.board2;
        }
    }

    private static Board opponentsBoard() {
        if (Main.player == 1) {
            return Main.board2;
        } else {
            return Main.board1;
        }
    }
}

class UnreachableCodeRuntimeException extends RuntimeException {
    public UnreachableCodeRuntimeException(String message) {
        super(message);
    }
}
