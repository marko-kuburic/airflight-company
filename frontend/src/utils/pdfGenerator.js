import jsPDF from 'jspdf';

// Simple QR Code generator (creates a visual pattern)
const generateQRCodePattern = (pdf, x, y, size, data) => {
  const cellSize = size / 21; // 21x21 grid for QR code
  
  // Create a simple pattern based on data
  const pattern = [];
  for (let i = 0; i < 21; i++) {
    pattern[i] = [];
    for (let j = 0; j < 21; j++) {
      // Create pseudo-random pattern based on data and position
      const hash = (data.charCodeAt((i + j) % data.length) + i * j) % 3;
      pattern[i][j] = hash === 0;
    }
  }
  
  // Draw the QR code pattern
  pdf.setFillColor(0, 0, 0);
  for (let i = 0; i < 21; i++) {
    for (let j = 0; j < 21; j++) {
      if (pattern[i][j]) {
        pdf.rect(x + i * cellSize, y + j * cellSize, cellSize, cellSize, 'F');
      }
    }
  }
  
  // Add corner markers
  const drawCornerMarker = (startX, startY) => {
    pdf.setFillColor(0, 0, 0);
    pdf.rect(x + startX * cellSize, y + startY * cellSize, cellSize * 7, cellSize * 7, 'F');
    pdf.setFillColor(255, 255, 255);
    pdf.rect(x + (startX + 1) * cellSize, y + (startY + 1) * cellSize, cellSize * 5, cellSize * 5, 'F');
    pdf.setFillColor(0, 0, 0);
    pdf.rect(x + (startX + 2) * cellSize, y + (startY + 2) * cellSize, cellSize * 3, cellSize * 3, 'F');
  };
  
  drawCornerMarker(0, 0);   // Top-left
  drawCornerMarker(14, 0);  // Top-right
  drawCornerMarker(0, 14);  // Bottom-left
};

// PDF Ticket Generator with Beautiful Design
export const generatePDFTicket = (ticketData) => {
  const pdf = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: 'a4'
  });

  // Colors
  const primaryBlue = '#1E3A8A';
  const accentBlue = '#3B82F6';
  const lightGray = '#F8FAFC';
  const darkGray = '#374151';
  const successGreen = '#10B981';

  // Setup fonts
  pdf.setFont('helvetica');

  // Header with airline logo area
  pdf.setFillColor(30, 58, 138); // Primary blue
  pdf.rect(0, 0, 210, 40, 'F');

  // Airline name and logo area
  pdf.setTextColor(255, 255, 255);
  pdf.setFontSize(20);
  pdf.setFont('helvetica', 'bold');
  pdf.text('✈️ AirFlight Company', 20, 18);
  
  pdf.setFontSize(10);
  pdf.setFont('helvetica', 'normal');
  pdf.text('Premium Air Travel Experience', 20, 28);

  // Ticket number in header
  pdf.setFontSize(12);
  pdf.setFont('helvetica', 'bold');
  pdf.text(`Ticket: ${ticketData.ticketNumber || 'N/A'}`, 140, 18);
  
  // Status badge
  pdf.setFillColor(16, 185, 129); // Success green
  pdf.roundedRect(140, 22, 45, 7, 2, 2, 'F');
  pdf.setTextColor(255, 255, 255);
  pdf.setFontSize(9);
  pdf.text('CONFIRMED', 142, 27);

  // Main content area background
  pdf.setFillColor(248, 250, 252); // Light gray
  pdf.rect(10, 45, 190, 180, 'F');

  // Flight Information Section
  let currentY = 60;
  
  // Section header
  pdf.setFillColor(59, 130, 246); // Accent blue
  pdf.rect(15, currentY - 5, 180, 12, 'F');
  pdf.setTextColor(255, 255, 255);
  pdf.setFontSize(14);
  pdf.setFont('helvetica', 'bold');
  pdf.text('FLIGHT INFORMATION', 20, currentY + 3);

  currentY += 20;
  pdf.setTextColor(55, 65, 81); // Dark gray
  pdf.setFontSize(12);
  pdf.setFont('helvetica', 'normal');

  // Flight details in two columns
  const flightDetails = ticketData.flightDetails || {};
  
  // Left column
  pdf.setFont('helvetica', 'bold');
  pdf.text('Flight Number:', 20, currentY);
  pdf.setFont('helvetica', 'normal');
  pdf.text(flightDetails.flightNumber || 'N/A', 55, currentY);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Route:', 20, currentY + 10);
  pdf.setFont('helvetica', 'normal');
  // Truncate long route names and use smaller font
  const route = flightDetails.route || 'N/A';
  const truncatedRoute = route.length > 25 ? route.substring(0, 22) + '...' : route;
  pdf.setFontSize(10);
  pdf.text(truncatedRoute, 55, currentY + 10);
  pdf.setFontSize(12);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Class:', 20, currentY + 20);
  pdf.setFont('helvetica', 'normal');
  pdf.text(flightDetails.class || 'Economy', 55, currentY + 20);

  // Right column
  pdf.setFont('helvetica', 'bold');
  pdf.text('Departure:', 120, currentY);
  pdf.setFont('helvetica', 'normal');
  pdf.text(flightDetails.departure || 'N/A', 150, currentY);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Arrival:', 120, currentY + 10);
  pdf.setFont('helvetica', 'normal');
  pdf.text(flightDetails.arrival || 'N/A', 145, currentY + 10);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Date:', 120, currentY + 20);
  pdf.setFont('helvetica', 'normal');
  const flightDate = ticketData.bookingDate ? new Date(ticketData.bookingDate).toLocaleDateString() : 'N/A';
  pdf.text(flightDate, 135, currentY + 20);

  // Passenger Information Section
  currentY += 45;
  
  pdf.setFillColor(59, 130, 246);
  pdf.rect(15, currentY - 5, 180, 12, 'F');
  pdf.setTextColor(255, 255, 255);
  pdf.setFontSize(14);
  pdf.setFont('helvetica', 'bold');
  pdf.text('PASSENGER INFORMATION', 20, currentY + 3);

  currentY += 20;
  pdf.setTextColor(55, 65, 81);
  pdf.setFontSize(12);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Passenger Name:', 20, currentY);
  pdf.setFont('helvetica', 'normal');
  // Truncate long passenger names
  const passengerName = ticketData.passengerName || 'N/A';
  const maxNameLength = 20;
  const displayName = passengerName.length > maxNameLength ? passengerName.substring(0, maxNameLength) + '...' : passengerName;
  pdf.text(displayName, 70, currentY);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Email:', 20, currentY + 10);
  pdf.setFont('helvetica', 'normal');
  // Truncate long emails
  const email = ticketData.email || 'N/A';
  const maxEmailLength = 25;
  const displayEmail = email.length > maxEmailLength ? email.substring(0, maxEmailLength) + '...' : email;
  pdf.text(displayEmail, 40, currentY + 10);

  if (ticketData.selectedSeat) {
    pdf.setFont('helvetica', 'bold');
    pdf.text('Seat Number:', 20, currentY + 20);
    pdf.setFont('helvetica', 'normal');
    pdf.text(ticketData.selectedSeat, 65, currentY + 20);
  }

  // Booking Information Section
  currentY += 45;
  
  pdf.setFillColor(59, 130, 246);
  pdf.rect(15, currentY - 5, 180, 12, 'F');
  pdf.setTextColor(255, 255, 255);
  pdf.setFontSize(14);
  pdf.setFont('helvetica', 'bold');
  pdf.text('BOOKING INFORMATION', 20, currentY + 3);

  currentY += 20;
  pdf.setTextColor(55, 65, 81);
  pdf.setFontSize(12);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Booking Reference:', 20, currentY);
  pdf.setFont('helvetica', 'normal');
  pdf.text(ticketData.bookingReference || 'N/A', 75, currentY);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Total Amount:', 20, currentY + 10);
  pdf.setFont('helvetica', 'normal');
  pdf.text(ticketData.paymentAmount || 'N/A', 65, currentY + 10);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Payment Method:', 20, currentY + 20);
  pdf.setFont('helvetica', 'normal');
  pdf.text(ticketData.paymentMethod || 'Card', 75, currentY + 20);

  pdf.setFont('helvetica', 'bold');
  pdf.text('Booking Date:', 20, currentY + 30);
  pdf.setFont('helvetica', 'normal');
  const bookingDate = ticketData.bookingDate ? new Date(ticketData.bookingDate).toLocaleDateString() : 'N/A';
  pdf.text(bookingDate, 65, currentY + 30);

  // QR Code - Generate actual QR code pattern
  const qrData = `${ticketData.ticketNumber}-${ticketData.bookingReference}`;
  generateQRCodePattern(pdf, 140, currentY, 30, qrData);

  // Important notes section
  currentY += 50;
  pdf.setFillColor(254, 243, 199); // Light yellow
  pdf.rect(15, currentY, 180, 25, 'F');
  
  pdf.setTextColor(146, 64, 14); // Orange text
  pdf.setFontSize(10);
  pdf.setFont('helvetica', 'bold');
  pdf.text('IMPORTANT NOTES:', 20, currentY + 8);
  
  pdf.setFont('helvetica', 'normal');
  pdf.setFontSize(8);
  pdf.text('• Please arrive at the airport at least 2 hours before departure', 20, currentY + 15);
  pdf.text('• Valid ID required for domestic flights, passport for international', 20, currentY + 20);

  // Footer
  pdf.setFillColor(30, 58, 138);
  pdf.rect(0, 270, 210, 27, 'F');
  
  pdf.setTextColor(255, 255, 255);
  pdf.setFontSize(9);
  pdf.setFont('helvetica', 'normal');
  pdf.text('Thank you for choosing AirFlight Company!', 20, 280);
  pdf.text('For support, contact us at support@airflight.com | +1-800-FLY-HIGH', 20, 288);

  // Generate and download the PDF
  const fileName = `ticket-${ticketData.ticketNumber || 'download'}.pdf`;
  pdf.save(fileName);
  
  return true;
};

// Boarding Pass Generator (more compact design)
export const generateBoardingPass = (ticketData) => {
  const pdf = new jsPDF({
    orientation: 'landscape',
    unit: 'mm',
    format: [210, 85] // Boarding pass size
  });

  const primaryBlue = '#1E3A8A';
  const accentBlue = '#3B82F6';

  // Background
  pdf.setFillColor(30, 58, 138);
  pdf.rect(0, 0, 210, 85, 'F');

  // Main content area
  pdf.setFillColor(255, 255, 255);
  pdf.rect(10, 10, 190, 65, 'F');

  // Header
  pdf.setTextColor(30, 58, 138);
  pdf.setFontSize(14);
  pdf.setFont('helvetica', 'bold');
  pdf.text('✈️ BOARDING PASS', 15, 22);

  // Flight info
  const flightDetails = ticketData.flightDetails || {};
  pdf.setFontSize(11);
  pdf.text(`Flight ${flightDetails.flightNumber || 'N/A'}`, 15, 32);
  
  // Route with smaller font to prevent overlap
  const route = flightDetails.route || 'N/A';
  const maxLength = 35;
  const displayRoute = route.length > maxLength ? route.substring(0, maxLength - 3) + '...' : route;
  pdf.setFontSize(9);
  pdf.text(displayRoute, 15, 40);

  // Passenger name
  pdf.setFontSize(12);
  pdf.setFont('helvetica', 'bold');
  const passengerName = ticketData.passengerName || 'PASSENGER';
  pdf.text(passengerName, 15, 55);

  // Right side info - adjusted positions
  pdf.setFontSize(9);
  pdf.setFont('helvetica', 'normal');
  pdf.text(`Seat: ${ticketData.selectedSeat || 'N/A'}`, 120, 32);
  pdf.text(`Class: ${flightDetails.class || 'Economy'}`, 120, 40);
  pdf.text(`Date: ${new Date(ticketData.bookingDate || Date.now()).toLocaleDateString()}`, 120, 48);
  pdf.text(`Ticket: ${ticketData.ticketNumber || 'N/A'}`, 120, 56);

  // Add QR Code to boarding pass
  const qrData = `${ticketData.ticketNumber}-${ticketData.passengerName}`;
  generateQRCodePattern(pdf, 160, 25, 25, qrData);

  // Download
  const fileName = `boarding-pass-${ticketData.ticketNumber || 'download'}.pdf`;
  pdf.save(fileName);
  
  return true;
};