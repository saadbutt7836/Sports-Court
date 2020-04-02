package com.canndecsolutions.garrisongamerss.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.canndecsolutions.garrisongamerss.R;


public class BookingConfirmation extends DialogFragment {

    private BookingConfirmationListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.booking_confirmation_layout, null);

        builder.setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.ConfirmClick(true);
                        Log.d("confirm", "done");
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.ConfirmClick(false);
                        Log.d("confirm", "cancel");
                    }
                });

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BookingConfirmationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Confirmation Listener");
        }

    }


    public interface BookingConfirmationListener {
        void ConfirmClick(Boolean confirm);
    }

}
