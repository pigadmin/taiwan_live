package com.live.event;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateTime {


    public UpdateTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    private long time;
    private SimpleDateFormat yMd = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat Hm = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat Md = new SimpleDateFormat("MM/dd");
    private SimpleDateFormat E = new SimpleDateFormat("E");
    private SimpleDateFormat HMmd = new SimpleDateFormat("HH:mm MM/dd");
    private SimpleDateFormat yMdHmsE = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss E");

    public String getYmd() {
        return yMd.format(new Date(time));
    }

    public String getHm() {
        return Hm.format(new Date(time));
    }

    public String getYmdhmse() {
        return yMdHmsE.format(new Date(time));
    }


}
