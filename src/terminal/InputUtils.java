package terminal;

import java.util.Scanner;

public final class InputUtils {
    private InputUtils() {}

    public static int readInt(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please try again: ");
            }
        }
    }

    public static double readDouble(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please try again: ");
            }
        }
    }

    public static String readLine(Scanner scanner, String prompt) {
        if (prompt != null && !prompt.isBlank()) System.out.print(prompt);
        return scanner.nextLine();
    }
}

