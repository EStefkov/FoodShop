package bg.emiliyan.acc_backend.security;

import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


public class CookieUtils {

    public static ResponseCookie createJwtCookie(String token) {
        // For local development without HTTPS set secure to false. In production
        // this should be true. Here we detect a common environment variable
        // (SPRING_PROFILES_ACTIVE) to enable secure cookies in prod-like profiles.
        boolean secure = false;
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null) profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profile != null && (profile.contains("prod") || profile.contains("production"))) {
            secure = true;
        }

        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60)
                .build();
    }

    public static String getJwtFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
