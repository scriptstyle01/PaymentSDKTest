package ottu.payment.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import ottu.payment.R;

public class Util {

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    public static SparseArray<Pattern> listCardPatter() {
        SparseArray<Pattern> mCCPatterns = new SparseArray<>();
        // With spaces for credit card masking
        mCCPatterns.put(R.drawable.icon_visa, Pattern.compile("^4[0-9]{2,12}(?:[0-9]{3})?$"));//visa
        mCCPatterns.put(R.drawable.icon_mastercard, Pattern.compile("^5[1-5][0-9]{1,14}$"));//master
        mCCPatterns.put(R.drawable.icon_amex, Pattern.compile("^3[47][0-9]{1,13}$")); //Amex
        mCCPatterns.put(R.drawable.icon_diner, Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{11}$"));//dinner
        mCCPatterns.put(R.drawable.card_knet, Pattern.compile("^^6(?:011|5[0-9]{2})[0-9]{12}$")); //discover
        mCCPatterns.put(R.drawable.icon_jcb, Pattern.compile("^(?:2131|1800|35\\\\d{3})\\\\d{11}$")); //jcb
        mCCPatterns.put(R.drawable.icon_maestro, Pattern.compile("^(5018|5020|5038|5612|5893|6304|6759|6761|6762|6763|0604|6390)\\d+$")); //mastro
        mCCPatterns.put(R.drawable.icon_rupay, Pattern.compile("^6(?!011)(?:0[0-9]{14}|52[12][0-9]{12})$")); //rupay
        mCCPatterns.put(R.drawable.icon_unionpay, Pattern.compile("^(62|88)\\d+$")); //unionpay

        return mCCPatterns;
    }

    public static ArrayList<String> listCardName() {
        //List of card Name same in listCardPatter() List above
        ArrayList<String> mCCPatterns = new ArrayList<>();
        // With spaces for credit card masking
        mCCPatterns.add("VISA");
        mCCPatterns.add("MASTERCARD");
        mCCPatterns.add("AMERICANEXPRESS");
        mCCPatterns.add("DINNER");
        mCCPatterns.add("DISCOVER");
        mCCPatterns.add("JCB");
        mCCPatterns.add("MAESTRO");
        mCCPatterns.add("RUPAY");
        mCCPatterns.add("UNIONPAY");

        return mCCPatterns;
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    public static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    public static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    public static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

}
