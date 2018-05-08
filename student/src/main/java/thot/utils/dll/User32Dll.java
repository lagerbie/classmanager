package thot.utils.dll;

import com.sun.jna.win32.StdCallLibrary;

/**
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface User32Dll extends StdCallLibrary {

    /**
     * The BlockInput function blocks keyboard and mouse input events from reaching applications.
     *
     * @param bvalid Specifies the function's purpose. If this parameter is TRUE, keyboard and mouse input
     *         events are blocked. If this parameter is FALSE, keyboard and mouse events are unblocked. Note that only
     *         the thread that blocked input can successfully unblock input.
     *
     * @return If the function succeeds, the return value is nonzero. If input is already blocked, the return value is
     *         zero.
     */
    boolean BlockInput(boolean bvalid);
}
