package PucciniaRecondita;

import WL.WindowLevelAdjuster;
import ij.*;
import ij.gui.Toolbar;
import ij.measure.Calibration;
import ij.plugin.tool.PlugInTool;
import ij.process.ImageStatistics;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public final class Window_Level_Tool extends PlugInTool implements ActionListener {
    static final int AUTO_THRESHOLD = 5000;
    final String origImg = "Image";
    final String abdom = "Chest-Abdomen";
    final String lung = "Lung";
    final String liver = "Liver";
    final String bone = "Bone";
    final String brain = "Brain-Sinus";
    int autoThreshold;
    private double currentMin = 0.0D;
    private double currentMax = 0.0D;
    private double rescaleIntercept = 0.0D;
    private double rescaleSlope = 1.0D;
    private double ctImgLevel;
    private double ctImgWidth;
    private int lastX = -1;
    private int lastY = -1;
    private ImagePlus impLast = null;
    private PopupMenu popup1 = null;
    private PopupMenu oldPopup = null;
    private MenuItem autoItem;
    private MenuItem resetItem;
    private MenuItem ctOrig;
    private MenuItem ctAbdomen;
    private MenuItem ctLung;
    private MenuItem ctLiver;
    private MenuItem ctBone;
    private MenuItem ctBrain;
    private final int OFFSET = 0;
    private boolean RGB;
    private boolean isCT = true;

    public Window_Level_Tool() {
    }

    public void mousePressed(ImagePlus var1, MouseEvent var2) {
        this.RGB = var1.getType() == 4;
        if (this.impLast != var1) {
            this.setupImage(var1, false);
        }

        this.lastX = var2.getX();
        this.lastY = var2.getY();
        this.currentMin = var1.getDisplayRangeMin();
        this.currentMax = var1.getDisplayRangeMax();
    }

    public void mouseDragged(ImagePlus var1, MouseEvent var2) {
        double var3 = this.currentMax - this.currentMin;
        int var5 = var2.getX();
        int var6 = var2.getY();
        int var7 = var5 - this.lastX;
        int var8 = var6 - this.lastY;
        int var9 = (int)((double)var1.getWidth() * var1.getCanvas().getMagnification());
        int var10 = (int)((double)var1.getHeight() * var1.getCanvas().getMagnification());
        double var11 = (double)var7 / (double)var9;
        double var13 = (double)var8 / (double)var10;
        double var15 = var3 * var11;
        double var17 = var3 * var13;
        var15 *= -1.0D;
        this.adjustWindowLevel(var1, var15, var17);
    }

    void adjustWindowLevel(ImagePlus var1, double var2, double var4) {
        double var6 = this.currentMax - this.currentMin;
        double var8 = this.currentMin + 0.5D * var6;
        double var10 = var6 + var2;
        double var12 = var8 + var4;
        if (var10 < 0.0D) {
            var10 = 0.0D;
        }

        if (var12 < 0.0D) {
            var12 = 0.0D;
        }

        double var14 = var10 * this.rescaleSlope;
        double var16 = (var12 + this.getCoef0(var1)) * this.rescaleSlope + this.rescaleIntercept;
        IJ.showStatus("Window: " + IJ.d2s(var14) + ", Level: " + IJ.d2s(var16));
        double var18 = var12 - 0.5D * var10;
        double var20 = var12 + 0.5D * var10;
        var1.setDisplayRange(var18, var20);
        if (this.RGB) {
            var1.draw();
        } else {
            var1.updateAndDraw();
        }

    }

    public void showPopupMenu(MouseEvent var1, Toolbar var2) {
        this.addPopupMenu(var2);
        this.popup1.show(var1.getComponent(), var1.getX() - 30, var1.getY() + 0);
    }

    void addPopupMenu(Toolbar var1) {
        ImagePlus var2 = IJ.getImage();
        if (this.impLast != var2) {
            this.setupImage(var2, true);
        }

        if (this.popup1 == null) {
            var1.remove(this.oldPopup);
            this.oldPopup = null;
            this.popup1 = new PopupMenu();
            if (Menus.getFontSize() != 0) {
                this.popup1.setFont(Menus.getFont());
            }

            this.autoItem = new MenuItem("Auto");
            this.autoItem.addActionListener(this);
            this.popup1.add(this.autoItem);
            this.resetItem = new MenuItem("Reset");
            this.resetItem.addActionListener(this);
            this.popup1.add(this.resetItem);
            if (this.isCT) {
                this.popup1.addSeparator();
                this.ctOrig = new MenuItem("Image");
                this.ctOrig.addActionListener(this);
                this.popup1.add(this.ctOrig);
                this.ctAbdomen = new MenuItem("Chest-Abdomen");
                this.ctAbdomen.addActionListener(this);
                this.popup1.add(this.ctAbdomen);
                this.ctLung = new MenuItem("Lung");
                this.ctLung.addActionListener(this);
                this.popup1.add(this.ctLung);
                this.ctLiver = new MenuItem("Liver");
                this.ctLiver.addActionListener(this);
                this.popup1.add(this.ctLiver);
                this.ctBone = new MenuItem("Bone");
                this.ctBone.addActionListener(this);
                this.popup1.add(this.ctBone);
                this.ctBrain = new MenuItem("Brain-Sinus");
                this.ctBrain.addActionListener(this);
                this.popup1.add(this.ctBrain);
            }

            var1.add(this.popup1);
        }
        //popup1.show(, 0, 0);
    }

    public void actionPerformed(ActionEvent var1) {
        String var2 = var1.getActionCommand();
        if ("Auto".equals(var2)) {
            //System.out.println("A");
            this.autoItemActionPerformed(var1);
        } else if ("Reset".equals(var2)) {
            this.resetItemActionPerformed(var1);
        } else {
            this.maybeSetCt(var2);
        }
    }

    private void setupImage(ImagePlus var1, boolean var2) {
        if (var1 != null) {
            if (var2) {
                this.RGB = var1.getType() == 4;
                this.currentMin = var1.getDisplayRangeMin();
                this.currentMax = var1.getDisplayRangeMax();
            }

            boolean var3 = this.isCTImage(var1);
            if (var3 != this.isCT) {
                this.oldPopup = this.popup1;
                this.popup1 = null;
            }

            this.isCT = var3;
            if (this.RGB) {
                var1.getProcessor().snapshot();
            }

            this.autoThreshold = 0;
            this.impLast = var1;
        }
    }

    private void autoItemActionPerformed(ActionEvent var1) {
        if (!(this.impLast != null && this.impLast.isVisible())) {
            this.setupImage(IJ.getImage(), true);
        }
        if (this.impLast != null && this.impLast.isVisible()) {
            //System.out.println("A");
            int var2 = this.impLast.getBitDepth();
            if (var2 != 16 && var2 != 32) {
                this.resetItemActionPerformed(var1);
            } else {
                Calibration var5 = this.impLast.getCalibration();
                this.impLast.setCalibration((Calibration)null);
                ImageStatistics var6 = this.impLast.getStatistics();
                this.impLast.setCalibration(var5);
                int var7 = var6.pixelCount / 10;
                int[] var8 = var6.histogram;
                if (this.autoThreshold < 10) {
                    this.autoThreshold = 5000;
                } else {
                    this.autoThreshold /= 2;
                }

                int var9 = var6.pixelCount / this.autoThreshold;
                int var10 = -1;

                boolean var11;
                int var12;
                do {
                    ++var10;
                    var12 = var8[var10];
                    if (var12 > var7) {
                        var12 = 0;
                    }

                    var11 = var12 > var9;
                } while(!var11 && var10 < 255);

                int var3 = var10;
                var10 = 256;

                do {
                    --var10;
                    var12 = var8[var10];
                    if (var12 > var7) {
                        var12 = 0;
                    }

                    var11 = var12 > var9;
                } while(!var11 && var10 > 0);

                if (var10 >= var3) {
                    this.currentMin = var6.histMin + (double)var3 * var6.binSize;
                    this.currentMax = var6.histMin + (double)var10 * var6.binSize;
                    if (this.currentMin == this.currentMax) {
                        this.currentMin = var6.min;
                        this.currentMax = var6.max;
                    }

                    this.adjustWindowLevel(this.impLast, 0.0D, 0.0D);
                }

            }
        }
        //else System.out.println("B");
        //System.out.println(var1.getID() + " " + var1.getActionCommand() + " " + var1.getSource().toString());
        if (var1.getID() != -1) {
            WindowLevelAdjuster wl = new WindowLevelAdjuster();
            wl.runInThread("");
        }
    }

    private void resetItemActionPerformed(ActionEvent var1) {
        if (this.impLast != null && this.impLast.isVisible()) {
            this.impLast.resetDisplayRange();
            this.currentMin = this.impLast.getDisplayRangeMin();
            this.currentMax = this.impLast.getDisplayRangeMax();
            this.autoThreshold = 0;
            if (this.RGB) {
                this.impLast.getProcessor().reset();
                this.currentMin = 0.0D;
                this.currentMax = 255.0D;
                this.impLast.setDisplayRange(this.currentMin, this.currentMax);
                this.impLast.draw();
            } else {
                this.adjustWindowLevel(this.impLast, 0.0D, 0.0D);
            }

        }
    }

    private void maybeSetCt(String var1) {
        if (this.impLast != null && this.impLast.isVisible()) {
            this.autoThreshold = 0;
            double var2 = 0.0D;
            double var4 = -100000.0D;
            if (var1.equals("Image")) {
                var2 = this.ctImgLevel;
                var4 = this.ctImgWidth;
            }

            if (var1.equals("Chest-Abdomen")) {
                var2 = 56.0D;
                var4 = 340.0D;
            }

            if (var1.equals("Lung")) {
                var2 = -498.0D;
                var4 = 1464.0D;
            }

            if (var1.equals("Liver")) {
                var2 = 93.0D;
                var4 = 108.0D;
            }

            if (var1.equals("Bone")) {
                var2 = 570.0D;
                var4 = 3080.0D;
            }

            if (var1.equals("Brain-Sinus")) {
                var2 = 40.0D;
                var4 = 80.0D;
            }

            if (var4 != -100000.0D) {
                var4 /= this.rescaleSlope;
                var2 = (var2 - this.rescaleIntercept) / this.rescaleSlope;
                var2 -= this.getCoef0(this.impLast);
                this.currentMin = var2 - var4 / 2.0D;
                this.currentMax = var2 + var4 / 2.0D;
                this.adjustWindowLevel(this.impLast, 0.0D, 0.0D);
            }
        }
    }

    private double getCoef0(ImagePlus var1) {
        double[] var2 = var1.getCalibration().getCoefficients();
        double var3 = 0.0D;
        if (var2 != null) {
            var3 = var2[0];
        }

        return var3;
    }

    private boolean isCTImage(ImagePlus var1) {
        this.rescaleIntercept = 0.0D;
        this.rescaleSlope = 1.0D;
        this.ctImgLevel = 56.0D;
        this.ctImgWidth = 340.0D;
        String var2 = var1.getStack().getSliceLabel(1);
        if (var2 == null || !var2.contains("0010,0010")) {
            var2 = (String)var1.getProperty("Info");
        }

        if (var2 == null) {
            return false;
        } else {
            String var3 = this.getDicomValue(var2, "0008,0016");
            if (var3 == null) {
                return false;
            } else if (var3.startsWith("1.2.840.10008.5.1.4.1.1.20")) {
                return false;
            } else if (var3.startsWith("1.2.840.10008.5.1.4.1.1.2")) {
                if (this.getCoef0(var1) == 0.0D) {
                    var3 = this.getDicomValue(var2, "0028,1052");
                    this.rescaleIntercept = this.parseDouble(var3, 0.0D);
                }

                var3 = this.getDicomValue(var2, "0028,1053");
                this.rescaleSlope = this.parseDouble(var3, 1.0D);
                var3 = this.getDicomValue(var2, "0028,1050");
                this.ctImgLevel = this.parseDouble(var3, this.ctImgLevel);
                var3 = this.getDicomValue(var2, "0028,1051");
                this.ctImgWidth = this.parseDouble(var3, this.ctImgWidth);
                var3 = this.getDicomValue(var2, "0008,1090");
                if (var3.equals("VARICAM") || var3.equals("INFINIA") || var3.equals("QUASAR")) {
                    this.rescaleIntercept = -1000.0D;
                    this.ctImgLevel += this.rescaleIntercept;
                }

                return true;
            } else {
                return false;
            }
        }
    }

    double parseDouble(String var1, double var2) {
        double var4 = var2;
        if (var1 != null && !var1.isEmpty()) {
            try {
                var4 = Double.parseDouble(var1);
            } catch (Exception var7) {
                var4 = var2;
            }
        }

        return var4;
    }

    private String getDicomValue(String var1, String var2) {
        String var4 = null;
        if (var1 == null) {
            return null;
        } else {
            int var5 = var1.indexOf(var2);
            if (var5 > 0) {
                int var6 = var1.indexOf("\n", var5);
                if (var6 < 0) {
                    return null;
                }

                String var3 = var1.substring(var5, var6);
                var6 = var3.indexOf(": ");
                if (var6 > 0) {
                    var3 = var3.substring(var6 + 2);
                }

                var4 = var3.trim();
                if (var4.isEmpty()) {
                    var4 = null;
                }
            }

            return var4;
        }
    }

    public String getToolIcon() {
        return "T0b12W Tbb12L";
    }

    public String getToolName() {
        return "Window Level Tool (right click for Reset, Auto)";
    }
}
