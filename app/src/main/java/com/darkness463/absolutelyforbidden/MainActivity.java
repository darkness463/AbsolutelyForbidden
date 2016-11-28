package com.darkness463.absolutelyforbidden;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.darkness463.absolutelyforbidden.activity.AboutActivity;
import com.darkness463.absolutelyforbidden.activity.BaseActivity;
import com.darkness463.absolutelyforbidden.adapt.ViewPagerAdapter;
import com.darkness463.absolutelyforbidden.common.asynctask.LoadAppTask;
import com.darkness463.absolutelyforbidden.common.concurrent.HandlerExecutor;
import com.darkness463.absolutelyforbidden.common.context.ApplicationContext;
import com.darkness463.absolutelyforbidden.common.event.DisableAllEvent;
import com.darkness463.absolutelyforbidden.common.event.ItemCancelEvent;
import com.darkness463.absolutelyforbidden.common.event.ItemSelectEvent;
import com.darkness463.absolutelyforbidden.common.event.PageChangedEvent;
import com.darkness463.absolutelyforbidden.common.event.ScrollEvent;
import com.darkness463.absolutelyforbidden.common.log.MyLog;
import com.darkness463.absolutelyforbidden.common.utils.DbUtil;
import com.darkness463.absolutelyforbidden.common.utils.PxUtil;
import com.darkness463.absolutelyforbidden.common.utils.SharedPrefsUtil;
import com.darkness463.absolutelyforbidden.common.utils.ShellUtil;
import com.darkness463.absolutelyforbidden.fragment.AppListFragment;
import com.darkness463.absolutelyforbidden.fragment.RecyclerViewScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity {

    public static final int FAB_TYPE_DISABLE = 0;
    public static final int FAB_TYPE_ADD = 1;

    public static final int FAB_MOVE_HOR = 0;
    public static final int FAB_MOVE_VER = 1;

    private ViewPager mViewPager;
//    private ProgressDialog progressDialog;
    private boolean getSys;
    private FloatingActionButton mFab;
    private Context mContext;

    private Set<String> selectedPkg = new HashSet<>();

    // fab status
    private boolean isRotating;
    private boolean isHideToRight;
    private boolean isHideToBottom;
    private int fabType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ApplicationContext.init(this);
        getSys = SharedPrefsUtil.getSys(this);
        mContext = this;

        setView();

        MyLog.i("check root..");
        if (!ShellUtil.checkRoot()) {
            MyLog.i("not root, finish");
            showNoRootDialog();
        } else {
            MyLog.i("got root access");
            boolean isFirst = SharedPrefsUtil.isFirst(this);
            if (!isFirst) {
                showGuideDialog();
            } else {
                loadingApp(getSys);
            }
        }
        fabMove(FAB_MOVE_HOR, 0, 600, false, 600);
    }

    private void showGuideDialog() {
        final Dialog dialog = new Dialog(this, R.style.guide_dialog);
        View guide = LayoutInflater.from(this).inflate(R.layout.dialog_guide, null);
        guide.findViewById(R.id.btn_guide_isee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPrefsUtil.setFirst(mContext);
                loadingApp(getSys);
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.addContentView(guide, params);
        dialog.show();
    }

    private void showNoRootDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_no_root_title)
                .setMessage(R.string.dialog_no_root_content)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create().show();
    }

    private void loadingApp(boolean getSys) {
        new LoadAppTask(this, getSys).execute();
    }

    private void setView() { //List<AppInfo> disabledApps, List<AppInfo> enabledNowApps,List<AppInfo> enabledApps
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setTranslationX(PxUtil.dip2px(this, 90));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 0) {
                    if (fabType == FAB_TYPE_DISABLE) {
                        EventBus.getDefault().post(new DisableAllEvent());
                    } else {
                        Snackbar.make(view, "啥都没有发生=.=", Snackbar.LENGTH_SHORT).show();
                    }
                } else if (mViewPager.getCurrentItem() == 1) {

                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AppListFragment.newInstance(AppListFragment.TYPE_DISABLE), getText(R.string.text_disabled).toString());
        adapter.addFragment(AppListFragment.newInstance(AppListFragment.TYPE_ENABLE), getText(R.string.text_enabled).toString());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                final int pos = position;
                EventBus.getDefault().post(new PageChangedEvent(position));
                selectedPkg.clear();
                if (position == 0 && isHideToRight) {
                    fabMove(FAB_MOVE_HOR, 0, 0, false, 600);
                    isHideToRight = false;
                } else if (position == 1) {
                    if (!isHideToRight && !isHideToBottom) {
                        fabMove(FAB_MOVE_HOR, PxUtil.dip2px(mContext, 90), 0, true, 300);
                        isHideToRight = true;
                    } else {
                    }
                    ApplicationContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // reset fab
                            mFab.setImageResource(R.drawable.ic_disable);
                            mFab.setTranslationX(PxUtil.dip2px(mContext, 90));
                            mFab.setTranslationY(0);
//                        mFab.animate().translationX(PxUtil.dip2px(mContext, 90));
//                        mFab.animate().translationY(0);
                            isHideToBottom = false;
                            isHideToRight = true;
                            fabType = FAB_TYPE_DISABLE;
                        }
                    }, 500);
                }
//                HandlerExecutor.getHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (pos == 0 && isHideToRight) {
//                            mFab.animate().translationX(0);
//                            mFabRotation(false, 600);
//                            isHideToRight = false;
//                        } else if (pos == 1 && !isHideToRight) {
//                            mFab.animate().translationX(PxUtil.dip2px(mContext, 90));
//                            mFabRotation(true, 600);
//                            isHideToRight = true;
//                        }
//                    }
//                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.text_disabled));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.text_enabled));
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_sysapp);
        item.setChecked(getSys);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sysapp:
                getSys = !getSys;
                item.setChecked(getSys);
                SharedPrefsUtil.putValue(this, SharedPrefsUtil.KEY_GET_SYS, getSys);
                loadingApp(getSys);
                break;
            case R.id.action_refresh:
                loadingApp(getSys);
                break;
            case R.id.action_all:
                enableAll();
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mFabRotation(boolean clockwise, long time) {
        if (isRotating) {
            return;
        }
        mFab.setRotation(0);
        float rotateDegree = (clockwise ? 360 : -360);
        isRotating = true;
        mFab.animate()
                .rotation(rotateDegree)
                .setDuration(time);
        HandlerExecutor.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isRotating = false;
            }
        }, time - 400);
    }

    private void fabMove(final int direction, final float px, long delay, final boolean clockwise, final long time) {
        HandlerExecutor.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (direction == FAB_MOVE_HOR) {
                    mFab.animate().translationX(px);
                } else if (direction == FAB_MOVE_VER) {
                    mFab.animate().translationY(px);
                }
                mFabRotation(clockwise, time);
            }
        }, delay);
    }

    @Subscribe
    public void onEvent(ScrollEvent event) {
        if (fabType == FAB_TYPE_ADD) {
            return;
        }
        if (event.direction == RecyclerViewScrollListener.SCROLL_DOWN) {
            if (!isHideToBottom) {
                fabMove(FAB_MOVE_VER, PxUtil.dip2px(mContext, 90), 0, false, 600);
                isHideToBottom = true;
            }
        } else if (event.direction == RecyclerViewScrollListener.SCROLL_UP) {
            if (isHideToBottom) {
                fabMove(FAB_MOVE_VER, 0, 0, true, 600);
                isHideToBottom = false;
            }
        }
    }

    @Subscribe
    public void onEvent(ItemSelectEvent event) {
        selectedPkg.add(event.packageName);
        if (mViewPager.getCurrentItem() == 0 && fabType == FAB_TYPE_DISABLE && selectedPkg.size() > 0) {
            if (!isHideToBottom) {
                fabMove(FAB_MOVE_VER, PxUtil.dip2px(mContext, 90), 0, false, 200);
            }

            ApplicationContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFab.setImageResource(R.drawable.ic_action_content_add);
                }
            }, 200);

            fabMove(FAB_MOVE_VER, 0, 300, true, 200);
            fabType = FAB_TYPE_ADD;
        } else if (mViewPager.getCurrentItem() == 1 && isHideToRight) {
            fabMove(FAB_MOVE_HOR, 0, 0, true, 300);
            isHideToRight = false;
        }
    }

    @Subscribe
    public void onEvent(ItemCancelEvent event) {
        selectedPkg.remove(event.packageName);
        if (mViewPager.getCurrentItem() == 0 && selectedPkg.size() == 0 && fabType == FAB_TYPE_ADD) {
            fabMove(FAB_MOVE_VER, PxUtil.dip2px(mContext, 90), 0, false, 200);

            ApplicationContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFab.setImageResource(R.drawable.ic_disable);
                }
            }, 200);

            fabMove(FAB_MOVE_VER, 0, 600, true, 300);
            fabType = FAB_TYPE_DISABLE;
        } else if (mViewPager.getCurrentItem() == 1 && selectedPkg.size() == 0) {
            fabMove(FAB_MOVE_HOR, PxUtil.dip2px(mContext, 90), 0, true, 300);
            isHideToRight = true;
        }
    }

    private void enableAll() {
        List<String> allApps = DbUtil.getAllApps();
        ShellUtil.enableApp(mContext, allApps);
        loadingApp(getSys);
    }

}
