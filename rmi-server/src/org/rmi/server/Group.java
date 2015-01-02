/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmi.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
//import static org.rmi.server.User;

/**
 *
 * @author EndSub
 */
public class Group {
    
    public  Server server;
    Group( Server xyz )   {
        server = xyz;     
    }
    /**
     * checks whether a group already exists
     * @param groupname
     * @return true if group already exists, else false
     * @throws SQLException 
     */
    public boolean groupExists (String groupname) throws SQLException {
        String query = "select 1 from groupList where gName = ?";
        PreparedStatement s = server.user.conn.prepareStatement (query);
        s.setString (1, groupname);
        ResultSet rs = s.executeQuery ();
        rs.last();
        int rowCount = rs.getRow();
        return rowCount!= 0;        
    }
    
    
    /**
     * insert a group in database, add current user to groupMemberList, it's suggested 
     * to use groupExists to see whether group already exists beforehand to avoid error ! 
     * @param sessionID
     * @param gName
     * @param gTopic
     * @throws SQLException 
     */
    public void addGroup (String sessionID, String gName, String gTopic) throws SQLException{
        String user = server.user.getUsername(sessionID);
        Statement group = server.user.conn.createStatement();
        group.executeUpdate("insert into TEAM_4I_DB.groupList values('"+gName+"','"+gTopic+"')");
        Statement groupmember = server.user.conn.createStatement();
        groupmember.executeUpdate("insert into groupMemberList values ('"+user+"','"+gName+"')");
    }
    
    public boolean isMember (String sessionID, String gName) throws SQLException {
        String query = "select 1 from groupMemberList where username = ? and gName = ?";
        PreparedStatement s = server.user.conn.prepareStatement (query);
        s.setString (1, server.user.getUsername(sessionID));
        s.setString(2, gName);
        ResultSet rs = s.executeQuery ();
        rs.last();
        int rowCount = rs.getRow();
        return rowCount!= 0;              
    }
    
    public void joinGroup (String sessionID, String gName) throws SQLException {
        String user = server.user.getUsername(sessionID);
        Statement join = server.user.conn.createStatement();
        join.executeUpdate ("insert into groupMemberList values ('"+user+"','"+gName+"')");
            
    }
    
    public DefaultListModel listMember (String gName) throws SQLException {
        String sql = "select username from groupMemberList where gName = ?";
        PreparedStatement list = server.user.conn.prepareStatement(sql);
        list.setString (1, gName);
        ResultSet rs = list.executeQuery();
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString ("username");
            m.addElement (listEntry);
        }
        return m;
    }
    
    public String getGroupTopic (String gName) throws SQLException {
        String sql = "select gTopic from groupList where gName = ?";
        PreparedStatement list = server.user.conn.prepareStatement(sql);
        list.setString (1, gName);
        ResultSet rs = list.executeQuery();
        rs.next();
        return rs.getString("gTopic");
    }
    
    public int countMember (String gName) throws SQLException {
        return listMember (gName).size();
    }
    
    /**
     * leave a group, if current user is last member, delete group after leaving
     * @param sessionID
     * @param gName name of the group
     * @throws SQLException 
     */
    public void leaveGroup (String sessionID, String gName) throws SQLException {
        String user = server.user.getUsername(sessionID);
        Statement leave = server.user.conn.createStatement();
        leave.executeUpdate("delete from groupMemberList where username ='"+user+"' and gName ='"+gName+"'");
        if (countMember (gName) == 0) {
            Statement delGroup = server.user.conn.createStatement();
            delGroup.executeUpdate("delete from groupList where gName ='"+gName+"'");
        }
    }
    
    public DefaultListModel searchGroup (String keyword) throws SQLException {
        Statement search = server.user.conn.createStatement();
        ResultSet rs = search.executeQuery("select gName from groupList where gName like '"+keyword+"%'");
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("gName");
            m.addElement(listEntry);
        }
        return m;
    }
    
    
    public DefaultListModel listAllGroup () throws SQLException {
        Statement list = server.user.conn.createStatement();
        ResultSet rs = list.executeQuery("select gName from groupList");
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("gName");
            m.addElement(listEntry);
        }
        return m;        
    }
    
    public DefaultListModel listMyGroups (String sessionID) throws SQLException {
        String user = server.user.getUsername(sessionID);
        Statement list = server.user.conn.createStatement();
        ResultSet rs = list.executeQuery("select gName from groupMemberList where username = '"+user+"'");
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("gName");
            m.addElement(listEntry);
        }
        return m;  
    }
}
