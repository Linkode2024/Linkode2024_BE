package com.linkode.api_server.util;

import com.linkode.api_server.domain.Member;
import com.linkode.api_server.domain.base.BaseStatus;
import com.linkode.api_server.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.NoSuchElementException;

@Component
public class JwtProvider {
    private final MemberRepository memberRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JwtProvider(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // Access Token 생성
    public String createAccessToken(String githubId) {
        Member member = memberRepository.findByGithubIdAndStatus(githubId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("해당 깃허브 아이디로 유저를 찾을 수 없습니다.: " + githubId));
        Claims claims = Jwts.claims().setSubject(githubId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("memberId", member.getMemberId())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String githubId) {
        Member member = memberRepository.findByGithubIdAndStatus(githubId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("해당 깃허브 아이디로 유저를 찾을 수 없습니다.: " + githubId));
        Claims claims = Jwts.claims().setSubject(githubId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("memberId", member.getMemberId())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Access Token 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 사용자 정보 추출
    public String extractGithubId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validate JWT token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String githubId = extractGithubId(token);
        return (githubId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
