package net.programmierecke.radiodroid2.players.mediaplayer;

import net.programmierecke.radiodroid2.data.ShoutcastInfo;
import net.programmierecke.radiodroid2.data.StreamLiveInfo;

interface StreamProxyEventReceiver {
    void foundShoutcastStream(ShoutcastInfo bitrate, boolean isHls);
    void foundLiveStreamInfo(StreamLiveInfo liveInfo);
    void streamCreated(String proxyConnection);
    void streamStopped();
    void onBytesRead(byte[] buffer, int offset, int length);
}
