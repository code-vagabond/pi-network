package org.rmi.server;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Class DBConnection connects to the given SQL database as shown in an example
 * in lecture
 */
public class DBConnection {
    private Connection DBConnection;
    public Connection connect (){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connection Success");
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println ("Connection Fail" + cnfe);
        }
        String url;
        url = "jdbc:mysql://141.2.89.26:3306/TEAM_4I_DB?zeroDateTimeBehavior=convertToNull";
        try {
            DBConnection = (Connection) DriverManager.getConnection(url,"TEAM_4I","vC4Q4H2q");
            System.out.println("Database connected");
        }
        catch (SQLException se){
            System.out.println ("No database"+ se);
            
        }
        return DBConnection;
    }        
}
