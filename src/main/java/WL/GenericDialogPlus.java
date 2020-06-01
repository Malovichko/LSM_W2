package WL;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import ij.IJ;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

class GenericDialogPlus extends GenericDialog2 implements AdjustmentListener, KeyListener, FocusListener {
    public static final int MAX_ITEMS = 20;
    protected Scrollbar[] scrollbars;
    protected double[] SBscales;
    protected double[] SBcurValues;
    private Label[] SBcurValueLabels;
    protected int sbIndex;
    protected int SBtotal;
    private int[] SBdigits;
    protected int x;
    protected int SBlastTouched;
    private Panel twoPanel;
    private GridBagConstraints tmpc;
    private GridBagLayout tmpgrid;
    private int tmpy;

    public GenericDialogPlus(String var1) {
        super(var1);
    }

    public GenericDialogPlus(String var1, Frame var2) {
        super(var1, var2);
    }

    public void addScrollBar(String var1, double var2, int var4, double var5, double var7) {
        this.addScrollBar(var1, var2, var4, var5, var7, 100);
    }

    public void addScrollBar(String var1, double var2, int var4, double var5, double var7, int var9) {
        if (this.sbIndex >= 20) {
            IJ.write("  cannot add another slider, have maxed out at: " + this.sbIndex);
        } else {
            if (this.scrollbars == null) {
                this.scrollbars = new Scrollbar[20];
                this.SBscales = new double[20];
                this.SBcurValues = new double[20];
                this.SBcurValueLabels = new Label[20];
                this.SBdigits = new int[20];
            }

            Panel var10 = new Panel();
            GridBagLayout var11 = new GridBagLayout();
            GridBagConstraints var12 = new GridBagConstraints();
            var10.setLayout(var11);
            Label var13 = new Label(var1);
            var12.insets = new Insets(5, 0, 0, 0);
            var12.gridx = 0;
            var12.gridy = 0;
            var12.anchor = 17;
            var11.setConstraints(var13, var12);
            var10.add(var13);
            this.SBscales[this.sbIndex] = Math.pow(10.0D, (double)var4);
            this.SBcurValues[this.sbIndex] = var2;
            int var14 = (int)Math.round((var7 - var5) * this.SBscales[this.sbIndex] / 10.0D);
            this.scrollbars[this.sbIndex] = new Scrollbar(0, (int)Math.round(var2 * this.SBscales[this.sbIndex]), var14, (int)Math.round(var5 * this.SBscales[this.sbIndex]), (int)Math.round(var7 * this.SBscales[this.sbIndex] + (double)var14));
            this.scrollbars[this.sbIndex].addAdjustmentListener(this);
            this.scrollbars[this.sbIndex].setUnitIncrement(Math.max(1, (int)Math.round((var7 - var5) * this.SBscales[this.sbIndex] / (double)var9)));
            var12.gridx = 1;
            var12.ipadx = 75;
            var11.setConstraints(this.scrollbars[this.sbIndex], var12);
            var10.add(this.scrollbars[this.sbIndex]);
            var12.ipadx = 0;
            this.SBdigits[this.sbIndex] = var4;
            this.SBcurValueLabels[this.sbIndex] = new Label(IJ.d2s(this.SBcurValues[this.sbIndex], var4));
            var12.gridx = 2;
            var12.insets = new Insets(5, 5, 0, 0);
            var12.anchor = 13;
            var11.setConstraints(this.SBcurValueLabels[this.sbIndex], var12);
            var10.add(this.SBcurValueLabels[this.sbIndex]);
            super.c.gridwidth = 2;
            super.c.gridx = this.x;
            super.c.gridy = super.y;
            super.c.insets = new Insets(0, 0, 0, 0);
            super.c.anchor = 10;
            super.grid.setConstraints(var10, super.c);
            super.activePanel.add(var10);
            ++this.sbIndex;
            if (super.activePanel == this) {
                this.x = 0;
                ++super.y;
            } else {
                ++this.x;
            }

            this.SBtotal = this.sbIndex;
        }
    }

    public synchronized void adjustmentValueChanged(AdjustmentEvent var1) {
        for(int var2 = 0; var2 < this.SBtotal; ++var2) {
            if (var1.getSource() == this.scrollbars[var2]) {
                this.SBcurValues[var2] = (double)this.scrollbars[var2].getValue() / this.SBscales[var2];
                this.setScrollBarValue(var2, this.SBcurValues[var2]);
                this.SBlastTouched = var2;
            }
        }

        this.sbIndex = 0;
    }

    public void beginRowOfItems() {
        this.tmpc = super.c;
        this.tmpgrid = super.grid;
        this.tmpy = super.y;
        this.twoPanel = new Panel();
        super.activePanel = this.twoPanel;
        super.grid = new GridBagLayout();
        this.twoPanel.setLayout(super.grid);
        super.c = new GridBagConstraints();
        this.x = super.y = 0;
    }

    public void endRowOfItems() {
        super.activePanel = this;
        super.c = this.tmpc;
        super.grid = this.tmpgrid;
        super.y = this.tmpy;
        super.c.gridwidth = 1;
        super.c.gridx = 0;
        super.c.gridy = super.y;
        super.c.anchor = 17;
        super.c.insets = new Insets(0, 0, 0, 0);
        super.grid.setConstraints(this.twoPanel, super.c);
        this.add(this.twoPanel);
        this.x = 0;
        ++super.y;
    }

    public boolean getBooleanValue(int var1) {
        if (super.checkbox == null) {
            return false;
        } else {
            Checkbox var2 = (Checkbox)super.checkbox.elementAt(var1);
            return var2.getState();
        }
    }

    public double getNextScrollBar() {
        return this.scrollbars[this.sbIndex] == null ? -1.0D : this.SBcurValues[this.sbIndex++];
    }

    public double getNumericValue(int var1) {
        if (super.numberField == null) {
            return 0.0D;
        } else {
            TextField var2 = (TextField)super.numberField.elementAt(var1);
            String var3 = var2.getText();
            String var4 = (String)super.defaultText.elementAt(var1);
            double var5 = (Double)super.defaultValues.elementAt(var1);
            double var7;
            if (var3.equals(var4)) {
                var7 = var5;
            } else {
                Double var9 = this.getValue(var3);
                if (var9 != null) {
                    var7 = var9;
                } else {
                    var7 = 0.0D;
                }
            }

            return var7;
        }
    }

    public double getScrollBarValue(int var1) {
        return var1 >= 0 && var1 < this.SBtotal && this.scrollbars[var1] != null ? this.SBcurValues[var1] : -1.0D;
    }

    public void setScrollBarUnitIncrement(int var1) {
        this.scrollbars[this.sbIndex - 1].setUnitIncrement(var1);
    }

    public void setScrollBarValue(int var1, double var2) {
        if (var1 >= 0 && var1 < this.SBtotal && this.scrollbars[var1] != null) {
            this.scrollbars[var1].setValue((int)Math.round(var2 * this.SBscales[var1]));
            this.SBcurValues[var1] = var2;
            this.SBcurValueLabels[var1].setText(IJ.d2s(this.SBcurValues[var1], this.SBdigits[var1]));
        }
    }

    public void showDialog() {
        this.sbIndex = 0;
        super.showDialog();
    }
}
