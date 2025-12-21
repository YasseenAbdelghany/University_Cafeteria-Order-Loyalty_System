package app.gui;

import javafx.application.Application;

/**
 * Main entry point for launching the JavaFX GUI application.
 * This class provides a separate entry point from the terminal-based Main.java.
 */
public class MainGUI {
    public static void main(String[] args) {
        System.out.println("Starting Cafeteria Management System (GUI Mode)...");
        Application.launch(CafeteriaGUIApp.class, args);
    }
}
