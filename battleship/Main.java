package battleship;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = new Board();

        board.print();
        System.out.println("Enter the coordinates of the ship:");
        String input = scanner.nextLine();
        String[] stringCoordinates = input.split(" ");
        int[][] coordinates = Main.convertCoordinates(stringCoordinates);
        if (coordinates == null) {
            System.out.println("Error!");
            return;
        }
        Main.printParts(coordinates);
    }

    private static int[][] convertCoordinates(String[] coordinates) {
        if (coordinates.length != 2) {
            return null;
        }

        int[][] result = new int[2][2];
        for (int i = 0; i < coordinates.length; i++) {
            int[] converted = Coordinates.alphanumericToInts(coordinates[i]);
            if (converted == null) {
                return null;
            }
            result[i] = converted;
        }
        if (result[0][0] == result[1][0]) {
            // same row
            return result;
        }
        if (result[0][1] == result[1][1]) {
            // same column
            return result;
        }
        // different rows and columns
        return null;
    }

    private static void printParts(int[][] coordinates) {
        int length;
        int[][] parts;
        if (coordinates[0][0] == coordinates[1][0]) {
            // same row
            length = Math.abs(coordinates[0][1] - coordinates[1][1]) + 1;
            parts = new int[length][2];
            int step = coordinates[0][1] > coordinates[1][1] ? -1 : 1;
            for (int i = 0; i < length; i++) {
                parts[i][0] = coordinates[0][0];
                parts[i][1] = coordinates[0][1] + i * step;
            }
        } else if (coordinates[0][1] == coordinates[1][1]) {
            // same column
            length = Math.abs(coordinates[0][0] - coordinates[1][0]) + 1;
            parts = new int[length][2];
            int step = coordinates[0][0] > coordinates[1][0] ? -1 : 1;
            for (int i = 0; i < length; i++) {
                parts[i][0] = coordinates[0][0] + i * step;
                parts[i][1] = coordinates[0][1];
            }
        } else {
            return;
        }
        System.out.println("Length: " + length);
        System.out.print("Parts:");
        for (int[] part : parts) {
            System.out.print(" " + Coordinates.intsToAlphanumeric(part));
        }
        System.out.println();
    }
}

class Board {
    char[][] grid;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 10;
    public static final String ROW_LABELS = "ABCDEFGHIJ";

    Board() {
        this.grid = new char[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(this.grid[i], '~');
        }
    }

    public void print() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < HEIGHT; i++) {
            StringBuilder row = new StringBuilder();
            row.append(ROW_LABELS.charAt(i));
            for (int j = 0; j < WIDTH; j++) {
                row.append(" ");
                row.append(grid[i][j]);
            }
            System.out.println(row);
        }
    }
}

class Coordinates {
    public static int[] alphanumericToInts(String coordinates) {
        int[] result = new int[2];
        String rowLetter = coordinates.substring(0, 1);
        if (!Board.ROW_LABELS.contains(rowLetter)) {
            return null;
        }
        result[0] = Board.ROW_LABELS.indexOf(rowLetter);
        try {
            result[1] = Integer.parseInt(coordinates.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (result[1] < 0 || result[1] >= Board.HEIGHT) {
            return null;
        }
        return result;
    }

    public static String intsToAlphanumeric(int[] coordinates) {
        for (int coordinate : coordinates) {
            if (coordinate < 0 || coordinate >= Board.WIDTH) {
                return null;
            }
        }
        char row = Board.ROW_LABELS.charAt(coordinates[0]);
        int column = coordinates[1] + 1;
        return String.format("%c%d", row, column);
    }
}