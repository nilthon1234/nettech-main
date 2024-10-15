package com.cibertec.netTech.controllers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

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
    @GetMapping("/reporte")
    public ResponseEntity<StreamingResponseBody> descargarReporte() throws JRException {
        // Cargar el archivo .jasper desde la carpeta de recursos
        InputStream reportStream = getClass().getResourceAsStream("/static/report/Cherry.jasper");
        
        // Verificar que el archivo .jasper exista
        if (reportStream == null) {
            throw new RuntimeException("No se pudo encontrar el archivo de reporte.");
        }

        // Llenar el reporte sin necesidad de pasarle un DataSource si el reporte ya tiene la consulta
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, null);

        // Streaming para la descarga del PDF
        StreamingResponseBody stream = outputStream -> {
            try {
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            } catch (JRException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al exportar el reporte a PDF: " + e.getMessage());
            }
        };

        // Configurar las cabeceras para la descarga del archivo
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_productos.pdf");

        // Devolver el PDF como respuesta
        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

}
