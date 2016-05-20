package ilmnuri.com.model;


import android.os.Environment;

/**
 * Created by Administrator on 3/7/2016.
 */
public class Api {
    public static String BaseUrl = "https://s3.amazonaws.com/api.ilmnuri.com/";

    public static String all_category = "http://api.azimov.xyz/api/v1.0/albums/";
    public static String catetory1 = "http://api.azimov.xyz/api/v1.0/albums/Abdulloh/";
    public static String catetory2 = "http://api.azimov.xyz/api/v1.0/albums/AbuNur/";
    public static String catetory3 = "http://api.azimov.xyz/api/v1.0/albums/Ayyubxon/";

    public static String catetory1_image = "http://images.ilmnuri.com.s3-website-us-west-2.amazonaws.com/ilm.jpg";
    public static String catetory2_image = "http://images.ilmnuri.com.s3-website-us-west-2.amazonaws.com/abunur.jpg";
    public static String catetory3_image = "http://images.ilmnuri.com.s3-website-us-west-2.amazonaws.com/ayyubxon.jpg";

    public static String about_us = "https://s3-us-west-2.amazonaws.com/images.ilmnuri.com/aboutus";

    public static String localPath = Environment.getExternalStorageDirectory().toString() + "/ilmnuri";
}
