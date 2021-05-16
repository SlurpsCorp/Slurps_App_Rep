package fr.autruche.slurpsV2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogBox extends DialogFragment {

    public String CHOICE;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceStates){

        String [] choiceTab = getActivity().getResources().getStringArray(R.array.photoChoix);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(choiceTab, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CHOICE = choiceTab[which];
            }
        });
        return builder.create();
    }

    //new FragmentDialogBox().show(getSupportFragmentManager(),"fragDialog");
}
