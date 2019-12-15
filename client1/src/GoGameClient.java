import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;


public class GoGameClient {

    private Socket socket;
    private Scanner in;
    private static PrintWriter out;
    private int GameSize;
    private GUI gui ;

    private GoGameClient(String serverAddress, int boardSize) throws Exception {

        socket = new Socket(serverAddress, 58901);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
        GameSize = boardSize;
        gui = new GUI(GameSize);

    }

    static void sendMoveMessage(int j, int x){
        out.println("MOVE " + j + ";" + x);
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

                    gui.currentCross.setPawn(pawn);
                    gui.currentCross.repaint();
                    gui.messageLabel.setText("Enemy turn");

                } else if (response.startsWith("OPPONENT_MOVED")) {

                    String[] cords = response.substring(15).split(";");

                    gui.board[Integer.parseInt(cords[0])][Integer.parseInt(cords[1])].setPawn(opponentPawn);
                    gui.board[Integer.parseInt(cords[0])][Integer.parseInt(cords[1])].repaint();
                    gui.messageLabel.setText("Opponent moved, your turn");

                }else if (response.startsWith("MOVE")) {

                    String[] cords = response.substring(5).split(";");

                    gui.board[Integer.parseInt(cords[0])][Integer.parseInt(cords[1])].setPawn('C');
                    gui.board[Integer.parseInt(cords[0])][Integer.parseInt(cords[1])].repaint();

                }
                else if (response.startsWith("MESSAGE")) {

                    gui.messageLabel.setText(response.substring(8));

                } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                    JOptionPane.showMessageDialog(gui.frame, "Other player left");
                    break;
                }
            }
            out.println("QUIT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
            gui.frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Pass the server IP and board size");
            return;
        }

        GoGameClient client = new GoGameClient(args[0],Integer.parseInt(args[1]));

        client.play();

    }
}