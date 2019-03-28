package com.cqsi;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.text.*;

class editor extends JFrame implements ActionListener {

    // Text component
    JTextPane t;

    // Frame
    JFrame f;

    private boolean saved = false;
    private methods m;

    // colors
    private Color lilac = new Color(187, 97, 154);
    private Color yellow = new Color(204, 204, 76);

    // Constructor
    public editor()
    {

        m = new methods();

        // Create a frame
        f = new JFrame("Casimirs Text Editor");

        try {
            // Set metl look and feel
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

            // Set theme to ocean
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        }
        catch (Exception e) {
        }

        // Create a menubar
        JMenuBar mb = new JMenuBar();

        // Create amenu for menu
        JMenu m1 = new JMenu("File");

        // Create menu items
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");
        JMenuItem mi9 = new JMenuItem("Print");

        // Add action listener
        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        mi9.addActionListener(this);


        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        m1.add(mi9);

        // Create amenu for menu
        JMenu m2 = new JMenu("Edit");

        // Create menu items
        JMenuItem mi4 = new JMenuItem("cut");
        JMenuItem mi5 = new JMenuItem("copy");
        JMenuItem mi6 = new JMenuItem("paste");

        // Add action listener
        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this);

        m2.add(mi4);


        m2.add(mi5);
        m2.add(mi6);

        JMenuItem mc = new JMenuItem("Run");

        mc.addActionListener(this);

        mb.add(m1);
        mb.add(m2);
        mb.add(mc);

        // making certain words colored
        final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, lilac);
        final AttributeSet attrGreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.WHITE);
        final AttributeSet attrYellow = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, yellow);

        DefaultStyledDocument doc = new DefaultStyledDocument() {

            // method
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                int before = m.findLastNonWordChar(text, offset);
                if (before < 0) before = 0;
                int after = m.findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches("(\\W)*(from|import)")) {
                            setCharacterAttributes(wordL, wordR - wordL, attr, false);
                        } else if(text.substring(wordL, wordR).matches("(\\W)*(def)")){
                            setCharacterAttributes(wordL, wordR - wordL, attrYellow, false);
                        }else {
                            setCharacterAttributes(wordL, wordR - wordL, attrGreen, false);
                        }
                        wordL = wordR;
                    }
                    wordR++;
                }
            }

            //method
            public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = m.findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = m.findFirstNonWordChar(text, offs);

                if (text.substring(before, after).matches("(\\W)*(private|public|protected)")) {
                    setCharacterAttributes(before, after - before, attr, false);
                }else if (text.substring(before, after).matches("(\\W)*(def)")) {
                    setCharacterAttributes(before, after - before, attrYellow, false);
                }else {
                    setCharacterAttributes(before, after - before, attrGreen, false);
                }
            }
        };

        // Text component
        t = new JTextPane(doc);
        t.setFont(new Font("Consolas", Font.PLAIN, 20));
        t.setCaretColor(Color.YELLOW);
        t.setBackground(Color.BLACK);
        t.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JScrollPane scroller = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // adding everything to the screen
        f.setJMenuBar(mb);
        f.add(scroller);
        f.setSize(800, 970);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // If a button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();



        if (s.equals("cut")) {
            t.cut();
        }
        else if (s.equals("copy")) {
            t.copy();
        }
        else if (s.equals("paste")) {
            t.paste();
        }
        else if (s.equals("Save")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsSaveDialog function to show the save dialog
            int r = j.showSaveDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {

                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());


                try {
                    // Create a file writer
                    FileWriter wr = new FileWriter(fi, false);

                    // Create buffered writer to write
                    BufferedWriter w = new BufferedWriter(wr);

                    // Write
                    w.write(t.getText());
                    w.write("\ninput(\"Press Enter to exit... \")");

                    w.flush();
                    w.close();
                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }

                saved = true;
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }


        else if (s.equals("Print")) {
            try {
                // print the file
                t.print();
            }
            catch (Exception evt) {
                JOptionPane.showMessageDialog(f, evt.getMessage());
            }
        }
        else if (s.equals("Open")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // If the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());


                try {
                    // String
                    String s1 = "", sl = "";

                    // File reader
                    FileReader fr = new FileReader(fi);

                    // Buffered reader
                    BufferedReader br = new BufferedReader(fr);

                    // Initilize sl
                    sl = br.readLine();

                    // Take the input from the file
                    while ((s1 = br.readLine()) != null) {
                        sl = sl + "\n" + s1;
                    }

                    // Set the text
                    t.setText(sl);

                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }
        else if (s.equals("New")) {
            t.setText("");
        }else if(s.equals("Run")){
            if(!saved){
                JFileChooser j = new JFileChooser("f:");

                // Invoke the showsSaveDialog function to show the save dialog
                int r = j.showSaveDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {

                    // Set the label to the path of the selected directory
                    File fi = new File(j.getSelectedFile().getAbsolutePath());


                    try {
                        // Create a file writer
                        FileWriter wr = new FileWriter(fi, false);

                        // Create buffered writer to write
                        BufferedWriter w = new BufferedWriter(wr);

                        // Write
                        w.write(t.getText());
                        w.write("\ninput(\"Press Enter to exit... \")");

                        w.flush();
                        w.close();
                    }
                    catch (Exception evt) {
                        JOptionPane.showMessageDialog(f, evt.getMessage());
                    }

                    try {
                        Desktop.getDesktop().open(fi);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }else{
                JFileChooser j = new JFileChooser("f:");

                // Invoke the showsOpenDialog function to show the save dialog
                int r = j.showOpenDialog(null);

                // If the user selects a file
                if (r == JFileChooser.APPROVE_OPTION) {
                    // Set the label to the path of the selected directory
                    File fi = new File(j.getSelectedFile().getAbsolutePath());

                    try {
                        Desktop.getDesktop().open(fi);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                saved = false;
            }
        }
    }

    // editor class
    public static void main(String args[])
    {
        editor e = new editor();
    }
}