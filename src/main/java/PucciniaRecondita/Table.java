package PucciniaRecondita;

import ResultWindow.ObjectInfo;
import ij.io.SaveDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

public class Table extends JFrame {

    Table(Map<Integer, List<Integer>> Y, String table_name)
    {
        branch_point_table(Y, table_name);
    }

    private void create_frame(Object[][] data, String[] columnNames, String table_name)
    {
        JFrame frame = new JFrame(table_name);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTable table = new JTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);

        frame.getContentPane().add(scrollPane);
        frame.setPreferredSize(new Dimension(350, 200));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        save_tables(data, table_name);
    }

    private void branch_point_table( Map<Integer, List<Integer>> Y, String table_name)
    {
        String[] columnNames = {
                "Номер точки",
                "Координата X",
                "Координата Y"
        };
        String[][] data = convertWithStream(Y);
        create_frame(data, columnNames, table_name);

    }

    private void sprout_table()
    {
        String[] columnNames = {
                "Количество точек ветвления",
                "Количество точек пересечения с клеточными стенками",
                "Длина ростковой трубки"
        };
        //String[][] data = convertWithStream(Y);
        // create_frame(data, columnNames, table_name);

    }
    public String[][] convertWithStream(Map<Integer, ?> map)
    {
        List<String> pairs = new ArrayList();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList al = (ArrayList)pair.getValue();
            for (int num = 0; num < al.size(); num++)
            {
                pairs.add(pair.getKey() + " " + al.get(num));
            }
        }
        String[][] data = new String[pairs.size()][3];
        for (int i = 0; i < pairs.size(); i++)
        {
            data[i][0] = String.valueOf(i + 1);
            String[] splt = pairs.get(i).split(" ");
            data[i][1] = splt[0];
            data[i][2] = splt[1];
        }
        return data;
    }

    public void save_tables(Object[][] data, String table_name) {

            try {

                SaveDialog openDialog = new SaveDialog("Save as CSV", this.getTitle(), ".csv");
                String directory = openDialog.getDirectory();
                String name = openDialog.getFileName();
                if (name == null) return;

                String path = directory + name;
                if (name.split(".").length == 1) {
                    path += ".csv";
                }
                System.out.println(path);
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                writer.write("Number; coordinate X; coordinate Y\n");
                for (int i = 0; i < data.length; i++) {
                    writer.write( data[i][0].toString() + ";"
                            + data[i][1].toString() + ";"
                            + data[i][2].toString() + "\n");
                }
                writer.close();
            }
            catch (IOException err) {
                err.printStackTrace();
            }
    }



}

