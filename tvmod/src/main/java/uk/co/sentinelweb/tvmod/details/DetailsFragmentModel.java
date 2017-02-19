package uk.co.sentinelweb.tvmod.details;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.model.Category;
import uk.co.sentinelweb.tvmod.model.Movie;

public class DetailsFragmentModel {
    private SmbLocation _location;

    private Category _category;

    private Movie _movie;

    public SmbLocation getLocation() {
        return _location;
    }

    public void setLocation(final SmbLocation location) {
        _location = location;
    }

    public Category getCategory() {
        return _category;
    }

    public void setCategory(final Category category) {
        this._category = category;
    }

    public Movie getMovie() {
        return _movie;
    }

    public void setMovie(final Movie movie) {
        this._movie = movie;
    }
}
