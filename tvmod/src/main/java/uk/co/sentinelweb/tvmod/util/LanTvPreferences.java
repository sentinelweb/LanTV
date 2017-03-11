package uk.co.sentinelweb.tvmod.util;

import android.app.Application;
import android.content.SharedPreferences;

import javax.inject.Inject;

import co.uk.sentinelweb.lantv.net.smb.TestData;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import co.uk.sentinelweb.lantv.net.smb.url.SmbLocationParser;
import co.uk.sentinelweb.lantv.net.smb.url.SmbUrlBuilder;
import uk.co.sentinelweb.tvmod.LanTvApplication;

/**
 * Created by robert on 10/03/2017.
 */

public class LanTvPreferences {

    public static final String KEY_LAST_LOCATION = "lastLocation";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    SharedPreferences preferences;

    @Inject
    public LanTvPreferences(final LanTvApplication app) {
        this.preferences = app.getSharedPreferences(app.getPackageName(), Application.MODE_PRIVATE);
    }

    public SmbLocation getLastLocation() {
        final String pref = preferences.getString(KEY_LAST_LOCATION, null);
        final SmbLocationParser parser = new SmbLocationParser();
        if (pref==null) {return parser.parse("smb://");}

        final SmbLocation lastLocation = parser.parse(pref);
        lastLocation.setUsername(getUserName());
        lastLocation.setPassword(getPassword());
        return lastLocation;
    }

    public String getUserName() {
        return preferences.getString(KEY_USERNAME, TestData.USER);
    }

    /**
     * TODO encrypt password
     * @return
     */
    public String getPassword() {
        return preferences.getString(KEY_PASSWORD, TestData.PASS);
    }

    public void saveLastLocation(final SmbLocation location) {
        final SmbLocation location1 = location.clone();
        final SharedPreferences.Editor edit = preferences.edit();
        final String user = location1.getUsername();
        location1.setUsername(null);
        final String pass = location1.getPassword();
        location1.setPassword(null);

        edit.putString(KEY_LAST_LOCATION, new SmbUrlBuilder().build(location1));
        if (user!=null) {
            edit.putString(KEY_USERNAME, user);
        }
        if (pass!=null) {
            edit.putString(KEY_PASSWORD, pass);
        }
        edit.apply();
    }
}
