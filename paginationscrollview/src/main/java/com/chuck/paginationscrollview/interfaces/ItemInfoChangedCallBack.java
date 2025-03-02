package com.chuck.paginationscrollview.interfaces;

import com.chuck.paginationscrollview.bean.ItemInfo;

public interface ItemInfoChangedCallBack {
    void itemInfoChanged(ItemInfo info);

    void itemPositionConflict(ItemInfo info);
}
