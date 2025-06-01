package startapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import controller.CommandParser;
import controller.InvalidCommandException;
import model.CalendarManager;
import model.MultiCalendarEventStorage;
import view.gui.CalendarFrame;

/**
 * Main application class for the Calendar system that supports interactive,
 * headless, and GUI modes of operation.
 */
public class CalendarApp {
  /**
   * Main entry point for the Calendar application.
   *
   * @param args Command line arguments specifying the mode of operation
   * @throws InvalidCommandException if an invalid command is encountered
   */
  public static void main(String[] args) throws InvalidCommandException {
    if (args.length == 0) {
      // GUI mode when no arguments provided
      runGUIMode();
      return;
    }

    if (args.length < 2 || !args[0].equals("--mode")) {
      System.out.println("Usage: java CalendarApp [--mode interactive | " +
              "--mode headless <filename>]");
      return;
    }

    String operationMode = args[1].toLowerCase();
    switch (operationMode) {
      case "interactive":
        runInteractiveMode();
        break;
      case "headless":
        if (args.length < 3) {
          System.out.println("Error: Missing filename for headless mode.");
          return;
        }
        runHeadlessMode(args[2]);
        break;
      case "gui":
        runGUIMode();
        break;
      default:
        System.out.println("Error: Invalid mode. Use 'interactive', 'headless', or 'gui'.");
    }
  }

  /**
   * Runs the application in GUI mode.
   */
  private static void runGUIMode() {

    SwingUtilities.invokeLater(() -> {
      try {
        MultiCalendarEventStorage storage = new MultiCalendarEventStorage();
        CalendarManager manager = new CalendarManager(storage);
        CommandParser parser = new CommandParser(manager);
        new CalendarFrame(parser, manager, "America/New_York").setVisible(true);
      } catch (Exception e) {
        System.err.println("Failed to initialize GUI: " + e.getMessage());
        e.printStackTrace();
      }
    });
  }

  /**
   * Runs the application in interactive mode, accepting commands from user input.
   *
   * @throws InvalidCommandException if an invalid command is entered
   */
  private static void runInteractiveMode() throws InvalidCommandException {
    MultiCalendarEventStorage multiCalendarEventStorage = new MultiCalendarEventStorage();
    CalendarManager calendarManager = new CalendarManager(multiCalendarEventStorage);
    CommandParser commandParser = new CommandParser(calendarManager);

    Scanner inputScanner = new Scanner(System.in);
    while (true) {
      System.out.print("Enter command: ");
      String userCommand = inputScanner.nextLine().trim();

      if (userCommand.equalsIgnoreCase("exit")) {
        System.out.println("Exiting interactive mode.");
        break;
      }

      if (!commandParser.executeCommand(userCommand)) {
        System.out.println("Command failed: " + userCommand);
      }
    }
  }

  /**
   * Runs the application in headless mode, reading commands from a file.
   *
   * @param inputFilename The name of the file containing commands to execute
   */
  private static void runHeadlessMode(String inputFilename) {
    MultiCalendarEventStorage multiCalendarEventStorage = new MultiCalendarEventStorage();
    CalendarManager calendarManager = new CalendarManager(multiCalendarEventStorage);
    CommandParser commandParser = new CommandParser(calendarManager);

    try (BufferedReader fileReader = new BufferedReader(new FileReader(inputFilename))) {
      String fileCommand;
      while ((fileCommand = fileReader.readLine()) != null) {
        fileCommand = fileCommand.trim();
        if (fileCommand.equalsIgnoreCase("exit")) {
          System.out.println("Exiting headless mode.");
          break;
        }

        try {
          if (!commandParser.executeCommand(fileCommand)) {
            System.out.println("Command failed: " + fileCommand);
          }
        } catch (InvalidCommandException e) {
          System.out.println("Error executing command: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.out.println("Error reading file: " + e.getMessage());
    }
  }
}