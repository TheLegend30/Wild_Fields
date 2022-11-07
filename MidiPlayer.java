import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

public class MidiPlayer {
    private Sequencer sequencer;
    private InputStream is;
    private int number = 1;

    public MidiPlayer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            is = new BufferedInputStream(new FileInputStream(new File(String.format("files/music/%s.mid", number))));
            sequencer.setSequence(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void start() {
        sequencer.start();
    }

    public void stop() {
        sequencer.stop();
    }

    private void setSong(int number) {
        try {
            is = new BufferedInputStream(new FileInputStream(new File(String.format("files/music/%s.mid", number))));
            sequencer.setSequence(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void previousSong() {
        this.number = this.number - 1;
        if (this.number == 0) {
            this.number = new File("files/music/").list().length;
        }
        setSong(this.number);
    }

    public void nextSong() {
        this.number = this.number + 1;
        if (this.number > new File("files/music/").list().length) {
            this.number = 1;
        }
        setSong(this.number);
    }

    public void checkIfRunning() {
        if (sequencer.getMicrosecondPosition() >= sequencer.getMicrosecondLength()) {
            nextSong();
            start();
        }
    }
}