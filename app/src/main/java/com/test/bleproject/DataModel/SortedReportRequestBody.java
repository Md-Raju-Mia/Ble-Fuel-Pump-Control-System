package com.test.bleproject.DataModel;

public class SortedReportRequestBody {
    String id;
    String startdate;
    String enddate;


    public SortedReportRequestBody(String id, String startdate, String enddate) {
        this.id = id;
        this.startdate=startdate;
        this.enddate=enddate;
    }
}
