package wyuen.kitchen_pantry;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class KPAdapter extends BaseAdapter implements Filterable {

    private List<ItemInfo> original;
    private List<ItemInfo> idList;
    LayoutInflater inflater;

    private class ViewHolder{
        TextView view;
    }

    public KPAdapter(Context context, List<ItemInfo> inList){
        idList = inList;
        original = inList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return idList.size();
    }

    @Override
    public Object getItem(int i) {
        return idList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return idList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_layout, null);
            holder.view = (TextView) view.findViewById(R.id.item);
            view.setTag(holder);
        }

        else{
            holder = (ViewHolder) view.getTag();
        }

        holder.view.setText(idList.get(i).getName());

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults ret = new FilterResults();
                List<ItemInfo> filtered;


                if((charSequence == null) || (charSequence.length() < 3)){
                    filtered = original;
                }

                else{
                    filtered = new ArrayList<ItemInfo>();

                    charSequence = charSequence.toString().toLowerCase();

                    for(ItemInfo item: original){
                        if(item.getName().toLowerCase().contains(charSequence)){
                            filtered.add(item);
                        }
                    }
                }

                ret.count = filtered.size();
                ret.values = filtered;
                return ret;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                idList = (List<ItemInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
