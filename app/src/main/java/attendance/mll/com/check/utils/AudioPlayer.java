package attendance.mll.com.check.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by danger
 * 2016/07/12
 */
public abstract class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static HashMap<String, Integer> durations = new HashMap<>();
    private MediaPlayer mediaPlayer;

    public AudioPlayer() {

    }

    /**
     * 获取音频文件的时长,单位毫秒
     *
     * @param path
     * @return
     */
    public static int getLength(String path) {
        int len = -1;
        if (durations.containsKey(path)) {
            return durations.get(path);
        }
        try {

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();

            len = mediaPlayer.getDuration();
            durations.put(path, len);
            mediaPlayer.release();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return len;
    }

    public void playAsset(Context context, String path) {
        stop();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd(path);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            onError(null, -1, -1);
        } catch (Exception e) {
            e.printStackTrace();
            onError(null, -1, -1);
        }
    }

    public void startPlay(String path) {
        /*try {*/
        stop();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        } catch (IOException e) {
            onError(null, -1, -1);
        } catch (Exception e) {
            e.printStackTrace();
            onError(null, -1, -1);
        }
    }

    public void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mediaPlayer = null;
    }

    @Override
    public abstract void onCompletion(MediaPlayer mp);

    @Override
    public abstract boolean onError(MediaPlayer mp, int what, int extra);

    public boolean isPlaying() {
        return false;

    }
}