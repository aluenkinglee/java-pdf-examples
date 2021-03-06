package com.muk.examples.pdf.pdfbox;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.Table;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.utils.FontUtils;

public class MedCertTemplate extends AbstractPdfBoxReport {
  
  public static final String LF_TRANSACTION_ID = "LF_TRANSACTION_ID";
  public static final String LF_MEM_FIRST_NAME = "LF_MEM_FIRST_NAME";
  public static final String LF_MEM_LAST_NAME = "LF_MEM_LAST_NAME";
  public static final String LF_MEM_DOB = "LF_MEM_DOB";
  
  public static final String LF_PAYER_NAME = "LF_PAYER_NAME";
  public static final String LF_PLAN_TYPE = "LF_PLAN_TYPE";
  public static final String LF_PLAN_DESC = "LF_PLAN_DESC";
  public static final String LF_GROUP_DESC = "LF_GROUP_DESC";
  public static final String LF_COVERAGE_STATUS = "LF_COVERAGE_STATUS";
  
  public static final String LF_DATETIME = "LF_DATETIME";
  public static final String LF_ACTIVITY_ID = "LF_ACTIVITY_ID";
  
  public static final String LOGO_RESOURCE_NAME = "/images/wp_logo_blue.png";
  private static final BoxParams PAGE_BOX = new AbstractPdfBoxReport.BoxParams();
  private static final BoxParams LOGO_BOX = new AbstractPdfBoxReport.BoxParams();
  private static final BoxParams TBL_BOX = new AbstractPdfBoxReport.BoxParams();
  private static float TBL_START_PAGE_Y = 0.0f;
  
  private static Color TABLE_HEADER_COLOR = new Color(224, 224, 224);
  private static Color TBL_SECTION_HEADER_COLOR = new Color(224, 224, 224);
  
  private static final String[] FOOTER_TEMPLATE_TEXT = {
      "This information was obtained by Wellpass for the purposes of verifying the applicant's/dependent’s Medicaid coverage and",
      "this document will be submitted to Dummy Carrier as proof of eligibility for Lifeline service."
  };
      
  private static BufferedImage LOGO_IMAGE = null;
  
  static {
    PAGE_BOX.width = PDRectangle.A4.getWidth();
    PAGE_BOX.height = PDRectangle.A4.getHeight();

    LOGO_IMAGE = readBufferedImageUnchecked(LOGO_RESOURCE_NAME);
    if (LOGO_IMAGE != null) {
      LOGO_BOX.topMargin = 20;
      LOGO_BOX.height = 60;
      LOGO_BOX.width = LOGO_IMAGE.getWidth() * (LOGO_BOX.height / LOGO_IMAGE.getHeight());
      // horizontal center align
      //LOGO_BOX.x = (PAGE_BOX.width - LOGO_BOX.width) / 2;
      LOGO_BOX.x = 80;
      LOGO_BOX.y = PAGE_BOX.height - LOGO_BOX.topMargin - LOGO_BOX.height;
      
      /*System.out.printf("iw=%d, ih=%d, bw=%.2f, bh=%.2f, ix=%.2f, iy=%.2f%n", 
          LOGO_IMAGE.getWidth(), LOGO_IMAGE.getHeight(), 
          LOGO_BOX.width, LOGO_BOX.height, LOGO_BOX.x, LOGO_BOX.y);*/
    }
    
    TBL_BOX.topMargin = 20;
    TBL_BOX.x = 80;
    TBL_BOX.width = PAGE_BOX.width - (2 * TBL_BOX.x);
    TBL_BOX.y = PAGE_BOX.height - TBL_BOX.topMargin;
    TBL_START_PAGE_Y = TBL_BOX.y - LOGO_BOX.topMargin - LOGO_BOX.height;
    
    /*System.out.printf("tw=%.2f, th=?, tx=%.2f, ty=%.2f, startY=%.2f%n", 
        TBL_BOX.width, TBL_BOX.x, TBL_BOX.y, TBL_START_PAGE_Y);*/
  }
      

  private Map<String, String> parameters;
  
  public MedCertTemplate(Map<String, String> parameters) {
    this.parameters = parameters;
  }
  
  @Override
  protected PDDocument generatePDDocument() {
    try {
      PDDocument doc = new PDDocument();
      PDPage page = new PDPage();
      doc.addPage(page);

      page.setMediaBox(new PDRectangle(PAGE_BOX.width, PAGE_BOX.height));
      
      if (LOGO_IMAGE != null) {
        PDImageXObject headerImage = imageObject(LOGO_IMAGE, LOGO_RESOURCE_NAME, doc);
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        contentStream.drawImage(headerImage, LOGO_BOX.x, LOGO_BOX.y, LOGO_BOX.width, LOGO_BOX.height);
        contentStream.close();
      }
      
      boolean drawLines = true;
      boolean drawContent = true;

      BaseTable table = new BaseTable(TBL_START_PAGE_Y, TBL_BOX.y, TBL_BOX.bottomMargin, TBL_BOX.width, TBL_BOX.x,
          doc, page, drawLines, drawContent);
      
      createTableHeader(table, "CERTIFICATE OF RECEIPT OF MEDICAID BENEFITS");    
      
      createDetailRow(table, "Lifeline Application Transaction ID", data(LF_TRANSACTION_ID));
      
      createSectionHeader(table, "Member Identifiers");
      createDetailRow(table, "Member’s First Name", data(LF_MEM_FIRST_NAME));
      createDetailRow(table, "Member’s Last Name", data(LF_MEM_LAST_NAME));
      createDetailRow(table, "Member’s DOB", data(LF_MEM_DOB));
      
      createSectionHeader(table, "Insurance Plan Coverage");
      createDetailRow(table, "Insurance Payer Name", data(LF_PAYER_NAME));
      createDetailRow(table, "Insurance Plan Type", data(LF_PLAN_TYPE));
      createDetailRow(table, "Insurance Plan Description", data(LF_PLAN_DESC));
      createDetailRow(table, "Insurance Plan Group Description", data(LF_GROUP_DESC));
      createDetailRow(table, "Insurance Coverage Status", data(LF_COVERAGE_STATUS));
      
      createSectionHeader(table, "X12 Eligibility Transaction");
      createDetailRow(table, "Date-Time of x12 270/271 Transaction", data(LF_DATETIME));
      createDetailRow(table, "X12 Activity ID", data(LF_ACTIVITY_ID));
      
      float tableEndY = table.draw();
      
      //System.out.printf("footerY=%.2f%n", tableEndY);
      
      drawFooter(tableEndY, 
          PAGE_BOX.width, 
          FOOTER_TEMPLATE_TEXT,
          page, doc);
      
      return doc;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private String data(String paramName) {
    return this.parameters.getOrDefault(paramName, "");
  }
  
  private void drawFooter(float tableEndY, float pageWidth, String[] text, PDPage page, PDDocument doc) throws IOException {
    PDPageContentStream stream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);
    
    BoxParams LINE_START = new AbstractPdfBoxReport.BoxParams();
    LINE_START.x = TBL_BOX.x;
    LINE_START.y = tableEndY - 20;
    BoxParams LINE_END = new AbstractPdfBoxReport.BoxParams();
    LINE_END.x = PAGE_BOX.width - LINE_START.x;
    LINE_END.y = tableEndY - 20;
    
    Color lineColor = new Color(224, 224, 224);
    stream.setNonStrokingColor(lineColor);
    stream.setStrokingColor(lineColor);
    stream.setLineWidth(1);
    stream.setLineCapStyle(0);
    //stream.setLineDashPattern(new float[] {}, 0f);
    stream.setLineDashPattern(new float[] {}, 0.0f);
    stream.moveTo(LINE_START.x, LINE_START.y);
    stream.lineTo(LINE_END.x, LINE_END.y);
    stream.stroke();
    stream.closePath();

    stream.setNonStrokingColor(Color.BLACK);
    stream.setFont(PDType1Font.HELVETICA, 8);
    float fontHeight = FontUtils.getHeight(PDType1Font.HELVETICA, 8);
    
    BoxParams FOOT_BOX = new AbstractPdfBoxReport.BoxParams();
    FOOT_BOX.x = LINE_START.x;
    FOOT_BOX.y = LINE_START.y - 5 - fontHeight;
    
    for (String line : text) {
      stream.beginText();
      stream.newLineAtOffset(FOOT_BOX.x, FOOT_BOX.y);
      stream.showText(line);
      stream.endText();
      stream.closePath();
      FOOT_BOX.y -= fontHeight;
    }
    stream.close();
  }
  
  private Row<PDPage> createTableHeader(Table<PDPage> table, String title) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell = row.createCell(100f, title,
        HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(14);
    
    cell.setFillColor(TABLE_HEADER_COLOR);
    return row;
  }

  private Row<PDPage> createSectionHeader(Table<PDPage> table, String title) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell =
        row.createCell(100f, title, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    // cell.setFontSize(8);
    
    cell.setFillColor(TBL_SECTION_HEADER_COLOR);
    return row;
  }

  private Row<PDPage> createDetailRow(Table<PDPage> table, String left, String right) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell =
        row.createCell(50f, left, HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM);
    cell.setFont(PDType1Font.HELVETICA);
    // cell.setFontSize(8);

    Cell<PDPage> cell2 =
        row.createCell(50f, right, HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM);
    cell2.setFont(PDType1Font.HELVETICA);
    // cell2.setFontSize(8);
    return row;
  }

}
