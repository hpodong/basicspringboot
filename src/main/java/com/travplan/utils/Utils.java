package com.travplan.utils;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {

    public static String replaceFileThumbnailUrl(int width, int height) {
        return "REPLACE(f_url, f_enc_name, CONCAT(f_enc_name, '_"+width+"x"+height+"')) f_url";
    }

    public static String stripHtmlTags(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
    }

    public static String formatTimestamp(Timestamp value) {
        return formatTimestamp(value, "yyyy-MM-dd");
    }

    public static String formatTimestamp(Timestamp value, String pattern) {
        if(value == null) return null;
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(value);
    }

    public static String priceToString(Integer price) {
        return priceToString(Long.parseLong(String.valueOf(price)));
    }

    public static String priceToString(Long price) {
        if(price == null || price == 0) {
            return "0원";
        } else {
            final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
            return numberFormat.format(price) + "원";
        }
    }
}
