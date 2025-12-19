#!/bin/bash
#
# truncate_horoscopes.sh
# Script to truncate (delete all data) from horoscope tables
#
# Usage:
#   ./truncate_horoscopes.sh [options]
#
# Options:
#   -h, --host       MySQL host (default: localhost)
#   -P, --port       MySQL port (default: 3306)
#   -u, --user       MySQL user (default: root)
#   -p, --password   MySQL password (will prompt if not provided)
#   -d, --database   Database name (default: lich_van_nien)
#   --dry-run        Show what would be done without executing

set -e

# Default values
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
DATABASE="${DATABASE:-lich_van_nien}"
DRY_RUN=false

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

count_rows() {
    local table="$1"
    $MYSQL_CMD -N -e "SELECT COUNT(*) FROM $table" 2>/dev/null || echo "0"
}

# Main execution
log_info "=========================================="
log_info "Horoscope Data Truncate Script"
log_info "=========================================="
log_info "Host: $MYSQL_HOST:$MYSQL_PORT"
log_info "Database: $DATABASE"
log_info "=========================================="

# Show current row counts
log_info "Current row counts:"
echo "  horoscope_lifetime: $(count_rows horoscope_lifetime)"
echo "  horoscope_yearly:   $(count_rows horoscope_yearly)"
echo "  horoscope_monthly:  $(count_rows horoscope_monthly)"
echo "  horoscope_daily:    $(count_rows horoscope_daily)"
echo ""

# Confirm before truncating
if [ "$DRY_RUN" = false ]; then
    log_warn "WARNING: This will DELETE ALL DATA from horoscope tables!"
    read -p "Are you sure you want to continue? (yes/no): " confirm
    if [ "$confirm" != "yes" ]; then
        log_info "Cancelled."
        exit 0
    fi
fi

# Truncate tables
log_info "Truncating tables..."
if [ "$DRY_RUN" = true ]; then
    log_info "[DRY RUN] Would execute:"
    echo "  TRUNCATE TABLE horoscope_lifetime;"
    echo "  TRUNCATE TABLE horoscope_yearly;"
    echo "  TRUNCATE TABLE horoscope_monthly;"
    echo "  TRUNCATE TABLE horoscope_daily;"
else
    $MYSQL_CMD -e "
        SET FOREIGN_KEY_CHECKS=0;
        TRUNCATE TABLE horoscope_lifetime;
        TRUNCATE TABLE horoscope_yearly;
        TRUNCATE TABLE horoscope_monthly;
        TRUNCATE TABLE horoscope_daily;
        SET FOREIGN_KEY_CHECKS=1;
    " 2>/dev/null
    
    log_info "✓ All tables truncated"
fi

# Show final row counts
log_info "Final row counts:"
echo "  horoscope_lifetime: $(count_rows horoscope_lifetime)"
echo "  horoscope_yearly:   $(count_rows horoscope_yearly)"
echo "  horoscope_monthly:  $(count_rows horoscope_monthly)"
echo "  horoscope_daily:    $(count_rows horoscope_daily)"
echo ""

log_info "=========================================="
log_info "Truncate completed successfully!"
log_info "=========================================="
