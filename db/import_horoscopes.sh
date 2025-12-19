#!/bin/bash
#
# import_horoscopes.sh
# Script to import horoscope SQL data into MySQL database
#
# Usage:
#   ./import_horoscopes.sh [options]
#
# Options:
#   -h, --host       MySQL host (default: localhost)
#   -P, --port       MySQL port (default: 3306)
#   -u, --user       MySQL user (default: root)
#   -p, --password   MySQL password (will prompt if not provided)
#   -d, --database   Database name (default: lich_van_nien)
#   --skip-schema    Skip schema creation
#   --skip-validate  Skip validation after import
#   --skip-truncate  Skip truncating existing data before import
#   --dry-run        Show what would be done without executing

set -e

# Default values
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
DATABASE="${DATABASE:-lich_van_nien}"
SKIP_SCHEMA=false
SKIP_VALIDATE=false
SKIP_TRUNCATE=false
DRY_RUN=false

# Directory containing this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_DIR="$SCRIPT_DIR/generated_horoscope.sql"
SCHEMA_FILE="$SCRIPT_DIR/horoscope.sql"

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

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--host)
            MYSQL_HOST="$2"
            shift 2
            ;;
        -P|--port)
            MYSQL_PORT="$2"
            shift 2
            ;;
        -u|--user)
            MYSQL_USER="$2"
            shift 2
            ;;
        -p|--password)
            MYSQL_PASSWORD="$2"
            shift 2
            ;;
        -d|--database)
            DATABASE="$2"
            shift 2
            ;;
        --skip-schema)
            SKIP_SCHEMA=true
            shift
            ;;
        --skip-validate)
            SKIP_VALIDATE=true
            shift
            ;;
        --skip-truncate)
            SKIP_TRUNCATE=true
            shift
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        *)
            log_error "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Prompt for password if not provided
if [ -z "$MYSQL_PASSWORD" ]; then
    read -sp "Enter MySQL password for $MYSQL_USER: " MYSQL_PASSWORD
    echo
fi

# MySQL command
MYSQL_CMD="mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD $DATABASE"

execute_sql() {
    local sql_file="$1"
    local description="$2"
    
    if [ ! -f "$sql_file" ]; then
        log_error "File not found: $sql_file"
        return 1
    fi
    
    local line_count=$(wc -l < "$sql_file")
    log_info "Importing $description ($line_count lines)..."
    
    if [ "$DRY_RUN" = true ]; then
        log_info "[DRY RUN] Would execute: $sql_file"
    else
        # Disable indexes for faster import
        $MYSQL_CMD -e "SET FOREIGN_KEY_CHECKS=0; SET UNIQUE_CHECKS=0; SET AUTOCOMMIT=0;" 2>/dev/null
        
        # Import with progress
        $MYSQL_CMD < "$sql_file" 2>/dev/null
        
        # Re-enable constraints
        $MYSQL_CMD -e "SET FOREIGN_KEY_CHECKS=1; SET UNIQUE_CHECKS=1; COMMIT;" 2>/dev/null
        
        log_info "✓ Completed: $description"
    fi
}

count_rows() {
    local table="$1"
    $MYSQL_CMD -N -e "SELECT COUNT(*) FROM $table" 2>/dev/null
}

# Main execution
log_info "=========================================="
log_info "Horoscope Data Import Script"
log_info "=========================================="
log_info "Host: $MYSQL_HOST:$MYSQL_PORT"
log_info "Database: $DATABASE"
log_info "SQL Directory: $SQL_DIR"
log_info "=========================================="

# Step 1: Create schema if needed
if [ "$SKIP_SCHEMA" = false ]; then
    if [ -f "$SCHEMA_FILE" ]; then
        log_info "Step 1: Creating horoscope tables..."
        if [ "$DRY_RUN" = true ]; then
            log_info "[DRY RUN] Would create schema from: $SCHEMA_FILE"
        else
            $MYSQL_CMD < "$SCHEMA_FILE" 2>/dev/null || true
            log_info "✓ Schema created/updated"
        fi
    else
        log_warn "Schema file not found: $SCHEMA_FILE"
    fi
else
    log_info "Step 1: Skipping schema creation (--skip-schema)"
fi

# Step 2: Truncate existing data (if not skipped)
if [ "$SKIP_TRUNCATE" = false ]; then
    log_info "Step 2: Truncating existing horoscope data..."
    if [ "$DRY_RUN" = true ]; then
        log_info "[DRY RUN] Would truncate tables: horoscope_lifetime, horoscope_yearly, horoscope_monthly, horoscope_daily"
    else
        $MYSQL_CMD -e "
            SET FOREIGN_KEY_CHECKS=0;
            TRUNCATE TABLE horoscope_lifetime;
            TRUNCATE TABLE horoscope_yearly;
            TRUNCATE TABLE horoscope_monthly;
            TRUNCATE TABLE horoscope_daily;
            SET FOREIGN_KEY_CHECKS=1;
        " 2>/dev/null || true
        log_info "✓ Existing data truncated"
    fi
else
    log_info "Step 2: Skipping truncate (--skip-truncate)"
fi

# Step 3: Disable indexes
log_info "Step 3: Preparing database for bulk import..."
if [ "$DRY_RUN" = false ]; then
    $MYSQL_CMD -e "SET GLOBAL foreign_key_checks=0;" 2>/dev/null || true
fi

# Step 4: Import data files
log_info "Step 4: Importing data files..."

# Order matters for foreign key relationships
declare -a import_files=(
    "insert_horoscope_lifetime.sql|Lifetime horoscope data"
    "insert_horoscope_yearly.sql|Yearly horoscope data"
    "insert_horoscope_monthly.sql|Monthly horoscope data"
    "insert_horoscope_daily.sql|Daily horoscope data"
)

for import_item in "${import_files[@]}"; do
    IFS='|' read -r file description <<< "$import_item"
    sql_path="$SQL_DIR/$file"
    if [ -f "$sql_path" ]; then
        execute_sql "$sql_path" "$description"
    else
        log_warn "File not found, skipping: $sql_path"
    fi
done

# Step 5: Re-enable indexes
log_info "Step 5: Re-enabling database constraints..."
if [ "$DRY_RUN" = false ]; then
    $MYSQL_CMD -e "SET GLOBAL foreign_key_checks=1;" 2>/dev/null || true
fi

# Step 6: Validate import
if [ "$SKIP_VALIDATE" = false ] && [ "$DRY_RUN" = false ]; then
    log_info "Step 6: Validating import..."
    echo ""
    log_info "Row counts:"
    echo "  horoscope_lifetime: $(count_rows horoscope_lifetime)"
    echo "  horoscope_yearly:   $(count_rows horoscope_yearly)"
    echo "  horoscope_monthly:  $(count_rows horoscope_monthly)"
    echo "  horoscope_daily:    $(count_rows horoscope_daily)"
    echo ""
else
    log_info "Step 6: Skipping validation"
fi

log_info "=========================================="
log_info "Import completed successfully!"
log_info "=========================================="

