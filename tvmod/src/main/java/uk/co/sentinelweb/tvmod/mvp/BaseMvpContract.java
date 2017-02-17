package uk.co.sentinelweb.tvmod.mvp;

/**
 * Created by robert on 16/02/2017.
 */
public interface BaseMvpContract {
    interface Presenter<V extends View> {
        void subscribe();
        void unsubscribe();
    }

    interface View<P extends Presenter> {
        void setPresenter(P presenter);
    }
}
