package eevision;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface LibFileVersion extends Library {

    int GetFileVersionInfoSizeA(String fileName, Pointer lpdwHandle);

    int GetFileVersionInfoA(String fileName, int dwHandle, int dwLen, byte[] lpData);

    int VerQueryValueA(byte[] pBlock, String subBlock, PointerByReference lplpBuffer, IntByReference len);

}
