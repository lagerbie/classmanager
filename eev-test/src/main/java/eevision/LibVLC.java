package eevision;

import com.sun.jna.Library;

/**
 * @author fabrice
 * @version 0.67
 */
public interface LibVLC extends Library {
    /**
     * Retrieve libvlc version. Example: "0.9.0-git Grishenko"
     *
     * @return a string containing the libvlc version
     */
    String libvlc_get_version();

}
