/// Tu Vi Chart Interpretation domain models matching backend DTOs.

/// Interpretation for a single star in a palace.
class StarInterpretation {
  final String? starCode;
  final String? starName;
  final String? starType;
  final String? brightness;
  final String? interpretation;
  final String? summary;

  StarInterpretation({
    this.starCode,
    this.starName,
    this.starType,
    this.brightness,
    this.interpretation,
    this.summary,
  });

  factory StarInterpretation.fromJson(Map<String, dynamic> json) {
    return StarInterpretation(
      starCode: json['starCode'] as String?,
      starName: json['starName'] as String?,
      starType: json['starType'] as String?,
      brightness: json['brightness'] as String?,
      interpretation: json['interpretation'] as String?,
      summary: json['summary'] as String?,
    );
  }
}

/// Interpretation for a single palace.
class PalaceInterpretation {
  final String? palaceCode;
  final String? palaceName;
  final String? palaceChi;
  final String? canChiPrefix;
  final String? summary;
  final String? introduction;
  final String? detailedAnalysis;
  final String? genderAnalysis;
  final List<StarInterpretation>? starAnalyses;
  final bool hasTuan;
  final bool hasTriet;
  final String? tuanTrietEffect;
  final String? adviceSection;
  final String? conclusion;

  PalaceInterpretation({
    this.palaceCode,
    this.palaceName,
    this.palaceChi,
    this.canChiPrefix,
    this.summary,
    this.introduction,
    this.detailedAnalysis,
    this.genderAnalysis,
    this.starAnalyses,
    this.hasTuan = false,
    this.hasTriet = false,
    this.tuanTrietEffect,
    this.adviceSection,
    this.conclusion,
  });

  factory PalaceInterpretation.fromJson(Map<String, dynamic> json) {
    return PalaceInterpretation(
      palaceCode: json['palaceCode'] as String?,
      palaceName: json['palaceName'] as String?,
      palaceChi: json['palaceChi'] as String?,
      canChiPrefix: json['canChiPrefix'] as String?,
      summary: json['summary'] as String?,
      introduction: json['introduction'] as String?,
      detailedAnalysis: json['detailedAnalysis'] as String?,
      genderAnalysis: json['genderAnalysis'] as String?,
      starAnalyses: (json['starAnalyses'] as List<dynamic>?)
          ?.map((e) => StarInterpretation.fromJson(e as Map<String, dynamic>))
          .toList(),
      hasTuan: json['hasTuan'] as bool? ?? false,
      hasTriet: json['hasTriet'] as bool? ?? false,
      tuanTrietEffect: json['tuanTrietEffect'] as String?,
      adviceSection: json['adviceSection'] as String?,
      conclusion: json['conclusion'] as String?,
    );
  }
}

/// Overview section containing general interpretations.
class OverviewSection {
  final String? introduction;
  final String? banMenhName;
  final String? banMenhNguHanh;
  final String? banMenhInterpretation;
  final String? cucName;
  final int? cucValue;
  final String? menhCucRelation;
  final String? cucInterpretation;
  final String? chuMenh;
  final String? chuMenhInterpretation;
  final String? chuThan;
  final String? chuThanInterpretation;
  final String? thanCu;
  final String? laiNhanInterpretation;
  final String? canLuong;
  final String? canLuongInterpretation;
  final bool thanMenhDongCung;
  final String? thanCuInterpretation;
  final String? thuanNghich;
  final String? thuanNghichInterpretation;
  final String? overallSummary;

  OverviewSection({
    this.introduction,
    this.banMenhName,
    this.banMenhNguHanh,
    this.banMenhInterpretation,
    this.cucName,
    this.cucValue,
    this.menhCucRelation,
    this.cucInterpretation,
    this.chuMenh,
    this.chuMenhInterpretation,
    this.chuThan,
    this.chuThanInterpretation,
    this.thanCu,
    this.laiNhanInterpretation,
    this.canLuong,
    this.canLuongInterpretation,
    this.thanMenhDongCung = false,
    this.thanCuInterpretation,
    this.thuanNghich,
    this.thuanNghichInterpretation,
    this.overallSummary,
  });

  factory OverviewSection.fromJson(Map<String, dynamic> json) {
    return OverviewSection(
      introduction: json['introduction'] as String?,
      banMenhName: json['banMenhName'] as String?,
      banMenhNguHanh: json['banMenhNguHanh'] as String?,
      banMenhInterpretation: json['banMenhInterpretation'] as String?,
      cucName: json['cucName'] as String?,
      cucValue: json['cucValue'] as int?,
      menhCucRelation: json['menhCucRelation'] as String?,
      cucInterpretation: json['cucInterpretation'] as String?,
      chuMenh: json['chuMenh'] as String?,
      chuMenhInterpretation: json['chuMenhInterpretation'] as String?,
      chuThan: json['chuThan'] as String?,
      chuThanInterpretation: json['chuThanInterpretation'] as String?,
      thanCu: json['thanCu'] as String?,
      laiNhanInterpretation: json['laiNhanInterpretation'] as String?,
      canLuong: json['canLuong'] as String?,
      canLuongInterpretation: json['canLuongInterpretation'] as String?,
      thanMenhDongCung: json['thanMenhDongCung'] as bool? ?? false,
      thanCuInterpretation: json['thanCuInterpretation'] as String?,
      thuanNghich: json['thuanNghich'] as String?,
      thuanNghichInterpretation: json['thuanNghichInterpretation'] as String?,
      overallSummary: json['overallSummary'] as String?,
    );
  }
}

/// Complete interpretation response for a Tu Vi chart.
class TuViInterpretationResponse {
  final String? name;
  final String? gender;
  final String? birthDate;
  final int? birthHour;
  final String? lunarYearCanChi;
  final OverviewSection? overview;
  final PalaceInterpretation? menhInterpretation;
  final PalaceInterpretation? quanLocInterpretation;
  final PalaceInterpretation? taiBachInterpretation;
  final PalaceInterpretation? phuTheInterpretation;
  final PalaceInterpretation? tatAchInterpretation;
  final PalaceInterpretation? tuTucInterpretation;
  final PalaceInterpretation? dienTrachInterpretation;
  final PalaceInterpretation? phuMauInterpretation;
  final PalaceInterpretation? huynhDeInterpretation;
  final PalaceInterpretation? phucDucInterpretation;
  final PalaceInterpretation? noBocInterpretation;
  final PalaceInterpretation? thienDiInterpretation;
  final String? generatedAt;
  final String? aiModel;

  TuViInterpretationResponse({
    this.name,
    this.gender,
    this.birthDate,
    this.birthHour,
    this.lunarYearCanChi,
    this.overview,
    this.menhInterpretation,
    this.quanLocInterpretation,
    this.taiBachInterpretation,
    this.phuTheInterpretation,
    this.tatAchInterpretation,
    this.tuTucInterpretation,
    this.dienTrachInterpretation,
    this.phuMauInterpretation,
    this.huynhDeInterpretation,
    this.phucDucInterpretation,
    this.noBocInterpretation,
    this.thienDiInterpretation,
    this.generatedAt,
    this.aiModel,
  });

  factory TuViInterpretationResponse.fromJson(Map<String, dynamic> json) {
    return TuViInterpretationResponse(
      name: json['name'] as String?,
      gender: json['gender'] as String?,
      birthDate: json['birthDate'] as String?,
      birthHour: json['birthHour'] as int?,
      lunarYearCanChi: json['lunarYearCanChi'] as String?,
      overview: json['overview'] != null
          ? OverviewSection.fromJson(json['overview'] as Map<String, dynamic>)
          : null,
      menhInterpretation: json['menhInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['menhInterpretation'] as Map<String, dynamic>)
          : null,
      quanLocInterpretation: json['quanLocInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['quanLocInterpretation'] as Map<String, dynamic>)
          : null,
      taiBachInterpretation: json['taiBachInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['taiBachInterpretation'] as Map<String, dynamic>)
          : null,
      phuTheInterpretation: json['phuTheInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['phuTheInterpretation'] as Map<String, dynamic>)
          : null,
      tatAchInterpretation: json['tatAchInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['tatAchInterpretation'] as Map<String, dynamic>)
          : null,
      tuTucInterpretation: json['tuTucInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['tuTucInterpretation'] as Map<String, dynamic>)
          : null,
      dienTrachInterpretation: json['dienTrachInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['dienTrachInterpretation'] as Map<String, dynamic>)
          : null,
      phuMauInterpretation: json['phuMauInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['phuMauInterpretation'] as Map<String, dynamic>)
          : null,
      huynhDeInterpretation: json['huynhDeInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['huynhDeInterpretation'] as Map<String, dynamic>)
          : null,
      phucDucInterpretation: json['phucDucInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['phucDucInterpretation'] as Map<String, dynamic>)
          : null,
      noBocInterpretation: json['noBocInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['noBocInterpretation'] as Map<String, dynamic>)
          : null,
      thienDiInterpretation: json['thienDiInterpretation'] != null
          ? PalaceInterpretation.fromJson(
              json['thienDiInterpretation'] as Map<String, dynamic>)
          : null,
      generatedAt: json['generatedAt'] as String?,
      aiModel: json['aiModel'] as String?,
    );
  }

  /// Get list of all palace interpretations for iteration.
  List<PalaceInterpretation> get allPalaceInterpretations {
    return [
      if (menhInterpretation != null) menhInterpretation!,
      if (quanLocInterpretation != null) quanLocInterpretation!,
      if (taiBachInterpretation != null) taiBachInterpretation!,
      if (phuTheInterpretation != null) phuTheInterpretation!,
      if (tatAchInterpretation != null) tatAchInterpretation!,
      if (tuTucInterpretation != null) tuTucInterpretation!,
      if (dienTrachInterpretation != null) dienTrachInterpretation!,
      if (phuMauInterpretation != null) phuMauInterpretation!,
      if (huynhDeInterpretation != null) huynhDeInterpretation!,
      if (phucDucInterpretation != null) phucDucInterpretation!,
      if (noBocInterpretation != null) noBocInterpretation!,
      if (thienDiInterpretation != null) thienDiInterpretation!,
    ];
  }

  /// Get palace interpretation by code.
  PalaceInterpretation? getPalaceByCode(String code) {
    switch (code) {
      case 'MENH':
        return menhInterpretation;
      case 'QUAN_LOC':
        return quanLocInterpretation;
      case 'TAI_BACH':
        return taiBachInterpretation;
      case 'PHU_THE':
        return phuTheInterpretation;
      case 'TAT_ACH':
        return tatAchInterpretation;
      case 'TU_TUC':
        return tuTucInterpretation;
      case 'DIEN_TRACH':
        return dienTrachInterpretation;
      case 'PHU_MAU':
        return phuMauInterpretation;
      case 'HUYNH_DE':
        return huynhDeInterpretation;
      case 'PHUC_DUC':
        return phucDucInterpretation;
      case 'NO_BOC':
        return noBocInterpretation;
      case 'THIEN_DI':
        return thienDiInterpretation;
      default:
        return null;
    }
  }
}
