package com.app.appbook;

public class PdfModel {
    String pdfUrl,pdfName,pdfFile;

    public PdfModel(){

    }
    public PdfModel(String pdfUrl, String pdfName,String pdfFile) {
        this.pdfUrl = pdfUrl;
        this.pdfName = pdfName;
        this.pdfFile = pdfFile;
    }



    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }


}
