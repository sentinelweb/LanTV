package uk.co.sentinelweb.tvmod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * A scope that exists for the loader lifetime
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {

}