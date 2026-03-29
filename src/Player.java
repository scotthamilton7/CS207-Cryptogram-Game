public class Player {
    // fields could be set to private, set to protected for now
    private String username;
    private double accuracy;
    private int totalGuesses;
    private int cryptogramsPlayed;
    private int cryptogramsCompleted;

    // Be able to create new empty Player
    public Player() {
        this.username = "";
        this.accuracy = 0;
        this.totalGuesses = 0;
        this.cryptogramsPlayed = 0;
        this.cryptogramsCompleted = 0;
    }

    public void updateUsername(String username) { this.username = username; }
    // This will have the accuracy calculation
    public void updateAccuracy() {
        this.accuracy = ((double) cryptogramsCompleted /  cryptogramsPlayed) * 100;
    }

    public void incrementCryptogramsPlayed(){ this.cryptogramsPlayed++; }

    public void incrementCryptogramsCompleted(){ this.cryptogramsCompleted++; }

    public void decrementCryptogramsCompleted(){ this.cryptogramsCompleted--; }

    public void incrementTotalGuesses(){ this.totalGuesses++; }

    public String getUsername() { return username; }

    public double getAccuracy() { return this.accuracy; }

    public int getNumCryptogramsCompleted() { return this.cryptogramsCompleted; }

    public int getNumCryptogramsPlayed() { return this.cryptogramsPlayed; }

    public int getTotalGuesses() { return this.totalGuesses; }

    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public void setTotalGuesses(int totalGuesses) { this.totalGuesses = totalGuesses; }

    public void setCryptogramsPlayed(int cryptogramsPlayed) { this.cryptogramsPlayed = cryptogramsPlayed; }

    public void setCryptogramsCompleted(int cryptogramsCompleted) { this.cryptogramsCompleted = cryptogramsCompleted; }
}
