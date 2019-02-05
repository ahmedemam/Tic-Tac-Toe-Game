/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 *
 * @author hagar
 */
public class DBConnection 
{
    
    /**
     * @param args the command line arguments
     */
    Connection con ; 
    String url="jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2277076";
    String user_name= "sql2277076";
    String password="pF8*dV3%";
    static DBConnection instance;
    
     //Constructor 
    
    private DBConnection() throws ClassNotFoundException, SQLException
    {
             Class.forName("com.mysql.jdbc.Driver") ; 
             con = DriverManager.getConnection(this.url,this.user_name,this.password) ;         
    }
    
    
    public Connection getConnection() 
    {
        return this.con;
    }

    //connect Function 
    public static DBConnection getConnectionInstance () throws ClassNotFoundException, SQLException
    {
        if (instance == null || instance.getConnection().isClosed())
        {
            instance = new DBConnection();
        } 
        
        return  instance;
    }
   
    
    public void closeConnection() throws SQLException 
    {
        this.con.close();
    }

    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // TODO code application logic here
        DBConnection instance = DBConnection.getConnectionInstance() ; 
        System.out.println("test test test 1..2..3  "+ instance);
        
         
    }
}
