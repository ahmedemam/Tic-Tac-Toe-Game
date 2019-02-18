package clientapplication;

public class Session {

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Session(String userID, String userName, String userScore) {
        this.userID = userID;
        this.userName = userName;
        this.userScore = userScore;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    private String userID;
    private String userName;
    private String userScore;
}
