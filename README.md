# PaginationScrollView(Lite-Launcher3)
This repository bases on Launcher3,only include some viewgroups,such as LauncherRootView(PaginationScrollView),Workspace,CellLayout and so on.The destination is to build a lite launcher3 to complete some function below:

- 1. Items display in grid layout and each grid layout in a page,
- 2. Each page can be scrolled left and right by finger,
- 3. Single item can be dragged to another page,
- 4. Other item will be re-range when finger drag one item,

# WHY
RecylerView may fullfill these function,but you must customize LayoutManager and related classes.In our company's project,we want to display all installed applications icon in a list,so we develop launcher3 to accomplish all requirements.As you know,Launcher3 is a complex and comlicated project,importing such a big monster for some requirements is worthless.

# HOW

Deleting database operation temporarily,and it will be included in a library module in my plan.

Deleting folder and widget icons in workspace temporarily,and it will be added back when application-icon functions are stable.

Deleting Launcher class and turn it to PaginationScrollView.

# Implementation

Implementation two aars first.

## 1.Define rules

```java
// grid rows
final int row = 3;
//grid columns
final int column = 5;

PaginationProfile paginationProfile = (PaginationProfile) new PaginationProfile.Builder()
        .setHeightPx(750)//grid height
        .setWidthPx(360)//grid width
        .setCellHeightPx(48)//item height
        .setCellWidthPx(48)//item width
        .setNumColumns(column)//grid columns
        .setNumRows(row)//grid rows
        .setIconDrawablePaddingPx(2)//item icon in a textview and set padding between icon and text
        .setDefaultPageSpacingPx(2)//page spacing
        .setEdgeMarginPx(2)//page edge margin
        .setIconSizePx(36)//item icon in a textview and set icon size
        .setIconTextSizePx(16)//item icon in a textview and set text size
        .setCellTextColor(getColor(R.color.black))//item icon in a textview and set text color
        .setWorkspacePadding(new Rect(0, 0, 0, 0))//workspace padding
        .build();
```

## 2. Create PaginationScrollView

```xml
<?xml version="1.0" encoding="utf-8"?>
    <com.chuck.paginationscrollview.view.PaginationScrollView xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:launcher="http://schemas.android.com/apk/res-auto"
            android:id="@+id/pagination_scroll_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:fitsSystemWindows="true"
            launcher:dragLayer="@+id/drag_layer"
            launcher:workspace="@+id/workspace">
    
        <com.chuck.paginationscrollview.dragndrop.DragLayer
                android:id="@+id/drag_layer"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:clipChildren="false"
                android:clipToPadding="false"
                android:importantForAccessibility="no">
        
        <!-- The workspace contains 5 screens of cells -->
        <!-- DO NOT CHANGE THE ID -->
            <com.chuck.paginationscrollview.view.Workspace
                    android:id="@+id/workspace"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:layout_gravity="center"
                    android:theme="@style/HomeScreenElementTheme"
                    launcher:pageIndicator="@+id/page_indicator" />
        
            <!-- Keep these behind the workspace so that they are not visible when
                    we go into AllApps -->
            <com.chuck.paginationscrollview.pageindicators.WorkspacePageIndicator
                    android:id="@+id/page_indicator"
                    android:layout_width="match_parent"
                    android:layout_height="@dimen/vertical_drag_handle_size"
                    android:layout_gravity="bottom|center_horizontal"
                    android:theme="@style/HomeScreenElementTheme" />
        </com.chuck.paginationscrollview.dragndrop.DragLayer>

</com.chuck.paginationscrollview.view.PaginationScrollView>
```

define views in xml file,and similar to original launcher3.

## 3. Generate your own data

```java
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
    
    //column
    itemInfo.cellX = currentItemIndexInPage % column;
    LogUtils.d(TAG, "cellX: " + itemInfo.cellX);
    if (currentItemIndexInPage != 0 && currentItemIndexInPage % column == 0) {
        currentRow++;
    }
    //row
    itemInfo.cellY = currentRow;
    itemInfo.screenId = i / sizePerPage;
    itemInfos.add(itemInfo);
    LogUtils.d(TAG, "itemInfo: " + itemInfo);
}
```

## 4.BindItems

```java
paginationScrollView.bindItems(itemInfos, false);
```