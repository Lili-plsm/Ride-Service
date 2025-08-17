package ru.site;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtSettings {

  private String secretKey;
  private long expirationTimeAccess;
  private long expirationTimeRefresh;

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public long getExpirationTimeAccess() {
    return expirationTimeAccess;
  }

  public void setExpirationTimeAccess(long expirationTimeAccess) {
    this.expirationTimeAccess = expirationTimeAccess;
  }

  public long getExpirationTimeRefresh() {
    return expirationTimeRefresh;
  }

  public void setExpirationTimeRefresh(long expirationTimeRefresh) {
    this.expirationTimeRefresh = expirationTimeRefresh;
  }
}
