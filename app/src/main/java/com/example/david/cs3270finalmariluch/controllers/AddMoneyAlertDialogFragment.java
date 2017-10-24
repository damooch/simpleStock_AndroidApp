package com.example.david.cs3270finalmariluch.controllers;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.example.david.cs3270finalmariluch.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMoneyAlertDialogFragment extends DialogFragment {

    public static AddMoneyAlertDialogFragment newInstance(String title, String message) {
        Log.d("test", "MyAlertDialogFragment newInstance()");
        AddMoneyAlertDialogFragment frag = new AddMoneyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("test", "MyAlertDialogFragment onCreateDialog()");
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        final long id = getArguments().getLong("id");
        return new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                MainActivity ma = (MainActivity) getActivity();
                                ma.selectDrawerItem(4, false);
                            }
                        }
                )
                .create();
    }
}
