package activities.shoppingapp.com.shoppingapp.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import activities.shoppingapp.com.shoppingapp.R;

/**
 * Created by prerana_katyarmal on 2/17/2016.
 */
public class CommonUtils {


    private static final String KEY_FIRST_TIME_IN_APP= "is_app";
    private static SharedPreferences sharedpreferences;

    /**
     * Show Alert Dialog.
     * @param context Context
     * @param title String
     * @param message String
     */
    public static void showAlert(Context context, String title, String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }


    public static boolean getIsAppLaunchedFirstTime(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(KEY_FIRST_TIME_IN_APP, true);
    }

    public static void setAppLaunchedFirstTime(Context context, boolean appLunched) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(KEY_FIRST_TIME_IN_APP, appLunched);
        editor.apply();
    }
}
