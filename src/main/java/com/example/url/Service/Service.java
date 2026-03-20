package com.example.url.Service;

import com.example.url.Dtos.RequestDto;
import com.example.url.Dtos.ResponseDto;
import com.example.url.Dtos.StatsDto;
import com.example.url.LinkRepository.LinkRepository;
import com.example.url.Model.Link;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
//import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
@org.springframework.stereotype.Service
public class Service {

   // private Link link;
    private final LinkRepository repo;
    private final StringRedisTemplate redisTemplate;

    public Service(LinkRepository linkRepository,StringRedisTemplate redis) {
        this.repo = linkRepository;
        this.redisTemplate = redis;
    }

    public ResponseDto createLink(RequestDto dto, String ip) {
        checkRateLimit(ip);
        System.out.println("RATE LIMIT CHECK CALLED, ip = " + ip);
        String longUrl = dto.getLongUrl() != null ? dto.getLongUrl().trim() : null;
        String customAlias = dto.getCustomAlias() != null ? dto.getCustomAlias().trim() : null;
        LocalDateTime expiresAtText = dto.getExpiresAt() != null ? dto.getExpiresAt() : null;

        if (longUrl == null || longUrl.isBlank()) {
            throw new RuntimeException("longUrl is required");
        }

        if (!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
            throw new RuntimeException("longUrl must start with http:// or https://");
        }

        String code;

        if (customAlias != null && !customAlias.isBlank()) {
            code = customAlias;

            if (repo.existsByCode(code)) {
                throw new RuntimeException("Custom alias already exists");
            }
        } else {
            int maxAttempts = 10;
            code = null;

            for (int i = 0; i < maxAttempts; i++) {
                String generatedCode = generateRandomCode(6);

                if (!repo.existsByCode(generatedCode)) {
                    code = generatedCode;
                    break;
                }
            }

            if (code == null) {
                throw new RuntimeException("Could not generate a unique short code");
            }
        }

        LocalDateTime expiresAt = dto.getExpiresAt();

        Link link = new Link();
        link.setCode(code);
        link.setLongUrl(longUrl);
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(expiresAt);
        link.setActive(true);

        Link savedLink = repo.save(link);

        return new ResponseDto(
                savedLink.getCode(),
                "http://localhost:8080/" + savedLink.getCode(),
                savedLink.getLongUrl(),
                savedLink.getExpiresAt()
        );


    }

    public String redirectLogic(String code) {
        String key = "code:" + code;

        String cachedUrl = redisTemplate.opsForValue().get(key);

        if (cachedUrl != null) {
            System.out.println("Redis Cache Working");
            return cachedUrl;
        }

        Optional<Link> linkOptional = repo.findByCode(code);

        if (linkOptional.isEmpty()) {
            throw new RuntimeException("Short URL not found");
        }

        Link link = linkOptional.get();

        if (!link.isActive()) {
            throw new RuntimeException("Link is inactive");
        }

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link has expired");
        }
        redisTemplate.opsForValue().set(key, link.getLongUrl());

        return link.getLongUrl();
    }
    private String generateRandomCode(int length) {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {

            int index = random.nextInt(characters.length());

            code.append(characters.charAt(index));
        }

        return code.toString();
    }
    public void checkRateLimit(String ip) {

        String key = "rl:create:" + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        if (count >= 10) {
            throw new RuntimeException("Too many requests");
        }
    }
    public List<Link> getAllLinks() {
        return repo.findAll();
    }
    public StatsDto getStats() {

        long total = repo.count();

        long active = repo.countByIsActiveTrue();

        long expired = repo.countByExpiresAtBefore(LocalDateTime.now());

        return new StatsDto(total, active, expired);
    }

}


