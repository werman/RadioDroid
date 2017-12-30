package net.programmierecke.radiodroid2.players.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.Log;

import net.programmierecke.radiodroid2.BuildConfig;
import net.programmierecke.radiodroid2.R;
import net.programmierecke.radiodroid2.data.ShoutcastInfo;
import net.programmierecke.radiodroid2.data.StreamLiveInfo;
import net.programmierecke.radiodroid2.players.PlayerWrapper;
import net.programmierecke.radiodroid2.players.RadioPlayer;
import net.programmierecke.radiodroid2.recording.RecordableListener;

import java.io.IOException;

public class MediaPlayerWrapper implements PlayerWrapper, StreamProxyEventReceiver {

    final private String TAG = "MediaPlayerWrapper";

    private MediaPlayer mediaPlayer;
    private StreamProxy proxy;
    private PlayListener stateListener;

    private String streamUrl;
    private Context context;
    private boolean isAlarm;

    private long totalTransferredBytes;
    private long currentPlaybackTransferredBytes;

    @Override
    public void playRemote(String streamUrl, Context context, boolean isAlarm) {
        Log.v(TAG, "Stream url:" + streamUrl);

        currentPlaybackTransferredBytes = 0;

        this.streamUrl = streamUrl;
        this.context = context;
        this.isAlarm = isAlarm;

        if (proxy != null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "stopping old proxy.");
            stopProxy();
        }

        proxy = new StreamProxy(streamUrl, MediaPlayerWrapper.this);
    }

    private void playProxyStream(String proxyUrl, Context context, boolean isAlarm) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setAudioStreamType(isAlarm ? AudioManager.STREAM_ALARM : AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(proxyUrl);
            mediaPlayer.prepare();
            stateListener.onStateChanged(RadioPlayer.PlayState.PrePlaying);
            mediaPlayer.start();
            stateListener.onStateChanged(RadioPlayer.PlayState.Playing);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "" + e);
            stateListener.onPlayerError(R.string.error_stream_url);
        } catch (IOException e) {
            Log.e(TAG, "" + e);
            stateListener.onPlayerError(R.string.error_caching_stream);
        } catch (Exception e) {
            Log.e(TAG, "" + e);
            stateListener.onPlayerError(R.string.error_play_stream);
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        stopProxy();
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        stopProxy();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public long getBufferedMs() {
        return -1;
    }

    @Override
    public int getAudioSessionId() {
        if (mediaPlayer != null) {
            return mediaPlayer.getAudioSessionId();
        }
        return 0;
    }

    @Override
    public long getTotalTransferredBytes() {
        return totalTransferredBytes;
    }

    @Override
    public long getCurrentPlaybackTransferredBytes() {
        return currentPlaybackTransferredBytes;
    }

    @Override
    public void setVolume(float newVolume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(newVolume, newVolume);
        }
    }

    @Override
    public void setStateListener(PlayListener listener) {
        stateListener = listener;
    }

    @Override
    public void startRecording(@NonNull RecordableListener recordableListener) {
        proxy.startRecording(recordableListener);
    }

    @Override
    public void stopRecording() {
        proxy.stopRecording();
    }

    @Override
    public boolean isRecording() {
        return proxy.isRecording();
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getExtension() {
        return proxy.getExtension();
    }

    @Override
    public void foundShoutcastStream(ShoutcastInfo shoutcastInfo, boolean isHls) {
        stateListener.onDataSourceShoutcastInfo(shoutcastInfo, isHls);
    }

    @Override
    public void foundLiveStreamInfo(StreamLiveInfo liveInfo) {
        stateListener.onDataSourceStreamLiveInfo(liveInfo);
    }

    @Override
    public void streamCreated(String proxyConnection) {
        playProxyStream(proxyConnection, context, isAlarm);
    }

    @Override
    public void streamStopped() {
        stop();
        playRemote(streamUrl, context, isAlarm);
    }

    @Override
    public void onBytesRead(byte[] buffer, int offset, int length) {
        totalTransferredBytes += length;
        currentPlaybackTransferredBytes += length;
    }

    private void stopProxy() {
        if (proxy != null) {
            try {
                proxy.stop();
            } catch (Exception e) {
                Log.e(TAG, "proxy stop exception: ", e);
            }
            proxy = null;
        }
    }
}
