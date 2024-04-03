package ui;

import core.ListenerThread;
import utils.Config;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow{

    ListenerThread mainThread;

    private final JFrame mainFrame;

    public MainWindow(ListenerThread listenerThread) {
        this.mainThread = listenerThread;
        String title = "Commodore64 Server - Panel v"+ Config.APP_VERSION;
        this.mainFrame = new JFrame(title);
        this.loadInitialInterface();

        int WINDOW_WIDTH = 600;
        int WINDOW_HEIGHT = 400;
        this.mainFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
    }

    public void loadInitialInterface() {
        // Set button for on/off
        JButton bootBtn = new JButton("Start");
        bootBtn.setBounds(20, 20 , 100, 30);
        bootBtn.addActionListener(e -> {

            if (!mainThread.isAlive()) {
                mainThread.start();
            } else {
                System.out.println("interrupting");
                mainThread.interrupt();
            }
        });
        this.mainFrame.add(bootBtn);
    }
}
