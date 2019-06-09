import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Main implements ControllerEventListener
{
    static JFrame f = new JFrame("Мой первый мюзик клип сгенеренный кодом");
    static MyDrawPanel ml;

    public static void main(String[] args)
    {
        Main mini = new Main();
        mini.go();
    }

    public void setUpGui()
    {
        ml = new MyDrawPanel();
        f.setContentPane(ml);
        f.setBounds(30,30,300,300);
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

    public static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick)
    {
        MidiEvent event = null;
        try{
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one , two);
            event = new MidiEvent(a, tick);
        } catch(Exception e) {}
        return event;
    }
/*
    public void actionPerformed(ActionEvent event)
    {
        button.setText("Clicked lol!");
    }*/

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
   /* public void play(int instrument, int note) {
        try {
            Sequencer player = MidiSystem.getSequencer();
            player.open();
            Sequence seq = new Sequence(Sequence.PPQ,4);
            Track track = seq.createTrack();

            MidiEvent event = null;

            ShortMessage first = new ShortMessage();
            first.setMessage(192, 1, instrument, 0);
            MidiEvent changeInstrument = new MidiEvent(first, 1);
            track.add(changeInstrument);

            ShortMessage a = new ShortMessage();
            a.setMessage(144,  1, note, 100);
            MidiEvent noteOn = new MidiEvent(a,1);
            track.add(noteOn);

            ShortMessage b = new ShortMessage();
            b.setMessage(128,  1, note, 100);
            MidiEvent noteOff = new MidiEvent(b,16);
            track.add(noteOff);

            player.setSequence(seq);
            player.start();

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }*/
}
