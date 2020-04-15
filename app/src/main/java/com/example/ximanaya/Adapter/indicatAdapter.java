package com.example.ximanaya.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.ximanaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class indicatAdapter extends CommonNavigatorAdapter {

    private final String[] titels;
    private OnIndicatorTapClickListener onIndicatorTapClickListener;

    public indicatAdapter(Context context){
        titels = context.getResources().getStringArray(R.array.indicat_name);
    }

    @Override
    public int getCount() {
        return titels==null?0:titels.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下的颜色为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //设置选中情况下的颜色为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setTextSize(18);
        //设置要显示的内容
        colorTransitionPagerTitleView.setText(titels[index]);
        //设置title的点击事件，这里的话，如果点击了title,那么就选中下面的viewPager到对应的index里面去
        //也就是说，当我们点击了title的时候，下面的viewPager会对应着index进行切换内容。
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换viewPager的内容，如果index不一样的话。
                if (onIndicatorTapClickListener!=null){
                    onIndicatorTapClickListener.onTabClick(index);
                }
            }
        });
        //把这个创建好的view返回回去
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        onIndicatorTapClickListener =listener;
    }

    public interface OnIndicatorTapClickListener{
        void onTabClick(Integer index);
    }
}
