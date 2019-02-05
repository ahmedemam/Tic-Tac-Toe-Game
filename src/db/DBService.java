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
import java.util.HashMap;
import java.util.Map;

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
    ArrayList<String> row;
    
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
    
    private String getColsNames(String tableName) throws SQLException, ClassNotFoundException{
        
        String colNames = "(" ; 
        String q2 = "select * from "+tableName ; 
       
        
            this.stmt = DBConnection.getConnectionInstance().getConnection().createStatement() ; 
            this.rs = this.stmt.executeQuery(q2);
            this.rsmd = this.rs.getMetaData();

            for (int i = 2; i <= this.rsmd.getColumnCount()-1; i++) 
            {
                   colNames+=(this.rsmd.getColumnName(i)+",");  
            }
            colNames+=(this.rsmd.getColumnName(this.rsmd.getColumnCount())+")") ;  
         
      return colNames ; 
    }
    
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
 
    
    //insert Function 
    public int insert(String table_name) throws SQLException, ClassNotFoundException{
  
        int row_affected=0;
        
        //get columns names
        String col_names = getColsNames(table_name) ;

        //get '?' structure
        String q_params = getQueryParm(); 

        //start insert 
        String query = "insert into "+table_name+col_names+" values"+q_params ;
         
        this.pst = con.getConnection().prepareStatement(query) ;

        for (int i = 0; i < this.row.size(); i++) 
        
            if ("VARCHAR".equalsIgnoreCase(rsmd.getColumnTypeName(i+2)))
                pst.setString(i+1,this.getRow().get(i));
                
            else if ("INT".equalsIgnoreCase(rsmd.getColumnTypeName(i+2)))
                pst.setInt(i+1, Integer.parseInt(this.getRow().get(i)) );
        
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
    public int update(String table_name,Map<String, String> newval, int id) throws SQLException, ClassNotFoundException
    {
        int row_affected=0;
        
         //String col_names = getColsNames(table_name);
        for(Map.Entry<String,String>entry:newval.entrySet())
        {
             String query = "update "+table_name+" set "+entry.getKey()+"="+entry.getValue()+" where id="+id ;
             this.pst = con.getConnection().prepareStatement(query) ;
            
             for (int i = 0; i < newval.size(); i++) 
                 
                 if ("VARCHAR".equalsIgnoreCase(rsmd.getColumnTypeName(i+2)))
                     pst.setString(i+1,entry.getValue());
                 
                 else if ("INT".equalsIgnoreCase(rsmd.getColumnTypeName(i+2)))
                     pst.setInt(i+1, Integer.parseInt(entry.getValue()) );
        
        }

        row_affected=pst.executeUpdate();
        
        return row_affected;
    }
   
    
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // TODO code application logic here
        DBConnection con = DBConnection.getConnectionInstance();
        DBService obj = new DBService(con) ; 
        
        ArrayList<String> valArr = new ArrayList<>() ; 
        Map <String,String>val=new HashMap<>();
        val.put("name", "hager");
        //val.put("score", "20");
       int updated=obj.update("player", val, 5);
       // valArr.add("5");
//        valArr.add("Mohamed"); //name 
//        valArr.add( "123456" ); // password
//        valArr.add("online"); // online state 
//        valArr.add("Mohamed@gmail.com") ; // email
//        valArr.add("14"); // score 
//        valArr.add("win"); // player state
//        valArr.add("fb.com"); // facebook url 
//         obj.setRow(valArr);
//         int inserted=obj.insert("player") ;
//         
//         int deleted=obj.delete("player", 5);
         con.closeConnection();
         System.out.println(updated+" row affected");
         

    }
}
