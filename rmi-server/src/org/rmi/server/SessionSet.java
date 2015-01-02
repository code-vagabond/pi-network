/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmi.server;

import java.rmi.server.UID;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;

/**
 *
 * @author EndSub
 */
public class SessionSet {
    /**
     * Create a dictionary with username as key and a date as value, this will 
     * be used to decide whether a session is timed out or not (@see isTimedOut)
     */
    public Map <String, Date> userLastAccess  = new HashMap <> ();
    
    /**
     * Create a dictionary to save sessionID as key, username as value
     */
    public Map <String,String> sessionIDUser = new HashMap <>();
    

    public String genSession (String username){
        UID session = new UID();
        String sessionID = session.toString();
        sessionIDUser.put (sessionID, username); //assign sessionID to username
        userLastAccess.put(username, new Date());//assign date to access
        return sessionID;//returns the generated sessionID to the client

    }
    
        /**
     * @param sessionID
     * @return the username of a given sessionID by looking it up 
     * in the dictionary (@see sessionIDUser)
     */
    public String getUsername (String sessionID) {
        return sessionIDUser.get(sessionID);
    }
    
    
    
        /**
     * isTimedOut compares last access time with current time 
     * @param sessionID
     * @return whether a session is timed out or not
     */
    public Boolean isTimedOut (String sessionID){
        long lastAccess = userLastAccess.get(getUsername(sessionID)).getTime();
        long current = new Date ().getTime();
        if (current - lastAccess <= 240000){
            userLastAccess.put(getUsername(sessionID), new Date ());
            return false;
        }
        else {
            sessionIDUser.remove(sessionID);//clears sessionID, ready for session closure
            return true;
        }
    }
    
    public int countOnlineUser (){
        return sessionIDUser.size();
    }
    
    public DefaultListModel showOnlineUser () {
        DefaultListModel m = new DefaultListModel ();
        for (Map.Entry<String,String> map : sessionIDUser.entrySet()) {
            String value = map.getValue();
            m.addElement(value);
        }
        return m;
    }
   
}
