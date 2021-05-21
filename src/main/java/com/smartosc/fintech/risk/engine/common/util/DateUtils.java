package com.smartosc.fintech.risk.engine.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
    public static Date parseToDate(String value) {
        try {
            return org.drools.core.util.DateUtils.parseDate(value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isParsable(String value) {
        Date date = parseToDate(value);
        return Objects.nonNull(date);
    }

    public static Long parseToMillisecond(String value) {
        Date date = parseToDate(value);
        if (Objects.isNull(date)) {
            return null;
        }

        return date.getTime();
    }

    public static String formatDateTime(long value) {
        Date date = new Date(value);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return String.format("%s %d:%d:%d.%d", org.drools.core.util.DateUtils.format(date),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
    }
}
