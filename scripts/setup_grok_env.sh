#!/bin/bash
# Setup script for Grok API configuration
# This script helps you set up the .env file for Grok API

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

echo "=========================================="
echo "Grok API Configuration Setup"
echo "=========================================="
echo ""

# Check if .env already exists
if [ -f .env ]; then
    echo "⚠️  .env file already exists!"
    read -p "Do you want to overwrite it? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Aborted. Keeping existing .env file."
        exit 0
    fi
fi

# Copy from example
if [ ! -f .env.example ]; then
    echo "❌ Error: .env.example not found!"
    exit 1
fi

cp .env.example .env
echo "✅ Created .env file from .env.example"
echo ""

# Prompt for API key
echo "Please enter your Grok API key:"
echo "(Get it from: https://console.x.ai/)"
read -p "GROK_API_KEY: " grok_key

if [ -z "$grok_key" ]; then
    echo "⚠️  Warning: No API key provided. You can edit .env file later."
else
    # Update .env file with actual key
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        sed -i '' "s|GROK_API_KEY=.*|GROK_API_KEY=$grok_key|" .env
    else
        # Linux
        sed -i "s|GROK_API_KEY=.*|GROK_API_KEY=$grok_key|" .env
    fi
    echo "✅ API key saved to .env file"
fi

echo ""
echo "=========================================="
echo "Setup Complete!"
echo "=========================================="
echo ""
echo "To use the environment variables:"
echo "  1. Load them: source load_env.sh"
echo "  2. Or run: export \$(cat .env | grep -v '^#' | xargs)"
echo ""
echo "To start the application:"
echo "  source load_env.sh && mvn spring-boot:run"
echo ""
echo "⚠️  IMPORTANT: Never commit .env file to git!"
echo "   The .gitignore is configured to exclude it."

