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


    SetLabelMapPlugin(MyLSMImage myimp, ImagePlus ip){
        this.myimp = myimp;
        this.ip = ip;
        im2dproc = myimp.get2DProc();
    }
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        if (gd.getPreviewCheckbox().getState())
            ip.setProcessor(im2dproc.getColored2dProc());
        return true;
    }

    @Override
    public void run(String arg) {

        // Display dialogs and waits for user validation
        GenericDialog gd = showDialog();
        if (gd.wasCanceled())
        {
            ip.updateAndDraw();
            return;
        }
        im2dproc.setDiveValue(diving_value);
        ip.setProcessor(im2dproc.getColored2dProc());
        ip.updateAndDraw();
    }

    public GenericDialog showDialog()
    {
        // Create a new generic dialog with appropriate options
        GenericDialog gd = new GenericDialog("Set Diving value");

        gd.addNumericField("Diving value", diving_value, 1);

        gd.addPreviewCheckbox(null);
        gd.addDialogListener(this);
        gd.showDialog();

        return gd;
    }
}
