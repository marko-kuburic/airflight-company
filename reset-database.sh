#!/bin/bash

# Script to restart the database with fresh data
# This will completely reset the database and populate it with test data

echo "🔄 Restarting database with fresh data..."

# Stop all services
echo "📦 Stopping all services..."
docker-compose down

# Remove the MySQL volume to completely reset the database
echo "🗑️  Removing MySQL volume to reset database..."
docker volume rm air-company_mysql_data 2>/dev/null || echo "Volume doesn't exist, continuing..."

# Start MySQL first and wait for it to be ready
echo "🚀 Starting MySQL service..."
docker-compose up -d mysql

# Wait for MySQL to be healthy
echo "⏳ Waiting for MySQL to be ready..."
while ! docker-compose exec mysql mysqladmin ping -h localhost --silent; do
    echo "Waiting for MySQL..."
    sleep 2
done

echo "✅ MySQL is ready!"

# Start the application
echo "🚀 Starting application..."
docker-compose up -d app

# Wait for application to be ready
echo "⏳ Waiting for application to be ready..."
sleep 10

# Check if application is running
if docker-compose ps app | grep -q "Up"; then
    echo "✅ Application is running!"
else
    echo "❌ Application failed to start. Check logs:"
    docker-compose logs app --tail 20
    exit 1
fi

# Start frontend
echo "🚀 Starting frontend..."
docker-compose up -d frontend

echo ""
echo "🎉 Database reset complete!"
echo ""
echo "📊 Test data includes:"
echo "   • 10 Countries"
echo "   • 10 Airports"
echo "   • 3 Flight Dispatchers"
echo "   • 4 Technicians"
echo "   • 10 Aircraft"
echo "   • 6 Maintenance Records"
echo "   • 6 Routes"
echo ""
echo "🔑 Login credentials:"
echo "   • Flight Dispatcher: dispatcher@aircompany.com / password123"
echo "   • Technician: milan.tech@aircompany.com / password123"
echo ""
echo "🌐 Access the application at: http://localhost:3000"
echo ""

