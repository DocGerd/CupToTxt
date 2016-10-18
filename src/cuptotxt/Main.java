/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cuptotxt;

/**
 *
 * @author Patrick Kuhn
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ConvertCupToTxt.run();
            }
        });
    }

    private Main() {
        throw new AssertionError();
    }
}
