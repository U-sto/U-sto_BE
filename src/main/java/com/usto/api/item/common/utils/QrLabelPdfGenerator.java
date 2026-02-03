package com.usto.api.item.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.usto.api.item.asset.domain.model.QrLabelData;
import lombok.RequiredArgsConstructor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.List;

/**
 * QR 라벨 PDF 생성기
 * 라벨 구성: QR 코드 + 물품고유번호
 */
/**
 * QR 라벨 PDF 생성기
 * 라벨 구성: QR 코드 + 물품고유번호
 */
@Component
@RequiredArgsConstructor
public class QrLabelPdfGenerator {

    private static final int QR_SIZE = 150; // QR 코드 크기 (픽셀)
    private static final float LABEL_WIDTH = 200f; // 라벨 너비 (포인트)
    private static final float LABEL_HEIGHT = 250f; // 라벨 높이 (포인트)
    private static final int LABELS_PER_ROW = 2; // 한 행에 라벨 개수
    private static final int LABELS_PER_PAGE = 8; // 한 페이지에 라벨 개수 (2x4)

    public byte[] generate(List<QrLabelData> labelDataList) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            System.out.println("=== PDF 생성 시작 ===");
            System.out.println("생성할 라벨 개수: " + labelDataList.size());

            // 한글 폰트 로드 (NanumGothic.ttf를 resources/fonts/에 넣어야 함)
            PDType0Font font = loadKoreanFont(document);

            int labelIndex = 0;
            PDPage currentPage = null;
            PDPageContentStream contentStream = null;

            for (QrLabelData data : labelDataList) {
                // 페이지당 8개씩 배치
                if (labelIndex % LABELS_PER_PAGE == 0) {
                    // 이전 페이지 스트림 닫기
                    if (contentStream != null) {
                        contentStream.close();
                    }

                    // 새 페이지 생성 (A4)
                    currentPage = new PDPage(PDRectangle.A4);
                    document.addPage(currentPage);
                    contentStream = new PDPageContentStream(document, currentPage);

                    System.out.println("새 페이지 생성 (페이지 " + (document.getNumberOfPages()) + ")");
                }

                // 라벨 위치 계산 (2열 x 4행 배치)
                int row = (labelIndex % LABELS_PER_PAGE) / LABELS_PER_ROW;
                int col = (labelIndex % LABELS_PER_PAGE) % LABELS_PER_ROW;

                float x = 50 + (col * (LABEL_WIDTH + 20)); // 좌측 여백 50 + 라벨간격 20
                float y = PDRectangle.A4.getHeight() - 50 - (row * (LABEL_HEIGHT + 20)); // 상단 여백 50

                // 라벨 그리기
                drawLabel(document, contentStream, data, x, y, font);

                labelIndex++;
            }

            // 마지막 스트림 닫기
            if (contentStream != null) {
                contentStream.close();
            }

            // PDF 저장
            document.save(baos);

            byte[] pdfBytes = baos.toByteArray();
            System.out.println("PDF 생성 완료: " + pdfBytes.length + " bytes");

            return pdfBytes;

        } catch (Exception e) {
            System.err.println("PDF 생성 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("PDF 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 개별 라벨 그리기
     */
    private void drawLabel(PDDocument document, PDPageContentStream contentStream,
                           QrLabelData data, float x, float y, PDType0Font font) throws Exception {

        // 1. 테두리 그리기
        contentStream.setLineWidth(1f);
        contentStream.addRect(x, y - LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);
        contentStream.stroke();

        // 2. QR 코드 생성 및 삽입
        byte[] qrImageBytes = generateQrCode(data.getQrContent());
        BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(qrImageBytes));

        // BufferedImage를 PDImageXObject로 변환
        ByteArrayOutputStream qrBaos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", qrBaos);
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                document,
                qrBaos.toByteArray(),
                "qr"
        );

        // QR 코드 중앙 배치
        float qrX = x + (LABEL_WIDTH - QR_SIZE) / 2;
        float qrY = y - 50 - QR_SIZE;
        contentStream.drawImage(pdImage, qrX, qrY, QR_SIZE, QR_SIZE);

        // 3. 물품고유번호 텍스트 (QR 코드 아래)
        contentStream.beginText();
        contentStream.setFont(font, 10);
        contentStream.newLineAtOffset(x + 10, qrY - 20);
        contentStream.showText("물품고유번호:");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(x + 10, qrY - 35);
        contentStream.showText(data.getItmNo());
        contentStream.endText();
    }

    /**
     * QR 코드 이미지 생성 (ZXing)
     */
    private byte[] generateQrCode(String content) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                QR_SIZE,
                QR_SIZE
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * 한글 폰트 로드
     */
    private PDType0Font loadKoreanFont(PDDocument document) {
        try {
            // 방법 1: resources/fonts/NanumGothic.ttf 사용
            InputStream fontStream = new ClassPathResource("fonts/Pretendard-Light.ttf").getInputStream();
            return PDType0Font.load(document, fontStream);
        } catch (Exception e) {
                throw new RuntimeException("폰트 로드 실패. resources/fonts/에 NanumGothic.ttf 파일을 추가해서 다시 시도해주세요.");
            }
        }
}
