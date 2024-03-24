package com.dle.apche.pdfbox;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDImmutableRectangle;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.apache.pdfbox.util.Matrix;

import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FixPDFScale {

    public static final float MAX_WIDTH = PDRectangle.A6.getWidth();
    public static final float MAX_HEIGHT = PDRectangle.A6.getHeight();

    public static void main(String[] args) throws IOException {
        //fix(new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf"), new File("/Users/dle/Downloads/fixScale.pdf"));

//        try (PDDocument pdf = Loader.loadPDF(new File("/Users/dle/Downloads/202311281608327F5F1D8B74F7251AFC51_1701187713512199465.pdf"))) {
//            PrinterJob job = PrinterJob.getPrinterJob();
//            PDPageTree tree = pdf.getDocumentCatalog().getPages();
//            Iterator<PDPage> iterator = tree.iterator();
//            while (iterator.hasNext()) {
//                PDPage page = iterator.next();
//                if (page.getMediaBox().getWidth() > 238.4 || page.getMediaBox().getHeight() > 336) {
//                    float fWidth = 238.4f / page.getMediaBox().getWidth();
//                    float fHeight = 336f / page.getMediaBox().getHeight();
//                    float factor = 0f;
//                    if (fWidth > fHeight) {
//                        factor = fHeight;
//                    } else {
//                        factor = fWidth;
//                    }
//                    PDPageContentStream contentStream = new PDPageContentStream(pdf, page,
//                            PDPageContentStream.AppendMode.PREPEND, false);
//                    contentStream.transform(Matrix.getScaleInstance(factor, factor));
//                    contentStream.close();
//                    page.setMediaBox(PDRectangle.LETTER);
//                }
//            }
//            Paper paper = new Paper();
//            paper.setSize(612, 792);
//            paper.setImageableArea(0, 0, 612, 792);
//            PageFormat pageFormat = new PageFormat();
//            pageFormat.setPaper(paper);
//            Book book = new Book();
//            book.append(new PDFPrintable(pdf, Scaling.SHRINK_TO_FIT), pageFormat, pdf.getNumberOfPages());
//            job.setPageable(book);
//
////            try {
////                job.print();
////            } catch (PrinterException pe) {
////            }
//        } catch (IOException ioe) {
//        }

//        List<String> lines = Files.readAllLines(Paths.get("/Users/dle/Downloads/ZNSG42MDRZ.txt"), StandardCharsets.UTF_8);
//        StringBuilder data = new StringBuilder();
//        List<String> ids = Arrays.asList("user1974561113549", "user3161276104175", "user1776794487887", "user8203108146667", "user8780742606854", "user3169718858034", "user9530143141849");
//        for (int i = 1; i < lines.size(); i++) {
//            String[] infos = lines.get(i).split("\\|");
//            if (infos.length >= 5) {
//                if (ids.contains(infos[0])) {
//                    data.append(infos[0]).append("|").append(infos[1]).append("|").append(infos[2]).append("|").append(infos[4]).append(System.lineSeparator());
//                }
//            }
//            else {
//                System.out.println(lines.get(i));
//            }
//        }
//        FileUtils.writeStringToFile(new File("/Users/dle/Downloads/accountSuspended.txt"), data.toString(), "UTF-8");

    }

    private static void fix(File input, File output) throws IOException {
        try (PDDocument pdf = Loader.loadPDF(input)) {
            PDPageTree tree = pdf.getPages();

            for (PDPage page : tree) {
                IScaler scaler = buildScaler(page);
                scaler.scale(pdf, page);
            }

            pdf.save(output);
        }
    }

    public static IScaler buildScaler(PDPage page) {
        PDRectangle mediaBox = page.getMediaBox();
        boolean isPortrait = mediaBox.getWidth() < mediaBox.getHeight();

        boolean shouldScale = true;

//        boolean shouldScale = false;

//        if (isPortrait) {
//            shouldScale = mediaBox.getWidth() > MAX_WIDTH || mediaBox.getHeight() > MAX_HEIGHT;
//        } else {
//            shouldScale = mediaBox.getWidth() > MAX_HEIGHT || mediaBox.getHeight() > MAX_WIDTH;
//        }


        if (!shouldScale) {
            return new NoopScaler();
        }

        // Calculate scale factors. Depending on the orientation this requires division of height or width.
        float fWidth = 1;
        float fHeight = 1;
        if (isPortrait) {
            fWidth = MAX_WIDTH / mediaBox.getWidth();
            fHeight = MAX_HEIGHT / mediaBox.getHeight();
        } else {
            fWidth = MAX_HEIGHT / mediaBox.getWidth();
            fHeight = MAX_WIDTH / mediaBox.getHeight();
        }

        float factor = Math.min(fWidth, fHeight);

        // Determine new media box
        PDRectangle targetMediaBox;
        if (isPortrait) {
            targetMediaBox = new PDImmutableRectangle(238.4F, 336F);
        } else {
            targetMediaBox = new PDImmutableRectangle(336F, 238.4F);
        }

        return new Scaler(factor, targetMediaBox);
    }

}
