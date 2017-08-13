package wyuen.kitchen_pantry;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KPDatabase {

    public static final String TABLE_RECIPE = "Recipe";
    public static final String TABLE_INGREDIENTS = "Ingredients";

    public static final String COLUMN_INGREDIENTID= "IngredientID";
    public static final String COLUMN_RECIPEID= "RecID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_TYPE = "Type";

    private SQLiteDatabase db;
    private Context context;

    public KPDatabase(Context context, SQLiteDatabase db){
        this.db = db;
        this.context = context.getApplicationContext();
        Log.d("KPDatabase", "count updated to " + Integer.toString(getMaxIngredientID()));
    }

    public List<ItemInfo> getAllIngredients(){
        List<ItemInfo> ret;
        Cursor results;

        String[] columns = {COLUMN_INGREDIENTID, COLUMN_NAME};

        results = db.query(TABLE_INGREDIENTS, columns, null, null, null, null, COLUMN_NAME);

        Log.d("KPDatabase", Integer.toString(results.getCount()) + " rows in Cursor");

        ret = convertToItemInfo(results);

        results.close();

        return ret;
    }

    public List<ItemInfo> getFilteredIngredients(){
        List<ItemInfo> ret;
        Cursor results;
        String select = null;
        String[] args = null;
        StringBuilder str = new StringBuilder();

        SharedPreferences shared = context.getSharedPreferences("ingredient_filter", Context.MODE_PRIVATE);

        String[] columns = {COLUMN_INGREDIENTID, COLUMN_NAME};

        Object[] keys = shared.getAll().keySet().toArray();

        if(keys.length > 0) {
            args = new String[keys.length];

            for (int i = 0; i < keys.length; i++) {
                if (i < 0) {
                    str.append(" AND ");
                }

                str.append((String) keys[i] + " = ?");
                args[i] = shared.getString((String) keys[i], null);
            }

            select = str.toString();
        }

        Log.d("KPDatabase", str.toString());

        results = db.query(TABLE_INGREDIENTS, columns, select, args, null, null, COLUMN_NAME);

        Log.d("KPDatabase", Integer.toString(results.getCount()) + " rows in Cursor");

        ret = convertToItemInfo(results);

        results.close();

        return ret;
    }

    public List<ItemInfo> getAllRecipes(){
        List<ItemInfo> ret;
        Cursor results;

        String[] columns = {COLUMN_RECIPEID, COLUMN_NAME};

        results = db.query(TABLE_RECIPE, columns, null, null, null, null, COLUMN_NAME);

        Log.d("KPDatabase", Integer.toString(results.getCount()) + " rows in Cursor");

        ret = convertToItemInfo(results);

        results.close();

        return ret;
    }

    public List<String> getTypes(){
        List<String> ret;
        Cursor results;

        String[] columns = {COLUMN_TYPE};

        results = db.query(true, TABLE_INGREDIENTS, columns, null, null, null, null, COLUMN_TYPE, null);

        Log.d("KPDatabase", Integer.toString(results.getCount()) + " rows in Cursor");

        ret = convertToString(results);

        results.close();

        return ret;
    }

    public boolean addIngredient(int id, String name, String type) {
        boolean success = true;
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENTID, id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_TYPE, type);

        try {
            db.beginTransaction();
            db.insertOrThrow(TABLE_INGREDIENTS, null, values);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            Log.d("KPDatabase", e.getMessage());
            success = false;
        }finally{
            db.endTransaction();
        }

        return success;
    }

    public int getMaxIngredientID(){
        return getMaxID(TABLE_INGREDIENTS, COLUMN_INGREDIENTID);
    }

    public int getMaxRecipeID(){
        return getMaxID(TABLE_RECIPE, COLUMN_RECIPEID);
    }

    private int getMaxID(String table, String column){
        Cursor cursor;
        StringBuilder max= new StringBuilder();

        max.append("MAX(").append(column).append(")");

        String[] columns = {max.toString()};

        cursor = db.query(false, table, columns, null, null, null, null, null, null);

        cursor.moveToNext();
        return cursor.getInt(0);
    }

    private void setSelectionString(String str){
        str = new String("hello");
    }

    private List<ItemInfo> convertToItemInfo(Cursor cursor){

        List<ItemInfo> ret = new ArrayList<ItemInfo>();

        while(cursor.moveToNext()){
            ret.add(new ItemInfo(cursor.getInt(0), cursor.getString(1)));
        }
        return ret;
    }

    private List<String> convertToString(Cursor cursor){
        List<String> ret = new ArrayList<String>();

        while(cursor.moveToNext()){
            String string = cursor.getString(0);

            if(string != null){
                ret.add(string);
            }
        }
        return ret;
    }
}
