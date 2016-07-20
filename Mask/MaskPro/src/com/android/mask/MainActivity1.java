package com.android.mask;

import java.util.ArrayList;

import com.android.mask.R;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.util.ToastFactory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cn.bmob.v3.Bmob;


public class MainActivity1 extends FragmentActivity{
	private ArrayList<Fragment> mFragments;
	private RadioGroup mRadioGroup;
    private ViewPager viewPager;
    private MyPagerAdapter adapter; 
    private RadioButton rB1;  
    private RadioButton rB2;  
    private RadioButton rB3;  
    private RadioButton rB4; 
    private FragmentManager fm;
    ActionSheetDialog menuDialog;
	private static boolean isExit;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bmob.initialize(this,"318faa2775adff8ebaa87f7528fe9248");
		setContentView(R.layout.activity_main1); //绑定当前layout为activity_main.xml
	    initview();
	    setListeners();
	   
	    mRadioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
	 	
  }
   public void initview()
    {
	 ViewGroup bottomGroup = (ViewGroup) findViewById(R.id.main_bottom_group);
	 mRadioGroup = (RadioGroup) bottomGroup.findViewById(R.id.include_main_bottom_tools);
	 viewPager=(ViewPager)findViewById(R.id.viewPager);
	 rB1=(RadioButton)findViewById(R.id.include_main_bottom_tool1);
	 rB2=(RadioButton)findViewById(R.id.include_main_bottom_tool2);
	 rB3=(RadioButton)findViewById(R.id.include_main_bottom_tool3);
	 rB4=(RadioButton)findViewById(R.id.include_main_bottom_tool4);
	    FragmentHall f1=new FragmentHall();
		Fragmentclassify f2=new Fragmentclassify();
		FragmentRecommend f3=new FragmentRecommend();
		Fragmenttopic f4=new Fragmenttopic();
		mFragments = new ArrayList<Fragment>();
		mFragments.add(f1);
		mFragments.add(f2);
		mFragments.add(f3);
		mFragments.add(f4);  
		adapter=new MyPagerAdapter(getSupportFragmentManager());  
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }
   class MyPagerAdapter extends FragmentPagerAdapter{  
       
       public MyPagerAdapter(FragmentManager fm) {  
           super(fm);  
       }  
       public Fragment getItem(int arg0) {  
           return mFragments.get(arg0);  
       }  
       public int getCount() {  
           return mFragments.size();  
       }  
   }  
     
   //通过监听viewpager滑动改变Checked的属性  
   @SuppressWarnings("deprecation")
private void setListeners() {  
       viewPager.setOnPageChangeListener(new OnPageChangeListener() {  
           public void onPageSelected(int position) {  
               switch (position) {  
               case 0:  
                   rB1.setChecked(true);  
                   break;  
               case 1:  
                   rB2.setChecked(true);  
                   break;  
               case 2:  
                   rB3.setChecked(true);  
                   break;  
               case 3:  
                   rB4.setChecked(true);  
                   break;  
               }  
           }  
           public void onPageScrolled(int arg0, float arg1, int arg2) {  
                 
           }  
           public void onPageScrollStateChanged(int arg0) {  
                 
           }  
       });  
   }
   @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			menuDialog.show();
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isExit) {
				isExit = true;
				ToastFactory.showToast(MainActivity1.this, "再按一次退出");
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(3000);
							isExit = false;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
				return false;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

   /**
	 * radiogroup切换监听
	 */
	private class MyOnCheckedChangeListener implements
			RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.include_main_bottom_tool1:
				viewPager.setCurrentItem(0);
				break;
			case R.id.include_main_bottom_tool2:
				viewPager.setCurrentItem(1);
				break;
			case R.id.include_main_bottom_tool3:
				viewPager.setCurrentItem(2);
				break;
			case R.id.include_main_bottom_tool4:
				viewPager.setCurrentItem(3);
				break;
			}
		}
	}
}