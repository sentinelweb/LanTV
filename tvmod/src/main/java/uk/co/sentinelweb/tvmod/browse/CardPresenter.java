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

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import uk.co.sentinelweb.tvmod.R;
import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.util.Extension;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private Drawable mDefaultCardImage;
    private OnLongClickListener longClickListener;

    private static void updateCardBackgroundColor(final ImageCardView view, final boolean selected) {
        final int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);
        mDefaultCardImage = parent.getResources().getDrawable(R.drawable.movie);

        final ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(final boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };


        cardView.setMainImageScaleType(ImageView.ScaleType.FIT_CENTER);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        if (longClickListener!=null) {
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    return longClickListener.onLongClick((Movie)v.getTag());
                }
            });
        }
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final Presenter.ViewHolder viewHolder, final Object item) {
        final Movie movie = (Movie) item;
        final ImageCardView cardView = (ImageCardView) viewHolder.view;

        //Log.d(TAG, "onBindViewHolder");
        cardView.setTitleText(movie.getTitle());
        cardView.setContentText(movie.getExtension());
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        cardView.setTag(movie);
        if (movie.getCardImageUrl() != null) {

            Glide.with(viewHolder.view.getContext())
                    .load(movie.getCardImageUrl())
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(cardView.getMainImageView());

        } else {
            // load default icon (SVG)
            @DrawableRes final int icon = Extension.getIcon(movie.getExtension());
            final boolean supported = Extension.isSupported(movie.getExtension());
            cardView.getMainImageView().setImageResource(icon);
            @ColorRes final int tintColor = supported ? R.color.card_icon : R.color.card_icon_unsupported;
            final ColorStateList colorStateList = ContextCompat.getColorStateList(cardView.getContext(), tintColor);
            cardView.getMainImageView().setImageTintList(colorStateList);

            // TODO load image where possible
        }

    }

    @Override
    public void onUnbindViewHolder(final Presenter.ViewHolder viewHolder) {
        //Log.d(TAG, "onUnbindViewHolder");
        final ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }

    public void setLongClickListener(final OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public interface OnLongClickListener {
        public boolean onLongClick(Movie m);
    }
}
