package capstone.bophelohaesoopen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Activity where recorded audio files are listed / shown
 * */

public class AudioGalleryActivity extends AppCompatActivity
{
    RelativeLayout shareMediaBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_gallery);

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
        Toast.makeText(this, "Sends a selected audio file", Toast.LENGTH_SHORT).show();
    }
}
