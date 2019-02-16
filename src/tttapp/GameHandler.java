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
    String msgToPlayer ;
    // linkedhashmap - data 
    
    public GameHandler(Socket s){
        
         try {
             
             dis = new DataInputStream(s.getInputStream()) ;
             ps = new PrintStream(s.getOutputStream()) ;
             count++ ; 
             pid = count ;
             msgToPlayer = "YOUR DATA is "+pid+" Player:"+pid ;
             //System.out.println("Player : "+pid+" Coneected with socket : "+s);
             ps.println(msgToPlayer);
             players.add(this) ; 
             
             start() ;
             
         } catch (Exception ex) {
             ex.printStackTrace();
         }
        
    }
    
    @Override 
    public void run(){
        
        int tileX ,tileY , toID , fromID ; 
       
        String msgRecived ; 
        String [] msgInfoArr ; 
        
        try{
            while(true){
              
                msgRecived = dis.readLine() ; 
               // System.out.println("play: "+playInfo);
               if(msgRecived.length()>0){
                   System.out.println("MSG Rec : "+msgRecived);
               
               msgInfoArr = msgRecived.split(" ") ;
               
               if ("GAME".equals(msgInfoArr[0]) && "PLAY".equals(msgInfoArr[1])){
                
                fromID = Integer.parseInt( msgInfoArr[2]) ;    
                toID  = Integer.parseInt( msgInfoArr[3] ); // Recieve id of player to send it to 
                tileX = Integer.parseInt( msgInfoArr[4] ); // Recieve Row Of play
                tileY = Integer.parseInt( msgInfoArr[5] ); // Recieve Col of play 
                
              msgToPlayer = "GAME PLAY "+fromID+" "+toID+" "+tileX+" "+tileY ;
             // send play to opponent 
              send(msgToPlayer) ; 
               } 
               
               }
            }         
//    
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void  send(String msg){
        
//        String playInfoToSend = tileX+" "+tileY ;
//        System.out.println("playInfoToSend "+playInfoToSend );


        System.out.println("Message From Send Fun : "+msg);
   String msgArr[] = msg.split(" ") ;
   int toID  ; 
   if ("GAME".equals(msgArr[0]) && "PLAY".equals(msgArr[1])){
   
       toID  = Integer.parseInt( msgArr[3] )  ; 
    for (GameHandler gh : players){
            if (gh.pid == toID){
               
               gh.ps.println(msg);
               //gh.ps.println(tileY);
           
            }
        }
   }
   
       
    }
}
