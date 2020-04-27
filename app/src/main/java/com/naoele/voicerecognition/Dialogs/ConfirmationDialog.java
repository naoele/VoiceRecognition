package com.naoele.voicerecognition.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class ConfirmationDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_LISTENER_OK = "ok";
    private static final String ARG_LISTENER_CANCEL = "cancel";

    public static ConfirmationDialog newInstance(String message) {
        ConfirmationDialog dialog = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);
        return dialog;
    }

    public static ConfirmationDialog newInstance(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        ConfirmationDialog dialog = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);

        setListenerOk(okListener);
        setListenerCancel(okListener);

        return dialog;
    }

    public static void show(String message, FragmentManager manager, String tag) {
        ConfirmationDialog.newInstance(message).show(manager, tag);
    }

    public static void show(String message, FragmentManager manager, String tag, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        ConfirmationDialog.newInstance(message, okListener, cancelListener).show(manager, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(getArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok, _listener_ok)
                .setNegativeButton(android.R.string.cancel, _listener_cancel)
                .create();
    }

    private static DialogInterface.OnClickListener _listener_ok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    private static DialogInterface.OnClickListener _listener_cancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public static void setListenerOk(DialogInterface.OnClickListener listenerOk) {
        ConfirmationDialog._listener_ok = listenerOk;
    }

    public static void setListenerCancel(DialogInterface.OnClickListener listenerCancel) {
        ConfirmationDialog._listener_cancel = listenerCancel;
    }
}
