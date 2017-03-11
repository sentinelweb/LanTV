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

package uk.co.sentinelweb.tvmod.playback;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RepeatAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ShuffleAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsDownAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsUpAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import uk.co.sentinelweb.tvmod.R;
import uk.co.sentinelweb.tvmod.browse.CardPresenter;
import uk.co.sentinelweb.tvmod.details.DetailsActivity;
import uk.co.sentinelweb.tvmod.model.Item;

/*
 * Class for video playback with media control
 */
public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static final String TAG = "PlaybackControlsFragmnt";

    private static final boolean SHOW_DETAIL = true;
    private static final boolean HIDE_MORE_ACTIONS = false;
    private static final int PRIMARY_CONTROLS = 5;
    private static final boolean SHOW_IMAGE = PRIMARY_CONTROLS <= 5;
    private static final int BACKGROUND_TYPE = PlaybackOverlayFragment.BG_LIGHT;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int SIMULATED_BUFFERED_TIME = 10000;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;
    private PlayPauseAction mPlayPauseAction;
    private RepeatAction mRepeatAction;
    private ThumbsUpAction mThumbsUpAction;
    private ThumbsDownAction mThumbsDownAction;
    private ShuffleAction mShuffleAction;
    private FastForwardAction mFastForwardAction;
    private RewindAction mRewindAction;
    private SkipNextAction mSkipNextAction;
    private SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow mPlaybackControlsRow;
    private ArrayList<Item> mItems = new ArrayList<>();
    private int mCurrentItem;
    private Handler mHandler;
    private Runnable mRunnable;
    private Item _mSelectedItem;

    private OnPlayPauseClickedListener mCallback;
    private Subscription _subscribe;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItems = new ArrayList<>();
        _mSelectedItem = (Item) getActivity()
                .getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        mItems.add(_mSelectedItem);
        _subscribe = Observable.<List<Item>>empty()
//                MovieList.setupMovies()
//                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Item>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(final Throwable e) {

                    }

                    @Override
                    public void onNext(final List<Item> movies) {
                        for (int j = 0; j < movies.size(); j++) {
                            mItems.add(movies.get(j));
                            if (_mSelectedItem.getTitle().contentEquals(movies.get(j).getTitle())) {
                                mCurrentItem = j;
                            }
                        }
                    }
                });


        mHandler = new Handler();

        setBackgroundType(BACKGROUND_TYPE);
        setFadingEnabled(false);

        setupRows();

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(final Presenter.ViewHolder itemViewHolder, final Object item,
                                       final RowPresenter.ViewHolder rowViewHolder, final Row row) {
                Log.i(TAG, "onItemSelected: " + item + " row " + row);
            }
        });
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(final Presenter.ViewHolder itemViewHolder, final Object item,
                                      final RowPresenter.ViewHolder rowViewHolder, final Row row) {
                Log.i(TAG, "onItemClicked: " + item + " row " + row);
            }
        });
    }

    @Override
    public void onAttach(final Activity context) {
        super.onAttach(context);
        if (context instanceof OnPlayPauseClickedListener) {
            mCallback = (OnPlayPauseClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayPauseClickedListener");
        }
    }

    private void setupRows() {

        final ClassPresenterSelector ps = new ClassPresenterSelector();

        final PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(
                    new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(final Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    togglePlayback(mPlayPauseAction.getIndex() == PlayPauseAction.PLAY);
                } else if (action.getId() == mSkipNextAction.getId()) {
                    next();
                } else if (action.getId() == mSkipPreviousAction.getId()) {
                    prev();
                } else if (action.getId() == mFastForwardAction.getId()) {
                    Toast.makeText(getActivity(), "TODO: Fast Forward", Toast.LENGTH_SHORT).show();
                } else if (action.getId() == mRewindAction.getId()) {
                    Toast.makeText(getActivity(), "TODO: Rewind", Toast.LENGTH_SHORT).show();
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    ((PlaybackControlsRow.MultiAction) action).nextIndex();
                    notifyChanged(action);
                }
            }
        });
        playbackControlsRowPresenter.setSecondaryActionsHidden(HIDE_MORE_ACTIONS);

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();
        addOtherRows();

        setAdapter(mRowsAdapter);
    }

    public void togglePlayback(final boolean playPause) {
        if (playPause) {
            startProgressAutomation();
            setFadingEnabled(true);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), mPlaybackControlsRow.getCurrentTime(), true);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PAUSE));
        } else {
            stopProgressAutomation();
            setFadingEnabled(false);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), mPlaybackControlsRow.getCurrentTime(), false);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PLAY));
        }
        notifyChanged(mPlayPauseAction);
    }

    private int getDuration() {
        final Item item = mItems.get(mCurrentItem);
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mmr.setDataSource(item.getVideoUrl(), new HashMap<>());
        } else {
            mmr.setDataSource(item.getVideoUrl());
        }
        final String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        final long duration = Long.parseLong(time);
        return (int) duration;
    }

    private void addPlaybackControlsRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = new PlaybackControlsRow(_mSelectedItem);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowsAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow(mCurrentItem);

        final ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        mPlayPauseAction = new PlayPauseAction(getActivity());
        mRepeatAction = new RepeatAction(getActivity());
        mThumbsUpAction = new ThumbsUpAction(getActivity());
        mThumbsDownAction = new ThumbsDownAction(getActivity());
        mShuffleAction = new ShuffleAction(getActivity());
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getActivity());
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getActivity());
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getActivity());
        mRewindAction = new PlaybackControlsRow.RewindAction(getActivity());

        if (PRIMARY_CONTROLS > 5) {
            mPrimaryActionsAdapter.add(mThumbsUpAction);
        } else {
            mSecondaryActionsAdapter.add(mThumbsUpAction);
        }
        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        if (PRIMARY_CONTROLS > 3) {
            mPrimaryActionsAdapter.add(new PlaybackControlsRow.RewindAction(getActivity()));
        }
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        if (PRIMARY_CONTROLS > 3) {
            mPrimaryActionsAdapter.add(new PlaybackControlsRow.FastForwardAction(getActivity()));
        }
        mPrimaryActionsAdapter.add(mSkipNextAction);

        mSecondaryActionsAdapter.add(mRepeatAction);
        mSecondaryActionsAdapter.add(mShuffleAction);
        if (PRIMARY_CONTROLS > 5) {
            mPrimaryActionsAdapter.add(mThumbsDownAction);
        } else {
            mSecondaryActionsAdapter.add(mThumbsDownAction);
        }
        mSecondaryActionsAdapter.add(new PlaybackControlsRow.HighQualityAction(getActivity()));
        mSecondaryActionsAdapter.add(new PlaybackControlsRow.ClosedCaptioningAction(getActivity()));
    }

    private void notifyChanged(final Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    private void updatePlaybackRow(final int index) {
        if (mPlaybackControlsRow.getItem() != null) {
            final Item item = (Item) mPlaybackControlsRow.getItem();
            if (mItems.size()<mCurrentItem) {
                item.setTitle(mItems.get(mCurrentItem).getTitle());
                item.setExtension(mItems.get(mCurrentItem).getExtension());
            }
        }
        if (SHOW_IMAGE) {
            if (mItems.size()<mCurrentItem) {
                updateVideoImage(mItems.get(mCurrentItem).getCardImageURI().toString());
            }
        }
        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
        mPlaybackControlsRow.setTotalTime(getDuration());
        mPlaybackControlsRow.setCurrentTime(0);
        mPlaybackControlsRow.setBufferedProgress(0);
    }

    private void addOtherRows() {
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (final Item item : mItems) {
            listRowAdapter.add(item);
        }
        final HeaderItem header = new HeaderItem(0, getString(R.string.related_movies));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));

    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void startProgressAutomation() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                final int updatePeriod = getUpdatePeriod();
                final int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
                final int totalTime = mPlaybackControlsRow.getTotalTime();
                mPlaybackControlsRow.setCurrentTime(currentTime);
                mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);

                if (totalTime > 0 && totalTime <= currentTime) {
                    next();
                }
                mHandler.postDelayed(this, updatePeriod);
            }
        };
        mHandler.postDelayed(mRunnable, getUpdatePeriod());
    }

    private void next() {
        if (++mCurrentItem >= mItems.size()) {
            mCurrentItem = 0;
        }

        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private void prev() {
        if (--mCurrentItem < 0) {
            mCurrentItem = mItems.size() - 1;
        }
        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onStop() {
        stopProgressAutomation();
        if (_subscribe != null) {
            _subscribe.unsubscribe();
            _subscribe = null;
        }
        super.onStop();
    }

    protected void updateVideoImage(final String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>(CARD_WIDTH, CARD_HEIGHT) {
                    @Override
                    public void onResourceReady(final GlideDrawable resource, final GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mPlaybackControlsRow.setImageDrawable(resource);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                    }
                });
    }

    // Container Activity must implement this interface
    public interface OnPlayPauseClickedListener {
        void onFragmentPlayPause(Item item, int position, Boolean playPause);
    }

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(final ViewHolder viewHolder, final Object item) {
            viewHolder.getTitle().setText(((Item) item).getTitle());
            viewHolder.getSubtitle().setText(((Item) item).getExtension().toString());
        }
    }
}
