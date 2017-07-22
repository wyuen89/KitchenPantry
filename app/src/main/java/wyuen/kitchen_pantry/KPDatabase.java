package wyuen.kitchen_pantry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class KPDatabase {

    private SQLiteDatabase db;

    public KPDatabase(SQLiteDatabase db){
        this.db = db;
    }

    public List<ItemInfo> selectAllIngredients(){
        List<ItemInfo> ret;
        Cursor results;

        String[] columns = {DbHelper.COLUMN_INGREDIENTID, DbHelper.COLUMN_NAME};

        results = db.query(DbHelper.TABLE_INGREDIENTS, columns, null, null, null, null, "Name");

        Log.d("KPDatabase", Integer.toString(results.getCount()) + " rows in Cursor");

        ret = convert(results);

        results.close();

        return ret;
    }

    public List<ItemInfo> selectAllRecipes(){
        List<ItemInfo> ret;
        Cursor results;

        String[] columns = {DbHelper.COLUMN_RECIPEID, DbHelper.COLUMN_NAME};

        results = db.query(DbHelper.TABLE_RECIPE, columns, null, null, null, null, "Name");

        Log.d("KPDatabase", Integer.toString(results.getCount()) + " rows in Cursor");

        ret = convert(results);

        results.close();

        return ret;
    }

    private List<ItemInfo> convert(Cursor cursor){

        List<ItemInfo> ret = new ArrayList<ItemInfo>();

        while(cursor.moveToNext()){
            ret.add(new ItemInfo(cursor.getInt(0), cursor.getString(1)));
        }
        return ret;
    }
}
