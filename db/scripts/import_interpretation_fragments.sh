#!/bin/bash
# Script to import interpretation fragments and rules into database

set -e

# Default values
MYSQL_HOST=${MYSQL_HOST:-localhost}
MYSQL_PORT=${MYSQL_PORT:-3306}
MYSQL_USER=${MYSQL_USER:-root}
MYSQL_PASSWORD=${MYSQL_PASSWORD:-root}
DATABASE=${DATABASE:-lich_van_nien}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Default import mode
IMPORT_MODE="ignore"  # ignore, replace, or insert

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --host)
            MYSQL_HOST="$2"
            shift 2
            ;;
        --port)
            MYSQL_PORT="$2"
            shift 2
            ;;
        --user)
            MYSQL_USER="$2"
            shift 2
            ;;
        --password)
            MYSQL_PASSWORD="$2"
            shift 2
            ;;
        --database)
            DATABASE="$2"
            shift 2
            ;;
        --mode)
            IMPORT_MODE="$2"
            if [[ "$IMPORT_MODE" != "ignore" && "$IMPORT_MODE" != "replace" && "$IMPORT_MODE" != "insert" ]]; then
                log_error "Invalid mode: $IMPORT_MODE. Must be 'ignore', 'replace', or 'insert'"
                exit 1
            fi
            shift 2
            ;;
        --help)
            echo "Usage: $0 [options]"
            echo "Options:"
            echo "  --host HOST         MySQL host (default: localhost)"
            echo "  --port PORT         MySQL port (default: 3306)"
            echo "  --user USER         MySQL user (default: root)"
            echo "  --password PASSWORD MySQL password"
            echo "  --database DATABASE Database name (default: lich_van_nien)"
            echo "  --mode MODE         Import mode: 'ignore' (skip duplicates, default), 'replace' (replace duplicates), 'insert' (fail on duplicates)"
            echo "  --help              Show this help"
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Build MySQL command
MYSQL_CMD="mysql -h${MYSQL_HOST} -P${MYSQL_PORT} -u${MYSQL_USER}"
if [ -n "$MYSQL_PASSWORD" ]; then
    MYSQL_CMD="${MYSQL_CMD} -p${MYSQL_PASSWORD}"
fi
MYSQL_CMD="${MYSQL_CMD} ${DATABASE}"

# Function to import SQL file with appropriate mode
import_sql_file() {
    local file_path="$1"
    local temp_file=$(mktemp)
    
    case "$IMPORT_MODE" in
        "ignore")
            # Convert INSERT INTO to INSERT IGNORE
            sed 's/^INSERT INTO /INSERT IGNORE INTO /g' "$file_path" > "$temp_file"
            ;;
        "replace")
            # Convert INSERT INTO to REPLACE INTO
            sed 's/^INSERT INTO /REPLACE INTO /g' "$file_path" > "$temp_file"
            ;;
        "insert")
            # Keep original INSERT INTO
            cp "$file_path" "$temp_file"
            ;;
    esac
    
    # Execute the converted SQL
    $MYSQL_CMD < "$temp_file"
    local exit_code=$?
    
    # Clean up
    rm -f "$temp_file"
    
    return $exit_code
}

# Function to convert INSERT INTO in piped input and save to temp file, then execute
import_piped_sql() {
    local temp_file=$(mktemp)
    
    # Read from stdin and convert
    case "$IMPORT_MODE" in
        "ignore")
            sed 's/^INSERT INTO /INSERT IGNORE INTO /g' > "$temp_file"
            ;;
        "replace")
            sed 's/^INSERT INTO /REPLACE INTO /g' > "$temp_file"
            ;;
        "insert")
            cat > "$temp_file"
            ;;
    esac
    
    # Execute the converted SQL
    $MYSQL_CMD < "$temp_file"
    local exit_code=$?
    
    # Clean up
    rm -f "$temp_file"
    
    return $exit_code
}

log_info "Importing interpretation fragments and rules..."
log_info "Host: ${MYSQL_HOST}:${MYSQL_PORT}"
log_info "Database: ${DATABASE}"
log_info "Import mode: ${IMPORT_MODE}"

# Check connection
if ! $MYSQL_CMD -e "SELECT 1" > /dev/null 2>&1; then
    log_error "Cannot connect to MySQL database"
    exit 1
fi

# Check if tables exist
log_info "Checking if tables exist..."
if ! $MYSQL_CMD -e "DESCRIBE interpretation_fragments" > /dev/null 2>&1; then
    log_error "Table interpretation_fragments does not exist. Please run migration V8 first."
    exit 1
fi

# Import fragments
log_info "Importing fragments..."

# Step 1: Import chính tinh fragments
if [ -f "${SCRIPT_DIR}/interpretation_fragments_menh_chinh_tinh.sql" ]; then
    log_info "Importing: interpretation_fragments_menh_chinh_tinh.sql"
    import_sql_file "${SCRIPT_DIR}/interpretation_fragments_menh_chinh_tinh.sql"
    FRAGMENT_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE '%_MENH_%'")
    log_info "Imported ${FRAGMENT_COUNT} chính tinh fragments"
else
    log_warn "File interpretation_fragments_menh_chinh_tinh.sql not found"
fi

# Step 2: Import combination fragments
if [ -f "${SCRIPT_DIR}/STEP5_COMBINATION_FRAGMENTS.sql" ]; then
    log_info "Importing: STEP5_COMBINATION_FRAGMENTS.sql"
    # Extract INSERT statements for fragments (extract from INSERT to semicolon)
    awk '/^INSERT INTO interpretation_fragments/,/;$/ {print}' "${SCRIPT_DIR}/STEP5_COMBINATION_FRAGMENTS.sql" | import_piped_sql
    COMBINATION_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'TU_PHU%' OR fragment_code LIKE 'SAT_PHA_THAM%' OR fragment_code LIKE 'KHONG_KIEP%' OR fragment_code LIKE 'KINH_DA%' OR fragment_code LIKE 'XUONG_KHUC%'")
    log_info "Imported ${COMBINATION_COUNT} combination fragments"
else
    log_warn "File STEP5_COMBINATION_FRAGMENTS.sql not found"
fi

# Step 3: Import Tuần/Triệt fragments
if [ -f "${SCRIPT_DIR}/STEP7_TUAN_TRIET.sql" ]; then
    log_info "Importing: STEP7_TUAN_TRIET.sql"
    # Extract INSERT statements for fragments (extract from INSERT to semicolon)
    awk '/^INSERT INTO interpretation_fragments/,/;$/ {print}' "${SCRIPT_DIR}/STEP7_TUAN_TRIET.sql" | import_piped_sql
    TUAN_TRIET_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'TUAN%' OR fragment_code LIKE 'TRIET%'")
    log_info "Imported ${TUAN_TRIET_COUNT} Tuần/Triệt fragments"
else
    log_warn "File STEP7_TUAN_TRIET.sql not found"
fi

# Step 4: Import Overview fragments (STEP 8)
if [ -f "${SCRIPT_DIR}/STEP8_OVERVIEW_FRAGMENTS.sql" ]; then
    log_info "Importing: STEP8_OVERVIEW_FRAGMENTS.sql"
    import_sql_file "${SCRIPT_DIR}/STEP8_OVERVIEW_FRAGMENTS.sql"
    OVERVIEW_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'BAN_MENH%' OR fragment_code LIKE 'CUC_%' OR fragment_code LIKE 'THUAN_%' OR fragment_code LIKE 'NGHICH_%' OR fragment_code LIKE 'THAN_CU%' OR fragment_code LIKE 'THAN_MENH_DONG_CUNG'")
    log_info "Imported ${OVERVIEW_COUNT} Overview fragments"
else
    log_warn "File STEP8_OVERVIEW_FRAGMENTS.sql not found"
fi

# Step 5: Import Detailed Palace fragments (Part 1)
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_PALACES.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_PALACES.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_PALACES.sql"
    DETAILED_PALACE_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'TU_VI_QUAN_LOC%' OR fragment_code LIKE 'THIEN_PHU_QUAN_LOC%' OR fragment_code LIKE 'THAM_LANG_QUAN_LOC%' OR fragment_code LIKE 'THAT_SAT_QUAN_LOC%' OR fragment_code LIKE 'PHA_QUAN_QUAN_LOC%' OR fragment_code LIKE 'TU_VI_TAI_BACH%' OR fragment_code LIKE 'THIEN_PHU_TAI_BACH%' OR fragment_code LIKE 'TU_VI_PHU_THE%' OR fragment_code LIKE 'THIEN_PHU_PHU_THE%' OR fragment_code LIKE 'TU_VI_TAT_ACH%'")
    log_info "Imported ${DETAILED_PALACE_COUNT} Detailed Palace fragments (Part 1: QUAN_LOC, TAI_BACH, PHU_THE, TAT_ACH)"
else
    log_warn "File DETAILED_FRAGMENTS_PALACES.sql not found"
fi

# Step 6: Import Detailed Palace fragments (Part 2)
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_PALACES_PART2.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_PALACES_PART2.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_PALACES_PART2.sql"
    DETAILED_PALACE2_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'TU_VI_TU_TUC%' OR fragment_code LIKE 'THIEN_PHU_TU_TUC%' OR fragment_code LIKE 'TU_VI_PHUC_DUC%' OR fragment_code LIKE 'THIEN_PHU_PHUC_DUC%'")
    log_info "Imported ${DETAILED_PALACE2_COUNT} Detailed Palace fragments (Part 2: TU_TUC, PHUC_DUC)"
else
    log_warn "File DETAILED_FRAGMENTS_PALACES_PART2.sql not found"
fi

# Step 7: Import Detailed Palace fragments (Part 3)
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_PALACES_PART3.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_PALACES_PART3.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_PALACES_PART3.sql"
    DETAILED_PALACE3_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'TU_VI_THIEN_DI%' OR fragment_code LIKE 'THIEN_PHU_THIEN_DI%' OR fragment_code LIKE 'TU_VI_NO_BOC%' OR fragment_code LIKE 'THIEN_PHU_NO_BOC%' OR fragment_code LIKE 'TU_VI_DIEN_TRACH%' OR fragment_code LIKE 'THIEN_PHU_DIEN_TRACH%' OR fragment_code LIKE 'TU_VI_PHU_MAU%' OR fragment_code LIKE 'THIEN_PHU_PHU_MAU%' OR fragment_code LIKE 'TU_VI_HUYNH_DE%' OR fragment_code LIKE 'THIEN_PHU_HUYNH_DE%'")
    log_info "Imported ${DETAILED_PALACE3_COUNT} Detailed Palace fragments (Part 3: THIEN_DI, NO_BOC, DIEN_TRACH, PHU_MAU, HUYNH_DE)"
else
    log_warn "File DETAILED_FRAGMENTS_PALACES_PART3.sql not found"
fi

# Step 8: Import Chủ Mệnh / Chủ Thân fragments
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_CHU_MENH_THAN.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_CHU_MENH_THAN.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_CHU_MENH_THAN.sql"
    CHU_MENH_THAN_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'CHU_MENH_%' OR fragment_code LIKE 'CHU_THAN_%'")
    log_info "Imported ${CHU_MENH_THAN_COUNT} Chủ Mệnh / Chủ Thân fragments"
else
    log_warn "File DETAILED_FRAGMENTS_CHU_MENH_THAN.sql not found"
fi

# Import rules
log_info "Importing rules..."

# Step 1: Import chính tinh rules
if [ -f "${SCRIPT_DIR}/interpretation_rules_menh_chinh_tinh.sql" ]; then
    log_info "Importing: interpretation_rules_menh_chinh_tinh.sql"
    import_sql_file "${SCRIPT_DIR}/interpretation_rules_menh_chinh_tinh.sql"
    RULE_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE '%_MENH_%'")
    log_info "Imported ${RULE_COUNT} chính tinh rules"
else
    log_warn "File interpretation_rules_menh_chinh_tinh.sql not found"
fi

# Step 2: Import combination rules
if [ -f "${SCRIPT_DIR}/STEP5_COMBINATION_FRAGMENTS.sql" ]; then
    log_info "Importing combination rules from STEP5_COMBINATION_FRAGMENTS.sql"
    # Extract INSERT statements for rules (extract from INSERT to semicolon)
    awk '/^INSERT INTO interpretation_rules/,/;$/ {print}' "${SCRIPT_DIR}/STEP5_COMBINATION_FRAGMENTS.sql" | import_piped_sql
    COMBINATION_RULE_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'TU_PHU%' OR fragment_code LIKE 'SAT_PHA_THAM%' OR fragment_code LIKE 'KHONG_KIEP%' OR fragment_code LIKE 'KINH_DA%' OR fragment_code LIKE 'XUONG_KHUC%'")
    log_info "Imported ${COMBINATION_RULE_COUNT} combination rules"
fi

# Step 3: Import Tuần/Triệt rules
if [ -f "${SCRIPT_DIR}/STEP7_TUAN_TRIET.sql" ]; then
    log_info "Importing Tuần/Triệt rules from STEP7_TUAN_TRIET.sql"
    # Extract INSERT statements for rules (extract from INSERT to semicolon)
    awk '/^INSERT INTO interpretation_rules/,/;$/ {print}' "${SCRIPT_DIR}/STEP7_TUAN_TRIET.sql" | import_piped_sql
    TUAN_TRIET_RULE_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'TUAN%' OR fragment_code LIKE 'TRIET%'")
    log_info "Imported ${TUAN_TRIET_RULE_COUNT} Tuần/Triệt rules"
fi

# Step 4: Import Overview rules (STEP 8)
if [ -f "${SCRIPT_DIR}/STEP8_OVERVIEW_RULES.sql" ]; then
    log_info "Importing: STEP8_OVERVIEW_RULES.sql"
    import_sql_file "${SCRIPT_DIR}/STEP8_OVERVIEW_RULES.sql"
    OVERVIEW_RULE_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'BAN_MENH%' OR fragment_code LIKE 'CUC_%' OR fragment_code LIKE 'THUAN_%' OR fragment_code LIKE 'NGHICH_%' OR fragment_code LIKE 'THAN_CU%' OR fragment_code LIKE 'THAN_MENH_DONG_CUNG'")
    log_info "Imported ${OVERVIEW_RULE_COUNT} Overview rules"
else
    log_warn "File STEP8_OVERVIEW_RULES.sql not found"
fi

# Step 5: Import Detailed fragments rules
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_RULES.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_RULES.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_RULES.sql"
    DETAILED_RULE_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'TU_VI_QUAN_LOC%' OR fragment_code LIKE 'THIEN_PHU_QUAN_LOC%' OR fragment_code LIKE 'THAM_LANG_QUAN_LOC%' OR fragment_code LIKE 'THAT_SAT_QUAN_LOC%' OR fragment_code LIKE 'PHA_QUAN_QUAN_LOC%' OR fragment_code LIKE 'TU_VI_TAI_BACH%' OR fragment_code LIKE 'THIEN_PHU_TAI_BACH%' OR fragment_code LIKE 'TU_VI_PHU_THE%' OR fragment_code LIKE 'THIEN_PHU_PHU_THE%' OR fragment_code LIKE 'TU_VI_TAT_ACH%' OR fragment_code LIKE 'TU_VI_TU_TUC%' OR fragment_code LIKE 'THIEN_PHU_TU_TUC%' OR fragment_code LIKE 'TU_VI_PHUC_DUC%' OR fragment_code LIKE 'THIEN_PHU_PHUC_DUC%' OR fragment_code LIKE 'TU_VI_THIEN_DI%' OR fragment_code LIKE 'THIEN_PHU_THIEN_DI%' OR fragment_code LIKE 'TU_VI_NO_BOC%' OR fragment_code LIKE 'THIEN_PHU_NO_BOC%' OR fragment_code LIKE 'TU_VI_DIEN_TRACH%' OR fragment_code LIKE 'THIEN_PHU_DIEN_TRACH%' OR fragment_code LIKE 'TU_VI_PHU_MAU%' OR fragment_code LIKE 'THIEN_PHU_PHU_MAU%' OR fragment_code LIKE 'TU_VI_HUYNH_DE%' OR fragment_code LIKE 'THIEN_PHU_HUYNH_DE%' OR fragment_code LIKE 'CHU_MENH_%' OR fragment_code LIKE 'CHU_THAN_%'")
    log_info "Imported ${DETAILED_RULE_COUNT} Detailed fragments rules"
else
    log_warn "File DETAILED_FRAGMENTS_RULES.sql not found"
fi

# Step 6: Import remaining star fragments
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_REMAINING_STARS.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_REMAINING_STARS.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_REMAINING_STARS.sql"
    REMAINING_STARS_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'THIEN_CO_%' OR fragment_code LIKE 'THAI_AM_%' OR fragment_code LIKE 'THAI_DUONG_%' OR fragment_code LIKE 'VU_KHUC_%' OR fragment_code LIKE 'THIEN_TUONG_%' OR fragment_code LIKE 'THIEN_LUONG_%' OR fragment_code LIKE 'LIEM_TRINH_%' OR fragment_code LIKE 'CU_MON_%' OR fragment_code LIKE 'THIEN_DONG_%'")
    log_info "Imported ${REMAINING_STARS_COUNT} Remaining star fragments"
else
    log_warn "File DETAILED_FRAGMENTS_REMAINING_STARS.sql not found"
fi

# Step 7: Import rules for remaining star fragments
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_REMAINING_STARS_RULES.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_REMAINING_STARS_RULES.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_REMAINING_STARS_RULES.sql"
    REMAINING_RULES_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'THIEN_CO_%' OR fragment_code LIKE 'THAI_AM_%' OR fragment_code LIKE 'THAI_DUONG_%' OR fragment_code LIKE 'VU_KHUC_%' OR fragment_code LIKE 'THIEN_TUONG_%' OR fragment_code LIKE 'THIEN_LUONG_%' OR fragment_code LIKE 'LIEM_TRINH_%' OR fragment_code LIKE 'CU_MON_%' OR fragment_code LIKE 'THIEN_DONG_%'")
    log_info "Imported ${REMAINING_RULES_COUNT} Remaining star fragment rules"
else
    log_warn "File DETAILED_FRAGMENTS_REMAINING_STARS_RULES.sql not found"
fi

# Step 8: Import fragments for THAT_SAT, PHA_QUAN, THAM_LANG (missing palaces)
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG.sql"
    MISSING_FRAGMENTS_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE (fragment_code LIKE 'THAT_SAT_%' OR fragment_code LIKE 'PHA_QUAN_%' OR fragment_code LIKE 'THAM_LANG_%') AND fragment_code NOT LIKE '%QUAN_LOC%' AND fragment_code NOT LIKE '%MENH%'")
    log_info "Imported fragments for THAT_SAT, PHA_QUAN, THAM_LANG (missing palaces)"
else
    log_warn "File DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG.sql not found"
fi

# Step 9: Import rules for THAT_SAT, PHA_QUAN, THAM_LANG (missing palaces)
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG_RULES.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG_RULES.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG_RULES.sql"
    MISSING_RULES_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE (fragment_code LIKE 'THAT_SAT_%' OR fragment_code LIKE 'PHA_QUAN_%' OR fragment_code LIKE 'THAM_LANG_%') AND fragment_code NOT LIKE '%QUAN_LOC%' AND fragment_code NOT LIKE '%MENH%'")
    log_info "Imported rules for THAT_SAT, PHA_QUAN, THAM_LANG (missing palaces)"
else
    log_warn "File DETAILED_FRAGMENTS_THAT_SAT_PHA_QUAN_THAM_LANG_RULES.sql not found"
fi

# Step 10: Import fragments for NO_CHINH_TINH_TRANG_SINH (palaces without Chính tinh)
if [ -f "${SCRIPT_DIR}/FRAGMENTS_NO_CHINH_TINH_TRANG_SINH.sql" ]; then
    log_info "Importing: FRAGMENTS_NO_CHINH_TINH_TRANG_SINH.sql"
    import_sql_file "${SCRIPT_DIR}/FRAGMENTS_NO_CHINH_TINH_TRANG_SINH.sql"
    NO_CHINH_TINH_FRAGMENTS_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE 'NO_CHINH_TINH_%'")
    log_info "Imported ${NO_CHINH_TINH_FRAGMENTS_COUNT} fragments for palaces without Chính tinh (Tràng Sinh states)"
else
    log_warn "File FRAGMENTS_NO_CHINH_TINH_TRANG_SINH.sql not found"
fi

# Step 11: Import rules for NO_CHINH_TINH_TRANG_SINH (palaces without Chính tinh)
if [ -f "${SCRIPT_DIR}/RULES_NO_CHINH_TINH_TRANG_SINH.sql" ]; then
    log_info "Importing: RULES_NO_CHINH_TINH_TRANG_SINH.sql"
    import_sql_file "${SCRIPT_DIR}/RULES_NO_CHINH_TINH_TRANG_SINH.sql"
    NO_CHINH_TINH_RULES_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'NO_CHINH_TINH_%'")
    log_info "Imported ${NO_CHINH_TINH_RULES_COUNT} rules for palaces without Chính tinh (Tràng Sinh states)"
else
    log_warn "File RULES_NO_CHINH_TINH_TRANG_SINH.sql not found"
fi

# Step 12: Import fragments for CHU_THAN with brightness (format: {STAR_CODE}_CHU_THAN_{BRIGHTNESS})
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_CHU_THAN.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_CHU_THAN.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_CHU_THAN.sql"
    CHU_THAN_BRIGHTNESS_FRAGMENTS_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE fragment_code LIKE '%_CHU_THAN_MIEU' OR fragment_code LIKE '%_CHU_THAN_VUONG' OR fragment_code LIKE '%_CHU_THAN_DAC' OR fragment_code LIKE '%_CHU_THAN_BINH' OR fragment_code LIKE '%_CHU_THAN_HAM'")
    log_info "Imported ${CHU_THAN_BRIGHTNESS_FRAGMENTS_COUNT} fragments for CHU_THAN with brightness levels"
else
    log_warn "File DETAILED_FRAGMENTS_CHU_THAN.sql not found"
fi

# Step 13: Import rules for CHU_THAN with brightness (format: {STAR_CODE}_CHU_THAN_{BRIGHTNESS})
if [ -f "${SCRIPT_DIR}/DETAILED_FRAGMENTS_CHU_THAN_RULES.sql" ]; then
    log_info "Importing: DETAILED_FRAGMENTS_CHU_THAN_RULES.sql"
    import_sql_file "${SCRIPT_DIR}/DETAILED_FRAGMENTS_CHU_THAN_RULES.sql"
    CHU_THAN_BRIGHTNESS_RULES_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE '%_CHU_THAN_MIEU' OR fragment_code LIKE '%_CHU_THAN_VUONG' OR fragment_code LIKE '%_CHU_THAN_DAC' OR fragment_code LIKE '%_CHU_THAN_BINH' OR fragment_code LIKE '%_CHU_THAN_HAM'")
    log_info "Imported ${CHU_THAN_BRIGHTNESS_RULES_COUNT} rules for CHU_THAN with brightness levels"
else
    log_warn "File DETAILED_FRAGMENTS_CHU_THAN_RULES.sql not found"
fi

# Step 14: Import fragments for missing MENH and TAI_BACH combinations
if [ -f "${SCRIPT_DIR}/FRAGMENTS_MISSING_MENH_TAI_BACH.sql" ]; then
    log_info "Importing: FRAGMENTS_MISSING_MENH_TAI_BACH.sql"
    import_sql_file "${SCRIPT_DIR}/FRAGMENTS_MISSING_MENH_TAI_BACH.sql"
    MISSING_FRAGMENTS_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE (fragment_code LIKE 'THIEN_LUONG_MENH_%' OR fragment_code LIKE 'THAI_DUONG_MENH_%' OR fragment_code LIKE 'THAM_LANG_TAI_BACH_%') AND fragment_code NOT LIKE '%_CHU_THAN_%'")
    log_info "Imported ${MISSING_FRAGMENTS_COUNT} fragments for missing MENH and TAI_BACH combinations"
else
    log_warn "File FRAGMENTS_MISSING_MENH_TAI_BACH.sql not found"
fi

# Step 15: Import rules for missing MENH and TAI_BACH combinations
if [ -f "${SCRIPT_DIR}/RULES_MISSING_MENH_TAI_BACH.sql" ]; then
    log_info "Importing: RULES_MISSING_MENH_TAI_BACH.sql"
    import_sql_file "${SCRIPT_DIR}/RULES_MISSING_MENH_TAI_BACH.sql"
    MISSING_RULES_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'THIEN_LUONG_MENH_%' OR fragment_code LIKE 'THAI_DUONG_MENH_%' OR fragment_code LIKE 'THAM_LANG_TAI_BACH_%'")
    log_info "Imported ${MISSING_RULES_COUNT} rules for missing MENH and TAI_BACH combinations"
else
    log_warn "File RULES_MISSING_MENH_TAI_BACH.sql not found"
fi

# Step 16: Import final missing fragments
if [ -f "${SCRIPT_DIR}/FRAGMENTS_MISSING_FINAL.sql" ]; then
    log_info "Importing: FRAGMENTS_MISSING_FINAL.sql"
    import_sql_file "${SCRIPT_DIR}/FRAGMENTS_MISSING_FINAL.sql"
    FINAL_FRAGMENTS_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments WHERE (fragment_code LIKE 'THIEN_CO_MENH_%' OR fragment_code LIKE 'THIEN_DONG_MENH_%' OR fragment_code LIKE 'THIEN_TUONG_MENH_%' OR fragment_code LIKE 'THAI_AM_MENH_%' OR fragment_code LIKE 'VU_KHUC_MENH_%' OR fragment_code LIKE 'CU_MON_MENH_%' OR fragment_code LIKE 'LIEM_TRINH_MENH_%' OR fragment_code LIKE 'THIEN_PHU_TAT_ACH_%' OR fragment_code LIKE 'THAT_SAT_TAI_BACH_%' OR fragment_code LIKE 'PHA_QUAN_TAI_BACH_%') AND fragment_code NOT LIKE '%_CHU_THAN_%'")
    log_info "Imported ${FINAL_FRAGMENTS_COUNT} final missing fragments"
else
    log_warn "File FRAGMENTS_MISSING_FINAL.sql not found"
fi

# Step 17: Import final missing rules
if [ -f "${SCRIPT_DIR}/RULES_MISSING_FINAL.sql" ]; then
    log_info "Importing: RULES_MISSING_FINAL.sql"
    import_sql_file "${SCRIPT_DIR}/RULES_MISSING_FINAL.sql"
    FINAL_RULES_COUNT=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules WHERE fragment_code LIKE 'THIEN_CO_MENH_%' OR fragment_code LIKE 'THIEN_DONG_MENH_%' OR fragment_code LIKE 'THIEN_TUONG_MENH_%' OR fragment_code LIKE 'THAI_AM_MENH_%' OR fragment_code LIKE 'VU_KHUC_MENH_%' OR fragment_code LIKE 'CU_MON_MENH_%' OR fragment_code LIKE 'LIEM_TRINH_MENH_%' OR fragment_code LIKE 'THIEN_PHU_TAT_ACH_%' OR fragment_code LIKE 'THAT_SAT_TAI_BACH_%' OR fragment_code LIKE 'PHA_QUAN_TAI_BACH_%'")
    log_info "Imported ${FINAL_RULES_COUNT} final missing rules"
else
    log_warn "File RULES_MISSING_FINAL.sql not found"
fi

# Verify import
log_info "Verifying import..."

TOTAL_FRAGMENTS=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_fragments")
TOTAL_RULES=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules")

log_info "============================================"
log_info "Import Summary:"
log_info "  Total Fragments: ${TOTAL_FRAGMENTS}"
log_info "  Total Rules: ${TOTAL_RULES}"
log_info "============================================"

# Check for orphaned rules (rules without fragments)
ORPHANED_RULES=$($MYSQL_CMD -N -e "SELECT COUNT(*) FROM interpretation_rules r LEFT JOIN interpretation_fragments f ON r.fragment_code = f.fragment_code WHERE f.fragment_code IS NULL")
if [ "$ORPHANED_RULES" -gt 0 ]; then
    log_warn "Found ${ORPHANED_RULES} orphaned rules (rules without matching fragments)"
else
    log_info "✓ All rules have matching fragments"
fi

log_info "Import completed successfully!"
