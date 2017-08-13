package wyuen.kitchen_pantry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddIngredientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        final KPDatabase db = new KPDatabase(this, DbHelper.getInstance(this.getApplicationContext()).getWritableDatabase());
        final EditText name = (EditText)findViewById(R.id.ingredient_name);
        final EditText type = (EditText)findViewById(R.id.ingredient_type);

        Button back = (Button)findViewById(R.id.ingredient_back);
        Button add = (Button)findViewById(R.id.ingredient_add);

        back.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                setResult(0);
                finish();
            }
        });

        add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //@TODO check if addingredients returns true: set result to success, false: set result to failed
                boolean success = db.addIngredient(db.getMaxIngredientID() + 1, name.getText().toString(), type.getText().toString());

                if(success){
                    setResult(1);
                }
                else{
                    setResult(0);
                }

                finish();
            }
        });
    }
}
