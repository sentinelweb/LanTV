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

package uk.co.sentinelweb.tvmod.details;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import javax.inject.Inject;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.R;
import uk.co.sentinelweb.tvmod.browse.CardPresenter;
import uk.co.sentinelweb.tvmod.exoplayer.ExoPlayerActivity;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Item;
import uk.co.sentinelweb.tvmod.playback.PlaybackOverlayActivity;
import uk.co.sentinelweb.tvmod.util.Extension;
import uk.co.sentinelweb.tvmod.util.FileUtils;
import uk.co.sentinelweb.tvmod.util.MxPlayerController;
import uk.co.sentinelweb.tvmod.util.Utils;
import uk.co.sentinelweb.tvmod.util.VlcController;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class DetailsFragment extends android.support.v17.leanback.app.DetailsFragment implements DetailsMvpContract.View {
    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_VLC = 1;
    private static final int ACTION_MX = 2;
    private static final int ACTION_DOWNLOAD = 3;
    private static final int ACTION_SYSTEM = 4;
    private static final int ACTION_EXO = 5;


    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;
    //private Movie mSelectedMovie;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    //private Subscription _subscribe;
    @Inject protected DetailsMvpContract.Presenter _presenter;

    @Inject protected VlcController _vlcController;
    @Inject protected  MxPlayerController _mxController;

    DetailsFragmentModel _model;

    public DetailsFragment() {

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);
//        _webProxyManager = new WebProxyManager(getActivity().getApplication());
//        _vlcController = new VlcController(getActivity(), _webProxyManager);
//        _mxController = new MxPlayerController(getActivity(), _webProxyManager);
        prepareBackgroundManager();

        setupAdapter();
        setupDetailsOverviewRowPresenter();
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void setupMovie() {
        setupDetailsOverviewRow();
        setupMovieListRowPresenter();
        updateBackground(_model.getItem().getBackgroundImageUrl());
    }

    @Override
    public void onStart() {
        super.onStart();
        final Item item = (Item) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        final SmbLocation location = (SmbLocation) getActivity().getIntent().getSerializableExtra(DetailsActivity.LOCATION);
        _presenter.setupData(location, item);
        _presenter.subscribe();
    }

    @Override
    public void onStop() {
        _presenter.unsubscribe();
        super.onStop();
    }

    @Override
    public void setPresenter(final DetailsMvpContract.Presenter presenter) {
        _presenter = presenter;
    }

    @Override
    public void setData(final DetailsFragmentModel model) {
        _model = model;
        setupMovie();
        processList(model.getCategory());
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    protected void updateBackground(final String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(mMetrics.widthPixels, mMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(final GlideDrawable resource,
                                                final GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
    }

    private void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {
        mAdapter.clear();
        //Log.d(TAG, "doInBackground: " + _model.getMovie().toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(_model.getItem());
        row.setImageDrawable(getResources().getDrawable(R.drawable.default_background));
        final int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        final int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);
        Glide.with(getActivity())
                .load(_model.getItem().getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(final GlideDrawable resource,
                                                final GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
//        final MimeMap.MimeData mimeData = MimeMap.get(_model.getMovie().getVideoUrl());
        final boolean supported = Extension.isSupported(FileUtils.getExt(_model.getItem().getVideoUrl()));
        if (supported) {
            row.addAction(new Action(ACTION_SYSTEM, getResources().getString(
                    R.string.play_system)));
            row.addAction(new Action(ACTION_EXO, getResources().getString(R.string.play_exo)));
        } else {
            if (_vlcController.checkInstalled()) {
                row.addAction(new Action(ACTION_VLC, getResources().getString(
                    R.string.play_vlc)));
            } else {
                // show install action
            }
            if (_mxController.checkInstalled()) {
                row.addAction(new Action(ACTION_MX, getResources().getString(R.string.play_mx)));
            } else {
                // show install action
            }
            //row.addAction(new Action(ACTION_DOWNLOAD, getResources().getString(R.string.download)));
        }
        mAdapter.add(row);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background and style.
        final DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                DetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(final Action action) {
                if (action.getId() == ACTION_VLC) {
                    launchVlc();
                } else if (action.getId() == ACTION_MX) {
                    launchMxPlayer();
                } else if (action.getId() == ACTION_SYSTEM) {
                    launchSystemPlayer();
                } else if (action.getId() == ACTION_EXO) {
                    launchExoplayer();
                } else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    @Override
    public void launchSystemPlayer() {
        final Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
        intent.putExtra(DetailsActivity.MOVIE, _model.getItem());
        startActivity(intent);
    }

    @Override
    public void launchExoplayer() {
        final Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
        intent.putExtra(DetailsActivity.MOVIE, _model.getItem());
        getActivity().startActivity(intent, null);
    }

    @Override
    public void launchVlc() {
        _vlcController.launchVlcProxy(_model.getItem());
    }

    @Override
    public void launchMxPlayer() {
        _mxController.launchMxPlayer(_model.getItem());
    }


    private void processList(final Category category) {
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int j = 0; j < category.items().size(); j++) {
            listRowAdapter.add(category.items().get(j));
        }

        final HeaderItem header = new HeaderItem(0, category.name());
        mAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void setupMovieListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(final Presenter.ViewHolder itemViewHolder, final Object item,
                                  final RowPresenter.ViewHolder rowViewHolder, final Row row) {

            if (item instanceof Item) {
                final Item movie = (Item) item;
                Log.d(TAG, "Item: " + item.toString());
                final Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);
                intent.putExtra(DetailsActivity.LOCATION, _model.getLocation());
                intent.putExtra(getResources().getString(R.string.should_start), true);
                //startActivity(intent);

                final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
                getActivity().finish();
            }
        }
    }
}
