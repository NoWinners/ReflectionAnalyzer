package org.tak.runtime;

import org.tak.runtime.gui.ReflectorGUI;
import org.tak.runtime.servers.OSS;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: Tommy
 * 5/27/13
 */
public class RuntimeAnalyzer {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        Game game = new Game(new OSS());
        game.init();
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new GridLayout());
        jFrame.add(game.getClient());
        jFrame.setVisible(true);
        jFrame.setSize(700, 500);
        new ReflectorGUI(game).setVisible(true);
    }
}
