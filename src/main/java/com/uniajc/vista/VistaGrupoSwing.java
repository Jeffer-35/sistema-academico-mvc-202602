package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Materia;

/**
 * Pantalla de Grupos. Es un JPanel que se agrega al CardLayout
 * de VistaPrincipalSwing. Solo habla con controladores.
 * Usa los controladores de Materia y Docente para llenar los combos
 * y para mostrar nombres en la tabla en vez de solo IDs.
 */
public class VistaGrupoSwing extends JPanel {

    private final ControladorGrupo controlador;
    private final ControladorMateria controladorMateria;
    private final ControladorDocente controladorDocente;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[] {"ID", "Materia", "Docente", "Aula", "Horario"}, 0) {
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

    private List<Grupo> todos = new ArrayList<>();
    private List<Materia> materias = new ArrayList<>();
    private List<Docente> docentes = new ArrayList<>();
    // Para mostrar el nombre a partir del id
    private final Map<Integer, String> nombreMateria = new HashMap<>();
    private final Map<Integer, String> nombreDocente = new HashMap<>();

    private int registrosPorPagina = 10;
    private int paginaActual = 1;
    private boolean confirmarEliminaciones = true;

    public VistaGrupoSwing(ControladorGrupo controlador,
            ControladorMateria controladorMateria,
            ControladorDocente controladorDocente) {
        this.controlador = controlador;
        this.controladorMateria = controladorMateria;
        this.controladorDocente = controladorDocente;

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

        JLabel titulo = new JLabel("Gestión de Grupos");
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

    /** La ventana principal lo llama cada vez que se abre el menú de Grupo. */
    public void cargarGrupos() {
        try {
            todos = controlador.listarGrupos();
            materias = controladorMateria.listarMaterias();
            docentes = controladorDocente.listarDocentes();
        } catch (RuntimeException ex) {
            todos = new ArrayList<>();
            materias = new ArrayList<>();
            docentes = new ArrayList<>();
            mostrarError(ex.getMessage());
        }

        nombreMateria.clear();
        for (Materia m : materias) {
            nombreMateria.put(m.getIdMateria(), m.getNombreMateria());
        }
        nombreDocente.clear();
        for (Docente d : docentes) {
            nombreDocente.put(d.getIdDocente(), d.getNombre());
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

    private String materiaDe(Grupo g) {
        return nombreMateria.getOrDefault(g.getIdMateria(), "ID " + g.getIdMateria());
    }

    private String docenteDe(Grupo g) {
        return nombreDocente.getOrDefault(g.getIdDocente(), "ID " + g.getIdDocente());
    }

    /** Filtra por ID, materia, docente, aula u horario. */
    private List<Grupo> filtrar() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Grupo> resultado = new ArrayList<>();
        for (Grupo g : todos) {
            boolean coincide = filtro.isEmpty()
                    || String.valueOf(g.getIdGrupo()).contains(filtro)
                    || materiaDe(g).toLowerCase().contains(filtro)
                    || docenteDe(g).toLowerCase().contains(filtro)
                    || g.getAula().toLowerCase().contains(filtro)
                    || g.getHorario().toLowerCase().contains(filtro);
            if (coincide) {
                resultado.add(g);
            }
        }
        return resultado;
    }

    private void mostrarPagina() {
        List<Grupo> filtrados = filtrar();
        int totalPaginas = Math.max(1,
                (int) Math.ceil(filtrados.size() / (double) registrosPorPagina));
        if (paginaActual > totalPaginas) {
            paginaActual = totalPaginas;
        }
        if (paginaActual < 1) {
            paginaActual = 1;
        }

        modeloTabla.setRowCount(0);
        int desde = (paginaActual - 1) * registrosPorPagina;
        int hasta = Math.min(desde + registrosPorPagina, filtrados.size());
        for (int i = desde; i < hasta; i++) {
            Grupo g = filtrados.get(i);
            modeloTabla.addRow(new Object[] {
                g.getIdGrupo(), materiaDe(g), docenteDe(g), g.getAula(), g.getHorario()});
        }

        lblPagina.setText("Página " + paginaActual + " de " + totalPaginas
                + " (" + filtrados.size() + " registros)");
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
    }

    // ===================== ACCIONES =====================

    private void registrar() {
        if (!hayMateriasYDocentes()) {
            return;
        }
        Grupo datos = pedirDatos("Registrar grupo", null);
        if (datos == null) {
            return;
        }
        try {
            controlador.registrarGrupo(datos.getIdMateria(), datos.getIdDocente(),
                    datos.getAula(), datos.getHorario());
            exito("Grupo registrado.");
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
        if (!hayMateriasYDocentes()) {
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Grupo actual = buscarEnLista(id);
        if (actual == null) {
            mostrarError("Grupo no encontrado.");
            return;
        }
        Grupo datos = pedirDatos("Editar grupo", actual);
        if (datos == null) {
            return;
        }
        try {
            controlador.actualizarGrupo(id, datos.getIdMateria(), datos.getIdDocente(),
                    datos.getAula(), datos.getHorario());
            exito("Grupo actualizado.");
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
                    "¿Eliminar el grupo con ID " + id + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            controlador.eliminarGrupo(id);
            exito("Grupo eliminado.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // ===================== FORMULARIO =====================

    /**
     * Opción de un combo: guarda el id y muestra un texto.
     * El JComboBox usa toString() para pintar cada opción.
     */
    private static class Opcion {
        private final int id;
        private final String texto;

        Opcion(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    /**
     * Muestra el formulario con combos de materia y docente.
     * Si "actual" no es null, deja seleccionados sus datos (modo edición).
     * Devuelve un Grupo con lo elegido, o null si se cancela.
     */
    private Grupo pedirDatos(String titulo, Grupo actual) {
        JComboBox<Opcion> comboMateria = new JComboBox<>();
        for (Materia m : materias) {
            Opcion op = new Opcion(m.getIdMateria(), m.getNombreMateria());
            comboMateria.addItem(op);
            if (actual != null && actual.getIdMateria() == m.getIdMateria()) {
                comboMateria.setSelectedItem(op);
            }
        }

        JComboBox<Opcion> comboDocente = new JComboBox<>();
        for (Docente d : docentes) {
            Opcion op = new Opcion(d.getIdDocente(), d.getNombre() + " - " + d.getEspecialidad());
            comboDocente.addItem(op);
            if (actual != null && actual.getIdDocente() == d.getIdDocente()) {
                comboDocente.setSelectedItem(op);
            }
        }

        JTextField txtAula = new JTextField(actual == null ? "" : actual.getAula(), 20);
        JTextField txtHorario = new JTextField(actual == null ? "" : actual.getHorario(), 20);

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Materia:"));
        form.add(comboMateria);
        form.add(new JLabel("Docente:"));
        form.add(comboDocente);
        form.add(new JLabel("Aula:"));
        form.add(txtAula);
        form.add(new JLabel("Horario:"));
        form.add(txtHorario);

        int opcion = JOptionPane.showConfirmDialog(this, form, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        Opcion materia = (Opcion) comboMateria.getSelectedItem();
        Opcion docente = (Opcion) comboDocente.getSelectedItem();
        return new Grupo(0, materia.id, docente.id, txtAula.getText(), txtHorario.getText());
    }

    /** No se puede crear un grupo si no hay materias o docentes registrados. */
    private boolean hayMateriasYDocentes() {
        if (materias.isEmpty() || docentes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Primero registre al menos una materia y un docente.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private Grupo buscarEnLista(int id) {
        for (Grupo g : todos) {
            if (g.getIdGrupo() == id) {
                return g;
            }
        }
        return null;
    }

    private void exito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cargarGrupos();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
