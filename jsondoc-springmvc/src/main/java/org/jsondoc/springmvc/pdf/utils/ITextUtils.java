package org.jsondoc.springmvc.pdf.utils;

import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Created by Alessio on 13/07/15.
 */
public class ITextUtils {

    public static PdfPCell getHeaderCell(String text) {
        PdfPCell cell = new PdfPCell();

        cell.setBorderWidthBottom(3f);
        cell.setBorderColorBottom(Colors.CELL_BORDER_COLOR);
        cell.setPhrase(new Phrase(text));

        return cell;
    }

    public static PdfPCell getCell(String text, int pos) {

        PdfPCell cell = new PdfPCell();

        cell.setBorderWidthRight(1f);
        cell.setBorderColorBottom(Colors.CELL_BORDER_COLOR);

        if (pos != 0) {
            if (pos % 2 == 0) cell.setBackgroundColor(Colors.EVEN_CELL_COLOR);
            else cell.setBackgroundColor(Colors.ODD_CELL_COLOR);
        }

        cell.setPhrase(new Phrase(text));

        return cell;
    }

}
