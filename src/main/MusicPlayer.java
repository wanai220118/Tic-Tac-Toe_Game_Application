package main;

import javax.sound.sampled.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MusicPlayer {
    private Clip clip;
    private boolean isPlaying = false;

    public void playMusic(String fileName, boolean loop) {
        try {
        	URL soundURL = getClass().getResource("/" + fileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(audioIn);

            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            isPlaying = true;
        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            isPlaying = false;
        }
    }

    public void resumeMusic() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
            isPlaying = true;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
    
    public void playOneShot(String filename) {
        try {
        	InputStream audioSrc = getClass().getResourceAsStream("/" + filename);
            if (audioSrc == null) throw new FileNotFoundException("Could not find: " + filename);

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}