package capstone.bophelohaesoopen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Controller.DatabaseUtils;
import capstone.bophelohaesoopen.HaesoAPI.Model.Image;
import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaLoadService;
import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;

/**
 * Activity where pictures taken are listed / shown
 */

public class PictureGalleryActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;

    RelativeLayout imagesLoadingScreen;

    ArrayList<Image> imageList = new ArrayList<>();

    MediaShareUserInterface mediaShareUserInterface;

    MediaLoadService mediaLoadService;

    TextView noMediaText;

    private static int CHECK_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_gallery);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
//                    System.out.println("images loaded!");
                    imageList = mediaLoadService.getImageList();
                    imagesLoadingScreen.setVisibility(View.INVISIBLE);
                    if(!imageList.isEmpty())
                    {
                        imageAdapter.setImages(imageList);
                        imageAdapter.notifyDataSetChanged();
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

        LogEntry logEntry = new LogEntry(LogEntry.LogType.PAGE_VISITS, "Picture Gallery", null);
        if(DatabaseUtils.isDatabaseSetup())
        {
            DatabaseUtils.getInstance().addLog(logEntry);
        }

        mediaShareUserInterface = new MediaShareUserInterface(getApplicationContext(), this);
        mediaLoadService = new MediaLoadService(this);
        startService(new Intent(this, MediaLoadService.class));

        imagesLoadingScreen = (RelativeLayout)findViewById(R.id.imagesLoadingScreen);

        noMediaText = (TextView)findViewById(R.id.noMediaText);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        imageAdapter = new ImageAdapter(this, recyclerView, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    public void displayImage(int position)
    {
        Image image = imageList.get(position);
        Intent intent = new Intent(this, PictureActivity.class);
        intent.putExtra(PictureActivity.IMAGE_NAME, image.getFilePath());
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
