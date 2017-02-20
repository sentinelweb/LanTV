package uk.co.sentinelweb.tvmod.browse;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.mvp.BaseMvpContract;

public interface SmbBrowseMvpContract {
    interface Presenter extends BaseMvpContract.Presenter<View>{

        void setupData(SmbLocation location);

        void launchMovie(Movie item);
    }

    interface View extends BaseMvpContract.View<Presenter> {

        void setData(SmbBrowseFragmentModel model);

        void updateBackGround();

        void launchExoplayer(Movie movie);

//        void launchVlc(Movie movie);
//
//        void showDownloadDialog();
//
//        void updateDownloadDialog(String message);
//
//        void closeDownloadDialog();
//
        void showError(Throwable throwable);

        void showError(String message);

//        File getBufferFile(Movie movie);

        void launchDetails(SmbLocation location, Movie movie);

        void launchBrowser(SmbLocation parent);

        void launchVlc(Movie movie);

        void launchMxPlayer(Movie movie);

        void finish();
    }

}
