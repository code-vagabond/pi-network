/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmi.server;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.rmi.server.User;

/**
 *
 * @author EndSub
 */
public class Friendship {

    public  Server server;
    Friendship( Server xyz )   {
        server = xyz;     
    }

    public boolean friendExists (String user, String friend) throws SQLException {
        String query = "select 1 from friendship where friendFrom = ? and friendTo = ?";
        PreparedStatement s = server.user.conn.prepareStatement (query);
        s.setString (1, user);
        s.setString(2, friend);
        ResultSet rs = s.executeQuery ();
        rs.last();
        int rowCount = rs.getRow();
        return rowCount!= 0;        
    }
    public void addFriend (String sessionID, String friend) throws SQLException{
        String user = server.user.getUsername(sessionID);
        if (friendExists (user, friend)){
            JOptionPane.showMessageDialog(null,"Already marked as friend");
        }
        else {
        Statement sta = server.user.conn.createStatement();
        sta.executeUpdate("insert into TEAM_4I_DB.friendship values('"+user+"','"+friend+"')");
        }
    }
    
    public void unfriend (String sessionID, String friend) throws SQLException {
        Statement del = server.user.conn.createStatement();
        del.executeUpdate("delete from friendship where friendFrom = '"+server.user.getUsername(sessionID)+"' and friendTo ='"+friend+"'");
    }
    
    /**
     * List people who current user has added as friend
     * @param sessionID
     * @return a DefaultListModel to display in jList
     * @throws SQLException 
     */
    public DefaultListModel listFriendsFrom (String sessionID) throws SQLException{
        PreparedStatement list = server.user.conn.prepareStatement("select friendTo from friendship where friendFrom=?");
        list.setString(1, server.user.getUsername(sessionID));
        ResultSet rs = list.executeQuery();
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("friendTo");
            m.addElement(listEntry);
        }
        return m;
    }
    
    /**
     * List people who have added current user as friend
     * @param sessionID
     * @return DefaultListModel to display in j.List
     * @throws SQLException 
     */
    public DefaultListModel listFriendsTo (String sessionID) throws SQLException{
        PreparedStatement list = server.user.conn.prepareStatement("select friendFrom from friendship where friendTo=?");
        list.setString(1, server.user.getUsername(sessionID));
        ResultSet rs = list.executeQuery();
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("friendFrom");
            m.addElement(listEntry);
        }
        return m;
    }
    
    /**
     * Method listMutualFriendships lists all people which user has added as friend,
     * who also added the user as friend
     * @param sessionID
     * @return
     * @throws SQLException 
     */
    public DefaultListModel listMutualFriendships (String sessionID) throws SQLException {
        PreparedStatement list = server.user.conn.prepareStatement ("select friendTo from friendship where friendFrom = ? and friendTo in (select friendFrom from friendship where friendTo =?)");
        list.setString(1, server.user.getUsername(sessionID));
        list.setString(2, server.user.getUsername(sessionID));
        ResultSet rs = list.executeQuery();
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString ("friendTo");
            m.addElement (listEntry);
        }
        return m;
    }
    
    /**
     * Method isFriend checks whether user already added someone as friend
     * @param sessionID
     * @param name username of the user to be checked
     * @return if the person defined by parameter name is already in friendlist 
     * return true, else return false
     * @throws SQLException 
     */
    public boolean isFriend (String sessionID, String name) throws SQLException {
        String query = "select 1 from friendship where friendFrom = ? and friendTo =?";
        PreparedStatement s = server.user.conn.prepareStatement (query);
        s.setString (1, server.user.getUsername(sessionID));
        s.setString(2, name);
        ResultSet rs = s.executeQuery ();
        rs.last();
        int rowCount = rs.getRow();
        return rowCount!= 0;
    }
    
   
}
