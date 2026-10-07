import '../config/api_config.dart';

/// API Constants for TEC-VERSE 2026.
///
/// Designed to connect directly to the local TEC-VERSE backend
/// (running by default on port 3000) while remaining fully configurable through
/// environment variables or build flags.
class ApiConstants {
  ApiConstants._();

  /// Centralized Base URL resolved via [ApiConfig]
  static String get baseUrl => ApiConfig.baseUrl;

  /// Official Website Registration URL (where users register before using the app)
  static const String websiteRegistrationUrl = String.fromEnvironment(
    'WEBSITE_REGISTRATION_URL',
    defaultValue: 'https://ngypr123.bosschn.in/tecverse',
  );

  /// Health Check Endpoint
  static const String healthEndpoint = '/api/health';

  /// Core Event Master Data Endpoints (from PostgreSQL backend)
  static const String eventEndpoint = '/api/event';
  static const String scheduleEndpoint = '/api/schedule';
  static const String exhibitorsEndpoint = '/api/exhibitors';
  static const String floorPlanEndpoint = '/api/floor-plan';
  static const String technologyDomainsEndpoint = '/api/technology-domains';
  static const String thematicSessionsEndpoint = '/api/thematic-sessions';
  static const String importantDatesEndpoint = '/api/important-dates';
  static const String newsEndpoint = '/api/news';
  static const String notificationsEndpoint = '/api/notifications';
  static const String faqEndpoint = '/api/faq';

  /// Profile & Attendee Data
  static const String profileEndpoint = '/api/profile';
  static const String userProfileEndpoint = '/api/profile';
  static const String userInterestsEndpoint = '/api/me/interests';

  /// Digital Event Pass & QR Verification Endpoints
  static const String digitalPassEndpoint = '/api/digital-pass';
  static const String refreshPassEndpoint = '/api/digital-pass';
  static const String verifyPassEndpoint = '/api/pass/verify';

  /// Authentication & User Session Endpoints
  static const String loginEndpoint = '/api/auth/login';
  static const String logoutEndpoint = '/api/auth/logout';
  static const String forgotPasswordEndpoint = '/api/auth/forgot-password';
  static const String verifyResetOtpEndpoint = '/api/auth/verify-reset-otp';
  static const String resetPasswordEndpoint = '/api/auth/reset-password';

  static const String speakersEndpoint = '/api/speakers';
  static const String thematicDomainsEndpoint = '/api/technology-domains';

  /// HTTP Headers
  static const String headerAuthorization = 'Authorization';
  static const String headerContentType = 'Content-Type';
  static const String headerAccept = 'Accept';
  static const String contentTypeJson = 'application/json';
  static const String bearerPrefix = 'Bearer ';

  /// Timeouts
  /// connectTimeout: per-candidate timeout. With 4 Android candidates, worst-case = 32s.
  /// Primary (10.184.48.20) will succeed instantly when on company network.
  static const Duration connectTimeout = Duration(seconds: 8);
  static const Duration receiveTimeout = Duration(seconds: 30);
}
