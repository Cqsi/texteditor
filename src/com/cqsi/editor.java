package com.cqsi;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.metal.*;
import javax.swing.text.*;

class editor extends JFrame implements ActionListener, KeyListener {

    // Frame
    private JFrame f;

    // Swing
    private JTextPane t;
    private JTextField tf;

    // Variables
    private boolean saved = false, isLocked = false;
    private methods m;
    private focuslistener foc;
    private String path;

    // Default colors
    private Color lilac = new Color(187, 97, 154);
    private Color yellow = new Color(204, 204, 76);
    private Color darkblue = new Color(106,90,205);
    private Color darkred = new Color(128,0,0);
    private Color limegreen = new Color(50,205,50);

    // Fonts & Dimensions
    private Font consolas = new Font("Consolas", Font.PLAIN, 20);
    private Dimension d = new Dimension(80,40);

    // DefaultStyledDocument
    private DefaultStyledDocument doc;

    // Constructor
    public editor()
    {

        m = new methods();
        foc = new focuslistener();

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

        // m1
        JMenu m1 = new JMenu("File");

        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");
        JMenuItem mi9 = new JMenuItem("Print");

        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        mi9.addActionListener(this);

        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        m1.add(mi9);

        // m2
        JMenu m2 = new JMenu("Edit");

        JMenuItem mi4 = new JMenuItem("cut");
        JMenuItem mi5 = new JMenuItem("copy");
        JMenuItem mi6 = new JMenuItem("paste");

        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this);

        m2.add(mi4);
        m2.add(mi5);
        m2.add(mi6);


        // m3
        JMenu m3 = new JMenu("Run");

        JMenuItem mc = new JMenuItem("Run");
        JMenuItem lock = new JMenuItem("Lock");
        JMenuItem unlock = new JMenuItem("Unlock");

        m3.add(mc);
        m3.add(lock);
        m3.add(unlock);

        mc.addActionListener(this);
        lock.addActionListener(this);
        unlock.addActionListener(this);

        // m4
        JMenu m4 = new JMenu("Help");

        JMenuItem help = new JMenuItem("Help");
        m4.add(help);

        help.addActionListener(this);

        // adding all menus
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        mb.add(m4);

        // making certain words colored
        final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, lilac);
        final AttributeSet attrWhite = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.WHITE);
        final AttributeSet attrYellow = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, yellow);
        final AttributeSet attrDarkBlue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, darkblue);
        final AttributeSet attrDarkRed = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, darkred);
        final AttributeSet attrLimeGreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, limegreen);

        doc = new DefaultStyledDocument() {

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
                        } else if(text.substring(wordL, wordR).matches("(\\W)*(def|or|not|is|while|class|if|in|else|elif|for)")){
                            setCharacterAttributes(wordL, wordR - wordL, attrYellow, false);
                        }else if(text.substring(wordL, wordR).matches("(\\W)*(#)")){
                            setCharacterAttributes(wordL, wordR - wordL, attrDarkBlue, false);
                        }else if(text.substring(wordL, wordR).matches("(\\W)*(print|input|range)")){
                            setCharacterAttributes(wordL, wordR - wordL, attrLimeGreen, false);
                        }else if(text.substring(wordL, wordR).matches("(\\W)*(True|False)")){
                            setCharacterAttributes(wordL, wordR - wordL, attrDarkRed, false);
                        }else {
                            setCharacterAttributes(wordL, wordR - wordL, attrWhite, false);
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

                if (text.substring(before, after).matches("(\\W)*(from|import)")) {
                    setCharacterAttributes(before, after - before, attr, false);
                }else if (text.substring(before, after).matches("(\\W)*(def|or|not|is|while|class|if|in|else|elif|for)")) {
                    setCharacterAttributes(before, after - before, attrYellow, false);
                }else if (text.substring(before, after).matches("(\\W)*(#)")) {
                    setCharacterAttributes(before, after - before, attrDarkBlue, false);
                }else if (text.substring(before, after).matches("(\\W)*(print|input|range)")) {
                    setCharacterAttributes(before, after - before, attrLimeGreen, false);
                }else if (text.substring(before, after).matches("(\\W)*(True|False)")) {
                    setCharacterAttributes(before, after - before, attrDarkRed, false);
                }else {
                    setCharacterAttributes(before, after - before, attrWhite, false);
                }
            }
        };

        // Text component
        t = new JTextPane(doc);
        t.setFont(consolas);
        t.setCaretColor(Color.YELLOW);
        t.addKeyListener(this);
        t.setBackground(Color.BLACK);
        t.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JScrollPane scroller = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // textfield
        tf = new JTextField();
        tf.setBackground(Color.BLACK);
        tf.setFont(consolas);
        tf.setForeground(Color.WHITE);
        tf.addKeyListener(this);
        tf.addFocusListener(foc);
        tf.setCaretColor(Color.YELLOW);
        tf.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        // adding everything to the screen
        f.setJMenuBar(mb);
        f.add(scroller);
        f.add(tf, BorderLayout.SOUTH);
        f.setSize(800, Toolkit.getDefaultToolkit().getScreenSize().height-50);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // If a button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();


        // Create an object of JFileChooser class
        //JFileChooser j = new JFileChooser("f:");
        JFileChooser j;

        // Invoke the showsOpenDialog function to show the save dialog
        //int r = j.showOpenDialog(null);
        int r;

        switch(s) {
            case "cut":
                t.cut();
                break;
            case "copy":
                t.copy();
                break;
            case "paste":
                t.paste();
                break;
            case "Save":
                save(false);
                break;
            case "Print":

                try {
                    // print the file
                    t.print();
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }

                break;
            case "Open":

                j = new JFileChooser("f:");
                r = j.showOpenDialog(null);

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

                    } catch (Exception evt) {
                        JOptionPane.showMessageDialog(f, evt.getMessage());
                    }
                }

                break;
            case "New":
                t.setText("");
                break;
            case "Run":

                if (!saved) {
                    save(true);
                } else {
                    j = new JFileChooser("f:");
                    r = j.showOpenDialog(null);

                    // If the user selects a file
                    if (r == JFileChooser.APPROVE_OPTION) {
                        // Set the label to the path of the selected directory
                        File fi = new File(j.getSelectedFile().getAbsolutePath());

                        try {
                            Desktop.getDesktop().open(fi);
                        } catch (Exception excep) {
                            JOptionPane.showMessageDialog(null, "The file doesn't exist!");
                        }
                    }
                    saved = false;
                }

                break;
            case "Lock":
                lock();
                break;
            case "Unlock":
                unlock();
                break;
            case "Help":
                JOptionPane.showMessageDialog(null, "Welcome to Casimir's Python TextEditor.\n\nHow to use:\n1. Write Python code.\n2. Click run.\n\nYou can also \"lock\" the file, which means that that file is automatically run when you click \"run\".\n\nCommands: \n\n:r - Save and run\n:s - Save\n:l - Lock\n:git - Opens git bash\n:cmd - Opens CMD\n:u - Unlock\n:github - Opens Github\n:w3 - Opens W3-Schools");
                break;
            default:

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            if(!foc.isTfbool()){
                tf.requestFocusInWindow();
            }else{
                t.requestFocusInWindow();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            if(foc.isTfbool()){

                String command = tf.getText();

                switch (command) {
                    case ":r":
                        save(true);
                        tf.setText("");
                        break;
                    case ":s":
                        save(false);
                        tf.setText("");
                        break;
                    case ":l":
                        lock();
                        tf.setText("");
                        break;
                    case ":u":
                        unlock();
                        tf.setText("");
                        break;
                    case ":cmd":
                        try {
                            Desktop.getDesktop().open(new File("C:\\Windows\\system32\\cmd.exe"));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        tf.setText("");
                        break;
                    case ":git":
                        try {
                            Desktop.getDesktop().open(new File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Git\\Git Bash.lnk"));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        tf.setText("");
                        break;
                    case ":github":
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/"));
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case ":w3":
                        try {
                            Desktop.getDesktop().browse(new URI("https://www.w3schools.com/python/"));
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    default:
                        tf.setForeground(Color.RED);
                        int length = tf.getText().length();
                        tf.getDocument().addDocumentListener(new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                changed();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                changed();
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                changed();
                            }

                            private void changed() {
                                if (tf.getText().length() != length) {
                                    tf.setForeground(Color.WHITE);
                                    tf.getDocument().removeDocumentListener(this);
                                }
                            }
                        });
                        break;
                }

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}



    private void save(boolean isRun){

        if(!isLocked){
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

                    if(isLocked){
                        JOptionPane.showMessageDialog(null, "Saved " + path);
                    }
                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(f, evt.getMessage());
                }

                if(isRun){
                    try {
                        Desktop.getDesktop().open(fi);
                    } catch (Exception excep) {
                        JOptionPane.showMessageDialog(null, "The file doesn't exist!");
                    }
                }

                saved = true;
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(f, "the user cancelled the operation");
        }else{

            // Set the label to the path of the selected directory
            File fi = new File(path);

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

            if(isRun){
                try {
                    Desktop.getDesktop().open(fi);
                } catch (Exception excep) {
                    JOptionPane.showMessageDialog(null, "The file doesn't exist!");
                }
            }
        }
    }

    private void lock(){
        path = m.getPath();
        isLocked = true;
        JOptionPane.showMessageDialog(null, "Locked your file!");
    }

    private void unlock(){
        if(isLocked){
            isLocked = false;
            JOptionPane.showMessageDialog(null, "Unlocked you file!");
        }else{
            JOptionPane.showMessageDialog(null, "You haven't locked a file yet!");
        }
    }

}