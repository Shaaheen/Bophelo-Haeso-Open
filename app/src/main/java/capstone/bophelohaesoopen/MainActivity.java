package capstone.bophelohaesoopen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        implements NavigationView.OnNavigationItemSelectedListener
{
    //region View declarations
    RelativeLayout mainMenu;
    RelativeLayout menuToggleBar;
    CardView shareMediaBar;
    RecyclerView recyclerView;
    ImageView menuToggle;
    ImageView shareIcon;
    TextView shareText;
    NavigationView navigationView;
    DrawerLayout drawer;

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
    boolean firstRun = true;
    private static int MENU_ANIMATION_DURATION = 300;
    private static int CHECK_DURATION = 1000;
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
        long time = System.currentTimeMillis();

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
//                    System.out.println("videos loaded!");
                    videoList = mediaLoadService.getVideoList();
                    videosLoadingScreen.setVisibility(View.INVISIBLE);
                    if(!videoList.isEmpty())
                    {
                        videoAdapter.setVideos(videoList);
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
        long endtime = System.currentTimeMillis();
        System.out.println("Start up time = "+((endtime - time)/1000));


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

        mediaLoadService = new MediaLoadService(this);
        startService(new Intent(this, MediaLoadService.class));

        videosLoadingScreen = (RelativeLayout)findViewById(R.id.videosLoadingScreen);

        String identifierPrefix = getResources().getString(R.string.identifier_prefix);
        Media.setIdentifierPrefix(identifierPrefix);
        fileUtils = new FileUtils(this);

        mediaShareUserInterface = new MediaShareUserInterface(getApplicationContext(), this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        noMediaText = (TextView)findViewById(R.id.noMediaText);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
//        Toast.makeText(this, "Opens camera to take picture.", Toast.LENGTH_SHORT).show();

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
            String appName = getResources().getString(R.string.app_name);

            setTitle(appName);

            shareIcon.setImageResource(R.drawable.share);
            shareText.setText("Share");
            inSelectionMode = false;

            // Hide video item tick overlay
            videoAdapter.setItemClicked(false);
            videoAdapter.notifyDataSetChanged();
        }
        else
        {
            setTitle("Select video");
            if (!menuHidden)
            {
                hideMenu();
            }
            shareIcon.setImageResource(R.drawable.cancel);
            shareText.setText("Cancel");
            inSelectionMode = true;
        }
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
        } else
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
        videoToSend = videoList.get(position);

        mediaShareUserInterface.sendMedia(videoToSend);
    }

    // region Activity overrides

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            // Close navigation drawer
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks
        int id = item.getItemId();
        if (id == R.id.nav_view_logs)
        {
            Toast.makeText(this, "Opens activity to show list of logs", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        Video video = videoList.get(position);
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.VIDEO_NAME, video.getName());
        intent.putExtra(VideoPlayerActivity.VIDEO_FILE_PATH, video.getFilePath());
        this.startActivity(intent);
    }

    private ArrayList<Video> getOrderedVideos(ArrayList<Video> unordered)
    {
        ArrayList<Video> ordered = new ArrayList<>();

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
                        unordered.remove(video);
                    }
                }
            }
            else
            {
                break;
            }
            count++;
        }

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
