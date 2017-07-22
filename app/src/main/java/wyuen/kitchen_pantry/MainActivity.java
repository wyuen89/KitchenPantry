package wyuen.kitchen_pantry;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        BottomNavigationView botNavView = (BottomNavigationView)findViewById(R.id.navigation);

        botNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;

                switch(item.getItemId()){
                    case R.id.home:
                        selected = HomeFragment.newInstance();
                        break;

                    case R.id.recipe:
                        selected = RecipeFragment.newInstance();
                        break;

                    case R.id.ingredients:
                        selected = IngredientFragment.newInstance();
                        break;

                    case R.id.settings:
                        selected = SettingFragment.newInstance();
                        break;
                }

                FragmentManager fragmentManage = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManage.beginTransaction();
                transaction.replace(R.id.active_fragment, selected);
                transaction.commit();

                return true;
            }
        });
        FragmentManager fragmentManage = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManage.beginTransaction();
        transaction.replace(R.id.active_fragment, HomeFragment.newInstance());
        transaction.commit();

    }
}
