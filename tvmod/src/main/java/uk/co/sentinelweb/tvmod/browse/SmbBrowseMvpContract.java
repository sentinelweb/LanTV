package uk.co.sentinelweb.tvmod.browse;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.model.Item;
import uk.co.sentinelweb.tvmod.mvp.BaseMvpContract;

public interface SmbBrowseMvpContract {
    interface Presenter extends BaseMvpContract.Presenter<View>{

        void setupData(SmbLocation location);

        void launchMovie(Item item);

        void loadDirectory(Item item);

        void launchDetails(Item m);

        void onStart();

    }

    interface View extends BaseMvpContract.View<Presenter> {

        void setData(SmbBrowseFragmentModel model);

        void updateBackGround();

        void launchExoplayer(Item item);

//        void launchVlcSmb(Movie movie);
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

        void launchDetails(SmbLocation location, Item item);

        void launchBrowser(SmbLocation parent);

        void launchVlc(Item item);

        void launchMxPlayer(Item item);

        void finishActivity();

        void showLoading(boolean show);
    }

}
