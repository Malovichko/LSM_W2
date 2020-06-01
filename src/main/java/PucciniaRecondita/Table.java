package PucciniaRecondita;

import javax.swing.*;
import java.awt.*;
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



}

