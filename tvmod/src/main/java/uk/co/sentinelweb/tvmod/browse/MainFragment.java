/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package uk.co.sentinelweb.tvmod.browse;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.net.URI;

import uk.co.sentinelweb.tvmod.R;
import uk.co.sentinelweb.tvmod.details.DetailsActivity;
import uk.co.sentinelweb.tvmod.error.BrowseErrorActivity;
import uk.co.sentinelweb.tvmod.exoplayer.ExoPlayerActivity;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.util.SupportedMedia;

public class MainFragment extends BrowseFragment implements MainMvpContract.View {
    private static final String TAG = "MainFragment";
    public static final int VLC_REQUEST_CODE = 42;

//    private static final int GRID_ITEM_WIDTH = 200;
//    private static final int GRID_ITEM_HEIGHT = 200;

    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;

    MainMvpContract.Presenter presenter;
    private CardPresenter _cardPresenter;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        setupEventListeners();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setData(final MainFragmentModel model) {
        final boolean create = mRowsAdapter == null;
        if (create) {
            mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
            _cardPresenter = new CardPresenter();
            int i = 0;
            for (final Category category : model._categories) {
                addRow(category, i++);
            }
            //Log.d(getClass().getSimpleName(),"movies:"+list.size());

//        final HeaderItem gridHeader = new HeaderItem(i, "PREFERENCES");

//        final GridItemPresenter mGridPresenter = new GridItemPresenter();
//        final ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
//        gridRowAdapter.add(getResources().getString(R.string.grid_view));
//        gridRowAdapter.add(getString(R.string.error_fragment));
//        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
//        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));
            setAdapter(mRowsAdapter);
        } else {
            updateAdapter(model);
        }
    }

    private void addRow(final Category category, final long index) {
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(_cardPresenter);
        for (final Movie m : category.movies()) {
            listRowAdapter.add(m);
        }
        final HeaderItem header = new HeaderItem(index, category.name() + " (" + category.count() + ")");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void updateAdapter(final MainFragmentModel model) {
        int categoryNumber = 0;
        for (final Category category : model._categories) {
            final ArrayObjectAdapter listRowAdapter;
            if (categoryNumber < mRowsAdapter.size()) {
                listRowAdapter = (ArrayObjectAdapter) ((ListRow) mRowsAdapter.get(categoryNumber)).getAdapter();
                int j = 0;
                for (final Movie m : category.movies()) {
                    if (j < listRowAdapter.size()) {
                        listRowAdapter.replace(j, m);
                    } else {
                        listRowAdapter.add(m);
                    }
                    j++;
                }
                if (j < listRowAdapter.size()) {
                    listRowAdapter.removeItems(j, listRowAdapter.size());
                }
                listRowAdapter.notifyArrayItemRangeChanged(0, listRowAdapter.size());
            } else {
                addRow(category, categoryNumber);
            }
            categoryNumber++;
        }
        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener((view) -> Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG).show());
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());

    }

    @Override
    public void updateBackGround() {
        if (mBackgroundURI != null) {
            final int width = mMetrics.widthPixels;
            final int height = mMetrics.heightPixels;
            Glide.with(getActivity())
                    .load(mBackgroundURI)
                    .centerCrop()
                    .error(mDefaultBackground)
                    .into(new SimpleTarget<GlideDrawable>(width, height) {
                        @Override
                        public void onResourceReady(final GlideDrawable resource,
                                                    final GlideAnimation<? super GlideDrawable>
                                                            glideAnimation) {
                            mBackgroundManager.setDrawable(resource);
                        }
                    });
            mBackgroundURI = null;
        }
    }

    @Override
    public void setPresenter(final MainMvpContract.Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * <a href="https://wiki.videolan.org/Android_Player_Intents/">VLC android intents</a>
     *
     * @param movie
     */
    @Override
    public void launchVlc(Movie movie) {
        Uri uri = Uri.parse("file:///storage/emulated/0/Movies/KUNG FURY Official Movie.mp4");
        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
        vlcIntent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
        vlcIntent.setPackage("org.videolan.vlc");
        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
        vlcIntent.putExtra("title", "Kung Fury");
        //vlcIntent.putExtra("from_start", false);
        //vlcIntent.putExtra("position", 90000l);
        //vlcIntent.putExtra("subtitles_location", "/sdcard/Movies/Fifty-Fifty.srt");
        startActivityForResult(vlcIntent, VLC_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        data.getLongExtra("extra_position", -1); //Last position in media when player exited
        data.getLongExtra("extra_duration", -1); //	long	Total duration of the media
    }

    @Override
    public void launchExoplayer(Movie movie) {
        final Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
        intent.putExtra(DetailsActivity.MOVIE, movie);
        getActivity().startActivity(intent, null);
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(final Presenter.ViewHolder itemViewHolder, final Object item,
                                   final RowPresenter.ViewHolder rowViewHolder, final Row row) {
            if (item instanceof Movie) {
                mBackgroundURI = ((Movie) item).getBackgroundImageURI();
            }

        }
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(final Presenter.ViewHolder itemViewHolder, final Object item,
                                  final RowPresenter.ViewHolder rowViewHolder, final Row row) {

            if (item instanceof Movie) {
                final Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + item.toString());
//                final Intent intent = new Intent(getActivity(), DetailsActivity.class);
//                intent.putExtra(DetailsActivity.MOVIE, movie);
//
//                final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        getActivity(),
//                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
//                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
//                getActivity().startActivity(intent, bundle);
                presenter.launchMovie((Movie) item);
            } else if (item instanceof String) {
                if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                    final Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

//    private class GridItemPresenter extends Presenter {
//        @Override
//        public ViewHolder onCreateViewHolder(final ViewGroup parent) {
//            final TextView view = new TextView(parent.getContext());
//            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
//            view.setFocusable(true);
//            view.setFocusableInTouchMode(true);
//            view.setBackgroundColor(getResources().getColor(R.color.default_background));
//            view.setTextColor(Color.WHITE);
//            view.setGravity(Gravity.CENTER);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder viewHolder, final Object item) {
//            ((TextView) viewHolder.view).setText((String) item);
//        }
//
//        @Override
//        public void onUnbindViewHolder(final ViewHolder viewHolder) {
//        }
//    }

}
