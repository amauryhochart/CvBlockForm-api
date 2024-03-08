package org.meedz.cvblockformapi.service;

import com.lowagie.text.DocumentException;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;

@Service
public class CvBlockFormService {

    public File generatePdfFromHtml(SkillFolder skillFolder) {
//        String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
        String outputFolder = "exports_pdf" + File.separator + "thymeleaf.pdf";
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputFolder);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FILE NOT FOUND");
        }

        ITextRenderer renderer = new ITextRenderer();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("first_name", skillFolder.getFirst_name());
        context.setVariable("actual_function", skillFolder.getActual_function());
        context.setVariable("experience_years", skillFolder.getExperience_years());
        context.setVariable("resume", skillFolder.getResume());
        context.setVariable("experiences", skillFolder.getExperiences());
        String parsedThymeleafTemplate = templateEngine.process("templates/Meedz - Dossier de competences", context);

        renderer.setDocumentFromString(parsedThymeleafTemplate);
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
        } catch (DocumentException e) {
            throw new RuntimeException("Document createPDF exception");
        }

        try {
            outputStream.close();
            return new File(outputFolder);
        } catch (IOException e) {
            throw new RuntimeException("IO Error to close");
        }
    }

}
