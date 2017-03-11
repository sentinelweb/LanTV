package uk.co.sentinelweb.tvmod.browse;

import dagger.Subcomponent;
import uk.co.sentinelweb.tvmod.ActivityScope;

/**
 * Created by robert on 10/03/2017.
 */

@Subcomponent(modules = SmbBrowseActivityModule.class)
@ActivityScope
public interface SmbBrowseActivityComponent {
    void inject(SmbBrowseActivity activity);
    void inject(SmbBrowseFragment fragment);// TODO should really make a component for the fragemnt

}
