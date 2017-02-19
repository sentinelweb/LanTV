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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.R;
import uk.co.sentinelweb.tvmod.browse.SmbBrowseActivity;
import uk.co.sentinelweb.tvmod.model.Movie;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsActivity extends Activity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String MOVIE = "Movie";
    public static final String LOCATION = "Location";

    public static Intent getIntent(final Context c, final SmbLocation location, final Movie movie) {
        final Intent i = new Intent(c, SmbBrowseActivity.class);
        i.putExtra(LOCATION, location);
        i.putExtra(MOVIE, location);
        return i;
    }

    DetailsMvpContract.View fragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        fragment = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details_fragment);
        final DetailsMvpContract.Presenter presenter = new DetailsPresenter(fragment);
        fragment.setPresenter(presenter);
    }

}
