package view.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import controller.CommandParser;
import controller.gui.GUIControllerBridge;
import model.CalendarManager;
import view.gui.components.CalendarSelector;
import view.gui.components.MonthView;
import view.gui.components.NavigationBar;
import view.gui.utils.DialogUtils;

/**
 * The CalendarFrame class is the main JFrame window of the Calendar GUI
 * application.
 * It integrates and organizes various GUI components:
 * - NavigationBar (Top bar for navigation & actions).
 * - CalendarSelector (Left panel to select calendars).
 * - MonthView (Center panel showing month grid).
 * - Calendar information label (Bottom bar showing current calendar & timezone).
 * Responsibilities:
 * - Initialize the UI layout.
 * - Manage the default calendar creation.
 * - Handle month shifting logic and synchronize the view.
 * - Update calendar information dynamically when user switches calendars.
 */
public class CalendarFrame extends JFrame {
  private final CommandParser parser;
  private final GUIControllerBridge bridge;
  private final IGUIView viewController;
  private MonthView monthView;
  private JLabel calendarInfoLabel;
  private NavigationBar navBar;

  /**
   * Constructs the main application window (CalendarFrame) and sets up
   * the initial GUI components, controllers, and default calendar.
   *
   * @param parser          the CommandParser for parsing user commands
   * @param calendarManager the CalendarManager for calendar data operations
   * @param initialTimezone the initial timezone for the default calendar
   */
  public CalendarFrame(CommandParser parser, CalendarManager calendarManager,
                       String initialTimezone) {
    this.parser = parser;
    this.bridge = new GUIControllerBridge(parser, calendarManager);
    this.viewController = new GUIViewImpl(bridge, parser);

    initializeUI();

    // Initialize the default calendar and handle any errors
    try {
      bridge.createDefaultCalendar(initialTimezone);
    } catch (Exception e) {
      DialogUtils.showOutputDialog("Error", "Failed to create default calendar: " +
              e.getMessage());
    }

    updateCalendarInfo("Default", initialTimezone);
    updateNavigationBarMonth();
  }

  public GUIControllerBridge getBridge() {
    return bridge;
  }

  public IGUIView getViewController() {
    return viewController;
  }

  private void initializeUI() {
    setTitle("Calendar Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel(new BorderLayout());

    navBar = new NavigationBar(parser, this);
    mainPanel.add(navBar, BorderLayout.NORTH);

    mainPanel.add(new CalendarSelector(parser, this, bridge), BorderLayout.WEST);

    monthView = new MonthView(viewController);
    mainPanel.add(monthView, BorderLayout.CENTER);

    calendarInfoLabel = new JLabel();
    mainPanel.add(calendarInfoLabel, BorderLayout.SOUTH);

    add(mainPanel);
  }

  public void updateCalendarInfo(String calendarName, String timezone) {
    calendarInfoLabel.setText("Calendar: " + calendarName + " | Timezone: " + timezone);
  }

  public void shiftMonth(int offset) {
    monthView.shiftMonth(offset);
    updateNavigationBarMonth();
  }

  private void updateNavigationBarMonth() {
    String monthYear = monthView.getCurrentMonthYear();
    navBar.updateMonthYear(monthYear);
  }
}