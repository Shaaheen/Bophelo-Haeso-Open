package capstone.bophelohaesoopen;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Model.Image;


public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder>
{
    ArrayList<Image> imageList;
    PictureGalleryActivity galleryActivity;
    RecyclerView recView;

    int selectedPosition = 0;

    boolean itemClicked = false;

    public ImageAdapter(PictureGalleryActivity activity, RecyclerView recyclerView, ArrayList<Image> images)
    {
        imageList = images;
        galleryActivity = activity;
        recView = recyclerView;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_layout, parent, false);
        ImageViewHolder viewHolder = new ImageViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int pos)
    {
        final int position = pos;

        Image image = imageList.get(position);

        holder.thumbnail.setImageBitmap(image.thumb);

        if(selectedPosition == position && itemClicked)
        {
            holder.selectionOverlay.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.selectionOverlay.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setItemClicked(true);
                if(galleryActivity.inSelectionMode)
                {
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                    galleryActivity.shareImage(position);
                }
                else
                {
                    galleryActivity.displayImage(position);
                }

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return imageList.size();
    }

    public void setItemClicked(boolean clicked)
    {
        itemClicked = clicked;
    }

    public void setImages(ArrayList<Image> images)
    {
        imageList = images;
    }

    public ArrayList<Image> getImages()
    {
        return imageList;
    }
}
