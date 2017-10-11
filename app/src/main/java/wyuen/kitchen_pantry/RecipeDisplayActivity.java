package wyuen.kitchen_pantry;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_display);

        KPDatabase db = new KPDatabase(this, DbHelper.getInstance(this).getReadableDatabase());
        TextView nameView = (TextView)findViewById(R.id.recipe_name);
        TextView cuisineView = (TextView)findViewById(R.id.recipe_cuisine);
        NonScrollListView instructions = (NonScrollListView)findViewById(R.id.display_instruction_list);
        NonScrollListView ingredients = (NonScrollListView)findViewById(R.id.display_ingredient_list);
        Map<String, List<String>> vals;

        long id = getIntent().getLongExtra(db.COLUMN_RECIPEID, -1);

        vals = db.getRecipeById(id);

        String name = vals.get(db.COLUMN_NAME).get(0);
        String cuisine = vals.get(db.COLUMN_CUISINE).get(0);

        nameView.setText(name);
        cuisineView.setText(cuisine);

        List<String> steps = vals.get(db.COLUMN_INSTRUCTION);

        ArrayAdapter<String> stepsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, steps);
        instructions.setAdapter(stepsAdapter);

        List<String> ingrNames = vals.get("IngredientName");
        List<String> quantity = vals.get("Quantity");
        List<String> measurement = vals.get("Measurement");
        List<String> ingredientsList = new ArrayList<String>();

        for(int i = 0; i < ingrNames.size(); i++){
            ingredientsList.add(quantity.get(i) + " " + ingrNames.get(i) + " " + measurement.get(i));
        }

        ArrayAdapter<String> ingrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredientsList);
        ingredients.setAdapter(ingrAdapter);

        Log.d(this.getClass().getSimpleName(), Integer.toString(vals.get("IngredientName").size()));
        Log.d(this.getClass().getSimpleName(), Integer.toString(vals.get("Quantity").size()));
        Log.d(this.getClass().getSimpleName(), Integer.toString(vals.get("Measurement").size()));

    }

}
