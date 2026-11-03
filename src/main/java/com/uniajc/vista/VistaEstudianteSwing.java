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
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

/**
 * Vista Swing (ventana) para la entidad Estudiante.
 * Muestra la tabla con paginación, búsqueda y botones del CRUD.
 * La vista NO valida ni guarda: todo lo manda al Controlador.
 */
public class VistaEstudianteSwing extends JPanel {

    // Paleta de colores de la guía (también la usa la ventana principal)
    static final Color COLOR_PRIMARIO = new Color(91, 44, 111);   // Morado oscuro
    static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    static final Color COLOR_ACENTO = new Color(231, 76, 60);
    static final Color COLOR_EXITO = new Color(39, 174, 96);
    static final Color COLOR_GRIS = new Color(149, 165, 166);

    private static final String[] COLUMNAS = {"ID", "Nombre", "Apellido", "Email"};

    private final ControladorEstudiante controlador;

    // Componentes de la pantalla
    private JTextField textoBusqueda;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel labelPagina;
    private JButton botonAnterior;
    private JButton botonSiguiente;

    // Paginación: lista que se está mostrando y página actual
    private List<Estudiante> listaActual = new ArrayList<>();
    private int paginaActual = 1;
    private int registrosPorPagina = 5;

    // Configuración: preguntar antes de eliminar
    private boolean confirmarEliminaciones = true;

    /**
     * Constructor: recibe el controlador, arma la pantalla y carga los datos.
     * @param controlador Controlador de estudiantes
     */
    public VistaEstudianteSwing(ControladorEstudiante controlador) {
        this.controlador = controlador;
        initComponents();
        cargarEstudiantes();
    }

    /**
     * Organiza la pantalla en tres zonas: búsqueda, tabla y botones.
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
        textoBusqueda.addActionListener(e -> buscarEstudiantes()); // Enter también busca

        JButton botonBuscar = crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarEstudiantes());

        JButton botonLimpiar = crearBoton("Limpiar", COLOR_GRIS);
        botonLimpiar.addActionListener(e -> cargarEstudiantes());

        panel.add(label);
        panel.add(textoBusqueda);
        panel.add(botonBuscar);
        panel.add(botonLimpiar);
        return panel;
    }

    /**
     * Zona del centro: la tabla de estudiantes.
     */
    private JScrollPane crearPanelTabla() {
        // Modelo de la tabla: las celdas no se pueden editar directamente
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

        // Encabezado azul con letra blanca en negrita
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

        JButton botonRegistrar = crearBoton("Registrar", COLOR_EXITO);
        botonRegistrar.addActionListener(e -> mostrarFormulario(null));

        JButton botonEditar = crearBoton("Editar", COLOR_PRIMARIO);
        botonEditar.addActionListener(e -> editarSeleccionado());

        JButton botonEliminar = crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarSeleccionado());

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);

        // Paginación
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelPaginacion.setBackground(COLOR_SECUNDARIO);

        botonAnterior = crearBoton("Anterior", COLOR_PRIMARIO);
        botonAnterior.addActionListener(e -> {
            paginaActual--;
            mostrarPagina();
        });

        labelPagina = new JLabel("Página 1 de 1");
        labelPagina.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelPagina.setForeground(COLOR_PRIMARIO);

        botonSiguiente = crearBoton("Siguiente", COLOR_PRIMARIO);
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

    /**
     * Crea un botón con el estilo de la guía.
     * Es static para que la ventana principal también lo use.
     */
    static JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setPreferredSize(new Dimension(115, 35));
        return boton;
    }

    // ===================== DATOS Y PAGINACIÓN =====================

    /**
     * Carga todos los estudiantes y vuelve a la página 1.
     */
    public void cargarEstudiantes() {
        textoBusqueda.setText("");
        try {
            listaActual = controlador.listarEstudiantes();
            paginaActual = 1;
            mostrarPagina();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Vuelve a cargar los datos sin cambiar de página
     * (se usa después de registrar, editar o eliminar).
     */
    private void refrescar() {
        textoBusqueda.setText("");
        try {
            listaActual = controlador.listarEstudiantes();
            mostrarPagina();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Filtra por ID, nombre, apellido o email.
     */
    private void buscarEstudiantes() {
        String texto = textoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            cargarEstudiantes();
            return;
        }

        try {
            List<Estudiante> resultado = new ArrayList<>();
            for (Estudiante e : controlador.listarEstudiantes()) {
                boolean coincide = String.valueOf(e.getIdEstudiante()).equals(texto)
                        || contiene(e.getNombre(), texto)
                        || contiene(e.getApellido(), texto)
                        || contiene(e.getEmail(), texto);
                if (coincide) {
                    resultado.add(e);
                }
            }
            listaActual = resultado;
            paginaActual = 1;
            mostrarPagina();

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron estudiantes.",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Revisa si un valor contiene el texto buscado (sin importar mayúsculas).
     */
    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    /**
     * Muestra en la tabla solo los registros de la página actual.
     */
    private void mostrarPagina() {
        modeloTabla.setRowCount(0);

        // Calcular cuántas páginas hay (mínimo 1)
        int totalPaginas = Math.max(1,
                (int) Math.ceil((double) listaActual.size() / registrosPorPagina));

        // Evitar que la página se salga del rango
        if (paginaActual > totalPaginas) {
            paginaActual = totalPaginas;
        }
        if (paginaActual < 1) {
            paginaActual = 1;
        }

        // Desde qué registro hasta cuál se muestra
        int inicio = (paginaActual - 1) * registrosPorPagina;
        int fin = Math.min(inicio + registrosPorPagina, listaActual.size());

        for (int i = inicio; i < fin; i++) {
            Estudiante e = listaActual.get(i);
            modeloTabla.addRow(new Object[]{
                e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail()
            });
        }

        labelPagina.setText("Página " + paginaActual + " de " + totalPaginas);
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
    }

    // ===================== REGISTRAR, EDITAR Y ELIMINAR =====================

    /**
     * Abre el formulario. Si estudiante es null, es para registrar;
     * si trae un estudiante, es para editarlo.
     */
    private void mostrarFormulario(Estudiante estudiante) {
        boolean esEdicion = estudiante != null;
        String titulo = esEdicion ? "Editar Estudiante" : "Registrar Estudiante";

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
        JTextField campoApellido = new JTextField(22);
        JTextField campoEmail = new JTextField(22);
        agregarCampo(panel, gbc, 1, "Nombre:", campoNombre);
        agregarCampo(panel, gbc, 2, "Apellido:", campoApellido);
        agregarCampo(panel, gbc, 3, "Email:", campoEmail);

        // Si es edición, llenar los campos con los datos actuales
        if (esEdicion) {
            campoNombre.setText(estudiante.getNombre());
            campoApellido.setText(estudiante.getApellido());
            campoEmail.setText(estudiante.getEmail());
        }

        // Botones Guardar y Cancelar
        JButton botonGuardar = crearBoton("Guardar", COLOR_EXITO);
        JButton botonCancelar = crearBoton("Cancelar", COLOR_GRIS);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(panelBotones, gbc);

        botonGuardar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            String apellido = campoApellido.getText().trim();
            String email = campoEmail.getText().trim();

            try {
                if (esEdicion) {
                    controlador.actualizarEstudiante(estudiante.getIdEstudiante(), nombre, apellido, email);
                    JOptionPane.showMessageDialog(dialogo, "Estudiante actualizado correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    controlador.registrarEstudiante(nombre, apellido, email);
                    JOptionPane.showMessageDialog(dialogo, "Estudiante registrado correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                dialogo.dispose();
                refrescar();
            } catch (IllegalArgumentException ex) {
                // Errores de validación del Servicio (nombre vacío, email inválido, etc.)
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
     * Busca el estudiante seleccionado en la tabla y abre el formulario de edición.
     */
    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        try {
            Optional<Estudiante> encontrado = controlador.buscarEstudiantePorId(id);
            if (encontrado.isPresent()) {
                mostrarFormulario(encontrado.get());
            } else {
                JOptionPane.showMessageDialog(this, "El estudiante ya no existe.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                refrescar();
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Elimina el estudiante seleccionado, preguntando antes si está configurado así.
     */
    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        String nombre = modeloTabla.getValueAt(fila, 1) + " " + modeloTabla.getValueAt(fila, 2);

        if (confirmarEliminaciones) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar a " + nombre + "?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            controlador.eliminarEstudiante(id);
            JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescar();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, "Error: " + mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ===================== CONFIGURACIÓN (la usa la ventana principal) =====================

    public int getRegistrosPorPagina() {
        return registrosPorPagina;
    }

    public void setRegistrosPorPagina(int registrosPorPagina) {
        this.registrosPorPagina = registrosPorPagina;
        paginaActual = 1;
        mostrarPagina();
    }

    public boolean isConfirmarEliminaciones() {
        return confirmarEliminaciones;
    }

    public void setConfirmarEliminaciones(boolean confirmarEliminaciones) {
        this.confirmarEliminaciones = confirmarEliminaciones;
    }
}