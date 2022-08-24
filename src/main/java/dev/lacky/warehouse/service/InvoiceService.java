package dev.lacky.warehouse.service;

import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.InvoiceDocument;
import java.util.List;

public class InvoiceService {

  private InvoiceDao invoiceDao;

  public InvoiceService() {
    invoiceDao = new InvoiceDao();
  }

  public InvoiceService(InvoiceDao invoiceDao) {
    this.invoiceDao = invoiceDao;
  }

  public List<InvoiceDocument> getAllInvoiceDocuments() throws NoContentException {
    List<InvoiceDocument> invoiceDocuments = invoiceDao.getAllInvoicesDocuments();
    if (invoiceDocuments.size() == 0) {
      throw new NoContentException();
    }
    return invoiceDocuments;
  }

  public InvoiceDocument getInvoiceDocumentById(int id)
      throws InvalidInputArgumentException, NoContentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid invoice ID");
    }

    InvoiceDocument invoiceDocument = invoiceDao.getInvoiceDocumentById(id);
    if (invoiceDocument == null) {
      throw new NoContentException();
    }
    return invoiceDocument;
  }
}
