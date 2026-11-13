package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Materia;

/**
 * Pantalla de Materias. Es un JPanel que se agrega al CardLayout
 * de VistaPrincipalSwing. Solo habla con el controlador.
 */
public class VistaMateriaSwing extends JPanel {

    private final ControladorMateria controlador;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[] {"ID", "Materia", "Créditos"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextField txtBuscar = new JTextField(22);
    private final JLabel lblPagina = new JLabel("", SwingConstants.CENTER);
    private JButton botonAnterior;
    private JButton botonSiguiente;

    private List<Materia> todas = new ArrayList<>();
    private int registrosPorPagina = 10;
    private int paginaActual = 1;
    private boolean confirmarEliminaciones = true;

    public VistaMateriaSwing(ControladorMateria controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout(10, 10));
        setBackground(VistaEstudianteSwing.COLOR_SECUNDARIO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setSelectionBackground(VistaEstudianteSwing.COLOR_PRIMARIO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        add(crearPanelSuperior(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(VistaEstudianteSwing.COLOR_PRIMARIO, 2));
        add(scroll, BorderLayout.CENTER);

        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    // ===================== CONSTRUCCIÓN DE LA PANTALLA =====================

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(VistaEstudianteSwing.COLOR_SECUNDARIO);

        JLabel titulo = new JLabel("Gestión de Materias");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(VistaEstudianteSwing.COLOR_PRIMARIO);

        JButton botonBuscar = VistaEstudianteSwing.crearBoton("Buscar", VistaEstudianteSwing.COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> {
            paginaActual = 1;
            mostrarPagina();
        });

        JButton botonLimpiar = VistaEstudianteSwing.crearBoton("Limpiar", VistaEstudianteSwing.COLOR_GRIS);
        botonLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            paginaActual = 1;
            mostrarPagina();
        });

        panel.add(titulo);
        panel.add(new JLabel("   Buscar:"));
        panel.add(txtBuscar);
        panel.add(botonBuscar);
        panel.add(botonLimpiar);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(VistaEstudianteSwing.COLOR_SECUNDARIO);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        botones.setBackground(VistaEstudianteSwing.COLOR_SECUNDARIO);

        JButton botonRegistrar = VistaEstudianteSwing.crearBoton("Registrar", VistaEstudianteSwing.COLOR_EXITO);
        botonRegistrar.addActionListener(e -> registrar());

        JButton botonEditar = VistaEstudianteSwing.crearBoton("Editar", VistaEstudianteSwing.COLOR_PRIMARIO);
        botonEditar.addActionListener(e -> editar());

        JButton botonEliminar = VistaEstudianteSwing.crearBoton("Eliminar", VistaEstudianteSwing.COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminar());

        botones.add(botonRegistrar);
        botones.add(botonEditar);
        botones.add(botonEliminar);

        JPanel paginacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        paginacion.setBackground(VistaEstudianteSwing.COLOR_SECUNDARIO);

        botonAnterior = VistaEstudianteSwing.crearBoton("Anterior", VistaEstudianteSwing.COLOR_PRIMARIO);
        botonAnterior.addActionListener(e -> {
            paginaActual--;
            mostrarPagina();
        });

        botonSiguiente = VistaEstudianteSwing.crearBoton("Siguiente", VistaEstudianteSwing.COLOR_PRIMARIO);
        botonSiguiente.addActionListener(e -> {
            paginaActual++;
            mostrarPagina();
        });

        paginacion.add(botonAnterior);
        paginacion.add(lblPagina);
        paginacion.add(botonSiguiente);

        panel.add(botones, BorderLayout.NORTH);
        panel.add(paginacion, BorderLayout.SOUTH);
        return panel;
    }

    // ===================== DATOS Y PAGINACIÓN =====================

    /** La ventana principal lo llama cada vez que se abre el menú de Materia. */
    public void cargarMaterias() {
        try {
            todas = controlador.listarMaterias();
        } catch (RuntimeException ex) {
            todas = new ArrayList<>();
            mostrarError(ex.getMessage());
        }
        paginaActual = 1;
        mostrarPagina();
    }

    public void setRegistrosPorPagina(int registrosPorPagina) {
        this.registrosPorPagina = registrosPorPagina;
        paginaActual = 1;
        mostrarPagina();
    }

    public int getRegistrosPorPagina() {
        return registrosPorPagina;
    }

    public void setConfirmarEliminaciones(boolean confirmarEliminaciones) {
        this.confirmarEliminaciones = confirmarEliminaciones;
    }

    public boolean isConfirmarEliminaciones() {
        return confirmarEliminaciones;
    }

    private List<Materia> filtrar() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Materia> resultado = new ArrayList<>();
        for (Materia m : todas) {
            boolean coincide = filtro.isEmpty()
                    || String.valueOf(m.getIdMateria()).contains(filtro)
                    || m.getNombreMateria().toLowerCase().contains(filtro);
            if (coincide) {
                resultado.add(m);
            }
        }
        return resultado;
    }

    private void mostrarPagina() {
        List<Materia> filtradas = filtrar();
        int totalPaginas = Math.max(1,
                (int) Math.ceil(filtradas.size() / (double) registrosPorPagina));
        if (paginaActual > totalPaginas) {
            paginaActual = totalPaginas;
        }
        if (paginaActual < 1) {
            paginaActual = 1;
        }

        modeloTabla.setRowCount(0);
        int desde = (paginaActual - 1) * registrosPorPagina;
        int hasta = Math.min(desde + registrosPorPagina, filtradas.size());
        for (int i = desde; i < hasta; i++) {
            Materia m = filtradas.get(i);
            modeloTabla.addRow(new Object[] {
                m.getIdMateria(), m.getNombreMateria(), m.getCreditos()});
        }

        lblPagina.setText("Página " + paginaActual + " de " + totalPaginas
                + " (" + filtradas.size() + " registros)");
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
    }

    // ===================== ACCIONES =====================

    private void registrar() {
        String[] datos = pedirDatos("Registrar materia", "", "");
        if (datos == null) {
            return;
        }
        try {
            controlador.registrarMateria(datos[0], leerCreditos(datos[1]));
            exito("Materia registrada.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String[] datos = pedirDatos("Editar materia",
                (String) modeloTabla.getValueAt(fila, 1),
                String.valueOf(modeloTabla.getValueAt(fila, 2)));
        if (datos == null) {
            return;
        }
        try {
            controlador.actualizarMateria(id, datos[0], leerCreditos(datos[1]));
            exito("Materia actualizada.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        if (confirmarEliminaciones) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar la materia con ID " + id + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            controlador.eliminarMateria(id);
            exito("Materia eliminada.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /** Muestra el formulario; devuelve {nombre, creditos} o null si se cancela. */
    private String[] pedirDatos(String titulo, String nombre, String creditos) {
        JTextField txtNombre = new JTextField(nombre, 20);
        JTextField txtCreditos = new JTextField(creditos, 20);

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Créditos:"));
        form.add(txtCreditos);

        int opcion = JOptionPane.showConfirmDialog(this, form, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }
        return new String[] {txtNombre.getText(), txtCreditos.getText()};
    }

    private int leerCreditos(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los créditos deben ser un número entero.");
        }
    }

    private void exito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cargarMaterias();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}