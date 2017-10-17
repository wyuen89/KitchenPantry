package wyuen.kitchen_pantry;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FilterRecipeActivity extends AppCompatActivity {
    KPDatabase db;
    Map<String, ?> filter;
    SharedPreferences.Editor editor;
    List<ItemInfo> ingredients;
    boolean[] checkedParam;
    String[] ingredientArray;
    HashSet<String> inFilter;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_recipe);

        SharedPreferences shared = getSharedPreferences("recipe_filter", MODE_PRIVATE);
        filter = shared.getAll();
        editor = shared.edit();
        db = new KPDatabase(this, DbHelper.getInstance(this).getReadableDatabase());
        ingredients = db.getAllIngredients();
        checkedParam = new boolean[ingredients.size()];
        ingredientArray = new String[ingredients.size()];
        inFilter = new HashSet<String>();
        List<String> cuisineList = db.getAllCuisine();
        Spinner cuisineSpinner = (Spinner)findViewById(R.id.cuisine_spinner);
        Spinner ingredientSpinner = (Spinner)findViewById(R.id.filter_ingredient_spinner);
        KPAdapter ingredientAdapter = new KPAdapter(this, db.getAllIngredients());
        ArrayAdapter<String> cuisineAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cuisineList);

        Log.d(this.getClass().getName(), Integer.toString(db.getMaxTime()));

        cuisineSpinner.setAdapter(cuisineAdapter);
        ingredientSpinner.setAdapter(ingredientAdapter);

        cuisineSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>)adapterView.getAdapter();
                editor.putString(db.COLUMN_CUISINE, spinnerAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                editor.remove(db.COLUMN_CUISINE);
            }
        });

        ingredientSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                KPAdapter spinnerAdapter = (KPAdapter)adapterView.getAdapter();
                editor.putString(db.COLUMN_INGREDIENTID, Long.toString(spinnerAdapter.getItemId(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                editor.remove(db.COLUMN_INGREDIENTID);
            }
        });

        int max = db.getMaxTime();
        TextView tv = (TextView) findViewById(R.id.max_time);
        tv.setText(Integer.toString(max));

        SeekBar sb = (SeekBar)findViewById(R.id.time_seekbar);
        sb.setMax(max);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView displayText = (TextView)findViewById(R.id.minutes_text);

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                displayText.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                Log.d(this.getClass().getName(), Integer.toString(progress));
                editor.putString(db.TOTAL_TIME, Integer.toString(progress));
            }
        });

        String ingrList = (String) filter.get(db.INGREDIENT_LIST);

        if(ingrList != null){
            String[] ingredientArray = ingrList.split(",");
            inFilter.addAll(Arrays.asList(ingredientArray));
        }

        initIngredientList();

        for(String s : filter.keySet()){
            setView(s, (String)filter.get(s));
        }
    }

    public void onCheckBoxClicked(View view){
        boolean checked = ((CheckBox)view).isChecked();
        ViewGroup viewGroup = (ViewGroup) view.getParent().getParent();
        View selectView;

        switch(view.getId()){
            case R.id.cuisine_check:
                selectView = viewGroup.findViewById(R.id.cuisine_spinner);

                if(checked){
                    selectView.setVisibility(View.VISIBLE);
                    editor.putString(db.COLUMN_CUISINE, (String)((Spinner)selectView).getSelectedItem());
                }
                else{
                    selectView.setVisibility(View.GONE);
                    editor.remove(db.COLUMN_CUISINE);
                }

                break;

            case R.id.ingredient_check:
                selectView = viewGroup.findViewById(R.id.filter_ingredient_spinner);
                KPAdapter adapter = (KPAdapter)((Spinner)selectView).getAdapter();

                if(checked){
                    selectView.setVisibility(View.VISIBLE);
                    editor.putString(db.COLUMN_INGREDIENTID, Long.toString(adapter.getItemId(((Spinner)selectView).getSelectedItemPosition())));
                }
                else{
                    selectView.setVisibility(View.GONE);
                    editor.remove(db.COLUMN_INGREDIENTID);
                }

                break;

            case R.id.time_check:
                selectView = getSiblingView(viewGroup, R.id.seek_layout);

                if(checked){
                    SeekBar sb = (SeekBar)findViewById(R.id.time_seekbar);

                    selectView.setVisibility(View.VISIBLE);
                    editor.putString(db.TOTAL_TIME, Integer.toString(sb.getProgress()));
                }
                else{
                    selectView.setVisibility(View.GONE);
                    editor.remove(db.TOTAL_TIME);
                }

                break;

            case R.id.ingredient_list_check:
                selectView = getSiblingView(viewGroup, R.id.ingredient_list);

                if(checked){
                    selectView.setVisibility(View.VISIBLE);
                    displayDialog();
                }
                else{
                    selectView.setVisibility(View.GONE);
                    editor.remove(db.INGREDIENT_LIST);
                }

                break;
        }
    }

    public void onButtonClicked(View view){
        int id = view.getId();

        switch(id){
            case R.id.clear_button:
                break;

            case R.id.recipe_filter_button:

                if(editor.commit()){
                    setResult(1);
                }

                else{
                    setResult(0);
                }

                finish();
                break;
        }
    }

    private void setView(String name, String value){

        if(name.equals(db.COLUMN_CUISINE)){
            Spinner spinner = (Spinner)findViewById(R.id.cuisine_spinner);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            CheckBox check = (CheckBox)findViewById(R.id.cuisine_check);

            check.setChecked(true);

            spinner.setSelection(adapter.getPosition(value));
            spinner.setVisibility(View.VISIBLE);
        }

        else if(name.equals(db.COLUMN_INGREDIENTID)){
            Spinner spinner = (Spinner)findViewById(R.id.filter_ingredient_spinner);
            KPAdapter adapter = (KPAdapter)spinner.getAdapter();
            CheckBox check = (CheckBox)findViewById(R.id.ingredient_check);

            check.setChecked(true);

            spinner.setSelection(adapter.getPosition(Integer.parseInt(value)));
            spinner.setVisibility(View.VISIBLE);
        }

        else if(name.equals(db.TOTAL_TIME)){
            SeekBar sb = (SeekBar)findViewById(R.id.time_seekbar);
            CheckBox check = (CheckBox)findViewById(R.id.time_check);
            View view = findViewById(R.id.seek_layout);

            check.setChecked(true);

            sb.setProgress(Integer.parseInt((String)filter.get(db.TOTAL_TIME)));
            view.setVisibility(View.VISIBLE);
        }

        else if(name.equals(db.INGREDIENT_LIST)){
            CheckBox check = (CheckBox)findViewById(R.id.ingredient_list_check);

            check.setChecked(true);
        }
    }

    private void initIngredientList(){
        for(int i = 0; i < ingredients.size(); i++){
            ingredientArray[i] = ingredients.get(i).getName();

            if(inFilter.contains(Integer.toString(ingredients.get(i).getId()))){
                checkedParam[i] = true;
            }
        }
    }

    private void displayDialog(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CheckBox check = (CheckBox)findViewById(R.id.ingredient_list_check);
        final boolean[] tempBool = checkedParam;
        final HashSet<String> tempHash = inFilter;

        builder.setMultiChoiceItems(ingredientArray, tempBool, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                tempBool[i] = b;

                if(b){
                    tempHash.add(Integer.toString(ingredients.get(i).getId()));
                }

                else{
                    tempHash.remove(Integer.toString(ingredients.get(i).getId()));
                }
            }
        });

        builder.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inFilter = tempHash;
                checkedParam = tempBool;

                String list = inFilter.toString().replace("[", "").replace("]", "").replace(" ", "");
                Log.d(this.getClass().getName(), "entered");

                editor.putString(db.INGREDIENT_LIST, list);
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(check.isChecked()){
                    check.setChecked(false);
                }
            }
        });

        dialog = builder.create();
        dialog.show();
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
