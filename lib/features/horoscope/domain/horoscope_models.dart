import 'dart:convert';

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
  // New fields from V4 migration
  final String? cungMenh;
  final String? cungXungChieu;
  final String? cungTamHop;
  final String? cungNhiHop;
  final Map<String, dynamic>? vanHan;
  final Map<String, dynamic>? tuTru;
  final Map<String, dynamic>? phongThuy;
  final List<Map<String, String>>? qaSection;
  final String? conclusion;
  final Map<String, String>? monthlyBreakdown;
  final Map<String, dynamic>? metadata;

  HoroscopeYearly({
    required this.zodiac,
    required this.year,
    required this.summary,
    required this.career,
    required this.love,
    required this.health,
    required this.fortune,
    this.cungMenh,
    this.cungXungChieu,
    this.cungTamHop,
    this.cungNhiHop,
    this.vanHan,
    this.tuTru,
    this.phongThuy,
    this.qaSection,
    this.conclusion,
    this.monthlyBreakdown,
    this.metadata,
  });

  factory HoroscopeYearly.fromJson(Map<String, dynamic> json) {
    // Helper to safely parse String fields that might come as Map, List, or other types
    String? _parseStringField(dynamic value) {
      if (value == null) return null;
      if (value is String) return value;
      if (value is Map) {
        // If it's a Map, try to extract 'interpretation' or convert to JSON string
        if (value.containsKey('interpretation')) {
          return value['interpretation']?.toString();
        }
        try {
          return jsonEncode(value);
        } catch (_) {
          return value.toString();
        }
      }
      if (value is List) {
        try {
          return jsonEncode(value);
        } catch (_) {
          return value.toString();
        }
      }
      return value.toString();
    }
    
    final metadata = json['metadata'] as Map<String, dynamic>?;
    final yearlySections = metadata?['yearly_sections'] as Map<String, dynamic>?;
    
    // Helper to parse JSON strings or objects
    Map<String, dynamic>? parseJsonField(dynamic value) {
      if (value == null) return null;
      if (value is Map) return Map<String, dynamic>.from(value);
      if (value is String) {
        try {
          return jsonDecode(value) as Map<String, dynamic>?;
        } catch (_) {
          return null;
        }
      }
      return null;
    }
    
    List<Map<String, String>>? parseQASection(dynamic value) {
      if (value == null) return null;
      if (value is List) {
        return value.map((e) {
          if (e is Map) {
            return Map<String, String>.from(e.map((k, v) => MapEntry(k.toString(), v.toString())));
          }
          return <String, String>{};
        }).toList();
      }
      if (value is String) {
        try {
          final parsed = jsonDecode(value) as List?;
          return parsed?.map((e) {
            if (e is Map) {
              return Map<String, String>.from(e.map((k, v) => MapEntry(k.toString(), v.toString())));
            }
            return <String, String>{};
          }).toList();
        } catch (_) {
          return null;
        }
      }
      return null;
    }
    
    Map<String, String>? parseMonthlyBreakdown(dynamic value) {
      if (value == null) return null;
      if (value is Map) {
        return value.map((k, v) => MapEntry(k.toString(), v.toString()));
      }
      if (value is String) {
        try {
          final parsed = jsonDecode(value) as Map?;
          return parsed?.map((k, v) => MapEntry(k.toString(), v.toString()));
        } catch (_) {
          return null;
        }
      }
      return null;
    }
    
    return HoroscopeYearly(
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
      cungMenh: _parseStringField(json['cungMenh']) ?? _parseStringField(yearlySections?['cung_menh']),
      cungXungChieu: _parseStringField(json['cungXungChieu']) ?? _parseStringField(yearlySections?['cung_xung_chieu']),
      cungTamHop: _parseStringField(json['cungTamHop']) ?? _parseStringField(yearlySections?['cung_tam_hop']),
      cungNhiHop: _parseStringField(json['cungNhiHop']) ?? _parseStringField(yearlySections?['cung_nhi_hop']),
      vanHan: parseJsonField(json['vanHan'] ?? yearlySections?['van_han']),
      tuTru: parseJsonField(json['tuTru'] ?? yearlySections?['tu_tru']),
      phongThuy: parseJsonField(json['phongThuy'] ?? yearlySections?['phong_thuy']),
      qaSection: parseQASection(json['qaSection'] ?? yearlySections?['qa']),
      conclusion: _parseStringField(json['conclusion']) ?? _parseStringField(yearlySections?['loi_ket']),
      monthlyBreakdown: parseMonthlyBreakdown(json['monthlyBreakdown'] ?? yearlySections?['monthly_breakdown']),
      metadata: metadata,
    );
  }
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
  // New fields from V4 migration
  final String? loveByMonthGroup1;
  final String? loveByMonthGroup2;
  final String? loveByMonthGroup3;
  final List<String>? compatibleAges;
  final List<int>? difficultYears;
  final List<String>? incompatibleAges;
  final Map<String, String>? yearlyProgression;
  final String? ritualGuidance;
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
    this.loveByMonthGroup1,
    this.loveByMonthGroup2,
    this.loveByMonthGroup3,
    this.compatibleAges,
    this.difficultYears,
    this.incompatibleAges,
    this.yearlyProgression,
    this.ritualGuidance,
    this.metadata,
  });

  factory LifetimeByBirthResponse.fromJson(Map<String, dynamic> json) {
    // Helper to safely parse String fields that might come as Map, List, or other types
    String? _parseStringField(dynamic value) {
      if (value == null) return null;
      if (value is String) return value;
      if (value is Map) {
        // If it's a Map, try to convert to JSON string or extract text
        try {
          return jsonEncode(value);
        } catch (_) {
          return value.toString();
        }
      }
      if (value is List) {
        // If it's a List, try to convert to JSON string
        try {
          return jsonEncode(value);
        } catch (_) {
          return value.toString();
        }
      }
      return value.toString();
    }
    
    // Parse metadata for structured content
    final metadata = json['metadata'] as Map<String, dynamic>?;
    final sections = metadata?['sections'] as Map<String, dynamic>?;
    
    // Parse compatible_ages, difficult_years, incompatible_ages from direct fields or metadata
    List<String>? parseCompatibleAges() {
      if (json['compatibleAges'] != null) {
        if (json['compatibleAges'] is String) {
          try {
            final parsed = jsonDecode(json['compatibleAges'] as String) as List?;
            return parsed?.map((e) => e.toString()).toList();
          } catch (_) {
            return null;
          }
        }
        if (json['compatibleAges'] is List) {
          return (json['compatibleAges'] as List).map((e) => e.toString()).toList();
        }
      }
      final fromMetadata = sections?['tuoi_hop_lam_an'] as List?;
      if (fromMetadata != null) {
        return fromMetadata.map((e) => e.toString()).toList();
      }
      return null;
    }
    
    List<int>? parseDifficultYears() {
      if (json['difficultYears'] != null) {
        if (json['difficultYears'] is String) {
          try {
            final parsed = jsonDecode(json['difficultYears'] as String) as List?;
            return parsed?.map((e) => (e as num).toInt()).toList();
          } catch (_) {
            return null;
          }
        }
        if (json['difficultYears'] is List) {
          return (json['difficultYears'] as List).map((e) => (e as num).toInt()).toList();
        }
      }
      final fromMetadata = sections?['nam_kho_khan'] as List?;
      if (fromMetadata != null) {
        return fromMetadata.map((e) => (e as num).toInt()).toList();
      }
      return null;
    }
    
    List<String>? parseIncompatibleAges() {
      if (json['incompatibleAges'] != null) {
        if (json['incompatibleAges'] is String) {
          try {
            final parsed = jsonDecode(json['incompatibleAges'] as String) as List?;
            return parsed?.map((e) => e.toString()).toList();
          } catch (_) {
            return null;
          }
        }
        if (json['incompatibleAges'] is List) {
          return (json['incompatibleAges'] as List).map((e) => e.toString()).toList();
        }
      }
      final fromMetadata = sections?['tuoi_dai_ky'] as List?;
      if (fromMetadata != null) {
        return fromMetadata.map((e) => e.toString()).toList();
      }
      return null;
    }
    
    Map<String, String>? parseYearlyProgression() {
      if (json['yearlyProgression'] != null) {
        if (json['yearlyProgression'] is String) {
          try {
            final parsed = jsonDecode(json['yearlyProgression'] as String) as Map?;
            return parsed?.map((k, v) => MapEntry(k.toString(), v.toString()));
          } catch (_) {
            return null;
          }
        }
        if (json['yearlyProgression'] is Map) {
          return (json['yearlyProgression'] as Map).map((k, v) => MapEntry(k.toString(), v.toString()));
        }
      }
      final fromMetadata = sections?['dien_bien_tung_nam'] as Map?;
      if (fromMetadata != null) {
        return fromMetadata.map((k, v) => MapEntry(k.toString(), v.toString()));
      }
      return null;
    }
    
    return LifetimeByBirthResponse(
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
      overview: _parseStringField(json['overview']) ?? _parseStringField(sections?['tong_quat']),
      career: _parseStringField(json['career']) ?? _parseStringField(sections?['gia_dinh_su_nghiep']),
      love: _parseStringField(json['love']),
      health: _parseStringField(json['health']),
      family: _parseStringField(json['family']),
      fortune: _parseStringField(json['fortune']) ?? _parseStringField(sections?['tai_van']),
      unlucky: _parseStringField(json['unlucky']),
      advice: _parseStringField(json['advice']),
      loveByMonthGroup1: _parseStringField(json['loveByMonthGroup1']) ?? _parseStringField(sections?['tinh_duyen']?['group1']),
      loveByMonthGroup2: _parseStringField(json['loveByMonthGroup2']) ?? _parseStringField(sections?['tinh_duyen']?['group2']),
      loveByMonthGroup3: _parseStringField(json['loveByMonthGroup3']) ?? _parseStringField(sections?['tinh_duyen']?['group3']),
      compatibleAges: parseCompatibleAges(),
      difficultYears: parseDifficultYears(),
      incompatibleAges: parseIncompatibleAges(),
      yearlyProgression: parseYearlyProgression(),
      ritualGuidance: _parseStringField(json['ritualGuidance']) ?? _parseStringField(sections?['nghi_le']),
      metadata: metadata,
    );
  }

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

