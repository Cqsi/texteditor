package com.cqsi;

import javax.swing.*;

public class methods {

    public int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    public int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

    public String getPath(){

        // Create an object of JFileChooser class
        JFileChooser j = new JFileChooser("f:");

        // Invoke the showsSaveDialog function to show the save dialog
        int r = j.showDialog(null, "Lock File");

        if (r == JFileChooser.APPROVE_OPTION) {
            return j.getSelectedFile().getAbsolutePath();
        }else {
            return null;
        }
    }
}
