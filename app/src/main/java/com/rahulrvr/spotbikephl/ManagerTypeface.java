/*
 * Copyright (c) 2015 Elsevier, Inc. All rights reserved.
 */

package com.rahulrvr.spotbikephl;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;

/**
 * This manager class is on charge for typeface caching and dispatching.
 * <p/>
 * Uses a factory to create the typefaces that are not created yet and store them as soon as they
 * are created.
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class ManagerTypeface {
    private static final SparseArray<Typeface> typefacesCache = new SparseArray<>();

    public static Typeface getTypeface(Context context, int typeface) {
        synchronized (typefacesCache) {
            if (typefacesCache.indexOfKey(typeface) < 0) {
                typefacesCache.put(typeface, FactoryTypeface.createTypeface(context, typeface));
            }

            return typefacesCache.get(typeface);
        }
    }
}
