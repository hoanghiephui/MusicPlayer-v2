package com.media.music.injector.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Hoang Hiep on 2016/11/1.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerApplication {
}

