package io.jenkins.plugins.customizable_header;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.Util;
import jakarta.activation.MimetypesFileTypeMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
public final class RemoteAssetCache {
  private static final Logger LOGGER = Logger.getLogger(RemoteAssetCache.class.getName());

  private static final Cache<String, Boolean> allowedUrlsCache =
      Caffeine.newBuilder()
          .maximumSize(1000)
          .expireAfterWrite(65, TimeUnit.MINUTES)
          .build();

  // ~20MB total cache, entries expire after 1h.
  private static final Cache<String, CachedResource> CACHE =
      Caffeine.newBuilder()
          .maximumWeight(20 * 1024 * 1024)
          .weigher((String k, CachedResource v) -> v.bytes.length)
          .expireAfterWrite(1, TimeUnit.HOURS)
          .build();

  private static final HttpClient CLIENT =
      HttpClient.newBuilder()
          .followRedirects(HttpClient.Redirect.NORMAL)
          .connectTimeout(Duration.ofSeconds(5))
          .version(HttpClient.Version.HTTP_2)
          .build();

  static final class CachedResource {
    final byte[] bytes;
    final String contentType;
    final long fetchedAtMillis;

    CachedResource(byte[] bytes, String contentType) {
      this.bytes = bytes;
      this.contentType = contentType;
      this.fetchedAtMillis = System.currentTimeMillis();
    }
  }

  public static void addUrlToCache(String url) {
    allowedUrlsCache.put(url, true);
  }

  @CheckForNull
  static CachedResource get(String url) {
    String norm = normalize(url);
    if (norm == null) return null;

    return CACHE.get(norm, key -> {
      try {
        URI uri = new URI(key);
        HttpRequest req =
            HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .header("User-Agent", "Jenkins-CustomHeader/1.0")
                .build();
        HttpResponse<byte[]> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofByteArray());
        if (resp.statusCode() != 200) {
          LOGGER.log(Level.FINE, "Remote asset {0} returned {1}", new Object[] {uri, resp.statusCode()});
          return null;
        }
        byte[] body = resp.body();
        String ctype = detectContentType(resp.headers().firstValue("Content-Type").orElse(null), body, uri);
        return new CachedResource(body, ctype);
      } catch (IOException | InterruptedException | URISyntaxException e) {
        LOGGER.log(Level.FINE, "Failed to fetch remote asset: " + key, e);
        return null;
      }
    });
  }

  private static String normalize(String url) {
    String u = Util.fixEmptyAndTrim(url);
    if (u == null) return null;
    try {
      URI uri = new URI(u);
      if (!uri.isAbsolute()) return null;
      String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
      if (!"http".equals(scheme) && !"https".equals(scheme)) return null;
      // Normalize scheme/host case; keep path/query; drop fragment.
      String host = Optional.ofNullable(uri.getHost()).orElse("").toLowerCase(Locale.ROOT);
      int port = uri.getPort();
      String authority = port == -1 ? host : host + ":" + port;
      return new URI(
          scheme,
          authority,
          uri.getRawPath(),
          uri.getRawQuery(),
          null /* fragment */)
          .toString();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  public static boolean isAllowed(String url) {
    return Boolean.TRUE.equals(allowedUrlsCache.getIfPresent(url));
  }

  private static String detectContentType(String headerValue, byte[] body, URI uri) {
    if (headerValue != null && !headerValue.isBlank()) {
      // Strip parameters
      String type = headerValue.split(";", 2)[0].trim();
      if (!type.isEmpty()) return type;
    }
    // Heuristic: SVG
    try {
      String head = new String(body, 0, Math.min(body.length, 256), java.nio.charset.StandardCharsets.UTF_8);
      if (head.contains("<svg")) return "image/svg+xml; charset=utf-8";
    } catch (Exception ignored) {}
    // Guess from path
    String path = uri.getPath().toLowerCase(Locale.ROOT);
    if (path.endsWith(".png")) return "image/png";
    if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
    if (path.endsWith(".gif")) return "image/gif";
    if (path.endsWith(".webp")) return "image/webp";
    if (path.endsWith(".ico")) return "image/x-icon";
    if (path.endsWith(".svg")) return "image/svg+xml; charset=utf-8";
    // Fallback
    try {
      String guess = new MimetypesFileTypeMap().getContentType(path);
      if (guess != null && !guess.isBlank()) return guess;
    } catch (Exception ignored) {}
    try {
      String guess = java.net.URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(body));
      if (guess != null && !guess.isBlank()) return guess;
    } catch (IOException ignored) {}
    return "application/octet-stream";
  }

  private RemoteAssetCache() {}
}
