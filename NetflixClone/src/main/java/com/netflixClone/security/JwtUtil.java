//package com.netflixClone.security;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//import javax.crypto.SecretKey;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//
//@Component
//public class JwtUtil {
//
//    private static final long JWT_TOKEN_VALIDITY = 30L * 24 * 60 * 60 * 1000; // 30 days
//
//    @Value("${jwt.secret}")
//    private String secret;
//
//    // ✅ HS256 signing key (Base64 decoded)
//    private SecretKey getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secret);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    // ================= TOKEN READ =================
//
//    public String getUsernameFromToken(String token) {
//        return getClaimFromToken(token, Claims::getSubject);
//    }
//
//    public String getRoleFromToken(String token) {
//        return getClaimFromToken(token, claims -> claims.get("role", String.class));
//    }
//
//    public Date getExpirationDateFromToken(String token) {
//        return getClaimFromToken(token, Claims::getExpiration);
//    }
//
//    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = getAllClaimsFromToken(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    private boolean isTokenExpired(String token) {
//        return getExpirationDateFromToken(token).before(new Date());
//    }
//
//    // ================= TOKEN CREATE =================
//
//    public String generateToken(String username, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", role);
//        return createToken(claims, username);
//    }
//
//    private String createToken(Map<String, Object> claims, String subject) {
//        return Jwts.builder()
//                .claims(claims)
//                .subject(subject)
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // ================= VALIDATION =================
//
//    public boolean validateToken(String token) {
//        try {
//            getAllClaimsFromToken(token);
//            return !isTokenExpired(token);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}



package com.netflixClone.security; 

import java.util.Date;
import java.util.HashMap; 
import java.util.Map; 
import java.util.function.Function; 
import javax.crypto.SecretKey; 
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Component; 
import io.jsonwebtoken.Claims; 
import io.jsonwebtoken.Jwts; 
import io.jsonwebtoken.security.Keys;

@Component 
public class JwtUtil { 
	
	private static final long JWT_TOKEN_VALIDITY = 30L*24*60*60*1000; 
	
	@Value("${jwt.secret:defaultSecretKeyForNetflixClonedefaultSecretKeyForNetflixClone}") 
	private String secret; 
	
	private SecretKey getSigningKey() { 
		return Keys.hmacShaKeyFor(secret.getBytes()); 
	} 
	
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject); 
	}
	
	public String getRoleFromToken(String token) { 
		return getClaimFromToken(token, claims -> claims.get("role", String.class));
	}
	
	public Date getExpirationDateFromToken(String token) { 
		return getClaimFromToken(token, Claims::getExpiration); 
	}
	
	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) { 
		final Claims claims = getAllClaimsFromToken(token); 
		return claimsResolver.apply(claims);
	}
	
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload(); 
	}
	
	private boolean isTokenExpired(String token) { 
		final Date expiration = getExpirationDateFromToken(token); 
		return expiration.before(new Date()); 
	}
	
	public String generateToken(String username, String role) { 
		Map<String, Object> claims = new HashMap<>(); 
		claims.put("role", role);
		return doGenerateToken(claims, username);
	}
	
	private String doGenerateToken(Map<String, Object> claims, String username) {
		return Jwts.builder() 
				.claims(claims) 
				.subject(username) 
				.issuedAt(new Date(System.currentTimeMillis())) 
				.expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) 
				.signWith(getSigningKey()) 
				.compact(); 
	}
	
	public Boolean validateToken(String token) {
		try { 
			getAllClaimsFromToken(token); 
			return !isTokenExpired(token); 
		} catch (Exception e) { 
			return false; 
		}
	}
}

