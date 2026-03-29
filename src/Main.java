public class Main {
    public static void main(String[] args) {
        // Create UI
        UI ui = new UI();

        String username = ui.getUsername();
        ui.startGame(username);
    }
}
