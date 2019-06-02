package com.cqsi.Methods;

import javax.swing.*;

public class otherMethods {

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
