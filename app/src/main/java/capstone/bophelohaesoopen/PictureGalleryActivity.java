package capstone.bophelohaesoopen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Activity where pictures taken are listed / shown
 * NB: Not implemented in the prototype
 */

public class PictureGalleryActivity extends AppCompatActivity
{

    RelativeLayout shareMediaBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_gallery);

        shareMediaBar = (RelativeLayout) findViewById(R.id.shareMediaBar);
        shareMediaBar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shareMediaButtonClick();
            }
        });
    }

    private void shareMediaButtonClick()
    {
        Toast.makeText(this, "Sends a selected picture", Toast.LENGTH_SHORT).show();
    }
}
