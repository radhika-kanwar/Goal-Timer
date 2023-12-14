/*
 * Goal Timer -
 * This GUI based Java project takes a list of goals that the individual wants to accomplish.
 * It further takes the deadline for the project in minutes.
 * Next, the goal is added to the table.
 * After the timer has been started for a particular goal, it counts down and displays the time up message.
*/

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GoalTimer {
    private static DefaultTableModel goalTableModel;
    private static List<Goal> goals = new ArrayList<>();
    private static Timer appTimer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Goal Timer Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Goal Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel goalLabel = new JLabel("Enter your goal:");
        JTextField goalInput = new JTextField();

        JLabel timerLabel = new JLabel("Enter the timer for the goal (minutes):");
        JTextField timerInput = new JTextField();

        JButton addButton = new JButton("Add goal to the list");
        addButton.addActionListener(e -> addGoal(goalInput.getText(), timerInput.getText()));

        inputPanel.add(goalLabel);
        inputPanel.add(goalInput);
        inputPanel.add(timerLabel);
        inputPanel.add(timerInput);
        inputPanel.add(addButton);

        // Goal Table Panel
        String[] columnNames = {"Goal", "Timer", "Countdown"};
        goalTableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable goalTable = new JTable(goalTableModel);
        goalTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering

        // Use a custom renderer for the "Countdown" column
        goalTable.getColumnModel().getColumn(2).setCellRenderer(new CountdownRenderer());
        goalTable.setRowHeight(30); // Increase row height for better readability

        // Center-align the "Goal" and "Timer" columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        goalTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Goal column
        goalTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Timer column

        JScrollPane tableScrollPane = new JScrollPane(goalTable);

        // Timer Control Panel
        JPanel controlPanel = new JPanel();
        JButton startTimerButton = new JButton("Start Timer");
        startTimerButton.addActionListener(e -> startSelectedGoalTimer(goalTable.getSelectedRow()));
        controlPanel.add(startTimerButton);

        // Add panels to the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Apply a more modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        frame.getContentPane().add(mainPanel);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void addGoal(String goalDescription, String timerValue) {
        try {
            int timerInMinutes = Integer.parseInt(timerValue);
            int timerInSeconds = timerInMinutes * 60;
            Goal goal = new Goal(goalDescription, timerInSeconds);
            goals.add(goal);
            updateGoalTable();
            JOptionPane.showMessageDialog(null, "Goal '" + goal.getDescription() + "' added successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid timer value (numeric).", "Invalid Timer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void startSelectedGoalTimer(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < goals.size()) {
            Goal selectedGoal = goals.get(selectedRow);
            appTimer = new Timer(1000, new ActionListener() {
                int remainingTime = selectedGoal.getTimer();

                @Override
                public void actionPerformed(ActionEvent e) {
                    remainingTime--;

                    // Format the remaining time explicitly
                    String formattedTime = formatTime(remainingTime);
                    goalTableModel.setValueAt(formattedTime, selectedRow, 2);

                    if (remainingTime == 0) {
                        appTimer.stop();
                        JOptionPane.showMessageDialog(null, "Time's up for goal: " + selectedGoal.getDescription());
                    }
                }
            });
            appTimer.start();
        } else {
            JOptionPane.showMessageDialog(null, "Please select a goal from the table.", "No Goal Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static String formatTime(int seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        if (seconds < 1800) {
            seconds += 30 * 60; // Add 30 minutes if remaining time is less than 30 minutes
        }
        return sdf.format(seconds * 1000);
    }

    private static void updateGoalTable() {
        goalTableModel.setRowCount(0); // Clear existing rows
        for (Goal goal : goals) {
            int remainingTime = goal.getTimer();
            goalTableModel.addRow(new Object[]{goal.getDescription(), goal.getTimer() / 60, formatTime(remainingTime)});
        }
    }

    private static class Goal {
        private String description;
        private int timer; // in seconds

        public Goal(String description, int timer) {
            this.description = description;
            this.timer = timer;
        }

        public String getDescription() {
            return description;
        }

        public int getTimer() {
            return timer;
        }
    }

    private static class CountdownRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            // Override to display the value as a String
            setText((value == null) ? "" : value.toString());
            setHorizontalAlignment(JLabel.CENTER); // Center-align the text
        }
    }
}
