package org.jsondoc.springmvc.pdf.utils;

import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Created by Alessio on 13/07/15.
 */
public class ITextUtils {

	public static PdfPCell getHeaderCell(String text) {
		PdfPCell cell = new PdfPCell();

		cell.setBorder(Rectangle.NO_BORDER);
		cell.setBorderWidthBottom(3f);
		cell.setBorderColorBottom(Colors.CELL_BORDER_COLOR);
		cell.setPaddingBottom(7f);
		cell.setPhrase(new Phrase(text));

		return cell;
	}

	public static PdfPCell getCell(String text, int pos) {

		PdfPCell cell = new PdfPCell();

		cell.setBorder(Rectangle.NO_BORDER);
		cell.setBorderWidthRight(1f);
		cell.setBorderColorRight(Colors.CELL_BORDER_COLOR);
		cell.setPaddingBottom(7f);

		if (pos != 0) {
			if (pos % 2 == 0)
				cell.setBackgroundColor(Colors.EVEN_CELL_COLOR);
			else
				cell.setBackgroundColor(Colors.ODD_CELL_COLOR);
		}

		cell.setPhrase(new Phrase(text));

		return cell;
	}

	public static PdfPCell setOddEvenStyle(PdfPCell cell, int pos) {

		if (pos % 2 == 0)
			cell.setBackgroundColor(Colors.EVEN_CELL_COLOR);
		else
			cell.setBackgroundColor(Colors.ODD_CELL_COLOR);

		cell.setPaddingBottom(7f);

		return cell;
	}

}
