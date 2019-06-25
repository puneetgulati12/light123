package com.example.light;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.LayoutInflaterFactory;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Lightadapter extends RecyclerView.Adapter<Lightadapter.LightHolder> {
    private ArrayList<lightclass> lightclass;
    private Context context;

    public Lightadapter(ArrayList<lightclass> lightclass , Context ctx){
        this.lightclass = lightclass;
        this.context =ctx;
    }



//    @Override
//    public int getCount() {
//        return lightclass.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return lightclass.get(position);
//    }

    @NonNull
    @Override
    public LightHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemView) {
LayoutInflater li  = LayoutInflater.from(context);
View inflatedView = li.inflate(R.layout.item_row , parent ,false);
        return new LightHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull LightHolder holder, int position) {
        lightclass current = lightclass.get(position);
        holder.cou.setText(current.getCount());
        holder.sat.setText(current.getSatellite());
        holder.vis.setText(current.getVis_median());

    }

    @Override
    public int getItemCount() {
        return lightclass.size();
    }
class LightHolder extends  RecyclerView.ViewHolder{
        private TextView sat , cou , vis;
    public LightHolder(View itemView) {
            super(itemView);

            cou = itemView.findViewById(R.id.count);
            sat = itemView.findViewById(R.id.satellite);
            vis = itemView.findViewById(R.id.vis_median);
    }
}


//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }

//    @Override
//    public View getView(int position, View view, ViewGroup viewGroup) {
//        lightclass current =  lightclass.get(position);
//        LayoutInflater li = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//        View inflatedView = li.inflate(R.layout.item_row , viewGroup ,false);
//
//        TextView count =  inflatedView.findViewById(R.id.count);
//        TextView vismedian =  inflatedView.findViewById(R.id.vis_median);
//        TextView satellite =  inflatedView.findViewById(R.id.satellite);
//
//        count.setText(current.getCount());
//        vismedian.setText(current.getVis_median());
//        satellite.setText(current.getSatellite());
//        return inflatedView;
//    }

}
