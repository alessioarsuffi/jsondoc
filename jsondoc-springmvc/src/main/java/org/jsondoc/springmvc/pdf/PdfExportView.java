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

import com.google.gson.Gson;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jsondoc.core.pojo.*;
import org.jsondoc.springmvc.pdf.utils.Colors;
import org.jsondoc.springmvc.pdf.utils.ITextUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * La classe <code>PdfExportView</code>:
 *
 * @author Alessio Arsuffi alessio.arsuffi@paybay.it
 * @version 1.00  Jul 13, 2015
 */

@Component
public class PdfExportView {

	public static final String FILE_EXTENSION = ".pdf";

	private JSONDoc jsonDoc;

	private ArrayList<ApiDoc> apiDocs;
	private ArrayList<ApiMethodDoc> apiMethodDocs;

	public File getPdfFile(String filename) {
		try {
			File file = new File(filename + "-v" + jsonDoc.getVersion() + FILE_EXTENSION);
			FileOutputStream fileout = new FileOutputStream(file);
			Document document = new Document();
			PdfWriter.getInstance(document, fileout);

			// Header
			HeaderFooter header = new HeaderFooter(new Phrase("Copyright " + Calendar.getInstance().get(Calendar.YEAR)
					+ " Paybay Networks - All rights reserved"), false);
			header.setBorder(Rectangle.NO_BORDER);
			header.setAlignment(Element.ALIGN_LEFT);
			document.setHeader(header);

			// Footer
			HeaderFooter footer = new HeaderFooter(new Phrase("Page "), true);
			footer.setBorder(Rectangle.NO_BORDER);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.setFooter(footer);

			document.open();

			//init documentation
			apiDocs = buildApiDocList();
			apiMethodDocs = buildApiMethodDocList(apiDocs);

			Phrase baseUrl = new Phrase("Base url: " + jsonDoc.getBasePath());
			document.add(baseUrl);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			int pos = 1;
			for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
				Phrase phrase = new Phrase(/*"Description: " + */apiMethodDoc.getDescription());
				document.add(phrase);
				document.add(Chunk.NEWLINE);

				PdfPTable table = new PdfPTable(2);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
				table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				table.setWidthPercentage(100);

				table.setWidths(new int[] { 50, 200 });

				// HEADER CELL START TABLE
				table.addCell(ITextUtils.getHeaderCell("URL"));
				table.addCell(ITextUtils.getHeaderCell("<baseUrl> " + apiMethodDoc.getPath()));
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
					table.addCell(ITextUtils.getCell("Request headers", 0));

					PdfPTable pathParamsTable = new PdfPTable(3);
					pathParamsTable.setWidths(new int[] { 30, 20, 40 });

					pathParamsTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
					pathParamsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

					for (ApiHeaderDoc apiHeaderDoc : apiMethodDoc.getHeaders()) {
						PdfPCell boldCell = new PdfPCell();
						Font fontbold = FontFactory.getFont("Times-Roman", 12, Font.BOLD);
						boldCell.setPhrase(new Phrase(apiHeaderDoc.getName(), fontbold));
						boldCell.getPhrase().setFont(new Font(Font.BOLD));
						boldCell.setBorder(Rectangle.NO_BORDER);
						pathParamsTable.addCell(boldCell);

						PdfPCell paramCell = new PdfPCell();

						StringBuilder builder = new StringBuilder();

						for (String value : apiHeaderDoc.getAllowedvalues())
							builder.append(value).append(", ");

						paramCell.setPhrase(new Phrase("Allowed values: " + builder.toString()));
						paramCell.setBorder(Rectangle.NO_BORDER);

						pathParamsTable.addCell(paramCell);

						paramCell.setPhrase(new Phrase(apiHeaderDoc.getDescription()));

						pathParamsTable.addCell(paramCell);
						pathParamsTable.completeRow();
					}

					PdfPCell bluBorderCell = new PdfPCell(pathParamsTable);
					bluBorderCell.setBorder(Rectangle.NO_BORDER);
					bluBorderCell.setBorderWidthRight(1f);
					bluBorderCell.setBorderColorRight(Colors.CELL_BORDER_COLOR);

					table.addCell(ITextUtils.setOddEvenStyle(bluBorderCell, pos));
					pos++;
					table.completeRow();
				}

				// PATH PARAMS
				if (!apiMethodDoc.getPathparameters().isEmpty()) {
					table.addCell(ITextUtils.getCell("Path params", 0));

					PdfPTable pathParamsTable = new PdfPTable(3);
					pathParamsTable.setWidths(new int[] { 30, 15, 40 });

					pathParamsTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
					pathParamsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

					for (ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
						PdfPCell boldCell = new PdfPCell();
						Font fontbold = FontFactory.getFont("Times-Roman", 12, Font.BOLD);
						boldCell.setPhrase(new Phrase(apiParamDoc.getName(), fontbold));
						boldCell.getPhrase().setFont(new Font(Font.BOLD));
						boldCell.setBorder(Rectangle.NO_BORDER);
						pathParamsTable.addCell(boldCell);

						PdfPCell paramCell = new PdfPCell();
						paramCell.setPhrase(new Phrase(apiParamDoc.getJsondocType().getOneLineText()));
						paramCell.setBorder(Rectangle.NO_BORDER);

						pathParamsTable.addCell(paramCell);

						paramCell.setPhrase(new Phrase(apiParamDoc.getDescription()));

						pathParamsTable.addCell(paramCell);
						pathParamsTable.completeRow();
					}

					PdfPCell bluBorderCell = new PdfPCell(pathParamsTable);
					bluBorderCell.setBorder(Rectangle.NO_BORDER);
					bluBorderCell.setBorderWidthRight(1f);
					bluBorderCell.setBorderColorRight(Colors.CELL_BORDER_COLOR);

					table.addCell(ITextUtils.setOddEvenStyle(bluBorderCell, pos));
					pos++;
					table.completeRow();
				}

				// QUERY PARAMS
				if (!apiMethodDoc.getQueryparameters().isEmpty()) {
					table.addCell(ITextUtils.getCell("Query params", 0));

					PdfPTable queryParamsTable = new PdfPTable(3);
					queryParamsTable.setWidths(new int[] { 30, 15, 40 });

					queryParamsTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
					queryParamsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

					for (ApiParamDoc apiParamDoc : apiMethodDoc.getQueryparameters()) {
						PdfPCell boldCell = new PdfPCell();
						Font fontbold = FontFactory.getFont("Times-Roman", 12, Font.BOLD);
						boldCell.setPhrase(new Phrase(apiParamDoc.getName(), fontbold));
						boldCell.getPhrase().setFont(new Font(Font.BOLD));
						boldCell.setBorder(Rectangle.NO_BORDER);
						queryParamsTable.addCell(boldCell);

						PdfPCell paramCell = new PdfPCell();
						paramCell.setPhrase(new Phrase(apiParamDoc.getJsondocType().getOneLineText()));
						paramCell.setBorder(Rectangle.NO_BORDER);

						queryParamsTable.addCell(paramCell);

						paramCell.setPhrase(
								new Phrase(apiParamDoc.getDescription() + ", mandatory: " + apiParamDoc.getRequired()));

						queryParamsTable.addCell(paramCell);
						queryParamsTable.completeRow();
					}

					PdfPCell bluBorderCell = new PdfPCell(queryParamsTable);
					bluBorderCell.setBorder(Rectangle.NO_BORDER);
					bluBorderCell.setBorderWidthRight(1f);
					bluBorderCell.setBorderColorRight(Colors.CELL_BORDER_COLOR);

					table.addCell(ITextUtils.setOddEvenStyle(bluBorderCell, pos));
					pos++;
					table.completeRow();
				}

				// BODY OBJECT
				if (null != apiMethodDoc.getBodyobject()) {
					table.addCell(ITextUtils.getCell("Body object:", 0));
					String jsonObject = buildJsonFromTemplate(apiMethodDoc.getBodyobject().getJsondocTemplate());
					table.addCell(ITextUtils.getCell(jsonObject, pos));
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

	private String buildJsonFromTemplate(JSONDocTemplate template) {
		StringBuilder builder = new StringBuilder();
		Gson gson = new Gson();
		Set<Map.Entry<String, Object>> set = template.entrySet();

		builder.append("{ ");
		for (Map.Entry<String, Object> s : set) {
			builder.append(gson.toJson(s.getKey())).append(":");
			builder.append(gson.toJson(s.getValue())).append(", ");
		}
		builder.append(" }");

		return builder.toString();
	}
}
