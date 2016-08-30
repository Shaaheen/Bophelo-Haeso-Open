package capstone.bophelohaesoopen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.support.v7.widget.AppCompatButton;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Video;
import capstone.bophelohaesoopen.HaesoAPI.FileUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //region View declarations
    RelativeLayout mainMenu;
    RelativeLayout menuToggleBar;
    RelativeLayout shareMediaBar;
    RecyclerView recyclerView;
    ImageView menuToggle;
    ImageView shareIcon;
    TextView shareText;
    NavigationView navigationView;
    DrawerLayout drawer;

    // region Button declarations
    AppCompatButton recordAudioButton;
    AppCompatButton shareMediaButton;
    AppCompatButton takePictureButton;
    AppCompatButton audioGalleryButton;
    AppCompatButton picturesButton;
    // endregion

    //endregion

    //region Other class declarations
    VideoAdapter videoAdapter;
    FileUtils fileUtils;
    CustomGridLayoutManager gridLayoutManager;
    //endregion

    // region Primitives declarations
    boolean menuHidden = false;
    private static int MENU_ANIMATION_DURATION = 300;
    boolean inSelectionMode = false;

    ArrayList<Video> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initialize();

    }

    private void initialize()
    {
        fileUtils = new FileUtils();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainMenu = (RelativeLayout)findViewById(R.id.mainMenu);
        menuToggleBar = (RelativeLayout)findViewById(R.id.menuToggleBar);
        menuToggleBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                menuToggleClick();
            }
        });
        menuToggle = (ImageView)findViewById(R.id.menuToggle);
        menuToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                menuToggleClick();
            }
        });

        shareMediaBar = (RelativeLayout)findViewById(R.id.shareMediaBar);
        shareMediaBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shareMediaButtonClick();
            }
        });
        shareIcon = (ImageView)findViewById(R.id.shareIcon);
        shareText = (TextView)findViewById(R.id.shareText);

        //region RecyclerView initialization

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new CustomGridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        populateVideoList();
        videoAdapter = new VideoAdapter(this, recyclerView, videoList);
        recyclerView.setAdapter(videoAdapter);

        //endregion

        //region Buttons initializations

        recordAudioButton = (AppCompatButton) findViewById(R.id.recordAudioButton);
        shareMediaButton = (AppCompatButton) findViewById(R.id.shareMediaButton);
        takePictureButton = (AppCompatButton) findViewById(R.id.takePictureButton);
        audioGalleryButton = (AppCompatButton) findViewById(R.id.audioGalleryButton);
        picturesButton = (AppCompatButton) findViewById(R.id.picturesButton);

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
        shareMediaButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shareMediaButtonClick();
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
        audioGalleryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                audioGalleryButtonClick();
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
        Toast.makeText(this,"Opens camera activity for user to take a picture.", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, PictureActivity.class);
//        this.startActivity(intent);
    }

    private void audioGalleryButtonClick()
    {
        Toast.makeText(this,"Opens list / gallery of recorded audio files.", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, AudioGalleryActivity.class);
//        this.startActivity(intent);
    }

    private void pictureGalleryButtonClick()
    {
        Toast.makeText(this,"Opens gallery of pictures taken.", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, PictureGalleryActivity.class);
//        this.startActivity(intent);
    }

    private void shareMediaButtonClick()
    {
//        Intent intent = new Intent(this, MediaShareActivity.class);
//        this.startActivity(intent);
        hideMenu();
        shareIcon.setImageResource(R.drawable.cancel);
        shareText.setText("Cancel");
        inSelectionMode = true;
    }

    private void recordAudioButtonClick()
    {
        Intent intent = new Intent(this, AudioRecorderActivity.class);
        this.startActivity(intent);
    }

    private void menuToggleClick()
    {
        if(!menuHidden)
        {
            hideMenu();
        }
        else
        {
            showMenu();
        }
    }

    private void hideMenu()
    {
        float yPos = mainMenu.getY();
        float yDelta = mainMenu.getHeight() - (menuToggle.getHeight() + shareMediaBar.getHeight());

        ValueAnimator anim = ValueAnimator.ofFloat(yPos, yPos+yDelta);
        anim.setDuration(MENU_ANIMATION_DURATION);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                mainMenu.setY((Float)valueAnimator.getAnimatedValue());
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
        float yDelta = mainMenu.getHeight() - (menuToggle.getHeight() + shareMediaBar.getHeight());

        ValueAnimator anim = ValueAnimator.ofFloat(yPos, yPos-yDelta);
        anim.setDuration(MENU_ANIMATION_DURATION);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                mainMenu.setY((Float)valueAnimator.getAnimatedValue());
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

    // endregion

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
        if(id == R.id.nav_view_logs)
        {
            Toast.makeText(this, "Opens activity to show list of logs", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // endregion

    /**
     * Populates video list with the list of videos from storage (Currently only prototype functionality)
     */
    private void populateVideoList()
    {
        videoList = fileUtils.getVideoCollectionFromStorage();
    }

    /**
     * Starts the VideoPlayerActivity with the video at the position (in the video list) given
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




}
