package PucciniaRecondita;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;

public class MedianFilter {
    private int size;
    private ImageProcessor imageProcessor;
    private ImagePlus imagePlus;
    int width;
    int height;
    String title;

    public MedianFilter(int size) {
        this.size = size;
        this.imagePlus = IJ.getImage();
        this.imageProcessor = IJ.getImage().getProcessor();
        this.title = IJ.getImage().getTitle();
        width = imageProcessor.getWidth();
        height = imageProcessor.getHeight();
    }
    public void setup() { }

    public void run(boolean param) throws IOException {
        if (imagePlus.isComposite() && imagePlus.getNChannels()==imagePlus.getStackSize()) {
            IJ.error("Median", "Composite color images not supported");
            return;
        }
        /*if (param) {
            if (!showDialog())
                return;
        }*/

        new GenericDialog("Median");
        IJ.log("Median filtering with a radius of " + size + " has been started for the image with title '" + title + "'");
        Median();
    }

    protected boolean showDialog(PlugInFilterRunner prf) {
        GenericDialog gd = new GenericDialog("Median");
        gd.addNumericField("Radius", size, 1);
        gd.addPreviewCheckbox(prf);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        size = (int) gd.getNextNumber();
        return true;
    }

    private int[][] return_array(int[][] input) {
        int[][] output = new int[width][height];
        int[] kernel = new int[size * size];
        int half_size = size / 2;
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


    public void Median() throws IOException {

        Raster[] rasters = new Raster[1];
        rasters[0] = imageProcessor.getBufferedImage().getRaster();
        WritableRaster outputRaster = rasters[0].createCompatibleWritableRaster();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);

        int image[][] = new int[width][height];

        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                int[] color = new int[1];
                image[i][j] = rasters[0].getSample(i, j, 0);
                color[0] = image[i][j];
                outputRaster.setPixel(i, j, color);
            }
        }

        int image_filtered[][] = return_array(image);
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                int[] color = new int[1];
                color[0] = image_filtered[i][j];
                outputRaster.setPixel(i, j, color);
            }
        }
        output.setData(outputRaster);
        ImagePlus imp = new ImagePlus("Image median", output);
        imp.updateAndDraw();
    }
}
