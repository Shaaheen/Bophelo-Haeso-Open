package capstone.bophelohaesoopen;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom video view holder class for the RecyclerView used to display list of videos
 */

public class VideoViewHolder extends RecyclerView.ViewHolder
{
    TextView nameTextView;
    ImageView thumbnail;

    public VideoViewHolder(View itemView)
    {
        super(itemView);

        nameTextView = (TextView)itemView.findViewById(R.id.nameTextView);
        thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
    }

    private void itemClick()
    {
//        MainActivity.playVideo();
    }

}
