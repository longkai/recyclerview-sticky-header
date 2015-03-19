package com.example.recycler_sticky_header.app;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements OnStickyLayout {
  private RecyclerView mRecyclerView;
  private View mStickyView;

  private int mScrollY;

  private RecyclerView.Adapter mAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
    mStickyView = findViewById(R.id.sticky_view);

    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    List<String> list = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      list.add(String.valueOf(i));
    }
    
    mAdapter = new StickyAdapter(list, this);
    
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override public void onStickyLayout(final int top, int stickyViewHeight) {
    ViewCompat.setTranslationY(mStickyView, top);

    mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        mScrollY += dy;

        int translationY = top - mScrollY;
        
        if (translationY < 0) translationY = 0;
        
        ViewCompat.setTranslationY(mStickyView, translationY);
      }
    });
  }

  static class StickyAdapter extends RecyclerView.Adapter {
    List<String> list;

    OnStickyLayout onStickyLayout;

    public StickyAdapter(List<String> list) {
      this.list = list;
    }

    public StickyAdapter(List<String> list, OnStickyLayout onStickyLayout) {
      this.list = list;
      this.onStickyLayout = onStickyLayout;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      if (viewType == 0) {
        return new StickyViewHolder(
            inflater.inflate(R.layout.sticky_view_holder, parent, false), onStickyLayout);
      }

      return new RecyclerView.ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false)) {
        @Override public String toString() {
          return super.toString();
        }
      };
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      if (position != 0) {
        TextView tv = (TextView) holder.itemView;
        tv.setText(String.valueOf(position));
      }
    }

    @Override public int getItemViewType(int position) {
      return position == 0 ? 0 : 1;
    }

    @Override public int getItemCount() {
      return list.size();
    }
  }

  static class StickyViewHolder extends RecyclerView.ViewHolder {
    View placeHolderView;

    public StickyViewHolder(View itemView, final OnStickyLayout onStickyLayout) {
      super(itemView);
      
      placeHolderView = itemView.findViewById(R.id.place_holder);

      if (onStickyLayout == null) return;

      placeHolderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {

          onStickyLayout.onStickyLayout(placeHolderView.getTop(), placeHolderView.getHeight());

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            placeHolderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          } else {
            placeHolderView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
          }
        }
      });
    }
  }
}
