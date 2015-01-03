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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; 

/**
 * 
 * @author werner
 */
public class News {
    
    public  Server server;
    News( Server xyz )   {
        server = xyz;     
    }  

    public void testRoutineMarkReadNews()
    {
        try {
            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String user = server.user.getUsername(sID);
            markReadNews(sID, 1 );
        }
        catch (SQLException e)
        {
            e.printStackTrace();     
        }
    }   

    
    public void testRoutineDeleteNews()
    {
        try {
            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String user = server.user.getUsername(sID);
            deleteNews(sID, 9);
        }
        catch (SQLException e)
        {
            e.printStackTrace();     
        }
    }     

    
    public void testRoutineDeleteUsersNews()
    {
        try {
            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String user = server.user.getUsername(sID);
            deleteUsersNews (sID);
        }
        catch (SQLException e)
        {
            e.printStackTrace();     
        }
    } 

    
    
    public void testRoutineSendNews()
    {
        try {
            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String user = server.user.getUsername(sID);
            sendNews(sID, "v", "Alles was keinen Sinn macht","UnSinn");
        }
        catch (SQLException e)
        {
            e.printStackTrace();     
        }
    }
    
    /**
    * 
    * @return 
    */
    public DefaultListModel<String>  testRoutinegetAllAdressees( ){
        String user = "";
        try {
//            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String sID = server.verifyLogin("Lars", "4139696413090770947967587880669088304954138678557");
            user = server.user.getUsername(sID);
            return getAllAdressees( sID );
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return getAllAdressees( user );
        }
        
    } 
      
    
    public void testRoutineGetNews()
    {
        try {
            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String user = server.user.getUsername(sID);
            DefaultListModel daten = getAllNews (sID, "N0"  );
            
            
            for (int i = 0; i< daten.size(); i++)
            {
                String [] obj  = (String [])daten.get(i);
                String abc = obj[0];
 
            }
            
        }
        catch (SQLException e)
        {
            e.printStackTrace();     
        }
    }    

    
    public void testRoutineSendNewsToUsers()
    {
        DefaultListModel <String> userList = new DefaultListModel <String>() ;
        userList.addElement("Lars");
        userList.addElement("werner");
        userList.addElement("v");
        
        try {
            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String user = server.user.getUsername(sID);
            sendNewsToUsers (sID, userList, "sssssss", "kkkkkkkkk" );
           
        }
        catch (SQLException e)
        {
            e.printStackTrace();   
        }       
        
    }
    
    
    
    public void testRoutineSendNewsToGroups ( )
    {
        DefaultListModel <String> groupList = new DefaultListModel <String>() ;
        groupList.addElement("PRG");
        groupList.addElement("pi");
        groupList.addElement("test");
        
        try {
            String sID = server.verifyLogin("werner", "12984859847925339246461645049445516606679622626466");
            String user = server.user.getUsername(sID);
            sendNewsToGroups (sID, groupList, "Hallo Groupwww", "What Groupfff" );
           
        }
        catch (SQLException e)
        {
            e.printStackTrace();   
        }       
    }
    
    /**
     * 
     * @param sessionID
     * @param toUser
     * @param news
     * @param newsTitle
     * @return 
     */
    
    public boolean sendNews (String sessionID, String toUser,   String news, String newsTitle ){
        String user = server.user.getUsername(sessionID);
        try 
        {   
            
            server.user.conn.setAutoCommit(false);
            Calendar calendar = Calendar.getInstance();
            java.sql.Date heute = new java.sql.Date(calendar.getTime().getTime());
            String sqlStatement = "insert into mailbox (newsFrom, news, newsTitle, newsDate) values ( ?,?,?,? )";
            
            PreparedStatement pst = server.user.conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1,user        );  // username
            pst.setString(2,news        );  // News
            pst.setString(3,newsTitle   );  // Title
            //get current date time with Date()
            // String heute = simpleDateFormat.format(now);
            pst.setDate(4,heute);  // Datum
            pst.execute();
            
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            int newsId  = rs.getInt(1);
            
            if (newsId <= 0 ){
                return false;
            }
            
            sqlStatement = "insert into acceptor (newsNumber, newsTo, newsState) values ( ?,?,? )";
            pst =server.user.conn.prepareStatement(sqlStatement);
            pst.setInt   (1,newsId  );  // newsNumber
            pst.setString(2,toUser  );  // toUser
            pst.setInt   (3, 0      );  // ungelesen
            pst.execute();
            
            server.user.conn.commit();
            return true;
           
        }
        catch (SQLException e)
        {
            try {
               server.user.conn.rollback();
               
            }catch(SQLException se2){
              se2.printStackTrace();   
            }
 
        }
        return false;    
    }
  /**
   * 
   * @param groupList
   * @return 
   */
    ArrayList<String> getUserFromGroupList( DefaultListModel <String> groupList)
    {
        ArrayList<String> userList = new ArrayList<>();
    // Datenbankfelder sind String username , String gName
        try 
        {      
            server.user.conn.setAutoCommit(false);
            String sqlStatement = "select distinct username from groupMemberList where gName in ";
            String gListe = "( ";
            for (int i= 0; i < groupList.size() ;i++){
                gListe = gListe + "'" + groupList.get(i) + "'";
                if (i + 1 < groupList.size())
                    gListe += ", ";   
            }
            gListe += ") order by 1";  
            sqlStatement =  sqlStatement + gListe; 


            PreparedStatement pst =server.user.conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                String user = rs.getString("username");
                userList.add(user);
            }
            server.user.conn.commit();
        }    
        catch(SQLException se2){
            se2.printStackTrace();
            return userList;
        }
        return userList;
    }
            
    /**
     * 
     * @param sessionID
     * @param groupList
     * @param news
     * @param newsTitle
     * @return
     */        
    public boolean sendNewsToGroups (String sessionID, DefaultListModel <String> groupList, String news, String newsTitle )
    {
        // String user = server.user.getUsername(sessionID);
        ArrayList<String> userList = getUserFromGroupList(groupList);
        DefaultListModel <String> toUsers = new DefaultListModel();
        
        for (int i = 0; i< userList.size(); i++) toUsers.add(i,userList.get(i));
          
        boolean ret = sendNewsToUsers  (sessionID, toUsers, news, newsTitle );
        return ret;   
    }
    public boolean sendNewsToUsers  (String sessionID, DefaultListModel <String> userList, String news, String newsTitle )
    {
        String user = server.user.getUsername(sessionID);
        try 
        {   
            server.user.conn.setAutoCommit(false);
            java.util.Date today   = new java.util.Date();
            java.sql.Date sqlToday = new java.sql.Date(today.getTime());
            
            //Date now = new Date();
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
           
            String sqlStatement = "insert into mailbox (newsFrom, news, newsTitle, newsDate) values ( ?,?,?,? )";
            PreparedStatement pst =server.user.conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1,user        );  // username
            pst.setString(2,news        );  // News
            pst.setString(3,newsTitle   );  // Title
            //get current date time with Date()
            pst.setDate  (4,sqlToday    );  // Datum
            pst.execute();
            
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            int newsId  = rs.getInt(1);
            
            if (newsId <= 0 ){
                return false;
            }
            
            for (int i= 0; i < userList.size() ;i++){
                String toUser  = userList.get(i);
                sqlStatement = "insert into acceptor (newsNumber, newsTo, newsState) values ( ?,?,? )";
                pst =server.user.conn.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
                pst.setInt   (1,newsId  );  // newsNumber
                pst.setString(2,toUser  );  // toUser
                pst.setInt   (3, 0      );  // ungelesen
                pst.execute();
            }
            server.user.conn.commit();
            return true;
        }
        catch (SQLException e)
        {
            try {
               server.user.conn.rollback();
               
            }catch(SQLException se2){
              se2.printStackTrace();   
            }
 
        }
        return false;    
    }
  

    public DefaultListModel<String>  getAllAdressees(String sessionID )
    {
        DefaultListModel<String> objectData = new DefaultListModel ();
        String user = server.user.getUsername(sessionID);
        String sqlStatement;
        String userName;
        try 
        {
            server.user.conn.setAutoCommit(false);
            sqlStatement = 
            "SELECT distinct username from groupMemberList where gName in ( " +
            "SELECT gName from groupMemberList where username =  ? ) and username != ? " +
            "union " +
            "SELECT distinct friendTo FROM TEAM_4I_DB.friendship Where friendFrom = ?"; 
            
            PreparedStatement pst =server.user.conn.prepareStatement(sqlStatement);
            pst.setString(1, user );
            pst.setString(2, user );
            pst.setString(3, user );
            
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                userName      = rs.getString(1);
                objectData.addElement(userName);
            }
        }
        catch (SQLException e)
        {
            try {
               server.user.conn.rollback();
               
            }catch(SQLException se2){
              se2.printStackTrace();   
            }
        }
        return objectData;
              
    }   
    
    
   
    
    
    
    
    public DefaultListModel getAllNews (String sessionID, String sortOrder  )
    {
        DefaultListModel objectData = new DefaultListModel ();
        ArrayList<Integer> msgList = new ArrayList<>();
        msgList.clear();
        String user = server.user.getUsername(sessionID);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String sqlStatement;
        try 
        {   
            // 1. Schritt aus Tabelle acceptor messageID holen
            server.user.conn.setAutoCommit(false);
            sqlStatement = "select distinct newsNumber, newsState  from acceptor where newsTo = ? ;"; 
            
            PreparedStatement pst =server.user.conn.prepareStatement(sqlStatement);
            pst.setString(1, user );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                int msgNr       = rs.getInt("newsNumber");
                int msgState    = rs.getInt("newsState");
                
                msgList.add(msgNr);
                msgList.add(msgState);
            }
            
            // 2. Schritt alle Nachrichten holen und in ein Object verpacken
            for (int i = 0 ; i< msgList.size(); i = i + 2){
                
                if ( sortOrder.equals("ASC") || sortOrder.equals("DESC") )
                {
                
                    sqlStatement = "select * from mailbox where newsNumber = ? ORDER BY, newsFrom, newsDate, ;"; 
                }
                else
                {
                    sqlStatement = "select * from mailbox where newsNumber = ? ORDER BY newsDate;";       
                }
                
                pst =server.user.conn.prepareStatement(sqlStatement);
                
                pst.setInt(1, msgList.get(i) );
                rs = pst.executeQuery();
                
                //NewsObject obj = new NewsObject();
                String [] strObj =  new String[6];
                
                while ( rs.next() )
                {
                    /*
                    obj.setNewsFrom     (rs.getString("newsFrom"));
                    obj.setNews         (rs.getString("news"));
                    obj.setNewsTitle    (rs.getString("newsTitle"));
                    
                    java.sql.Date today = rs.getDate("newsDate");
                    String dateAscii = simpleDateFormat.format(today);
                    
                    obj.setNewsDate     (dateAscii);
                    obj.setNewsNumber   (msgList.get(i));
                    obj.setNewsState    (msgList.get(i+1));       ;
                    objectData.addElement(obj);

                    case 0:  return "Absender";
                    case 1:  return "Betreff";
                    case 2:  return "Datum";
                    case 3:  return "Gelesen";  als String 1 gelesen 0 sonst
                    case 4:  return "NewsText";
                    case 5:  return "NewsID"; // als String
                    */
                    
                    java.sql.Date today = rs.getDate("newsDate");
                    String dateAscii = simpleDateFormat.format(today);
                    
                    strObj[0] = rs.getString("newsFrom");
                    strObj[1] = rs.getString("newsTitle");
                    strObj[2] = dateAscii;
                    strObj[3] = msgList.get(i+1).toString();
                    strObj[4] = rs.getString("news");
                    strObj[5] = msgList.get(i).toString();
                    objectData.addElement(strObj);

                    
                }
            }
            server.user.conn.commit();
            return objectData;
        }
        catch (SQLException e)
        {
            try {
               server.user.conn.rollback();
               
            }catch(SQLException se2){
              se2.printStackTrace();   
            }
        }
        return objectData;  
    }
    
    public boolean markReadNews (String sessionID, int news_number)
    {
        String user = server.user.getUsername(sessionID);
        try 
        {   
            server.user.conn.setAutoCommit(false);
            String sqlStatement = "update acceptor set newsState = 1 where newsTo = ? and newsNumber = ? ";
            PreparedStatement pst =server.user.conn.prepareStatement(sqlStatement);
            pst.setString(1, user );
            pst.setInt(2, news_number );
            pst.execute();
            return true;
        }
        catch (SQLException e)
        {
            try {
               server.user.conn.rollback();
               
            }catch(SQLException se2){
              se2.printStackTrace();   
            }
        }
        return false;
    } 


    /**
     * 
     * @param sessionID
     * @param newsNumber
     * @return 
     */
    public boolean deleteNews (String sessionID, int newsNumber )
    {
        String user = server.user.getUsername(sessionID);    
        try 
        {   
            // 1. Schritt
            server.user.conn.setAutoCommit(false);
            String sqlStatement = "delete from acceptor where newsTo = ? and newsNumber = ?";
            PreparedStatement pst =server.user.conn.prepareStatement(sqlStatement);
            ResultSet rs;
            
            pst.setString(1, user );
            pst.setInt(2, newsNumber );
            pst.execute();
            
            // 2. Schritt Anzahl der zeilen in der Tabelle mit diese msgId
            sqlStatement = "select count(*) from acceptor where newsNumber = ? ;";
            pst =server.user.conn.prepareStatement(sqlStatement);
            pst.setInt(1, newsNumber );
            rs = pst.executeQuery();
            
            rs.next();
            if (rs.getInt(1) >0 ){
                server.user.conn.commit();
                return true;
            }
            // 3. Losche messabe wenn kein user mehr die Nachricht hält
            sqlStatement = "delete from mailbox where newsNumber = ? ";
            pst =server.user.conn.prepareStatement(sqlStatement);
            pst.setInt(1, newsNumber );
            pst.execute();
            server.user.conn.commit();
            return true;
        }
        catch (SQLException e)
        {
            try {
               server.user.conn.rollback();
               
            }catch(SQLException se2){
              se2.printStackTrace();   
            }
        }
        return false;
    }
    /**
     * Delete Users News when User is deleted
     * @parrs sessionID
     * @return 
     */
    public boolean deleteUsersNews (String sessionID  )
    {
        String user = server.user.getUsername(sessionID);
        String sqlStatement;
        
        try 
        {   
            // 1. Schritt
            server.user.conn.setAutoCommit(false);
            
            sqlStatement = "select newsNumber from acceptor where newsTo = ?";
            PreparedStatement pst =server.user.conn.prepareStatement(sqlStatement);
            pst.setString(1, user );        
            
            ResultSet rs = pst.executeQuery();
            
            while ( rs.next() )
            {
                int newsNumber       = rs.getInt("newsNumber");
                deleteNews (sessionID, newsNumber );
            
            }
           
            server.user.conn.commit();
            
        }
        catch (SQLException e)
        {
            try {
               server.user.conn.rollback();
               
            }catch(SQLException se2){
              se2.printStackTrace();   
            }
        }
        return false;
    }
   
}