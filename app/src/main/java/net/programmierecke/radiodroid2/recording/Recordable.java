package net.programmierecke.radiodroid2.recording;

import android.support.annotation.NonNull;

public interface Recordable {

    void startRecording(@NonNull RecordableListener recordableListener);

    void stopRecording();

    boolean isRecording();

    String getTitle();
    String getExtension();
}
