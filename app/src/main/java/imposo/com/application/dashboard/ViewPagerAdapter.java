package imposo.com.application.dashboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	CharSequence Titles[];
	int NumbOfTabs;

	public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
		super(fm);
		this.Titles = mTitles;
		this.NumbOfTabs = mNumbOfTabsumb;
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			AllFeedsFragment tab1 = new AllFeedsFragment();
			return tab1;
		} else if(position == 1){
			MyFeedFragment tab2 = new MyFeedFragment();
			return tab2;
		}else{
			AllFeedsFragment tab1 = new AllFeedsFragment();
			return tab1;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return Titles[position];
	}

	@Override
	public int getCount() {
		return NumbOfTabs;
	}
}
