/*
 * @(#)Badoing.java
 */
package cuptotxt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Patrick Kuhn
 */
public class MergeTxt extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    private final JFileChooser jfc1;
    private final JFileChooser jfc2;
    private final JFileChooser jfs;
    private final JButton open1;
    private final JButton open2;
    private final JButton merge;
    private final JButton save;
    private final JButton exit;
    private final JLabel labelCurrentFile1;
    private final JLabel labelCurrentFile2;
    private final JLabel labelMerged;
    private final JLabel labelWritten;
    private String inputFile1;
    private String inputFile2;
    private String outputFile;

    public MergeTxt() {
        super();
        jfc1 = new JFileChooser();
        jfc1.setFileFilter(new FileNameExtensionFilter("pocket*Strepla Turnpoints .txt", "txt"));
        jfc2 = new JFileChooser();
        jfc2.setFileFilter(new FileNameExtensionFilter("pocket*Strepla Turnpoints .txt", "txt"));

        jfs = new JFileChooser();
        jfs.setFileFilter(new FileNameExtensionFilter("pocket*Strepla Turnpoints .txt", "txt"));

        open1 = new JButton("Open TXT File 1");
        open1.setActionCommand("open1");
        open1.addActionListener((ActionListener) this);

        open2 = new JButton("Open TXT File 2");
        open2.setActionCommand("open2");
        open2.addActionListener((ActionListener) this);

        merge = new JButton("Merge");
        merge.setEnabled(false);
        merge.setActionCommand("merge");
        merge.addActionListener((ActionListener) this);

        save = new JButton("Save TXT File");
        save.setEnabled(false);
        save.setActionCommand("save");
        save.addActionListener((ActionListener) this);

        exit = new JButton("Exit");
        exit.setActionCommand("exit");
        exit.addActionListener((ActionListener) this);

        labelCurrentFile1 = new JLabel();
        labelCurrentFile1.setText("Please select file one.");
        labelCurrentFile2 = new JLabel();
        labelCurrentFile2.setText("Please selecte file two");
        labelMerged = new JLabel();
        labelMerged.setText("No files merged yet.");
        labelWritten = new JLabel();
        labelWritten.setText("No data to save.");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(open1);
        this.add(labelCurrentFile1);
        this.add(open2);
        this.add(labelCurrentFile2);
        this.add(merge);
        this.add(labelMerged);
        this.add(save);
        this.add(labelWritten);
        this.add(exit);
    }

    public static void show(final JFrame parent) {
        frame = new JFrame("CUP to TXT Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MergeTxt bdg = new MergeTxt();
        bdg.setOpaque(true);
        frame.setContentPane(bdg);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final String cmd = e.getActionCommand();
        if ("exit".equals(cmd)) {
            frame.dispose();
        } else if ("open1".equals(cmd)) {
            int returnVal = jfc1.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = jfc1.getSelectedFile();
                if (f != null) {
                    inputFile1 = Tools.readFile(f);
                    save.setEnabled(false);
                    labelCurrentFile1.setText("No file converted.");
                    labelWritten.setText("No data to save.");
                    if (inputFile1 != null) {
                        open2.setEnabled(true);
                        labelCurrentFile1.setText(f.getName() + " loaded.");
                    }
                }
            }
        } else if ("open2".equals(cmd)) {
            int returnVal = jfc2.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = jfc2.getSelectedFile();
                if (f != null) {
                    inputFile2 = Tools.readFile(f);
                    save.setEnabled(false);
                    labelCurrentFile2.setText("No file converted.");
                    labelWritten.setText("No data to save.");
                    if (inputFile2 != null) {
                        merge.setEnabled(true);
                        labelCurrentFile2.setText(f.getName() + " loaded.");
                    }
                }
            }
        } else if ("convert".equals(cmd)) {
            if (inputFile1 != null && inputFile2 != null) {
                outputFile = mergeTxt(inputFile1, inputFile2);
                labelMerged.setText("Files merged!");
                save.setEnabled(true);
            }
        } else if ("save".equals(cmd)) {
            if (outputFile != null) {
                int returnVal = jfs.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File saveTo = jfs.getSelectedFile();
                    Tools.writeFile(outputFile, saveTo);
                    labelWritten.setText("Saved to " + saveTo.getName() + ".");
                }
            }
        }
    }

    public static String mergeTxt(final String s1, final String s2) {
        final String[] s1arr = s1.split("\n");
        final String[] s2arr = s2.split("\n");
        final List<String[]> l1 = new LinkedList<String[]>();
        final List<String[]> l2 = new LinkedList<String[]>();
        // build both lists
        for (String s : s1arr) {
            l2.add(s.split(";"));
        }
        for (String s : s2arr) {
            l2.add(s.split(";"));
        }
        // find maximum of first list
        int max = 0;
        for (String[] s : l1) {
            int i = Tools.stringToInt(s[1]);
            if (i > max) {
                max = i;
            }
        }
        // change all numbers of second list accordingly
        for (int i = max; !l2.isEmpty(); ++i) {
            String[] s = l2.remove(0);
            s[1] = Tools.intToString(i);
            l1.add(s);
        }

        final StringBuilder sb = new StringBuilder();
        for (String[] s : l1) {
            for (String b : s) {
                sb.append(b);
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
