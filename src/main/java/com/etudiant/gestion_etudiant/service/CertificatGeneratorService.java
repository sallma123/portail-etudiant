package com.etudiant.gestion_etudiant.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CertificatGeneratorService {

    public String generatePdf(String nom, String cours, double note, LocalDate date) {
        try {
            // 📄 Nom du fichier
            String nomFichier = nom.replaceAll(" ", "_") + "_" + cours.replaceAll(" ", "_") + ".pdf";
            String chemin = "uploads/" + nomFichier;

            // 📐 Format personnalisé paysage (rectangle type Coursera)
            Rectangle formatPaysage = new Rectangle(842, 595); // équiv. à A4 horizontal
            Document document = new Document(formatPaysage, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(chemin));
            document.open();

            // 🎨 Couleur violette AcadéLink
            BaseColor violet = new BaseColor(118, 75, 162); // #764ba2
            BaseColor gris = new BaseColor(90, 90, 90);

            // 🏷️ Titre
            Font fontTitre = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, violet);
            Paragraph titre = new Paragraph("CERTIFICAT DE RÉUSSITE", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(25);
            document.add(titre);

            // 🪄 Ligne violette
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(violet);
            document.add(separator);

            // 📄 Nom
            Font fontNom = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph nomPara = new Paragraph("\nCe certificat est décerné à", new Font(Font.FontFamily.HELVETICA, 14));
            nomPara.setAlignment(Element.ALIGN_CENTER);
            Paragraph nomValue = new Paragraph(nom, fontNom);
            nomValue.setAlignment(Element.ALIGN_CENTER);

            // 📘 Cours
            Paragraph coursPara = new Paragraph("\nPour avoir terminé avec succès le cours :", new Font(Font.FontFamily.HELVETICA, 14));
            coursPara.setAlignment(Element.ALIGN_CENTER);
            Paragraph coursValue = new Paragraph(cours, new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD));
            coursValue.setAlignment(Element.ALIGN_CENTER);

            // 🧮 Note
            Paragraph notePara = new Paragraph("\nNote finale : " + String.format("%.2f", note) + " / 100", new Font(Font.FontFamily.HELVETICA, 13));
            notePara.setAlignment(Element.ALIGN_CENTER);

            // 📅 Date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            Paragraph datePara = new Paragraph("\nDélivré le : " + date.format(formatter), new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, gris));
            datePara.setAlignment(Element.ALIGN_CENTER);

            // ➕ Ajouter tout au document
            document.add(nomPara);
            document.add(nomValue);
            document.add(coursPara);
            document.add(coursValue);
            document.add(notePara);
            document.add(datePara);

            // ✍ Signature
            Paragraph signature = new Paragraph("\nAcadéLink", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, gris));
            signature.setAlignment(Element.ALIGN_RIGHT);
            signature.setSpacingBefore(40);
            document.add(signature);

            document.close();
            return chemin;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
