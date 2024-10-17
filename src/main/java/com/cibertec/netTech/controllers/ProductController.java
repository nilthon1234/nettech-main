package com.cibertec.netTech.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.cibertec.netTech.models.Product;
import com.cibertec.netTech.services.CategoryService;
import com.cibertec.netTech.services.ProductService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.sql.DataSource;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private DataSource dataSource;

    @GetMapping("/edit/{productId}")
    public String showProductDetails(@PathVariable Long productId, Model model) {
        Product product = productService.findById(productId).get();
        model.addAttribute("product", product);
        model.addAttribute("category", categoryService.findAll());
        return "product_detail";
    }

    @PostMapping("/guardar")
    public String guardar_producto(Product producto, Model model) {
        System.out.println(producto.toString());
        productService.save(producto);
        return "redirect:/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String delere_producto(@PathVariable long id) {
        productService.deleteById(id);
        return "redirect:/dashboard";
    }

    // Descargar reporte en PDF
    //reportes
    @GetMapping("/reporteropa")
    public void generarPDFCliente(HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline;");
        response.setContentType("application/pdf");
        try {
            String ru = resourceLoader.getResource("classpath:reporte/reporte_ropas.jasper").getURI().getPath();
            JasperPrint jasperPrint = JasperFillManager.fillReport(ru,null, dataSource.getConnection());
            OutputStream outStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
