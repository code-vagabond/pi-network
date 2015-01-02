/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmi.client;

import gui.WelcomeScreen;
import java.rmi.RemoteException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Viktor
 */
public class TimeOutHandler extends TimerTask{
    User current;
    
    public TimeOutHandler(User u) {
        this.current = u;
        current.timer = this;
    }

    @Override
    public void run() {
        try {
            if(current.server.isTimedOut(current.sessionID)){
                JOptionPane.showMessageDialog(current.currentWindow, "Session abgelaufen");
                current.currentWindow.dispose();
                WelcomeScreen welcomeScreen = new WelcomeScreen();
                welcomeScreen.setVisible(true);
                cancel();
            } 
        } catch (RemoteException ex) {
            Logger.getLogger(TimeOutHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
