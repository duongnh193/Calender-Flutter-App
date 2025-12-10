/// Zodiac information
class ZodiacInfo {
  final int id;
  final String code;
  final String nameVi;
  final String? nameEn;
  final String? description;

  ZodiacInfo({
    required this.id,
    required this.code,
    required this.nameVi,
    this.nameEn,
    this.description,
  });

  factory ZodiacInfo.fromJson(Map<String, dynamic> json) => ZodiacInfo(
        id: json['id'] as int? ?? json['zodiacId'] as int? ?? 0,
        code: json['code'] as String? ?? json['zodiacCode'] as String? ?? '',
        nameVi: json['nameVi'] as String? ?? json['zodiacName'] as String? ?? '',
        nameEn: json['nameEn'] as String?,
        description: json['description'] as String?,
      );
}

/// Daily horoscope response
class HoroscopeDaily {
  final ZodiacInfo zodiac;
  final DateTime solarDate;
  final String summary;
  final String career;
  final String love;
  final String health;
  final String fortune;
  final Map<String, dynamic>? metadata;

  HoroscopeDaily({
    required this.zodiac,
    required this.solarDate,
    required this.summary,
    required this.career,
    required this.love,
    required this.health,
    required this.fortune,
    this.metadata,
  });

  factory HoroscopeDaily.fromJson(Map<String, dynamic> json) => HoroscopeDaily(
        zodiac: ZodiacInfo(
          id: json['zodiacId'] as int? ?? 0,
          code: json['zodiacCode'] as String? ?? '',
          nameVi: json['zodiacName'] as String? ?? '',
        ),
        solarDate: json['solarDate'] != null 
            ? DateTime.parse(json['solarDate'] as String)
            : DateTime.now(),
        summary: json['summary'] as String? ?? '',
        career: json['career'] as String? ?? '',
        love: json['love'] as String? ?? '',
        health: json['health'] as String? ?? '',
        fortune: json['fortune'] as String? ?? '',
        metadata: json['metadata'] as Map<String, dynamic>?,
      );
}

/// Monthly horoscope response
class HoroscopeMonthly {
  final ZodiacInfo zodiac;
  final int year;
  final int month;
  final String summary;
  final String career;
  final String love;
  final String health;
  final String fortune;
  final Map<String, dynamic>? metadata;

  HoroscopeMonthly({
    required this.zodiac,
    required this.year,
    required this.month,
    required this.summary,
    required this.career,
    required this.love,
    required this.health,
    required this.fortune,
    this.metadata,
  });

  factory HoroscopeMonthly.fromJson(Map<String, dynamic> json) =>
      HoroscopeMonthly(
        zodiac: ZodiacInfo(
          id: json['zodiacId'] as int? ?? 0,
          code: json['zodiacCode'] as String? ?? '',
          nameVi: json['zodiacName'] as String? ?? '',
        ),
        year: json['year'] as int? ?? DateTime.now().year,
        month: json['month'] as int? ?? DateTime.now().month,
        summary: json['summary'] as String? ?? '',
        career: json['career'] as String? ?? '',
        love: json['love'] as String? ?? '',
        health: json['health'] as String? ?? '',
        fortune: json['fortune'] as String? ?? '',
        metadata: json['metadata'] as Map<String, dynamic>?,
      );
}

/// Yearly horoscope response
class HoroscopeYearly {
  final ZodiacInfo zodiac;
  final int year;
  final String summary;
  final String career;
  final String love;
  final String health;
  final String fortune;
  final Map<String, dynamic>? metadata;

  HoroscopeYearly({
    required this.zodiac,
    required this.year,
    required this.summary,
    required this.career,
    required this.love,
    required this.health,
    required this.fortune,
    this.metadata,
  });

  factory HoroscopeYearly.fromJson(Map<String, dynamic> json) => HoroscopeYearly(
        zodiac: ZodiacInfo(
          id: json['zodiacId'] as int? ?? 0,
          code: json['zodiacCode'] as String? ?? '',
          nameVi: json['zodiacName'] as String? ?? '',
        ),
        year: json['year'] as int? ?? DateTime.now().year,
        summary: json['summary'] as String? ?? '',
        career: json['career'] as String? ?? '',
        love: json['love'] as String? ?? '',
        health: json['health'] as String? ?? '',
        fortune: json['fortune'] as String? ?? '',
        metadata: json['metadata'] as Map<String, dynamic>?,
      );
}

/// Lifetime horoscope response
class HoroscopeLifetime {
  final ZodiacInfo zodiac;
  final String canChi;
  final String gender;
  final String overview;
  final String career;
  final String love;
  final String health;
  final String family;
  final String fortune;
  final String? unlucky;
  final String? advice;
  final Map<String, dynamic>? metadata;

  HoroscopeLifetime({
    required this.zodiac,
    required this.canChi,
    required this.gender,
    required this.overview,
    required this.career,
    required this.love,
    required this.health,
    required this.family,
    required this.fortune,
    this.unlucky,
    this.advice,
    this.metadata,
  });

  factory HoroscopeLifetime.fromJson(Map<String, dynamic> json) =>
      HoroscopeLifetime(
        zodiac: ZodiacInfo(
          id: json['zodiacId'] as int? ?? 0,
          code: json['zodiacCode'] as String? ?? '',
          nameVi: json['zodiacName'] as String? ?? '',
        ),
        canChi: json['canChi'] as String? ?? '',
        gender: json['gender'] as String? ?? 'male',
        overview: json['overview'] as String? ?? '',
        career: json['career'] as String? ?? '',
        love: json['love'] as String? ?? '',
        health: json['health'] as String? ?? '',
        family: json['family'] as String? ?? '',
        fortune: json['fortune'] as String? ?? '',
        unlucky: json['unlucky'] as String?,
        advice: json['advice'] as String?,
        metadata: json['metadata'] as Map<String, dynamic>?,
      );
}

/// Lifetime by birth response
class LifetimeByBirthResponse {
  final int? zodiacId;
  final String? zodiacCode;
  final String? zodiacName;
  final String? canChi;
  final String gender;
  final String? hourBranch;
  final String? hourBranchName;
  final String? message;
  final bool computed;
  final bool isFallback;
  final String? overview;
  final String? career;
  final String? love;
  final String? health;
  final String? family;
  final String? fortune;
  final String? unlucky;
  final String? advice;
  final Map<String, dynamic>? metadata;

  LifetimeByBirthResponse({
    this.zodiacId,
    this.zodiacCode,
    this.zodiacName,
    this.canChi,
    required this.gender,
    this.hourBranch,
    this.hourBranchName,
    this.message,
    required this.computed,
    required this.isFallback,
    this.overview,
    this.career,
    this.love,
    this.health,
    this.family,
    this.fortune,
    this.unlucky,
    this.advice,
    this.metadata,
  });

  factory LifetimeByBirthResponse.fromJson(Map<String, dynamic> json) =>
      LifetimeByBirthResponse(
        zodiacId: json['zodiacId'] as int?,
        zodiacCode: json['zodiacCode'] as String?,
        zodiacName: json['zodiacName'] as String?,
        canChi: json['canChi'] as String?,
        gender: json['gender'] as String? ?? 'male',
        hourBranch: json['hourBranch'] as String?,
        hourBranchName: json['hourBranchName'] as String?,
        message: json['message'] as String?,
        computed: json['computed'] as bool? ?? false,
        isFallback: json['isFallback'] as bool? ?? false,
        overview: json['overview'] as String?,
        career: json['career'] as String?,
        love: json['love'] as String?,
        health: json['health'] as String?,
        family: json['family'] as String?,
        fortune: json['fortune'] as String?,
        unlucky: json['unlucky'] as String?,
        advice: json['advice'] as String?,
        metadata: json['metadata'] as Map<String, dynamic>?,
      );

  /// Check if this response has actual horoscope data
  bool get hasData => overview != null && overview!.isNotEmpty;
}

/// Can-Chi calculation result
class CanChiResult {
  final String canYear;
  final String chiYear;
  final String canChiYear;
  final int? zodiacId;
  final String? zodiacCode;
  final String? canDay;
  final String? chiDay;
  final String? canChiDay;
  final String? canMonth;
  final String? chiMonth;
  final String? canChiMonth;

  CanChiResult({
    required this.canYear,
    required this.chiYear,
    required this.canChiYear,
    this.zodiacId,
    this.zodiacCode,
    this.canDay,
    this.chiDay,
    this.canChiDay,
    this.canMonth,
    this.chiMonth,
    this.canChiMonth,
  });

  factory CanChiResult.fromJson(Map<String, dynamic> json) => CanChiResult(
        canYear: json['canYear'] as String? ?? '',
        chiYear: json['chiYear'] as String? ?? '',
        canChiYear: json['canChiYear'] as String? ?? '',
        zodiacId: json['zodiacId'] as int?,
        zodiacCode: json['zodiacCode'] as String?,
        canDay: json['canDay'] as String?,
        chiDay: json['chiDay'] as String?,
        canChiDay: json['canChiDay'] as String?,
        canMonth: json['canMonth'] as String?,
        chiMonth: json['chiMonth'] as String?,
        canChiMonth: json['canChiMonth'] as String?,
      );
}

/// Gender enum for horoscope queries
enum HoroscopeGender {
  male,
  female;

  String get value => name;

  static HoroscopeGender fromString(String value) {
    return HoroscopeGender.values.firstWhere(
      (g) => g.name == value.toLowerCase(),
      orElse: () => HoroscopeGender.male,
    );
  }
}

