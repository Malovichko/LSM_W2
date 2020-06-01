package PucciniaRecondita;
import ij.*;
import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import static ij.plugin.filter.GaussianBlur.resetOutOfRoi;

public class MedianFilterRunner implements ExtendedPlugInFilter, DialogListener {
    private static double size = 5.0;
    /** whether sigma is given in units corresponding to the pixel scale (not pixels)*/
    private static boolean sigmaScaled = false;
    private ImageProcessor imageProcessor;
    private ImagePlus imp;
    int width;
    int height;
    String title;
    private int nPasses = 1;
    private int pass;
    private int nChannels = 1;
    private int flags = DOES_ALL|SUPPORTS_MASKING|KEEP_PREVIEW;
    private boolean calledAsPlugin;
    private boolean hasScale = false;   // whether the image has an x&y scale

    public MedianFilterRunner() {
        this.imp = IJ.getImage().duplicate();
        this.imageProcessor = this.imp.getProcessor();
        this.title = IJ.getImage().getTitle();
        width = imageProcessor.getWidth();
        height = imageProcessor.getHeight();
    }

    public int setup(String arg, ImagePlus imp) {
        //this.imp = imp;
        if (imp!=null && imp.getRoi()!=null) {
            Rectangle roiRect = imp.getRoi().getBoundingRect();
            if (roiRect.y > 0 || roiRect.y+roiRect.height < imp.getDimensions()[1])
                flags |= SNAPSHOT;                  // snapshot for pixels above and/or below roi rectangle
        }
        return flags;
    }

    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        calledAsPlugin = true;
        String options = Macro.getOptions();
        boolean oldMacro = false;
        nChannels = imp.getProcessor().getNChannels();
        if  (options!=null) {
            if (options.indexOf("radius=") >= 0) {  // ensure compatibility with old macros
                oldMacro = true;                    // specifying "radius=", not "sigma=
                Macro.setOptions(options.replaceAll("radius=", "size="));
            }
        }
        GenericDialog gd = new GenericDialog(command);
        size = Math.abs(size);
        gd.addNumericField("Radius", size, 1);
        if (imp.getCalibration()!=null && !imp.getCalibration().getUnits().equals("pixels")) {
            hasScale = true;
            gd.addCheckbox("Scaled Units ("+imp.getCalibration().getUnits()+")", sigmaScaled);
        } else sigmaScaled = false;
        gd.addPreviewCheckbox(pfr);
        //gd.addDialogListener(this);
        gd.showDialog();                    // input by the user (or macro) happens here
        if (gd.wasCanceled()) {
            imp.setImage(this.imp);
            //imp.getProcessor().reset(this.imageProcessor);
            //resetOutOfRoi(imp.getProcessor(), 20);
            return DONE;
        }
        if (oldMacro) size /= 2.5;         // for old macros, "radius" was 2.5 sigma
        //size = (int) gd.getNextNumber();
        IJ.register(this.getClass());       // protect static class variables (parameters) from garbage collection
        return IJ.setupDialog(imp, flags);
    }

    public void setNPasses(int nPasses) {
        this.nPasses = 2 * nChannels * nPasses;
        pass = 0;
    }

    public void run(ImageProcessor ip) {
        double sizeX = sigmaScaled ? size/imp.getCalibration().pixelWidth : size;
        double sizeY = sigmaScaled ? size/imp.getCalibration().pixelHeight : size;
        if (imp.isComposite() && imp.getNChannels()==imp.getStackSize()) {
            IJ.error("Median", "Composite color images not supported");
            return;
        }
        //new GenericDialog("Median");
        IJ.log("Median filtering with a radius of " + size + " has been started for the image with title '" + title + "'");

        ip.snapshot();
        Median(ip, sizeX, sizeY);
    }

    public void Median(ImageProcessor ip, double sizeX, double sizeY) {
        boolean hasRoi = ip.getRoi().height!=ip.getHeight() && sizeX>0 && sizeY>0;
        if (hasRoi && !calledAsPlugin)
            ip.snapshot();
        FloatProcessor fp = null;
        for (int i=0; i<ip.getNChannels(); i++) {
            fp = ip.toFloat(i, fp);
            if (Thread.currentThread().isInterrupted()) return; // interruption for new parameters during preview?
            MedianFloat(fp);
            if (Thread.currentThread().isInterrupted()) return;
            ip.setPixels(i, fp);
        }
        if (hasRoi)
            resetOutOfRoi(ip, (int)Math.ceil(5*size)); // reset out-of-Rectangle pixels above and below roi
        return;
    }

    private int[][] return_array(int[][] input) {
        int size = (int)this.size;
        int[][] output = new int[width][height];
        int[] kernel = new int[size * size];
        int half_size = (int)size / 2;
        boolean even = (size % 2 == 0) ? true : false;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (even) {
                    for (int w_offset = -half_size; w_offset < half_size; w_offset++) {
                        for (int h_offset = -half_size; h_offset < half_size; h_offset++) {
                            int w_off = w_offset + half_size, h_off = h_offset + half_size;
                            try {
                                kernel[h_off * size + w_off] = input[i + w_offset][j + h_offset];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                kernel[h_off * size + w_off] = 0;
                            }
                        }
                    }
                } else {
                    for (int w_offset = -half_size; w_offset <= half_size; w_offset++) {
                        for (int h_offset = -half_size; h_offset <= half_size; h_offset++) {
                            int w_off = w_offset + half_size, h_off = h_offset + half_size;
                            try {
                                kernel[h_off * size + w_off] = input[i + w_offset][j + h_offset];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                kernel[h_off * size + w_off] = 0;
                            }
                        }
                    }
                }
                Arrays.sort(kernel);
                if (even) output[i][j] = (kernel[(size * size) / 2 - 1] + kernel[(size * size) / 2]) / 2;
                else output[i][j] = kernel[(size * size) / 2];
            }
        }
        return output;
    }

    public void MedianFloat(FloatProcessor fp) {
        Raster[] rasters = new Raster[1];
        rasters[0] = fp.getBufferedImage().getRaster();
        WritableRaster outputRaster = rasters[0].createCompatibleWritableRaster();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        int image[][] = new int[width][height];
        for (int i = 0; i < width; i++){
            if (Thread.currentThread().isInterrupted()) return; // interruption for new parameters during preview?
            for (int j = 0; j < height; j++){
                image[i][j] = rasters[0].getSample(i, j, 0);
            }
        }
        int image_filtered[][] = return_array(image);
        int[] color = new int[1];
        for (int i = 0; i < width; i++){
            if (Thread.currentThread().isInterrupted()) return; // interruption for new parameters during preview?
            for (int j = 0; j < height; j++){
                color[0] = image_filtered[i][j];
                fp.putPixel(i, j, color);
                //outputRaster.setPixel(i, j, color);
            }
        }
        //output.setData(outputRaster);
        //ImagePlus imp = new ImagePlus("Image median", output);
        //imp.show();
        return;
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        size = (int) gd.getNextNumber();
        if (size < 0 || gd.invalidNumber()) return true;
        return false;
    }
}
