/*
 * @(#)Badoing.java
 */
package cuptotxt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
public class ConvertCupToTxt extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    private final JFileChooser jfc;
    private final JFileChooser jfs;
    private final JButton open;
    private final JButton convert;
    private final JButton save;
    private final JButton exit;
    private final JLabel labelCurrentFile;
    private final JLabel labelConverted;
    private final JLabel labelWritten;
    
    private String inputFile;
    private String outputFile;

    public ConvertCupToTxt() {
        super();
        jfc = new JFileChooser();
        jfc.setFileFilter(new FileNameExtensionFilter("SeeYou Waypoint File .cup", "cup"));
        jfs = new JFileChooser();
        jfs.setFileFilter(new FileNameExtensionFilter("pocket*Strepla Turnpoints .txt", "txt"));

        open = new JButton("Open CUP File");
        open.setActionCommand("open");
        open.addActionListener((ActionListener) this);

        convert = new JButton("Convert CUP to TXT");
        convert.setEnabled(false);
        convert.setActionCommand("convert");
        convert.addActionListener((ActionListener) this);

        save = new JButton("Save TXT File");
        save.setEnabled(false);
        save.setActionCommand("save");
        save.addActionListener((ActionListener) this);

        exit = new JButton("Exit");
        exit.setActionCommand("exit");
        exit.addActionListener((ActionListener) this);
        
        labelCurrentFile = new JLabel();
        labelCurrentFile.setText("Please select file.");
        labelConverted = new JLabel();
        labelConverted.setText("No file converted.");
        labelWritten = new JLabel();
        labelWritten.setText("No data to save.");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(open);
        this.add(labelCurrentFile);
        this.add(convert);
        this.add(labelConverted);
        this.add(save);
        this.add(labelWritten);
        this.add(exit);        
    }

    public static void run() {
        frame = new JFrame("CUP to TXT Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ConvertCupToTxt bdg = new ConvertCupToTxt();
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
        } else if ("open".equals(cmd)) {
            int returnVal = jfc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = jfc.getSelectedFile();
                if (f != null) {
                    inputFile = Tools.readFile(f);
                    save.setEnabled(false);
                    labelConverted.setText("No file converted.");
                    labelWritten.setText("No data to save.");
                    if (inputFile != null) {
                        convert.setEnabled(true);
                        labelCurrentFile.setText(f.getName() + " loaded.");
                    }
                }
            }
        } else if ("convert".equals(cmd)) {
            if (inputFile != null) {
                outputFile = cupToTxt(inputFile);
                labelConverted.setText("File converted!");
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

    /**
     * Convert CUP to pocket*StrePla txt.
     * "001Hahnweide",001HW,,4837.900N,00925.900E,360.0m,2,310,600.0m,"123,250","Competition Airfield"
     * to
     * 1;00001;001Hahnweide;4837900N;00925900E;360;AS;123,250;31/13;;600;3;3;Competition Airfield
     * 1;ID;Name;N;E;altitude;style;freq;rwy;;length;wth;wth;comment
     * @param cupstring String with the entire cup file w/o the leading line
     * @return the pocket*Strepla txt file
     */
    public static String cupToTxt(final String cupstring) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        String[] ss = cupstring.split("\n");
        for (String s : ss) {
            sb.append(parseLine(i, s));
            sb.append('\n');
            ++i;
        }
        return sb.toString();
    }

    private static String parseLine(final int i, final String line) {
        assert i > 0;
        if (line == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("1;");
        sb.append(Tools.intToString(i));
        sb.append(';');

        String[] items = line.split(",");
        if (items.length != 12 && items.length != 11) {
            return "";
        }
        // ID
        sb.append(items[0].replaceAll("\"", ""));
        sb.append(';');
        // lat
        sb.append(items[3].replaceAll("[.]", ""));
        sb.append(';');
        // lon
        sb.append(items[4].replaceAll("[.]", ""));
        sb.append(';');
        // altitude
        sb.append(items[5].substring(0, items[5].indexOf('.')));
        sb.append(';');
        // style
        final int type = Integer.parseInt(items[6]);
        switch (type) {
            case 1:
                sb.append("W"); // 5;0
                break;
            case 2:
                sb.append("AS"); // 3;3
                break;
            case 3:
                sb.append("L"); // 5;3
                break;
            case 4:
                sb.append("AG"); // 5;3
                break;
            case 5:
                sb.append("AS"); // 1;3
                break;
            default:
                assert false;
        }
        sb.append(';');
        // frequency
        // FIXME
        int offset = -1;
        if (type != 1 && !items[9].isEmpty()) {
            if (items[9].contains(".") || (items[9].startsWith("\"") && items[9].endsWith("\""))) {
                sb.append(items[9].replaceAll("\"", ""));
            } else {
                sb.append(items[9].replaceAll("\"", "")).append(',').append(items[10].replaceAll("\"", ""));
                offset = 0;
            }
        }
        sb.append(';');
        // RWY
        if (type != 1) {
            sb.append(getRwyOfHeading(items[7]));
        }
        sb.append(';');
        // no idea
        sb.append(';');
        // RWY length
        sb.append(items[8].substring(0, items[8].indexOf('.')));
        sb.append(';');
        // wth
        switch (Integer.parseInt(items[6])) {
            case 1:
                sb.append("5;0"); // 5;0
                break;
            case 2:
                sb.append("3;3"); // 3;3
                break;
            case 3:
                sb.append("5;3"); // 5;3
                break;
            case 4:
                sb.append("5;3"); // 5;3
                break;
            case 5:
                sb.append("1;3"); // 1;3
                break;
            default:
                assert false;
        }
        sb.append(';');
        // comment
        sb.append(items[11 + offset].replaceAll("\"", ""));

        return sb.toString();
    }

    /**
     * Returns the runway numbers of a given runway heading
     * @param hdg main runway heading
     * @return the runway numbers in format "main runway/opposite runway"
     */
    private static String getRwyOfHeading(final String hdg) {
        if (hdg == null || hdg.isEmpty()) {
            return "";
        }
        String result = "";
        try {
            final int heading = Integer.parseInt(hdg);
            result = (heading / 10) + "/" + ((heading + 180) % 360 / 10);
        } catch (NumberFormatException ex) {
            result = "";
        }
        return result;
    }
}
