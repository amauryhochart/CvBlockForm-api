package org.meedz.cvblockformapi.service;

import com.lowagie.text.DocumentException;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class CvBlockFormService {

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Value("${spring.profile}")
    String activeSpringProfile;

    public File generatePdfFromHtml(SkillFolder skillFolder) {
        String outputFolder = "exports_pdf" + File.separator + "thymeleaf.pdf";
        OutputStream outputStream = null;
        try {
            if ("local".equalsIgnoreCase(activeSpringProfile)) {
                outputStream = new FileOutputStream(outputFolder);
            } else {
                File file = ResourceUtils.getFile("/resources/" + outputFolder);
                outputStream = new FileOutputStream(file);
            }
        } catch (IOException e) {
            logger.error("FILE NOT FOUND " + e);
            throw new RuntimeException("FILE NOT FOUND " + e);
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
        context.setVariable("preamble", skillFolder.getPreamble());
        context.setVariable("experiences", skillFolder.getExperiences());
        context.setVariable("skills", skillFolder.getSkills());
        context.setVariable("languages", skillFolder.getLanguages());
        context.setVariable("learnings", skillFolder.getLearnings());
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
            if ("local".equalsIgnoreCase(activeSpringProfile)) {
                return new File(outputFolder);
            } else {
                return ResourceUtils.getFile("/resources/" + outputFolder);
            }
        } catch (IOException e) {
            logger.error("IO Error to close " + e);
            throw new RuntimeException("IO Error to close " + e);
        }
    }

}
