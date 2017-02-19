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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.C;
import uk.co.sentinelweb.tvmod.R;
import uk.co.sentinelweb.tvmod.details.DetailsActivity;
import uk.co.sentinelweb.tvmod.error.BrowseErrorActivity;
import uk.co.sentinelweb.tvmod.exoplayer.ExoPlayerActivity;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.util.VlcUtil;

public class SmbBrowseFragment extends BrowseFragment implements SmbBrowseMvpContract.View {
    private static final String TAG = "MainFragment";

//    private static final int GRID_ITEM_WIDTH = 200;
//    private static final int GRID_ITEM_HEIGHT = 200;

    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Uri mBackgroundURI;
    private BackgroundManager mBackgroundManager;

    SmbBrowseMvpContract.Presenter presenter;
    private CardPresenter _cardPresenter;

    private final VlcUtil _vlcUtil;
    SmbBrowseFragmentModel model = null;

    public SmbBrowseFragment() {
        _vlcUtil = new VlcUtil();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);
        SmbLocation location = (SmbLocation) getActivity().getIntent().getSerializableExtra(DetailsActivity.LOCATION);
        if (location == null) {
            location = C.TEST_LOCATION;
        }
        presenter.setupData(location);

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
        if (model == null) {
            presenter.subscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        selectedImageViewForTransition = null;
        super.onDestroy();
    }

    @Override
    public void setData(final SmbBrowseFragmentModel model) {
        final boolean create = mRowsAdapter == null;
        setTitle(model.getTitle());
        this.model = model;
        if (create) {
            mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
            _cardPresenter = new CardPresenter();
            int i = 0;
            for (final Category category : model.getCategories()) {
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

    private void updateAdapter(final SmbBrowseFragmentModel model) {
        int categoryNumber = 0;
        for (final Category category : model.getCategories()) {
            final ArrayObjectAdapter listRowAdapter;
            if (categoryNumber < mRowsAdapter.size()) {
                listRowAdapter = (ArrayObjectAdapter) ((ListRow) mRowsAdapter.get(categoryNumber)).getAdapter();
                int j = 0;
                if (listRowAdapter.size()!=category.movies().size()) {
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
                }
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
        //setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
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
    public void setPresenter(final SmbBrowseMvpContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //_vlcUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void launchExoplayer(final Movie movie) {
        if (selectedImageViewForTransition != null) {
            selectedImageViewForTransition = null;
        }
        final Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
        intent.putExtra(DetailsActivity.MOVIE, movie);
        getActivity().startActivity(intent, null);
    }

    @Override
    public void showError(final Throwable error) {
        showError(error.getMessage() + "(" + error.getClass().getSimpleName() + ")");
    }

    @Override
    public void showError(final String item) {
        if (item.indexOf(getString(R.string.error_fragment)) >= 0) {
            startActivity(BrowseErrorActivity.getLaunchIntent(getActivity()));
        } else {
            Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
        }
    }

    ImageView selectedImageViewForTransition = null;

    @Override
    public void launchDetails(final SmbLocation location, final Movie movie) {
        // scene transition to details
        final Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.MOVIE, movie);
        intent.putExtra(DetailsActivity.LOCATION, location);

        if (selectedImageViewForTransition != null) {
            final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    selectedImageViewForTransition,
                    DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
            selectedImageViewForTransition = null;
            getActivity().startActivity(intent, bundle);
        } else {
            getActivity().startActivity(intent);
        }

    }

    @Override
    public void launchBrowser(final SmbLocation dir) {
        getActivity().startActivity(SmbBrowseActivity.getIntent(getActivity(), dir));
    }

    @Override
    public void launchVlc(final Movie movie) {
        _vlcUtil.launchVlc(getActivity(), movie);
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(final Presenter.ViewHolder itemViewHolder, final Object item,
                                   final RowPresenter.ViewHolder rowViewHolder, final Row row) {
            if (item instanceof Movie) {
                final Movie movie = (Movie) item;
//                if (movie==MovieList.PARENT_DIR_MOVIE) {
//                    presenter.launchMovie((Movie) item);
//                }
                if (movie.getBackgroundImageURI() != null) {
                    mBackgroundURI = movie.getBackgroundImageURI();
                }
            }
        }
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(final Presenter.ViewHolder itemViewHolder, final Object item,
                                  final RowPresenter.ViewHolder rowViewHolder, final Row row) {
            if (item instanceof Movie) {
                selectedImageViewForTransition = ((ImageCardView) itemViewHolder.view).getMainImageView();
                presenter.launchMovie((Movie) item);
            } else if (item instanceof String) {
                showError((String) item);
            }
        }
    }

//    /**
//     * <a href="https://wiki.videolan.org/Android_Player_Intents/">VLC android intents</a>
//     *
//     * @param movie
//     */
//    @Override
//    public void launchVlc(final Movie movie) {
//        _vlcUtil.launchVlc(getActivity(), movie);
//    }


//    @Override
//    public void showDownloadDialog() {
//        downloadDialog = new AlertDialog.Builder(getActivity())
//                .setTitle("Downloading")
//                .setMessage("Downloading ...")
//                .create();
//        downloadDialog.show();
//    }
//
//    @Override
//    public void updateDownloadDialog(final String message) {
//        if (downloadDialog != null) {
//            downloadDialog.setMessage(message);
//        }
//    }
//
//    @Override
//    public void closeDownloadDialog() {
//        if (downloadDialog != null) {
//            downloadDialog.dismiss();
//        }
//    }
//
//    @Override
//    public File getBufferFile(final Movie movie) {
//        return FileUtils.getBufferFile(getActivity(), movie.getVideoUrl());
//    }


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