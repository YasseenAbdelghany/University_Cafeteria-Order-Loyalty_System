package app;

import terminal.LoggerFactory;
import terminal.MainConsole;
import Services.*;
import Enums.Category;
import Core.MenuItem;
import Values.Money;
import terminal.ServiceContainer;
import terminal.ConsoleUI;

import java.util.Scanner;
import java.util.logging.Logger;


public class tempCodeRunnerFile {
    private static final Logger logger = LoggerFactory.getLogger(tempCodeRunnerFile.class);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        surpriseWelcome();
        ServiceContainer container = new ServiceContainer();
        seedDemoMenu(container.getMenuManager());
        new MainConsole(container, scanner).run();
        scanner.close();
    }

    private static void surpriseWelcome() {
        String bannerTop = ConsoleUI.CYAN + ConsoleUI.BOLD + "\n╔════════════════════════════════════════════════╗" + ConsoleUI.RESET;
        String bannerMid1 = ConsoleUI.CYAN + ConsoleUI.BOLD + "║" + ConsoleUI.RESET
                + "  " + ConsoleUI.YELLOW + ConsoleUI.BOLD + "    🍽️  Welcome to CAFETERIA SYSTEM  🍕                    " + ConsoleUI.RESET
                + ConsoleUI.CYAN + ConsoleUI.BOLD + "  ║" + ConsoleUI.RESET;
        String bannerMid2 = ConsoleUI.CYAN + ConsoleUI.BOLD + "║" + ConsoleUI.RESET
                + "  " + ConsoleUI.MAGENTA + ConsoleUI.BOLD + "            Fresh • Fast • Friendly  " + ConsoleUI.BLUE + "✨                        " + ConsoleUI.RESET
                + ConsoleUI.CYAN + ConsoleUI.BOLD + "                ║" + ConsoleUI.RESET;
        String bannerBot = ConsoleUI.CYAN + ConsoleUI.BOLD + "╚════════════════════════════════════════════════╝\n" + ConsoleUI.RESET;
        System.out.println(bannerTop);
        System.out.println(bannerMid1);
        System.out.println(bannerMid2);
        System.out.println(bannerBot);
    }

    private static void seedDemoMenu(MenuManager menuManager) {
        try {
            if (menuManager.getAvailableItems().isEmpty()) {
                menuManager.addMenuItem(new MenuItem("Margherita Pizza", "Classic Italian pizza", new Money(25.0), Category.MAIN_COURSE));
                menuManager.addMenuItem(new MenuItem("Beef Burger", "Juicy beef burger with fries", new Money(20.0), Category.MAIN_COURSE));
                menuManager.addMenuItem(new MenuItem("Potato Chips", "Crispy potato chips", new Money(5.0), Category.SNACK));
                menuManager.addMenuItem(new MenuItem("Cola", "Refreshing cola drink", new Money(3.0), Category.DRINK));
                logger.info("Seeded demo menu items.");
            }
        }
        catch (Exception e) {
            logger.info("Menu seed skipped: " + e.getMessage());
        }

    }
}
