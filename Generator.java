public class Generator {
    // generator params
    static long k = 16807, m = Integer.MAX_VALUE, s0 = 1234;
    
    public static float getExp(float lambda) {
        // generate uniform RV
        float u = getUni();
        // generate exponential RV
        return (float) (-(1 / lambda) * Math.log(u));
    }
    
    public static float getUni() {
        // s_n = k * s_0 mod m
        long sn = (k * s0) % m;
        // r_n = s_n / m
        float rn = (float) sn / m;
        // iterate
        s0 = sn;
        return rn;
    }
}