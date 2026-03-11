package com.salon.dto;

public class TopItemResponse {

    private String name;
    private long count;

    public TopItemResponse(String name, long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() { return name; }
    public long getCount() { return count; }
}
