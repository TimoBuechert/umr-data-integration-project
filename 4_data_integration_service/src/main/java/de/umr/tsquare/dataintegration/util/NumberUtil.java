package de.umr.tsquare.dataintegration.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtil {

    public static double parseDouble(final String doubleValue) throws ParseException {
        final NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        final Number number = format.parse(doubleValue);
        return number.doubleValue();
    }
    
}
