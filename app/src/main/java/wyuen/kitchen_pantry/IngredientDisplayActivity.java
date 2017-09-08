package wyuen.kitchen_pantry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class IngredientDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Map<String, List<String>> vals;
        KPDatabase db = new KPDatabase(this, DbHelper.getInstance(this).getReadableDatabase());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_display);

        Intent intent = this.getIntent();
        long id = intent.getLongExtra("id", -1);

        vals = db.getIngredientById(id);

        List<String> nameList = vals.get("Name");
        List<String> typeList = vals.get("Type");

        TextView tv = (TextView) findViewById(R.id.ingredient_display_name);
        tv.setText(nameList.get(0));

        tv = (TextView) findViewById(R.id.ingredient_display_type);
        tv.setText(typeList.get(0));
    }

}
