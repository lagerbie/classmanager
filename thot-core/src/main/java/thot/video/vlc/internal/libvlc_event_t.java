package thot.video.vlc.internal;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.Getter;

/**
 * A LibVLC event.
 *
 * @author Fabrice Alleau
 * @version 1.8.4 (VLC 0.9.x à 3.0.x et compatible JET)
 */
public class libvlc_event_t extends Structure {

    /**
     * Event type (see ref libvlc_event_e).
     */
    @Getter
    public int type;
    /**
     * Object emitting the event.
     */
    public Pointer p_obj;
    /**
     * Type-dependent event description.
     */
    public Pointer event_type_specific;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("type", "p_obj", "event_type_specific");
    }

}
