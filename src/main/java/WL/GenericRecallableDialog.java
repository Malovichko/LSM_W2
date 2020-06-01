package WL;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import ij.IJ;
import ij.gui.GUI;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GenericRecallableDialog extends GenericDialogPlus implements AdjustmentListener, KeyListener, FocusListener {
    private Button[] buttons = new Button[20];
    private boolean[] buttons_touched = new boolean[20];
    private int butIndex;
    private int butTot;
    Thread thread;
    public final int WEST = 0;
    public final int CENTER = 1;

    public GenericRecallableDialog(String var1) {
        super(var1);
        this.setModal(false);
    }

    public GenericRecallableDialog(String var1, Frame var2) {
        super(var1, var2);
        this.setModal(false);
    }

    public void actionPerformed(ActionEvent var1) {
        super.wasCanceled = var1.getSource() == super.cancel;
        super.nfIndex = 0;
        super.sfIndex = 0;
        super.cbIndex = 0;
        super.sbIndex = 0;
        super.choiceIndex = 0;

        for(int var2 = 0; var2 < this.butTot; ++var2) {
            this.buttons_touched[var2] = var1.getSource() == this.buttons[var2];
        }

        this.butIndex = 0;
        if (super.wasCanceled) {
            this.setVisible(false);
            this.dispose();
        }

    }

    public void addButton(String var1) {
        this.addButton(var1, 0);
    }

    public void addButton(String var1, int var2) {
        if (this.butIndex >= 20) {
            IJ.write("  cannot add another button, have maxed out at: " + this.butIndex);
        } else {
            this.buttons[this.butIndex] = new Button(var1);
            this.buttons[this.butIndex].addActionListener(this);
            this.buttons[this.butIndex].addKeyListener(this);
            super.c.gridwidth = 1;
            super.c.gridx = 0;
            super.c.gridy = super.y;
            super.c.anchor = 17;
            if (var2 == 1) {
                super.c.anchor = 10;
            }

            super.c.insets = new Insets(5, 0, 5, 0);
            super.grid.setConstraints(this.buttons[this.butIndex], super.c);
            super.activePanel.add(this.buttons[this.butIndex]);
            this.buttons_touched[this.butIndex] = false;
            ++this.butIndex;
            this.butTot = this.butIndex;
            ++super.y;
        }
    }

    public void addButtonToPanel(String var1, Panel var2, GridBagLayout var3, int var4) {
        if (this.butIndex >= 20) {
            IJ.write("  cannot add another button, have maxed out at: " + this.butIndex);
        } else {
            GridBagConstraints var5 = new GridBagConstraints();
            this.buttons[this.butIndex] = new Button(var1);
            this.buttons[this.butIndex].addActionListener(this);
            this.buttons[this.butIndex].addKeyListener(this);
            var5.gridx = var4;
            var5.gridy = 0;
            var5.anchor = 17;
            var5.insets = new Insets(0, 5, 0, 10);
            var3.setConstraints(this.buttons[this.butIndex], var5);
            var2.add(this.buttons[this.butIndex]);
            this.buttons_touched[this.butIndex] = false;
            ++this.butIndex;
            this.butTot = this.butIndex;
        }
    }

    public void addButtons(String var1, String var2) {
        Panel var3 = new Panel();
        GridBagLayout var4 = new GridBagLayout();
        var3.setLayout(var4);
        this.addButtonToPanel(var1, var3, var4, 0);
        this.addButtonToPanel(var2, var3, var4, 1);
        super.c.gridwidth = 2;
        super.c.gridx = 0;
        super.c.gridy = super.y;
        super.c.anchor = 17;
        super.c.insets = new Insets(5, 0, 5, 0);
        super.grid.setConstraints(var3, super.c);
        super.activePanel.add(var3);
        ++super.y;
    }

    public void addButtons(String var1, String var2, String var3) {
        Panel var4 = new Panel();
        GridBagLayout var5 = new GridBagLayout();
        var4.setLayout(var5);
        this.addButtonToPanel(var1, var4, var5, 0);
        this.addButtonToPanel(var2, var4, var5, 1);
        this.addButtonToPanel(var3, var4, var5, 2);
        super.c.gridwidth = 2;
        super.c.gridx = 0;
        super.c.gridy = super.y;
        super.c.anchor = 17;
        super.c.insets = new Insets(5, 0, 5, 0);
        super.grid.setConstraints(var4, super.c);
        super.activePanel.add(var4);
        ++super.y;
    }

    public void addButtons(String var1, String var2, String var3, String var4) {
        Panel var5 = new Panel();
        GridBagLayout var6 = new GridBagLayout();
        var5.setLayout(var6);
        this.addButtonToPanel(var1, var5, var6, 0);
        this.addButtonToPanel(var2, var5, var6, 1);
        this.addButtonToPanel(var3, var5, var6, 2);
        this.addButtonToPanel(var4, var5, var6, 3);
        super.c.gridwidth = 1;
        super.c.gridx = 0;
        super.c.gridy = super.y;
        super.c.anchor = 17;
        super.c.insets = new Insets(5, 0, 5, 0);
        super.grid.setConstraints(var5, super.c);
        super.activePanel.add(var5);
        ++super.y;
    }

    public boolean getButtonValue(int var1) {
        if (var1 >= 0 && var1 < this.butTot) {
            if (!this.buttons_touched[var1]) {
                return false;
            } else {
                this.buttons_touched[var1] = false;
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean getNextButton() {
        if (this.butIndex >= this.butTot) {
            return false;
        } else if (this.buttons_touched[this.butIndex]) {
            this.buttons_touched[this.butIndex++] = false;
            return true;
        } else {
            ++this.butIndex;
            return false;
        }
    }

    public void keyPressed(KeyEvent var1) {
        int var2 = var1.getKeyCode();
        IJ.setKeyDown(var2);
        if (super.scrollbars[super.SBlastTouched] != null) {
            double[] var10000;
            int var10001;
            if (var2 == 37 || var2 == 100) {
                var10000 = super.SBcurValues;
                var10001 = super.SBlastTouched;
                var10000[var10001] -= (double)super.scrollbars[super.SBlastTouched].getUnitIncrement() / super.SBscales[super.SBlastTouched];
                this.setScrollBarValue(super.SBlastTouched, super.SBcurValues[super.SBlastTouched]);
            }

            if (var2 == 39 || var2 == 102) {
                var10000 = super.SBcurValues;
                var10001 = super.SBlastTouched;
                var10000[var10001] += (double)super.scrollbars[super.SBlastTouched].getUnitIncrement() / super.SBscales[super.SBlastTouched];
                this.setScrollBarValue(super.SBlastTouched, super.SBcurValues[super.SBlastTouched]);
            }
        }

    }

    public void showDialog() {
        super.nfIndex = 0;
        super.sfIndex = 0;
        super.cbIndex = 0;
        super.choiceIndex = 0;
        super.sbIndex = 0;
        this.butIndex = 0;
        if (super.macro) {
            this.dispose();
        } else {
            if (super.stringField != null && super.numberField == null) {
                TextField var1 = (TextField)super.stringField.elementAt(0);
                var1.selectAll();
            }

            super.cancel = new Button(" Done ");
            super.cancel.addActionListener(this);
            super.c.gridx = 0;
            super.c.gridy = super.y;
            super.c.anchor = 10;
            super.c.insets = new Insets(5, 0, 0, 0);
            super.grid.setConstraints(super.cancel, super.c);
            this.add(super.cancel);
            if (IJ.isMacintosh()) {
                this.setResizable(false);
            }

            this.pack();
            GUI.center(this);
            this.setVisible(true);
            IJ.wait(250);
        }
    }
}
