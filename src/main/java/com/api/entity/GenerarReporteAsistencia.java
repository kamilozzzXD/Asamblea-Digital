package com.api.entity;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Component("/admin/asambleas/asistentes")
public class GenerarReporteAsistencia extends AbstractPdfView {
    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Usuarios> usuariosAsistentes= (List<Usuarios>) model.get("usuarios");
        PdfPTable tablaAsistentes=new PdfPTable(4);

        usuariosAsistentes.forEach(asistentes->{
            tablaAsistentes.addCell(String.valueOf(asistentes.getDocumento()));
            tablaAsistentes.addCell(asistentes.getNombre());
            tablaAsistentes.addCell(asistentes.getEmail());
            tablaAsistentes.addCell(String.valueOf(asistentes.getTelefono()));
        });

        document.add(tablaAsistentes);
    }
}
