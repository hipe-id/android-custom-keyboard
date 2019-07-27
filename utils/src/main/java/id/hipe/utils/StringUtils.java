package id.hipe.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.TypedValue;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import timber.log.Timber;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 24/05/18.
 */
public class StringUtils {
    static final String HEXES = "0123456789abcdef";

    /**
     * formatting Currency idr
     */
    public static String decimalFormatter(double data) {
        DecimalFormat formattter = new DecimalFormat("#,###,###,###");
        return formattter.format(data);
    }

    public static String decimalFormatterTwoDigit(double data) {
        DecimalFormat format = new DecimalFormat("#.00");
        return format.format(data);
    }

    public static String decimalFormatterZeroDigit(double data) {
        DecimalFormat format = new DecimalFormat("#");
        return format.format(data);
    }

    public static String getAppNameVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("getAppNameVersion() :  %s", e.getLocalizedMessage());
        }

        return result;
    }

    public static float getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * get TimeStamp
     */
    public static String getCurrentTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }

    public static DateTime convertDateStringToJodaTime(String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.parseDateTime(date);
    }

    public static Long convertDateStringToTimeStamp(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            long time = calendar.getTimeInMillis();
            long curr = System.currentTimeMillis();
            long diff = curr - time;    //Time difference in milliseconds
            return diff;
        } catch (ParseException e) {
            Timber.e("error : " + e.getMessage());
        }
        return null;
    }

    /**
     * URL Encoder
     */
    public static String encodeString(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Timber.d("debug", "encodeString : " + e.getMessage());
        }
        return null;
    }

    public static String hmacSha1(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            // byte[] hexBytes = Base64Coder.encode(rawHmac);

            //  Covert array of Hex bytes to a String
            return byteArrayToHexString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String byteArrayToHexString(byte in[]) {

        byte ch = 0x00;
        int i = 0;
        if (in == null || in.length <= 0) return null;

        String pseudo[] = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        };
        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0); // Strip offhigh nibble
            ch = (byte) (ch >>> 4);
            // shift the bits down
            ch = (byte) (ch & 0x0F);
            // must do this is high order bit is on!
            out.append(pseudo[ch]); // convert thenibble to a String
            // Character
            ch = (byte) (in[i] & 0x0F); // Strip off low nibble
            out.append(pseudo[ch]); // convert the nibble to a String
            // Character
            i++;
        }
        String rslt = new String(out);
        return rslt;
    }

    public static String SHA256(String text) throws NoSuchAlgorithmException {
        Timber.d("SHA256(): " + text);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes(Charset.forName("UTF-8")));
        return getHex(md.digest());
    }

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String transformGsonToString(Object object) {
        String json = new Gson().toJson(object);
        Timber.d("transformGsonToString() :  %s", json);
        return json;
    }

    public static boolean checkNumber(String jmlPenarikan) {
        String regex = "[0-9]+";
        Timber.d("checkNumber %s", jmlPenarikan);
        return (jmlPenarikan.matches(regex)) ? true : false;
    }

    public static int getDp(Context context, int h) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, h,
                context.getResources().getDisplayMetrics());
    }
}