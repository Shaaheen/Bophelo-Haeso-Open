package capstone.bophelohaesoopen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaShareUtils;
import capstone.bophelohaesoopen.HaesoAPI.Model.Audio;
import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaLoadService;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;

/**
 * Activity where recorded audio files are listed / shown
 * */

public class AudioGalleryActivity extends AppCompatActivity
{
    CardView shareMediaBar;
    RecyclerView recyclerView;
    AudioAdapter audioAdapter;

    RelativeLayout audioLoadingScreen;

    ArrayList<Audio> audioList = new ArrayList<>();

    MediaShareUserInterface mediaShareUserInterface;

    MediaLoadService mediaLoadService;

    TextView noMediaText;

    ImageView shareIcon;
    TextView shareText;

    boolean inSelectionMode = false;
    private static int CHECK_DURATION = 1000;
    private static int SHARE_STATE_CHECK_INTERVAL = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_gallery);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            Log.i("APP", "Action bar null");
        }

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
//                    System.out.println("recordings_black loaded!");
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
        mediaShareUserInterface = new MediaShareUserInterface(getApplicationContext(), this);
        mediaLoadService = new MediaLoadService(this, Media.MediaType.AUDIO);
        startService(new Intent(this, MediaLoadService.class));

        audioLoadingScreen = (RelativeLayout)findViewById(R.id.audioLoadingScreen);

        shareMediaBar = (CardView) findViewById(R.id.shareMediaBar);
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
            hideSelectionContext();
        }
        else
        {
            showSelectionContext();
        }

    }

    private void showSelectionContext()
    {
        setTitle("Select recording");
        shareIcon.setImageResource(R.drawable.cancel);
        shareText.setText("Cancel");
        inSelectionMode = true;
    }

    private void hideSelectionContext()
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

    public void playAudio(int position)
    {
        Audio audio = audioList.get(position);
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(AudioPlayerActivity.AUDIO_NAME, audio.getName());
        intent.putExtra(AudioPlayerActivity.AUDIO_FILE_PATH, audio.getFilePath());
        this.startActivity(intent);
    }

    public void shareAudio(int position)
    {
        Audio audioToSend = audioAdapter.audioList.get(position);

        mediaShareUserInterface.sendMedia(audioToSend);
        Runnable checkState;
        final Handler stateChecker;
        stateChecker = new Handler();
        checkState = new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaShareUserInterface.state == MediaShareUserInterface.State.FAILED ||
                        mediaShareUserInterface.state == MediaShareUserInterface.State.CANCELLED ||
                        mediaShareUserInterface.state == MediaShareUserInterface.State.SENDING_COMPLETE)
                {
                    hideSelectionContext();
                    stateChecker.removeCallbacks(this);
                }
                else
                {
                    stateChecker.postDelayed(this, SHARE_STATE_CHECK_INTERVAL);
                }
            }
        };
        stateChecker.postDelayed(checkState, 0);
    }

    @Override
    protected void onDestroy()
    {
        mediaShareUserInterface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        inSelectionMode = false;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}

