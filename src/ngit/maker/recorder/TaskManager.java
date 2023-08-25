package ngit.maker.recorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TaskManager extends JFrame {
    List<TaskRecorder> recorders = new ArrayList<>();
    public TaskManager(){
        defaultInit();

        JPanel panel = new JPanel(null);
        panel.setSize(getSize());
        panel.setLocation(0, 0);
        addButton(panel);

        add(panel);
        setVisible(true);
    }
    public void defaultInit(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                super.windowClosing(e);
            }
        });
    }


    public void addButton(JPanel panel){
        JButton button = new JButton("新建");
        button.setSize(70, 30);
        button.setLocation((panel.getWidth()-button.getWidth())/2, (panel.getHeight()-button.getHeight())/2);
        button.addActionListener(e -> {
            TaskRecorder recorder = new TaskRecorder(recorders);
            recorders.add(recorder);
            for (TaskRecorder r : recorders){
                System.out.println(r.getCreatedTime());
            }
            System.out.println("\n");
        });
        panel.add(button);
    }
}

class TaskRecorder extends JFrame {
    private String createdTime;
    private JTextArea area;
    public TaskRecorder(List<TaskRecorder> recorders){
        defaultInit(recorders);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(this.getSize());
        panel.setLocation(0,0);
        addTextArea(panel);

        add(panel, BorderLayout.CENTER);

        setTitle(createdTime);
        setVisible(true);
    }

    public void defaultInit(List<TaskRecorder> recorders){
        setSize(200, 100);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        SimpleDateFormat sdf2 = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss:SSS]");
        createdTime = sdf2.format(new Date());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int count = 0;
                if (!recorders.isEmpty()) {
                    for (TaskRecorder r : recorders) {
                        if (Objects.equals(r.createdTime, createdTime)) {
                            recorders.remove(count);
                            break;
                        }
                        count++;
                    }

                    super.windowClosing(e);
                }
            }
        });
    }

    public void addTextArea(JPanel panel){
        area = new JTextArea();
        area.setSize(panel.getSize());
        area.setLocation(0, 0);
        panel.add(area, BorderLayout.CENTER);
    }



    @Override
    public String toString() {
        return area.getText();
    }

    public String  getCreatedTime() {
        return createdTime;
    }
}

class TaskSaver{
    public static void saveTasks(TaskRecorder[] recorders){

    }
}