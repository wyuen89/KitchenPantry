package wyuen.kitchen_pantry;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity implements InstructionDialog.OnCompleteListener, IngredientDialog.OnCompleteListener {

    ViewGroup viewGroup;
    ArrayAdapter<String> stepAdapter;
    ArrayAdapter<String> ingredientAdapter;
    List<Integer> idList;
    List<Double> quantity;
    List<String> units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        viewGroup = (ViewGroup)((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0);

        idList = new ArrayList<Integer>();
        quantity = new ArrayList<Double>();
        units = new ArrayList<String>();

        NonScrollListView instructionList = (NonScrollListView) findViewById(R.id.instruction_group);
        NonScrollListView ingredientList = (NonScrollListView)findViewById(R.id.ingredient_group);

        ArrayList<String> steps = new ArrayList<String>();
        stepAdapter = new StepAdapter(this, R.layout.step_layout, steps);

        instructionList.setAdapter(stepAdapter);

        ArrayList<String> ingredients = new ArrayList<String>();
        ingredientAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients);

        ingredientList.setAdapter(ingredientAdapter);


        EditText edit = (EditText)findViewById(R.id.prep_time_input);
        edit.setTransformationMethod(null);

        edit = (EditText)findViewById(R.id.cook_time_input);
        edit.setTransformationMethod(null);

    }

    public void onButtonClicked(View view){
        DialogFragment dialog;

        switch(view.getId()){
            case R.id.add_ingredient_button:
                Log.d("AddRecipeActivity", "Add button clicked");
                dialog = IngredientDialog.newInstance();
                dialog.show(getFragmentManager(), "add ingredient");
                break;

            case R.id.add_step_button:
                Log.d("AddRecipeActivity", "Step button clicked");
                dialog = InstructionDialog.newInstance();
                dialog.show(getFragmentManager(), "add step");
                break;

            case R.id.done_button:
                Log.d(getClass().getName(), "Done button clicked");
                KPDatabase db = new KPDatabase(this, DbHelper.getInstance(this).getWritableDatabase());
                List<String> recInfo = new ArrayList<String>();
                List<String> steps = ((StepAdapter)stepAdapter).getSteps();

                EditText et = (EditText)findViewById(R.id.name_input);
                recInfo.add(et.getText().toString());

                et = (EditText)findViewById(R.id.cuisine_input);
                recInfo.add(et.getText().toString());

                et = (EditText)findViewById(R.id.prep_time_input);
                recInfo.add(et.getText().toString());

                et = (EditText)findViewById(R.id.cook_time_input);
                recInfo.add(et.getText().toString());

                boolean success = db.addRecipe(recInfo, idList, quantity, units, steps);

                if(success){
                    setResult(1);
                }
                else{
                    setResult(0);
                }

                finish();
                break;
        }
    }

    @Override
    public void onComplete(String instruction) {
        Log.d("AddRecipeActivity", "onComplete:" + instruction);
        stepAdapter.add(instruction);
    }

    @Override
    public void onComplete(int id, String name, Double quantity, String unit) {
        Log.d("AddRecipeActivity", "onComplete:" + Integer.toString(id) + " " + name + " " + quantity + " " + unit);
        idList.add(id);
        this.quantity.add(quantity);
        this.units.add(unit);

        String quantityDisp;

        if(quantity%1 > 0.0){
            quantityDisp = Double.toString(quantity);
        }

        else{
            quantityDisp = Integer.toString(quantity.intValue());
        }

        ingredientAdapter.add(quantityDisp + " " + unit + " " + name);
    }
}
