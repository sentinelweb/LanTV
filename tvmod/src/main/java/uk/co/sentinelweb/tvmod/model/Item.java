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

package uk.co.sentinelweb.tvmod.model;

import android.net.Uri;
import android.util.Log;

import java.io.Serializable;

import uk.co.sentinelweb.tvmod.util.Extension;

/*
 * Movie class represents video entity with title, description, image thumbs and video url.
 * TODO make autovalue
 */
public class Item implements Serializable {
    static final long serialVersionUID = 727566175075960653L;

    private String title;
    private String description;
    private String bgImageUrl;
    private String cardImageUrl;
    private String videoUrl;
    private Extension extension;
    private String category;
    private long position;
    private long duration;

    public Item() {
    }

    public Item(final String title, final String description, final String videoUrl, final String bgImageUrl, final String cardImageUrl, final String category, final long duration, final Extension extension, final long position) {
        this.bgImageUrl = bgImageUrl;
        this.cardImageUrl = cardImageUrl;
        this.category = category;
        this.description = description;
        this.duration = duration;
        this.extension = extension;
        this.position = position;
        this.title = title;
        this.videoUrl = videoUrl;
    }

    public Item clone() {
        final Item clone = new Item();
        clone.bgImageUrl = this.bgImageUrl;
        clone.cardImageUrl = this.cardImageUrl;
        clone.category = this.category;
        clone.description = this.description;
        clone.duration = this.duration;
        clone.extension = this.extension;
        clone.position = this.position;
        clone.title = this.title;
        clone.videoUrl = this.videoUrl;
        return clone;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(final Extension extension) {
        this.extension = extension;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(final String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getBackgroundImageUrl() {
        return bgImageUrl;
    }

    public void setBackgroundImageUrl(final String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(final String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(final long position) {
        this.position = position;
    }

    public Uri getBackgroundImageURI() {
        try {
            Log.d("BACK ITEM: ", bgImageUrl);
            return Uri.parse(getBackgroundImageUrl());
        } catch (final Exception e) {
            Log.d("URI exception: ", "problem with background URI"+bgImageUrl);
            return null;
        }
    }

    public Uri getCardImageURI() {
        try {
            return Uri.parse(getCardImageUrl());
        } catch (final Exception e) {
            Log.d("URI exception: ", "problem with background URI"+getCardImageUrl());
            return null;
        }
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Item item = (Item) o;

        if (position != item.position) {
            return false;
        }
        if (duration != item.duration) {
            return false;
        }
        if (title != null ? !title.equals(item.title) : item.title != null) {
            return false;
        }
        if (description != null ? !description.equals(item.description) : item.description != null) {
            return false;
        }
        if (bgImageUrl != null ? !bgImageUrl.equals(item.bgImageUrl) : item.bgImageUrl != null) {
            return false;
        }
        if (cardImageUrl != null ? !cardImageUrl.equals(item.cardImageUrl) : item.cardImageUrl != null) {
            return false;
        }
        if (videoUrl != null ? !videoUrl.equals(item.videoUrl) : item.videoUrl != null) {
            return false;
        }
        if (extension != null ? !extension.equals(item.extension) : item.extension != null) {
            return false;
        }
        return category != null ? category.equals(item.category) : item.category == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (bgImageUrl != null ? bgImageUrl.hashCode() : 0);
        result = 31 * result + (cardImageUrl != null ? cardImageUrl.hashCode() : 0);
        result = 31 * result + (videoUrl != null ? videoUrl.hashCode() : 0);
        result = 31 * result + (extension != null ? extension.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (int) (position ^ (position >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", category='" + category + '\'' +
                ", bgImageUrl='" + bgImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", extension='" + extension + '\'' +
                ", position=" + position +
                ", duration=" + duration +
                '}';
    }
}
