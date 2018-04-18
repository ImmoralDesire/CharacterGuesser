package me.alithernyx.bot.handlers;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.Guild;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Date;

public class AudioReceiver implements AudioReceiveHandler {

    public boolean record = false;
    public byte[] audio = new byte[] {};
    public Guild guild;

    public AudioReceiver(Guild guild) {
        this.guild = guild;
    }


    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public boolean canReceiveUser() {
        return false;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        try {
            if(this.record)
                audio = concat(audio, combinedAudio.getAudioData(1D));
            //audio = combinedAudio.getAudioData(100);
            //System.out.println(combinedAudio.getAudioData(100).length + " : " + combinedAudio.getUsers().toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
        //if(this.record)
        //  audio = concat(audio, userAudio.getAudioData(1D));
        //userAudio.getAudioData();
        //System.out.println(userAudio.getAudioData(100) + " : " + userAudio.getUser());
    }

    public void start() {
        this.record = true;
    }

    public void stop() {
        this.record = false;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(this.audio);
            File file = new File("src/main/resources/" + new Date().getTime() + ".wav");
            AudioSystem.write(new AudioInputStream(stream, OUTPUT_FORMAT, audio.length), AudioFileFormat.Type.WAVE, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);

        return result;
    }

    public byte[] get16BitPcm(short[] data) {
        byte[] resultData = new byte[2 * data.length];
        int iter = 0;
        for (double sample : data) {
            short maxSample = (short)((sample * Short.MAX_VALUE));
            resultData[iter++] = (byte)(maxSample & 0x00ff);
            resultData[iter++] = (byte)((maxSample & 0xff00) >>> 8);
        }
        return resultData;
    }

    public void rawToWave(File waveFile, byte[] data) throws IOException {

        waveFile.createNewFile();


        DataOutputStream output = null;//following block is converting raw to wav.
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            output.writeUTF("RIFF"); // chunk id
            output.writeInt(36 + data.length); // chunk size
            output.writeUTF("WAVE"); // format
            output.writeUTF("fmt "); // subchunk 1 id
            output.writeInt(16); // subchunk 1 size
            output.writeShort((short) 1); // audio format (1 = PCM)
            output.writeShort((short) 1); // number of channels
            output.writeInt(48000); // sample rate
            output.writeInt(48000 * 2); // byte rate
            output.writeShort((short) 2); // block align
            output.writeShort((short) 16); // bits per sample
            output.writeUTF("data"); // subchunk 2 id
            output.writeInt(data.length); // subchunk 2 size
            output.write(data);

        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    public void PCMtoFile(OutputStream os, byte[] pcmdata, int srate, int channel, int format) throws IOException {
        byte[] header = new byte[44];
        byte[] data = pcmdata;

        long totalDataLen = data.length + 36;
        long bitrate = srate * channel * format;

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = (byte) format;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channel;
        header[23] = 0;
        header[24] = (byte) (srate & 0xff);
        header[25] = (byte) ((srate >> 8) & 0xff);
        header[26] = (byte) ((srate >> 16) & 0xff);
        header[27] = (byte) ((srate >> 24) & 0xff);
        header[28] = (byte) ((bitrate / 8) & 0xff);
        header[29] = (byte) (((bitrate / 8) >> 8) & 0xff);
        header[30] = (byte) (((bitrate / 8) >> 16) & 0xff);
        header[31] = (byte) (((bitrate / 8) >> 24) & 0xff);
        header[32] = (byte) ((channel * format) / 8);
        header[33] = 0;
        header[34] = 16;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (data.length  & 0xff);
        header[41] = (byte) ((data.length >> 8) & 0xff);
        header[42] = (byte) ((data.length >> 16) & 0xff);
        header[43] = (byte) ((data.length >> 24) & 0xff);

        os.write(header, 0, 44);
        os.write(data);
        os.close();
    }

    public static short[] shortMe(byte[] bytes) {
        short[] out = new short[bytes.length / 2]; // will drop last byte if odd number
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getShort();
        }
        return out;
    }
}
