package com.chuck.paginationscrollview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chuck.paginationscrollview.R;
import com.chuck.paginationscrollview.annotation.Thunk;
import com.chuck.paginationscrollview.bean.ItemInfo;
import com.chuck.paginationscrollview.builder.PageIndicatorProfile;
import com.chuck.paginationscrollview.builder.PaginationProfile;
import com.chuck.paginationscrollview.dragndrop.DragController;
import com.chuck.paginationscrollview.dragndrop.DragLayer;
import com.chuck.paginationscrollview.helper.ViewGroupFocusHelper;
import com.chuck.paginationscrollview.interfaces.ItemClickHandler;
import com.chuck.paginationscrollview.interfaces.ItemInfoChangedCallBack;
import com.chuck.paginationscrollview.util.LauncherAnimUtils;
import com.chuck.paginationscrollview.util.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * As library entrance,you can define you child in this view,include workspace and someone else.
 *
 * @author Chuck
 * @date 2025/3/1
 * @description
 */
public class PaginationScrollView extends FrameLayout {

    private final String TAG = "PaginationScrollView";

    private static PaginationScrollView instance;

    private PageIndicatorProfile pageIndicatorProfile;

    private PaginationProfile paginationProfile;

    private DragController dragController;

    private DragLayer dragLayer;

    private Workspace workspace;

    public ViewGroupFocusHelper mFocusHandler;

    private static final int NEW_APPS_PAGE_MOVE_DELAY = 500;
    private static final int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
    @Thunk
    static final int NEW_APPS_ANIMATION_DELAY = 500;

    private int mDragLayerViewId;

    private int mWorkspaceViewId;

    public PaginationScrollView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PaginationScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaginationScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void initChildViews(Workspace workspace, DragLayer dragLayer) {
//        TypedArray a = getContext().obtainStyledAttributes(null,
//                R.styleable.PaginationScrollView, 0, 0);
//        mDragLayerViewId = a.getResourceId(R.styleable.PaginationScrollView_dragLayer, -1);
//        mWorkspaceViewId = a.getResourceId(R.styleable.PaginationScrollView_workspace, -1);
//        LogUtils.d(TAG, "mDragLayerViewId: " + mDragLayerViewId + " mWorkspaceViewId: " + mWorkspaceViewId);
//        a.recycle();
//        workspace = (Workspace) findViewById(mWorkspaceViewId);
//        dragLayer = (DragLayer) findViewById(mDragLayerViewId);
        this.workspace = workspace;
        this.dragLayer = dragLayer;
        workspace.initParentViews(dragLayer);
        dragLayer.setup(dragController, workspace);
        workspace.setup(dragController);

        workspace.bindAndInitFirstWorkspaceScreen(null /* recycled qsb */);
        dragController.addDragListener(workspace);

        mFocusHandler = dragLayer.getFocusIndicatorHelper();
    }

    public PaginationScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        instance = this;
        dragController = new DragController(this);
    }

    public static PaginationScrollView getInstance() {
        return instance;
    }

    public void setPageIndicatorProfile(PageIndicatorProfile pageIndicatorProfile) {
        this.pageIndicatorProfile = pageIndicatorProfile;
    }

    public PageIndicatorProfile getPageIndicatorProfile() {
        return this.pageIndicatorProfile;
    }

    public void setPaginationProfile(PaginationProfile paginationProfile) {
        this.paginationProfile = paginationProfile;
    }

    public PaginationProfile getPaginationProfile() {
        return this.paginationProfile;
    }

    public DragLayer getDragLayer() {
        return dragLayer;
    }

    public DragController getDragController() {
        return dragController;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public boolean isWorkspaceLoading() {
        return false;
    }

    private static int viewId = 123;

    public int getViewIdForItem(ItemInfo info) {
        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
        // This cast is safe as long as the id < 0x00FFFFFF
        // Since we jail all the dynamically generated views, there should be no clashes
        // with any other views.
        return ++viewId;
    }

    private ItemClickHandler itemClickHandler;

    public void setItemClickHandler(ItemClickHandler itemClickHandler) {
        this.itemClickHandler = itemClickHandler;
    }

    public View createShortcut(ViewGroup parent, ItemInfo info) {
        BubbleTextView favorite = (BubbleTextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_icon, parent, false);
        favorite.setTextColor(paginationProfile.getCellTextColor());
        favorite.applyFromItemInfo(info);
        favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickHandler != null) {
                    itemClickHandler.onClick(v, info);
                }
            }
        });
        favorite.setOnFocusChangeListener(mFocusHandler);
        return favorite;
    }

    /**
     * Returns the CellLayout of the specified container at the specified screen.
     */
    public CellLayout getCellLayout(long screenId) {
        return workspace.getScreenWithId(screenId);
    }

    public void setItemInfoChangedCallBack(ItemInfoChangedCallBack itemInfoChangedCallBack) {
        workspace.setItemInfoChangedCallBack(itemInfoChangedCallBack);
    }

    private boolean canRunNewAppsAnimation() {
        long diff = System.currentTimeMillis() - dragController.getLastGestureUpTime();
        return diff > (NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS * 1000);
    }

    View createShortcut(ItemInfo info) {
        return createShortcut((ViewGroup) workspace.getChildAt(workspace.getCurrentPage()), info);
    }

    private static final float BOUNCE_ANIMATION_TENSION = 1.3f;
    public static final int NEW_SHORTCUT_BOUNCE_DURATION = 450;
    public static final int NEW_SHORTCUT_STAGGER_DELAY = 85;

    private ValueAnimator createNewAppBounceAnimation(View v, int i) {
        ValueAnimator bounceAnim = LauncherAnimUtils.ofViewAlphaAndScale(v, 1, 1, 1);
        bounceAnim.setDuration(NEW_SHORTCUT_BOUNCE_DURATION);
        bounceAnim.setStartDelay(i * NEW_SHORTCUT_STAGGER_DELAY);
        bounceAnim.setInterpolator(new OvershootInterpolator(BOUNCE_ANIMATION_TENSION));
        return bounceAnim;
    }

    private void bindAddScreens(int pageCount) {
        for (int i = 1; i < pageCount; i++) {
            long screenId = i;
            LogUtils.d(TAG, "bindAddScreens: " + screenId);
            workspace.insertNewWorkspaceScreenBeforeEmptyScreen(screenId);
        }
    }

    public void bindItems(final List<ItemInfo> items, final boolean forceAnimateIcons) {
        int sizePerPage = paginationProfile.getNumColumns() * paginationProfile.getNumRows();
        int pageCount = (items.size() + sizePerPage - 1) / sizePerPage;
        LogUtils.d(TAG, "bindItems pageCount: " + pageCount);
        bindAddScreens(pageCount);
        // Get the list of added items and intersect them with the set of items here
        final AnimatorSet anim = LauncherAnimUtils.createAnimatorSet();
        final Collection<Animator> bounceAnims = new ArrayList<>();
        final boolean animateIcons = forceAnimateIcons && canRunNewAppsAnimation();
        long newItemsScreenId = -1;
        int end = items.size();
        for (int i = 0; i < end; i++) {
            final ItemInfo item = items.get(i);

            final View view = createShortcut(item);

            CellLayout cl = workspace.getScreenWithId(item.screenId);
            if (cl != null && cl.isOccupied(item.cellX, item.cellY)) {
                View v = cl.getChildAt(item.cellX, item.cellY);
                Object tag = v.getTag();
                String desc = "Collision while binding workspace item: " + item
                        + ". Collides with " + tag;
                if (workspace.getItemInfoChangedCallBack() != null) {
                    workspace.getItemInfoChangedCallBack().itemPositionConflict(item);
                    LogUtils.d(TAG, desc);
                    continue;
                }
            }

            workspace.addInScreenFromBind(view, item);
            if (animateIcons) {
                // Animate all the applications up now
                view.setAlpha(0f);
                view.setScaleX(0f);
                view.setScaleY(0f);
                bounceAnims.add(createNewAppBounceAnimation(view, i));
                newItemsScreenId = item.screenId;
            }
        }

        if (animateIcons) {
            // Animate to the correct page
            if (newItemsScreenId > -1) {
                long currentScreenId = workspace.getScreenIdForPageIndex(workspace.getNextPage());
                final int newScreenIndex = workspace.getPageIndexForScreenId(newItemsScreenId);
                final Runnable startBounceAnimRunnable = new Runnable() {
                    public void run() {
                        anim.playTogether(bounceAnims);
                        anim.start();
                    }
                };
                if (newItemsScreenId != currentScreenId) {
                    // We post the animation slightly delayed to prevent slowdowns
                    // when we are loading right after we return to launcher.
                    workspace.postDelayed(new Runnable() {
                        public void run() {
                            if (workspace != null) {
                                workspace.snapToPage(newScreenIndex);
                                workspace.postDelayed(startBounceAnimRunnable,
                                        NEW_APPS_ANIMATION_DELAY);
                            }
                        }
                    }, NEW_APPS_PAGE_MOVE_DELAY);
                } else {
                    workspace.postDelayed(startBounceAnimRunnable, NEW_APPS_ANIMATION_DELAY);
                }
            }
        }
        workspace.requestLayout();
    }

    public boolean removeItem(View v, final ItemInfo itemInfo) {
        workspace.removeWorkspaceItem(v);
        return true;
    }


}
