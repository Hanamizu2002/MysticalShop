package com.mysticalkingdoms.mysticalshop.utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class FormatUtils {


    public static String formatTime(long time) {
        if (time <= 0) {
            return "0s";
        }

        final StringBuilder builder = new StringBuilder();

        Duration duration = Duration.of(time, ChronoUnit.SECONDS);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        final long weeks = days / 7;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        days %= 7;

        if (seconds > 0) {
            builder.insert(0, seconds + "s");
        }

        if (minutes > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }

            builder.insert(0, minutes + "m");
        }

        if (hours > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }

            builder.insert(0, hours + "h");
        }

        if (days > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }

            builder.insert(0, days + "d");
        }

        if (weeks > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }

            builder.insert(0, weeks + "w");
        }

        return builder.toString();
    }
}
