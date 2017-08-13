package wyuen.kitchen_pantry;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

public class KPSpinnerAdapter extends BaseAdapter {

    List<String> list;
    Context context;

    public KPSpinnerAdapter(Context context, List<String>  inlist){
        list = inlist;
        this.context = context;
    }

    private class ViewHolder{
        TextView view;
    }

    public int getPosition(String s){
        return list.indexOf(s);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView text = new TextView(context);
        text.setText(list.get(i));

        return text;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        TextView text = new TextView(context);

        if(position == 0){
            text.setHeight(0);
            text.setVisibility(View.GONE);
        }

        else {
            text.setText(list.get(position));
        }

        return text;
    }
}
