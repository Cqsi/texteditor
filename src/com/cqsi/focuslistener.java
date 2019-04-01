package com.cqsi;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class focuslistener implements FocusListener {

    private boolean tfbool = false;

    @Override
    public void focusGained(FocusEvent e) {
        tfbool = true;
    }

    @Override
    public void focusLost(FocusEvent e) {
        tfbool = false;
    }

    public boolean isTfbool() {
        return tfbool;
    }
}
