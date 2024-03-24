package com.dle.google.drive;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * This class provides utility methods to create a PDF document with a header, footer, table, and image using PDFBox 3.0.0.
 */
public class PDFCreator {
    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private float margin = 50;
    private float yPosition;
    private float tableWidth;
    private float tableHeight;
    private boolean newPage;

    /**
     * Constructor to initialize the PDF document and set the default page properties.
     */
    public PDFCreator() {
        document = new PDDocument();
        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);
        yPosition = currentPage.getMediaBox().getHeight() - (2 * margin);
        tableWidth = currentPage.getMediaBox().getWidth() - (2 * margin);
        tableHeight = 20;
        newPage = false;
    }

    /**
     * Adds a header to the current page of the PDF document.
     *
     * @param headerText The text to be displayed in the header.
     * @throws IOException If an I/O error occurs while writing to the PDF document.
     */
    public void addHeader(String headerText) throws IOException {
        contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, true);
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin, currentPage.getMediaBox().getHeight() - margin);
        contentStream.showText(headerText);
        contentStream.endText();
        contentStream.close();
    }

    /**
     * Adds a footer to the current page of the PDF document.
     *
     * @param footerText The text to be displayed in the footer.
     * @throws IOException If an I/O error occurs while writing to the PDF document.
     */
    public void addFooter(String footerText) throws IOException {
        contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, true);
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(margin, margin);
        contentStream.showText(footerText);
        contentStream.endText();
        contentStream.close();
    }

    /**
     * Adds a table to the current page of the PDF document.
     *
     * @param tableData The data to be displayed in the table.
     * @throws IOException If an I/O error occurs while writing to the PDF document.
     */
    public void addTable(String[][] tableData) throws IOException {
        contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, true);
        float startX = margin;
        float startY = yPosition;
        float cellWidth = tableWidth / (float) tableData[0].length;
        float cellHeight = tableHeight;

        for (String[] row : tableData) {
            for (int i = 0; i < row.length; i++) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.setLeading(12);
                contentStream.beginText();
                contentStream.newLineAtOffset(startX, startY);
                contentStream.showText(row[i]);
                contentStream.endText();
                startX += cellWidth;
            }
            startY -= cellHeight;
            startX = margin;
        }

        contentStream.close();
    }

    /**
     * Adds an image to the current page of the PDF document.
     *
     * @param imagePath The path to the image file.
     * @throws IOException If an I/O error occurs while writing to the PDF document.
     */
    public void addImage(String imagePath) throws IOException {
        PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);
        float imageWidth = image.getWidth();
        float imageHeight = image.getHeight();
        float scale = 0.5f;
        float x = (currentPage.getMediaBox().getWidth() - (imageWidth * scale)) / 2;
        float y = (currentPage.getMediaBox().getHeight() - (imageHeight * scale)) / 2;

        contentStream = new PDPageContentStream(document, currentPage, AppendMode.APPEND, true, true);
        contentStream.drawImage(image, x, y, imageWidth * scale, imageHeight * scale);
        contentStream.close();
    }

    /**
     * Saves the PDF document to the specified file path.
     *
     * @param filePath The path where the PDF document should be saved.
     * @throws IOException If an I/O error occurs while saving the PDF document.
     */
    public void saveDocument(String filePath) throws IOException {
        document.save(new File(filePath));
        document.close();
    }

    /**
     * Unit tests for the PDFCreator class.
     */
    public static void main(String[] args) {
//        try {
//            PDFCreator pdfCreator = new PDFCreator();
//
//            // Add header
//            pdfCreator.addHeader("Sample PDF Header");
//
//            // Add footer
//            pdfCreator.addFooter("Sample PDF Footer");
//
//            // Add table
//            String[][] tableData = {{"Name", "Age", "City"}, {"John Doe", "30", "New York"}, {"Jane Smith", "25", "London"}};
////            pdfCreator.addTable(tableData);
//
//            // Add image
////            String imagePath = "path/to/image.jpg";
////            pdfCreator.addImage(imagePath);
//
//            // Save the PDF document
//            String filePath = "/Users/dle/Downloads/output.pdf";
//            pdfCreator.saveDocument(filePath);
//
//            System.out.println("PDF document created successfully.");
//        } catch (IOException e) {
//            System.out.println("Error occurred while creating the PDF document: " + e.getMessage());
//        }


////        String url = "jdbc:databricks://adb-4437978527350769.9.azuredatabricks.net:443";
//        String url = "jdbc:databricks://adb-5541770754405938.18.azuredatabricks.net:443";
//        Properties p = new java.util.Properties();
////        p.put("httpPath", "/sql/1.0/warehouses/d40cc241a467aede");
//        p.put("httpPath", "/sql/1.0/warehouses/b8ccc44142b335f0");
//        p.put("AuthMech", "3");
//        p.put("UID", "token");
////        p.put("PWD", "");
//        p.put("PWD", "");
//        p.put("EnableArrow", "0"); //https://community.databricks.com/t5/data-engineering/jdbc-driver-support-for-openjdk-17/td-p/32584
//        try {
//            Connection conn = DriverManager.getConnection(url, p);
//            //TABLE-samples-nyctaxi-trips
//            //TABLE-samples-tpch-customer
//            //TABLE-samples-tpch-lineitem
//            //TABLE-samples-tpch-nation
//            //TABLE-samples-tpch-orders
//            //TABLE-samples-tpch-part
//            //TABLE-samples-tpch-partsupp
//            //TABLE-samples-tpch-region
//            //TABLE-samples-tpch-supplier
//            // cde6fa59-abb3-4971-be01-2443c417cbda / test-databricks /  MyDatabricksWorkspace
//
//            String query = "select * from samples.tpch.customer limit 5";
////            String query1 = "SELECT CatalogName AS TABLE_CAT, SchemaName AS TABLE_SCHEM, TableName AS TABLE_NAME, TableType AS TABLE_TYPE, Description AS REMARKS FROM sys_tables";
//            try (Statement stmt = conn.createStatement()) {
//                ResultSet rs = stmt.executeQuery(query);
////                ResultSet rs = stmt.executeQuery(query1);
//                while (rs.next()) {
//                    String c_name = rs.getString("c_name");
//                    String c_address = rs.getString("c_address");
//                    String c_comment = rs.getString("c_comment");
//                    System.out.printf("[c_name: %s, c_address: %s, c_comment: %s]\n", c_name, c_address, c_comment);
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
            //com.databricks.client.jdbc.Driver
            //jdbc:databricks://<HOST>:<PORT>;httpPath=/sql/1.0/warehouses/377827591cf1d629;AuthMech=3;UID=token;PWD=;EnableArrow=0



//            DatabaseMetaData md = conn.getMetaData();
//
//            ResultSet rs = md.getCatalogs();
//            while (rs.next()) {
//                System.out.print(rs.getString(1));
//                System.out.println();
//            }
//
//            rs = md.getSchemas(null, "%");
//            while (rs.next()) {
//                System.out.print(rs.getString(1));
//                System.out.print("-");
//                System.out.print(rs.getString(2));
//                System.out.println();
//            }
//
//            rs = md.getTables(null, null, "%", new String[]{"TABLE", "VIEW"});
//            while (rs.next()) {
//                System.out.print(rs.getString(1));
//                System.out.print("-");
//                System.out.print(rs.getString(2));
//                System.out.print("-");
//                System.out.print(rs.getString(3));
//                System.out.println();
//            }
//
//            rs = md.getColumns(null, null, "%", "%");
//            while (rs.next()) {
//                System.out.print(rs.getString(1));
//                System.out.print("-");
//                System.out.print(rs.getString(2));
//                System.out.print("-");
//                System.out.print(rs.getString(3));
//                System.out.print("-");
//                System.out.print(rs.getString(4));
//                System.out.println();
//            }



        String url = "jdbc:databricks://adb-5541770754405938.18.azuredatabricks.net:443";
        Properties p = new java.util.Properties();
        p.put("httpPath", "/sql/1.0/warehouses/b8ccc44142b335f0");
        p.put("AuthMech", "3");
        p.put("UID", "token");
        p.put("PWD", "");
        p.put("EnableArrow", "0"); //https://community.databricks.com/t5/data-engineering/jdbc-driver-support-for-openjdk-17/td-p/32584
        try {
            Connection conn = DriverManager.getConnection(url, p);
            String query = "select * from samples.tpch.customer limit 5";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String c_name = rs.getString("c_name");
                    String c_address = rs.getString("c_address");
                    String c_comment = rs.getString("c_comment");
                    System.out.printf("[c_name: %s, c_address: %s, c_comment: %s]\n", c_name, c_address, c_comment);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}