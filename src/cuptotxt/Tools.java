/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cuptotxt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Patrick Kuhn
 */
public class Tools {

    private Tools() {
        throw new AssertionError();
    }

    public static String readFile(final File file) {
        BufferedReader br = null;
        List<String> list = Collections.synchronizedList(new LinkedList<String>());
        try {
            br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                list.add(br.readLine());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.INFO, null, ex);
            return null;
        } catch (IOException ex) {
            throw new IOError(ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        // no header etc...
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Read a file. Omits all lines without quotes, so header and comments
     * are not in it anymore.
     * @param file the file
     * @return a String with all lines of the file.
     */
    public static String readFileOnlyData(File file) {
        final String[] inp = readFile(file).split("\n");
        final StringBuilder sb = new StringBuilder();
        for (String s : inp) {
            if (s.startsWith("\"")) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    public static boolean writeFile(String output, File file) {
        BufferedWriter bw = null;
        boolean result = false;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(output);
        } catch (IOException ex) {
            throw new IOError(ex);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    result = true;
                } catch (IOException ex) {
                    Logger.getLogger(ConvertCupToTxt.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    public static String intToString(final int i) {
        // 00000
        String s = Integer.toString(i);
        while (s.length() < 5) {
            s = '0' + s;
        }
        return s;
    }
    
    /**
     * Convert a String to an int.
     * @param s the String to convert.
     * @return the int of the String or <tt>-1</tt> if unable to parse.
     */
    public static int stringToInt(final String s) {
        int i = -1;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            Logger.getLogger(ConvertCupToTxt.class.getName()).log(Level.FINE, null, ex);
        }
        return i;
    }
}
