package terminal;

public final class ConsoleUI {
    private ConsoleUI() {}

    // ANSI styles
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";

    // Colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static void title(String text) {
        String line = "\n" + CYAN + BOLD + "┌" + "─".repeat(Math.max(8, text.length() + 4)) + "┐" + RESET;
        String mid = CYAN + BOLD + "│ " + YELLOW + BOLD + text + CYAN + BOLD + " │" + RESET;
        String end = CYAN + BOLD + "└" + "─".repeat(Math.max(8, text.length() + 4)) + "┘" + RESET;
        System.out.println(line);
        System.out.println(mid);
        System.out.println(end);
    }

    public static void divider() {
        System.out.println(CYAN + "─".repeat(40) + RESET);
    }

    public static void menuItem(int index, String text) {
        System.out.println(BLUE + BOLD + index + ". " + RESET + WHITE + text + RESET);
    }

    public static void prompt(String text) {
        System.out.print(MAGENTA + BOLD + text + " " + RESET);
    }

    public static void success(String text) {
        System.out.println(GREEN + "✅ " + text + RESET);
    }

    public static void error(String text) {
        System.out.println(RED + "❌ " + text + RESET);
    }

    public static void warn(String text) {
        System.out.println(YELLOW + "⚠️  " + text + RESET);
    }

    public static void info(String text) {
        System.out.println(CYAN + "ℹ️  " + text + RESET);
    }

    public static String badge(String text) {
        return BOLD + YELLOW + text + RESET;
    }
}

