package wyuen.kitchen_pantry;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class InstructionDialog extends DialogFragment {

    public static interface OnCompleteListener{
        public abstract void onComplete(String instruction);
    }

    private OnCompleteListener listener;

    public static InstructionDialog newInstance(){
        return new InstructionDialog();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            this.listener = (OnCompleteListener)getActivity();
        }
        catch(final ClassCastException e){
            throw new ClassCastException(getActivity().toString() + " must implement OnCompleteListener");
        }
    }

    @SuppressWarnings("depracation")
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            this.listener = (OnCompleteListener)getActivity();
        }
        catch(final ClassCastException e){
            throw new ClassCastException(getActivity().toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.activity_instruction_dialog, null);
        builder.setView(layout);

        final EditText editText = (EditText)layout.findViewById(R.id.instruction_text);

        builder.setPositiveButton(R.string.add_step, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onAddStep(editText.getText().toString());
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

    private void onAddStep(String s){
        this.listener.onComplete(s);
    }
}
