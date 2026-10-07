import 'dart:async';
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../authentication/presentation/auth_controller.dart';
import '../../../navigation/app_router.dart';

/// Pixel-perfect Initial Loading & Splash Animation Screen matching the reference design:
/// 1. Glowing 3D Earth Globe with orbital rings, holographic pedestal, and 6 orbiting tech domain nodes
/// 2. TEC-VERSE 2026 Typography with golden orbit swoosh and "Bridging Research. Building the Future." tagline
/// 3. Venue & Date Capsule: CHENNAI TRADE CENTRE | NOV 26–27, 2026
/// 4. Animated Gradient Progress Bar with glowing lead knob and live percentage counter
/// 5. Panoramic Convention Centre building backdrop with light-trail reflections
/// 6. "Designed and Developed by C-DAC" footer pill & Consortium attribution
class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen>
    with TickerProviderStateMixin {
  // Continuous rotation for orbital rings and floating tech badges
  late AnimationController _orbitController;
  late AnimationController _pulseController;
  late AnimationController _progressController;

  double _currentProgress = 0.0;

  @override
  void initState() {
    super.initState();

    // 1. Smooth slow rotation for orbital tracks (16s cycle)
    _orbitController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 16),
    )..repeat();

    // 2. Pulse / breathing glow animation (2.5s cycle)
    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 2500),
    )..repeat(reverse: true);

    // 3. Realistic loading progress bar (2.6s)
    _progressController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 2600),
    );

    _progressController.addListener(() {
      if (mounted) {
        setState(() {
          _currentProgress = _progressController.value;
        });
      }
    });

    _startLoadingSequence();
  }

  Future<void> _startLoadingSequence() async {
    _progressController.forward();

    // Check auth session in background while smooth animation plays
    final authController = context.read<AuthController>();
    final sessionCheckFuture = authController.checkSession();

    // Ensure smooth display duration (at least 2.7s for full visual experience)
    final minDelayFuture = Future.delayed(const Duration(milliseconds: 2700));

    final results = await Future.wait([sessionCheckFuture, minDelayFuture]);
    final isAuthenticated = results[0] as bool;

    if (!mounted) return;

    if (isAuthenticated) {
      Navigator.of(context).pushReplacementNamed(AppRouter.home);
    } else {
      Navigator.of(context).pushReplacementNamed(AppRouter.login);
    }
  }

  @override
  void dispose() {
    _orbitController.dispose();
    _pulseController.dispose();
    _progressController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        top: false,
        child: LayoutBuilder(
          builder: (context, constraints) {
            return Stack(
              children: [
                // ─── Background Ambient Light Streaks & Soft Glow ───
                Positioned.fill(
                  child: Container(
                    decoration: const BoxDecoration(
                      gradient: LinearGradient(
                        colors: [
                          Color(0xFFFFFFFF),
                          Color(0xFFF8FAFC),
                          Color(0xFFEFF6FF),
                        ],
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                      ),
                    ),
                  ),
                ),

                // Ambient Radial Blue-Cyan Top Aura
                Positioned(
                  top: -60,
                  left: 0,
                  right: 0,
                  height: constraints.maxHeight * 0.55,
                  child: Container(
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: RadialGradient(
                        colors: [
                          const Color(0xFF38BDF8).withValues(alpha: 0.18),
                          const Color(0xFF0284C7).withValues(alpha: 0.08),
                          Colors.transparent,
                        ],
                        radius: 0.65,
                      ),
                    ),
                  ),
                ),

                // ─── Bottom Architecture Backdrop with Light Trails ───
                Positioned(
                  left: 0,
                  right: 0,
                  bottom: 0,
                  height: constraints.maxHeight * 0.38,
                  child: _buildBottomBackdrop(),
                ),

                // ─── Main Interactive Foreground Content ───
                Positioned.fill(
                  child: SingleChildScrollView(
                    physics: const ClampingScrollPhysics(),
                    child: ConstrainedBox(
                      constraints: BoxConstraints(minHeight: constraints.maxHeight),
                      child: IntrinsicHeight(
                        child: Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              SizedBox(height: MediaQuery.of(context).padding.top + 8),

                              // 1. Globe with Holographic Base & 6 Orbiting Tech Domain Nodes
                              _buildGlobeWithOrbitalNodes(),

                              const SizedBox(height: 12),

                              // 2. TEC-VERSE 2026 Brand Title & Orbital Swoosh
                              _buildBrandTitleSection(),

                              const SizedBox(height: 8),

                              // 3. Tagline
                              _buildTagline(),

                              const SizedBox(height: 14),

                              // 4. Venue & Date Pill (CHENNAI TRADE CENTRE | NOV 26–27, 2026)
                              _buildVenueDateCapsule(),

                              const SizedBox(height: 22),

                              // 5. Loading Progress Bar & Live Counter
                              _buildLoadingProgressBar(),

                              const Spacer(),

                              // 6. Footer Pill & Attribution
                              _buildFooterAttribution(),

                              const SizedBox(height: 14),
                            ],
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }

  // ===========================================================================
  // 1. TOP GLOBE + HOLOGRAPHIC PEDESTAL + 6 ORBITING TECH NODES
  // ===========================================================================
  Widget _buildGlobeWithOrbitalNodes() {
    return SizedBox(
      width: 320,
      height: 290,
      child: Stack(
        alignment: Alignment.center,
        children: [
          // Background subtle concentric orbit guide rings
          CustomPaint(
            size: const Size(320, 280),
            painter: _OrbitalTrackPainter(
              pulseValue: _pulseController,
            ),
          ),

          // Glowing Holographic Pedestal at the bottom
          Positioned(
            bottom: 4,
            child: _buildHolographicPedestal(),
          ),

          // Central 3D Globe with atmosphere glow & planetary rings
          Positioned(
            top: 28,
            child: _buildCentralGlobe(),
          ),

          // ─── 6 Orbiting Tech Domain Badges ───
          // 1. Chip / Microprocessor Node (Top Center-Left - Orange)
          _buildFloatingBadge(
            left: 78,
            top: 10,
            icon: Icons.memory_rounded,
            badgeColor: const Color(0xFFEA580C),
            bgLight: const Color(0xFFFFF7ED),
            orbitAngleOffset: 0.0,
          ),

          // 2. Settings / Cog Node (Top Right - Blue)
          _buildFloatingBadge(
            right: 68,
            top: 24,
            icon: Icons.settings_rounded,
            badgeColor: const Color(0xFF0284C7),
            bgLight: const Color(0xFFEFF6FF),
            orbitAngleOffset: math.pi / 3,
          ),

          // 3. Satellite / Radar Comms Node (Middle Left - Blue)
          _buildFloatingBadge(
            left: 32,
            top: 76,
            icon: Icons.satellite_alt_rounded,
            badgeColor: const Color(0xFF1D4ED8),
            bgLight: const Color(0xFFEFF6FF),
            orbitAngleOffset: 2 * math.pi / 3,
          ),

          // 4. Analytics / Growth Chart Node (Middle Right - Orange)
          _buildFloatingBadge(
            right: 36,
            top: 106,
            icon: Icons.bar_chart_rounded,
            badgeColor: const Color(0xFFF97316),
            bgLight: const Color(0xFFFFF7ED),
            orbitAngleOffset: math.pi,
          ),

          // 5. Cloud Computing Node (Bottom Left - Sky Blue)
          _buildFloatingBadge(
            left: 34,
            top: 154,
            icon: Icons.cloud_rounded,
            badgeColor: const Color(0xFF0EA5E9),
            bgLight: const Color(0xFFF0F9FF),
            orbitAngleOffset: 4 * math.pi / 3,
          ),

          // 6. Eco / Green Deep-Tech Node (Bottom Right - Teal Green)
          _buildFloatingBadge(
            right: 40,
            top: 148,
            icon: Icons.eco_rounded,
            badgeColor: const Color(0xFF059669),
            bgLight: const Color(0xFFECFDF5),
            orbitAngleOffset: 5 * math.pi / 3,
          ),
        ],
      ),
    );
  }

  /// Central 3D Globe with planetary orbital glow rings
  Widget _buildCentralGlobe() {
    return AnimatedBuilder(
      animation: Listenable.merge([_orbitController, _pulseController]),
      builder: (context, child) {
        final pulse = _pulseController.value;
        return Stack(
          alignment: Alignment.center,
          children: [
            // Outer atmospheric neon aura
            Container(
              width: 174 + (pulse * 10),
              height: 174 + (pulse * 10),
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                gradient: RadialGradient(
                  colors: [
                    const Color(0xFF38BDF8).withValues(alpha: 0.35),
                    const Color(0xFF0284C7).withValues(alpha: 0.15),
                    Colors.transparent,
                  ],
                ),
              ),
            ),

            // Globe Image / Shader Sphere
            Container(
              width: 154,
              height: 154,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(
                    color: const Color(0xFF0284C7).withValues(alpha: 0.4),
                    blurRadius: 24,
                    spreadRadius: 2,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: ClipOval(
                child: Image.asset(
                  'assets/images/app_logo.png',
                  fit: BoxFit.cover,
                  errorBuilder: (context, error, stackTrace) => Container(
                    decoration: const BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: LinearGradient(
                        colors: [Color(0xFF0284C7), Color(0xFF1E40AF)],
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                      ),
                    ),
                    child: const Center(
                      child: Icon(Icons.public, color: Colors.white, size: 76),
                    ),
                  ),
                ),
              ),
            ),

            // Planetary Holographic Orbit Ring with Light Streaks
            Transform.rotate(
              angle: -0.35 + (math.sin(_orbitController.value * 2 * math.pi) * 0.08),
              child: Container(
                width: 200,
                height: 72,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(100),
                  border: Border.all(
                    color: const Color(0xFF38BDF8).withValues(alpha: 0.75),
                    width: 2.2,
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: const Color(0xFF0EA5E9).withValues(alpha: 0.5),
                      blurRadius: 12,
                    ),
                  ],
                ),
              ),
            ),

            // Secondary Golden Glowing Orbit Ring
            Transform.rotate(
              angle: 0.28 + (math.cos(_orbitController.value * 2 * math.pi) * 0.06),
              child: Container(
                width: 184,
                height: 64,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(100),
                  border: Border.all(
                    color: const Color(0xFFF97316).withValues(alpha: 0.7),
                    width: 1.8,
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: const Color(0xFFEA580C).withValues(alpha: 0.4),
                      blurRadius: 10,
                    ),
                  ],
                ),
              ),
            ),
          ],
        );
      },
    );
  }

  /// Holographic Pedestal at the bottom of the globe
  Widget _buildHolographicPedestal() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // Top glowing light plate
        Container(
          width: 130,
          height: 10,
          decoration: BoxDecoration(
            color: const Color(0xFF38BDF8).withValues(alpha: 0.4),
            borderRadius: BorderRadius.circular(20),
            boxShadow: [
              BoxShadow(
                color: const Color(0xFF0284C7).withValues(alpha: 0.6),
                blurRadius: 14,
                spreadRadius: 2,
              ),
            ],
          ),
        ),

        const SizedBox(height: 2),

        // Stepped metallic blue platform rings
        Container(
          width: 180,
          height: 14,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(30),
            gradient: const LinearGradient(
              colors: [
                Color(0xFF0284C7),
                Color(0xFF38BDF8),
                Color(0xFF0284C7),
              ],
            ),
            border: Border.all(color: const Color(0xFFBAE6FD), width: 1.0),
          ),
        ),

        const SizedBox(height: 2),

        // Base broad reflective glow ring
        Container(
          width: 240,
          height: 16,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(40),
            gradient: LinearGradient(
              colors: [
                const Color(0xFF0C4A6E).withValues(alpha: 0.8),
                const Color(0xFF0284C7),
                const Color(0xFF38BDF8),
                const Color(0xFF0284C7),
                const Color(0xFF0C4A6E).withValues(alpha: 0.8),
              ],
            ),
            boxShadow: [
              BoxShadow(
                color: const Color(0xFF0284C7).withValues(alpha: 0.45),
                blurRadius: 18,
                offset: const Offset(0, 4),
              ),
            ],
          ),
        ),
      ],
    );
  }

  /// Individual Floating Badge on Orbit
  Widget _buildFloatingBadge({
    double? left,
    double? right,
    double? top,
    double? bottom,
    required IconData icon,
    required Color badgeColor,
    required Color bgLight,
    required double orbitAngleOffset,
  }) {
    return Positioned(
      left: left,
      right: right,
      top: top,
      bottom: bottom,
      child: AnimatedBuilder(
        animation: _orbitController,
        builder: (context, child) {
          final t = _orbitController.value * 2 * math.pi + orbitAngleOffset;
          final dy = math.sin(t) * 3.5;
          final dx = math.cos(t) * 2.0;

          return Transform.translate(
            offset: Offset(dx, dy),
            child: Container(
              width: 38,
              height: 38,
              decoration: BoxDecoration(
                color: bgLight,
                shape: BoxShape.circle,
                border: Border.all(
                  color: badgeColor.withValues(alpha: 0.4),
                  width: 1.5,
                ),
                boxShadow: [
                  BoxShadow(
                    color: badgeColor.withValues(alpha: 0.25),
                    blurRadius: 10,
                    offset: const Offset(0, 3),
                  ),
                ],
              ),
              child: Center(
                child: Container(
                  width: 28,
                  height: 28,
                  decoration: BoxDecoration(
                    color: badgeColor,
                    shape: BoxShape.circle,
                  ),
                  child: Icon(
                    icon,
                    color: Colors.white,
                    size: 15,
                  ),
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  // ===========================================================================
  // 2. BRAND TITLE SECTION (TEC-VERSE 2026 + Golden Orbit Swoosh)
  // ===========================================================================
  Widget _buildBrandTitleSection() {
    return Stack(
      alignment: Alignment.center,
      children: [
        // Golden orbital swoosh arching over VERSE
        Positioned(
          top: -2,
          right: 32,
          child: CustomPaint(
            size: const Size(120, 32),
            painter: _GoldenSwooshPainter(),
          ),
        ),

        // Main Brand Row
        Row(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            // "TEC-" in Deep Navy Blue
            const Text(
              'TEC-',
              style: TextStyle(
                fontSize: 34,
                fontWeight: FontWeight.w900,
                color: Color(0xFF0B2265),
                letterSpacing: -0.5,
              ),
            ),

            // "VERSE" in Bright Gradient Orange
            ShaderMask(
              shaderCallback: (bounds) => const LinearGradient(
                colors: [Color(0xFFEA580C), Color(0xFFF97316)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ).createShader(bounds),
              child: const Text(
                'VERSE',
                style: TextStyle(
                  fontSize: 34,
                  fontWeight: FontWeight.w900,
                  color: Colors.white,
                  letterSpacing: -0.5,
                ),
              ),
            ),

            const SizedBox(width: 8),

            // "2026" Pill Container
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFF0284C7), Color(0xFF0EA5E9)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(10),
                boxShadow: [
                  BoxShadow(
                    color: const Color(0xFF0284C7).withValues(alpha: 0.3),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: const Text(
                '2026',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.w900,
                  letterSpacing: 0.5,
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }

  // ===========================================================================
  // 3. TAGLINE
  // ===========================================================================
  Widget _buildTagline() {
    return RichText(
      textAlign: TextAlign.center,
      text: const TextSpan(
        style: TextStyle(
          fontSize: 14,
          color: Color(0xFF475569),
          letterSpacing: 0.2,
        ),
        children: [
          TextSpan(
            text: 'Bridging Research. ',
            style: TextStyle(fontWeight: FontWeight.w500),
          ),
          TextSpan(
            text: 'Building the Future.',
            style: TextStyle(
              fontWeight: FontWeight.w800,
              color: Color(0xFF0F172A),
            ),
          ),
        ],
      ),
    );
  }

  // ===========================================================================
  // 4. VENUE & DATE CAPSULE
  // ===========================================================================
  Widget _buildVenueDateCapsule() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 9),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(30),
        border: Border.all(color: const Color(0xFFE2E8F0), width: 1.0),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF102A43).withValues(alpha: 0.04),
            blurRadius: 10,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          // Location Pin + Chennai Trade Centre
          const Icon(
            Icons.location_on_rounded,
            color: Color(0xFFEA580C),
            size: 16,
          ),
          const SizedBox(width: 5),
          const Text(
            'CHENNAI TRADE CENTRE',
            style: TextStyle(
              fontSize: 11,
              fontWeight: FontWeight.w800,
              color: Color(0xFF1E293B),
              letterSpacing: 0.4,
            ),
          ),

          const SizedBox(width: 10),

          // Divider
          Container(
            width: 1,
            height: 14,
            color: const Color(0xFFCBD5E1),
          ),

          const SizedBox(width: 10),

          // Calendar Icon + Nov 26-27, 2026
          const Icon(
            Icons.calendar_month_rounded,
            color: Color(0xFFEA580C),
            size: 16,
          ),
          const SizedBox(width: 5),
          const Text(
            'NOV 26–27, 2026',
            style: TextStyle(
              fontSize: 11,
              fontWeight: FontWeight.w800,
              color: Color(0xFF1E293B),
              letterSpacing: 0.4,
            ),
          ),
        ],
      ),
    );
  }

  // ===========================================================================
  // 5. LOADING PROGRESS BAR WITH GLOWING KNOB & PERCENTAGE
  // ===========================================================================
  Widget _buildLoadingProgressBar() {
    final percentInt = (_currentProgress * 100).clamp(0, 100).toInt();

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12),
      child: Column(
        children: [
          // Glowing Gradient Capsule Progress Track
          Container(
            height: 10,
            width: double.infinity,
            decoration: BoxDecoration(
              color: const Color(0xFFE2E8F0),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Stack(
              children: [
                // Filled progress bar
                FractionallySizedBox(
                  widthFactor: _currentProgress.clamp(0.02, 1.0),
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(10),
                      gradient: const LinearGradient(
                        colors: [
                          Color(0xFF0284C7),
                          Color(0xFF06B6D4),
                          Color(0xFFF97316),
                          Color(0xFFEA580C),
                        ],
                        stops: [0.0, 0.4, 0.8, 1.0],
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: const Color(0xFFEA580C).withValues(alpha: 0.45),
                          blurRadius: 8,
                          offset: const Offset(0, 1),
                        ),
                      ],
                    ),
                  ),
                ),

                // Glowing Leading Knob
                Align(
                  alignment: Alignment(-1.0 + (2.0 * _currentProgress.clamp(0.02, 1.0)), 0.0),
                  child: Container(
                    width: 14,
                    height: 14,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      shape: BoxShape.circle,
                      border: Border.all(
                        color: const Color(0xFFEA580C),
                        width: 2.5,
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: const Color(0xFFEA580C).withValues(alpha: 0.6),
                          blurRadius: 8,
                          spreadRadius: 2,
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),

          const SizedBox(height: 10),

          // Loading Text & Percentage Counter
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'Loading TEC-VERSE 2026...',
                style: TextStyle(
                  fontSize: 12.5,
                  fontWeight: FontWeight.w600,
                  color: Color(0xFF64748B),
                ),
              ),
              Text(
                '$percentInt%',
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w900,
                  color: Color(0xFFEA580C),
                  letterSpacing: 0.2,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  // ===========================================================================
  // 6. BOTTOM BACKDROP (Panoramic Convention Centre Architecture + Light Trails)
  // ===========================================================================
  Widget _buildBottomBackdrop() {
    return Stack(
      fit: StackFit.expand,
      children: [
        // Panoramic Convention Centre Architecture Image
        Image.asset(
          'assets/images/conbanner.png',
          fit: BoxFit.cover,
          alignment: Alignment.bottomCenter,
          errorBuilder: (context, error, stackTrace) => Image.asset(
            'assets/images/banner.jpg',
            fit: BoxFit.cover,
            errorBuilder: (context, error, stackTrace) => Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFFE0F2FE), Color(0xFFBAE6FD)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
            ),
          ),
        ),

        // Gradient overlay to blend seamlessly into white top background
        Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [
                Colors.white,
                Colors.white.withValues(alpha: 0.85),
                Colors.white.withValues(alpha: 0.0),
                Colors.white.withValues(alpha: 0.65),
                Colors.white.withValues(alpha: 0.95),
              ],
              stops: const [0.0, 0.18, 0.5, 0.85, 1.0],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
            ),
          ),
        ),
      ],
    );
  }

  // ===========================================================================
  // 7. FOOTER ATTRIBUTION (C-DAC Badge & CSC Consortium)
  // ===========================================================================
  Widget _buildFooterAttribution() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // Pill: Designed and Developed by C-DAC
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
          decoration: BoxDecoration(
            color: Colors.white.withValues(alpha: 0.92),
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: const Color(0xFFE2E8F0), width: 0.9),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withValues(alpha: 0.04),
                blurRadius: 8,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: 7,
                height: 7,
                decoration: const BoxDecoration(
                  color: Color(0xFFEA580C),
                  shape: BoxShape.circle,
                ),
              ),
              const SizedBox(width: 7),
              RichText(
                text: const TextSpan(
                  style: TextStyle(
                    fontSize: 11,
                    color: Color(0xFF334155),
                    fontWeight: FontWeight.w600,
                  ),
                  children: [
                    TextSpan(text: 'Designed and Developed by '),
                    TextSpan(
                      text: 'C-DAC',
                      style: TextStyle(
                        color: Color(0xFFEA580C),
                        fontWeight: FontWeight.w900,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),

        const SizedBox(height: 6),

        // Consortium Subtitle
        const Text(
          'Consortium of scientific societies (CSC) • MeitY • C-DAC • SAMEER • C-MET',
          style: TextStyle(
            fontSize: 9.5,
            fontWeight: FontWeight.w500,
            color: Color(0xFF64748B),
            letterSpacing: 0.2,
          ),
          textAlign: TextAlign.center,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
      ],
    );
  }
}

// =============================================================================
// CUSTOM PAINTERS FOR ORBITAL TRACKS & GOLDEN SWOOSH
// =============================================================================

/// Custom painter for the orbital dotted rings and constellation connection arcs
class _OrbitalTrackPainter extends CustomPainter {
  final Animation<double> pulseValue;

  _OrbitalTrackPainter({required this.pulseValue})
      : super(repaint: pulseValue);

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2 - 10);

    // Inner orbital circle
    final innerPaint = Paint()
      ..color = const Color(0xFF38BDF8).withValues(alpha: 0.3)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.0;
    canvas.drawCircle(center, 90, innerPaint);

    // Middle orbital dotted track
    final middlePaint = Paint()
      ..color = const Color(0xFF0284C7).withValues(alpha: 0.22)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.2;
    canvas.drawCircle(center, 120, middlePaint);

    // Outer faint orbital track
    final outerPaint = Paint()
      ..color = const Color(0xFFF97316).withValues(alpha: 0.18)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.0;
    canvas.drawCircle(center, 142, outerPaint);
  }

  @override
  bool shouldRepaint(covariant _OrbitalTrackPainter oldDelegate) => true;
}

/// Custom painter for the golden orbital swoosh over "VERSE"
class _GoldenSwooshPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..shader = const LinearGradient(
        colors: [
          Color(0xFF38BDF8),
          Color(0xFFF59E0B),
          Color(0xFFF97316),
        ],
      ).createShader(Rect.fromLTWH(0, 0, size.width, size.height))
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2.2
      ..strokeCap = StrokeCap.round;

    final path = Path();
    path.moveTo(4, size.height - 4);
    path.quadraticBezierTo(
      size.width * 0.45,
      -6,
      size.width - 8,
      size.height * 0.4,
    );
    canvas.drawPath(path, paint);

    // Sparkle star at apex
    final starPaint = Paint()..color = const Color(0xFFF59E0B);
    canvas.drawCircle(Offset(size.width * 0.52, 2), 2.5, starPaint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
