package clientapplication;

import java.util.ArrayList;

public class SessionList {
    public static ArrayList<Session> usersSessionLists=new ArrayList();
    
    public static ArrayList<Session> getAllSessions(){
        return SessionList.usersSessionLists;
    }
    
    public static void addSession(Session session){
        SessionList.usersSessionLists.add(session);
    }
}
