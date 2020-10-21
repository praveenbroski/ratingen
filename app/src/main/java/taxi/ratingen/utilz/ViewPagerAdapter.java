package taxi.ratingen.utilz;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import taxi.ratingen.ui.tour.IntroFragment;
import taxi.ratingen.ui.tour.LiveRide;
import taxi.ratingen.ui.tour.TripSharing;
import taxi.ratingen.ui.tour.VehSelection;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IntroFragment.newInstance(); //ChildFragment1 at position 0
            case 1:
                return VehSelection.newInstance(); //ChildFragment2 at position 1
            case 2:
                return LiveRide.newInstance(); //ChildFragment3 at position 2
            case 3:
                return TripSharing.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}