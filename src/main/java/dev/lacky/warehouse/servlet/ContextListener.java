package dev.lacky.warehouse.servlet;

import dev.lacky.warehouse.service.IncomeService;
import dev.lacky.warehouse.service.InvoiceService;
import dev.lacky.warehouse.service.MovementService;
import dev.lacky.warehouse.service.ProductService;
import dev.lacky.warehouse.service.SaleService;
import dev.lacky.warehouse.service.StoreService;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

  private ProductService productService;
  private StoreService storeService;
  private SaleService saleService;
  private IncomeService incomeService;
  private MovementService movementService;
  private InvoiceService invoiceService;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    final ServletContext servletContext = sce.getServletContext();

    productService = new ProductService();
    storeService = new StoreService();
    saleService = new SaleService();
    incomeService = new IncomeService();
    movementService = new MovementService();
    invoiceService = new InvoiceService();

    servletContext.setAttribute("productService", productService);
    servletContext.setAttribute("storeService", storeService);
    servletContext.setAttribute("saleService", saleService);
    servletContext.setAttribute("incomeService", incomeService);
    servletContext.setAttribute("movementService", movementService);
    servletContext.setAttribute("invoiceService", invoiceService);
  }
}
