package capstone.bophelohaesoopen;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Model.Audio;

public class AudioAdapter extends RecyclerView.Adapter<AudioViewHolder>
{
    ArrayList<Audio> audioList;
    AudioGalleryActivity audioGalleryActivity;
    RecyclerView recView;

    int selectedPosition = 0;

    boolean itemClicked = false;

    public AudioAdapter(AudioGalleryActivity activity, RecyclerView recyclerView, ArrayList<Audio> recordings)
    {
        audioList = recordings;
        audioGalleryActivity = activity;
        recView = recyclerView;
    }

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item_layout, parent, false);
        AudioViewHolder viewHolder = new AudioViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AudioViewHolder holder, int pos)
    {
        final int position = pos;

        Audio audio = audioList.get(position);

        holder.name.setText(audio.getName());

        long duration = audio.duration;
        String durationText = getFormattedDuration(duration);
        holder.duration.setText(durationText);


        // If the selected position is the same as that of the current view
        // draw the current view with the selection overlay
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
                if(audioGalleryActivity.inSelectionMode)
                {
//                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                    audioGalleryActivity.shareAudio(position);
                }
                else
                {
                    audioGalleryActivity.playAudio(position);
                }

            }
        });
    }

    private String getFormattedDuration(long d)
    {
        int seconds = (int)((d % 60000) / 1000);

        int minutes = (int)(d / 60000);

        String mins = "";
        if(minutes < 10)
        {
            mins = "0"+minutes;
        }
        else
        {
            mins = ""+minutes;
        }
        String scs = "";
        if(seconds < 10)
        {
            scs = "0"+seconds;
        }
        else
        {
            scs = ""+seconds;
        }

        return mins+":"+scs;
    }

    @Override
    public int getItemCount()
    {
        return audioList.size();
    }

    public void setItemClicked(boolean clicked)
    {
        itemClicked = clicked;
    }

    public void setRecordings(ArrayList<Audio> recordings)
    {
        audioList = recordings;
    }

    public ArrayList<Audio> getRecordings()
    {
        return audioList;
    }

    
}
