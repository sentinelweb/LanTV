package uk.co.sentinelweb.tvmod.details;

import dagger.Subcomponent;
import uk.co.sentinelweb.tvmod.ActivityScope;

/**
 * Created by robert on 10/03/2017.
 */

@Subcomponent(modules = DetailsActivityModule.class)
@ActivityScope
public interface DetailsActivityComponent {
    void inject(DetailsActivity activity);
    void inject(DetailsFragment fragment);// TODO should really make a component for the fragemnt

}
