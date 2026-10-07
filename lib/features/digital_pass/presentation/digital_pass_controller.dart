import 'package:flutter/foundation.dart';
import '../../authentication/domain/authenticated_user.dart';
import '../domain/digital_event_pass.dart';
import '../domain/digital_pass_repository.dart';

class DigitalPassController extends ChangeNotifier {
  final DigitalPassRepository repository;
  DigitalEventPass? _pass;
  bool _isLoading = false;
  String? _errorMessage;

  DigitalPassController({required this.repository});

  DigitalEventPass? get pass => _pass;
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  Future<void> loadPass({
    required String token,
    required UserProfile user,
  }) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _pass = await repository.getDigitalPass(token: token, user: user);
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _errorMessage = 'Failed to load Digital Event Pass: $e';
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> refreshPassStatus({required String token}) async {
    if (_pass == null) return;
    try {
      final updated = await repository.refreshPassStatus(
        token: token,
        passId: _pass!.passId,
      );
      _pass = updated;
      notifyListeners();
    } catch (_) {}
  }

  void clearPass() {
    _pass = null;
    _errorMessage = null;
    _isLoading = false;
    notifyListeners();
  }
}
