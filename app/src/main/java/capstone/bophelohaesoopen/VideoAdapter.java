package capstone.bophelohaesoopen;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Media;
import capstone.bophelohaesoopen.HaesoAPI.Video;

/**
 * Data adapter for MainActivity RecyclerView (list of videos)
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder>
{
    ArrayList<Video> videoList;
    MainActivity mainActivity;
    RecyclerView recView;
    VideoViewHolder currentHolder;

    int selectedPosition = 0;

    boolean itemClicked = false;

    private static int nameCharCount = 12;

    public VideoAdapter(MainActivity mainActivity, RecyclerView recView, ArrayList<Video> videos)
    {
        videoList = videos;
        Log.i(">> LOG", "Video adapter created");
        this.mainActivity = mainActivity;
        this.recView = recView;
    }

    public VideoAdapter()
    {}

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_layout, parent, false);
        VideoViewHolder viewHolder = new VideoViewHolder(itemView);
        currentHolder = viewHolder;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position)
    {
        Video video = videoList.get(position);
        String name = video.getName();
        if(name.length() > nameCharCount)
        {
            name = name.substring(0, nameCharCount);
            name+="..";
            holder.nameTextView.setText(name);
        }
        else
        {
            holder.nameTextView.setText(name);

        }

        holder.thumbnail.setImageBitmap(video.thumb);

        if(selectedPosition == position && itemClicked)
        {
            holder.selectionOverlay.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.selectionOverlay.setVisibility(View.INVISIBLE);
        }

        final VideoViewHolder tempHolder = holder;
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setItemClicked(true);
                if(mainActivity.inSelectionMode)
                {
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                    mainActivity.shareVideo(position);
                }
                else
                {
                    mainActivity.playVideo(position);
                }

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return videoList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setVideoList(ArrayList<Video> videos)
    {
        videoList = videos;
    }

    public void setItemClicked(boolean clicked)
    {
        itemClicked = clicked;
    }
}
