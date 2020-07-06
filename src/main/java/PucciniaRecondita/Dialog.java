package PucciniaRecondita;

import javax.swing.*;
import java.awt.event.*;

class Dialog extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static JLabel label   = null;
    private static String TEMPL   = "For further work you need an watershed-lines-image";

    public Dialog() {
        super("Warning!");
        // Выход из программы при закрытии
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        label = new JLabel(String.format(TEMPL));
        super.getContentPane().add(label);

        // Кнопки для создания диалоговых окон
        JButton button1 = new JButton("Cancel");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        // Создание панели содержимого с размещением кнопок
        JPanel contents = new JPanel();
        contents.add(button1);
        setContentPane(contents);
        // Определение размера и открытие окна
        setSize(350, 100);
        setVisible(true);
    }
}

