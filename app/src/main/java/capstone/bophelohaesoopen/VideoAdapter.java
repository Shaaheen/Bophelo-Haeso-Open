package capstone.bophelohaesoopen;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder>
{
    ArrayList<Video> videoList;
    Context context;

    public VideoAdapter(Context context, ArrayList<Video> videos)
    {
        videoList = videos;
        Log.i(">> LOG", "Video adapter created");
        this.context = context;
    }

    public VideoAdapter()
    {

    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_layout, parent, false);
        VideoViewHolder viewHolder = new VideoViewHolder(itemView);
        Log.i(">> LOG", "View holder created");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position)
    {
        Video video = videoList.get(position);
        Log.i(">> LOG: video name",video.getName());
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
}
