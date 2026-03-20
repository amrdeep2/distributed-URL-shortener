package com.example.url.Dtos;

import java.time.LocalDateTime;

public class ResponseDto {
    private String code;
    private String shortUrl;
    private String longUrl;
    private LocalDateTime expiresAt;
    public ResponseDto(String code, String shortUrl, String longUrl, LocalDateTime expiresAt) {
        this.code = code;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
