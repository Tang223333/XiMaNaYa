package com.example.ximanaya;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.ximanaya.Adapter.MainContentAdapter;
import com.example.ximanaya.Adapter.indicatAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private MagicIndicator indicat;
    private ViewPager indicat_pager;
    private com.example.ximanaya.Adapter.indicatAdapter indicatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initvent();

    }

    private void initvent() {
        indicatAdapter.setOnIndicatorTapClickListener(new indicatAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(Integer index) {
                if (indicat_pager!=null){//
                    indicat_pager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView() {
        indicat = (MagicIndicator) findViewById(R.id.indicat);
        indicat_pager = (ViewPager) findViewById(R.id.indicat_pager);
        indicat.setBackgroundColor(this.getColor(R.color.main_color));
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        indicatAdapter = new indicatAdapter(this);
        commonNavigator.setAdapter(indicatAdapter);

        //创建viewpager适配器
        FragmentManager fragmentManager=getSupportFragmentManager();
        MainContentAdapter mainContentAdapter=new MainContentAdapter(fragmentManager);
        indicat_pager.setAdapter(mainContentAdapter);

        //设置内容
        indicat.setNavigator(commonNavigator);
        //ViewPager与指示器绑定到一起
        ViewPagerHelper.bind(indicat,indicat_pager);
    }

}
