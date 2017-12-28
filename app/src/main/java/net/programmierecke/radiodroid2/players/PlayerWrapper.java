package net.programmierecke.radiodroid2.players;

import android.content.Context;

import net.programmierecke.radiodroid2.data.ShoutcastInfo;
import net.programmierecke.radiodroid2.data.StreamLiveInfo;
import net.programmierecke.radiodroid2.recording.Recordable;

public interface PlayerWrapper extends Recordable {
    interface PlayListener {
        void onStateChanged(RadioPlayer.PlayState state);

        void onPlayerError(final int messageId);

        void onDataSourceShoutcastInfo(ShoutcastInfo shoutcastInfo, boolean isHls);

        void onDataSourceStreamLiveInfo(StreamLiveInfo liveInfo);
    }

    void playRemote(String streamUrl, Context context, boolean isAlarm);

    void pause();

    void stop();

    boolean isPlaying();

    long getBufferedMs();

    int getAudioSessionId();

    void setVolume(float newVolume);

    void setStateListener(PlayListener listener);
}
