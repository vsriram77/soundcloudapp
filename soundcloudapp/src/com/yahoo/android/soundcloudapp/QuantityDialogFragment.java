package com.yahoo.android.soundcloudapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

public class QuantityDialogFragment extends DialogFragment implements OnClickListener {

    private EditText editQuantity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editQuantity = new EditText(getActivity());
        editQuantity.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.app_name).setMessage("Give your audio clip a name")
                .setPositiveButton("Save", this).setNegativeButton("Discard", null).setView(editQuantity).create();

    }

    @Override
    public void onClick(DialogInterface dialog, int position) {

        String value = editQuantity.getText().toString();
        Log.d("DDDDD", "*********** ClipName: " + value);
        Log.d("DDDDD", "*********** Position: " + String.valueOf(position));
        RecordActivity callingActivity = (RecordActivity) getActivity();
        callingActivity.onNamePicked(value);
        dialog.dismiss();
    }

}
