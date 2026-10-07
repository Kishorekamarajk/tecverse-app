import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Abstract contract for secure token and credential persistence.
abstract class TokenStorage {
  Future<void> saveTokens({required String accessToken, String? refreshToken});
  Future<String?> getAccessToken();
  Future<String?> getRefreshToken();
  Future<void> saveUserCache(Map<String, dynamic> userData);
  Future<Map<String, dynamic>?> getUserCache();
  Future<void> clearAll();
}

/// Platform-safe implementation utilizing FlutterSecureStorage with SharedPreferences fallback.
class SecureTokenStorageImpl implements TokenStorage {
  static const String _keyAccessToken = 'tecverse_access_token';
  static const String _keyRefreshToken = 'tecverse_refresh_token';
  static const String _keyUserData = 'tecverse_user_profile_cache';

  final FlutterSecureStorage _secureStorage;

  SecureTokenStorageImpl({FlutterSecureStorage? secureStorage})
      : _secureStorage = secureStorage ??
            const FlutterSecureStorage(
              aOptions: AndroidOptions(),
              iOptions: IOSOptions(accessibility: KeychainAccessibility.first_unlock),
              mOptions: MacOsOptions(accessibility: KeychainAccessibility.first_unlock),
              wOptions: WindowsOptions(useBackwardCompatibility: true),
            );

  @override
  Future<void> saveTokens({required String accessToken, String? refreshToken}) async {
    try {
      await _secureStorage.write(key: _keyAccessToken, value: accessToken);
      if (refreshToken != null) {
        await _secureStorage.write(key: _keyRefreshToken, value: refreshToken);
      }
    } catch (_) {
      // Fallback to SharedPreferences if platform secure storage is not provisioned
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_keyAccessToken, accessToken);
      if (refreshToken != null) {
        await prefs.setString(_keyRefreshToken, refreshToken);
      }
    }
  }

  @override
  Future<String?> getAccessToken() async {
    try {
      final token = await _secureStorage.read(key: _keyAccessToken);
      if (token != null) return token;
    } catch (_) {}
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_keyAccessToken);
  }

  @override
  Future<String?> getRefreshToken() async {
    try {
      final token = await _secureStorage.read(key: _keyRefreshToken);
      if (token != null) return token;
    } catch (_) {}
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_keyRefreshToken);
  }

  @override
  Future<void> saveUserCache(Map<String, dynamic> userData) async {
    try {
      final jsonStr = jsonEncode(userData);
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(_keyUserData, jsonStr);
    } catch (_) {}
  }

  @override
  Future<Map<String, dynamic>?> getUserCache() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final jsonStr = prefs.getString(_keyUserData);
      if (jsonStr != null && jsonStr.isNotEmpty) {
        return jsonDecode(jsonStr) as Map<String, dynamic>;
      }
    } catch (_) {}
    return null;
  }

  @override
  Future<void> clearAll() async {
    try {
      await _secureStorage.delete(key: _keyAccessToken);
      await _secureStorage.delete(key: _keyRefreshToken);
    } catch (_) {}
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(_keyAccessToken);
      await prefs.remove(_keyRefreshToken);
      await prefs.remove(_keyUserData);
    } catch (_) {}
  }
}
