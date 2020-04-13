package com.example.ximanaya;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "fasdfasd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
                List<Category> categories=categoryList.getCategories();
                Log.d(TAG, "onSuccess: "+categories.size());
                for (int i = 0; i < categories.size(); i++) {
                    Category category=categories.get(i);
                    Log.d(TAG, "onSuccess: "+category.getId()+"   "+category.getKind()+"   "+category.getCoverUrlSmall()+"   "+category.getCoverUrlMiddle()+"   "+category.getCoverUrlLarge()+"   "+category.getCategoryName());
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: "+i+"     "+s);//
            }
        });
    }

    public static void getCategories(final Map<String,String> specificParams,
                                     final IDataCallBack<CategoryList> callback){

    }
}
