package capstone.bophelohaesoopen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import capstone.bophelohaesoopen.HaesoAPI.Controller.DatabaseUtils;
import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;
import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaLoadService;
import capstone.bophelohaesoopen.HaesoAPI.Model.Video;
import capstone.bophelohaesoopen.HaesoAPI.Controller.FileUtils;

public class MainActivity extends AppCompatActivity
{
    //region View declarations
    RelativeLayout mainMenu;
    RelativeLayout menuToggleBar;
    CardView shareMediaBar;
    RecyclerView recyclerView;
    ImageView menuToggle;
    ImageView shareIcon;
    TextView shareText;

    TextView noMediaText;
    RelativeLayout videosLoadingScreen;

    MediaLoadService mediaLoadService;

    CardView recordAudioButton;
    CardView takePictureButton;
    CardView recordingsButton;
    CardView picturesButton;
    // endregion

    public static String appRootFolder;
    public static String appImageFolder;
    public static String appRecordingsFolder;
    public static String appVideosFolder;

    //endregion

    //region Other class declarations
    VideoAdapter videoAdapter;
    FileUtils fileUtils;
    CustomGridLayoutManager gridLayoutManager;
    Video videoToSend;
    //endregion

    // region Primitives declarations
    boolean menuHidden = false;
    private static int MENU_ANIMATION_DURATION = 300;
    private static int CHECK_DURATION = 1000;
    private static int SHARE_STATE_CHECK_INTERVAL = 500;
    public boolean inSelectionMode = false;

    public boolean videosLoaded = false;

    ArrayList<Video> videoList = new ArrayList<>();

    MediaShareUserInterface mediaShareUserInterface;

    //Logging db
    public static DatabaseUtils databaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize UI elements
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
                    // Get all videos from storage
                    videoList = mediaLoadService.getVideoList();

                    // Get videos ordered  according to most watched
                    ArrayList<Video> orderedList = getOrderedVideos(videoList);

                    // Reset videoList
                    videoList = orderedList;

                    videosLoadingScreen.setVisibility(View.INVISIBLE);
                    if(!videoList.isEmpty())
                    {
                        videoAdapter.setVideos(orderedList);
                        videoAdapter.notifyDataSetChanged();
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
        //region
        appRootFolder = getResources().getString(R.string.root_folder);
        appImageFolder = getResources().getString(R.string.images_folder);
        appRecordingsFolder = getResources().getString(R.string.recordings_folder);
        appVideosFolder = getResources().getString(R.string.videos_folder);
        //endregion

        databaseUtils = new DatabaseUtils(this); // Connect to database

        LogEntry logEntry = new LogEntry(LogEntry.LogType.PAGE_VISITS, "Main Screen", null);
        if(DatabaseUtils.isDatabaseSetup())
        {
            DatabaseUtils.getInstance().addLog(logEntry);
        }

        mediaLoadService = new MediaLoadService(this, Media.MediaType.VIDEO);
        startService(new Intent(this, MediaLoadService.class));

        videosLoadingScreen = (RelativeLayout)findViewById(R.id.videosLoadingScreen);

        String identifierPrefix = getResources().getString(R.string.identifier_prefix);
        Media.setIdentifierPrefix(identifierPrefix);
        FileUtils.setFolderNames(appRootFolder,appVideosFolder,appRecordingsFolder,appImageFolder);
        fileUtils = new FileUtils(this);

        mediaShareUserInterface = new MediaShareUserInterface(getApplicationContext(), this);

        noMediaText = (TextView)findViewById(R.id.noMediaText);

        mainMenu = (RelativeLayout) findViewById(R.id.mainMenu);
        menuToggleBar = (RelativeLayout) findViewById(R.id.menuToggleBar);
        menuToggleBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                menuToggleClick();
            }
        });
        menuToggle = (ImageView) findViewById(R.id.menuToggle);
        menuToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                menuToggleClick();
            }
        });

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

        //region RecyclerView initialization

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new CustomGridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

//        populateVideoList();
        videoAdapter = new VideoAdapter(this, recyclerView, videoList);
        recyclerView.setAdapter(videoAdapter);

        //endregion

        //region Buttons initializations

        recordAudioButton = (CardView) findViewById(R.id.recordAudioButton);
        takePictureButton = (CardView) findViewById(R.id.takePictureButton);
        recordingsButton = (CardView) findViewById(R.id.recordingsButton);
        picturesButton = (CardView) findViewById(R.id.picturesButton);

        //endregion

        //region Buttons OnClickListeners

        recordAudioButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                recordAudioButtonClick();
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                takePictureButtonClick();
            }
        });
        recordingsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                recordingsButtonClick();
            }
        });

        picturesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pictureGalleryButtonClick();
            }
        });

        //endregion
    }

    // region Click handlers

    private void takePictureButtonClick()
    {
        Intent intent = new Intent(this, CameraActivity.class);
        this.startActivity(intent);
    }

    private void recordingsButtonClick()
    {
        Intent intent = new Intent(this, AudioGalleryActivity.class);
        this.startActivity(intent);
    }

    private void pictureGalleryButtonClick()
    {
        Intent intent = new Intent(this, PictureGalleryActivity.class);
        this.startActivity(intent);
    }

    private void shareMediaButtonClick()
    {
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
        String title = getResources().getString(R.string.title_select_video);
        setTitle(title);
        if (!menuHidden)
        {
            hideMenu();
        }
        shareIcon.setImageResource(R.drawable.cancel);
        String buttonText = getResources().getString(R.string.share_button_cancel_text);
        shareText.setText(buttonText);
        inSelectionMode = true;
    }

    private void hideSelectionContext()
    {
        String appName = getResources().getString(R.string.app_name);

        setTitle(appName);

        shareIcon.setImageResource(R.drawable.share);
        String buttonText = getResources().getString(R.string.share_button_share_text);
        shareText.setText(buttonText);
        inSelectionMode = false;

        // Hide video item tick overlay
        videoAdapter.setItemClicked(false);
        videoAdapter.notifyDataSetChanged();
    }

    private void recordAudioButtonClick()
    {
        Intent intent = new Intent(this, AudioRecorderActivity.class);
        this.startActivity(intent);
    }

    private void menuToggleClick()
    {
        if (!menuHidden)
        {
            hideMenu();
        }
        else
        {
            showMenu();
        }
    }

    // endregion

    private void hideMenu()
    {
        float yPos = mainMenu.getY();
        float yDelta = mainMenu.getHeight() - menuToggle.getHeight();

        ValueAnimator anim = ValueAnimator.ofFloat(yPos, yPos + yDelta);
        anim.setDuration(MENU_ANIMATION_DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                mainMenu.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();

        menuHidden = true;

        // Enable scrolling on the video list since the list is in full view
        gridLayoutManager.setScrollEnabled(true);
        menuToggle.setImageResource(R.drawable.arrow_up);

    }

    private void showMenu()
    {
        float yPos = mainMenu.getY();
        float yDelta = mainMenu.getHeight() - menuToggle.getHeight();

        ValueAnimator anim = ValueAnimator.ofFloat(yPos, yPos - yDelta);
        anim.setDuration(MENU_ANIMATION_DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                mainMenu.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();

        menuHidden = false;

        // Delay to allow the video list to scroll to the top before scrolling is disabled
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Disable scrolling so that the 4 videos at the top stay in view
                gridLayoutManager.setScrollEnabled(false);
            }
        }, 200);


        // Scroll video list to the top in case it was scrolled down, in order to show the 4 videos at the top
        recyclerView.smoothScrollToPosition(0);

        menuToggle.setImageResource(R.drawable.arrow_down);
    }

    public void shareVideo(int position)
    {
        videoToSend = videoAdapter.videoList.get(position);

        mediaShareUserInterface.sendMedia(videoToSend);

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

    // region Activity overrides

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        videosLoaded = false;
        super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
        videosLoaded = false;
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        videosLoaded = false;
        super.onResume();
    }

    // endregion

    /**
     * Populates video list with the list of videos from storage
     */
    private void populateVideoList()
    {
        videoList = (ArrayList<Video>) fileUtils.getMediaCollectionFromStorage("chw_", Video.mediaExtension);
    }

    /**
     * Starts the VideoPlayerActivity with the video at the position (in the video list) given
     *
     * @param position a position in the video list
     */
    public void playVideo(int position)
    {
        Video video = videoAdapter.videoList.get(position);
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.VIDEO_NAME, video.getName());
        intent.putExtra(VideoPlayerActivity.VIDEO_FILE_PATH, video.getFilePath());
        this.startActivity(intent);
    }

    private ArrayList<Video> getOrderedVideos(ArrayList<Video> unordered)
    {
      // List of ordered most watched videos
        ArrayList<Video> ordered = new ArrayList<>();

        // List of videos to  be removed from the unordered list since they will be added to the ordered list
        ArrayList<Video> tobeRemoved = new ArrayList<>();

        ArrayList<Video> unorderedTemp= new ArrayList<>();

        // Using a TreeMap to obtain the map objects sorted according to their values
        TreeMap<String, Integer> playFrequencies = databaseUtils.getMostPlayedVideos();

        // TreeMap<String, Integer> playFrequencies = LogEntry.getMostPlayedVideos() // or something like this

        int count = 0;

        for(String name : playFrequencies.keySet())
        {
            // Only add the 4 most watched videos first as the rest will be ordered differently
            if(count < 4)
            {
                for(Video video : unordered)
                {
                    if(name.equals(video.getFileName()))
                    {
                        ordered.add(video);
                        tobeRemoved.add(video);
                    }
                }
            }
            else
            {
                break;
            }
            count++;
        }

        // Exclude videos to be removed from unordered list
        for(Video v : unordered)
        {
            boolean found = false;
            for(Video video : tobeRemoved)
            {
                if(video.equals(v))
                {
                    found = true;
                }
            }

            if(!found)
            {
                unorderedTemp.add(v);
            }
        }

        unordered.clear();
        unordered = unorderedTemp;
        unorderedTemp = null;

        // Generate tree of alphabetically ordered video file names
        TreeSet<String> alphabeticallyOrdered = new TreeSet<>();
        for(Video video : unordered)
        {
            alphabeticallyOrdered.add(video.getFileName());
        }

        Iterator iterator = alphabeticallyOrdered.iterator();
        while(iterator.hasNext())
        {
            String current = (String)iterator.next();
            for(Video video : unordered)
            {
                if(current.equals(video.getFileName()))
                {
                    ordered.add(video);
                }
            }

        }

        return ordered;
    }
}
