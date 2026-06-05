package vthreads;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import vthreads.lib.Histogram;
import vthreads.task.ScanContext;
import vthreads.util.Pair;

public class View extends JFrame {
    
    private ScanContext scanContext;
    private JTextArea outputArea;
    private JButton startButton;
    private JButton stopButton;
    private JTextField directoryField;
    private JTextField maxSizeField;
    private JTextField numBandsField;
    private AtomicBoolean scanGoing = new AtomicBoolean();
    
    public View() {
        setTitle("Directory Scanner - vthreads");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Scan Settings"));
        
        inputPanel.add(new JLabel("Directory:"));
        directoryField = new JTextField(25);
        inputPanel.add(directoryField);
        
        inputPanel.add(new JLabel("Max Size:"));
        maxSizeField = new JTextField("1000000", 10);
        inputPanel.add(maxSizeField);
        
        inputPanel.add(new JLabel("Bands:"));
        numBandsField = new JTextField("10", 5);
        inputPanel.add(numBandsField);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        startButton = new JButton("Start Scan");
        startButton.addActionListener(e -> startScan());
        buttonPanel.add(startButton);
        
        stopButton = new JButton("Stop Scan");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopScan());
        buttonPanel.add(stopButton);
        
        // Output panel
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        
        outputArea = new JTextArea(15, 80);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(outputPanel);
        
        add(mainPanel);
    }
    
    private void startScan() {
        String directory = directoryField.getText().trim();
        if (directory.isEmpty()) {
            outputArea.setText("Error: Please enter a directory path\n");
            return;
        }
        
        try {
            long maxSize = Long.parseLong(maxSizeField.getText().trim());
            int numBands = Integer.parseInt(numBandsField.getText().trim());
            
            scanContext = new ScanContext(Path.of(directory), numBands, maxSize);
            
            startButton.setEnabled(false);
            directoryField.setEnabled(false);
            maxSizeField.setEnabled(false);
            numBandsField.setEnabled(false);
            stopButton.setEnabled(true);
            
            outputArea.setText("Scanning directory: " + directory + "\n");

            scanContext.startScan();
            scanGoing.compareAndExchange(false, true);
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(() -> {
                if(!scanGoing.get() || scanContext.isScanOver()) {
                    exec.shutdown();
                    startButton.setEnabled(true);
                    directoryField.setEnabled(true);
                    maxSizeField.setEnabled(true);
                    numBandsField.setEnabled(true);
                    stopButton.setEnabled(false);
                }
                SwingUtilities.invokeLater(this::displayResults);
            }, 0, 100, TimeUnit.MILLISECONDS);

            
        } catch (NumberFormatException e2) {
            outputArea.setText("Error: Invalid input format\n");
        }
    }
    
    private void stopScan() {
        if (scanContext != null) {
            scanContext.stopScan();
            scanGoing.compareAndExchange(true, false);
            outputArea.append("Stop requested...\n");
            startButton.setEnabled(true);
            directoryField.setEnabled(true);
            maxSizeField.setEnabled(true);
            numBandsField.setEnabled(true);
        }
    }
    
    private void displayResults() {
        if (scanContext == null) return;
        outputArea.setText("");
        Histogram hist = scanContext.getHistogram();
        outputArea.append("Total files: " + hist.getTotalFiles() + "\n");
        outputArea.append("Total directories: " + hist.getDirectoryCount() + "\n");
        outputArea.append("\nFile size distribution:\n");
        
        for (Entry<Pair<Long>, Integer> entry : hist.getDistribution()) {
            outputArea.append("  Band [" + formatBytes(entry.getKey().floor()) + " - " + 
                            formatBytes(entry.getKey().ceiling()) + "]: " + 
                            entry.getValue() + " files\n");
        }
    }

    private String formatBytes(long bytes) {
        if (bytes == 0) return "0 B";
        if (bytes == Long.MAX_VALUE) return "inf B";
        final long k = 1024;
        final String[] sizes = {"B", "KB", "MB", "GB", "TB"};
        int i = (int) Math.floor(Math.log(bytes) / Math.log(k));
        return String.format("%.2f %s", (double) bytes / Math.pow(k, i), sizes[i]);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View frame = new View();
            frame.setVisible(true);
        });
    }
}
