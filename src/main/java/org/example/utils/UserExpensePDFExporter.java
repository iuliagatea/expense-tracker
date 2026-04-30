package org.example.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Expense;
import org.openpdf.text.*;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.*;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserExpensePDFExporter {
    private List<Expense> expenseList;

    public UserExpensePDFExporter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public void export(HttpServletResponse response) throws IOException {
        System.out.println("Exporting PDF with " + expenseList.size() + " expenses");
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        fontTitle.setColor(Color.decode("#6b8e23"));

        Paragraph title = new Paragraph("Expense Tracker Report", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        // Add some space
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {2.5f, 2.0f, 1.5f, 2.0f, 2.0f, 3.0f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        // Add summary section
        document.add(new Paragraph(" "));
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        summaryFont.setSize(14);
        Paragraph summaryTitle = new Paragraph("Summary", summaryFont);
        document.add(summaryTitle);

        double totalExpenses = expenseList.stream()
                .filter(e -> e.getExpenseType() == 0)
                .mapToDouble(Expense::getAmount)
                .sum();
        double totalIncome = expenseList.stream()
                .filter(e -> e.getExpenseType() == 1)
                .mapToDouble(Expense::getAmount)
                .sum();
        double netBalance = totalIncome - totalExpenses;

        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA);
        normalFont.setSize(12);

        document.add(new Paragraph("Total Expenses: $" + String.format("%.2f", totalExpenses), normalFont));
        document.add(new Paragraph("Total Income: $" + String.format("%.2f", totalIncome), normalFont));
        document.add(new Paragraph("Net Balance: $" + String.format("%.2f", netBalance), normalFont));
        document.add(new Paragraph("Total Transactions: " + expenseList.size(), normalFont));

        document.close();
    }

    private void writeTableData(PdfPTable table) {
        System.out.println("Writing table data for " + expenseList.size() + " expenses");
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA);
        normalFont.setSize(10);

        for (Expense expense : expenseList) {
            table.addCell(new Phrase(expense.getDate(), normalFont));
            table.addCell(new Phrase(expense.getCategory().getName(), normalFont));
            table.addCell(new Phrase(expense.getExpenseType() == 0 ? "Expense" : "Income", normalFont));
            table.addCell(new Phrase("$" + String.format("%.2f", expense.getAmount()), normalFont));
            table.addCell(new Phrase(expense.getAccount() != null ? expense.getAccount() : "-", normalFont));
            table.addCell(new Phrase(expense.getNote() != null ? expense.getNote() : "-", normalFont));
        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.decode("#6b8e23"));
        cell.setPadding(8);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);
        font.setSize(11);

        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Category", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Type", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Amount", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Account", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Note", font));
        table.addCell(cell);
    }
}
