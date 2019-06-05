package com.alvaroruiz.tenisspfc;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by alvaroruiz on 14/03/18.
 */

public class DialogBox {
    public boolean dialogBox(String msg, String okButton, String cancelButton, final Context activity) {
        Dialog v = null;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(msg);
        if (okButton != "") {
            alertDialogBuilder.setPositiveButton(okButton,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) { /**/ }
                    });
        }
        if (cancelButton != "") {
            alertDialogBuilder.setNegativeButton(cancelButton,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) { /**/ }
                    });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return true;
    }
}
