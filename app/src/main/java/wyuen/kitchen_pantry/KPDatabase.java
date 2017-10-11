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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KPDatabase {

    public static final String TABLE_RECIPE = "Recipe";
    public static final String TABLE_INGREDIENTS = "Ingredients";
    public static final String TABLE_REC_INGREDIENTS = "RecIngredients";
    public static final String TABLE_INSTRUCTIONS = "Instructions";

    public static final String COLUMN_INGREDIENTID= "IngredientID";
    public static final String COLUMN_RECIPEID= "RecID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_CUISINE = "Cuisine";
    public static final String COLUMN_PREP_TIME = "PrepTime";
    public static final String COLUMN_COOK_TIME = "CookTime";
    public static final String COLUMN_QUANTITY = "Quantity";
    public static final String COLUMN_MEASUREMENT = "Measurement";
    public static final String COLUMN_INSTRUCTION = "Instr";
    public static final String COLUMN_STEP = "Step";

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

    public Map<String, List<String>> getIngredientById(long id){
        Map<String, List<String>> ret;
        Cursor results;

        String[] columns = {COLUMN_NAME, COLUMN_TYPE};
        String select = "IngredientID = ?";
        String[] args = {Long.toString(id)};

        results = db.query(TABLE_INGREDIENTS, columns, select, args, null, null, null);
        ret =  convertToMap(results);

        results.close();

        for(String string : ret.keySet()){
            Log.d("KPDatabase", string);
        }

        return ret;
    }

    public Map<String, List<String>> getRecipeById(long id){
        Map<String, List<String>> ret;
        String recId = Long.toString(id);
        Cursor recResults;
        Cursor ingredientResults;
        Cursor stepResults;

        String[] recColumns = {COLUMN_NAME, COLUMN_CUISINE};
        String recSelect = COLUMN_RECIPEID + " = ?";
        String[] recArgs = {recId};

        recResults = db.query(TABLE_RECIPE, recColumns, recSelect, recArgs, null, null, null);
        ret = convertToMap(recResults);

        recResults.close();

        String[] stepColumns = {COLUMN_STEP, COLUMN_INSTRUCTION};
        String stepSelect = COLUMN_RECIPEID + " = ?";
        String[] stepArgs = {recId};

        stepResults = db.query(TABLE_INSTRUCTIONS, stepColumns, stepSelect, stepArgs, null, null, COLUMN_STEP);
        ret.putAll(convertToMap(stepResults));

        String dropQuery = "DROP TABLE IF EXISTS tempTable;";
        db.execSQL(dropQuery);

        String viewQuery = "CREATE TEMPORARY TABLE tempTable AS SELECT IngredientID, Quantity, Measurement FROM RecIngredients WHERE RecID = ?;";
        String[] viewArgs = {recId};
        db.execSQL(viewQuery, viewArgs);

        String ingredientQuery = "SELECT t1.*, Ingredients.Name AS IngredientName FROM tempTable t1 INNER JOIN Ingredients on Ingredients.IngredientID = t1.IngredientID;";
        ingredientResults = db.rawQuery(ingredientQuery, null);

        ret.putAll(convertToMap(ingredientResults));

        for(String string : ret.keySet()){
            Log.d("KPDatabase", string);
        }

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

    public boolean addRecipe(List<String> recInfo, List<Integer> idList, List<Double> quantity, List<String> units, List<String> instructions){
        ContentValues values;
        boolean success = true;
        int id = getMaxRecipeID() + 1;



        try{
            db.beginTransaction();

            values = fillValues(TABLE_RECIPE, recInfo, id);
            db.insertOrThrow(TABLE_RECIPE, null, values);

            for(int i = 0; i < idList.size(); i++){
                values = fillValues(idList, quantity, units, id, i);
                db.insertOrThrow(TABLE_REC_INGREDIENTS, null, values);
            }

            for(int i = 0; i < instructions.size(); i++){
                values = fillValues(instructions, id, i);
                db.insertOrThrow(TABLE_INSTRUCTIONS, null, values);
            }

            db.setTransactionSuccessful();


        }catch(SQLException e){
            Log.d(getClass().getName(), e.getMessage());
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

    public Map<String, List<String>> convertToMap(Cursor cursor){
        Map<String, List<String>> ret = new HashMap<String, List<String>>();
        String[] columns = cursor.getColumnNames();
        List<List<String>> temp = new ArrayList<List<String>>();

        for(int i = 0; i<columns.length; i++){
            temp.add(new ArrayList<String>());
        }

        while(cursor.moveToNext()){
            for(int i = 0; i<cursor.getColumnCount(); i++){
                int type = cursor.getType(i);

                if(type == Cursor.FIELD_TYPE_NULL){
                    temp.get(i).add("");
                }

                else if(type == Cursor.FIELD_TYPE_INTEGER){
                    temp.get(i).add(Integer.toString(cursor.getInt(i)));
                }

                else if(type == Cursor.FIELD_TYPE_FLOAT){
                    temp.get(i).add(Double.toString(cursor.getFloat(i)));
                }

                else{
                    temp.get(i).add(cursor.getString(i));
                }
            }
        }

        for(int i = 0; i<columns.length; i++){
            ret.put(columns[i], temp.get(i));
        }

        return ret;
    }

    //used to fill content values for recipe info
    private ContentValues fillValues(String table, List<String> recInfo, Integer id){
        ContentValues values = new ContentValues();

        values.put(COLUMN_RECIPEID, id);
        values.put(COLUMN_NAME, recInfo.get(0));
        values.put(COLUMN_CUISINE, recInfo.get(1));

        if(!recInfo.get(2).equals("")) {
            values.put(COLUMN_PREP_TIME, Integer.parseInt(recInfo.get(2)));
        }

        if(!recInfo.get(3).equals("")) {
            values.put(COLUMN_COOK_TIME, Integer.parseInt(recInfo.get(3)));
        }

        return values;
    }

    //used to fill content values for recipe ingredients
    private ContentValues fillValues(List<Integer> idList, List<Double> quantity, List<String> unit, Integer recID,  Integer index){
        ContentValues values = new ContentValues();

        values.put(COLUMN_RECIPEID, recID);
        values.put(COLUMN_INGREDIENTID, idList.get(index));

        if(quantity.get(index) > 0.0){
            values.put(COLUMN_QUANTITY, quantity.get(index));
            values.put(COLUMN_MEASUREMENT, unit.get(index));
        }

        return values;
    }

    //used to fill content values for instructions
    private ContentValues fillValues(List<String> instructions, Integer recID, Integer stepNum){
        ContentValues values = new ContentValues();

        values.put(COLUMN_RECIPEID, recID);
        values.put(COLUMN_STEP, stepNum + 1);
        values.put(COLUMN_INSTRUCTION, instructions.get(stepNum));

        return values;
    }
}
