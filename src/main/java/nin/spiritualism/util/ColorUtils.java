package nin.spiritualism.util;

public class ColorUtils {

    public static int blend(int base, int up, float a) {
        int bB = base & 0xFF;
        int bG = (base >> 8) & 0xFF;
        int bR = (base >> 16) & 0xFF;
        int uB = up & 0xFF;
        int uG = (up >> 8) & 0xFF;
        int uR = (up >> 16) & 0xFF;
        int nB = (int) (bB * (1 - a) + uB * a);
        int nG = (int) (bG * (1 - a) + uG * a);
        int nR = (int) (bR * (1 - a) + uR * a);
        return (nR << 16) + (nG << 8) + nB;
    }
}
