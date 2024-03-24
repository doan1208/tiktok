package com.dle.google.drive;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDImmutableRectangle;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;

/**
 * This is an example on how to add annotations to pages of a PDF document.
 *
 * @author Paul King
 * @version $Revision: 1.2 $
 */
public class Annotation
{

    //https://stackoverflow.com/questions/73655564/disabling-logging-messages-from-org-apache-fontbox-and-pdfbox
    static {
        java.util.logging.Logger.getLogger(
                "org.apache").setLevel(java.util.logging.Level.SEVERE);
    }
    public static void main(String[] args) throws IOException
    {

//        Overlay overlayer = new Overlay();
//        overlayer.setInputFile("/Users/dle/Downloads/20231128160636D472C1026AA31A19C812_1701187597354004080.pdf");  //the file to be overlayed
//
//        PDDocument result = overlayer.overlay(Map.of(2, "/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf")); //This will add overlays to a documents.
//        result.save("/Users/dle/Downloads/combine.pdf");
//        result.close();
//        overlayer.close();  //close the input files AFTER saving the resulting file

        //Overlay pdfDocument = new Overlay();
        //PDDocument finail = pdfDocument.overlay(PDDocument firstDoc, PDDocument otherDoc);



////        File file2 = new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf");
////        PDDocument overlayDoc = Loader.loadPDF(file2);
//        Overlay overlayObj = new Overlay();
//
////        PDDocument originalDoc = Loader.loadPDF(new File("/Users/dle/Downloads/20231128160636D472C1026AA31A19C812_1701187597354004080.pdf"));
//        PDDocument originalDoc = Loader.loadPDF(new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf"));
//        overlayObj.setOverlayPosition(Overlay.Position.FOREGROUND);
//        overlayObj.setInputPDF(originalDoc);
////        overlayObj.setAllPagesOverlayPDF(overlayDoc);      //alternatives?
//        Map<Integer, String> ovmap = new HashMap<Integer, String>();
////        ovmap.put(2, "/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf");
//        ovmap.put(2, "/Users/dle/Downloads/a.pdf");
//        overlayObj.overlay(ovmap);
//        originalDoc.save("/Users/dle/Downloads/combine.pdf");
//
////        overlayDoc.close();
//        originalDoc.close();


//
//        PDDocument newPdf = Loader.loadPDF(new File("/Users/dle/Downloads/a.pdf"));
//        PDPage firstPage=newPdf.getPage(0);
//        PDFont pdfFont= new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
//        int fontSize = 14;
//        PDPageContentStream contentStream = new PDPageContentStream(newPdf, firstPage, PDPageContentStream.AppendMode.APPEND,true,true);
//        contentStream.setFont(pdfFont, fontSize);
//        contentStream.beginText();
//        contentStream.newLineAtOffset(200,685);
//        contentStream.showText("John");
//        contentStream.endText();
//        contentStream.close(); // don't forget that one!





        //to load PDF where you
//        PDDocument pdDocument = Loader.loadPDF(new File("/Users/dle/Downloads/fixScale.pdf"));
//        PDDocument pdDocument = Loader.loadPDF(new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf"));
//        PDDocument pdDocument = Loader.loadPDF(new File("/Users/dle/Downloads/11-27_23-03-10_Shipping label+Packing list.pdf"));
        PDDocument pdDocument = Loader.loadPDF(new File("/Users/dle/Downloads/01-19_10-19-09_Shipping label+Packing list.pdf"));
        //get the page where you want to write code,for first page you need to use 0
        PDPage firstPage = pdDocument.getPage(0);





//        myStripper stripper = new myStripper();
//
//        stripper.setStartPage(1); // fix it to first page just to test it
//        stripper.setEndPage(1);
//        stripper.getText(pdDocument);
//
//        stripper.writeString(stripper.lines.get(71).text + "-> Doan", stripper.lines.get(71).textPositions);
//        stripper.processPage(firstPage);

        //  Page 1_sp: 61.800964
        //  Page 2_sp: 41.25006
//        float packageIdYIndex = stripper.lines.get(stripper.lines.size() - 1).textPositions.get(0).getTextMatrix().getTranslateY();

        //you can load new font if required, by using `ttf` file for that font
        PDFont pdfFontBold = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/Helvetica-Bold.ttf"));
        PDFont pdfFont = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/HelveticaWorld-Regular.ttf"));
        PDFont pdfFontBungeeShade = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/BungeeShade-Regular.ttf"));
        PDFont pdfFontSairaStencilOne = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/SairaStencilOne-Regular.ttf"));
        PDFont pdfFontBungeeOutline = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/BungeeOutline-Regular.ttf"));


        float fWidth = 238.4f / firstPage.getMediaBox().getWidth();
        float fHeight = 336f / firstPage.getMediaBox().getHeight();
        float factor = 0f;
        if (fWidth > fHeight) {
            factor = fHeight;
        } else {
            factor = fWidth;
        }


        myStripper stripper = new myStripper();

        stripper.setStartPage(1); // fix it to first page just to test it
        stripper.setEndPage(1);
        stripper.getText(pdDocument);


//        int fontSize = 9;
        //PDPageContentStream.AppendMode.APPEND this part is must if you want just add new data in exsitnig one
        PDPageContentStream contentStream = new PDPageContentStream(pdDocument, firstPage,
                PDPageContentStream.AppendMode.PREPEND, true, true);

        float x = 0;
        float y = 0;
        for (TextLine textLine : stripper.lines) {
            if (textLine.text.contains("Chân váy xếp ly dáng chữ A") && textLine.text.contains("Xám, S")) {
//            if (textLine.text.contains("Chân váy xếp ly dáng chữ A") && textLine.text.contains("S, Đen")) {
//                x = textLine.textPositions.get(textLine.text.indexOf("Xám, S") + "Xám, S".length() - 1).getEndX();
//                y = textLine.textPositions.get(textLine.text.indexOf("Xám, S") + "Xám, S".length() - 1).getEndY();
                List<TextPosition> positions = textLine.textPositions;
                if (positions.get(textLine.textPositions.size() - 2).getEndX() + 7 < positions.get(positions.size() - 1).getX()) {
                    x = positions.get(positions.size() - 3).getEndX() + 10;
                    y = positions.get(positions.size() - 3).getEndY();
                } else {
                    x = positions.get(positions.size() - 2).getEndX() + 10;
                    y = positions.get(positions.size() - 2).getEndY();
                }
            }
        }
        contentStream.setFont(pdfFontBold, 8f);
        contentStream.beginText();
        contentStream.newLineAtOffset(x * 0.95f + 7f,y * 0.95f + 20f);
        contentStream.showText("-> L, Xám");
        contentStream.endText();

//        contentStream.moveTo(firstPage.getMediaBox().getUpperRightX(),firstPage.getMediaBox().getLowerLeftY());
//        contentStream.lineTo(firstPage.getMediaBox().getLowerLeftX(), firstPage.getMediaBox().getUpperRightY());
        contentStream.moveTo(firstPage.getMediaBox().getLowerLeftX(), firstPage.getMediaBox().getUpperRightY()); // 0, 420
        contentStream.lineTo(firstPage.getMediaBox().getUpperRightX(), firstPage.getMediaBox().getLowerLeftY()); // 298, 0
        contentStream.stroke();

//        contentStream.saveGraphicsState();
//        contentStream.transform(Matrix.getScaleInstance(0.85f, 0.85f));
//        contentStream.transform(new Matrix(0.95f, 0.0F, 0.0F, 0.95f, 16F, 42F));
        contentStream.transform(new Matrix(0.95f, 0.0F, 0.0F, 0.95f, 7F, 20F));
//        contentStream.close();
//        firstPage.setMediaBox(PDRectangle.A6);
//        contentStream.restoreGraphicsState();

//        // Vẽ hcn có góc bo tròn bao quanh chữ
//        float x = 5;
//        float y = 3;
//        float width = 130;
//        float height = 4;
//        contentStream.setLineWidth(3);
//
//        contentStream.setNonStrokingColor(Color.BLACK);
//
//        contentStream.moveTo(x, y);
//
//        // bottom of rectangle, left to right
//        contentStream.lineTo(x + width, y);
//        contentStream.curveTo(x + width + 5.9f, y + 0.14f,
//                x + width + 11.06f, y + 5.16f,
//                x + width + 10.96f, y + 10);
//
//        // right of rectangle, bottom to top
//        contentStream.lineTo(x + width + 10.96f, y + height);
//        contentStream.curveTo(x + width + 11.06f, y + height - 5.16f + 10,
//                x + width + 5.9f, y + height + 0.14f + 10,
//                x + width, y + height + 10);
//
//        // top of rectangle, right to left
//        contentStream.lineTo(x, y + height + 10);
//        contentStream.curveTo(x - 5.9f, y + height + 0.14f + 10,
//                x - 11.06f, y + height - 5.16f + 10,
//                x - 10.96f, y + height);
//
//        // left of rectangle, top to bottom
//        contentStream.lineTo(x - 10.96f, y + 10);
//        contentStream.curveTo(x - 11.06f, y + 5.16f,
//                x - 5.9f, y + 0.14f,
//                x, y);
//
//        contentStream.closePath();
//        contentStream.stroke();


//        contentStream.close();
//        contentStream = new PDPageContentStream(pdDocument, firstPage,
//                PDPageContentStream.AppendMode.PREPEND, true, true);

//        contentStream.addRect(100, 100, 200, 400);


        // Start-------------------
//        contentStream.setFont(PDType0Font.load(pdDocument, new File("/Users/dle/Downloads/Morissa/Morissa.ttf")), 20);
        contentStream.setFont(pdfFontBold, 12f);
        //for first Line
        contentStream.beginText();
        //For adjusting location of text on page you need to adjust this two values
//        contentStream.newLineAtOffset(9,packageIdYIndex - 25);
        contentStream.newLineAtOffset(-3f,34f);
        contentStream.showText("CHO KIỂM TRA HÀNG / QUAY VIDEO KHI BÓC HÀNG");
        contentStream.endText();

        //for second line
        contentStream.setFont(pdfFont, 15);
        contentStream.beginText();
//        contentStream.newLineAtOffset(13,packageIdYIndex - 35);
        contentStream.newLineAtOffset(4f,14f);
        contentStream.showText("Shop chỉ hỗ trợ đơn hàng khi chưa đánh giá");
        contentStream.endText();

        contentStream.setFont(pdfFontBold, 14.5f);
        contentStream.beginText();
        contentStream.newLineAtOffset(-3f,-9f);
        contentStream.showText("ZALO GIẢI QUYẾT KHIẾU NẠI: 038.898.0432");
        contentStream.endText();



        // Vẽ hcn
        contentStream.addRect(-5f, -14f, 308, 65);
        contentStream.setLineWidth(2);
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.stroke();
        //content.setNonStrokingColor(color);
        //content.fill();


        //https://stackoverflow.com/questions/59553242/rotate-text-using-pdfbox
//        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(90), 0, 0);
//        matrix.translate(0, -firstPage.getMediaBox().getWidth());
//
//        contentStream.beginText();
//        contentStream.setTextMatrix(matrix);
//        int xPos = 295;
//        int yPos = 90;
//        int fontSize = 30;
//        float titleWidth = pdfFontBungeeShade.getStringWidth("BY K.E.M STUDIO") / 1000;
//        contentStream.newLineAtOffset(yPos - titleWidth / 2 - fontSize, firstPage.getMediaBox().getWidth() - xPos - titleWidth / 2 - fontSize);
//
//        contentStream.setFont(pdfFontBungeeShade, fontSize);
//        contentStream.showText("BY K.E.M STUDIO");
//        contentStream.endText();

        // End ------------------


        //for  third line
//        contentStream.beginText();
//        contentStream.newLineAtOffset(600,685);
//        contentStream.showText("this is line third");
//        contentStream.endText();


        //and so on.

        //at last you need to close the document to save data
        contentStream.close();



//        myStripper stripper = new myStripper();
//
//        stripper.setStartPage(0); // fix it to first page just to test it
//        stripper.setEndPage(1);
//        stripper.getText(pdDocument);

//        PDPageContentStream contentStream1 = new PDPageContentStream(pdDocument, pdDocument.getPage(0),
//                PDPageContentStream.AppendMode.PREPEND, true, true);
//
//        contentStream1.setFont(pdfFontBold, 8f);
//        contentStream1.beginText();
//        contentStream1.newLineAtOffset(stripper.lines.get(73).textPositions.get(46).getEndX(),stripper.lines.get(73).textPositions.get(46).getEndY());
//        contentStream1.showText(" -> L, Xám");
//        contentStream1.endText();
//        contentStream1.close();



        //this is for saving your PDF you can save with new name
        //or you can replace existing one by giving same name
        pdDocument.save("/Users/dle/Downloads/temp17.pdf");
//        pdDocument.save("/Users/dle/Downloads/temp4.pdf");

//        update();


//        // Load the PDF document created by SimpleForm.java
//        PDDocument document = Loader.loadPDF(new File("/Users/dle/Downloads/20231128160636D472C1026AA31A19C812_1701187597354004080.pdf"));
//        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
//
//        // Get the field and the widget associated to it.
//        // Note: there might be multiple widgets
//        PDField field = acroForm.getField("SampleField");
//        PDAnnotationWidget widget = field.getWidgets().get(0);
//
//        // Get the width of the fields box
//        float widthOfField = widget.getRectangle().getWidth();
//
//        // Get the font and the font size setting
//        // This is currently a little awkward and needs improvement to have a better API
//        // for that. In many cases the string will be built like that:
//        //    /Helv 12 Tf 0 g
//        // We could use PDFStreamParser to do the parsing. For the sample we split the
//        // string.
//        String defaultAppearance = ((PDTextField) field).getDefaultAppearance();
//        String[] parts = defaultAppearance.split(" ");
//
//        // Get the font name
//        COSName fontName = COSName.getPDFName(parts[0].substring(1));
//        float fontSize = Float.parseFloat(parts[1]);
//
//        // Get the font resource.
//        // First look up the font from the widgets appearance stream.
//        // This will be the case if there is already a value.
//        // If the value hasn't been set yet the font resource needs to be looked up from
//        // the AcroForm default resources
//
//        PDFont font = null;
//        PDResources resources = null;
//
//        resources = widget.getNormalAppearanceStream().getResources();
//        if (resources != null)
//        {
//            font = resources.getFont(fontName);
//        }
//        if (font == null)
//        {
//            font = acroForm.getDefaultResources().getFont(fontName);
//        }
//
//        String willFit = "short string";
//        String willNotFit = "this is a very long string which will not fit the width of the widget";
//
//        // calculate the string width at a certain font size
//        float willFitWidth = font.getStringWidth(willFit) * fontSize / 1000;
//        float willNotFitWidth = font.getStringWidth(willNotFit) * fontSize / 1000;
//
//        assert willFitWidth < widthOfField;
//        assert willNotFitWidth > widthOfField;
//
//        document.close();
//        update();
    }

    private static void update() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("fieldname", "value to update");
        File template = new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf");
        PDDocument document = Loader.loadPDF(template);
//        List<PDField> fields = document.getDocumentCatalog().getAcroForm().getFields();
//        for (PDField field : fields) {
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                if (entry.getKey().equals(field.getFullyQualifiedName())) {
//                    field.setValue(entry.getValue());
//                    field.setReadOnly(true);
//                }
//            }
//        }

        myStripper stripper = new myStripper();

        stripper.setStartPage(0); // fix it to first page just to test it
        stripper.setEndPage(1);
        stripper.getText(document);

        PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0),
                PDPageContentStream.AppendMode.PREPEND, true, true);
        PDFont pdfFontBold = PDType0Font.load(document,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/Helvetica-Bold.ttf"));
        contentStream.setFont(pdfFontBold, 7f);
        contentStream.beginText();
        contentStream.newLineAtOffset(stripper.lines.get(70).textPositions.get(46).getEndX(),stripper.lines.get(70).textPositions.get(46).getEndY());
        contentStream.showText(" -> Xám, Đen");
        contentStream.endText();
        contentStream.close();
        File out = new File("/Users/dle/Downloads/temp19.pdf");
        document.save(out);
        document.close();
    }
}

class TextLine {
    public List<TextPosition> textPositions = null;
    public String text = "";
}

class myStripper extends PDFTextStripper {
    public myStripper() throws IOException {
    }

    @Override
    protected void startPage(PDPage page) throws IOException {
        startOfLine = true;
        super.startPage(page);
    }

    @Override
    protected void writeLineSeparator() throws IOException {
        startOfLine = true;
        super.writeLineSeparator();
    }

    @Override
    public String getText(PDDocument doc) throws IOException {
        lines = new ArrayList<TextLine>();
        return super.getText(doc);
    }

    @Override
    protected void writeWordSeparator() throws IOException {
        TextLine tmpline = null;

        tmpline = lines.get(lines.size() - 1);
        tmpline.text += getWordSeparator();

        super.writeWordSeparator();
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        TextLine tmpline = null;

        if (startOfLine) {
            tmpline = new TextLine();
            tmpline.text = text;
            tmpline.textPositions = textPositions;
            lines.add(tmpline);
        } else {
            tmpline = lines.get(lines.size() - 1);
            tmpline.text += text;
            tmpline.textPositions.addAll(textPositions);
        }

        if (startOfLine) {
            startOfLine = false;
        }
        super.writeString(text, textPositions);
    }

    boolean startOfLine = true;
    public ArrayList<TextLine> lines = null;
}
