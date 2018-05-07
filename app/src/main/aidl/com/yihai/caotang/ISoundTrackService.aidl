// ISoundTrackService.aidl
package com.yihai.caotang;

// Declare any non-default types here with import statements

interface ISoundTrackService {
    void play(in String uri);

    void stop();

    void playBackground();

    void stopBackground();

    long duration();

    long position();

     boolean isPlayingSoundTrack();
}
