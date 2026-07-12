package bg.emiliyan.acc_backend.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public class CookieUtils {

    private static final String COOKIE_NAME = "access_token";
    private static final int MAX_AGE = 15 * 60;

    public static ResponseCookie createJwtCookie(String token) {

        boolean secure = isProduction();

        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(MAX_AGE)
                .build();
    }

    public static ResponseCookie clearJwtCookie() {

        boolean secure = isProduction();

        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

    public static String getJwtFromRequest(HttpServletRequest request) {

        if (request == null || request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static boolean isProduction() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }

        return profile != null &&
                (profile.contains("prod") || profile.contains("production"));
    }
}