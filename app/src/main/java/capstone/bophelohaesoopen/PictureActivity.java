package capstone.bophelohaesoopen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;


/**
 * Activity for viewing single photos
 */

public class PictureActivity extends AppCompatActivity
{
    public static String IMAGE_NAME = "Image name";
    ImageView imageView;
    String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        imagePath = intent.getStringExtra(IMAGE_NAME);

        imageView = (ImageView)findViewById(R.id.imageView);

        Bitmap image = BitmapFactory.decodeFile(imagePath);

        imageView.setImageBitmap(image);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
