package capstone.bophelohaesoopen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Audio;
import capstone.bophelohaesoopen.HaesoAPI.MediaLoadService;

/**
 * Activity where recorded audio files are listed / shown
 * */

public class AudioGalleryActivity extends AppCompatActivity
{
    RelativeLayout shareMediaBar;
    RecyclerView recyclerView;
    AudioAdapter audioAdapter;

    RelativeLayout audioLoadingScreen;

    ArrayList<Audio> audioList = new ArrayList<>();

    MediaShareUtils mediaShareUtils;

    MediaLoadService mediaLoadService;

    TextView noMediaText;

    ImageView shareIcon;
    TextView shareText;

    boolean inSelectionMode = false;
    private static int CHECK_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_gallery);

        initialize();

        mediaLoadService.start();
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {

                if (mediaLoadService.mediaLoaded)
                {
//                    System.out.println("recordings loaded!");
                    audioList = mediaLoadService.getAudioList();
                    audioLoadingScreen.setVisibility(View.INVISIBLE);
                    if(!audioList.isEmpty())
                    {
                        audioAdapter.setRecordings(audioList);
                        audioAdapter.notifyDataSetChanged();

                    }
                    else
                    {
                        noMediaText.setVisibility(View.VISIBLE);
                    }
                    handler.removeCallbacks(this);

                }
                else
                {
                    handler.postDelayed(this, CHECK_DURATION);
                }

            }
        };
        handler.postDelayed(runnable, CHECK_DURATION);


    }

    private void initialize()
    {
        mediaShareUtils = new MediaShareUtils(getApplicationContext(), this);
        mediaLoadService = new MediaLoadService(this);
        startService(new Intent(this, MediaLoadService.class));

        shareMediaBar = (RelativeLayout) findViewById(R.id.shareMediaBar);
        shareMediaBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shareMediaButtonClick();
            }
        });
        shareIcon = (ImageView) findViewById(R.id.shareIcon);
        shareText = (TextView) findViewById(R.id.shareText);

        noMediaText = (TextView)findViewById(R.id.noMediaText);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        audioAdapter = new AudioAdapter(this, recyclerView, audioList);
        recyclerView.setAdapter(audioAdapter);
    }


    private void shareMediaButtonClick()
    {
//        Toast.makeText(this, "Sends a selected audio file", Toast.LENGTH_SHORT).show();
        if (inSelectionMode)
        {
            String appName = getResources().getString(R.string.app_name);

            setTitle(appName);

            shareIcon.setImageResource(R.drawable.share);
            shareText.setText("Share");
            inSelectionMode = false;

            // Hide video item tick overlay
            audioAdapter.setItemClicked(false);
            audioAdapter.notifyDataSetChanged();
        }
        else
        {
            setTitle("Select recording");
            shareIcon.setImageResource(R.drawable.cancel);
            shareText.setText("Cancel");
            inSelectionMode = true;
        }

    }

    public void playAudio(int position)
    {
        Toast.makeText(this, "Plays audio", Toast.LENGTH_SHORT).show();
    }

    public void shareAudio(int position)
    {
        Toast.makeText(this, "Shares audio file", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        inSelectionMode = false;
    }
}
