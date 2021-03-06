package me.alithernyx.bot.utils;

import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AudioUtils {

    private File file = null;                       // the audio file
    private byte[] audio;                           // the audio data
    private AudioFormat format = null;              // audio format data

    // playback data used in methods play() and stop()
    private Clip audioClip = null;
    private AudioInputStream audioInputStream = null;

    /**
     * constructor, generates empty instance
     */
    public AudioUtils() {
        this.audio = new byte[0];                   // initialize an empty array
    }

    /**
     * constructor with AudioInputStream
     *
     * @param inputStream
     */
    public AudioUtils(AudioInputStream inputStream) {
        this.audio = convertAudioInputStream2ByteArray(inputStream);
        this.format = inputStream.getFormat();
    }

    /**
     * with this constructor all data is given explicitly
     *
     * @param audioData
     * @param format
     * @param file
     */
    public AudioUtils(byte[] audioData, AudioFormat format, File file) {
        this.audio = audioData;
        this.format = format;
        this.file = file;
    }

    /**
     * loads the audio file into an AudioInputStream
     *
     * @param file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public static AudioInputStream loadWavFileToAudioInputStream(File file) throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(file);
    }

    /**
     * convert an AudioInputStream to a byte array
     *
     * @param stream
     * @return
     */
    public static byte[] convertAudioInputStream2ByteArray(AudioInputStream stream) {
        byte[] array;
        try {
            array = new byte[(int) (stream.getFrameLength() * stream.getFormat().getFrameSize())];   // initialize the byte array with the length of the stream
            stream.read(array);         // write the stream's bytes into the byte array
        } catch (IOException e) {       // in case of an IOException
            e.printStackTrace();        // output error
            return new byte[0];         // return empty array
        }
        return array;
    }

    /**
     * convert a byte array (without audio file header, just pure audio data) into an AudioInputStream in a given AudioFormat
     *
     * @param array
     * @param format
     * @return
     */
    public static AudioInputStream convertByteArray2AudioInputStream(byte[] array, AudioFormat format) {
        ByteArrayInputStream bis = new ByteArrayInputStream(array);             // byte array to ByteArrayInputStream
        AudioInputStream ais = new AudioInputStream(bis, format, array.length); // byteArrayInputStream to AudioInputStream
        return ais;                                                             // return it
    }

    /**
     * check if there is data in the audio byte array
     *
     * @return
     */
    public boolean isEmpty() {
        return ((this.audio == null) || (this.audio.length == 0));
    }

    /**
     * a getter for the file
     *
     * @return
     */
    public File getFile() {
        return this.file;
    }

    /**
     * a setter for the file object
     *
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * a getter for the audio data
     *
     * @return
     */
    public byte[] getAudio() {
        return this.audio;
    }

    public AudioFormat getFormat() {
        return this.format;
    }

    /**
     * a getter for the sample rate
     *
     * @return
     */
    public float getSampleRate() {
        return this.format.getSampleRate();
    }

    /**
     * a getter for the sample size in bits
     *
     * @return
     */
    public int getSampleSizeInBits() {
        return this.format.getSampleSizeInBits();
    }

    /**
     * a getter for the frame size
     *
     * @return
     */
    public int getFrameSize() {
        return this.format.getFrameSize();
    }

    /**
     * a getter for the frame rate
     *
     * @return
     */
    public float getFrameRate() {
        return this.format.getFrameRate();
    }

    /**
     * a getter for the number of channels
     *
     * @return
     */
    public int getChannels() {
        return this.format.getChannels();
    }

    /**
     * a getter for the encoding
     *
     * @return
     */
    public AudioFormat.Encoding getEncoding() {
        return this.format.getEncoding();
    }

    /**
     * a getter for the bigEndian
     *
     * @return
     */
    public boolean isBigEndian() {
        return this.format.isBigEndian();
    }

    /**
     * is the audioClip playing?
     *
     * @return
     */
    public boolean isPlaying() {
        if (this.audioClip == null) return false;
        return this.audioClip.isRunning();
    }

    public Clip getAudioClip() {
        return this.audioClip;
    }

    /**
     * (over-)write the file with the data in audioStream
     *
     * @return true if success, false if an error occurred
     */
    public boolean writeAudio() {
        return this.writeAudio(this.file);
    }

    /**
     * writes the audioStream into a file
     *
     * @param filename
     * @return true if success, false if an error occurred
     */
    public boolean writeAudio(String filename) {
        File file = new File(filename);     // create the file with this filename
        file.getParentFile().mkdirs();                              // ensure that the directory exists
        return this.writeAudio(file);              // write into it
    }

    /**
     * write the audio data to the file system
     *
     * @param file
     * @return true if success, false if an error occurred
     */
    public boolean writeAudio(File file) {
        if (file == null) {                                                 // if no valid file
            System.err.println("No file specified to write audio data.");   // print error message
            return false;                                                         // cancel
        }

        if (this.file == null)                                              // if no file has been specified, yet
            this.file = file;                                               // take this

        try {
            file.createNewFile();                                           // create the file if it does not already exist
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            return false;
        }
        AudioInputStream ais = convertByteArray2AudioInputStream(this.audio, this.format);  // convert the audio byte array to an AudioInputStream
        try {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);                            // write to file system
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * start playing back the audio data
     *
     * @throws LineUnavailableException
     * @throws IOException
     */
    public void play() throws LineUnavailableException, IOException {
        if (this.isEmpty()) return;
        this.stop();

        DataLine.Info info = new DataLine.Info(Clip.class, this.format);
        this.audioClip = (Clip) AudioSystem.getLine(info);
        this.audioInputStream = convertByteArray2AudioInputStream(this.audio, this.format);
        this.audioClip.open(this.audioInputStream);
        this.audioClip.start();
    }

    /**
     * stop audio playback
     */
    public void stop() {
        if (this.audioClip != null) {
            if (this.audioClip.isRunning()) this.audioClip.stop();
            if (this.audioClip.isOpen()) this.audioClip.close();
            this.audioClip = null;
        }

        if (this.audioInputStream != null) {
            try {
                this.audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.audioInputStream = null;
        }
    }
}