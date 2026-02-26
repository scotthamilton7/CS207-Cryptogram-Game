import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Players {
    private List<Player> allPlayers = new ArrayList<Player>();
    private String playersFile = "src/playerData.csv";

    public void addPlayer(Player p) { allPlayers.add(p); }

    public void clearPlayerData() {
        try {
            FileWriter fw = new FileWriter(playersFile);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }

    public void savePlayers() {
        try {
            FileWriter fw = new FileWriter(playersFile);
            // This should be uncommented when we have a function to read player data, so we can modify the file in place
            //clearPlayerData();
            for (Player p : allPlayers) {
                fw.write(p.getUsername() + "," + p.getAccuracy() + "," + p.getTotalGuesses() + "," + p.getNumCryptogramsPlayed() + "," + p.getNumCryptogramsCompleted());
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving player data: " + e.getMessage());
        }
    }

    // Unsure if this should return the Player (which seems pointless?) or return boolean if the player is in the saved data
    public Player findPlayer(Player p) {
        for (Player player : allPlayers) {
            if (player == p) {
                return player;
            }
        }
        return null;
    }

    public double[] getAllPlayersAccuracies() {
        double[] accuracies = new double[allPlayers.size()];
        for (int i = 0; i < allPlayers.size(); i++) {
            accuracies[i] = allPlayers.get(i).getAccuracy();
        }
        return accuracies;
    }

    public int[] getAllPlayersCryptogramsPlayed() {
        int[] cryptogramsPlayedData = new int[allPlayers.size()];
        for (int i = 0; i < allPlayers.size(); i++) {
            cryptogramsPlayedData[i] = allPlayers.get(i).getNumCryptogramsPlayed();
        }
        return cryptogramsPlayedData;
    }

    public int[] getAllPlayersCryptogramsCompleted() {
        int[] cryptogramsCompletedData = new int[allPlayers.size()];
        for (int i = 0; i < allPlayers.size(); i++) {
            cryptogramsCompletedData[i] = allPlayers.get(i).getNumCryptogramsCompleted();
        }
        return cryptogramsCompletedData;
    }

}
