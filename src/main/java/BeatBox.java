import javax.sound.midi.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BeatBox {

    private JPanel mainPanel;
    private ArrayList<JCheckBox> checkBoxList;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private JFrame theFrame;
    private String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
    "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
    "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
    "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
    "Open Hi Conga"};

    private int[] instruments = {35,42,46,38,49,39,50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    public static void main (String[] args)
    {
        new BeatBox().buildGUI();
    }

    private void buildGUI()
    {
        theFrame = new JFrame("Super BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkBoxList = new ArrayList<>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i =0; i < 16; ++i)
        {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid  = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for( int i =0; i < 256; ++i)
        {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    private void setUpMidi()
    {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);

//            int[] eventsIWant = new int[]{127};
            //            sequencer.addControllerEventListener(ml, eventsIWant);

            System.out.println("We got a sequencer");
////
//            for(int i =5; i < 161; i +=4)
//            {
//                track.add(makeEvent(144, 1, i, 100, i));
//                track.add(makeEvent(176,1,127,0,i));
//                track.add(makeEvent(128, 1, i, 100, i + 2));
//            }
////
//            sequencer.setSequence(sequence);
//            sequencer.start();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void buildTrackAndStart()
    {
        int[] trackList;
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        for( int i=0; i< 16; ++i )
        {
            trackList = new int[16];
            int key = instruments[i];
            for( int j=0; j < 16; ++j)
            {
                JCheckBox jc =  checkBoxList.get(j + (16*i));
                if( jc.isSelected())
                {
                    trackList[j] = key;
                }else
                {
                    trackList[j] = 0;
                }
            }

            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
        }

        track.add(makeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);

        }catch(Exception e ){e.printStackTrace();}
    }

    public class MyStartListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * .97));
        }
    }

    private void makeTracks(int[] list){
        for(int i =0; i < 16; ++i)
        {
            int key = list[i];
            if( key != 0 )
            {
                track.add(makeEvent(144,9, key, 100, i ));
                track.add(makeEvent(128,9, key, 100, i+1 ));
            }
        }
    }



//    public void play()
//    {
//        setUpGui();
//
//    }

//    JButton button;

//    @Override
//    public void controlChange(ShortMessage event) {
//        System.out.println("la");
//    }

//    class MyDrawPanel extends JPanel implements ControllerEventListener
//    {
//        boolean msg = false;
//        @Override
//        public void controlChange(ShortMessage event) {
//            msg = true;
//            repaint();
//        }
//
////        public void paintComponent(Graphics g)
////        {
////            if( msg )
////            {
////                Graphics2D g2 = (Graphics2D)g;
////                int r  = (int) (Math.random() * 250);
////                int gr  = (int) (Math.random() * 250);
////                int b  = (int) (Math.random() * 250);
////
////                g.setColor(new Color(r, gr, b));
////
////                int ht = (int) ((Math.random() * 120) + 10);
////                int width = (int) ((Math.random() * 120) + 10);
////                int x = (int) ((Math.random() * 40) + 10);
////                int y = (int) ((Math.random() * 40) + 10);
////                g.fillRect(x,y,ht, width);
////                msg = false;
////            }
////        }
//    }
//    static MyDrawPanel ml;
//    static JFrame frame = new JFrame("Midi music app");
//    public static void main1(String [] args) {
//
//        BeatBox mma = new BeatBox();
//        mma.play();
//        //mma.go();
//
//
////        MiniMiniMusicApp mini = new MiniMiniMusicApp();
////        mini.play();
//    }

//    void go() {
//        JFrame frame = new JFrame();
//        button = new JButton("click me");
//        button.addActionListener(this);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        frame.getContentPane().add(button);
//
//        frame.setSize(300, 300);
//
//        frame.setVisible(true);
//    }

//    public void setUpGui(){
//        ml = new MyDrawPanel();
//        frame.setContentPane(ml);
//        frame.setBounds(30,30,300,300);
//        frame.setVisible(true);
//    }


    private static MidiEvent makeEvent(int cmd, int chan, int one, int two, int tick)
    {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(cmd, chan, one, two);
            event = new MidiEvent(a, tick);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return event;
    }

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        button.setText("I’ve been clicked!");
//    }
}