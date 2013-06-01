package org.tak;

import org.tak.runtime.Game;
import org.tak.runtime.gui.ReflectorGUI;
import org.tak.runtime.servers.BattleScape;

import javax.swing.*;
import java.awt.*;

/**
 * User: Tommy
 * 5/27/13
 */
public class ClientAnalyzer {

    public static void main(String[] args) {
        Game game = new Game(new BattleScape());
        game.init();
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new GridLayout());
        jFrame.add(game.getClient());
        jFrame.setVisible(true);
        jFrame.setSize(700, 500);
        new ReflectorGUI(game).setVisible(true);
    }
}
