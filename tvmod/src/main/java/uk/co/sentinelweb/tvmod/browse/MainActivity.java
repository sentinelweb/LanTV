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

import android.app.Activity;
import android.os.Bundle;

import java.io.File;

import uk.co.sentinelweb.tvmod.R;
import uk.co.sentinelweb.tvmod.util.FileUtils;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {

    public static final String TEST_MOVIE = "ETRG.mp4";
    public static final String SAMPLE_MOVIE = "sample.avi";
    public static File TEST_MOVIE_FILE = null;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TEST_MOVIE_FILE = new File(getFilesDir(), SAMPLE_MOVIE);
//        FileUtils.copyFileFromAsset(this, SAMPLE_MOVIE, TEST_MOVIE_FILE);
    }


}
