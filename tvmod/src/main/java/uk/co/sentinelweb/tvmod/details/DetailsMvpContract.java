package uk.co.sentinelweb.tvmod.details;

import co.uk.sentinelweb.lantv.net.smb.url.SmbLocation;
import uk.co.sentinelweb.tvmod.model.Item;
import uk.co.sentinelweb.tvmod.mvp.BaseMvpContract;

public interface DetailsMvpContract {
    interface Presenter extends BaseMvpContract.Presenter<View>{

        void setupData(SmbLocation location, Item m);
    }

    interface View extends BaseMvpContract.View<Presenter> {

        void setData(DetailsFragmentModel model);

        void launchSystemPlayer();

        void launchExoplayer();

        void launchVlc();

        void launchMxPlayer();
    }

}
