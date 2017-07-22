package wyuen.kitchen_pantry;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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

public class RecipeFragment extends Fragment {

    private KPDatabase db;
    private KPAdapter adapter;

    public static RecipeFragment newInstance(){
        return new RecipeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("RecipeFragment", "onCreate called");

        db = new KPDatabase(DbHelper.getInstance(this.getContext()).getWritableDatabase());
        adapter = new KPAdapter(this.getContext(), db.selectAllRecipes());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("IngredientFragment", "OnCreateView called");

        View view = inflater.inflate(R.layout.recipe_fragment, container, false);
        ListView lv = (ListView)view.findViewById(R.id.recipe_list);
        SearchView sv = (SearchView)view.findViewById(R.id.recipe_search);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: make so clicking position will open respective item page
                Log.d("RecipeFragment", "Position: " + i + " ID: " + adapter.getItemId(i));
            }
        });

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("RecipeFragment", newText);
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        Log.d("RecipeFragment", "OnCreateOptionsMenu called");
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_bar);
        View view = item.getActionView();

        Button addButton = (Button)view.findViewById(R.id.add_button);
        Button filterButton = (Button)view.findViewById(R.id.filter_button);

        addButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("RecipeFragment", "Add Button Pressed");
            }
        });


        filterButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("RecipeFragment", "Filter Button Pressed");
            }
        });
    }
}
