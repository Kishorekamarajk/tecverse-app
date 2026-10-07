import 'package:flutter/foundation.dart' show kIsWeb;

/// Centralized Production / Staging API Configuration for TEC-VERSE.
///
/// Automatically points to the staging HTTPS backend:
/// `https://ngypr123.bosschn.in/tecverse`
///
/// Can also be dynamically overridden at build time via:
/// `--dart-define=API_BASE_URL=https://...`
class ApiConfig {
  ApiConfig._();

  /// Active Backend IP on current Wi-Fi network
  static const String springBootHost = '10.184.37.107';
  static const int port = 2303;

  /// Staging / Server Fallback IPs
  static const String companyDbHost = '10.184.48.20';
  static const String stagingBaseUrl = 'https://ngypr123.bosschn.in/tecverse';

  static String? _resolvedBaseUrl;

  /// Centralized base URL for all API requests
  static String get baseUrl {
    const definedUrl = String.fromEnvironment('API_BASE_URL');
    if (definedUrl.isNotEmpty) {
      return _normalizeUrl(definedUrl);
    }

    if (_resolvedBaseUrl != null) return _resolvedBaseUrl!;

    // Default to active running backend on current laptop IP
    return 'http://$springBootHost:$port/api';
  }

  /// Ordered candidate base URLs to attempt in sequence
  static List<String> get candidateBaseUrls {
    const definedUrl = String.fromEnvironment('API_BASE_URL');
    if (definedUrl.isNotEmpty) {
      return [_normalizeUrl(definedUrl)];
    }

    return [
      // 1st Priority: Active backend running on current laptop IP
      'http://$springBootHost:$port/api',
      // 2nd Priority: Company server host
      'http://$companyDbHost:$port/api',
      // 3rd Priority: Localhost / Emulator fallbacks
      if (!kIsWeb) ...[
        'http://10.0.2.2:$port/api',
        'http://127.0.0.1:$port/api',
        'http://localhost:$port/api',
      ],
      // 4th Priority: Staging web URL
      _normalizeUrl(stagingBaseUrl),
    ];
  }

  /// Ensures URL ends with `/api` without double slashes
  static String _normalizeUrl(String url) {
    var trimmed = url.trim();
    while (trimmed.endsWith('/')) {
      trimmed = trimmed.substring(0, trimmed.length - 1);
    }
    return trimmed.endsWith('/api') ? trimmed : '$trimmed/api';
  }

  /// Cache a successfully verified base URL
  static void setResolvedBaseUrl(String url) {
    _resolvedBaseUrl = url;
  }

  /// Direct URL for physical devices
  static String get physicalDeviceBaseUrl => baseUrl;
}
