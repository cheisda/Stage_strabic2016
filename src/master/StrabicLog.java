package master;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Cheisda on 20/08/2016.
 */
public class StrabicLog {
    //private final static Logger logger = Logger.getLogger(StrabicLog.class.getName());
    private static FileHandler fh = null;

    public static void init(){
        try {
            fh=new FileHandler("loggerStrabic.log", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger log = Logger.getLogger("");
        fh.setFormatter(new SimpleFormatter());
        log.addHandler(fh);
        log.setLevel(Level.CONFIG);
    }

}
