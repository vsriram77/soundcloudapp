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

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.app_name).setMessage("Please Enter Quantity")
                .setPositiveButton("OK", this).setNegativeButton("CANCEL", null).setView(editQuantity).create();

    }

    @Override
    public void onClick(DialogInterface dialog, int position) {

        String value = editQuantity.getText().toString();
        Log.d("Quantity: ", value);
        RecordActivityOld callingActivity = (RecordActivityOld) getActivity();
        callingActivity.onUserSelectValue(value);
        dialog.dismiss();
    }

}
