/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmi.server;

import com.mysql.jdbc.Connection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
/**
 *
 * @author EndSub
 */
public class User/** extends Server**/ {
    
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String birthday;
    private String description;
    public  Connection conn;
    

    public  Server server;
    User( Server xyz )   {
        server = xyz;
        conn = new DBConnection().connect();
        if ( conn != null ){
            xyz.frame.setlogText("DBConnection established");
        }
        else{
            xyz.frame.setlogText("No DBConnection established");   
        }
    }
    
    public User (String username, String password, String email, String firstName, String lastName, String birthday, String description) throws RemoteException {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.description = description;
        
    }

    
    public String getUsername (String SessionID){
        return server.s.getUsername(SessionID);       
    }
    
    public String getEmail (String sessionID) throws SQLException {
        
        String query ="select email from TEAM_4I_DB.user where username=?";
        PreparedStatement sta = conn.prepareStatement (query);
        sta.setString (1, getUsername(sessionID)); //map first '?' to inputted username
        ResultSet rs = sta.executeQuery();
        rs.next();
        return rs.getString("email");
    
    }    

    public String getFirstName (String sessionID) throws SQLException {
        
        String query ="select firstName from user where username=?";
        PreparedStatement sta = conn.prepareStatement (query);
        sta.setString (1, getUsername(sessionID)); //map first '?' to inputted username
        ResultSet rs = sta.executeQuery();
        rs.next();
        return rs.getString("firstName");
    
    }
    
   
    public String getLastName(String sessionID) throws SQLException {
        String query ="select lastName from user where username=?";
        PreparedStatement sta = conn.prepareStatement (query);
        sta.setString (1, getUsername(sessionID)); //map first '?' to inputted username
        ResultSet rs = sta.executeQuery();
        rs.next();
        return rs.getString("lastName");

    }
    
    
    public String getBirthday(String sessionID) throws SQLException {
        String query ="select birthday from user where username=?";
        PreparedStatement sta = conn.prepareStatement (query);
        sta.setString (1, getUsername(sessionID)); //map first '?' to inputted username
        ResultSet rs = sta.executeQuery();
        rs.next();
        return rs.getString("birthday");
    }
    
    
    public String getDescription(String sessionID) throws SQLException {
        String query ="select description from user where username=?";
        PreparedStatement sta = conn.prepareStatement (query);
        sta.setString (1, getUsername(sessionID)); //map first '?' to inputted username
        ResultSet rs = sta.executeQuery();
        rs.next();
        return rs.getString("description");
    }
    
    /**
     * Method getInfo is used to transfer information about current user to the client platform
     * @param sessionID
     * @return an array list which contains first name, last name, birthday and description 
     * @throws SQLException 
     */
    public ArrayList<String> getInfo (String sessionID) throws SQLException{
        ArrayList<String> userInfo = new ArrayList<>();
        userInfo.add(getEmail(sessionID));
        userInfo.add(getFirstName(sessionID));
        userInfo.add(getLastName(sessionID));
        userInfo.add(getBirthday(sessionID));
        userInfo.add(getDescription(sessionID));
        return userInfo;
    }
    
    
    /**
     * Get public information from other people, e.g someone you want to add
     * as friend
     * @param username username of user you want to get public information from
     * @return an ArrayList which contains first name, last name, birthday and description
     * @throws SQLException 
     */
    public ArrayList<String> getPublicInfo (String username) throws SQLException {
        ArrayList<String> info = new ArrayList<>();
        String query ="select email,firstName,lastName,birthday,description from user where username=?";
        PreparedStatement sta = conn.prepareStatement (query);
        sta.setString (1, username); 
        ResultSet rs = sta.executeQuery();
        rs.next();
        info.add(rs.getString("email"));
        info.add(rs.getString("firstName"));
        info.add(rs.getString("lastName"));
        info.add(rs.getString("birthday"));
        info.add(rs.getString("description"));
        return info;
    }
   
    
    /**
     * Method updateUserInfo updates information about an existing user 
     * @param sessionID
     * @param first
     * @param last
     * @param bd
     * @param d
     * @throws SQLException 
     */
    public void updateUserInfo (String sessionID,String mail, String first, String last, String bd, String d) throws SQLException {
        if (server.s.isTimedOut(sessionID)){
            JOptionPane.showMessageDialog(null, "Session timed out! please login again");        
        }
        else {
            Statement sta = conn.createStatement();
            sta.executeUpdate("update TEAM_4I_DB.user set email = '"+mail+"',firstName ='"+first+"', lastName ='"+last+"',birthday ='"+bd+"',description ='"+d+"' where username ='"+getUsername(sessionID)+"'");
        }
    }
    
    /**
     * checks whether username already exists in database
     * @param username inputted username
     * @return true if username already exists, else false
     * @throws SQLException 
     */
    public boolean userExist (String username) throws SQLException{
        String query = "select 1 from user where username = ?";
        PreparedStatement s = conn.prepareStatement (query);
        s.setString (1, username);
        ResultSet rs = s.executeQuery ();
        rs.last();
        int rowCount = rs.getRow();
        return rowCount!= 0;
    }
    
    /**
     * Method createUser inserts given parameters into the datebase
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @param birthday
     * @param description 
     */
    public void createUser (String username, String email, String password, String firstName, String lastName, String birthday, String description) {
        try {
            Statement sta = conn.createStatement();
            sta.executeUpdate("insert into TEAM_4I_DB.user values ('"+username+"','"+password+"','"+email+"','"+firstName+"','"+lastName+"','"+birthday+"','"+description+"')");
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: "+ex.getMessage());
        }
    }
    
    /**
     * Method deletes an existing user from the database
     * @param sessionID sessionID will then be looked up to find the user,
     * which shall be deleted
     */
    public void deleteUser (String sessionID){
        try {
                String user     = getUsername(sessionID);
                Statement del   = conn.createStatement();
                del.executeUpdate("delete from user where username = '"+user+"'");
                Object[] ob = server.group.listMyGroups(sessionID).toArray();
                for (int i =0; i < ob.length;i++) {
                    server.group.leaveGroup(sessionID, ob[i].toString());
                }
                // new Code start -------------------------------------------
                // delete users friendchip connection from table friendchip
                server.friendship.leaveFriendship(sessionID);
                // delete all news from table acceptor when user is deleted
                server.news.deleteUsersNews(sessionID);
                // delete all posts and votes from user
                server.post.deletePostsByUser(sessionID);
                // new Code end --------------------------------------------
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Error:"+ex.getMessage());
    
            }
    }
    
    /**
     * Method loginVerified checks whether inputted username and password
     * exist in database
     * @param username
     * @param password
     * @return true if username and password are verified, else false
     * @throws SQLException 
     */
    public boolean loginVerified (String username, String password)throws SQLException{
        //Prepare SQL-Query for execution
        String sqlGetLogData ="select * from user where username=? and password=?";
        PreparedStatement logData = conn.prepareStatement (sqlGetLogData);
        logData.setString (1, username); //map first '?' to inputted username
        logData.setString (2, password);//map second '?' to inputted password
        ResultSet logSQLExecuted = logData.executeQuery ();
        
        return logSQLExecuted.next();
    }
    
    /**
     * Method passwordVerified verifies compares inputted password with password
     * in database
     * @param input inputted password
     * @param sessionID sessionID of current user
     * @return true if inputted password is correct, else false
     * @throws SQLException 
     */
    public boolean passwordVerified (String input, String sessionID ) throws SQLException {
        String query = "select password from user where username = ?";
        PreparedStatement sta = conn.prepareStatement (query);
        sta.setString (1, getUsername(sessionID));
        ResultSet rs = sta.executeQuery ();
        rs.next();
        String pass = rs.getString("password");
        return (pass.equals(input));
      
    }
    
    /**
     * change password of current user, suggest to use passwordVerified to check
     * whether old password was entered correctly
     * @param sessionID
     * @param old inputted old password
     * @param newpass inputted new password
     * @throws SQLException 
     */
    public void changePassword (String sessionID, String old, String newpass) throws SQLException {
        if (passwordVerified (old, sessionID)){
            Statement sta = conn.createStatement();
            sta.executeUpdate("update user set password ='"+newpass+"' where username = '"+getUsername(sessionID)+"'");
        }
        else {
            JOptionPane.showMessageDialog(null,"Altes Passwort ist falsch, bitte erneut eingegeben");
        }
    }
    
    /**
     * Method listUser lists all existing users in database
     * @return returns a DefaultListModel for displaying purpose
     * @throws SQLException 
     */
    public DefaultListModel listUser () throws SQLException{
        PreparedStatement list = conn.prepareStatement("select username from user");
        ResultSet rs = list.executeQuery();
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("username");
            m.addElement(listEntry);
        }
        return m;
    }
    
    public DefaultListModel searchUsername (String keyword) throws SQLException {
        Statement search = conn.createStatement();
        ResultSet rs = search.executeQuery("select username from user where username like '"+keyword+"%'");
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("username");
            m.addElement(listEntry);
        }
        return m;
    }
    
    public DefaultListModel searchEmail (String keyword) throws SQLException {
        Statement search = conn.createStatement();
        ResultSet rs = search.executeQuery("select username from user where email like '"+keyword+"%'");
        DefaultListModel m = new DefaultListModel ();
        while (rs.next()){
            String listEntry = rs.getString("username");
            m.addElement(listEntry);
        }
        return m;        
    }
    
}
