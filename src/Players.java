import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Players {
    private List<Player> allPlayers = new ArrayList<Player>(); // Creating a List to store all Player objects
    private String playersFile = "src/playerData.csv"; // Pathway to CSV file were all the player data is stored

    // Method to add new player
    public void addPlayer(Player p) { allPlayers.add(p); }

    // Method to clear player data 
    public void clearPlayerData() {
        try {
            // Opens file for writing and overwrites the existing contents 
            FileWriter fw = new FileWriter(playersFile);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            // Print error message if file operation fails
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }

    // Method to save all player data to CSV file
    public void savePlayers() {
        try {
            FileWriter fw = new FileWriter(playersFile);
            // This should be uncommented when we have a function to read player data, so we can modify the file in place
            //clearPlayerData();

            // Making each players data as a CSV line for file
            for (Player p : allPlayers) {
                fw.write(p.getUsername() + "," + p.getAccuracy() + "," + p.getTotalGuesses() + "," + p.getNumCryptogramsPlayed() + "," + p.getNumCryptogramsCompleted());
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e) {
            // Print error message if write operation fails
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
    
    // Method to get an array of all players accuracy values and return them
    public double[] getAllPlayersAccuracies() {
        double[] accuracies = new double[allPlayers.size()];
        for (int i = 0; i < allPlayers.size(); i++) { // Loop for size of allPlayers
            accuracies[i] = allPlayers.get(i).getAccuracy();
        }
        return accuracies;
    }

    // Method to get an array of all players number of cryptograms played and return them
    public int[] getAllPlayersCryptogramsPlayed() {
        int[] cryptogramsPlayedData = new int[allPlayers.size()];
        for (int i = 0; i < allPlayers.size(); i++) { // Loop for size of allPlayers
            cryptogramsPlayedData[i] = allPlayers.get(i).getNumCryptogramsPlayed();
        }
        return cryptogramsPlayedData;
    }

    // Method to get an array of all players number of cryptograms completed and return them
    public int[] getAllPlayersCryptogramsCompleted() {
        int[] cryptogramsCompletedData = new int[allPlayers.size()];
        for (int i = 0; i < allPlayers.size(); i++) { // Loop for size of allPlayers
            cryptogramsCompletedData[i] = allPlayers.get(i).getNumCryptogramsCompleted();
        }
        return cryptogramsCompletedData;
    }

}
