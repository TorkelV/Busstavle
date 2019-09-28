package no.lekrot.busstavle;

import android.widget.ArrayAdapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class BusAdapter extends ArrayAdapter<BusRide> {

    private Context mContext;
    private List<BusRide> busRideList;

    public BusAdapter(@NonNull Context context, List<BusRide> list) {
        super(context, 0 , list);
        mContext = context;
        busRideList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        BusRide busRide = busRideList.get(position);

        TextView name = listItem.findViewById(R.id.txt_busname);
        name.setText(busRide.getBusText());

        TextView id =  listItem.findViewById(R.id.txt_busid);
        id.setText(busRide.getBusid());

        TextView departure1 = listItem.findViewById(R.id.txt_busdeparture1);
        departure1.setText(departureToString(busRide.getDepartures().get(0)));

        if(busRide.getDepartures().size() > 1){
            TextView departure2 = listItem.findViewById(R.id.txt_busdeparture2);
            departure2.setText(departureToString(busRide.getDepartures().get(1)));
        }

        if(busRide.getDepartures().size() > 2){
            TextView departure2 = listItem.findViewById(R.id.txt_busdeparture3);
            departure2.setText(departureToString(busRide.getDepartures().get(2)));
        }


        return listItem;
    }

    private String departureToString(LocalDateTime time){
        return time.toLocalTime().minusSeconds(time.getSecond()).toString();
    }
}
