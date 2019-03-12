package com.github.waterpeak.onepage;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager.widget.PagerAdapter;

public class OnePageSwitcher implements LifecycleObserver {

    private final OnePageActivity mHost;
    private final OnePageContainerLayout container;
    private final OnePage pages[];
    private int index = -1;

    public OnePageSwitcher(@NonNull OnePageActivity host,
                           @NonNull OnePageContainerLayout container,
                           @NonNull OnePage... pages) {
        mHost = host;
        this.container = container;
        this.pages = pages;
    }

    public void setIndex(int index) {
        if (index != this.index) {
            OnePage current = null;
            if (this.index >= 0) {
                current = pages[this.index];
            }
            if (current != null) {
                current.onPause();
            }
            OnePage target = pages[index];
            target.createInternal(mHost);
            target.onStart();
            container.addView(target.mContentView);
            target.onResume();
            if (current != null) {
                container.removeView(current.mContentView);
                current.onStop();
            }
            this.index = index;
        }
    }

    public int getIndex() {
        return index;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onStart();
            }

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onResume();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onPause();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onStop();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        for (OnePage page : pages) {
            if (page.isCreated() && !page.isDestroyed()) {
                page.onDestroy();
            }
        }
    }

    @NonNull
    public PagerAdapter getAdapter() {
        return new PagerAdapter() {

            @Override
            public int getCount() {
                return pages.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                OnePage page = pages[position];
                page.createInternal(mHost);
                page.onStart();
                container.addView(page.mContentView);
                page.onResume();
                return page.mContentView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                OnePage page = pages[position];
                page.onPause();
                container.removeView(page.mContentView);
                page.onStop();
            }
        };
    }
}
