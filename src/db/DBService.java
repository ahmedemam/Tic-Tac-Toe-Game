/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

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
public class DBService {
    
    
    DBConnection con;
    PreparedStatement pst ;
    ResultSet rs ; 
    Statement stmt ; 
    ResultSetMetaData rsmd ;
    ArrayList<String> row=new ArrayList<String>();
    
    public DBService(DBConnection con)
    {
        this.con=con;
    }
    
    public void setRow(ArrayList<String> row)
    {
        this.row=row;
    }
    
    public ArrayList<String>getRow()
    {
        return this.row;
    }
    
    //retrieves coloumn names of specific table using metadata to pass it into SQL queries
    private String getColsNames(String tableName) throws SQLException, ClassNotFoundException{
        
        String colNames = "" ; 
        String q2 = "select * from "+tableName ; 
       
        
            this.stmt = DBConnection.getConnectionInstance().getConnection().createStatement() ; 
            this.rs = this.stmt.executeQuery(q2);
            this.rsmd = this.rs.getMetaData();

            for (int i = 2; i <= this.rsmd.getColumnCount()-1; i++) 
            {
                   colNames+=(this.rsmd.getColumnName(i)+",");  
            }
            colNames+=(this.rsmd.getColumnName(this.rsmd.getColumnCount())) ;  
         
      return colNames ; 
    }
    
    //retrieves corresponding parammeters
    private String getQueryParm()
    {
        
       // Calculate number of '?'
       String q_params = "(" ; 
        for (int i = 0; i < this.getRow().size()-1; i++) {
            q_params+="?," ; 
        }
        q_params+= "?)" ; 
         System.out.println(q_params);
         return q_params;
    }
 
    
    private void considerDatatype() throws SQLException
    {
        int i;
        for (i = 0; i < this.row.size(); i++) 
            if ("VARCHAR".equalsIgnoreCase(rsmd.getColumnTypeName(i+2)))
                pst.setString(i+1,this.getRow().get(i));
            else if ("INT".equalsIgnoreCase(rsmd.getColumnTypeName(i+2)))
                pst.setInt(i+1, Integer.parseInt(this.getRow().get(i)) );
    }
    
    //insert Function 
    public int insert(String table_name) throws SQLException, ClassNotFoundException{
  
        int row_affected=0;
        
        //get columns names
        String col_names = "( "+getColsNames(table_name)+" )" ;
        System.out.print(col_names);
        //get '?' structure
        String q_params = getQueryParm(); 

        //start insert 
        String query = "insert into "+table_name+col_names+" values"+q_params ;
         
        this.pst = con.getConnection().prepareStatement(query) ;

        this.considerDatatype();
        
        row_affected=pst.executeUpdate();
     
        return row_affected;
    }
    
    //delete Function
    public int delete(String table_name,int id) throws SQLException
    {
        int row_affected=0;
        String query = "Delete from "+table_name+" where id="+id;
        this.pst = con.getConnection().prepareStatement(query) ;
        
        row_affected = pst.executeUpdate();
        return row_affected;
    }

    //update Function
    public int update(String table_name,int id) throws SQLException, ClassNotFoundException
    {
        String col_names=getColsNames(table_name);
        String col_arr[]=col_names.split(",");
        String q_param="";
        int row_affected=0;
        int i;

        for( i=0;i<col_arr.length-1;i++)
        {
            System.out.println(col_arr[i]);
            q_param+=col_arr[i]+"= ?,";
        }
            q_param+=col_arr[i]+"= ?";

        String query = "update "+table_name+" set "+q_param+" where id = "+id;

        this.pst = con.getConnection().prepareStatement(query);
        
        this.considerDatatype();
        
        row_affected=pst.executeUpdate();
        
        return row_affected;
    }
    
    public  ArrayList<String> getRecord(String table_name,int id) throws SQLException
    {
        String query="select * from "+table_name+" where id ="+id;
        this.pst = con.getConnection().prepareStatement(query);
        
        this.rs=pst.executeQuery();
        
        
        int count=rs.getMetaData().getColumnCount();
        
        while(rs.next())
        {
            int i=1;
            while(i<=count)
            {
            this.row.add(this.rs.getString(i++));               
            }
 
        }
    
        return this.row;
    }
    public static void main(String args[]) throws SQLException, ClassNotFoundException
    {
        ArrayList<String> record = new ArrayList();
        DBConnection con = DBConnection.getConnectionInstance();
        DBService serv= new DBService(con);
        
                
        record = serv.getRecord("player", 21);
         for(int i=0;i<record.size();i++)
            System.out.println(record.get(i));
    }
}
