package ru.tcgeo.application.home_screen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GILayer;

/**
 * Created by a_belov on 06.07.15.
 */
public class LayersAdapter extends ArrayAdapter<LayersAdapterItem> {
    Geoinfo mActivity;
//    View.OnClickListener mListener;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayersAdapterItem item = getItem(position);
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.re_layers_list_item, null);
        TextView name = ((TextView) v.findViewById(R.id.layers_list_item_text));
        name.setText(item.m_tuple.layer.getName());


        CheckBox cbMarkers = (CheckBox)v.findViewById(R.id.cbMarkersSource);
        if(item.m_tuple.layer.type_== GILayer.GILayerType.XML) {
            cbMarkers.setVisibility(View.VISIBLE);
            cbMarkers.setChecked(item.m_tuple.layer.getName().equalsIgnoreCase(mActivity.getMap().ps.m_markers));
            cbMarkers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                    mActivity.getMap().ps.m_markers = item.m_tuple.layer.getName();
                    notifyDataSetChanged();
                    }
                }
            });
        }else {
            cbMarkers.setVisibility(View.GONE);
        }

//        name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(item.m_tuple.layer.type_== GILayer.GILayerType.XML){
//                    mActivity.getMap().ps.m_markers = item.m_tuple.layer.getName();
//                    notifyDataSetChanged();
//                }
//            }
//        });
//        name.setOnClickListener(mListener);
        CheckBox checkbox = (CheckBox) v.findViewById(R.id.layers_list_item_switch);
        checkbox.setChecked(item.m_tuple.visible);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                item.m_tuple.visible = isChecked;
                mActivity.getMap().UpdateMap();
            }
        });


        return v;
    }

    public LayersAdapter(Geoinfo activity, int resource,
                         int textViewResourceId/*,View.OnClickListener listener*/) {
        super(activity, resource, textViewResourceId);
        mActivity = activity;
//        mListener = listener;
    }
}
