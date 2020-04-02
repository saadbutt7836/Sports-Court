package com.canndecsolutions.garrisongamerss.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.canndecsolutions.garrisongamerss.R;


public class BookingComment extends DialogFragment {
    private EditText Cast_Sports_Message;
    private AddCommentListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_comment_layout, null);

        Cast_Sports_Message = (EditText) view.findViewById(R.id.Sports_Message);

        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cast_Sports_Message.setText(null);
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = Cast_Sports_Message.getText().toString().trim();
                        listener.SendText(message);
                    }
                });

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddCommentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Add Comment Listeners");
        }
    }

    //    INTERFACE
    public interface AddCommentListener {
        void SendText(String message);
    }
}
