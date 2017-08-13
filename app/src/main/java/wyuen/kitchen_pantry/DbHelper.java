package wyuen.kitchen_pantry;
import android.database.sqlite.*;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "kp";
    private static final int VERSION = 1;
    private Context context;
    private static DbHelper instance = null;

    private DbHelper(Context context){
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    public static synchronized DbHelper getInstance(Context context) {

        if (instance == null) {
            instance = new DbHelper(context.getApplicationContext());
            Log.d("DbHelper", "instance initialized");
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try
        {
            Init(context, sqLiteDatabase);
        }catch(IOException e){
            Log.d("DbHelper", "Database creation failed");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private static void Init(Context context, SQLiteDatabase db) throws IOException{

        Log.d("DBHelper::Init", "Opening init file");

        InputStream in = context.getResources().openRawResource(R.raw.init);
        BufferedReader inReader = new BufferedReader(new InputStreamReader(in));

        Log.d("DBHelper::Init", "Executing init");

        //reads and creates database tables
        while(inReader.ready()){
            String stmt = inReader.readLine();
            db.execSQL(stmt);
        }

        Log.d("DBHelper::Init", "Finished init");

        inReader.close();

        Log.d("DBHelper::Init", "Opening populate file");

        in = context.getResources().openRawResource(R.raw.populate);
        inReader = new BufferedReader(new InputStreamReader(in));

        Log.d("DBHelper::Init", "Populating Database");

        //populates database with default values
        while(inReader.ready()){
            String stmt = inReader.readLine();
            db.execSQL(stmt);
        }

        Log.d("DBHelper::Init", "Populating Database Complete");

        inReader.close();
    }
}
