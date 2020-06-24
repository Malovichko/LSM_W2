package PucciniaRecondita;

import MainWindow.Image2DProcessor;
import MainWindow.MyLSMImage;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.IJ;

import java.awt.*;

public class SetLabelMapPlugin implements PlugIn, DialogListener {
    private ImagePlus ip;
    private MyLSMImage myimp;
    private Image2DProcessor im2dproc;
    private int diving_value = 0;
    private boolean colored;


    SetLabelMapPlugin(MyLSMImage myimp, ImagePlus ip, boolean colored){
        this.myimp = myimp;
        this.ip = ip;
        im2dproc = myimp.get2DProc();
        this.ip.getProcessor().snapshot();
        this.colored = colored;
    }

    SetLabelMapPlugin(MyLSMImage myimp, ImagePlus ip, int dv, boolean colored){
        this.myimp = myimp;
        this.ip = ip;
        im2dproc = myimp.get2DProc();
        this.ip.getProcessor().snapshot();
        this.diving_value = dv;
        this.colored = colored;
    }
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        diving_value = (int)gd.getNextNumber();
        if (gd.getPreviewCheckbox().getState()) {
            im2dproc.setDiveValue(diving_value);
            if (colored) ip.setProcessor(im2dproc.getColored2dProc());
            else ip.setProcessor(im2dproc.getCur2DProc());
        }
        return true;
    }

    @Override
    public void run(String arg) {
        // Display dialogs and waits for user validation
        GenericDialog gd = showDialog();
        if (gd.wasCanceled())
        {
            return;
        }
        im2dproc.setDiveValue(diving_value);
        if (colored) ip.setProcessor(im2dproc.getColored2dProc());
        else ip.setProcessor(im2dproc.getCur2DProc());
//        ip.updateAndDraw();
    }

    public GenericDialog showDialog()
    {
        // Create a new generic dialog with appropriate options
        GenericDialog gd = new GenericDialog("Set Diving value");

        gd.addNumericField("Diving value", diving_value, 0);

        gd.addPreviewCheckbox(null);
        gd.addDialogListener(this);
        gd.showDialog();

        return gd;
    }
}
