package org.rmi.client;

import java.rmi.RemoteException;

public class Client {
    

    public static void main(String[] args) throws RemoteException {
        gui.WelcomeScreen frame = new gui.WelcomeScreen();
        frame.setVisible(true);//open the login screen
    }
}