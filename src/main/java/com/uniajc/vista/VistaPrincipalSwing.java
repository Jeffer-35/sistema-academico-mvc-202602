package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.db.DatabaseConexion;

/**
 * Ventana principal del sistema.
 * Tiene el menú de arriba y un CardLayout que cambia entre pantallas:
 * Inicio, Estudiantes, Docentes y Configuración.
 *
 * La ventana implementa ActionListener: es el "oyente" de todas las opciones del menú,
 * y en actionPerformed se revisa con getSource() cuál opción se presionó.
 */
public class VistaPrincipalSwing extends JFrame implements ActionListener {

    // Colores (se reutilizan los de VistaEstudianteSwing)
    private static final Color COLOR_PRIMARIO = VistaEstudianteSwing.COLOR_PRIMARIO;
    private static final Color COLOR_SECUNDARIO = VistaEstudianteSwing.COLOR_SECUNDARIO;
    private static final Color COLOR_EXITO = VistaEstudianteSwing.COLOR_EXITO;
    private static final Color COLOR_GRIS = VistaEstudianteSwing.COLOR_GRIS;
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(127, 140, 141);

    // Nombres de cada pantalla dentro del CardLayout
    private static final String PANEL_INICIO = "inicio";
    private static final String PANEL_ESTUDIANTES = "estudiantes";
    private static final String PANEL_DOCENTES = "docentes";
    private static final String PANEL_CONFIGURACION = "configuracion";

    // CardLayout: muestra una pantalla a la vez, como un mazo de cartas
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelContenedor = new JPanel(cardLayout);

    // Pantallas de cada entidad
    private final VistaEstudianteSwing vistaEstudiante;
    private final VistaDocenteSwing vistaDocente;

    // Opciones del menú: son atributos para poder compararlas en actionPerformed
    private JMenuItem itemInicio;
    private JMenuItem itemConfiguracion;
    private JMenuItem itemSalir;
    private JMenuItem itemEstudiantes;
    private JMenuItem itemDocentes;
    private JMenuItem itemMaterias;
    private JMenuItem itemGrupos;
    private JMenuItem itemInscripciones;

    // Opciones de la pantalla de configuración
    private JComboBox<Integer> comboRegistros;
    private JComboBox<String> comboConfirmar;

    /**
     * Constructor: arma la ventana con el menú y las pantallas.
     * @param controladorEstudiante Controlador de estudiantes
     * @param controladorDocente Controlador de docentes
     */
    public VistaPrincipalSwing(ControladorEstudiante controladorEstudiante,
            ControladorDocente controladorDocente) {
        super("Sistema de Gestión Académica - UNIAJC");

        vistaEstudiante = new VistaEstudianteSwing(controladorEstudiante);
        vistaDocente = new VistaDocenteSwing(controladorDocente);

        // Al cerrar con la X, se pregunta y se cierra la conexión
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null); // Centrar en la pantalla

        setJMenuBar(crearMenu());

        // Agregar las pantallas (cartas) al CardLayout
        panelContenedor.add(crearPanelInicio(), PANEL_INICIO);
        panelContenedor.add(vistaEstudiante, PANEL_ESTUDIANTES);
        panelContenedor.add(vistaDocente, PANEL_DOCENTES);
        panelContenedor.add(crearPanelConfiguracion(), PANEL_CONFIGURACION);
        add(panelContenedor, BorderLayout.CENTER);

        cardLayout.show(panelContenedor, PANEL_INICIO);
    }

    // ===================== MENÚ =====================

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();
        barra.setBackground(COLOR_PRIMARIO);
        barra.setOpaque(true);
        barra.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        // Menú Archivo
        JMenu menuArchivo = crearMenuBarra("Archivo");
        itemInicio = crearItem("Inicio");
        itemConfiguracion = crearItem("Configuración");
        itemSalir = crearItem("Salir");
        menuArchivo.add(itemInicio);
        menuArchivo.add(itemConfiguracion);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        // Menú Estudiante
        JMenu menuEstudiante = crearMenuBarra("Estudiante");
        itemEstudiantes = crearItem("Gestionar estudiantes");
        menuEstudiante.add(itemEstudiantes);

        // Menú Docente
        JMenu menuDocente = crearMenuBarra("Docente");
        itemDocentes = crearItem("Gestionar docentes");
        menuDocente.add(itemDocentes);

        // Módulos que todavía no están implementados
        JMenu menuMateria = crearMenuBarra("Materia");
        itemMaterias = crearItem("Gestionar materias");
        menuMateria.add(itemMaterias);

        JMenu menuGrupo = crearMenuBarra("Grupo");
        itemGrupos = crearItem("Gestionar grupos");
        menuGrupo.add(itemGrupos);

        JMenu menuInscripcion = crearMenuBarra("Inscripción");
        itemInscripciones = crearItem("Gestionar inscripciones");
        menuInscripcion.add(itemInscripciones);

        barra.add(menuArchivo);
        barra.add(menuEstudiante);
        barra.add(menuDocente);
        barra.add(menuMateria);
        barra.add(menuGrupo);
        barra.add(menuInscripcion);

        return barra;
    }

    private JMenu crearMenuBarra(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return menu;
    }

    /**
     * Crea una opción del menú y registra la ventana (this) como su oyente.
     */
    private JMenuItem crearItem(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(this);
        return item;
    }

    /**
     * Recibe TODOS los clics del menú.
     * Con getSource() se sabe cuál opción se presionó y se decide qué hacer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object fuente = e.getSource();

        if (fuente == itemInicio) {
            cardLayout.show(panelContenedor, PANEL_INICIO);
        } else if (fuente == itemConfiguracion) {
            mostrarConfiguracion();
        } else if (fuente == itemSalir) {
            salir();
        } else if (fuente == itemEstudiantes) {
            vistaEstudiante.cargarEstudiantes();
            cardLayout.show(panelContenedor, PANEL_ESTUDIANTES);
        } else if (fuente == itemDocentes) {
            vistaDocente.cargarDocentes();
            cardLayout.show(panelContenedor, PANEL_DOCENTES);
        } else if (fuente == itemMaterias) {
            mostrarEnConstruccion("Materia");
        } else if (fuente == itemGrupos) {
            mostrarEnConstruccion("Grupo");
        } else if (fuente == itemInscripciones) {
            mostrarEnConstruccion("Inscripción");
        }
    }

    private void mostrarEnConstruccion(String modulo) {
        JOptionPane.showMessageDialog(this,
                "El módulo " + modulo + " está en construcción.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===================== PANTALLA DE INICIO =====================

    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_SECUNDARIO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Logo de la universidad (si la imagen no existe, no se muestra y el programa sigue normal)
        URL rutaLogo = getClass().getResource("/imagenes/logo-uniajc.png");
        if (rutaLogo != null) {
            ImageIcon iconoOriginal = new ImageIcon(rutaLogo);
            if (iconoOriginal.getIconHeight() > 0) {
                // Escalar el logo a 150 píxeles de alto, sin deformarlo
                int alto = 150;
                int ancho = iconoOriginal.getIconWidth() * alto / iconoOriginal.getIconHeight();
                Image imagenEscalada = iconoOriginal.getImage()
                        .getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

                gbc.gridy = 0;
                panel.add(new JLabel(new ImageIcon(imagenEscalada)), gbc);
            }
        }

        gbc.gridy = 1;
        panel.add(crearEtiqueta("Sistema de Gestión Académica", 30, COLOR_PRIMARIO), gbc);

        gbc.gridy = 2;
        panel.add(crearEtiqueta("UNIAJC - Universidad Antonio José Camacho", 16, COLOR_TEXTO_SECUNDARIO), gbc);

        gbc.gridy = 3;
        panel.add(crearEtiqueta("Bienvenido. Seleccione una opción del menú para comenzar.", 15, COLOR_EXITO), gbc);

        return panel;
    }

    private JLabel crearEtiqueta(String texto, int tamano, Color color) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, tamano));
        label.setForeground(color);
        return label;
    }

    // ===================== PANTALLA DE CONFIGURACIÓN =====================

    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_SECUNDARIO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(crearEtiqueta("Configuración", 24, COLOR_PRIMARIO), gbc);

        // Registros por página
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(crearEtiqueta("Registros por página:", 14, COLOR_PRIMARIO), gbc);

        comboRegistros = new JComboBox<>(new Integer[]{5, 10, 20});
        comboRegistros.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1;
        panel.add(comboRegistros, gbc);

        // Confirmar eliminaciones
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(crearEtiqueta("Confirmar eliminaciones:", 14, COLOR_PRIMARIO), gbc);

        comboConfirmar = new JComboBox<>(new String[]{"si", "no"});
        comboConfirmar.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1;
        panel.add(comboConfirmar, gbc);

        // Botones (estos sí usan lambdas, porque son botones de un panel)
        JButton botonGuardar = VistaEstudianteSwing.crearBoton("Guardar", COLOR_EXITO);
        botonGuardar.addActionListener(e -> guardarConfiguracion());

        JButton botonCancelar = VistaEstudianteSwing.crearBoton("Cancelar", COLOR_GRIS);
        botonCancelar.addActionListener(e -> cardLayout.show(panelContenedor, PANEL_INICIO));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(COLOR_SECUNDARIO);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonCancelar);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        return panel;
    }

    /**
     * Abre la configuración mostrando los valores que están en uso.
     */
    private void mostrarConfiguracion() {
        comboRegistros.setSelectedItem(vistaEstudiante.getRegistrosPorPagina());
        comboConfirmar.setSelectedItem(vistaEstudiante.isConfirmarEliminaciones() ? "si" : "no");
        cardLayout.show(panelContenedor, PANEL_CONFIGURACION);
    }

    /**
     * Aplica la configuración a las pantallas de estudiantes y docentes.
     */
    private void guardarConfiguracion() {
        int registros = (Integer) comboRegistros.getSelectedItem();
        boolean confirmar = "si".equals(comboConfirmar.getSelectedItem());

        vistaEstudiante.setRegistrosPorPagina(registros);
        vistaEstudiante.setConfirmarEliminaciones(confirmar);
        vistaDocente.setRegistrosPorPagina(registros);
        vistaDocente.setConfirmarEliminaciones(confirmar);

        JOptionPane.showMessageDialog(this, "Configuración guardada.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cardLayout.show(panelContenedor, PANEL_INICIO);
    }

    // ===================== SALIR =====================

    /**
     * Pregunta si desea salir, cierra la conexión y termina el programa.
     */
    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea salir del sistema?",
                "Salir", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            DatabaseConexion.closeConnection();
            dispose();
            System.exit(0);
        }
    }
}