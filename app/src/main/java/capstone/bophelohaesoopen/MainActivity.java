package capstone.bophelohaesoopen;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
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
    RelativeLayout videosLoadingScreen;

    RecyclerView recyclerView;
    ImageView menuToggle;
    ImageView shareIcon;
    TextView shareText;
    TextView noMediaText;

    CardView shareMediaBar;
    CardView recordAudioButton;
    CardView takePictureButton;
    CardView recordingsButton;
    CardView picturesButton;
    // endregion

    //region Custom class declarations
    VideoAdapter videoAdapter;
    FileUtils fileUtils;
    GridLayoutManager gridLayoutManager;
    Video videoToSend;
    MediaLoadService mediaLoadService;
    MediaShareUserInterface mediaShareUserInterface;
    private Bundle savedInstance;
    //endregion

    // region Primitives declarations
    boolean menuHidden = false;
    private static int MENU_ANIMATION_DURATION = 300;
    private static int CHECK_DURATION = 1000;
    private static int SHARE_STATE_CHECK_INTERVAL = 500;
    public boolean inSelectionMode = false;
    public boolean videosLoaded = false;

    public static String appRootFolder;
    public static String appImageFolder;
    public static String appRecordingsFolder;
    public static String appVideosFolder;
    //endregion

    ArrayList<Video> videoList = new ArrayList<>();
    Handler videoLoadHandler;
    Runnable videoLoadRunnable;

    // Logging DB
    public static DatabaseUtils databaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (verifyStoragePermission())
        {
            setUpMainActivity();
        }
    }

    private void setUpMainActivity()
    {
        // Initialize activity view components
        initializeViews();

        mediaShareUserInterface = new MediaShareUserInterface(getApplicationContext(), this);
        mediaLoadService.start();
        videoLoadHandler = new Handler();
        videoLoadRunnable = new Runnable()
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
                    recyclerView.setVisibility(View.VISIBLE);
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

                    videoLoadHandler.removeCallbacks(this);

                    stopService(new Intent(getApplicationContext(), MediaLoadService.class));
                }
                else
                {
                    videoLoadHandler.postDelayed(this, CHECK_DURATION);
                }

            }
        };
        videoLoadHandler.postDelayed(videoLoadRunnable, CHECK_DURATION);
    }

    private boolean verifyStoragePermission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
            {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

            }
            else
            {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }
        else
        {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setUpMainActivity();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                else
                {
                    Log.w("Rec","Permission denied");
                    onStop();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Initialises view components of the activity
     */
    private void initializeViews()
    {
        //region Folder Names
        appRootFolder = getResources().getString(R.string.root_folder);
        appImageFolder = getResources().getString(R.string.images_folder);
        appRecordingsFolder = getResources().getString(R.string.recordings_folder);
        appVideosFolder = getResources().getString(R.string.videos_folder);
        //endregion
        FileUtils.setFolderNames(appRootFolder,appVideosFolder,appRecordingsFolder,appImageFolder);

        databaseUtils = new DatabaseUtils(this); // Connect to database

        // Logging class instance
        LogEntry logEntry = new LogEntry(LogEntry.LogType.PAGE_VISITS, "Main Screen", null);
        if(DatabaseUtils.isDatabaseSetup())
        {
            DatabaseUtils.getInstance().addLog(logEntry);
        }

        // Start service that loads videos in the background
        mediaLoadService = new MediaLoadService(this, Media.MediaType.VIDEO);
        startService(new Intent(this, MediaLoadService.class));

        String identifierPrefix = getResources().getString(R.string.identifier_prefix);
        Media.setIdentifierPrefix(identifierPrefix);

        fileUtils = new FileUtils(this);

        videosLoadingScreen = (RelativeLayout)findViewById(R.id.videosLoadingScreen);

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
        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

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
        if(storageAvailable())
        {
            Intent intent = new Intent(this, CameraActivity.class);
            this.startActivity(intent);
        }
        else
        {
            displayUnavailableStorageDialog();
        }
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

    private void recordAudioButtonClick()
    {
        if(storageAvailable())
        {
            Intent intent = new Intent(this, AudioRecorderActivity.class);
            this.startActivity(intent);
        }
        else
        {
            displayUnavailableStorageDialog();
        }

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

    /**
     * Put the screen into share mode by showing the relevant text and icons for sharing
     */
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

    /**
     * Put the screen into normal mode by hiding the text and icons shown when sharing
     */
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



    /**
     * Slides button drawer down
     */
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
        menuToggle.setImageResource(R.drawable.arrow_up);

    }

    /**
     * Slides button drawer up
     */
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

        // Scroll video list to the top in case it was scrolled down, in order to show the 4 videos at the top
        recyclerView.smoothScrollToPosition(0);

        menuToggle.setImageResource(R.drawable.arrow_down);
    }


    /**
     * Initiates video sharing
     *
     * @param position a position of the video to be shared
     */
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
                        mediaShareUserInterface.state == MediaShareUserInterface.State.SENT)
                {
                    hideSelectionContext();
                    stateChecker.removeCallbacks(this);
                }
                else if(mediaShareUserInterface.state == MediaShareUserInterface.State.RECEIVED)
                {
                    hideSelectionContext();
                    stateChecker.removeCallbacks(this);
                    recyclerView.setVisibility(View.INVISIBLE);
                    videosLoadingScreen.setVisibility(View.VISIBLE);

                    startService(new Intent(getApplicationContext(), MediaLoadService.class));
                    mediaLoadService.start();

                    videoLoadHandler.postDelayed(videoLoadRunnable, CHECK_DURATION);
                }
                else
                {
                    stateChecker.postDelayed(this, SHARE_STATE_CHECK_INTERVAL);
                }
            }
        };
        stateChecker.postDelayed(checkState, 0);
    }

    /**
     * Starts the VideoPlayerActivity with the video at the position (in the video list) given
     *
     * @param position position of the video to be played
     */
    public void playVideo(int position)
    {
        Video video = videoAdapter.videoList.get(position);
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.VIDEO_NAME, video.getName());
        intent.putExtra(VideoPlayerActivity.VIDEO_FILE_PATH, video.getFilePath());
        this.startActivity(intent);
    }

    /**
     * Takes in an unordered list of videos and returns a list
     * of videos ordered according to the 4 most watched
     *
     * @param unordered a list of arbitrarily ordered videos
     */
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

    private boolean storageAvailable()
    {
        boolean available = true;
        long freeBytes = new File(getExternalFilesDir(null).toString()).getFreeSpace();
        long freeMegs = freeBytes / (1024 * 1024);

        if(freeMegs < 8)
        {
            available = false;
        }

        return available;
    }

    private void displayUnavailableStorageDialog()
    {
        AlertDialog.Builder storageAlert = new AlertDialog.Builder(this);;
        String storageAlertMessage = getResources().getString(R.string.storage_alert_message);
        String storageAlertTitle = getResources().getString(R.string.storage_alert_title);
        String okText = getResources().getString(R.string.positive_action_button_text);
        storageAlert.setTitle(storageAlertTitle);
        storageAlert.setMessage(storageAlertMessage);
        storageAlert.setPositiveButton(okText, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        storageAlert.show();
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
}
