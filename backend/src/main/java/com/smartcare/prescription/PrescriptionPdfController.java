package com.smartcare.prescription;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * GET /api/patients/me/prescriptions/{id}/pdf
 * Genere l'ordonnance en PDF. Seul le patient proprietaire peut la telecharger.
 * Utilise JdbcTemplate (independant des entites JPA de Dev 2).
 */
@RestController
@RequestMapping("/api/patients/me/prescriptions")
public class PrescriptionPdfController {

    private final JdbcTemplate jdbc;

    public PrescriptionPdfController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id, Authentication auth) throws Exception {
        // auth.getName() = email du patient connecte
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT p.id, p.date_emission, p.medicaments, p.posologie, p.remarques,
                       pa.nom AS p_nom, pa.prenom AS p_prenom, pa.cin,
                       m.nom AS m_nom, m.prenom AS m_prenom, s.nom AS specialite
                FROM prescription p
                JOIN patient pa   ON pa.id = p.patient_id
                JOIN app_user u   ON u.id = pa.user_id
                JOIN medecin m    ON m.id = p.medecin_id
                JOIN specialite s ON s.id = m.specialite_id
                WHERE p.id = ? AND u.email = ?
                """, id, auth.getName());

        if (rows.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Map<String, Object> r = rows.get(0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        Paragraph title = new Paragraph("Smart Care - Ordonnance", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);

        doc.add(new Paragraph("Date : " + r.get("date_emission"), normalFont));
        doc.add(new Paragraph("Medecin : Dr " + r.get("m_prenom") + " " + r.get("m_nom")
                + " (" + r.get("specialite") + ")", normalFont));
        doc.add(new Paragraph("Patient : " + r.get("p_prenom") + " " + r.get("p_nom")
                + "   CIN : " + nullToDash(r.get("cin")), normalFont));

        addSection(doc, "Medicaments", r.get("medicaments"), boldFont, normalFont);
        addSection(doc, "Posologie", r.get("posologie"), boldFont, normalFont);
        addSection(doc, "Remarques", r.get("remarques"), boldFont, normalFont);

        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ordonnance-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    private void addSection(Document doc, String label, Object value, Font boldFont, Font normalFont) throws Exception {
        Paragraph head = new Paragraph(label, boldFont);
        head.setSpacingBefore(15);
        doc.add(head);
        doc.add(new Paragraph(nullToDash(value), normalFont));
    }

    private String nullToDash(Object o) {
        return o == null ? "-" : o.toString();
    }
}
