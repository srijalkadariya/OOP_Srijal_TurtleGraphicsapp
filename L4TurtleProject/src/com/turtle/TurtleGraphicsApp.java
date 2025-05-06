package com.turtle;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class TurtleGraphicsApp extends LBUGraphics {
    
    private boolean firstClear = false;
    private boolean hasUnsavedChanges = false;  // Track if drawing has unsaved changes
    private static final int MAX_MOVEMENT = 1000;
    private static final int MAX_SIZE = 500;
    private JTextArea commandLog;
    private JScrollPane logScrollPane;
    private List<String> commandHistory = new ArrayList<>();
    
    public void superAbout() {
    	super.about();
    }

    public TurtleGraphicsApp() {
        JFrame mainFrame = new JFrame("Turtle Graphics Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // Create File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadImageItem = new JMenuItem("Load Image");
        JMenuItem saveImageItem = new JMenuItem("Save Image");
        JMenuItem loadCommandsItem = new JMenuItem("Load Commands");
        JMenuItem saveCommandsItem = new JMenuItem("Save Commands");
        JMenuItem clearLogItem = new JMenuItem("Clear Log");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        // Create Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem commandsHelpItem = new JMenuItem("Show Commands");
        
        // Create Info menu
        JMenu infoMenu = new JMenu("Info");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showInfoDialog());
        
        // Add action listeners
        loadImageItem.addActionListener(e -> loadDrawingWithDialog());
        saveImageItem.addActionListener(e -> saveDrawingWithDialog());
        loadCommandsItem.addActionListener(e -> loadCommandsWithDialog());
        saveCommandsItem.addActionListener(e -> saveCommandsWithDialog());
        clearLogItem.addActionListener(e -> clearCommandLog());
        exitItem.addActionListener(e -> exitApplication());
        commandsHelpItem.addActionListener(e -> showHelpDialog());
        
        // Add items to File menu
        fileMenu.add(loadImageItem);
        fileMenu.add(saveImageItem);
        fileMenu.addSeparator();
        fileMenu.add(loadCommandsItem);
        fileMenu.add(saveCommandsItem);
        fileMenu.addSeparator();
        fileMenu.add(clearLogItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        infoMenu.add(aboutItem);
        // Add items to Help menu
        helpMenu.add(commandsHelpItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(infoMenu);
        
        // Set menu bar to frame
        mainFrame.setJMenuBar(menuBar);
        
        // Create the command log area
        commandLog = new JTextArea();
        commandLog.setEditable(false);
        commandLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        commandLog.setBackground(new Color(240, 240, 240));
        commandLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        logScrollPane = new JScrollPane(commandLog);
        logScrollPane.setPreferredSize(new Dimension(600, 100));
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Command Log"));
        
        // Create a panel to hold both the turtle graphics and the log
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(this, BorderLayout.CENTER);
        contentPanel.add(logScrollPane, BorderLayout.SOUTH);
        
        // Add content panel to frame
        mainFrame.add(contentPanel, BorderLayout.CENTER);
        
        // Add window listener for exit confirmation
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitApplication();
            }
        });
        
        mainFrame.pack();
        mainFrame.setVisible(true);
        
        // Initialize turtle
        setPenColour(Color.red);
        setStroke(2);
        setPenState(true);
        displayMessage("Welcome! Type 'clear' when ready to start drawing.");
        logCommand("Application started");
    }

    private void logCommand(String command) {
        commandLog.append("> " + command + "\n");
        commandLog.setCaretPosition(commandLog.getDocument().getLength());
    }

    private void clearCommandLog() {
        commandLog.setText("");
        logCommand("Command log cleared");
    }

    private void exitApplication() {
        if (hasUnsavedChanges) {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "You have unsaved changes. Do you want to save before exiting?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                saveDrawingWithDialog();
                System.exit(0);
            } else if (confirm == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            // If CANCEL, do nothing and return to application
        } else {
            System.exit(0);
        }
    }

    private void clearDisplay() {
        if (!firstClear) {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "This will clear the initial animation. Continue?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm != JOptionPane.YES_OPTION) {
                displayMessage("Clear operation cancelled");
                return;
            }
            firstClear = true;
        }
        
        // Check for unsaved changes before clearing
        if (hasUnsavedChanges) {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "You have unsaved changes. Do you want to save before clearing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                saveDrawingWithDialog();
                // Continue with clear only if save was successful
                if (!hasUnsavedChanges) {
                    performClear();
                }
            } else if (confirm == JOptionPane.NO_OPTION) {
                performClear();
            }
            // If CANCEL, do nothing and return
        } else {
            performClear();
        }
    }
    private void showInfoDialog() {
        String infoText = "<html><div style='text-align: center;'>" +
            "<h2>Turtle Graphics Application</h2>" +
            "<p>Version 1.0</p>" +
            "<p>Developed by <b>Srijal</b></p>" +
            "<p>Copyright © 2023 All Rights Reserved</p>" +
            "<hr>" +
            "<p>This application allows you to create turtle graphics drawings" +
            " using simple commands. Features include:</p>" +
            "<ul style='text-align: left;'>" +
            "<li>Drawing shapes (circles, squares, triangles)</li>" +
            "<li>Color and pen width control</li>" +
            "<li>Save/load drawings</li>" +
            "<li>Command history</li>" +
            "</ul></div></html>";
        
        JOptionPane.showMessageDialog(
            null,
            infoText,
            "Application Information",
            JOptionPane.INFORMATION_MESSAGE
        );
        logCommand("info - viewed application information");
    }
    
    private void performClear() {
        clear();
        commandHistory.clear();
        int centerY = getHeight() / 2;
        setxPos(50);
        setyPos(centerY);
        pointTurtle(0);
        repaint();
        hasUnsavedChanges = false;  // Reset unsaved changes flag after clearing
        displayMessage("Canvas ready - turtle reset to left side");
        logCommand("clear");
    }

    private void saveDrawingWithDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Drawing As");
        fileChooser.setSelectedFile(new File("drawing.png"));
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            if (!filePath.toLowerCase().endsWith(".png")) {
                fileToSave = new File(filePath + ".png");
            }
            
            try {
                BufferedImage image = getBufferedImage();
                ImageIO.write(image, "PNG", fileToSave);
                hasUnsavedChanges = false;  // Reset unsaved changes flag after saving
                displayMessage("Drawing saved to:\n" + fileToSave.getAbsolutePath());
                logCommand("saveimage " + fileToSave.getName());
            } catch (Exception e) {
                displayMessage("Error saving image: " + e.getMessage());
                logCommand("Error saving image: " + e.getMessage());
            }
        }
    }

    private void loadDrawingWithDialog() {
        // Check for unsaved changes before loading
        if (hasUnsavedChanges) {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "You have unsaved changes. Do you want to save before loading a new image?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                saveDrawingWithDialog();
                // Continue with load only if save was successful
                if (!hasUnsavedChanges) {
                    performLoadDrawing();
                }
            } else if (confirm == JOptionPane.NO_OPTION) {
                performLoadDrawing();
            }
            // If CANCEL, do nothing and return
            return;
        } else {
            performLoadDrawing();
        }
    }
    
    private void performLoadDrawing() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Drawing");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(fileToLoad);
                if (image != null) {
                    setBufferedImage(image);
                    firstClear = true;
                    hasUnsavedChanges = false;  // Reset unsaved changes flag after loading
                    displayMessage("Drawing loaded from:\n" + fileToLoad.getAbsolutePath());
                    logCommand("loadimage " + fileToLoad.getName());
                } else {
                    displayMessage("Invalid image file");
                    logCommand("Error: Invalid image file");
                }
            } catch (Exception e) {
                displayMessage("Error loading image: " + e.getMessage());
                logCommand("Error loading image: " + e.getMessage());
            }
        }
    }

    private void saveCommandsWithDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Commands As");
        fileChooser.setSelectedFile(new File("commands.txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                // Save only drawing commands from history (exclude save/load operations)
                for (String cmd : commandHistory) {
                    String cmdLower = cmd.toLowerCase();
                    boolean isSaveLoadCommand = 
                        cmdLower.startsWith("save") || 
                        cmdLower.startsWith("load") || 
                        cmdLower.equals("exit");
                    
                    if (!isSaveLoadCommand) {
                        writer.println(cmd);
                    }
                }
                displayMessage("Drawing commands saved to:\n" + fileToSave.getAbsolutePath());
                logCommand("savecommands " + fileToSave.getName());
            } catch (Exception e) {
                displayMessage("Error saving commands: " + e.getMessage());
                logCommand("Error saving commands: " + e.getMessage());
            }
        }
    }

    private void loadCommandsWithDialog() {
        // Check for unsaved changes before loading commands
        if (hasUnsavedChanges) {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "You have unsaved changes. Do you want to save before loading commands?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                saveDrawingWithDialog();
                // Continue with load only if save was successful
                if (!hasUnsavedChanges) {
                    performLoadCommands();
                }
            } else if (confirm == JOptionPane.NO_OPTION) {
                performLoadCommands();
            }
            // If CANCEL, do nothing and return
            return;
        } else {
            performLoadCommands();
        }
    }
    
    private void performLoadCommands() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Commands File");
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                // Clear existing history when loading new commands
                commandHistory.clear();
                
                // Clear and reset before loading commands
                performClear();
                reset();
                
                // Create a temporary list of commands to execute
                List<String> commandsToExecute = new ArrayList<>();
                
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("//")) {
                        // Filter out save and load operations
                        String cmdLower = line.toLowerCase();
                        boolean isSaveLoadCommand = 
                            cmdLower.startsWith("save") || 
                            cmdLower.startsWith("load") || 
                            cmdLower.equals("exit");
                        
                        if (!isSaveLoadCommand) {
                            commandsToExecute.add(line);
                        }
                    }
                }
                
                // Execute the filtered commands
                for (String cmd : commandsToExecute) {
                    processCommand(cmd);
                    // Add small delay between commands for visualization
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                displayMessage("Commands loaded and executed from:\n" + fileToLoad.getAbsolutePath());
                logCommand("loadcommands " + fileToLoad.getName());
                
                // Mark as having unsaved changes since new drawing was created
                hasUnsavedChanges = true;
            } catch (Exception e) {
                displayMessage("Error loading commands: " + e.getMessage());
                logCommand("Error loading commands: " + e.getMessage());
            }
        }
    }

    private void showHelpDialog() {
        JTextArea helpTextArea = new JTextArea();
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setText(getHelpText());
        
        JScrollPane scrollPane = new JScrollPane(helpTextArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(
            null,
            scrollPane,
            "Turtle Graphics Help",
            JOptionPane.INFORMATION_MESSAGE
        );
        logCommand("help");
    }

    private String getHelpText() {
        return "=== TURTLE GRAPHICS COMMANDS ===\n\n" +
               "MOVEMENT COMMANDS:\n" +
               "  move [distance] or fd <distance> - Move forward (default 100)\n" +
               "  reverse [distance] or bk <distance> - Move backward (default 100)\n" +
               "  right <angle> or rt <angle> - Turn right (degrees, default 90)\n" +
               "  left <angle> or lt <angle> - Turn left (degrees, default 90)\n\n" +
               
               "DRAWING CONTROL:\n" +
               "  penup or pu - Lift pen (stop drawing)\n" +
               "  pendown or pd - Lower pen (start drawing)\n" +
               "  setpencolor <color> - Set pen color\n" +
               "  clear - Clear canvas (with confirmation first time)\n" +
               "  reset - Reset turtle position\n\n" +
               
               "SHAPES:\n" +
               "  circle <radius> - Draw circle\n" +
               "  square <size> - Draw square\n" +
               "  polygon <sides> <size> - Draw regular polygon\n\n" +
               "  triangle <size> - Draw equilateral triangle\n" +
               "  triangle <side1>,<side2>,<side3> - Draw any triangle\n" +
               "  faststar <size>    - rainbow star\n" +
               
               "FILE OPERATIONS:\n" +
               "  save/saveimage - Save drawing as PNG\n" +
               "  load/loadimage - Load drawing from PNG\n" +
               "  savecommands - Save all executed commands to text file\n" +
               "  loadcommands - Load and execute commands from text file\n\n" +
               
               "OTHER COMMANDS:\n" +
               "  setspeed <1-10> - Set animation speed (1=slow, 10=fast)\n" +
               "  help - Show this help\n" +
               "  exit - Exit the program";
    }
    
    public void about() {
    	clear();
        reset();
        setStroke(15);
        setTurtleSpeed(2);
        setPenColour(Color.BLUE);
        displayMessage("I am Srijal");

        setPenState(false);
        right(90);
        forward(100);
        forward(100);
        forward(100);
        left(90);
        forward(-50);
        right(90);
        //letter S
        setPenState(true);
        setPenColour(Color.RED);
        forward(50);
        left(90);
        forward(50);
        left(90);
        forward(50);
        right(90);
        forward(50);
        right(90);
        forward(50);
        
        //letter R
        setPenState(false);
        forward(-100);
        right(90);
        setPenState(true);
        setPenColour(Color.BLUE);
        forward(100);
        right(90);
        forward(50);
        right(90);
        forward(50);
        right(90);
        forward(50);
        left(90);
        left(45);
        forward(75);
        setPenState(false);
        left(45);
        forward(80);
        setPenState(true);
        setPenColour(Color.YELLOW);
        forward(50);
        forward(-25);
        left(90);
        forward(100);
        left(90);
        forward(25);
        forward(-50);
        setPenState(false);
        forward(-75);
        setPenState(true);
        setPenColour(Color.GREEN);
        forward(-50);
        forward(25);
        left(90);
        forward(100);
        right(90);
        forward(35);
        right(90);
        forward(35);
        setPenState(false);
        forward(-35);
        right(90);
        forward(35);
        forward(80);
        left(75);
        setPenState(true);
        setPenColour(Color.MAGENTA);
        forward(100);
        right(75);
        right(75);
        forward(100);
        left(75);
        setPenState(false);
        forward(80);
        setPenState(true);
        setPenColour(Color.CYAN);
        forward(80);
        forward(-80);
        left(90);
        forward(100);
        setPenState(false);
        forward(60);
        left(90);
        forward(50);
      
        displayMessage("This app belongs to SRIJAL");
    }
    
    @Override
    public void processCommand(String command) {
        logCommand(command);
        commandHistory.add(command);
        
        try {
            String[] parts = command.trim().split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty()) return;
            
            String cmd = parts[0].toLowerCase();
            
            switch(cmd) {
            	case "about":
            	superAbout();
            	break;
            	case "name":
            	about();
            	break;
                case "move":
                case "forward":
                case "fd":
                    if (parts.length == 2) {
                        try {
                            int distance = Integer.parseInt(parts[1]);
                            if (distance > MAX_MOVEMENT) {
                                displayMessage("Error: Distance too large (max " + MAX_MOVEMENT + ")");
                            } else {
                                forward(distance);
                                checkBounds();
                                hasUnsavedChanges = true;  // Mark as having unsaved changes
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid number format");
                        }
                    } else if (parts.length == 1) {
                        forward(100);
                        checkBounds();
                        hasUnsavedChanges = true;  // Mark as having unsaved changes
                        displayMessage("Moved forward with no parameter (default 100 px)");
                    } else {
                        displayMessage("Error: Too many parameters. Usage: forward [distance]");
                    }
                    break;
                    
                case "reverse":
                case "backward":
                case "bk":
                    if (parts.length == 2) {
                        try {
                            int distance = Integer.parseInt(parts[1]);
                            if (distance > MAX_MOVEMENT) {
                                displayMessage("Error: Distance too large (max " + MAX_MOVEMENT + ")");
                            } else {
                                forward(-distance);
                                checkBounds();
                                hasUnsavedChanges = true;  // Mark as having unsaved changes
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid number format");
                        }
                    } else if (parts.length == 1) {
                        forward(-100);
                        checkBounds();
                        hasUnsavedChanges = true;  // Mark as having unsaved changes
                        displayMessage("Moved backward with no parameter (default 100 px)");
                    } else {
                        displayMessage("Error: Too many parameters. Usage: backward [distance]");
                    }
                    break;
                    
                case "right":
                case "rt":
                    if (parts.length == 2) {
                        try {
                            int angle = Integer.parseInt(parts[1]);
                            if (angle < 0 || angle > 360) {
                                displayMessage("Error: Angle must be between 0-360 degrees");
                            } else {
                                right(angle);
                                // No need to mark as unsaved for rotation alone
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid number format");
                        }
                    } else if (parts.length == 1) {
                        right(90);
                        displayMessage("Turned right with no parameter (default 90 degrees)");
                    } else {
                        displayMessage("Error: Too many parameters. Usage: right [angle]");
                    }
                    break;
                    
                case "left":
                case "lt":
                    if (parts.length == 2) {
                        try {
                            int angle = Integer.parseInt(parts[1]);
                            if (angle < 0 || angle > 360) {
                                displayMessage("Error: Angle must be between 0-360 degrees");
                            } else {
                                left(angle);
                                // No need to mark as unsaved for rotation alone
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid number format");
                        }
                    } else if (parts.length == 1) {
                        left(90);
                        displayMessage("Turned left with no parameter (default 90 degrees)");
                    } else {
                        displayMessage("Error: Too many parameters. Usage: left [angle]");
                    }
                    break;
                case "triangle":
                    if (parts.length == 2) {
                        // Handle both formats: "triangle 100" and "triangle 100,100,150"
                        String argument = parts[1].trim();
                        
                        if (argument.contains(",")) {
                            // Handle three side lengths
                            try {
                                String[] sides = argument.split("\\s*,\\s*"); // Handles spaces around commas
                                if (sides.length != 3) {
                                    displayMessage("Syntax: triangle <side1>,<side2>,<side3>");
                                    break;
                                }
                                
                                int side1 = Integer.parseInt(sides[0]);
                                int side2 = Integer.parseInt(sides[1]);
                                int side3 = Integer.parseInt(sides[2]);
                                
                                // Validate triangle
                                if (side1 + side2 <= side3 || 
                                    side1 + side3 <= side2 || 
                                    side2 + side3 <= side1) {
                                    displayMessage("Error: Invalid triangle sides");
                                } else {
                                    drawTriangleBySides(side1, side2, side3);
                                    hasUnsavedChanges = true;
                                }
                            } catch (NumberFormatException e) {
                                displayMessage("Error: All sides must be numbers");
                            }
                        } else {
                            // Handle single size (equilateral)
                            try {
                                int size = Integer.parseInt(argument);
                                drawEquilateralTriangle(size);
                                hasUnsavedChanges = true;
                            } catch (NumberFormatException e) {
                                displayMessage("Error: Size must be a number");
                            }
                        }
                    } else {
                        displayMessage("Syntax: triangle <size> OR triangle <side1>,<side2>,<side3>");
                    }
                    break;
                case "penup":
                case "pu":
                    setPenState(false);
                    displayMessage("Pen lifted");
                    break;
                    
                case "pendown":
                case "pd":
                    setPenState(true);
                    displayMessage("Pen lowered");
                    break;
                    
                case "setpencolor":
                    if (parts.length == 2) {
                        setPenColor(parts[1]);
                    } else {
                        displayMessage("Syntax: setpencolor <color>");
                    }
                    break;
                case "pen":
                    if (parts.length == 4) {
                        try {
                            int red = Integer.parseInt(parts[1]);
                            int green = Integer.parseInt(parts[2]);
                            int blue = Integer.parseInt(parts[3]);
                            
                            // Validate each component is between 0-255
                            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                                displayMessage("Error: RGB values must be between 0-255");
                            } else {
                                Color newColor = new Color(red, green, blue);
                                setPenColour(newColor);
                                displayMessage("Pen color set to RGB(" + red + "," + green + "," + blue + ")");
                                hasUnsavedChanges = true;
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid RGB values - must be numbers");
                        }
                    } else {
                        displayMessage("Syntax: pen <red> <green> <blue> (values 0-255)");
                    }
                    break;
                case "penwidth":
                    if (parts.length == 2) {
                        try {
                            int width = Integer.parseInt(parts[1]);
                            if (width <= 0) {
                                displayMessage("Error: Pen width must be positive");
                            } else if (width > 20) {  // Reasonable maximum
                                displayMessage("Error: Pen width too large (max 20)");
                            } else {
                                setStroke(width);
                                displayMessage("Pen width set to " + width);
                                hasUnsavedChanges = true;
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid pen width - must be a number");
                        }
                    } else {
                        displayMessage("Syntax: penwidth <width> (positive number)");
                    }
                    break;
                    
                case "clear":
                    clearDisplay();
                    break;
                    
                case "reset":
                    reset();
                    firstClear = true;
                    // Don't change the unsaved status as reset only affects position
                    displayMessage("Turtle reset");
                    break;
                    
                case "circle":
                    if (parts.length == 2) {
                        try {
                            int radius = Integer.parseInt(parts[1]);
                            if (radius <= 0) {
                                displayMessage("Error: Radius must be positive");
                            } else if (radius > MAX_SIZE) {
                                displayMessage("Error: Radius too large (max " + MAX_SIZE + ")");
                            } else {
                                circle(radius);
                                hasUnsavedChanges = true;  // Mark as having unsaved changes
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid number format");
                        }
                    } else {
                        displayMessage("Syntax: circle <radius>");
                    }
                    break;
                    
                case "square":
                    if (parts.length == 2) {
                        try {
                            int size = Integer.parseInt(parts[1]);
                            if (size <= 0) {
                                displayMessage("Error: Size must be positive");
                            } else if (size > MAX_SIZE) {
                                displayMessage("Error: Size too large (max " + MAX_SIZE + ")");
                            } else {
                                drawSquare(size);
                                hasUnsavedChanges = true;  // Mark as having unsaved changes
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid number format");
                        }
                    } else {
                        displayMessage("Syntax: square <size>");
                    }
                    break;
                   
                    
                case "save":
                case "saveimage":
                    saveDrawingWithDialog();
                    break;
                    
                case "load":
                case "loadimage":
                    loadDrawingWithDialog();
                    break;
                    
                case "savecommands":
                    saveCommandsWithDialog();
                    break;
                    
                case "loadcommands":
                    loadCommandsWithDialog();
                    break;
                    
                case "setspeed":
                    if (parts.length == 2) {
                        try {
                            int speed = Integer.parseInt(parts[1]);
                            if (speed < 1 || speed > 10) {
                                displayMessage("Error: Speed must be between 1-10");
                            } else {
                                setTurtleSpeed(speed);
                                displayMessage("Speed set to " + speed);
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Invalid number format");
                        }
                    } else {
                        displayMessage("Syntax: setspeed <1-10>");
                    }
                    break;
                case "faststar":
                    if (parts.length == 2) {
                        drawFastRainbowStar(Integer.parseInt(parts[1]));
                    }
                    break;   
                case "help":
                    showHelpDialog();
                    break;
                    
                case "exit":
                    exitApplication();
                    break;
                    
                default:
                    displayMessage("Unknown command: " + cmd);
            }
        } catch (NumberFormatException e) {
            displayMessage("Please enter a valid number");
        } catch (ArrayIndexOutOfBoundsException e) {
            displayMessage("Missing parameter for command");
        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
        }
    }

    private void setPenColor(String colorStr) {
        try {
            Color color;
            if (colorStr.startsWith("#") && colorStr.length() == 7) {
                color = Color.decode(colorStr);
            }
            else {
                switch(colorStr.toLowerCase()) {
                    case "red": color = Color.RED; break;
                    case "green": color = Color.GREEN; break;
                    case "blue": color = Color.BLUE; break;
                    case "black": color = Color.BLACK; break;
                    case "white": color = Color.WHITE; break;
                    case "yellow": color = Color.YELLOW; break;
                    case "cyan": color = Color.CYAN; break;
                    case "magenta": color = Color.MAGENTA; break;
                    default: throw new IllegalArgumentException("Unknown color");
                }
            }
            setPenColour(color);
            displayMessage("Pen color set to " + colorStr);
        } catch (Exception e) {
            displayMessage("Invalid color: " + colorStr + ". Try: red, green, blue, etc. or #RRGGBB");
        }
    }

    private void checkBounds() {
        int x = getxPos();
        int y = getyPos();
        int width = getWidth();
        int height = getHeight();
        
        if (x < 0 || x > width || y < 0 || y > height) {
            displayMessage("Warning: Turtle is outside canvas bounds!");
            
            if (x < 0) {
                setxPos(width);
            } else if (x > width) {
                setxPos(0);
            }
            
            if (y < 0) {
                setyPos(height);
            } else if (y > height) {
                setyPos(0);
            }
            
            repaint();
        }
    }

    private void drawSquare(int size) {
        setPenState(true);
        for (int i = 0; i < 4; i++) {
            forward(size);
            right(90);
            repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    private void drawEquilateralTriangle(int size) {
        setPenState(true);
        for (int i = 0; i < 3; i++) {
            forward(size);
            left(120);  // 120° turns for equilateral triangle
        }
    }

    private void drawTriangleBySides(int a, int b, int c) {
        // Validate triangle inequality
        if (a + b <= c || a + c <= b || b + c <= a) {
            displayMessage("Error: Invalid triangle sides");
            return;
        }

        setPenState(true);
        
        // Calculate angles using law of cosines
        double angleC = Math.acos((a*a + b*b - c*c) / (2.0*a*b));
        double angleB = Math.acos((a*a + c*c - b*b) / (2.0*a*c));
        double angleA = Math.PI - angleB - angleC;
        
        // Convert to integer degrees
        int turn1 = (int) Math.round(Math.toDegrees(Math.PI - angleC));
        int turn2 = (int) Math.round(Math.toDegrees(Math.PI - angleA));
        
        // Draw triangle
        forward(a);
        left(turn1);
        forward(b);
        left(turn2);
        forward(c);
        
        // Final adjustment to return to original heading
        left(180 - turn1 - turn2);
    }
    private void drawFastRainbowStar(int size) {
        setPenState(true);
        try { setStroke(10); } catch (Exception e) {} // Try thick lines
        
        // Reduced steps and no delay for speed
        for (int i = 0; i < 36; i++) { // Half the steps
            setPenColour(Color.getHSBColor(i/36f, 1, 1));
            forward(size * (i+1)/36);
            left(144); // 5-point star angle
            
            // Minimal delay for slight animation
            try { Thread.sleep(10); } catch (Exception e) {} 
        }
    }
    
}