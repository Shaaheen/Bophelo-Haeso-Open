package capstone.bophelohaesoopen;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;

public class AudioViewHolder extends RecyclerView.ViewHolder
{

    public RelativeLayout selectionOverlay;

    public TextView name;
    public TextView duration;

    public AudioViewHolder(View itemView)
    {
        super(itemView);
        selectionOverlay = (RelativeLayout)itemView.findViewById(R.id.selectionOverlay);
        name = (TextView)itemView.findViewById(R.id.name);
        duration = (TextView)itemView.findViewById(R.id.duration);

    }
}

