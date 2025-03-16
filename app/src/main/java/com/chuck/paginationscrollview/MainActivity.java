package com.chuck.paginationscrollview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;

import com.chuck.paginationscrollview.bean.ItemInfo;
import com.chuck.paginationscrollview.builder.PaginationProfile;
import com.chuck.paginationscrollview.dragndrop.DragLayer;
import com.chuck.paginationscrollview.view.PaginationScrollView;
import com.chuck.paginationscrollview.view.Workspace;

import com.chuck.paginationscrollview.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private final int[] fruits = new int[]{
            R.drawable.boluo,
            R.drawable.caomei,
            R.drawable.hamigua,
            R.drawable.huolongguo,
            R.drawable.lanmei,
            R.drawable.li,
            R.drawable.mangguo,
            R.drawable.mihoutao,
            R.drawable.ningmeng,
            R.drawable.pingguo,
            R.drawable.putao,
            R.drawable.shiliu,
            R.drawable.shizi,
            R.drawable.taozi,
            R.drawable.xiangjiao,
            R.drawable.xigua,
            R.drawable.yangmei,
            R.drawable.yezi,
            R.drawable.yingtao,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int row = 3;
        final int column = 5;

        PaginationProfile paginationProfile = (PaginationProfile) new PaginationProfile.Builder()
                .setHeightPx(750)
                .setWidthPx(360)
                .setCellHeightPx(48)
                .setCellWidthPx(48)
                .setNumColumns(column)
                .setNumRows(row)
                .setIconDrawablePaddingPx(2)
                .setDefaultPageSpacingPx(2)
                .setEdgeMarginPx(2)
                .setIconSizePx(36)
                .setIconTextSizePx(16)
                .setCellTextColor(getColor(R.color.black))
                .setWorkspacePadding(new Rect(0, 0, 0, 0))
                .build();

        setContentView(R.layout.activity_main);

        PaginationScrollView paginationScrollView = findViewById(R.id.pagination_scroll_view);
        paginationScrollView.setPaginationProfile(paginationProfile);
        paginationScrollView.initChildViews(findViewById(R.id.workspace), findViewById(R.id.drag_layer));

        int sizePerPage = row * column;

        List<ItemInfo> itemInfos = new ArrayList<>();
        int currentRow = -1;
        for (int i = 0; i < fruits.length; i++) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.iconDrawable = getDrawable(fruits[i]);
            itemInfo.title = "水果" + i;
            itemInfo.spanX = 1;
            itemInfo.spanY = 1;

            int currentItemIndexInPage = i % sizePerPage;
            if (currentItemIndexInPage == 0) {
                currentRow = 0;
            }

            //列
            itemInfo.cellX = currentItemIndexInPage % column;
            LogUtils.d(TAG, "cellX: " + itemInfo.cellX);
            if (currentItemIndexInPage != 0 && currentItemIndexInPage % column == 0) {
                currentRow++;
            }
            //行
            itemInfo.cellY = currentRow;
            itemInfo.screenId = i / sizePerPage;
            itemInfos.add(itemInfo);
            LogUtils.d(TAG, "itemInfo: " + itemInfo);
        }
        paginationScrollView.bindItems(itemInfos, false);
    }
}