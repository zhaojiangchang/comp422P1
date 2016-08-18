/**
 * Created by JackyChang on 16/8/18.
 */
public class Main {
    public static void main(String[] args) {
        GUI gui = new GUI();
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.createAndShowGUI();
            }
        });
    }
}
