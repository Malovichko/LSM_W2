package WL;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import ij.IJ;
import ij.Macro;
import ij.WindowManager;
import ij.gui.GUI;
import ij.gui.MultiLineLabel;
import ij.plugin.frame.Recorder;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Hashtable;
import java.util.Vector;

public class GenericDialog2 extends Dialog implements ActionListener, TextListener, FocusListener, ItemListener, KeyListener {
    protected Vector defaultValues;
    protected Vector defaultText;
    protected Vector numberField;
    protected Vector stringField;
    protected Vector checkbox;
    protected Vector choice;
    protected Component theLabel;
    protected TextArea textArea1;
    protected TextArea textArea2;
    protected Button cancel;
    protected Button okay;
    protected boolean wasCanceled;
    protected int x;
    protected int y;
    protected int nfIndex;
    protected int sfIndex;
    protected int cbIndex;
    protected int choiceIndex;
    protected GridBagLayout grid;
    protected GridBagConstraints c;
    private boolean firstNumericField;
    private boolean invalidNumber;
    private boolean firstPaint;
    private Hashtable labels;
    protected boolean macro;
    private String macroOptions;
    protected Container activePanel;

    public GenericDialog2(String var1) {
        this(var1, (Frame)(WindowManager.getCurrentImage() != null ? WindowManager.getCurrentImage().getWindow() : IJ.getInstance()));
    }

    public GenericDialog2(String var1, Frame var2) {
        super(var2, var1, true);
        this.firstNumericField = true;
        this.firstPaint = true;
        this.grid = new GridBagLayout();
        this.c = new GridBagConstraints();
        this.setLayout(this.grid);
        this.macroOptions = Macro.getOptions();
        this.macro = this.macroOptions != null;
        this.addKeyListener(this);
        this.activePanel = this;
    }

    public void actionPerformed(ActionEvent var1) {
        this.wasCanceled = var1.getSource() == this.cancel;
        this.setVisible(false);
        this.dispose();
    }

    public void addCheckbox(String var1, boolean var2) {
        if (this.checkbox == null) {
            this.checkbox = new Vector(4);
            this.c.insets = new Insets(15, 20, 0, 0);
        } else {
            this.c.insets = new Insets(0, 20, 0, 0);
        }

        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.gridwidth = 2;
        this.c.anchor = 17;
        Checkbox var3 = new Checkbox(var1);
        this.grid.setConstraints(var3, this.c);
        var3.setState(var2);
        var3.addItemListener(this);
        var3.addKeyListener(this);
        this.activePanel.add(var3);
        this.checkbox.addElement(var3);
        if (Recorder.record || this.macro) {
            this.saveLabel(var3, var1);
        }

        if (this.activePanel == this) {
            this.x = 0;
            ++this.y;
        } else {
            ++this.x;
        }

    }

    public void addCheckboxGroup(int var1, int var2, String[] var3, boolean[] var4) {
        Panel var5 = new Panel();
        var5.setLayout(new GridLayout(var1, var2, 10, 0));
        int var6 = this.cbIndex;
        int var7 = 0;
        int[] var8 = new int[var3.length];
        if (this.checkbox == null) {
            this.checkbox = new Vector(12);
        }

        for(int var9 = 0; var9 < var1; ++var9) {
            for(int var10 = 0; var10 < var2; ++var10) {
                int var11 = var10 * var1 + var9;
                if (var11 >= var3.length) {
                    break;
                }

                var8[var7] = var11;
                Checkbox var12 = new Checkbox(var3[var7]);
                this.checkbox.addElement(var12);
                var12.setState(var4[var7]);
                if (Recorder.record || this.macro) {
                    this.saveLabel(var12, var3[var7]);
                }

                var5.add(var12);
                ++var7;
            }
        }

        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.gridwidth = 2;
        this.c.anchor = 17;
        this.c.insets = new Insets(10, 0, 0, 0);
        this.grid.setConstraints(var5, this.c);
        this.activePanel.add(var5);
        if (this.activePanel == this) {
            this.x = 0;
            ++this.y;
        } else {
            ++this.x;
        }

    }

    public void addChoice(String var1, String[] var2, String var3) {
        Label var4 = this.makeLabel(var1);
        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.anchor = 13;
        this.c.gridwidth = 1;
        if (this.choice == null) {
            this.choice = new Vector(4);
            this.c.insets = new Insets(5, 0, 5, 0);
        } else {
            this.c.insets = new Insets(0, 0, 5, 0);
        }

        this.grid.setConstraints(var4, this.c);
        this.activePanel.add(var4);
        Choice var5 = new Choice();
        var5.addKeyListener(this);
        var5.addItemListener(this);

        for(int var6 = 0; var6 < var2.length; ++var6) {
            var5.addItem(var2[var6]);
        }

        var5.select(var3);
        ++this.x;
        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.anchor = 17;
        this.grid.setConstraints(var5, this.c);
        this.activePanel.add(var5);
        this.choice.addElement(var5);
        if (Recorder.record || this.macro) {
            this.saveLabel(var5, var1);
        }

        if (this.activePanel == this) {
            this.x = 0;
            ++this.y;
        } else {
            ++this.x;
        }

    }

    public void addMessage(String var1) {
        if (var1.indexOf(10) >= 0) {
            this.theLabel = new MultiLineLabel(var1);
        } else {
            this.theLabel = new Label(var1);
        }

        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.gridwidth = 2;
        this.c.anchor = 17;
        this.c.insets = new Insets(var1.equals("") ? 0 : 10, 20, 0, 0);
        this.grid.setConstraints(this.theLabel, this.c);
        this.activePanel.add(this.theLabel);
        if (this.activePanel == this) {
            this.x = 0;
            ++this.y;
        } else {
            ++this.x;
        }

    }

    public void addNumericField(String var1, double var2, int var4) {
        Label var5 = this.makeLabel(var1);
        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.anchor = 13;
        this.c.gridwidth = 1;
        if (this.firstNumericField) {
            this.c.insets = new Insets(5, 0, 3, 0);
        } else {
            this.c.insets = new Insets(0, 0, 3, 0);
        }

        this.grid.setConstraints(var5, this.c);
        this.activePanel.add(var5);
        if (this.numberField == null) {
            this.numberField = new Vector(5);
            this.defaultValues = new Vector(5);
            this.defaultText = new Vector(5);
        }

        TextField var6 = new TextField(IJ.d2s(var2, var4), 6);
        var6.addActionListener(this);
        var6.addTextListener(this);
        var6.addFocusListener(this);
        var6.addKeyListener(this);
        this.numberField.addElement(var6);
        this.defaultValues.addElement(new Double(var2));
        this.defaultText.addElement(var6.getText());
        ++this.x;
        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.anchor = 17;
        this.grid.setConstraints(var6, this.c);
        var6.setEditable(true);
        if (this.firstNumericField) {
            var6.selectAll();
        }

        this.firstNumericField = false;
        this.activePanel.add(var6);
        if (Recorder.record || this.macro) {
            this.saveLabel(var6, var1);
        }

        if (this.activePanel == this) {
            this.x = 0;
            ++this.y;
        } else {
            ++this.x;
        }

    }

    public void addStringField(String var1, String var2) {
        this.addStringField(var1, var2, 8);
    }

    public void addStringField(String var1, String var2, int var3) {
        Label var4 = this.makeLabel(var1);
        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.anchor = 13;
        this.c.gridwidth = 1;
        if (this.stringField == null) {
            this.stringField = new Vector(4);
            this.c.insets = new Insets(5, 0, 5, 0);
        } else {
            this.c.insets = new Insets(0, 0, 5, 0);
        }

        this.grid.setConstraints(var4, this.c);
        this.activePanel.add(var4);
        TextField var5 = new TextField(var2, var3);
        var5.addActionListener(this);
        var5.addTextListener(this);
        var5.addFocusListener(this);
        var5.addKeyListener(this);
        ++this.x;
        this.c.gridx = this.x;
        this.c.gridy = this.y;
        this.c.anchor = 17;
        this.grid.setConstraints(var5, this.c);
        var5.setEditable(true);
        this.activePanel.add(var5);
        this.stringField.addElement(var5);
        if (Recorder.record || this.macro) {
            this.saveLabel(var5, var1);
        }

        if (this.activePanel == this) {
            this.x = 0;
            ++this.y;
        } else {
            ++this.x;
        }

    }

    public void addTextAreas(String var1, String var2, int var3, int var4) {
        if (this.textArea1 == null) {
            Panel var5 = new Panel();
            this.textArea1 = new TextArea(var1, var3, var4, 3);
            var5.add(this.textArea1);
            if (var2 != null) {
                this.textArea2 = new TextArea(var2, var3, var4, 3);
                var5.add(this.textArea2);
            }

            this.c.gridx = this.x;
            this.c.gridy = this.y;
            this.c.gridwidth = 2;
            this.c.anchor = 17;
            this.c.insets = new Insets(15, 20, 0, 0);
            this.grid.setConstraints(var5, this.c);
            this.activePanel.add(var5);
            if (this.activePanel == this) {
                this.x = 0;
                ++this.y;
            } else {
                ++this.x;
            }

        }
    }

    public void focusGained(FocusEvent var1) {
        Component var2 = var1.getComponent();
        if (var2 instanceof TextField) {
            ((TextField)var2).selectAll();
        }

    }

    public void focusLost(FocusEvent var1) {
        Component var2 = var1.getComponent();
        if (var2 instanceof TextField) {
            ((TextField)var2).select(0, 0);
        }

    }

    public Insets getInsets() {
        Insets var1 = super.getInsets();
        return new Insets(var1.top + 10, var1.left + 10, var1.bottom + 10, var1.right + 10);
    }

    public boolean getNextBoolean() {
        if (this.checkbox == null) {
            return false;
        } else {
            Checkbox var1 = (Checkbox)this.checkbox.elementAt(this.cbIndex);
            if (Recorder.record) {
                this.recordCheckboxOption(var1);
            }

            boolean var2 = var1.getState();
            if (this.macro) {
                String var3 = (String)this.labels.get(var1);
                String var4 = Macro.trimKey(var3);
                var2 = this.macroOptions.indexOf(var4 + " ") >= 0;
            }

            ++this.cbIndex;
            return var2;
        }
    }

    public String getNextChoice() {
        if (this.choice == null) {
            return "";
        } else {
            Choice var1 = (Choice)this.choice.elementAt(this.choiceIndex);
            String var2 = var1.getSelectedItem();
            if (this.macro) {
                String var3 = (String)this.labels.get(var1);
                var2 = Macro.getValue(this.macroOptions, var3, var2);
            }

            if (Recorder.record) {
                this.recordOption(var1, var2);
            }

            ++this.choiceIndex;
            return var2;
        }
    }

    public int getNextChoiceIndex() {
        if (this.choice == null) {
            return -1;
        } else {
            Choice var1 = (Choice)this.choice.elementAt(this.choiceIndex);
            int var2 = var1.getSelectedIndex();
            if (this.macro) {
                String var3 = (String)this.labels.get(var1);
                String var4 = var1.getSelectedItem();
                int var5 = var1.getSelectedIndex();
                String var6 = Macro.getValue(this.macroOptions, var3, var4);
                var1.select(var6);
                var2 = var1.getSelectedIndex();
                if (var2 == var5 && !var6.equals(var4)) {
                    IJ.showMessage(this.getTitle(), "\"" + var6 + "\" is not a vaid choice for \"" + var3 + "\"");
                    Macro.abort();
                }
            }

            if (Recorder.record) {
                this.recordOption(var1, var1.getSelectedItem());
            }

            ++this.choiceIndex;
            return var2;
        }
    }

    public double getNextNumber() {
        if (this.numberField == null) {
            return -1.0D;
        } else {
            TextField var1 = (TextField)this.numberField.elementAt(this.nfIndex);
            String var2 = var1.getText();
            String var3;
            if (this.macro) {
                var3 = (String)this.labels.get(var1);
                var2 = Macro.getValue(this.macroOptions, var3, var2);
            }

            var3 = (String)this.defaultText.elementAt(this.nfIndex);
            double var4 = (Double)this.defaultValues.elementAt(this.nfIndex);
            double var6;
            if (var2.equals(var3)) {
                var6 = var4;
            } else {
                Double var8 = this.getValue(var2);
                if (var8 != null) {
                    var6 = var8;
                } else {
                    this.invalidNumber = true;
                    var6 = 0.0D;
                }
            }

            if (Recorder.record) {
                this.recordOption(var1, this.trim(var2));
            }

            ++this.nfIndex;
            return var6;
        }
    }

    public String getNextString() {
        if (this.stringField == null) {
            return "";
        } else {
            TextField var2 = (TextField)this.stringField.elementAt(this.sfIndex);
            String var1 = var2.getText();
            if (this.macro) {
                String var3 = (String)this.labels.get(var2);
                var1 = Macro.getValue(this.macroOptions, var3, var1);
            }

            if (Recorder.record) {
                this.recordOption(var2, var1);
            }

            ++this.sfIndex;
            return var1;
        }
    }

    public String getNextText() {
        String var1;
        if (this.textArea1 != null) {
            this.textArea1.selectAll();
            var1 = this.textArea1.getText();
            this.textArea1 = null;
        } else if (this.textArea2 != null) {
            this.textArea2.selectAll();
            var1 = this.textArea2.getText();
            this.textArea2 = null;
        } else {
            var1 = null;
        }

        return var1;
    }

    protected Double getValue(String var1) {
        Double var2;
        try {
            var2 = new Double(var1);
        } catch (NumberFormatException var3) {
            var2 = null;
        }

        return var2;
    }

    public boolean invalidNumber() {
        boolean var1 = this.invalidNumber;
        this.invalidNumber = false;
        return var1;
    }

    public void itemStateChanged(ItemEvent var1) {
    }

    public void keyPressed(KeyEvent var1) {
        int var2 = var1.getKeyCode();
        IJ.setKeyDown(var2);
    }

    public void keyReleased(KeyEvent var1) {
        IJ.setKeyUp(var1.getKeyCode());
    }

    public void keyTyped(KeyEvent var1) {
    }

    private Label makeLabel(String var1) {
        if (IJ.isMacintosh()) {
            var1 = var1 + " ";
        }

        return new Label(var1);
    }

    public void paint(Graphics var1) {
        super.paint(var1);
        if (this.firstPaint && this.numberField != null) {
            TextField var2 = (TextField)this.numberField.elementAt(0);
            var2.requestFocus();
            this.firstPaint = false;
        }

    }

    private void recordCheckboxOption(Checkbox var1) {
        String var2 = (String)this.labels.get(var1);
        if (var1.getState() && var2 != null) {
            Recorder.recordOption(var2);
        }

    }

    private void recordOption(Component var1, String var2) {
        String var3 = (String)this.labels.get(var1);
        Recorder.recordOption(var3, var2);
    }

    private void saveLabel(Component var1, String var2) {
        if (this.labels == null) {
            this.labels = new Hashtable();
        }

        this.labels.put(var1, var2);
    }

    protected void setup() {
    }

    public void showDialog() {
        this.nfIndex = 0;
        this.sfIndex = 0;
        this.cbIndex = 0;
        this.choiceIndex = 0;
        if (this.macro) {
            this.dispose();
        } else {
            if (this.stringField != null && this.numberField == null) {
                TextField var1 = (TextField)this.stringField.elementAt(0);
                var1.selectAll();
            }

            Panel var2 = new Panel();
            var2.setLayout(new FlowLayout(1, 5, 0));
            this.cancel = new Button("Cancel");
            this.cancel.addActionListener(this);
            this.okay = new Button("  OK  ");
            this.okay.addActionListener(this);
            if (IJ.isMacintosh()) {
                var2.add(this.cancel);
                var2.add(this.okay);
            } else {
                var2.add(this.okay);
                var2.add(this.cancel);
            }

            this.c.gridx = 0;
            this.c.gridy = this.y;
            this.c.anchor = 13;
            this.c.gridwidth = 2;
            this.c.insets = new Insets(15, 0, 0, 0);
            this.grid.setConstraints(var2, this.c);
            this.activePanel.add(var2);
            if (IJ.isMacintosh()) {
                this.setResizable(false);
            }

            this.pack();
            this.setup();
            GUI.center(this);
            IJ.wait(250);
        }
    }

    public void textValueChanged(TextEvent var1) {
    }

    private String trim(String var1) {
        if (var1.endsWith(".0")) {
            var1 = var1.substring(0, var1.length() - 2);
        }

        if (var1.endsWith(".00")) {
            var1 = var1.substring(0, var1.length() - 3);
        }

        return var1;
    }

    public boolean wasCanceled() {
        if (this.wasCanceled) {
            Macro.abort();
        }

        return this.wasCanceled;
    }
}
