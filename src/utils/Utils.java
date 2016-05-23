package utils;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils
 * @author Loann Neveu
 */
public class Utils {

    /**
     * Iterable to list
     * @param iterable
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(Iterable<T> iterable) {
        if(iterable instanceof List) {
            return (List<T>) iterable;
        }
        ArrayList<T> list = new ArrayList<T>();
        if(iterable != null) {
            for(T e: iterable) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Read file to string
     * @param path
     * @param encoding
     * @retur
     * @throws IOException
     */
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.toByteArray(new File(path));
        return new String(encoded, encoding);
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}
