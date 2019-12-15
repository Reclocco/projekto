import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class GUI {

    JFrame frame = new JFrame("GoGame");
    JLabel messageLabel = new JLabel("Enemy turn");

    Cross[][] board;
    Cross currentCross;

    GUI(int boardSize){

        frame.getContentPane().add(messageLabel, BorderLayout.NORTH);

        var crossPanel = new JPanel();
        crossPanel.setLayout(new GridLayout( boardSize , boardSize ));
        board = new Cross[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int z = 0; z < boardSize; z++) {
                final int j = i;
                final int x = z;
                board[i][z] = new Cross( i , z , boardSize);
                board[i][z].addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        currentCross = board[j][x];
                        GoGameClient.sendMoveMessage(j,x);
                    }
                });
                crossPanel.add(board[i][z]);
            }
        }

        frame.getContentPane().add(crossPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

    }

    static class Cross extends JPanel {

        JLabel label = new JLabel();

        String placement;

        Cross(int row, int col, int size) {


            setLayout(new GridBagLayout());

            if(row == 0 && col == 0) placement =".\\icon\\nwC.png";
            else if(row == 0 && col == size-1) placement =".\\icon\\neC.png";
            else if(row == size-1 && col == size-1) placement =".\\icon\\seC.png";
            else if(row == size-1 && col == 0) placement =".\\icon\\swC.png";
            else if(row == 0) placement =".\\icon\\nC.png";
            else if(row == size-1) placement =".\\icon\\sC.png";
            else if(col == 0) placement =".\\icon\\wC.png";
            else if(col == size-1) placement =".\\icon\\eC.png";
            else  placement =".\\icon\\cC.png";

            label.setIcon(new ImageIcon(placement));

            add(label);
        }

        void setPawn(char pawn) {

            if(placement.indexOf('C')>-1) placement = placement.replace("C", pawn + "");
            else if(placement.indexOf('B')>-1) placement = placement.replace("B", pawn + "");
            else placement = placement.replace("W", pawn + "");

            label.setIcon(new ImageIcon(placement));

        }
    }
}
