import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;


public class GoGameClient {


    private JFrame frame = new JFrame("GoGame");
    private JLabel messageLabel = new JLabel("Enemy turn");

    private Cross[] board;
    private Cross currentCross;

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    private int GameSize;

    private GoGameClient(String serverAddress, int boardSize) throws Exception {

        socket = new Socket(serverAddress, 58901);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

        GameSize = boardSize;

        frame.getContentPane().add(messageLabel, BorderLayout.NORTH);

        var crossPanel = new JPanel();
        crossPanel.setLayout(new GridLayout( GameSize , GameSize ));
        board = new Cross[GameSize*GameSize];
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Cross( i , GameSize);
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentCross = board[j];
                    out.println("MOVE " + j);
                }
            });
            crossPanel.add(board[i]);
        }
        frame.getContentPane().add(crossPanel, BorderLayout.CENTER);
    }

    private void play() throws Exception {
        try {
            var response = in.nextLine();
            var pawn = response.charAt(8);
            var opponentPawn = pawn == 'B' ? 'W' : 'B';
            out.println("SIZE " + GameSize);
            while (in.hasNextLine()) {
                response = in.nextLine();
                if (response.startsWith("VALID_MOVE")) {
                    messageLabel.setText("Enemy turn");
                    currentCross.setPawn(pawn);
                    currentCross.repaint();
                } else if (response.startsWith("OPPONENT_MOVED")) {
                    var loc = Integer.parseInt(response.substring(15));
                    board[loc].setPawn(opponentPawn);
                    board[loc].repaint();
                    messageLabel.setText("Opponent moved, your turn");
                } else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                } else if (response.startsWith("BOARD_FULL")) {
                    JOptionPane.showMessageDialog(frame, "Board is full");
                    break;
                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(frame, "Other player left");
                    break;
                }
            }
            out.println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
            frame.dispose();
        }
    }

    static class Cross extends JPanel {

        JLabel label = new JLabel();

        String placement;

        Cross(int i, int size) {


            setLayout(new GridBagLayout());

            if(i == 0) placement =".\\icon\\nwC.png";
                else if(i == size-1) placement =".\\icon\\neC.png";
                else if(i == size*size-1) placement =".\\icon\\seC.png";
                else if(i == size*(size-1)) placement =".\\icon\\swC.png";
                else if(i < size) placement =".\\icon\\nC.png";
                else if(i > size*(size-1)) placement =".\\icon\\sC.png";
                else if(i%size == 0) placement =".\\icon\\wC.png";
                else if(i%size == size-1) placement =".\\icon\\eC.png";
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

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }

        GoGameClient client = new GoGameClient(args[0], Integer.parseInt(args[1]));
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.pack();
        client.frame.setVisible(true);
        client.frame.setResizable(false);
        client.play();
    }
}