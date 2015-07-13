/*
* @(#)PdfExportView        1.00	13/Jul/2015
*
* Copyright (c) 2007-2015 Paybay Networks srl,
* XX Settembre Road, Rome, Italy.
* All rights reserved.
*
* This software is the confidential and proprietary information of Paybay
* Networks srl, Inc. ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only in
* accordance with the terms of the license agreement you entered into
* with Paybay Networks.
*/

package org.jsondoc.springmvc.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jsondoc.core.pojo.ApiDoc;
import org.jsondoc.core.pojo.ApiMethodDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * La classe <code>PdfExportView</code>:
 *
 * @author Alessio Arsuffi alessio.arsuffi@paybay.it
 * @version 1.00  Jul 13, 2015
 */

@Component
public class PdfExportView {

	public static final String FILE_PREFIX = "api-v";
	public static final String FILE_EXTENSION = ".pdf";

	private Map<String, Set<ApiDoc>> apis;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String version;

	private ArrayList<ApiDoc> apiDocs;
	private ArrayList<ApiMethodDoc> apiMethodDocs;

	public File getPdfFile() {
		try {
			File file = new File("itext-test.pdf");
			FileOutputStream fileout = new FileOutputStream(file);
			Document document = new Document();
			PdfWriter.getInstance(document, fileout);

			document.open();

			//init documentation
			apiDocs = buildApiDocList();
			apiMethodDocs = buildApiMethodDocList(apiDocs);

			for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
				Paragraph paragraph = new Paragraph("Description: " + apiMethodDoc.getDescription());
				document.add(paragraph);
				document.add(Chunk.NEWLINE);

				PdfPTable table = new PdfPTable(2);
				table.setWidths(new int[] { 35, 100 });

				table.addCell("URL");
				table.addCell(apiMethodDoc.getPath());
				table.completeRow();

				table.addCell("Http Method");
				table.addCell(apiMethodDoc.getVerb().name());
				table.completeRow();

				if (!apiMethodDoc.getHeaders().isEmpty()) {
					//  do logic
				}

				if (!apiMethodDoc.getPathparameters().isEmpty()) {
					// do logic
				}

				if (!apiMethodDoc.getQueryparameters().isEmpty()) {
					// do logic
				}

				document.add(table);
				document.add(Chunk.NEWLINE);
			}

			document.close();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setList(Map<String, Set<ApiDoc>> apis) {
		this.apis = apis;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private ArrayList<ApiDoc> buildApiDocList() {

		ArrayList<ApiDoc> result = new ArrayList<ApiDoc>();
		for (String s : apis.keySet()) {
			Set<ApiDoc> apiDocSet = apis.get(s);
			for (ApiDoc apiDoc : apiDocSet) {
				result.add(apiDoc);
			}
		}
		return result;
	}

	private ArrayList<ApiMethodDoc> buildApiMethodDocList(ArrayList<ApiDoc> apiDocs) {

		ArrayList<ApiMethodDoc> result = new ArrayList<ApiMethodDoc>();
		for (ApiDoc apiDoc : apiDocs) {
			for (ApiMethodDoc apiMethod : apiDoc.getMethods()) {
				result.add(apiMethod);
			}
		}
		return result;
	}
}
