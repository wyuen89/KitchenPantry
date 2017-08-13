package wyuen.kitchen_pantry;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.Map;

public class FilterIngredientActivity extends AppCompatActivity {
    KPDatabase db;
    Map<String, ?> filter;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_ingredient);

        SharedPreferences shared = getSharedPreferences("ingredient_filter", MODE_PRIVATE);
        filter = shared.getAll();
        editor = shared.edit();
        db = new KPDatabase(this, DbHelper.getInstance(this).getReadableDatabase());
        Spinner spinner = (Spinner)findViewById(R.id.type_spinner);
        KPSpinnerAdapter adapter = new KPSpinnerAdapter(this, db.getTypes());

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                KPSpinnerAdapter spinnerAdapter = (KPSpinnerAdapter)adapterView.getAdapter();
                editor.putString(db.COLUMN_TYPE, (String)spinnerAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                editor.remove(db.COLUMN_TYPE);
            }
        });

        for(String s : filter.keySet()){
            setView(s, (String)filter.get(s));
        }

    }

    public void onCheckBoxClicked(View view){
        boolean checked = ((CheckBox)view).isChecked();
        ViewGroup viewGroup;

        switch(view.getId()){
            case R.id.type_check:
                //Parent of Parent used since spinner is in top view
                viewGroup = (ViewGroup)view.getParent().getParent();
                View spinner = getSiblingView(viewGroup, R.id.type_spinner);

                if(checked){
                    Log.d("FilterIngrActivity", Boolean.toString(checked));
                    spinner.setVisibility(View.VISIBLE);
                }

                else{
                    spinner.setVisibility(View.GONE);
                    editor.remove(db.COLUMN_TYPE);
                }

        }
    }

    public void onButtonClicked(View view){
        switch(view.getId()){
            case R.id.filter_button:

                if(editor.commit()){
                    setResult(1);
                }

                else{
                    setResult(0);
                }

                finish();
        }
    }

    private void setView(String name, String value){

        if(name.equals("Type")){
            Spinner spinner = (Spinner)findViewById(R.id.type_spinner);
            KPSpinnerAdapter adapter = (KPSpinnerAdapter)spinner.getAdapter();
            CheckBox check = (CheckBox)findViewById(R.id.type_check);

            check.setChecked(true);

            spinner.setSelection(adapter.getPosition(value));
            spinner.setVisibility(View.VISIBLE);
        }
    }

    private View getSiblingView(ViewGroup group, int id){
        Log.d("FilterIngrActivity", Integer.toString(group.getChildCount()));

        for(int i = 0; i < group.getChildCount(); i++){
            View view = group.getChildAt(i);

            if(view.getId() == id){
                return view;
            }
        }

        return null;
    }
}
