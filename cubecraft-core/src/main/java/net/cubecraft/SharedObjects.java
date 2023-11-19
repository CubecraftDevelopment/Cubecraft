package net.cubecraft;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public interface SharedObjects {
    DecimalFormat LONG_DECIMAL_FORMAT = new DecimalFormat("0.00000");
    DecimalFormat SHORT_DECIMAL_FORMAT = new DecimalFormat("0.000");
    DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
