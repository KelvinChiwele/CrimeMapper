package com.techart.crimemapper.utils;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to override system fonts all over the application
 */
public class TypefaceUtil {

    public static final int REGULAR = 1;
    public static final int BOLD = 2;
    public static final int ITALIC = 3;
    public static final int BOLD_ITALIC = 4;
    public static final int LIGHT = 5;
    public static final int CONDENSED = 6;
    public static final int THIN = 7;
    public static final int MEDIUM = 8;

    public static final String SANS_SERIF = "sans-serif";
    public static final String SANS_SERIF_LIGHT = "sans-serif-light";
    public static final String SANS_SERIF_CONDENSED = "sans-serif-condensed";
    public static final String SANS_SERIF_THIN = "sans-serif-thin";
    public static final String SANS_SERIF_MEDIUM = "sans-serif-medium";

    public static final String FIELD_DEFAULT = "DEFAULT";
    public static final String FIELD_SANS_SERIF = "SANS_SERIF";
    public static final String FIELD_SERIF = "SERIF";
    public static final String FIELD_DEFAULT_BOLD = "DEFAULT_BOLD";

    public static void overrideFonts(Context context){
        Typeface regular = getTypeface(REGULAR, context);
        Typeface light = getTypeface(REGULAR, context);
        Typeface condensed = getTypeface(CONDENSED, context);
        Typeface thin = getTypeface(THIN, context);
        Typeface medium = getTypeface(MEDIUM, context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Map<String, Typeface> fonts = new HashMap<>();
            fonts.put(SANS_SERIF, regular);
            fonts.put(SANS_SERIF_LIGHT, light);
            fonts.put(SANS_SERIF_CONDENSED, condensed);
            fonts.put(SANS_SERIF_THIN, thin);
            fonts.put(SANS_SERIF_MEDIUM, medium);
            overrideFontsMap(fonts);
        } else {
            overrideFont(FIELD_SANS_SERIF, getTypeface(REGULAR, context));
            overrideFont(FIELD_SERIF, getTypeface(REGULAR, context));
        }
        overrideFont(FIELD_DEFAULT, getTypeface(REGULAR, context));
        overrideFont(FIELD_DEFAULT_BOLD, getTypeface(BOLD, context));
    }

    /**
     * Using reflection to override default typefaces
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE
     * OVERRIDDEN
     *
     * @param typefaces map of fonts to replace
     */
    private static void overrideFontsMap(Map<String, Typeface> typefaces) {
        try {
            final Field field = Typeface.class.getDeclaredField("sSystemFontMap");
            field.setAccessible(true);
            Map<String, Typeface> oldFonts = null;
            if (oldFonts != null) {
                oldFonts.putAll(typefaces);
            } else {
                oldFonts = typefaces;
            }
            field.set(null, oldFonts);
            field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            //Crashlytics.logException(e);
            Log.e("TypefaceUtil", "Can not set custom fonts, NoSuchFieldException");
        } catch (IllegalAccessException e) {
           // Crashlytics.logException(e);
            Log.e("TypefaceUtil", "Can not set custom fonts, IllegalAccessException");
        }
    }

    public static void overrideFont(String fontName, Typeface typeface) {
        try {
            final Field field = Typeface.class.getDeclaredField(fontName);
            field.setAccessible(true);
            field.set(null, typeface);
            field.setAccessible(false);
        } catch (Exception e) {
           // Crashlytics.logException(e);
            Log.e("TypefaceUtil", "Can not set custom font " + typeface.toString() + " instead of " + fontName);
        }
    }

    public static Typeface getTypeface(int fontType, Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/time-new-roman.ttf");
    }

}
