//package com.dle;
//
//import java.awt.Color;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.pdfbox.Loader;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.font.PDFont;
//import org.apache.pdfbox.pdmodel.font.PDType0Font;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.apache.pdfbox.text.TextPosition;
//import org.apache.pdfbox.util.Matrix;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
///**
// * @author mkl
// */
//public class RectanglesOverText {
//    final static File RESULT_FOLDER = new File("target/test-outputs", "content");
//
//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//        RESULT_FOLDER.mkdirs();
//    }
//
//    /**
//     * <a href="https://stackoverflow.com/questions/46080131/text-coordinates-when-stripping-from-pdfbox">
//     * Text coordinates when stripping from PDFBox
//     * </a>
//     * <p>
//     * This test applies the OP's code to an arbitrary PDF file and it did work properly
//     * (well, it did only cover the text from the baseline upwards but that is to be expected).
//     * </p>
//     */
//    @Test
//    public void testCoverTextByRectanglesInput() throws IOException {
//
//        try (   InputStream resource = getClass().getResourceAsStream("input.pdf")  ) {
//            PDDocument doc = Loader.loadPDF(new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf"));
//            //you can load new font if required, by using `ttf` file for that font
//            PDFont pdfFontBold = PDType0Font.load(doc,
//                    new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/Helvetica-Bold.ttf"));
//            PDFont pdfFont = PDType0Font.load(doc,
//                    new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/HelveticaWorld-Regular.ttf"));
//            myStripper stripper = new myStripper();
//
//            stripper.setStartPage(1); // fix it to first page just to test it
//            stripper.setEndPage(1);
//            stripper.getText(doc);
//
//            TextLine line = stripper.lines.get(1); // the line i want to paint on
//
//            float minx = -1;
//            float maxx = -1;
//
//            for (TextPosition pos: line.textPositions)
//            {
//                if (pos == null)
//                    continue;
//
//                if (minx == -1 || pos.getTextMatrix().getTranslateX() < minx) {
//                    minx = pos.getTextMatrix().getTranslateX();
//                }
//                if (maxx == -1 || pos.getTextMatrix().getTranslateX() > maxx) {
//                    maxx = pos.getTextMatrix().getTranslateX();
//                }
//            }
//
//            TextPosition firstPosition = line.textPositions.get(0);
//            TextPosition lastPosition = line.textPositions.get(line.textPositions.size() - 1);
//
////            float x = minx;
////            float y = firstPosition.getTextMatrix().getTranslateY();
////            float w = (maxx - minx) + lastPosition.getWidth();
////            float h = lastPosition.getHeightDir();
//
//            PDPageContentStream contentStream = new PDPageContentStream(doc, doc.getPage(0), PDPageContentStream.AppendMode.APPEND, false, true);
////            contentStream.saveGraphicsState();
//////            contentStream.setNonStrokingColor(Color.RED);
////            contentStream.addRect(x, y, w, h);
//////            contentStream.addRect(y, x, h, w);
//////            contentStream.addRect(x, y + 100, w, h);
////            contentStream.addRect(10, 1600, w, h);
////            contentStream.fill();
////            contentStream.close();
//
////            contentStream.setStrokingColor(Color.magenta);
////            contentStream.addRect(x, y, w + 100, h + 500);
////            contentStream.stroke();
//////            contentStream.fill();
////            contentStream.close();
//
//
//
////                    float x = 100;
////                    float y = 100;
////                    float width = w + 100;
////                    float height = h + 20;
//
//            float x = 20;
//            float y = 3;
//            float width = 300;
//            float height = 20;
//
//                    contentStream.setLineWidth(0.5f);
//
////                    contentStream.setStrokingColor(Color.BLACK);
//                    contentStream.setNonStrokingColor(Color.BLACK);
//
//                    contentStream.moveTo(x, y);
//
//                    // bottom of rectangle, left to right
//                    contentStream.lineTo(x + width, y);
//                    contentStream.curveTo(x + width + 5.9f, y + 0.14f,
//                            x + width + 11.06f, y + 5.16f,
//                            x + width + 10.96f, y + 10);
//
//                    // right of rectangle, bottom to top
//                    contentStream.lineTo(x + width + 10.96f, y + height);
//                    contentStream.curveTo(x + width + 11.06f, y + height - 5.16f + 10,
//                            x + width + 5.9f, y + height + 0.14f + 10,
//                            x + width, y + height + 10);
//
//                    // top of rectangle, right to left
//                    contentStream.lineTo(x, y + height + 10);
//                    contentStream.curveTo(x - 5.9f, y + height + 0.14f + 10,
//                            x - 11.06f, y + height - 5.16f + 10,
//                            x - 10.96f, y + height);
//
//                    // left of rectangle, top to bottom
//                    contentStream.lineTo(x - 10.96f, y + 10);
//                    contentStream.curveTo(x - 11.06f, y + 5.16f,
//                            x - 5.9f, y + 0.14f,
//                            x, y);
//
//                    contentStream.closePath();
//                    contentStream.stroke();
////                    contentStream.close();
//
//
//
//            // draw a filled box with rect x=200, y=500, w=200, h=100
////            contentStream.saveGraphicsState();
////            contentStream.transform(Matrix.getRotateInstance(Math.toRadians(105), 200, 500));
////            contentStream.addRect(0, 0, 200, 100);
////            contentStream.fill();
////            contentStream.restoreGraphicsState();
//
//
////            contentStream.restoreGraphicsState();
////            contentStream.curveTo(0,0,0,0,0,0);
//            contentStream.setFont(pdfFontBold, 20);
//            //for first Line
//            contentStream.beginText();
//            //For adjusting location of text on page you need to adjust this two values
//            contentStream.newLineAtOffset(9,16);
//            contentStream.showText("QUAY VIDEO KHI BÓC HÀNG");
//            contentStream.endText();
//
//            contentStream.setFont(pdfFont, 10);
//            //for second line
//            contentStream.beginText();
//            contentStream.newLineAtOffset(13,5);
//            contentStream.showText("Shop chỉ giải quyết khiếu nại khi có video, Zalo: 038.898.0432");
//            contentStream.endText();
//            contentStream.close();
//
//            File fileout = new File(RESULT_FOLDER, "input-withRectangles.pdf");
//            doc.save(fileout);
//            doc.close();
//        }
//    }
//
//    /**
//     * <a href="https://stackoverflow.com/questions/46080131/text-coordinates-when-stripping-from-pdfbox">
//     * Text coordinates when stripping from PDFBox
//     * </a>
//     * <br/>
//     * <a href="https://download-a.akamaihd.net/files/media_mwb/b7/mwb_I_201711.pdf">
//     * mwb_I_201711.pdf
//     * </a>
//     * <p>
//     * This test applies the OP's code to his example PDF file and indeed, there is an offset!
//     * This is due to the <code>LegacyPDFStreamEngine</code> method <code>showGlyph</code>
//     * which manipulates the text rendering matrix to make the lower left corner of the
//     * crop box the origin. In the current version of this test, that offset is corrected,
//     * see below.
//     * </p>
//     */
//    @Test
//    public void testCoverTextByRectanglesMwbI201711() throws IOException {
//        try (   InputStream resource = getClass().getResourceAsStream("mwb_I_201711.pdf")  ) {
//            PDDocument doc = Loader.loadPDF(new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf"));
//
//            myStripper stripper = new myStripper();
//
//            stripper.setStartPage(1); // fix it to first page just to test it
//            stripper.setEndPage(1);
//            stripper.getText(doc);
//
//            TextLine line = stripper.lines.get(1); // the line i want to paint on
//
//            float minx = -1;
//            float maxx = -1;
//
//            for (TextPosition pos: line.textPositions)
//            {
//                if (pos == null)
//                    continue;
//
//                if (minx == -1 || pos.getTextMatrix().getTranslateX() < minx) {
//                    minx = pos.getTextMatrix().getTranslateX();
//                }
//                if (maxx == -1 || pos.getTextMatrix().getTranslateX() > maxx) {
//                    maxx = pos.getTextMatrix().getTranslateX();
//                }
//            }
//
//            TextPosition firstPosition = line.textPositions.get(0);
//            TextPosition lastPosition = line.textPositions.get(line.textPositions.size() - 1);
//
//            // corrected x and y
//            PDRectangle cropBox = doc.getPage(0).getCropBox();
//
//            float x = minx + cropBox.getLowerLeftX();
//            float y = firstPosition.getTextMatrix().getTranslateY() + cropBox.getLowerLeftY();
//            float w = (maxx - minx) + lastPosition.getWidth();
//            float h = lastPosition.getHeightDir();
//
//            PDPageContentStream contentStream = new PDPageContentStream(doc, doc.getPage(0), PDPageContentStream.AppendMode.APPEND, false, true);
//
//            contentStream.setNonStrokingColor(Color.RED);
//            contentStream.addRect(x, y, w, h);
//            contentStream.fill();
//            contentStream.close();
//
//            File fileout = new File(RESULT_FOLDER, "mwb_I_201711-withRectangles.pdf");
//            doc.save(fileout);
//            doc.close();
//        }
//    }
//}
//
///**
// * @see RectanglesOverText#testCoverTextByRectangles()
// * @author samue
// */
//class TextLine {
//    public List<TextPosition> textPositions = null;
//    public String text = "";
//}
//
///**
// * @see RectanglesOverText#testCoverTextByRectangles()
// * @author samue
// */
//class myStripper extends PDFTextStripper {
//    public myStripper() throws IOException {
//    }
//
//    @Override
//    protected void startPage(PDPage page) throws IOException {
//        startOfLine = true;
//        super.startPage(page);
//    }
//
//    @Override
//    protected void writeLineSeparator() throws IOException {
//        startOfLine = true;
//        super.writeLineSeparator();
//    }
//
//    @Override
//    public String getText(PDDocument doc) throws IOException {
//        lines = new ArrayList<TextLine>();
//        return super.getText(doc);
//    }
//
//    @Override
//    protected void writeWordSeparator() throws IOException {
//        TextLine tmpline = null;
//
//        tmpline = lines.get(lines.size() - 1);
//        tmpline.text += getWordSeparator();
//
//        super.writeWordSeparator();
//    }
//
//    @Override
//    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
//        TextLine tmpline = null;
//
//        if (startOfLine) {
//            tmpline = new TextLine();
//            tmpline.text = text;
//            tmpline.textPositions = textPositions;
//            lines.add(tmpline);
//        } else {
//            tmpline = lines.get(lines.size() - 1);
//            tmpline.text += text;
//            tmpline.textPositions.addAll(textPositions);
//        }
//
//        if (startOfLine) {
//            startOfLine = false;
//        }
//        super.writeString(text, textPositions);
//    }
//
//    boolean startOfLine = true;
//    public ArrayList<TextLine> lines = null;
//}
