package capstone.bophelohaesoopen;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Video;

/**
 * Data adapter for MainActivity RecyclerView (list of videos)
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> implements View.OnClickListener
{
    ArrayList<Video> videoList;
    Context context;
    RecyclerView recView;


    public VideoAdapter(Context context, RecyclerView recView, ArrayList<Video> videos)
    {
        videoList = videos;
        Log.i(">> LOG", "Video adapter created");
        this.context = context;
        this.recView = recView;
    }

    public VideoAdapter()
    {}

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_layout, parent, false);
        itemView.setOnClickListener(this);
        VideoViewHolder viewHolder = new VideoViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position)
    {
        Video video = videoList.get(position);
        holder.nameTextView.setText(video.getName());

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

    @Override
    public void onClick(View view)
    {
        int position = recView.getChildLayoutPosition(view);

        ((MainActivity)context).playVideo(position);
    }
}
