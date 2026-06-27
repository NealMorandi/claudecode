package com.dom.dormitory.config;

import com.dom.dormitory.constant.StatusConstant;
import com.dom.dormitory.util.JwtUtil;
import com.dom.dormitory.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT認証フィルター
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            try {
                // ホワイトリスト確認（ログイン済みトークン）
                String whitelistKey = StatusConstant.REDIS_AUTH_PREFIX + token;
                if (!Boolean.TRUE.equals(redisUtil.hasKey(whitelistKey))) {
                    log.warn("無効なトークン（ホワイトリスト未登録）: {}", request.getRequestURI());
                    filterChain.doFilter(request, response);
                    return;
                }

                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsername(token);
                    Long userId = jwtUtil.getUserId(token);
                    String role = jwtUtil.getRole(token);

                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authentication.setDetails(userId);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.warn("JWT処理エラー: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /** Authorizationヘッダーからトークンを抽出 */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
