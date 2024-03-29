package capstone.bophelohaesoopen;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Custom image view holder class for the RecyclerView used to display gallery_black of images
 */
public class ImageViewHolder extends RecyclerView.ViewHolder
{
    public ImageView thumbnail;

    public ImageViewHolder(View itemView)
    {
        super(itemView);
        thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
    }
}
