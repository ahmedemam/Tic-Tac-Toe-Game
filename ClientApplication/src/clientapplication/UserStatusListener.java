/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

/**
 *
 * @author rahma
 */
public interface UserStatusListener {
    public void online (String userName , String userScore );
    public void offline(String userName, String userScore);
    
}
