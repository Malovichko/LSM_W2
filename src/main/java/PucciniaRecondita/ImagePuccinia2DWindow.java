package PucciniaRecondita;

import LsmReader.CZLSMInfo;
import MainWindow.*;
import WL.WindowLevelAdjuster;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.Toolbar;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageConverter;
import ij.io.FileSaver;
import ij.io.OpenDialog;
import ij.process.ImageProcessor;
import inra.ijpb.plugins.MorphologicalSegmentation;
import inra.ijpb.plugins.GrayscaleAttributeFilteringPlugin;
import inra.ijpb.plugins.DirectionalFilteringPlugin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.util.ArrayList;

public class ImagePuccinia2DWindow extends JFrame
{
    JComboBox<String> new_box;
    MyLSMImage myimp;
    protected MorphologicalSegmentation morphologicalSegmentation;
    protected DirectionalFilteringPlugin directionalFilteringPlugin;
    protected GrayscaleAttributeFilteringPlugin grayscaleAttributeFilteringPlugin;
    JWindow load_window;
    Toolbar toolBar;



    public ImagePuccinia2DWindow(MyLSMImage myimp)
    {
        super("Puccinia Recondita toolbar");
        IJ.debugMode = false;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // Создание панелей инструментов
        JToolBar tbCommon = new JToolBar();
        tbCommon.add(new MorfSegm());
        tbCommon.add(new GrayScale());
        tbCommon.add(new DirectionalFiltering());
        tbCommon.add(new Anis_Diffusion());
        tbCommon.addSeparator();
        // Блокируем возможность перетаскивания панели
        tbCommon.setFloatable(false);
        //PopupMenu jb = new PopupMenu("WL");
        JButton jb = new JButton("WL");
        jb.setMaximumSize(new Dimension(32, 32));
        //jb.addMouseListener(new CustomListener());
        //tbCommon.add(jb);
        jb.setToolTipText("Window/Level settings");

        toolBar = new Toolbar();
        //Window_Level_Tool tool = new Window_Level_Tool();
        //tool.addPopupMenu(toolBar);

        jb.addMouseListener(new CustomListener(toolBar));
        //toolBar.add(jb);
        //toolBar.addMouseListener(new CustomListener());

        this.myimp = myimp;

        String[] processing_method = new String[]{"Leaf surface with sprout tube", "Cell structure of the epidermis of the leaf",
                "Cell structure segmentation",
                "Segmentation of morphological structures of the fungus" };
        JToolBar tbEducation = new JToolBar();
        new_box = new JComboBox<>(processing_method);
        tbEducation.add(new JButton("Choose an image processing method"));
        tbEducation.add(new_box);
        new_box.addActionListener(new Method_selection());





        // Блокируем возможность перетаскивания панели
        tbEducation.setFloatable(false);
        // Блокируем возможность эффекта интерактивности - при наведении
        // мыши кнопка выделяется
        tbEducation.setRollover(false);
        // Выравнивание содержимого
        tbEducation.add(Box.createGlue());

        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem(new OpenAction());
        open.setText("Open");
        JMenuItem save = new JMenuItem(new SaveAction());
        save.setText("Save");
        JMenuItem exit = new JMenuItem(new ExitAction());
        exit.setText("Exit");
        JMenu process = new JMenu("Process");
        JMenu action = new JMenu("Action");
        JMenu filters = new JMenu("Filters");
        filters.setText("Filters");
        JMenuItem gauss_blr = new JMenuItem(new Gauss_Blr());
        gauss_blr.setText("Gaussian Blur");
        JMenuItem median = new JMenuItem(new Median());
        median.setText("Median");
        JMenuItem tables = new JMenuItem(new Open_table_creating());
        tables.setText("Create tables");
        JMenuItem skeleton = new JMenuItem(new Skeleton());
        skeleton.setText("Skeletonize");
        JMenuItem diving_value_update = new JMenuItem(new Diving_value_update());
        diving_value_update.setText("Diving value");

        JToolBar jtb = new JToolBar();
        jtb.add(jb);
        jtb.add(toolBar);
        jtb.setFloatable(false);

        file.add(open);
        file.addSeparator();
        file.add(save);
        file.addSeparator();
        file.add(exit);
        file.addSeparator();
        filters.add(gauss_blr);
        filters.add(median);
        action.add(tables);
        action.add(skeleton);
        action.add(diving_value_update);
        process.add(filters);
        process.add(action);
        menu.add(file);
        menu.add(process);
        menu.add(tbCommon);
        menu.add(tbEducation);
        super.add(jtb);
        //super.add(toolBar);

        super.setJMenuBar(menu);
       // super.setAlwaysOnTop(true);

        // Выводим окно на экран
        setSize(1000, 115);
        setVisible(true);

        super.addMouseListener(new CustomListener(toolBar));


        load_window = new JWindow();
        JLabel jLabel = new JLabel("",  SwingConstants.CENTER);
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("VAyR.gif"));
        image = image.getScaledInstance(75, 75, 0);
        ImageIcon imageIcon = new ImageIcon(image);
        imageIcon.setImageObserver(jLabel);
        jLabel.setIcon(imageIcon);
        load_window.getContentPane().add(jLabel);
        load_window.setBounds(super.getWidth() / 2, 200, imageIcon.getIconWidth(), imageIcon.getIconHeight());
        load_window.setBackground(new Color(1f,1f,1f,0f ));

       // CZLSMInfo czlsmInfo = new CZLSMInfo();
        //Reader r = new Reader();
       // czlsmInfo = r.lsmInfo;
       //czlsmInfo= r.getLsmInfo();


     //   double pixelSize = info.VoxelSizeX * info.VoxelSizeY ;
       // System.out.println("x * y = " + pixelSize);
    }


    public void create_new_chanel(double chanel_index_1, double chanel_index_2, double chanel_index_3)
    {
        ArrayList<Double> coef = new ArrayList<>();
        coef.add(chanel_index_1);
        coef.add(chanel_index_2);
        coef.add(chanel_index_3);
        myimp.setNewChannel(coef);
        myimp.setChanel(3);
    }

    public void Puccinia()
    {
        ImagePlus ip = create_mask();
        ImagePlus mask = ip;
        ip = myimp.convertTo2DImage(0, 0);
        ImageWindow win = new ImageWindow(ip);
        Image2DProcessor im2dproc = myimp.get2DProc();
       /* OpenDialog od = new OpenDialog("Open image mask...");
        String name = od.getFileName();
        if (name == null) return;
        String dir = od.getDirectory();
        String path = dir + name;
        ImagePlus new_imp = new ImagePlus(path);*/
         im2dproc.setImageMask(mask.getProcessor());
        win.getImagePlus().setProcessor(im2dproc.getCur2DProc());
    }

    public void cell_structure_rgb()
    {
        double coef[] = new double[3];
        coef[0] = 2.0; coef[1] = 1.0; coef[2] = 3.0;
        ImagePlus ip = create_mask();
        ImagePlus mask = ip;
        coef = show_dialog(coef);
        load_window.setVisible(true);
        create_new_chanel(coef[0], coef[1], coef[2]);
        IJ.log("Channel coefficients: channel 1 " + coef[0] + ", channel 2 " + coef[1] + ", channel 3 " + coef[2]);

        ip = myimp.convertTo2DImage(10, 10);
        ImageWindow win = new ImageWindow(ip);
        Image2DProcessor im2dproc = myimp.get2DProc();
        ip.setProcessor(im2dproc.getColored2dProc());
        /*OpenDialog od = new OpenDialog("Open image mask...");
        String name = od.getFileName();
        if (name == null) return;
        String dir = od.getDirectory();
        String path = dir + name;
        ImagePlus new_imp = new ImagePlus(path);*/
        im2dproc.setImageMask(mask.getProcessor());
        win.getImagePlus().setProcessor(im2dproc.getColored2dProc());
        SetLabelMapPlugin dialog = new SetLabelMapPlugin(myimp, ip, true);

       // diving_value = show_dialog(14);
        load_window.setVisible(true);
       // im2dproc.setDiveValue(diving_value);
        dialog.run(null);
    }

    public void cell_structure() throws InterruptedException {
        double coef[] = new double[3];
        coef[0] = 2.0; coef[1] = 1.0; coef[2] = 3.0;
        int diving_value;
        ImagePlus ip = create_mask();
        ImagePlus mask = ip;
        coef = show_dialog(coef);
        load_window.setVisible(true);
        create_new_chanel(coef[0], coef[1], coef[2]);
        IJ.log("Channel coefficients: channel 1 " + coef[0] + ", channel 2 " + coef[1] + ", channel 3 " + coef[2]);
        ip = myimp.convertTo2DImage(5, 10);
        ImageWindow win = new ImageWindow(ip);
        Image2DProcessor im2dproc = myimp.get2DProc();
        ip.setProcessor(im2dproc.getCur2DProc());

      // OpenDialog od = new OpenDialog("Open image mask...");
       /* String name = "C://LSM_W2//mask.tiff";
        if (name == null) return;
       /* String dir = od.getDirectory();
        String path = dir + name;
        ImagePlus new_imp = new ImagePlus("C://LSM_W2//mask.tiff");*/
        im2dproc.setImageMask(mask.getProcessor());
        win.getImagePlus().setProcessor(im2dproc.getCur2DProc());
        SetLabelMapPlugin dialog = new SetLabelMapPlugin(myimp, ip, 14, false);
        load_window.setVisible(true);
        dialog.run(null);

        Window_Level_Tool wlt = new Window_Level_Tool();
        ActionEvent ae = new ActionEvent(this, -1, "Auto");
//        wlt.showPopupMenu(toolBar);
        wlt.actionPerformed(ae);
        load_window.setVisible(true);

        Anisotropic_Diffusion_2D anisotropicDiffusion2D = new Anisotropic_Diffusion_2D();
        anisotropicDiffusion2D.setup("", IJ.getImage());
        anisotropicDiffusion2D.show_window(false);
        anisotropicDiffusion2D.join();
        load_window.setVisible(true);

        Gaussian_Blur_3D gb3d = new Gaussian_Blur_3D();
        gb3d.run(IJ.getImage(), false);

        load_window.setVisible(false);
        morphologicalSegmentation = new MorphologicalSegmentation();
        morphologicalSegmentation.run("");
    }
    public void sprout_tube()
    {
        ImagePlus imp = myimp.getImp();
        /*ImageStack imageStack;
        imageStack = */ imp.getImageStack();
        double coef[] = new double[3];
        coef[0] = -1.0; coef[1] = 2.0; coef[2] = 3.0;

        ImagePlus ip = create_mask();
        ImagePlus mask = ip;


        /*imp = myimp.getImp();
        imageStack = imp.getImageStack();
        n = imageStack.getSize();
        for (int i = 0; i < n/2; i++) {
            imageStack.deleteLastSlice();
        }
        myimp.setImpStack(imageStack);*/
        coef = show_dialog(coef);
        load_window.setVisible(true);
        create_new_chanel(coef[0], coef[1], coef[2]);
        IJ.log("Channel coefficients: channel 1 " + coef[0] + ", channel 2 " + coef[1] + ", channel 3 " + coef[2]);

        ip = myimp.convertTo2DImage(10, 10);

        ImageWindow win = new ImageWindow(ip);
        Image2DProcessor im2dproc = myimp.get2DProc();
        ip.setProcessor(im2dproc.getCur2DProc());

        im2dproc.setImageMask(mask.getProcessor());
        win.getImagePlus().setProcessor(im2dproc.getCur2DProc());
        SetLabelMapPlugin dialog = new SetLabelMapPlugin(myimp, ip, -1, false);
        load_window.setVisible(true);
        dialog.run(null);

        directionalFilteringPlugin = new DirectionalFilteringPlugin();
        directionalFilteringPlugin.showDialog(IJ.getImage(), "", null);
        load_window.setVisible(true);
        directionalFilteringPlugin.run(IJ.getImage().getProcessor());
        directionalFilteringPlugin.setup("final", IJ.getImage());

        GaussianBlur gaussianBlur = new GaussianBlur();
        new PlugInFilterRunner(gaussianBlur, "Gaussian Blur", null);

        Window_Level_Tool wlt = new Window_Level_Tool();
        wlt.run(false);

        //костыль
        IJ.getImage().setImage(IJ.getImage().getImage());
        new Mask(IJ.getImage().getProcessor());

        MedianFilterRunner medianFilterRunner = new MedianFilterRunner();
        new PlugInFilterRunner(medianFilterRunner, "Median Filter", null);

       // new Mask(IJ.getImage().getProcessor());


    }


    protected ImagePlus create_mask()
    {
        ImagePlus imp = myimp.getImp();
        Gaussian_Blur_3D gb3d = new Gaussian_Blur_3D();
        gb3d.run(imp, false);
        ImagePlus ip;
        Image2DProcessor mask = new Image2DProcessor();
        mask.calculateSurface(myimp.imageStacks.get(0), 10, 10);
        mask.smooth2DImage(10, 10);
        ip = mask.getMaskImage();
        ip.setCalibration(myimp.getOriginalImage().getCalibration());
        myimp.cleanImage();
        return ip;
    }
    private void advice(){
        GenericDialog gd = new GenericDialog("Warning!");
        gd.addMessage("For further work you need the watershed-lines-image");
        gd.showDialog();
    }



    protected double[] show_dialog(double[] coef){
        GenericDialog gd = new GenericDialog("Coefficients");
        gd.addMessage("select channel coefficients");
        gd.addNumericField("Channel 1", coef[0], 1);
        gd.addNumericField("Channel 2", coef[1], 1);
        gd.addNumericField("Channel 3", coef[2], 1);
        gd.showDialog();
        if (gd.wasCanceled())
            return null;
        coef[0] = gd.getNextNumber();
        coef[1] = gd.getNextNumber();
        coef[2] = gd.getNextNumber();
        return coef;
    }


    //-----------------------------------------------------------------------------
    // Команда для кнопки "Сохранения"
    class SaveAction extends AbstractAction{
        private BufferedImage image;

        public SaveAction() {
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource("save.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Настройка иконок
            putValue(AbstractAction.SMALL_ICON, new ImageIcon(image));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // Сохранение файла как TIFF
            (new FileSaver(IJ.getImage())).saveAsTiff();
        }
    }
    //-----------------------------------------------------------------------------
    // Команда для кнопки "Размытия Гаусса"
    class Gauss_Blr extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            GaussianBlur gaussianBlur = new GaussianBlur();
            new PlugInFilterRunner(gaussianBlur, "Gaussian Blur", null);

        }
    }
    //-----------------------------------------------------------------------------
    // Команда для кнопки "Median"
    class Median extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            MedianFilterRunner medianFilterRunner = new MedianFilterRunner();
            new PlugInFilterRunner(medianFilterRunner, "Median Filter", null);
        }
    }

    //-----------------------------------------------------------------------------
    // Команда для кнопки "Морфологическая сегментация"
    class MorfSegm extends AbstractAction{

        private BufferedImage image;

        public MorfSegm() {
            // Настройка иконок
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource("morf_segm.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            putValue(AbstractAction.SMALL_ICON, new ImageIcon(image));
            putValue(AbstractAction.SHORT_DESCRIPTION, "plugin Morphological Segmentation");
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            morphologicalSegmentation = new MorphologicalSegmentation();
            morphologicalSegmentation.run("");
        }
    }
    //-----------------------------------------------------------------------------
    // Команда для кнопки "Анизотропная диффузия"
    class Anis_Diffusion extends AbstractAction{
        private BufferedImage image;
        public Anis_Diffusion(){
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource("anis.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            putValue(AbstractAction.SMALL_ICON, new ImageIcon(image));
            putValue(AbstractAction.SHORT_DESCRIPTION, "Anisotropic diffusion");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Anisotropic_Diffusion_2D anisotropicDiffusion2D = new Anisotropic_Diffusion_2D();
            anisotropicDiffusion2D.setup("", IJ.getImage());
            anisotropicDiffusion2D.show_window(true);
        }
    }
    //-----------------------------------------------------------------------------
    // Команда для кнопки "Gray scale"
    class GrayScale extends AbstractAction{

        private BufferedImage image;

        public GrayScale() {
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource("wheat.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Настройка иконок
            putValue(AbstractAction.SMALL_ICON, new ImageIcon(image));
            putValue(AbstractAction.SHORT_DESCRIPTION, "Grayscale filtering");
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            grayscaleAttributeFilteringPlugin = new GrayscaleAttributeFilteringPlugin();
            System.out.println(grayscaleAttributeFilteringPlugin.setup("", IJ.getImage()));
            System.out.println(grayscaleAttributeFilteringPlugin.showDialog(IJ.getImage(), "", null));
            grayscaleAttributeFilteringPlugin.run(IJ.getImage().getProcessor());
            grayscaleAttributeFilteringPlugin.setup("final", IJ.getImage());
        }
    }


    //-----------------------------------------------------------------------------
    // Команда для кнопки "create tables"
    class Open_table_creating extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            advice();
            ImagePlus imagePlus = IJ.getImage();
            ImageConverter imageConverter = new ImageConverter(imagePlus);
            imageConverter.convertToGray8();
            ImageProcessor imageProcessor = IJ.getImage().getProcessor();
            Skeletonize3D_ skeletonize3D_ = new Skeletonize3D_();
            skeletonize3D_.setup("", imagePlus);
            skeletonize3D_.run(imageProcessor);
            imagePlus.updateAndDraw();
            try {
                new Mask(IJ.getImage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //-----------------------------------------------------------------------------
    // Команда для кнопки "skeletonize"
    class Skeleton extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            ImagePlus imagePlus = IJ.getImage();
            ImageConverter imageConverter = new ImageConverter(imagePlus);
            imageConverter.convertToGray8();
            ImageProcessor imageProcessor = IJ.getImage().getProcessor();
            Skeletonize3D_ skeletonize3D_ = new Skeletonize3D_();
            skeletonize3D_.setup("", imagePlus);
            skeletonize3D_.run(imageProcessor);
            imagePlus.updateAndDraw();
            IJ.getImage().setTitle("Skeleton of a sprout tube");
        }
    }


    //-----------------------------------------------------------------------------
    // Команда для кнопки "Открытия"
    class OpenAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private BufferedImage image;

        public OpenAction() {
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource("open.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Настройка иконок
            putValue(AbstractAction.SMALL_ICON, new ImageIcon(image));
        }

        // Обработка действия
        public void actionPerformed(ActionEvent e) {
            OpenDialog od = new OpenDialog("Open image...");
            String name = od.getFileName();
            if (name == null)
                return;
            String dir = od.getDirectory();
            String path = dir + name;
            ImagePlus imp = new ImagePlus(path);
            imp.show();
            ImageConverter imageConverter = new ImageConverter(imp);
            imageConverter.convertToGray8();

        }
    }

    class Diving_value_update extends  AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            SetLabelMapPlugin dialog = new SetLabelMapPlugin(myimp, IJ.getImage(), -1, false);
        }
    }


    //-----------------------------------------------------------------------------
    // Команда для кнопки "DirectionalFilter"
    class DirectionalFiltering extends AbstractAction {
        private BufferedImage image;

        public DirectionalFiltering() {
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource("directional.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Настройка иконок
            putValue(AbstractAction.SMALL_ICON, new ImageIcon(image));
            putValue(AbstractAction.SHORT_DESCRIPTION, "Directional filtering");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            directionalFilteringPlugin = new DirectionalFilteringPlugin();
            directionalFilteringPlugin.showDialog(IJ.getImage(), "", null);
            directionalFilteringPlugin.run(IJ.getImage().getProcessor());
            directionalFilteringPlugin.setup("final", IJ.getImage());

        }
    }



    //-----------------------------------------------------------------------------
    // Команда для кнопки "Выхода"
    class ExitAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        private BufferedImage image;

        public ExitAction() {
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource("exit.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Настройка иконок
            putValue(AbstractAction.SMALL_ICON, new ImageIcon(image));
        }

        // Обработка действия
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    //-----------------------------------------------------------------------------

    /**
     * Класс выравнивания компонентов в контейнере
     */
    class BoxLayoutUtils
    {
        // Выравнивание компонентов по оси X
        public void setGroupAlignmentX(JComponent[] cs, float alignment) {
            for (int i = 0; i < cs.length; i++) {
                cs[i].setAlignmentX(alignment);
            }
        }

        // Выравнивание компонентов по оси Y
        public void setGroupAlignmentY(JComponent[] cs, float alignment) {
            for (int i = 0; i < cs.length; i++) {
                cs[i].setAlignmentY(alignment);
            }
        }

        // Создание панели с установленным вертикальным блочным расположением
        public JPanel createVerticalPanel() {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            return p;
        }

        // Создание панели с установленным горизонтальным блочным расположением
        public JPanel createHorizontalPanel() {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
            return p;
        }
    }


    class CustomListener implements MouseListener
    {
        Toolbar toolBar;
        CustomListener (Toolbar toolBar) {
            this.toolBar = toolBar;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            Window_Level_Tool wlt = new Window_Level_Tool();
            e.setSource(this.toolBar);
            wlt.showPopupMenu(e, this.toolBar);

            //CT_Window_Level ctwl = new CT_Window_Level();
            //ctwl.run("");
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    class Method_selection implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println(new_box.getSelectedIndex());
            switch (new_box.getSelectedIndex()) {
                case (0):
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            load_window.setVisible(true);
                            Puccinia();
                            load_window.setVisible(false);
                        }
                    }).start();
                    break;
                case (1):
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            load_window.setVisible(true);
                            cell_structure_rgb();
                            myimp.cleanImage();
                            load_window.setVisible(false);
                        }
                    }).start();
                    break;
                case (2):
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                load_window.setVisible(true);
                                cell_structure();
                                myimp.cleanImage();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case (3):
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            load_window.setVisible(true);
                            sprout_tube();
                            myimp.cleanImage();
                            load_window.setVisible(false);
                        }
                    }).start();
                    break;
                case (4):
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        load_window.setVisible(true);
                        try {
                            new Denoise();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        load_window.setVisible(false);
                    }
                }).start();
                break;
            }
        }
    }

}
