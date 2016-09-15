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

import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaShareUtils;
import capstone.bophelohaesoopen.HaesoAPI.Model.Image;
import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaLoadService;

/**
 * Activity where pictures taken are listed / shown
 */

public class PictureGalleryActivity extends AppCompatActivity
{
    CardView shareMediaBar;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;

    ImageView shareIcon;
    TextView shareText;

    RelativeLayout imagesLoadingScreen;

    ArrayList<Image> imageList = new ArrayList<>();

    MediaShareUtils mediaShareUtils;

    MediaLoadService mediaLoadService;

    TextView noMediaText;

    boolean inSelectionMode = false;
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
        mediaShareUtils = new MediaShareUtils(getApplicationContext(), this);
        mediaLoadService = new MediaLoadService(this);
        startService(new Intent(this, MediaLoadService.class));

        imagesLoadingScreen = (RelativeLayout)findViewById(R.id.imagesLoadingScreen);

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        imageAdapter = new ImageAdapter(this, recyclerView, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    private void shareMediaButtonClick()
    {
//        Toast.makeText(this, "Sends a selected picture", Toast.LENGTH_SHORT).show();
        if (inSelectionMode)
        {
            String appName = getResources().getString(R.string.app_name);

            setTitle(appName);

            shareIcon.setImageResource(R.drawable.share);
            shareText.setText("Share");
            inSelectionMode = false;

            // Hide video item tick overlay
            imageAdapter.setItemClicked(false);
            imageAdapter.notifyDataSetChanged();
        }
        else
        {
            setTitle("Select picture");
            shareIcon.setImageResource(R.drawable.cancel);
            shareText.setText("Cancel");
            inSelectionMode = true;
        }
    }

    public void shareImage(int position)
    {
        Toast.makeText(this, "Shares picture", Toast.LENGTH_SHORT).show();
    }

    public void displayImage(int position)
    {
        Toast.makeText(this, "Shows picture", Toast.LENGTH_SHORT).show();
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
