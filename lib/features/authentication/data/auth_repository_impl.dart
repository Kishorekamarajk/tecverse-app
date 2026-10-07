import '../../../core/networking/api_exceptions.dart';
import '../../../core/storage/token_storage.dart';
import '../domain/auth_repository.dart';
import '../domain/authenticated_user.dart';
import 'auth_api.dart';

/// Implementation of [AuthRepository] interacting directly with Spring Boot backend & PostgreSQL database.
class AuthRepositoryImpl implements AuthRepository {
  final AuthApi authApi;
  final TokenStorage tokenStorage;

  AuthRepositoryImpl({
    required this.authApi,
    required this.tokenStorage,
  });

  @override
  Future<AuthenticatedUser> login({
    required String identifier,
    required String password,
  }) async {
    final cleanIdentifier = identifier.trim().toLowerCase();
    final cleanPassword = password.trim();

    final data = await authApi.login(identifier: cleanIdentifier, password: cleanPassword);
    if (data.isNotEmpty && (data.containsKey('accessToken') || data.containsKey('token'))) {
      final tokens = AuthTokens.fromJson(data);
      final userProfile = UserProfile.fromJson(
        data['user'] is Map<String, dynamic> ? data['user'] : data,
      );
      await tokenStorage.saveTokens(
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
      );
      await tokenStorage.saveUserCache(userProfile.toJson());
      return AuthenticatedUser(tokens: tokens, profile: userProfile);
    }

    throw const ServerException('Invalid response received from authentication service.');
  }

  @override
  Future<String> requestPasswordReset({required String identifier}) async {
    final cleanIdentifier = identifier.trim().toLowerCase();
    final res = await authApi.requestPasswordReset(identifier: cleanIdentifier);
    if (res.containsKey('message')) {
      return res['message'].toString();
    }
    return 'OTP has been sent to your registered email.';
  }

  @override
  Future<bool> verifyResetOtp({
    required String identifier,
    required String otp,
  }) async {
    final cleanIdentifier = identifier.trim().toLowerCase();
    final res = await authApi.verifyResetOtp(
      identifier: cleanIdentifier,
      otp: otp.trim(),
    );
    if (res.containsKey('success')) {
      return res['success'] == true;
    }
    return true;
  }

  @override
  Future<void> verifyResetOtpAndSetPassword({
    required String identifier,
    required String otp,
    required String newPassword,
    String? confirmPassword,
  }) async {
    final cleanIdentifier = identifier.trim().toLowerCase();
    await authApi.resetPassword(
      identifier: cleanIdentifier,
      otp: otp.trim(),
      newPassword: newPassword.trim(),
      confirmPassword: confirmPassword?.trim() ?? newPassword.trim(),
    );
  }

  @override
  Future<UserProfile> getProfile({required String token}) async {
    try {
      final data = await authApi.fetchProfile(token);
      if (data.isNotEmpty) {
        final profile = UserProfile.fromJson(data);
        await tokenStorage.saveUserCache(profile.toJson());
        return profile;
      }
    } catch (_) {}

    // Read from local session cache if device is offline or backend is temporarily unreachable
    final cached = await tokenStorage.getUserCache();
    if (cached != null) {
      return UserProfile.fromJson(cached);
    }

    throw const UnauthorizedException('Session expired or profile unavailable.');
  }

  @override
  Future<UserProfile> updateInterests({
    required String token,
    required List<String> interests,
  }) async {
    final cached = await tokenStorage.getUserCache();
    UserProfile? currentProfile = cached != null ? UserProfile.fromJson(cached) : null;

    try {
      final data = await authApi.updateInterests(
        token: token,
        interests: interests,
        email: currentProfile?.email,
        referenceNumber: currentProfile?.referenceNumber,
      );
      if (data.isNotEmpty) {
        final updated = currentProfile != null
            ? currentProfile.copyWith(interests: interests)
            : UserProfile.fromJson(data);
        await tokenStorage.saveUserCache(updated.toJson());
        return updated;
      }
    } catch (_) {}

    if (currentProfile != null) {
      final updated = currentProfile.copyWith(interests: interests);
      await tokenStorage.saveUserCache(updated.toJson());
      return updated;
    }

    throw const UnauthorizedException('Session expired or profile unavailable.');
  }

  @override
  Future<void> logout({String? token}) async {
    await authApi.logout(token);
    await tokenStorage.clearAll();
  }
}

