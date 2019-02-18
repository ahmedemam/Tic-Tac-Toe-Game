package clientapplication;
import java.util.LinkedHashMap;

public class PlayerDataSession {

    public static LinkedHashMap<String, String> playerDataSession = new LinkedHashMap<>();

    public static void print_x() {
        System.out.println(playerDataSession.values());
    }

    public static LinkedHashMap<String, String> getPlayerData() {
        return playerDataSession;
    }

    public static void setPlayerData(LinkedHashMap<String, String> playerData) {
        PlayerDataSession.playerDataSession = playerData;
    }

}
