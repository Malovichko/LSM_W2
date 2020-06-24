package PucciniaRecondita;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.process.ImageProcessor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Mask {

    Mask(ImagePlus imagePlus) throws IOException {
        ImagePlus imagePlus1 = open_image();
        convert_to_raster(imagePlus.getProcessor(), imagePlus1.getProcessor());
    }

    Mask(ImageProcessor imageProcessor){
        make_black_and_white(imageProcessor);
    }


    public void printMap(Map mp)
    {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    private ImagePlus open_image(){
        JFileChooser chooser = new JFileChooser(OpenDialog.getLastDirectory());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileNameExtensionFilter("TIFF File(*.tif)", "tif"));
        int returnVal = chooser.showOpenDialog(null);

        if (returnVal != JFileChooser.APPROVE_OPTION)
            return null;

        String filePath = chooser.getSelectedFile().getAbsolutePath();
        IJ.showStatus("Opening: " + filePath);
        ImagePlus imagePlus = IJ.openImage(filePath);
        return imagePlus;
    }

    public void show_info(String text){
        GenericDialog gd = new GenericDialog("Message");
        gd.addMessage(text);
        gd.showDialog();
    }

    public void make_black_and_white(ImageProcessor imageProcessor){
        Raster[] rasters = new Raster[1];
        rasters[0] = imageProcessor.getBufferedImage().getRaster();
        int width = rasters[0].getWidth();
        int height = rasters[0].getHeight();
        WritableRaster outputRaster = rasters[0].createCompatibleWritableRaster();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        int band[] = new int[rasters.length];
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int[] color = new int[4];

                for (int imageNum = 0; imageNum < rasters.length; imageNum++)
                {
                    band[imageNum] = rasters[imageNum].getSample(x, y, 0);

                }
                if (band[0] > 0 && band[0] < 255) band[0] = 255;
                color[0] = band[0];
                outputRaster.setPixel(x, y, color);
            }
        }
        output.setData(outputRaster);
        ImagePlus imp = new ImagePlus("Image", output);
        IJ.getImage().setImage(imp);
        //imp.show();

    }

    protected void vector_coordinate_search(Map<Integer, List<Integer>> points, WritableRaster r) {
        boolean found = false;
        Iterator it = points.entrySet().iterator();
        while (it.hasNext()) {
            int x, y;
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList al = (ArrayList) pair.getValue();
            x = (int) al.get(0);
            y = (int) al.get(1);
            int[] color = new int[4];
            r.getPixel(x, y, color);
            for (int k = 0; k < 5; k++) {
                int prevX = 0, prevY = 0;
                for (int j = -1; j <= 1; j++) {
                    if (found == true) break;
                    for (int i = -1; i <= 1; i++) {
                        if (x + i < 0 || x + i >= r.getWidth() || y + j < 0 || y + j >= r.getHeight() || (i == 0 && j == 0)
                                || x + i == prevX && y + j == prevY)
                            continue;
                        r.getPixel(x + i, y + j, color);
                        prevX = x;
                        prevY = y;
                        if (color[0] == 0 || color[0] == 150)
                        {
                            found = true;
                            x = x + i;
                            y = y + j;
                            break;
                        }

                    }
                }

            }
        angle_calculation(x, y, 613, 213);

        }
    }

    private void angle_calculation(int ax, int ay, int bx, int by){
        double ma = Math.sqrt(ax*ax + ay*ay);
        double mb = Math.sqrt(bx*bx + by*by);
        double sc = ax * bx + ay * by;
        double res = Math.acos(sc / ma / mb);
        System.out.printf("%.5f\n",res);
    }


    public void convert_to_raster(ImageProcessor imageProcessor, ImageProcessor imageProcessor1) throws IOException {
        Raster[] rasters = new Raster[2];
        rasters[0] = imageProcessor.getBufferedImage().getRaster();
        rasters[1] = imageProcessor1.getBufferedImage().getRaster();
        int width = rasters[0].getWidth();
        int height = rasters[0].getHeight();
        WritableRaster outputRaster = rasters[0].createCompatibleWritableRaster();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);

        Map<Integer, List<Integer>> intersection_points = new HashMap();
        int band[] = new int[rasters.length];


        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int[] color = new int[4];

                for (int imageNum = 0; imageNum < rasters.length; imageNum++)
                {
                    band[imageNum] = rasters[imageNum].getSample(x, y, 0);
                }

                if (band[0] == 0) band[0] = 255;
                else  band[0] = 0;
                color[0] = band[0];
                if (band[0] == 0 && band[1] == 0)
                {
                    if (!intersection_points.containsKey(x)) {
                        intersection_points.put(x, new ArrayList<Integer>());
                    }
                    intersection_points.get(x).add(y);
                    color[0] = 150;

                }
                outputRaster.setPixel(x, y, color);
            }
        }
      //  vector_coordinate_search(intersection_points, outputRaster);
        new Table(intersection_points, "Точки пересечения с клеточными стенками");
        output.setData(outputRaster);
        ImagePlus imp = new ImagePlus("Image", output);
        imp.show();
        showY(outputRaster);
    }




    void showY (WritableRaster r) {
        WritableRaster outputRaster = r;
        int width = r.getWidth();
        int height = r.getHeight();
        Map<Integer, List<Integer>> Y = new HashMap();
        for (int y = 1; y < height-1; y++)
        {
            for (int x = 1; x < width-1; x++)
            {
                int[] color = new int[4];
                outputRaster.getPixel(x, y, color);
                if (color[0] == 255) continue;
                boolean arr[] = new boolean[9];
                if (color[0] == 0) {}
                for (int j = -1; j <= 1; j++)
                {
                    for (int i = -1; i <= 1; i++)
                    {
                        if (x + i < 0 || x + i >= width || y + j < 0 || y + j >= height || (i == 0 && j == 0)) continue;
                        outputRaster.getPixel(x + i, y + j, color);
                        if (color[0] != 255)
                        {
                            arr[(j + 1) * 3 + (i + 1)] = true;
                        }
                    }
                }

                int wasV = 0;
                boolean waspix = false;
                int num;
                for (int k = 0; k <= 7; k++) {
                    num = k;
                    if (num == 3) num = 5;
                    else if (num == 4) num = 8;
                    else if (num == 5) num = 7;
                    else if (num == 7) num = 3;
                    if (waspix && arr[num]) continue;
                    else if (arr[num]) waspix = true;
                    else if (waspix){
                        wasV++;
                        waspix = false;
                    }
                }
                if (waspix && !arr[0]) wasV++;
                if (wasV >= 3)
                {
                    if (!Y.containsKey(x)) {
                        Y.put(x, new ArrayList<Integer>());
                    }
                    Y.get(x).add(y);
                    //Y.get(x).add(y - 20);
                    //Y.get(x).add(y - 100);
                 //   System.out.println(x + " " + y);
                }
            }
        }

        new Table(Y, "Точки ветвления ростковой трубки");
        paint(Y, outputRaster);

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        output.setData(outputRaster);
        ImagePlus imp = new ImagePlus("Image", output);
        imp.show();
    }

    void paint(Map Y, WritableRaster outputRaster) {
        int width = outputRaster.getWidth();
        int height = outputRaster.getHeight();
        Iterator it = Y.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ArrayList al = ((ArrayList)pair.getValue());
            for (int num = 0; num < al.size(); num++)
            {
                int x = (int)pair.getKey();
                int y = (int)al.get(num);
                int color[] = new int[1];
                color[0] = 10;
                int R = 15;
                int lasth = R;
                for (int k = 0; k <= R; k++)
                {
                    int i = k;
                    int j = (int)Math.sqrt(R*R - i*i);
                    if (!(x + i < 0 || x + i >= width || y + j < 0 || y + j >= height)) paintLine(x + i, y + lasth, y + j, color, outputRaster);
                    j = -j;
                    lasth = -lasth;
                    if (!(x + i < 0 || x + i >= width || y + j < 0 || y + j >= height)) paintLine(x + i, y + lasth, y + j, color, outputRaster);
                    i = -i;
                    if (!(x + i < 0 || x + i >= width || y + j < 0 || y + j >= height)) paintLine(x + i, y + lasth, y + j, color, outputRaster);
                    j = -j;
                    lasth = -lasth;
                    if (!(x + i < 0 || x + i >= width || y + j < 0 || y + j >= height)) paintLine(x + i, y + lasth, y + j, color, outputRaster);
                    lasth = j;
                }
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    void paintLine(int x, int y1, int y2, int[] color, WritableRaster outputRaster) {
        int y;
        if (y2 >= y1) {
            y = y1;
            outputRaster.setPixel(x, y, color);
            while (y < y2) {
                outputRaster.setPixel(x, y, color);
                y++;
            }
        } else {
            y = y2;
            outputRaster.setPixel(x, y, color);
            while (y < y1) {
                outputRaster.setPixel(x, y, color);
                y++;
            }
        }
    }
}