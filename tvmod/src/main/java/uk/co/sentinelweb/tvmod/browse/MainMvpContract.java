package uk.co.sentinelweb.tvmod.browse;

import java.io.File;

import uk.co.sentinelweb.tvmod.model.Movie;
import uk.co.sentinelweb.tvmod.mvp.BaseMvpContract;

public interface MainMvpContract  {
    interface Presenter extends BaseMvpContract.Presenter<View>{

        void launchMovie(Movie item);
    }

    interface View extends BaseMvpContract.View<Presenter> {

        void setData(MainFragmentModel model);

        void updateBackGround();

        void launchExoplayer(Movie movie);

        void launchVlc(Movie movie);

        void showDownloadDialog();

        void updateDownloadDialog(String message);

        void closeDownloadDialog();

        void showError(Throwable throwable);

        void showError(String message);

        File getBufferFile(Movie movie);
    }

}
