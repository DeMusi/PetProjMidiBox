import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Main
{
    // Панель где расположены дорожки с драмами
   JPanel mainPanel;
   ArrayList<JCheckBox> checkboxList;
   Sequencer sequencer;
   Sequence sequence;
   Track track;
   JFrame theFrame;

   String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
           "Open Hi-Hat", "Acoustic Share", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
            "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
            "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args)
    {
        new Main().buildGUI();
    }

    public void buildGUI()
    {
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        // Поля между краями панели и местом размещения компонентов
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        // 4 кнопки Старт, Стоп, Выше/Ниже скорость
        JButton start = new JButton("Start");
        // Привязываем к кнопке старт, класс слушатель который будет обрабатывать эвенты
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        // Привязываем к кнопке стоп, класс слушатель который будет обрабатывать эвенты
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo UP");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        // Размещение всех 16 названий инструментов в колонке nameBox по вертикали(Y_Axis)
        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i = 0; i < 16; i++)
        {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        // отступы по вертикали и горизонтали
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        /* создаем флажки для дорожек, все будут false - не активны
        *  добавляем их в ArrayList, потом на панель*/
        for(int i = 0; i < 256; i++)
        {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    // Преобразование состояния флажков в МИДИ события
    // Добавление в дорожку
    public void buildTrackAndStart()
    {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i = 0; i < 16; i++)
        {
            trackList = new int[16];
            int key = instruments[i];

            for(int j = 0; j < 16; j++)
            {
                JCheckBox jc = (JCheckBox)checkboxList.get(j+(16*i));
                if(jc.isSelected())
                {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            makeTracks(trackList);
            track.add(makeEvent(176, 1,127,0,16));
        }

        track.add(makeEvent(192,9,1,0,15));
        try {
            sequencer.setSequence(sequence);
            // Непрерывный цикл воспроизведения
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch(Exception e) { e.printStackTrace(); }
    }

    public class MyStartListener implements ActionListener
    {
        public void actionPerfromed(ActionEvent a)
        {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener
    {
        public void actionPerfromed(ActionEvent a)
        {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener
    {
        public void actionPerfromed(ActionEvent a)
        {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener
    {
        public void actionPerfromed(ActionEvent a)
        {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * .97));
        }
    }

    public void setUpMidi()
    {
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch(Exception e) { e.printStackTrace(); }
    }

    public void setUpGui()
    {
        ml = new MyDrawPanel();
        f.setContentPane(ml);
        f.setBounds(30,30,300,300);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    public void go()
    {
        setUpGui();
        try{
            Sequencer player = MidiSystem.getSequencer();
            player.open();
            player.addControllerEventListener(ml, new int[] {127});
            Sequence seq = new Sequence(Sequence.PPQ,4);
            Track track = seq.createTrack();

            int r = 0;
            for(int i = 0; i < 60; i+=4)
            {
                r = (int)((Math.random() * 50) + 1);
                track.add(makeEvent(144, 1, r, 100, i));
                // ловит события воспроизведения звука (первый парам - 176)
                track.add(makeEvent(176, 1, 127, 0, i));
                track.add(makeEvent(128, 1, r, 100, i+2));
            }

            player.setSequence(seq);
            player.start();
            player.setTempoInBPM(220);
        } catch(Exception ex) { ex.printStackTrace(); }
        /*JFrame frame = new JFrame();
        button = new JButton("click me");
        button.addActionListener(this);
        frame.getContentPane().add(button);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.setVisible(true);*/
    }

    public void controlChange(ShortMessage event)
    {
        System.out.println("Ля!");
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick)
    {
        MidiEvent event = null;
        try{
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one , two);
            event = new MidiEvent(a, tick);
        } catch(Exception e) {}
        return event;
    }


    class MyDrawPanel extends JPanel implements ControllerEventListener
    {
        boolean msg = false;

        public void controlChange(ShortMessage event)
        {
            msg = true;
            repaint();
        }

        public void paintComponent(Graphics g)
        {
            if(msg)
            {
                Graphics2D g2 = (Graphics2D) g;
                int r = (int) (Math.random() * 250);
                int gr = (int) (Math.random() * 250);
                int b = (int) (Math.random() * 250);

                g.setColor(new Color(r, gr, b));

                int ht = (int)((Math.random() * 120) + 10 );
                int width = (int)((Math.random() * 120) + 10 );
                int x = (int)((Math.random() * 40) + 10 );
                int y = (int)((Math.random() * 40) + 10 );
                g.fillRect(x, y, width, ht);
                msg = false;
            }
        }
    }
}
