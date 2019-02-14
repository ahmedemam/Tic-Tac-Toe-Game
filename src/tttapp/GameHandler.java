/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tttapp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;


/**
 *
 * @author MKhaled
 */
public class GameHandler extends Thread{
    
     DataInputStream dis ; 
    PrintStream ps ; 
    static ArrayList <GameHandler> players = new ArrayList<>() ; 
    static int count = 0 ; 
    int pid ; 
    // linkedhashmap - data 
    
    public GameHandler(Socket s){
        
         try {
             
             dis = new DataInputStream(s.getInputStream()) ;
             ps = new PrintStream(s.getOutputStream()) ;
             count++ ; 
             pid = count ;         
             System.out.println("Player : "+pid+" Coneected with socket : "+s);
             ps.println(pid);
             players.add(this) ; 
             
             start() ;
             
         } catch (Exception ex) {
             ex.printStackTrace();
         }
        
    }
    
    @Override 
    public void run(){
        
        int tileX ,tileY , toID , fromID ; 
       
        String playInfo ; 
        String [] playInfoArr ; 
        
        try{
            while(true){
              
                playInfo = dis.readLine() ; 
               // System.out.println("play: "+playInfo);
               if(playInfo.length()>0){
                   System.out.println("");
               
               playInfoArr = playInfo.split(" ") ;
                
                fromID = Integer.parseInt(playInfoArr[2]) ;    
                toID  = Integer.parseInt( playInfoArr[3] ); // Recieve id of player to send it to 
                tileX = Integer.parseInt( playInfoArr[4] ); // Recieve Row Of play
                tileY = Integer.parseInt( playInfoArr[5] ); // Recieve Col of play 
                // send play to opponent 
              send(tileX,tileY,toID) ; 
               } 
                
//                System.out.println("There is play to "+toID+" in row : "+tileX+"and Col : "+tileY);
                // send play to opponent 
//              send(tileX,tileY,toID) ; 
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void  send(int tileX , int tileY , int toID){
        String playInfoToSend = tileX+" "+tileY ;
        System.out.println("playInfoToSend "+playInfoToSend );
        for (GameHandler gh : players){
            if (gh.pid == toID){
               
               gh.ps.println(playInfoToSend);
               //gh.ps.println(tileY);
           
            }
        }
    }
}
