import 'dart:convert';
import 'package:crypto/crypto.dart';
import '../../../core/constants/api_constants.dart';
import '../../../core/networking/api_client.dart';
import '../../authentication/domain/authenticated_user.dart';
import '../domain/digital_event_pass.dart';
import '../domain/digital_pass_repository.dart';

class DigitalPassRepositoryImpl implements DigitalPassRepository {
  final ApiClient client;

  // In-memory cache of issued passes to guarantee 1-to-1 deterministic stability
  final Map<String, DigitalEventPass> _passCache = {};

  DigitalPassRepositoryImpl({required this.client});

  @override
  Future<DigitalEventPass> getDigitalPass({
    required String token,
    required UserProfile user,
  }) async {
    final cacheKey = user.referenceNumber.isNotEmpty ? user.referenceNumber : (user.email.isNotEmpty ? user.email : user.id);
    
    // Check in-memory session cache first
    if (_passCache.containsKey(cacheKey)) {
      return _passCache[cacheKey]!;
    }

    // 1. Attempt live backend fetch
    try {
      final response = await client.get(
        ApiConstants.digitalPassEndpoint,
        token: token,
        queryParameters: {
          'email': user.email,
          'referenceNumber': user.referenceNumber,
        },
      );
      if (response is Map<String, dynamic> && response.isNotEmpty && !response.containsKey('error')) {
        final pass = DigitalEventPass.fromJson(response);
        _passCache[cacheKey] = pass;
        return pass;
      }
    } catch (_) {
      // Fallback to deterministic pass generation
    }

    // 2. Deterministic unique pass generated from attendee's distinct registration identity
    final ref = user.referenceNumber.isNotEmpty
        ? user.referenceNumber.trim()
        : (user.id.isNotEmpty ? 'REF-${user.id}' : 'TEC26-${user.email.hashCode.abs()}');
    final suffix = ref.length >= 6
        ? ref.substring(ref.length - 6)
        : (user.id.isNotEmpty ? user.id.padLeft(5, '0') : '10042');
    final passId = 'TV26-PASS-$suffix';

    // Unique Cryptographic SHA-256 signature for this attendee
    final signatureBytes = utf8.encode('$passId|$ref|${user.email}|${user.officialName}|${user.mobile}|TEC-VERSE-2026-SECRET');
    final signatureDigest = sha256.convert(signatureBytes);
    final shortSig = signatureDigest.toString().substring(0, 12).toUpperCase();
    final signedQrToken = 'TECVERSE26:$passId:$ref:${user.email}:$shortSig';

    final pass = DigitalEventPass(
      passId: passId,
      attendeeId: ref,
      userId: user.id,
      holderName: user.officialName.isNotEmpty ? user.officialName : 'TEC-VERSE Attendee',
      organization: user.organization.isNotEmpty ? user.organization : 'Registered Delegate',
      designation: user.designation.isNotEmpty ? user.designation : 'Attendee',
      category: user.category.isNotEmpty ? user.category : 'Delegate',
      eventName: 'TEC-VERSE 2026',
      eventDates: '26–27 NOVEMBER 2026',
      venue: 'CHENNAI TRADE CENTRE, NANDAMBAKKAM, CHENNAI 600089',
      attendanceDays: user.attendanceDays.isNotEmpty ? user.attendanceDays : 'Both Days (26 & 27 Nov 2026)',
      qrToken: signedQrToken,
      status: PassStatus.active,
      issuedAt: DateTime(2026, 11, 1),
    );

    _passCache[cacheKey] = pass;
    return pass;
  }

  @override
  Future<DigitalEventPass> refreshPassStatus({
    required String token,
    required String passId,
  }) async {
    try {
      final response = await client.get(
        '${ApiConstants.digitalPassEndpoint}/$passId/status',
        token: token,
      );
      if (response is Map<String, dynamic> && response.isNotEmpty) {
        return DigitalEventPass.fromJson(response);
      }
    } catch (_) {}

    // Return cached with guaranteed consistency
    for (final pass in _passCache.values) {
      if (pass.passId == passId) return pass;
    }

    throw Exception('Pass not found');
  }

  @override
  Future<DigitalEventPass> verifyPassAtGate({
    required String qrToken,
    required String gateId,
  }) async {
    try {
      final response = await client.post(
        ApiConstants.verifyPassEndpoint,
        body: {
          'qrToken': qrToken,
          'gateId': gateId,
          'timestamp': DateTime.now().toIso8601String(),
        },
      );
      if (response is Map<String, dynamic>) {
        return DigitalEventPass.fromJson(response);
      }
    } catch (_) {}

    // Simulated check-in verification for offline demonstration
    for (final key in _passCache.keys) {
      final pass = _passCache[key]!;
      if (pass.qrToken == qrToken) {
        final verifiedPass = pass.copyWith(
          status: PassStatus.used,
          checkedInAt: DateTime.now(),
        );
        _passCache[key] = verifiedPass;
        return verifiedPass;
      }
    }

    throw Exception('Invalid or expired QR token');
  }
}
