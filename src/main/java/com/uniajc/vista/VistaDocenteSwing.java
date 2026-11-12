package com.uniajc.vista;

// Clases de AWT para colores, fuentes y organización
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Clases de Swing para la interfaz gráfica
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Importar el controlador y el modelo
import com.uniajc.controlador.ControladorDocente;
import com.uniajc.modelo.Docente;

/**
 * Vista Swing (ventana) para la entidad Docente.
 * Muestra la tabla con paginación, búsqueda y botones del CRUD.
 * La vista NO valida ni guarda: todo lo manda al Controlador.
 * Usa los mismos colores y botones de VistaEstudianteSwing para que todo el sistema se vea igual.
 */
public class VistaDocenteSwing extends JPanel {

    // Se reutilizan los colores de VistaEstudianteSwing
    private static final Color COLOR_PRIMARIO = VistaEstudianteSwing.COLOR_PRIMARIO;
    private static final Color COLOR_SECUNDARIO = VistaEstudianteSwing.COLOR_SECUNDARIO;
    private static final Color COLOR_ACENTO = VistaEstudianteSwing.COLOR_ACENTO;
    private static final Color COLOR_EXITO = VistaEstudianteSwing.COLOR_EXITO;
    private static final Color COLOR_GRIS = VistaEstudianteSwing.COLOR_GRIS;

    private static final String[] COLUMNAS = {"ID", "Nombre", "Especialidad"};

    private final ControladorDocente controlador;

    // Componentes de la pantalla
    private JTextField textoBusqueda;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel labelPagina;
    private JButton botonAnterior;
    private JButton botonSiguiente;

    // Paginación
    private List<Docente> listaActual = new ArrayList<>();
    private int paginaActual = 1;
    private int registrosPorPagina = 5;

    // Configuración: preguntar antes de eliminar
    private boolean confirmarEliminaciones = true;

    /**
     * Constructor: recibe el controlador, arma la pantalla y carga los datos.
     * @param controlador Controlador de docentes
     */
    public VistaDocenteSwing(ControladorDocente controlador) {
        this.controlador = controlador;
        initComponents();
        cargarDocentes();
    }

    /**
     * Organiza la pantalla con BorderLayout: búsqueda arriba, tabla al centro y botones abajo.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_SECUNDARIO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    /**
     * Zona de arriba: campo de búsqueda con botones Buscar y Limpiar.
     */
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(COLOR_SECUNDARIO);

        JLabel label = new JLabel("Buscar:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(COLOR_PRIMARIO);

        textoBusqueda = new JTextField(25);
        textoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textoBusqueda.addActionListener(e -> buscarDocentes()); // Enter también busca

        JButton botonBuscar = VistaEstudianteSwing.crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarDocentes());

        JButton botonLimpiar = VistaEstudianteSwing.crearBoton("Limpiar", COLOR_GRIS);
        botonLimpiar.addActionListener(e -> cargarDocentes());

        panel.add(label);
        panel.add(textoBusqueda);
        panel.add(botonBuscar);
        panel.add(botonLimpiar);
        return panel;
    }

    /**
     * Zona del centro: la tabla de docentes.
     */
    private JScrollPane crearPanelTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(189, 195, 199));
        tabla.setSelectionBackground(COLOR_PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 30));

        // Texto de las celdas centrado
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.setDefaultRenderer(Object.class, centrado);

        // Encabezado con el color principal y letra blanca en negrita
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionado, boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, seleccionado, foco, fila, columna);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(COLOR_PRIMARIO);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    /**
     * Zona de abajo: botones del CRUD a la izquierda y paginación a la derecha.
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_SECUNDARIO);

        // Botones del CRUD
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBotones.setBackground(COLOR_SECUNDARIO);

        JButton botonRegistrar = VistaEstudianteSwing.crearBoton("Registrar", COLOR_EXITO);
        botonRegistrar.addActionListener(e -> mostrarFormulario(null));

        JButton botonEditar = VistaEstudianteSwing.crearBoton("Editar", COLOR_PRIMARIO);
        botonEditar.addActionListener(e -> editarSeleccionado());

        JButton botonEliminar = VistaEstudianteSwing.crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarSeleccionado());

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);

        // Paginación
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelPaginacion.setBackground(COLOR_SECUNDARIO);

        botonAnterior = VistaEstudianteSwing.crearBoton("Anterior", COLOR_PRIMARIO);
        botonAnterior.addActionListener(e -> {
            paginaActual--;
            mostrarPagina();
        });

        labelPagina = new JLabel("Página 1 de 1");
        labelPagina.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelPagina.setForeground(COLOR_PRIMARIO);

        botonSiguiente = VistaEstudianteSwing.crearBoton("Siguiente", COLOR_PRIMARIO);
        botonSiguiente.addActionListener(e -> {
            paginaActual++;
            mostrarPagina();
        });

        panelPaginacion.add(botonAnterior);
        panelPaginacion.add(labelPagina);
        panelPaginacion.add(botonSiguiente);

        panel.add(panelBotones, BorderLayout.WEST);
        panel.add(panelPaginacion, BorderLayout.EAST);
        return panel;
    }

    // ===================== DATOS Y PAGINACIÓN =====================

    /**
     * Carga todos los docentes y vuelve a la página 1.
     */
    public void cargarDocentes() {
        textoBusqueda.setText("");
        try {
            listaActual = controlador.listarDocentes();
            paginaActual = 1;
            mostrarPagina();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Vuelve a cargar los datos sin cambiar de página.
     */
    private void refrescar() {
        textoBusqueda.setText("");
        try {
            listaActual = controlador.listarDocentes();
            mostrarPagina();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Filtra por ID, nombre o especialidad.
     */
    private void buscarDocentes() {
        String texto = textoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            cargarDocentes();
            return;
        }

        try {
            List<Docente> resultado = new ArrayList<>();
            for (Docente d : controlador.listarDocentes()) {
                boolean coincide = String.valueOf(d.getIdDocente()).equals(texto)
                        || contiene(d.getNombre(), texto)
                        || contiene(d.getEspecialidad(), texto);
                if (coincide) {
                    resultado.add(d);
                }
            }
            listaActual = resultado;
            paginaActual = 1;
            mostrarPagina();

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron docentes.",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    /**
     * Muestra en la tabla solo los registros de la página actual.
     */
    private void mostrarPagina() {
        modeloTabla.setRowCount(0);

        int totalPaginas = Math.max(1,
                (int) Math.ceil((double) listaActual.size() / registrosPorPagina));

        if (paginaActual > totalPaginas) {
            paginaActual = totalPaginas;
        }
        if (paginaActual < 1) {
            paginaActual = 1;
        }

        int inicio = (paginaActual - 1) * registrosPorPagina;
        int fin = Math.min(inicio + registrosPorPagina, listaActual.size());

        for (int i = inicio; i < fin; i++) {
            Docente d = listaActual.get(i);
            modeloTabla.addRow(new Object[]{d.getIdDocente(), d.getNombre(), d.getEspecialidad()});
        }

        labelPagina.setText("Página " + paginaActual + " de " + totalPaginas);
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
    }

    // ===================== REGISTRAR, EDITAR Y ELIMINAR =====================

    /**
     * Abre el formulario. Si docente es null, es para registrar;
     * si trae un docente, es para editarlo.
     */
    private void mostrarFormulario(Docente docente) {
        boolean esEdicion = docente != null;
        String titulo = esEdicion ? "Editar Docente" : "Registrar Docente";

        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), titulo,
                Dialog.ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título del formulario
        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTitulo.setForeground(COLOR_PRIMARIO);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(labelTitulo, gbc);

        // Campos del formulario
        JTextField campoNombre = new JTextField(22);
        JTextField campoEspecialidad = new JTextField(22);
        agregarCampo(panel, gbc, 1, "Nombre:", campoNombre);
        agregarCampo(panel, gbc, 2, "Especialidad:", campoEspecialidad);

        // Si es edición, llenar los campos con los datos actuales
        if (esEdicion) {
            campoNombre.setText(docente.getNombre());
            campoEspecialidad.setText(docente.getEspecialidad());
        }

        // Botones Guardar y Cancelar
        JButton botonGuardar = VistaEstudianteSwing.crearBoton("Guardar", COLOR_EXITO);
        JButton botonCancelar = VistaEstudianteSwing.crearBoton("Cancelar", COLOR_GRIS);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonCancelar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(panelBotones, gbc);

        botonGuardar.addActionListener(e -> {
            String nombre = campoNombre.getText();
            String especialidad = campoEspecialidad.getText();

            try {
                if (esEdicion) {
                    controlador.actualizarDocente(docente.getIdDocente(), nombre, especialidad);
                    JOptionPane.showMessageDialog(dialogo, "Docente actualizado correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    controlador.registrarDocente(nombre, especialidad);
                    JOptionPane.showMessageDialog(dialogo, "Docente registrado correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                dialogo.dispose();
                refrescar();
            } catch (IllegalArgumentException ex) {
                // Errores de validación del Servicio
                JOptionPane.showMessageDialog(dialogo, ex.getMessage(),
                        "Validación", JOptionPane.WARNING_MESSAGE);
            } catch (RuntimeException ex) {
                // Errores de la base de datos
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        botonCancelar.addActionListener(e -> dialogo.dispose());

        dialogo.getRootPane().setDefaultButton(botonGuardar); // Enter = Guardar
        dialogo.setContentPane(panel);
        dialogo.pack();
        dialogo.setResizable(false);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    /**
     * Agrega una fila al formulario: etiqueta a la izquierda y campo a la derecha.
     */
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
            String texto, JTextField campo) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(COLOR_PRIMARIO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridwidth = 1;
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    /**
     * Busca el docente seleccionado en la tabla y abre el formulario de edición.
     */
    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un docente de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        try {
            Optional<Docente> encontrado = controlador.buscarDocentePorId(id);
            if (encontrado.isPresent()) {
                mostrarFormulario(encontrado.get());
            } else {
                JOptionPane.showMessageDialog(this, "El docente ya no existe.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                refrescar();
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Elimina el docente seleccionado, preguntando antes si está configurado así.
     */
    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un docente de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        String nombre = String.valueOf(modeloTabla.getValueAt(fila, 1));

        if (confirmarEliminaciones) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar a " + nombre + "?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            controlador.eliminarDocente(id);
            JOptionPane.showMessageDialog(this, "Docente eliminado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescar();
        } catch (IllegalArgumentException ex) {
            // Aquí llega, por ejemplo, el aviso de que el docente tiene grupos asignados
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo eliminar", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, "Error: " + mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ===================== CONFIGURACIÓN (la usa la ventana principal) =====================

    public void setRegistrosPorPagina(int registrosPorPagina) {
        this.registrosPorPagina = registrosPorPagina;
        paginaActual = 1;
        mostrarPagina();
    }

    public void setConfirmarEliminaciones(boolean confirmarEliminaciones) {
        this.confirmarEliminaciones = confirmarEliminaciones;
    }
}