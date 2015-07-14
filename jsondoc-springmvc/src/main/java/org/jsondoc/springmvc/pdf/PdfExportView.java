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

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiDoc;
import org.jsondoc.core.pojo.ApiMethodDoc;
import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.springmvc.pdf.utils.ITextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
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

	private JSONDoc jsonDoc;

	private ArrayList<ApiDoc> apiDocs;
	private ArrayList<ApiMethodDoc> apiMethodDocs;

	public File getPdfFile() {
		try {
			File file = new File(FILE_PREFIX + jsonDoc.getVersion() + FILE_EXTENSION);
			FileOutputStream fileout = new FileOutputStream(file);
			Document document = new Document();
			PdfWriter.getInstance(document, fileout);

			document.open();

			//init documentation
			apiDocs = buildApiDocList();
			apiMethodDocs = buildApiMethodDocList(apiDocs);

            int pos = 0;
			for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
				Phrase phrase = new Phrase("Description: " + apiMethodDoc.getDescription());
				document.add(phrase);
				document.add(Chunk.NEWLINE);

				PdfPTable table = new PdfPTable(2);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

				table.setWidths(new int[] { 35, 100 });

                // HEADER CELL START TABLE
				table.addCell(ITextUtils.getHeaderCell("URL"));
				table.addCell(ITextUtils.getHeaderCell(jsonDoc.getBasePath() + "/" + apiMethodDoc.getPath()));
				table.completeRow();


                // FIRST CELL
				table.addCell(ITextUtils.getCell("Http Method", 0));
				table.addCell(ITextUtils.getCell(apiMethodDoc.getVerb().name(), pos));
                pos++;
				table.completeRow();

                // PRODUCES
                if (!apiMethodDoc.getProduces().isEmpty()) {
                    table.addCell(ITextUtils.getCell("Produces", 0));
                    table.addCell(ITextUtils.getCell(buildApiMethodProduces(apiMethodDoc), pos));
                    pos++;
                    table.completeRow();
                }

                // CONSUMES
                if (!apiMethodDoc.getConsumes().isEmpty()) {
                    table.addCell(ITextUtils.getCell("Consumes", 0));
                    table.addCell(ITextUtils.getCell(buildApiMethodConsumes(apiMethodDoc), pos));
                    pos++;
                    table.completeRow();
                }

                // HEADERS
				if (!apiMethodDoc.getHeaders().isEmpty()) {
					//  do logic
				}

                // PATH PARAMS
				if (!apiMethodDoc.getPathparameters().isEmpty()) {
					// do logic
				}

                // QUERY PARAMS
				if (!apiMethodDoc.getQueryparameters().isEmpty()) {
					// do logic
				}

                // BODY OBJECT
                if (null != apiMethodDoc.getBodyobject()) {
                    table.addCell(ITextUtils.getCell("Body object:", 0));
                    table.addCell(ITextUtils.getCell(apiMethodDoc.getBodyobject().getJsondocType().getOneLineText(), pos));
                    pos++;
                    table.completeRow();
                }

                // RESPONSE OBJECT
                table.addCell(ITextUtils.getCell("Json response:", 0));
                table.addCell(ITextUtils.getCell(apiMethodDoc.getResponse().getJsondocType().getOneLineText(), pos));
                pos++;
                table.completeRow();

                // RESPONSE STATUS CODE
                table.addCell(ITextUtils.getCell("Status code:", 0));
                table.addCell(ITextUtils.getCell(apiMethodDoc.getResponsestatuscode(), pos));
                pos++;
                table.completeRow();

                table.setSpacingAfter(10f);
                table.setSpacingBefore(5f);
				document.add(table);
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

	public void setJsonDoc(JSONDoc jsonDoc) {
		this.jsonDoc = jsonDoc;
	}

	private ArrayList<ApiDoc> buildApiDocList() {

        Map<String, Set<ApiDoc>> apis = jsonDoc.getApis();
        
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

    private String buildApiMethodProduces(ApiMethodDoc apiMethodDoc) {

        StringBuilder builder = new StringBuilder();

        for (String value : apiMethodDoc.getProduces()) {
            builder.append(value).append(", ");
        }

        return builder.toString();
    }

    private String buildApiMethodConsumes(ApiMethodDoc apiMethodDoc) {

        StringBuilder builder = new StringBuilder();

        for (String value : apiMethodDoc.getConsumes()) {
            builder.append(value).append(", ");
        }

        return builder.toString();
    }
}
