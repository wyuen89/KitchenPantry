package wyuen.kitchen_pantry;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

public class IngredientFragment extends Fragment {
    private KPDatabase db;
    private KPAdapter adapter;

    public static IngredientFragment newInstance(){
        return new IngredientFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("IngredientFragment", "onCreate called");

        db = new KPDatabase(DbHelper.getInstance(this.getContext()).getWritableDatabase());
        adapter = new KPAdapter(this.getContext(), db.selectAllIngredients());
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("IngredientFragment", "OnCreateView called");

        View view = inflater.inflate(R.layout.ingredient_fragment, container, false);
        Toolbar tb = (Toolbar)view.findViewById(R.id.ingredient_toolbar);
        ListView lv = (ListView)view.findViewById(R.id.ingredient_list);
        SearchView sv = (SearchView)view.findViewById(R.id.item_search);

        tb.inflateMenu(R.menu.toolbar_menu);

        Button addButton = (Button)view.findViewById(R.id.add_button);
        Button filterButton = (Button)view.findViewById(R.id.filter_button);

        addButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("IngredientFragment", "Add Button Pressed");
            }
        });


        filterButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("IngredientFragment", "Filter Button Pressed");
            }
        });

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: make so clicking position will open respective item page
                Log.d("IngredientFragment", "Position: " + i + " ID: " + adapter.getItemId(i));
            }
        });

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("IngredientFragment", newText);
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return view;
    }
}
