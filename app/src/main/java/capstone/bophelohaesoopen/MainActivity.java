package capstone.bophelohaesoopen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Video;
import capstone.bophelohaesoopen.HaesoAPI.FileUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //region View declarations
    RelativeLayout mainMenu;
    RecyclerView recyclerView;
    ImageView menuToggle;
    NavigationView navigationView;
    DrawerLayout drawer;

    // region Button declarations
    AppCompatButton recordAudioButton;
    AppCompatButton shareMediaButton;
    AppCompatButton takePictureButton;
    AppCompatButton audioGalleryButton;
    AppCompatButton videoGalleryButton;
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
    private static int MENU_ANIMATION_DURATION = 150;

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
        fileUtils = new FileUtils(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        System.out.println("CREATED");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainMenu = (RelativeLayout)findViewById(R.id.mainMenu);
        menuToggle = (ImageView)findViewById(R.id.menuToggle);
        menuToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                menuToggleClick();
            }
        });

        //region RecyclerView initialization

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new CustomGridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        populateVideoList();
        videoAdapter = new VideoAdapter(this, videoList);
        recyclerView.setAdapter(videoAdapter);

        //endregion

        //region Buttons initializations

        recordAudioButton = (AppCompatButton) findViewById(R.id.recordAudioButton);
        shareMediaButton = (AppCompatButton) findViewById(R.id.shareMediaButton);
        takePictureButton = (AppCompatButton) findViewById(R.id.takePictureButton);
        audioGalleryButton = (AppCompatButton) findViewById(R.id.audioGalleryButton);
        videoGalleryButton = (AppCompatButton) findViewById(R.id.videoGalleryButton);
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
        videoGalleryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                videoGalleryButtonClick();
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
        Intent intent = new Intent(this, PictureActivity.class);
        this.startActivity(intent);
    }

    private void videoGalleryButtonClick()
    {
//        Toast.makeText(getApplicationContext(),"Playing Video",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, VideoGalleryActivity.class);
        this.startActivity(intent);


    }

    private void audioGalleryButtonClick()
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
        Intent intent = new Intent(this, MediaShareActivity.class);
        this.startActivity(intent);
    }

    private void recordAudioButtonClick()
    {
        Intent intent = new Intent(this, AudioRecorderActivity.class);
        this.startActivity(intent);
    }

    private void menuToggleClick()
    {
        System.out.println("Menu toggle clicked!");
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
        float yDelta = mainMenu.getHeight() - menuToggle.getHeight();

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
        gridLayoutManager.setScrollEnabled(true);

    }

    private void showMenu()
    {
        float yPos = mainMenu.getY();
        float yDelta = mainMenu.getHeight() - menuToggle.getHeight();

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
        gridLayoutManager.setScrollEnabled(false);

    }

    // endregion

    // region Activity overrides

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // endregion

    private void populateVideoList()
    {
        videoList = fileUtils.getVideoCollectionFromStorage();
    }

    public void playVideo(int position)
    {
        Video video = videoList.get(position);
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.VIDEO_NAME, video.getName());
        intent.putExtra(VideoPlayerActivity.VIDEO_NAME, video.getFilePath());
        this.startActivity(intent);

    }


}
