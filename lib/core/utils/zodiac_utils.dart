/// Utility functions for zodiac-related operations
class ZodiacUtils {
  ZodiacUtils._();

  /// Get zodiac PNG image asset path from zodiac code
  /// Returns the asset path for the zodiac image, or null if code is invalid
  static String? getZodiacImagePath(String code) {
    final normalizedCode = code.toLowerCase().trim();
    
    // Map zodiac codes to PNG file names
    final imageMap = {
      'ti': 'assets/ti.png',
      'suu': 'assets/suu.png',
      'dan': 'assets/dan.png',
      'mao': 'assets/mao.png',
      'thin': 'assets/thin.png',
      'ty': 'assets/ti(ran).png',
      'ngo': 'assets/ngo.png',
      'mui': 'assets/mui.png',
      'than': 'assets/than.png',
      'dau': 'assets/dau.png',
      'tuat': 'assets/tuat.png',
      'hoi': 'assets/hoi.png',
    };
    
    return imageMap[normalizedCode];
  }

  /// Get zodiac Vietnamese name from code
  static String? getZodiacName(String code) {
    final normalizedCode = code.toLowerCase().trim();
    
    final nameMap = {
      'ti': 'Tý',
      'suu': 'Sửu',
      'dan': 'Dần',
      'mao': 'Mão',
      'thin': 'Thìn',
      'ty': 'Tỵ',
      'ngo': 'Ngọ',
      'mui': 'Mùi',
      'than': 'Thân',
      'dau': 'Dậu',
      'tuat': 'Tuất',
      'hoi': 'Hợi',
    };
    
    return nameMap[normalizedCode];
  }
}
