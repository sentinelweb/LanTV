package uk.co.sentinelweb.tvmod.browse;

import java.util.List;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.model.Category;

/**
 * Model for main MVP
 */
public class SmbBrowseFragmentModel {

    private SmbLocation _location;

    private List<Category> _categories;

    private String _title;

    public SmbBrowseFragmentModel(final SmbLocation location, final String title, final List<Category> movies) {
        _location = location;

        _title = title;

        _categories = movies;
    }

    public List<Category> getCategories() {
        return _categories;
    }

    public String getTitle() {
        return _title;
    }

    public void setCategories(final List<Category> categories) {
        _categories = categories;
    }

    public void setTitle(final String title) {
        _title = title;
    }

    public void setLocation(final SmbLocation location) {
        this._location = location;
    }

    public SmbLocation getLocation() {
        return _location;
    }
}
