package PucciniaRecondita;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class Denoise {

    /*
     * @param inputFiles массив с файлами для обработки
     * @param outputFile файл, в который сохранится результат обработки
     * @param difference максимальная разница между пикселями (0-255)
     * @throws IOException
     */
    Denoise() throws IOException {
        int difference = 50;
        File outputFile = new File("C:Users//gents//Desktop//Puccinia Recondita//2D image4.tif");
        File[] inputFiles = {new File("C:Users//gents//Desktop//Puccinia Recondita//2D image1.tif"),
                new File( "C:Users//gents//Desktop//Puccinia Recondita//2D image2.tif"),
                new File("C:Users//gents//Desktop//Puccinia Recondita//2D image3.tif")};
        //Создаем массив для данных изображений
        Raster[] rasters = new Raster[inputFiles.length];

        //В цикле читаем каждое изображение
        for(int i = 0; i<inputFiles.length; i++) {
            try (ImageInputStream is = ImageIO.createImageInputStream(inputFiles[i])) {
                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(is) ;
                ImageReader imageReader = imageReaders.next();
                imageReader.setInput(is);
                if(imageReader.canReadRaster()) {
                    rasters[i] = imageReader.readRaster(0, null);
                }
                else {
                    rasters[i] =  imageReader.readAsRenderedImage(0, null).getData();
                }
            }
        }

        //Получаем ширину и высоту первого изображения, считая что размеры всех изображений равны
        int width = rasters[0].getWidth();
        int height = rasters[0].getHeight();

        //Создаем растр для записи результирующего изображения, используя характеристики первого изображения
        WritableRaster outputRaster = rasters[0].createCompatibleWritableRaster();

        //В цикле обходим каждый пиксель каждого изображения, усредняя значения по каждому каналу
        for(int x = 0; x<width; x++){
            for(int y = 0; y<height; y++){
                //Массив, со значениями цветов пикселя
                int[] color = new int[3];

                for(int band = 0; band<3; band++){
                    //Массив, со значениями канала определенного пикселя
                    int data[] = new int[rasters.length];

                    for (int imageNum = 0; imageNum<rasters.length; imageNum++) {
                        data[imageNum] = rasters[imageNum].getSample(x, y, band);
                    }

                    //Получаем усредненное значение канала
                    color[band]  = average(data, difference);
                }

                //Устанавливаем цвет пикселю результирующего изображения
                outputRaster.setPixel(x, y, color);
            }
        }

        //Сохраняем изображение
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        output.setData(outputRaster);
        ImageIO.write(output, "tiff", outputFile);
    }

    /**
     *
     * @param data массив с данными пикселя всех изображений для отдельного канала
     * @param difference максимальная разница между пикселем
     * @return усредненное значение канала
     */
    private int average(int[] data, int difference){
        /**Количество изображений*/
        int imagesCount = data.length;
        /**Медианное значение цвета пикселей*/
        int median;

        //Сортируем массив, чтобы цвет пикселя выстроился в порядке возрастания
        Arrays.sort(data);

        //Если количество изображений является четным, используем для получения медианного значения
        //среднее арифметическое значение двух центральных пикселей
        if(imagesCount % 2 == 0) {
            median = (data[imagesCount / 2 - 1] + data[imagesCount / 2]) / 2;
        }
        else {
            median = data[(int)Math.floor(imagesCount / 2)];
        }

        //Максимальное и минимальное отклонение цвета пикселя от медианного значения
        int min = median - difference;
        int max = median + difference;

        //сумма значений канала всех изображений
        int sumBands = 0;
        //Общее количество изображений, не выходящих за рамки min и max
        int counter = 0;

        //В цикле рассчитываем сумму значений канала всех изображений
        for(int i = 0; i<imagesCount; i++){
            //Если значение не превышает указанные пороги - добавляем его к общему значению
            if(data[i]>=min && data[i]<= max){
                sumBands = sumBands+data[i];
                counter++;
            }
        }

        //Если отклонение от медианного значения пикселя не превышает только одно (или ни одно)
        //из изображений - просто усредняем все полученные значения,
        //в противном случае - усредняем только те, которые вошли в указанные рамки
        if(counter <= 1){
            sumBands = 0;
            for(int i = 0; i<imagesCount; i++){
                sumBands = sumBands + data[i];
            }
            sumBands = sumBands/imagesCount;
        }
        else {
            sumBands = sumBands / counter;
        }

        return sumBands;
    }

}