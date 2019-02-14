/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tttapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author MKhaled
 */
public class Server {
    
      ServerSocket ss ; 

    
    public Server (){
        try {
            ss = new ServerSocket(5552);
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        while(true){
            try { 
                 Socket s = ss.accept() ;
                 new GameHandler(s); 
                 
             
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Server Started!!!");
        new Server(); 
    }
    
}
