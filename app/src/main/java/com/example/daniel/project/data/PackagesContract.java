package com.example.daniel.project.data;

/**
 * Created by daniel on 3/02/15.
 */

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class PackagesContract {
    public static final String CONTENT_AUTHORITY = "com.example.daniel.project";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PACKAGES = "packages";
    public static final String PATH_PACKAGE = "package";
    public static final String PATH_PREFERENCE = "preference";
    public static final String PATH_PREFERENCES = "preferences";
    public static final String PATH_PREFERENCES_STRING = "preferences_string";


    public static final class PackageEntry implements BaseColumns {
        public static final Uri CONTENT_URI_LIST = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PACKAGES).build();
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PACKAGE).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/"+ CONTENT_AUTHORITY + "/" + PATH_PACKAGES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/"+ CONTENT_AUTHORITY + "/" + PATH_PACKAGES;
        public static final String TABLE_NAME = "packages";
        public static final String ACTIVE_STRING = "active";
        public static final String INACTIVE_STRING = "inactive";

        public static final String COLUMN_PACKAGE_INFO = "package_info";
        public static final String COLUMN_PACKAGE_ACTIVE = "package_forward_active";
        public static final String COLUMN_PACKAGE_ID = "_id";
        public static final String COLUMN_PACKAGE_NAME = "package_name";
        public static final String COLUMN_PACKAGE_ICON = "package_icon";

        public static Uri buildPackageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_LIST, id);
        }

        public static String getPackageIdForUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getPackageNameForUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PreferencesEntry implements BaseColumns {
        public static final String TABLE_NAME = "preferences";

        public static final Uri  CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PREFERENCE).build();
        public static final Uri  CONTENT_URI_LIST = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PREFERENCES).build();
        public static final Uri CONTENT_URI_STRING_LIST = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PREFERENCES_STRING).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/"+ CONTENT_AUTHORITY + "/" + PATH_PREFERENCE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/"+ CONTENT_AUTHORITY + "/" + PATH_PREFERENCE;
        public static final String COLUMN_PACKAGE_ID = "package_id";
        public static final String COLUMN_FORWARD_METHOD = "forward_method";
        public static final String MAIL_METHOD = "mail";
        public static final String WEB_METHOD = "web";
        public static final String COLUMN_EXTRA_PARAM = "extra_param";

        public static Uri buildPrefrencesPackage(String packageSetting) {
            return CONTENT_URI.buildUpon().appendPath(packageSetting).build();
        }
    }
}
