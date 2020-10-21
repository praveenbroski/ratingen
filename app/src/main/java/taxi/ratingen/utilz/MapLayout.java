package taxi.ratingen.utilz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import taxi.ratingen.ui.drawerscreen.mapscreen.MapFragmentViewModel;
import taxi.ratingen.ui.drawerscreen.mapscrn.MapScrnViewModel;

public class MapLayout extends FrameLayout {
    public MapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public MapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MapLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                MapScrnViewModel.mMapIsTouched = true;
                MapFragmentViewModel.mMapIsTouched = true;
                break;
            case MotionEvent.ACTION_UP:
                MapScrnViewModel.mMapIsTouched = false;
                MapFragmentViewModel.mMapIsTouched = false;
                break;
        }

        return super.dispatchTouchEvent(ev);

    }


}