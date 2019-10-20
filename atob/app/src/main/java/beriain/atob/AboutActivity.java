package beriain.atob;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SharedPreferences preferences;
        String about = "<html><head><title>atob</title><meta charset=\"utf-8\"><meta name=\"" +
                "viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, " +
                "minimum-scale=1\"><style>body{margin:0;text-align:center;font-family:sans-serif;" +
                "}img{margin-top:10px;width:25%;}h5{margin-top:-20px;color:grey;}h2{margin-top:-10px;" +
                "}p{text-align:justify;padding:10px;margin-top:-15px;}</style></head><body>" +
                "<p style=\"margin:10px;\">atob uses <a href=\"https://jsoup.org/\">Jsoup</a>, " +
                "distributed under the <a href=\"https://jsoup.org/license\">MIT license</a>.</p>" +
                "<hr><img src=\"file:///android_asset/logo.png\" /><h2>atob</h2><h5><i>Version: 1.0</i></h5><h5>" +
                "<a href=\"https://github.com/beriain/atob\">https://github.com/beriain/atob</a>" +
                "</h5><p>This program is free software: you can redistribute it and/or modify it" +
                "under the terms of the GNU General Public License as published by the Free" +
                "Software Foundation, either version 3 of the License, or(at your option) any" +
                "later version.</p><p>This program is distributed in the hope that it will be" +
                "useful,but WITHOUT ANY WARRANTY; without even the implied warranty of " +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See theGNU General Public" +
                "License for more details.</p><p>You should have received a copy of the GNU" +
                "General Public License along with this program. If not, see " +
                "<a href=\"http://www.gnu.org/licenses\">http://www.gnu.org/licenses</a>.</p></body></html>";

        String darkAbout = "<html><head><title>atob</title><meta charset=\"utf-8\"><meta name=\"" +
                "viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, " +
                "minimum-scale=1\"><style>html{color:#cccccc;background:#444444;}a{color:#2196F3;}" +
                "body{margin:0;text-align:center;font-family:sans-serif;" +
                "}img{margin-top:10px;width:25%;}h5{margin-top:-20px;color:grey;}h2{margin-top:-10px;" +
                "}p{text-align:justify;padding:10px;margin-top:-15px;}</style></head><body>" +
                "<p style=\"margin:10px;\">atob uses <a href=\"https://jsoup.org/\">Jsoup</a>, " +
                "distributed under the <a href=\"https://jsoup.org/license\">MIT license</a>.</p>" +
                "<hr><img src=\"file:///android_asset/logo.png\" /><h2>atob</h2><h5><i>Version: 1.0</i></h5><h5>" +
                "<a href=\"https://github.com/beriain/atob\">https://github.com/beriain/atob</a>" +
                "</h5><p>This program is free software: you can redistribute it and/or modify it" +
                "under the terms of the GNU General Public License as published by the Free" +
                "Software Foundation, either version 3 of the License, or(at your option) any" +
                "later version.</p><p>This program is distributed in the hope that it will be" +
                "useful,but WITHOUT ANY WARRANTY; without even the implied warranty of " +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See theGNU General Public" +
                "License for more details.</p><p>You should have received a copy of the GNU" +
                "General Public License along with this program. If not, see " +
                "<a href=\"http://www.gnu.org/licenses\">http://www.gnu.org/licenses</a>.</p></body></html>";

        WebView wv = (WebView) findViewById(R.id.webViewAbout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString("themes", "1").compareToIgnoreCase(("1")) == 0)
            wv.loadDataWithBaseURL("", about, "text/html", "utf-8", "");
        else
            wv.loadDataWithBaseURL("", darkAbout, "text/html", "utf-8", "");
    }
}
