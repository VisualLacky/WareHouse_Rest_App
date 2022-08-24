package dev.lacky.warehouse.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.InvoiceDocument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class InvoiceServiceTest {

  @Mock
  private InvoiceDao invoiceDao;

  private InvoiceService invoiceService;
  private InvoiceDocument invoiceDocument;
  private List<InvoiceDocument> invoiceDocuments;

  @BeforeEach
  public void before() throws Exception {
    invoiceDao = Mockito.mock(InvoiceDao.class);
    invoiceService = new InvoiceService(invoiceDao);

    invoiceDocument = new InvoiceDocument();
    invoiceDocuments = new ArrayList<>();
    invoiceDocuments.add(invoiceDocument);
    when(invoiceDao.getAllInvoicesDocuments()).thenReturn(invoiceDocuments);
  }

  @Test
  public void getAllInvoiceDocuments_ThrowException_IfNoDocumentsReturned() {
    when(invoiceDao.getAllInvoicesDocuments()).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () ->
        invoiceService.getAllInvoiceDocuments());
  }

  @Test
  public void getAllInvoiceDocuments_ShouldReturn_Documents() {
    List<InvoiceDocument> returnedDocuments = null;

    try {
      returnedDocuments = invoiceService.getAllInvoiceDocuments();
    } catch (Exception e) {
      fail();
    }
    assertEquals(invoiceDocuments, returnedDocuments);
  }

  @Test
  public void getInvoiceDocumentById_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        invoiceService.getInvoiceDocumentById(-15));
  }

  @Test
  public void getInvoiceDocumentById_ThrowException_IfReturnedInvoiceIsNull() {
    int id = 15;
    when(invoiceDao.getInvoiceDocumentById(15)).thenReturn(null);
    assertThrows(NoContentException.class, () ->
        invoiceService.getInvoiceDocumentById(id));
  }

  @Test
  public void getInvoiceDocumentById_ShouldReturn_OneDocument() {
    int id = 15;
    when(invoiceDao.getInvoiceDocumentById(id)).thenReturn(invoiceDocument);
    InvoiceDocument resultDocument = null;
    try {
      resultDocument = invoiceService.getInvoiceDocumentById(id);
    } catch (Exception e) {
      fail();
    }
    assertEquals(resultDocument, invoiceDocument);
  }
}