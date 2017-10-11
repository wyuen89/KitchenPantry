package wyuen.kitchen_pantry;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientDialog extends DialogFragment {

    public static interface OnCompleteListener{
        public abstract void onComplete(int id, String name, Double quantity, String unit);
    }

    private IngredientDialog.OnCompleteListener listener;

    public static IngredientDialog newInstance(){
        return new IngredientDialog();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            this.listener = (IngredientDialog.OnCompleteListener)getActivity();
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
            this.listener = (IngredientDialog.OnCompleteListener)getActivity();
        }
        catch(final ClassCastException e){
            throw new ClassCastException(getActivity().toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Dialog ret;
        KPDatabase db = new KPDatabase(getActivity().getApplicationContext(), DbHelper.getInstance(getActivity().getApplicationContext()).getReadableDatabase());
        KPAdapter ingredientAdapter = new KPAdapter(getActivity().getApplicationContext(), db.getAllIngredients());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.activity_ingredient_dialog, null);

        ArrayAdapter<String> unitAdapter;

        builder.setView(layout);

        final Spinner ingredientSpinner = (Spinner)layout.findViewById(R.id.ingredient_spinner);
        ingredientSpinner.setAdapter(ingredientAdapter);

        String[] arr = {"", "unit", "gram(s)", "cup(s)","mL"};
        List<String> units = new ArrayList<String>(Arrays.asList(arr));
        unitAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, units);


        final EditText quantityAmt = (EditText)layout.findViewById(R.id.quantity_amt);
        final Spinner unitSpinner = (Spinner)layout.findViewById(R.id.unit_spinner);
        unitSpinner.setAdapter(unitAdapter);

        //@TODO:set up views and adapters

        builder.setPositiveButton(R.string.add_ingredient, null);
        builder.setNegativeButton(R.string.Cancel, null);
        ret = builder.create();

        ret.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button addButton = ((AlertDialog)ret).getButton(ret.BUTTON_POSITIVE);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(quantityAmt.getText().toString().equals("")){
                            quantityAmt.setError("Amount Required");
                        }

                        else{
                            int position = ingredientSpinner.getSelectedItemPosition();
                            ItemInfo selected = (ItemInfo)ingredientSpinner.getItemAtPosition(position);

                            int id = selected.getId();
                            String unit = (String)unitSpinner.getSelectedItem();
                            String name = selected.getName();
                            Double quantity = Double.parseDouble(quantityAmt.getText().toString());
                            onAddIngredient(id, name, quantity, unit);
                            dialogInterface.dismiss();
                        }
                    }
                });
            }
        });

        return ret;
    }

    private void onAddIngredient(int id, String name, Double quantity, String unit){
        this.listener.onComplete(id, name, quantity, unit);
    }
}
