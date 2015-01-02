/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmi.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.Timer;
import org.rmi.common.ServerInterface;

/**
 *
 * @author Viktor
 */
public class User {
public String username;
public String sessionID;
public ServerInterface server;
public TimeOutHandler timer;
public JFrame currentWindow;

    public User(String username, String sessionID, ServerInterface server, JFrame currentWindow ) {
        this.username = username;
        this.sessionID = sessionID;
        this.server = server;
        this.currentWindow = currentWindow;
    }
    
    public User() {
        this.username = "blabla";
        this.sessionID = "1234";
    try {
        this.server = (ServerInterface) Naming.lookup("rmi://127.0.0.1/Server");
    } catch (NotBoundException ex) {
        Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MalformedURLException ex) {
        Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
    } catch (RemoteException ex) {
        Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
}
