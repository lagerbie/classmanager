package thot.video.vlc.internal;

import java.util.Arrays;

import lombok.Getter;

/**
 * Event types.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public enum libvlc_event_e {

    libvlc_MediaPlayerNothingSpecial(0x101),
    libvlc_MediaPlayerOpening(0x102),
    libvlc_MediaPlayerBuffering(0x103),
    libvlc_MediaPlayerPlaying(0x104),
    libvlc_MediaPlayerPaused(0x105),
    libvlc_MediaPlayerStopped(0x106),
    libvlc_MediaPlayerForward(0x107),
    libvlc_MediaPlayerBackward(0x108),
    libvlc_MediaPlayerEndReached(0x109),
    libvlc_MediaPlayerEncounteredError(0x10A),
    libvlc_MediaPlayerTimeChanged(0x10B),
    libvlc_MediaPlayerPositionChanged(0x10C),
    libvlc_MediaPlayerSeekableChanged(0x10D),
    libvlc_MediaPlayerPausableChanged(0x10E);


    public static libvlc_event_e event(int vlcValue) {
        return Arrays.stream(libvlc_event_e.values()).filter(event -> event.getVlcValue() == vlcValue).findFirst()
                .orElse(null);
    }

    @Getter
    private int vlcValue;

    libvlc_event_e(int vlcValue) {
        this.vlcValue = vlcValue;
    }

}
