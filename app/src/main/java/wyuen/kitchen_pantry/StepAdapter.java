package wyuen.kitchen_pantry;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StepAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    List<String> strings;

    private class ViewHolder{
        TextView stepNum;
        TextView step;
    }

    public StepAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        inflater = LayoutInflater.from(context);
        strings = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        Log.d("hello", Integer.toString(getCount()));

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.step_layout, null);
            holder.stepNum = (TextView) convertView.findViewById(R.id.step_num);
            holder.step = (TextView) convertView.findViewById(R.id.step_text);
            convertView.setTag(holder);
        }

        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.stepNum.setText("Step " + Integer.toString(position + 1) + ":");
        holder.step.setText(getItem(position));

        return convertView;
    }

    public List<String> getSteps(){
        return strings;
    }
}
