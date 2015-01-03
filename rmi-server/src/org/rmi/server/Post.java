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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author EndSub
 */
public class Post {
    
    public String id;
    public String submitter;
    public String title;
    public String content;
    public String date;
    public int publicity;
    public String vote;
    
    public Server server;
    Post (Server xyz){
        server = xyz;
    }
    

    
    public Post (String id, String aut, String tit, String date, String vote) {
        this.id = id;
        this.submitter = aut;
        this.title = tit;
        this.date = date;
        this.vote = vote;
    }

    public String getUsername (String SessionID){
        return server.s.getUsername(SessionID);       
    }
    
    
    /**
     * submitPost submits a post into database, title, content and publicity
     * setting are given by user, function automatically inserts the submit time
     * and username into each new submission
     * @param sessionid
     * @param tit title
     * @param con content
     * @param pub publicity setting (1 for public, 0 for private)
     */
    public void submitPost (String sessionid, String tit, String con, int pub){
        String sub = getUsername(sessionid);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date current = Calendar.getInstance().getTime(); 
        String date = df.format(current);
        
        try {
            Statement sta = server.user.conn.createStatement();
            sta.executeUpdate("insert into TEAM_4I_DB.post "
                    + "(submitter, title, content, submitTime, publicity) values"
                    + "('"+sub+"','"+tit+"','"+con+"','"+date+"','"+pub+"')");
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: "+ex.getMessage());
        }
    }
    
    public void deletePost (String sessionid, int id) throws SQLException{
        String user = getUsername(sessionid);
        Statement del = server.user.conn.createStatement();
        del.executeUpdate("delete from post where submitter ='"+user+"' and id ="+Integer.toString(id));
        del.executeUpdate("delete from vote where postID ="+Integer.toString(id));
    }
    
    /**
     * getPost fetchs posts which are allowed to be seen by current user
     * @param sessionid
     * @return returns an array list containing postID, username of submitter, title of 
     * the post, post content and submit time as strings
     * @throws SQLException 
     */
    public ArrayList <String> getPosts (String sessionid) throws SQLException {
        String user = server.user.getUsername(sessionid);
        PreparedStatement view = server.user.conn.prepareStatement ("select *  from post where submitter \n" +
" = ? or submitter in (select friendTo from friendship where friendFrom = ? and publicity = 1) \n" +
"or submitter in (select friendTo from friendship where friendFrom = ? \n" +
"and friendTo in (select friendFrom from friendship where friendTo =?))  and publicity = 0 order by id desc");
        view.setString(1, user);
        view.setString(2, user);
        view.setString(3, user);
        view.setString(4, user);
        ResultSet rs = view.executeQuery();
        ArrayList <String> a = new ArrayList <String> ();
        while (rs.next()){
            String id = rs.getString ("id");
            String sub = rs.getString ("submitter");
            String tit = rs.getString ("title");
            String con = rs.getString ("content");
            String date = rs.getString("submitTime");
            a.add(id);
            a.add (sub);
            a.add (tit);
            a.add (con);
            a.add (date);
        }
        return a;
    }
    
    
    public DefaultListModel <String[]> viewPosts (String sessionid) throws SQLException {
        DefaultListModel <String[]> list = new DefaultListModel ();
        ArrayList <String> a = getPosts(sessionid);
        for (int i=0 ; i < a.size()-4; i = i + 5 ) {
            String [] row = {a.get(i+2), a.get(i+1), a.get(i+4),
                getVoteCount(a.get(i)), a.get(i+3), a.get(i) };
            list.addElement(row);
        }
        return list;
    }
    
    /**
     * 
     * @param sessionid
     * @return all post submitted by current user
     * @throws SQLException 
     */
    public ArrayList <String> getEditablePosts (String sessionid) throws SQLException {
        PreparedStatement editable = server.user.conn.prepareStatement ("select *  from post where submitter = ?");
        editable.setString(1, getUsername(sessionid));
        ResultSet rs = editable.executeQuery();
        ArrayList <String> a = new ArrayList <String> ();
        while (rs.next()){
            String tit = rs.getString ("title");
            String date = rs.getString ("submitTime");  
            String con = rs.getString ("content");
            String id = rs.getString ("id");
            a.add(tit);
            a.add (date);
            a.add (con);
            a.add (id);
        }
        return a;            
    }
    
    public DefaultListModel <String[]> viewEditablePosts (String sessionid) throws SQLException {
        DefaultListModel <String[]> list = new DefaultListModel ();
        ArrayList <String> a = getEditablePosts(sessionid);
        for (int i=0 ; i < a.size()-3; i = i + 4 ) {
            String [] row = {a.get(i), a.get(i+1), a.get(i+2), a.get(i+3)};
            list.addElement(row);
        }
        return list;
    }
    
        
    /**
     * Used to get information from a post in post editing panel
     * @param sessionid
     * @param postID
     * @return a post which the current user has clicked on to edit
     * @throws SQLException 
     */
    public ArrayList<String> getSelectedPost (String sessionid, int postID) throws SQLException {
        String user = getUsername(sessionid);
        Statement get = server.user.conn.createStatement();
        ResultSet rs = get.executeQuery("select * from post where submitter ='"
        +user+"' and id ="+Integer.toString(postID));      
        ArrayList <String> a = new ArrayList <String> ();
        while (rs.next()){
            String id = rs.getString ("id");
            String sub = rs.getString ("submitter");  
            String tit = rs.getString ("title");
            String con = rs.getString ("content");
            String date = rs.getString("submitTime");
            String pub = rs.getString("publicity");
            a.add(id);
            a.add (sub);
            a.add (tit);
            a.add (con);
            a.add (date);
            a.add (pub);
        }
        return a;          
    }
    
    /**
     * editPost is used to edit post title, content or publicity setting of
     * submitted posts
     * @param sessionid sessionID of current user
     * @param postID ID of the post which is going to be edited
     * @param title up to date title
     * @param content up to date content
     * @param pub up to date privacy setting (1 for public, 0 for private)
     * @throws SQLException 
     */
    public void editPost (String sessionid, int postID, String title, String content) throws SQLException {
        Statement edit = server.user.conn.createStatement();
        edit.executeUpdate("update post set title ='"+title+"', content = '"+content+
                "'where id = " +postID+" and submitter ='"+getUsername(sessionid)+"'");
    }
    
    public void editPublicity (String sessionid, int postID, int pub) {
        String user = getUsername(sessionid);
        String epub = "update post set publicity = ? where id = ? and submitter = ?";
        
        
        try {
            PreparedStatement epubex = server.user.conn.prepareStatement(epub);
            epubex.setInt(1, pub);
            epubex.setInt(2, postID);
            epubex.setString(3, user);
            epubex.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
        /**
     * look up all post submitted by the inputted username that the current user
     * is allowed to view
     * @param sessionid
     * @param input inputted username
     * @return an array list of posts with id, submitter, title, content and
     * submit time as strings
     * @throws SQLException 
     */
    public ArrayList <String> searchPost (String sessionid, String submitter) throws SQLException {
        String user = getUsername(sessionid);
        Statement search = server.user.conn.createStatement();
        ResultSet rs = search.executeQuery("select * from (select *  from post where submitter \n" +
" = '"+user+"' or submitter in (select friendTo from friendship where friendFrom = '"+user+"' and publicity = 1) \n" +
"or submitter in (select friendTo from friendship where friendFrom = '"+user+"' \n" +
"and friendTo in (select friendFrom from friendship where friendTo = '"+user+"'))  and publicity = 0) "
                + " as Alias where submitter like '"+submitter+"%'");      
        ArrayList <String> a = new ArrayList <String> ();
        while (rs.next()){
            String id = rs.getString ("id");
            String sub = rs.getString ("submitter");  
            String tit = rs.getString ("title");
            String con = rs.getString ("content");
            String date = rs.getString("submitTime");
            a.add(id);
            a.add (sub);
            a.add (tit);
            a.add (con);
            a.add (date);
        }
        return a;           
    }
    
    
    /**
     * 
     * @param postID postID as String
     * @return a String containing vote count for given postID
     * @throws SQLException 
     */
    public String getVoteCount (String postID) throws SQLException {
        String query ="select vote from post where id=?";
        PreparedStatement sta = server.user.conn.prepareStatement (query);
        sta.setInt(1, Integer.parseInt(postID)); //map first '?' to inputted username
        ResultSet rs = sta.executeQuery();
        rs.next();
        return rs.getString("vote");
    
    }
   
    
    /**
     * internal function, increases vote count in table post by 1
     * @param postID
     * @throws SQLException 
     */
    public void increaseVoteCount (int postID) throws SQLException {
        Statement change = server.user.conn.createStatement ();
        change.executeUpdate("update post set vote = vote +1 where id ="+postID);
    }
    
    /**
     * internal function, decreases vote count in table post by 1
     * @param sessionid
     * @param postID
     * @throws SQLException 
     */
    public void decreaseVoteCount (int postID) throws SQLException {
        Statement change = server.user.conn.createStatement ();
        change.executeUpdate("update post set vote = vote -1 where id ="+postID);
    }
    
    
    /**
     * votePost places one positive vote to given post
     * @param sessionid sessionID of current user
     * @param postID ID of the post which is going to take the vote
     * @throws SQLException 
     */
    public void votePost (String sessionid, int postID) throws SQLException {
        Statement vote = server.user.conn.createStatement ();
        vote.executeUpdate ("insert into vote values ("+Integer.toString(postID)+",'"+getUsername(sessionid)+"')"); 
        increaseVoteCount (postID);
    }
    
    /**
     * remove the vote from a given post
     * @param sessionid sessionID of current user
     * @param postID ID of the post which's vote is going to be decreased
     * @throws SQLException 
     */
    public void unvotePost (String sessionid, int postID) throws SQLException {
        Statement unvote  = server.user.conn.createStatement ();
        unvote.executeUpdate("delete from vote where postID="+postID+" and voter ='"+getUsername(sessionid)+"'");
        decreaseVoteCount (postID);
    }
    
    
    /**
     * isVoted checks whether current user already given a vote to given post
     * @param sessionid
     * @param postID
     * @return true if a vote already exists, else false
     * @throws SQLException 
     */
    public boolean isVoted (String sessionid, int postID) throws SQLException {
        String query = "select 1 from vote where voter = ? and postID = ?";
        PreparedStatement s = server.user.conn.prepareStatement (query);
        s.setString (1, getUsername(sessionid));
        s.setString (2, Integer.toString(postID));
        ResultSet rs = s.executeQuery ();
        rs.last();
        int rowCount = rs.getRow();
        return rowCount!= 0;            
    }
    
    
}
