package com.example.url.Dtos;

public class StatsDto {

    private long totalLinks;
    private long activeLinks;
    private long expiredLinks;

    public StatsDto(long totalLinks, long activeLinks, long expiredLinks) {
        this.totalLinks = totalLinks;
        this.activeLinks = activeLinks;
        this.expiredLinks = expiredLinks;
    }

    public long getTotalLinks() { return totalLinks; }
    public long getActiveLinks() { return activeLinks; }
    public long getExpiredLinks() { return expiredLinks; }
}