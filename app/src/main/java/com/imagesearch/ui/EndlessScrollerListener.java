package com.imagesearch.ui;

import android.widget.AbsListView;

/**
 * Created by sabelo on 9/18/15.
 */
public abstract  class EndlessScrollerListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 4;
    private int previousTotalCount = 0;
    private int currentPage = 0;
    private int startPageIndex = 0;
    private int increment;
    private int limit;
    private boolean loading;

    public EndlessScrollerListener(){}

    public EndlessScrollerListener(int visibleThreshold) {this.visibleThreshold = visibleThreshold;}

    public EndlessScrollerListener(int visibleThreshold, int startpage, int increment, int limit) {
        this.visibleThreshold = visibleThreshold;
        this.startPageIndex = startpage;
        this.currentPage = startpage;
        this.increment = increment;
        this.limit = limit;

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // State is invalid if total items are less than the last known total
        if (totalItemCount < previousTotalCount) {
            currentPage = startPageIndex;
            previousTotalCount = 0;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If we've passed visible threshhold
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && currentPage <= limit ) {
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }

        // Done loading
        if (loading && (totalItemCount > previousTotalCount)) {
            loading = false;
            previousTotalCount = totalItemCount;
            currentPage += increment;
        }


    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        // Leave blank
    }

    // Load more data. Must extend this class and implement onLoadMore
        public abstract void onLoadMore(int page, int totalItemsCount);
}
