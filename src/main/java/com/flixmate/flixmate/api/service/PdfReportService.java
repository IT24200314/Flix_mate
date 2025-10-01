package com.flixmate.flixmate.api.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class PdfReportService {

    public byte[] generateSalesReportPdf(Map<String, Object> reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            document.add(new Paragraph("SALES REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20));

            // Report generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setMarginBottom(20));

            // Summary statistics
            Table summaryTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Revenue").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph("$" + reportData.get("totalRevenue"))));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Bookings").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(reportData.get("totalBookings")))));
            summaryTable.addCell(new Cell().add(new Paragraph("Average Ticket Price").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph("$" + reportData.get("averageTicketPrice"))));
            summaryTable.addCell(new Cell().add(new Paragraph("Growth Rate").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph(reportData.get("growthRate") + "%")));

            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            // Time series data
            @SuppressWarnings("unchecked")
            List<String> labels = (List<String>) reportData.get("labels");
            @SuppressWarnings("unchecked")
            List<Double> revenueData = (List<Double>) reportData.get("revenueData");

            if (labels != null && revenueData != null && !labels.isEmpty()) {
                document.add(new Paragraph("Revenue Over Time").setBold().setFontSize(14).setMarginTop(20));
                
                Table dataTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
                dataTable.addCell(new Cell().add(new Paragraph("Date").setBold()));
                dataTable.addCell(new Cell().add(new Paragraph("Revenue").setBold()));

                for (int i = 0; i < labels.size() && i < revenueData.size(); i++) {
                    dataTable.addCell(new Cell().add(new Paragraph(labels.get(i))));
                    dataTable.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", revenueData.get(i)))));
                }

                document.add(dataTable);
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate sales report PDF: " + e.getMessage());
        }
    }

    public byte[] generatePopularMoviesReportPdf(Map<String, Object> reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            document.add(new Paragraph("POPULAR MOVIES REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20));

            // Report generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setMarginBottom(20));

            // Movies data
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> movies = (List<Map<String, Object>>) reportData.get("movies");

            if (movies != null && !movies.isEmpty()) {
                Table moviesTable = new Table(6).setWidth(UnitValue.createPercentValue(100));
                
                // Header row
                moviesTable.addCell(new Cell().add(new Paragraph("Rank").setBold()));
                moviesTable.addCell(new Cell().add(new Paragraph("Movie Title").setBold()));
                moviesTable.addCell(new Cell().add(new Paragraph("Bookings").setBold()));
                moviesTable.addCell(new Cell().add(new Paragraph("Revenue").setBold()));
                moviesTable.addCell(new Cell().add(new Paragraph("Rating").setBold()));
                moviesTable.addCell(new Cell().add(new Paragraph("Release Year").setBold()));

                // Data rows
                for (int i = 0; i < movies.size(); i++) {
                    Map<String, Object> movie = movies.get(i);
                    moviesTable.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
                    moviesTable.addCell(new Cell().add(new Paragraph((String) movie.get("title"))));
                    moviesTable.addCell(new Cell().add(new Paragraph(String.valueOf(movie.get("bookings")))));
                    moviesTable.addCell(new Cell().add(new Paragraph("$" + movie.get("revenue"))));
                    moviesTable.addCell(new Cell().add(new Paragraph(String.valueOf(movie.get("rating")))));
                    moviesTable.addCell(new Cell().add(new Paragraph(String.valueOf(movie.get("releaseYear")))));
                }

                document.add(moviesTable);
            } else {
                document.add(new Paragraph("No movie data available."));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate popular movies report PDF: " + e.getMessage());
        }
    }

    public byte[] generatePaymentReportPdf(Map<String, Object> reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            document.add(new Paragraph("PAYMENT REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20));

            // Report generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setMarginBottom(20));

            // Payment statistics
            Table summaryTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Payments").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(reportData.get("totalPayments")))));
            summaryTable.addCell(new Cell().add(new Paragraph("Successful").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(reportData.get("successCount")))));
            summaryTable.addCell(new Cell().add(new Paragraph("Failed").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(reportData.get("failedCount")))));
            summaryTable.addCell(new Cell().add(new Paragraph("Pending").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(reportData.get("pendingCount")))));
            summaryTable.addCell(new Cell().add(new Paragraph("Success Rate").setBold()));
            summaryTable.addCell(new Cell().add(new Paragraph(reportData.get("successRate") + "%")));

            document.add(summaryTable);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payment report PDF: " + e.getMessage());
        }
    }

    public byte[] generateTrendsReportPdf(Map<String, Object> reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            document.add(new Paragraph("BOOKING TRENDS REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20));

            // Report generation date
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setMarginBottom(20));

            // Trends data
            @SuppressWarnings("unchecked")
            List<String> labels = (List<String>) reportData.get("labels");
            @SuppressWarnings("unchecked")
            List<Double> values = (List<Double>) reportData.get("values");

            if (labels != null && values != null && !labels.isEmpty()) {
                Table trendsTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
                trendsTable.addCell(new Cell().add(new Paragraph("Period").setBold()));
                trendsTable.addCell(new Cell().add(new Paragraph("Bookings").setBold()));

                for (int i = 0; i < labels.size() && i < values.size(); i++) {
                    trendsTable.addCell(new Cell().add(new Paragraph(labels.get(i))));
                    trendsTable.addCell(new Cell().add(new Paragraph(String.valueOf(values.get(i).intValue()))));
                }

                document.add(trendsTable);
            } else {
                document.add(new Paragraph("No trends data available."));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate trends report PDF: " + e.getMessage());
        }
    }
}
