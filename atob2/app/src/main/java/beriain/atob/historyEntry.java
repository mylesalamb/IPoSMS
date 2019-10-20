package beriain.atob;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class historyEntry implements Serializable {
    private String title;
    private String url;
    private Date date;

    public historyEntry(String t, String u, Date d){
        title = t;
        url = u;
        date = d;
    }

    public String getTitle(){
        return title;
    }

    public String getUrl(){
        return url;
    }

    public String getDate(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
