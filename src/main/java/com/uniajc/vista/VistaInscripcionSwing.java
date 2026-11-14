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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Clases de Swing para la interfaz gráfica
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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

// Importar los controladores y los modelos
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcion;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Inscripcion;
import com.uniajc.modelo.Materia;

/**
 * Vista Swing (panel) para la entidad Inscripcion.
 * Muestra la tabla con paginación, búsqueda y botones del CRUD.
 * En el formulario el estudiante y el grupo se eligen con listas desplegables (JComboBox),
 * así el usuario no tiene que escribir IDs a mano.
 * La vista NO valida reglas de negocio: todo lo manda al Controlador.
 */
public class VistaInscripcionSwing extends JPanel {

    // Se reutilizan los colores de VistaEstudianteSwing
    private static final Color COLOR_PRIMARIO = VistaEstudianteSwing.COLOR_PRIMARIO;
    private static final Color COLOR_SECUNDARIO = VistaEstudianteSwing.COLOR_SECUNDARIO;
    private static final Color COLOR_ACENTO = VistaEstudianteSwing.COLOR_ACENTO;
    private static final Color COLOR_EXITO = VistaEstudianteSwing.COLOR_EXITO;
    private static final Color COLOR_GRIS = VistaEstudianteSwing.COLOR_GRIS;

    private static final String[] COLUMNAS = {"ID", "Estudiante", "Grupo", "Nota final", "Estado"};
    private static final String[] ESTADOS = {
        Inscripcion.ACTIVO, Inscripcion.APROBADO, Inscripcion.REPROBADO, Inscripcion.CANCELADO
    };

    private final ControladorInscripcion controlador;
    private final ControladorEstudiante controladorEstudiante;
    private final ControladorGrupo controladorGrupo;
    private final ControladorMateria controladorMateria;

    // Componentes de la pantalla
    private JTextField textoBusqueda;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel labelPagina;
    private JButton botonAnterior;
    private JButton botonSiguiente;

    // Datos: lista mostrada y "diccionarios" para pasar de id a texto
    private List<Inscripcion> listaActual = new ArrayList<>();
    private final Map<Integer, String> textoEstudiante = new HashMap<>();
    private final Map<Integer, String> textoGrupo = new HashMap<>();

    // Paginación y configuración
    private int paginaActual = 1;
    private int registrosPorPagina = 5;
    private boolean confirmarEliminaciones = true;

    /**
     * Opción de una lista desplegable: guarda el id y el texto que se ve.
     * El JComboBox muestra lo que devuelve toString().
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
     * Constructor: recibe los controladores, arma la pantalla y carga los datos.
     * Usa los controladores de Estudiante, Grupo y Materia para llenar las listas
     * y para mostrar nombres en la tabla en vez de solo IDs.
     */
    public VistaInscripcionSwing(ControladorInscripcion controlador,
            ControladorEstudiante controladorEstudiante,
            ControladorGrupo controladorGrupo,
            ControladorMateria controladorMateria) {
        this.controlador = controlador;
        this.controladorEstudiante = controladorEstudiante;
        this.controladorGrupo = controladorGrupo;
        this.controladorMateria = controladorMateria;
        initComponents();
        cargarInscripciones();
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
     * Zona de arriba: título, campo de búsqueda y botones Buscar y Limpiar.
     */
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(COLOR_SECUNDARIO);

        JLabel titulo = new JLabel("Gestión de Inscripciones   ");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(COLOR_PRIMARIO);

        JLabel label = new JLabel("Buscar:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(COLOR_PRIMARIO);

        textoBusqueda = new JTextField(20);
        textoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textoBusqueda.addActionListener(e -> buscarInscripciones()); // Enter también busca

        JButton botonBuscar = VistaEstudianteSwing.crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarInscripciones());

        JButton botonLimpiar = VistaEstudianteSwing.crearBoton("Limpiar", COLOR_GRIS);
        botonLimpiar.addActionListener(e -> cargarInscripciones());

        panel.add(titulo);
        panel.add(label);
        panel.add(textoBusqueda);
        panel.add(botonBuscar);
        panel.add(botonLimpiar);
        return panel;
    }

    /**
     * Zona del centro: la tabla de inscripciones.
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

        // La columna del grupo es la más larga
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(320);

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

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBotones.setBackground(COLOR_SECUNDARIO);

        JButton botonRegistrar = VistaEstudianteSwing.crearBoton("Inscribir", COLOR_EXITO);
        botonRegistrar.addActionListener(e -> mostrarFormulario(null));

        JButton botonEditar = VistaEstudianteSwing.crearBoton("Editar", COLOR_PRIMARIO);
        botonEditar.addActionListener(e -> editarSeleccionado());

        JButton botonEliminar = VistaEstudianteSwing.crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarSeleccionado());

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);

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
     * Arma los "diccionarios" para mostrar el nombre del estudiante y la
     * descripción del grupo (materia, aula y horario) en vez de solo los IDs.
     */
    private void cargarTextos() {
        textoEstudiante.clear();
        for (Estudiante e : controladorEstudiante.listarEstudiantes()) {
            textoEstudiante.put(e.getIdEstudiante(), e.getNombre() + " " + e.getApellido());
        }

        Map<Integer, String> nombreMateria = new HashMap<>();
        for (Materia m : controladorMateria.listarMaterias()) {
            nombreMateria.put(m.getIdMateria(), m.getNombreMateria());
        }

        textoGrupo.clear();
        for (Grupo g : controladorGrupo.listarGrupos()) {
            String materia = nombreMateria.getOrDefault(g.getIdMateria(), "Materia " + g.getIdMateria());
            textoGrupo.put(g.getIdGrupo(),
                    "G" + g.getIdGrupo() + " · " + materia + " · " + g.getAula() + " · " + g.getHorario());
        }
    }

    /**
     * Carga todas las inscripciones y vuelve a la página 1.
     * Es público porque la ventana principal lo llama al abrir el menú.
     */
    public void cargarInscripciones() {
        textoBusqueda.setText("");
        try {
            cargarTextos();
            listaActual = controlador.listarInscripciones();
            paginaActual = 1;
            mostrarPagina();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Vuelve a cargar los datos sin cambiar de página (después de inscribir, editar o eliminar).
     */
    private void refrescar() {
        textoBusqueda.setText("");
        try {
            cargarTextos();
            listaActual = controlador.listarInscripciones();
            mostrarPagina();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Filtra por ID, nombre del estudiante, grupo (materia, aula, horario) o estado.
     */
    private void buscarInscripciones() {
        String texto = textoBusqueda.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            cargarInscripciones();
            return;
        }

        try {
            cargarTextos();
            List<Inscripcion> resultado = new ArrayList<>();
            for (Inscripcion i : controlador.listarInscripciones()) {
                boolean coincide = String.valueOf(i.getIdInscripcion()).equals(texto)
                        || contiene(nombreEstudiante(i.getIdEstudiante()), texto)
                        || contiene(descripcionGrupo(i.getIdGrupo()), texto)
                        || contiene(i.getEstado(), texto);
                if (coincide) {
                    resultado.add(i);
                }
            }
            listaActual = resultado;
            paginaActual = 1;
            mostrarPagina();

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron inscripciones.",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    private String nombreEstudiante(int id) {
        return textoEstudiante.getOrDefault(id, "Estudiante " + id);
    }

    private String descripcionGrupo(int id) {
        return textoGrupo.getOrDefault(id, "Grupo " + id);
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
            Inscripcion ins = listaActual.get(i);
            modeloTabla.addRow(new Object[]{
                ins.getIdInscripcion(),
                nombreEstudiante(ins.getIdEstudiante()),
                descripcionGrupo(ins.getIdGrupo()),
                ins.getNotaFinal() == null ? "—" : String.format("%.1f", ins.getNotaFinal()),
                ins.getEstado()
            });
        }

        labelPagina.setText("Página " + paginaActual + " de " + totalPaginas);
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
    }

    // ===================== INSCRIBIR, EDITAR Y ELIMINAR =====================

    /**
     * Abre el formulario. Si inscripcion es null, es para inscribir;
     * si trae una inscripción, es para editarla (por ejemplo, poner la nota).
     */
    private void mostrarFormulario(Inscripcion inscripcion) {
        boolean esEdicion = inscripcion != null;
        String titulo = esEdicion ? "Editar Inscripción" : "Inscribir Estudiante";

        // Llenar las listas desplegables
        JComboBox<Opcion> comboEstudiante = new JComboBox<>();
        JComboBox<Opcion> comboGrupo = new JComboBox<>();
        try {
            cargarTextos();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        textoEstudiante.forEach((id, texto) -> comboEstudiante.addItem(new Opcion(id, texto)));
        textoGrupo.forEach((id, texto) -> comboGrupo.addItem(new Opcion(id, texto)));

        if (comboEstudiante.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero registre al menos un estudiante.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (comboGrupo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero registre al menos un grupo.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField campoNota = new JTextField(8);
        JComboBox<String> comboEstado = new JComboBox<>(ESTADOS);

        // Si es edición, seleccionar los datos actuales
        if (esEdicion) {
            seleccionar(comboEstudiante, inscripcion.getIdEstudiante());
            seleccionar(comboGrupo, inscripcion.getIdGrupo());
            if (inscripcion.getNotaFinal() != null) {
                campoNota.setText(String.valueOf(inscripcion.getNotaFinal()));
            }
            comboEstado.setSelectedItem(inscripcion.getEstado());
        }

        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), titulo,
                Dialog.ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTitulo.setForeground(COLOR_PRIMARIO);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(labelTitulo, gbc);

        agregarCampo(panel, gbc, 1, "Estudiante:", comboEstudiante);
        agregarCampo(panel, gbc, 2, "Grupo:", comboGrupo);
        agregarCampo(panel, gbc, 3, "Nota final (0 a 5):", campoNota);
        agregarCampo(panel, gbc, 4, "Estado:", comboEstado);

        JLabel ayuda = new JLabel("La nota puede quedar vacía mientras el curso está ACTIVO.");
        ayuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        ayuda.setForeground(COLOR_GRIS);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(ayuda, gbc);

        JButton botonGuardar = VistaEstudianteSwing.crearBoton("Guardar", COLOR_EXITO);
        JButton botonCancelar = VistaEstudianteSwing.crearBoton("Cancelar", COLOR_GRIS);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonCancelar);

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(panelBotones, gbc);

        botonGuardar.addActionListener(e -> {
            int idEstudiante = ((Opcion) comboEstudiante.getSelectedItem()).id;
            int idGrupo = ((Opcion) comboGrupo.getSelectedItem()).id;
            String estado = (String) comboEstado.getSelectedItem();

            // La vista solo convierte el texto a número; si el número es válido lo decide el servicio
            Double nota;
            String textoNota = campoNota.getText().trim().replace(',', '.');
            if (textoNota.isEmpty()) {
                nota = null;
            } else {
                try {
                    nota = Double.parseDouble(textoNota);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialogo, "La nota debe ser un número, por ejemplo 4.5",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            try {
                if (esEdicion) {
                    controlador.actualizarInscripcion(inscripcion.getIdInscripcion(),
                            idEstudiante, idGrupo, nota, estado);
                    JOptionPane.showMessageDialog(dialogo, "Inscripción actualizada correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    controlador.registrarInscripcion(idEstudiante, idGrupo, nota, estado);
                    JOptionPane.showMessageDialog(dialogo, "Estudiante inscrito correctamente.",
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
     * Selecciona en el combo la opción que tenga ese id.
     */
    private void seleccionar(JComboBox<Opcion> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Agrega una fila al formulario: etiqueta a la izquierda y campo a la derecha.
     */
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
            String texto, JComponent campo) {
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
     * Busca la inscripción seleccionada y abre el formulario de edición.
     */
    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una inscripción de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        try {
            Optional<Inscripcion> encontrada = controlador.buscarInscripcionPorId(id);
            if (encontrada.isPresent()) {
                mostrarFormulario(encontrada.get());
            } else {
                JOptionPane.showMessageDialog(this, "La inscripción ya no existe.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                refrescar();
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Elimina la inscripción seleccionada, preguntando antes si está configurado así.
     */
    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una inscripción de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        String estudiante = String.valueOf(modeloTabla.getValueAt(fila, 1));

        if (confirmarEliminaciones) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar la inscripción de " + estudiante + "?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            controlador.eliminarInscripcion(id);
            JOptionPane.showMessageDialog(this, "Inscripción eliminada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescar();
        } catch (IllegalArgumentException ex) {
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
