/**
 * 
 */
package gov.cdc.irdu.samwise.server;

import gov.cdc.irdu.samwise.Config;
import gov.cdc.irdu.samwise.server.dao.DocumentDAO;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.docx4j.convert.out.html.AbstractHtmlExporter;
import org.docx4j.convert.out.html.HtmlExporterNG2;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.Conversion;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

/**
 * @author joel
 *
 */
public class UploadFileServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = -5652779896611649208L;

	private static Config CONFIG = Config.getConfig();
	private static Logger LOG = Logger.getLogger(UploadFileServlet.class);
	
	private DocumentDAO documentDAO = new DocumentDAO(); 

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		response.setContentType("text/html");
		
		FileItem uploadItem = getFileItem(request);
		
		if (null == uploadItem) {
			LOG.info("Attempt to upload a file. Failed to find upload item");
			response.getWriter().write("NO-SCRIPT-DATA");
			return;
		}
		
		if (uploadItem.getSize() == 0) {
			LOG.info("Attempt to upload a file. File size is zero bytes");
			response.getWriter().write("NO-FILE-DATA");
			return;
		}
		
		String fileName = uploadItem.getName();
		if (null == fileName || fileName.length() == 0) {
			LOG.info("Attempt to upload a file. File has no name");
			response.getWriter().write("NO-FILE-NAME");
			return;
		}
		
		if (fileName.contains("\\")) {
			int pos = fileName.lastIndexOf('\\');
			fileName = fileName.substring(pos + 1);
		}
		
		LOG.info("Uploading file " + fileName);
				
		File file;
		try {
			if (fileName.toLowerCase().endsWith(".docx")) 
				file = uploadWord(fileName, uploadItem.getInputStream());
			else 
				file = uploadPDF(fileName, uploadItem.getInputStream());
		} catch (Exception e) {
			LOG.error("Error processing uploaded document", e);
			response.getWriter().write("SAVE-FILE-FAILED");
			return;
		}
			
		documentDAO.create(file);
		
		LOG.info("File " + uploadItem.getName() + " successfully uploaded");
		
		response.getWriter().write(fileName);
	}
	
	private void copy(InputStream in, OutputStream out) throws IOException {
		InputStreamReader from = new InputStreamReader(in);
		OutputStreamWriter to = new OutputStreamWriter(out);
		char[] buffer = new char[4096];
		
		try {
			for (int read = from.read(buffer); read > -1; read = from.read(buffer)) {
				to.write(buffer, 0, read);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try { from.close(); } catch (Exception e) {}
			try { to.close(); } catch (Exception e) {}
		}
	}
	
	private void generateImages(Document document, String fileName) throws IOException {
		// First, we strip the .pdf from the end of the filename
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		
		// Then, we create a folder to contain all the images
		File folder = new File(CONFIG.getDocumentFolder(), fileName);
		FileUtils.forceMkdir(folder);
		
		// Now, we create a base filename.
		fileName = folder.getPath() + "/" + fileName;
		
		// Write the images to the file system
        for (int i = 0; i < document.getNumberOfPages(); i++) {
        	BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0f, 1.0f);
        	RenderedImage rendered = image;
        	File file = new File(fileName + "-" + i + ".png");
        	LOG.info("Writing image file " + file.getAbsolutePath());
        	ImageIO.write(rendered, "png", file);            
            image.flush();
        }
	}
	
	@SuppressWarnings("rawtypes")
	private FileItem getFileItem(HttpServletRequest request) {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			List items = upload.parseRequest(request);
			for (Object object: items) {
				if (object instanceof FileItem) {
					FileItem fileItem = (FileItem) object;
					if (!fileItem.isFormField() && "uploadFormElement".equals(fileItem.getFieldName()))
						return fileItem;
				}
			}
		} catch (Throwable e) {
			LOG.error("Failed to upload file", e);
			return null;
		}
		return null;
	}
	
	private Document savePDFDocument(InputStream input, File file) throws Exception {
		// First, we load the document
		Document document = new Document();
		document.setInputStream(input, "remote");
		
		// Then, we write it to a local file
		OutputStream out = new FileOutputStream(file);
		document.saveToOutputStream(out);
		
		return document;
	}
	
	private File uploadPDF(String fileName, InputStream input) throws Exception {
		if (!fileName.toLowerCase().endsWith(".pdf"))
			fileName = fileName + ".pdf";
				
		File file = new File(CONFIG.getDocumentFolder(), fileName);
		Document document = null;
		try {
			document = savePDFDocument(input, file);
			generateImages(document, fileName);
		} catch (Exception e) {
			throw e;
		}
		finally {
			// Clean up resources
			if (null != document)
				document.dispose();
		}
		
		return file;
	}
	
	private File uploadWord(String fileName, InputStream input) throws Exception {
		File file = new File(CONFIG.getDocumentFolder(), fileName);
		OutputStream out = new FileOutputStream(file);
		
		copy(input, out);
		
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
		
		String pdfFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".pdf";
		OutputStream pdf = new FileOutputStream(new File(CONFIG.getDocumentFolder(), pdfFileName));
		wordMLPackage.setFontMapper(new IdentityPlusMapper());
		PdfConversion convert = new Conversion(wordMLPackage);
		convert.output(pdf);
		
		String htmlFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".html";
		OutputStream html = new FileOutputStream(new File(CONFIG.getDocumentFolder(), htmlFileName));
		StreamResult result = new StreamResult(html);

		File imageFolder = new File(CONFIG.getDocumentFolder(), fileName.substring(0, fileName.lastIndexOf('.')));
		AbstractHtmlExporter exporter = new HtmlExporterNG2();
		exporter.html(wordMLPackage, result, imageFolder.getAbsolutePath());
		
		return file;
	}
}
