package VSCodeCheckers;
import java.io.IOException;
import java.util.Scanner;

public class TwoDArray {

    public static void main(String[] args) {
        int[][] twoDArray = new int[7][14];
        twoDArray[3][3] = 42;
        /*
         * int[][] twoDArray = { { 1, 2, 3 },
         * { 4, 5, 6 },
         * { 7, 8, 9 } };
         */
        clearConsole();

        System.out.println("Printing the 2D array:");
        for (int[] row : twoDArray) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }

        try (Scanner info = new Scanner(System.in)) {
            int searchValue;
            System.out.print("Enter the value to search: ");
            searchValue = info.nextInt();

            boolean found = false;

            for (int[] row : twoDArray) {
                for (int value : row) {
                    if (value == searchValue) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }

            if (found) {
                System.out.println("The value has been found");
            } else {
                System.out.println("The value has not been found");
            }
        }
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
