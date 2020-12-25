package lab.galaxy.dlfunc;

public class JNIHelper {
    public static native int JNICall_32(int func, int arg1, int arg2);

    public static native long JNICall_64(long func, long arg1, long arg2);
}
