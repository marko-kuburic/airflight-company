#!/bin/bash

# Database Population Script
# This script populates the database with test data after the application is running

echo "🚀 Starting database population..."

# Check if containers are running
if ! docker ps | grep -q "air-company-mysql"; then
    echo "❌ MySQL container is not running. Please start the containers first with: docker-compose up -d"
    exit 1
fi

if ! docker ps | grep -q "air-company-app"; then
    echo "❌ Application container is not running. Please start the containers first with: docker-compose up -d"
    exit 1
fi

echo "✅ Containers are running"

# Wait for application to be ready
echo "⏳ Waiting for application to be ready..."
sleep 5

# Check if application is responding
if ! curl -s http://localhost:8080/api/actuator/health > /dev/null; then
    echo "❌ Application is not responding. Please wait for it to start completely."
    exit 1
fi

echo "✅ Application is ready"

# Run the population script
echo "📊 Running database population script..."
docker cp ./docker/mysql/populate-database.sql air-company-mysql:/tmp/populate-database.sql
docker exec air-company-mysql mysql -u root -ppassword air_company -e "source /tmp/populate-database.sql"

# Verify the data was inserted
echo "🔍 Verifying data insertion..."
docker exec air-company-mysql mysql -u root -ppassword air_company -e "
SELECT 'Users' as Table_Name, COUNT(*) as Records FROM users
UNION ALL SELECT 'Airports', COUNT(*) FROM airports  
UNION ALL SELECT 'Countries', COUNT(*) FROM countries
UNION ALL SELECT 'Aircraft', COUNT(*) FROM aircraft
UNION ALL SELECT 'Services', COUNT(*) FROM services;
"

echo "✅ Database population completed successfully!"
echo ""
echo "🔐 Test credentials:"
echo "   Flight Dispatcher: dispatcher@aircompany.com / password"
echo "   Technician: milan.tech@aircompany.com / password"

