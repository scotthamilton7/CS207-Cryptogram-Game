import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard extends JDialog {

    public Leaderboard(UI ui, JFrame parent) {
        super(parent, "Leaderboard", true);

        setSize(300, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        buildLeaderboard(ui);

        setVisible(true);
    }

    private void buildLeaderboard(UI parent) {
        String[] columns = {"", "Name", "Cryptograms Solved"};

        List<Player> playerList = new ArrayList<>(parent.getPlayers());
        playerList.sort((a, b) ->
                Integer.compare(b.getNumCryptogramsCompleted(), a.getNumCryptogramsCompleted()));
        Object[][] data = new Object[playerList.size()][3];
        for (int i = 0; i < playerList.size(); i++) {
            Player p = playerList.get(i);
            if (p != null) {
                data[i][0] = i + 1;
                data[i][1] = p.getUsername();
                data[i][2] = p.getNumCryptogramsCompleted();
            }
        }

        JTable table = new JTable(data, columns);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        add(new JScrollPane(table));
    }

}
