package thot.video.vlc.internal;

import java.util.Arrays;

import lombok.Getter;
import thot.video.MediaPlayerState;

/**
 * Note the order of libvlc_state_t enum must match exactly the order of \see mediacontrol_PlayerStatus, \see
 * input_state_e enums, and VideoLAN.LibVLC.State (at bindings/cil/src/media.cs).
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 1.1.0 à 3.0.x et compatible JET)
 */
public enum libvlc_state_t {

    libvlc_NothingSpecial(MediaPlayerState.IDLE_CLOSE, 0),
    libvlc_Opening(MediaPlayerState.OPENING, 1),
    libvlc_Buffering(MediaPlayerState.BUFFERING, 2),
    libvlc_Playing(MediaPlayerState.PLAYING, 3),
    libvlc_Paused(MediaPlayerState.PAUSED, 4),
    libvlc_Stopped(MediaPlayerState.STOPPING, 5),
    libvlc_Ended(MediaPlayerState.ENDED, 6),
    libvlc_Error(MediaPlayerState.ERROR, 7);

    /**
     * Retourne l'état suivant la valeur de l'état de VLC.
     *
     * @param vlcValue la valeur de létat dans VLC.
     *
     * @return l'état du media player.
     */
    public static MediaPlayerState getState(int vlcValue) {
        return Arrays.stream(libvlc_state_t.values()).filter(state -> state.getVlcValue() == vlcValue)
                .map(libvlc_state_t::getState).findFirst().orElse(null);
    }

    @Getter
    private int vlcValue;

    @Getter
    private MediaPlayerState state;

    libvlc_state_t(MediaPlayerState state, int vlcValue) {
        this.vlcValue = vlcValue;
        this.state = state;
    }

}
