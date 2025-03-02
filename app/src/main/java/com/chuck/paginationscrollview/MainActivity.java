package com.chuck.paginationscrollview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;

import com.chuck.paginationscrollview.bean.ItemInfo;
import com.chuck.paginationscrollview.builder.PaginationProfile;
import com.chuck.paginationscrollview.dragndrop.DragLayer;
import com.chuck.paginationscrollview.view.PaginationScrollView;
import com.chuck.paginationscrollview.view.Workspace;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
                .setHeightPx(1200)
                .setWidthPx(800)
                .setCellHeightPx(100)
                .setCellWidthPx(60)
                .setNumColumns(column)
                .setNumRows(row)
                .setIconDrawablePaddingPx(8)
                .setDefaultPageSpacingPx(10)
                .setEdgeMarginPx(8)
                .setIconSizePx(48)
                .setWorkspacePadding(new Rect(0, 0, 0, 0))
                .build();

        setContentView(R.layout.activity_main);

        PaginationScrollView paginationScrollView = findViewById(R.id.pagination_scroll_view);
        DragLayer dragLayer = findViewById(R.id.drag_layer);
        paginationScrollView.setDragLayer(dragLayer);
        Workspace workspace = findViewById(R.id.workspace);
        paginationScrollView.setWorkspace(workspace);

        paginationScrollView.setPaginationProfile(paginationProfile);

        int total = row * column;

        List<ItemInfo> itemInfos = new ArrayList<>();
        for (int i = 0; i < fruits.length; i++) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.iconDrawable = getDrawable(fruits[i]);
            itemInfo.title = "水果" + i;
            itemInfo.spanX = 1;
            itemInfo.spanY = 1;
            itemInfo.cellX = i / column;
            itemInfo.cellY = i % column;
            itemInfo.screenId = i / total;
            itemInfos.add(itemInfo);
        }
        paginationScrollView.bindItems(itemInfos, true);
    }
}