package capstone.bophelohaesoopen;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * Created by Omondi on 14 Aug 2016.
 */
public class CustomGridLayoutManager extends GridLayoutManager
{
    private boolean scrollEnabled = false;

    public CustomGridLayoutManager(Context context, int spanCount)
    {
        super(context, spanCount);
    }

    public void setScrollEnabled(boolean enabled)
    {
        scrollEnabled = enabled;
    }

    @Override
    public boolean canScrollVertically()
    {
        return scrollEnabled;
    }
}
