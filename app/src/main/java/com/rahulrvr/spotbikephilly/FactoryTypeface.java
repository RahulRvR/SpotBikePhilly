/*
 * Copyright (c) 2015 Elsevier, Inc. All rights reserved.
 */

package com.rahulrvr.spotbikephilly;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Typeface creation.
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class FactoryTypeface {
    public static Typeface createTypeface(Context context, int typeface) {
        return Typeface.createFromAsset(context.getAssets(),
                String.format("font/%s.ttf", context.getString(typeface)));
    }
}
