package com.uniajc.vista;

// Clases de AWT para colores, fuentes y organización
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.util.Arrays;

// Clases de Swing para la interfaz gráfica
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

// Importar el controlador y el modelo
import com.uniajc.controlador.ControladorUsuario;
import com.uniajc.modelo.Usuario;

/**
 * Ventana de inicio de sesión. Aparece antes de la ventana principal.
 * Es un JDialog "modal": el programa espera aquí hasta que el usuario
 * entre o cierre la ventana.
 * La vista NO valida ni cifra: le pasa los datos al Controlador.
 */
public class VistaLoginSwing extends JDialog {

    // Número máximo de intentos antes de cerrar el sistema
    private static final int MAXIMO_INTENTOS = 3;

    private static final Color COLOR_PRIMARIO = VistaEstudianteSwing.COLOR_PRIMARIO;
    private static final Color COLOR_SECUNDARIO = VistaEstudianteSwing.COLOR_SECUNDARIO;
    private static final Color COLOR_EXITO = VistaEstudianteSwing.COLOR_EXITO;
    private static final Color COLOR_GRIS = VistaEstudianteSwing.COLOR_GRIS;

    private final ControladorUsuario controlador;

    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JLabel labelMensaje;

    private int intentosFallidos = 0;
    private Usuario usuarioAutenticado; // Queda en null si no entró

    /**
     * Constructor: recibe el controlador y arma la ventana.
     * @param controlador Controlador de usuarios
     */
    public VistaLoginSwing(ControladorUsuario controlador) {
        super((Frame) null, "Iniciar sesión - Sistema Académico UNIAJC", true);
        this.controlador = controlador;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(crearPanel());
        pack();
        setResizable(false);
        setLocationRelativeTo(null); // Centrar en la pantalla
    }

    /**
     * Arma el formulario: logo, título, usuario, contraseña y botones.
     */
    private JPanel crearPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_SECUNDARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Logo (si la imagen no existe, simplemente no se muestra)
        URL rutaLogo = getClass().getResource("/imagenes/logo-uniajc.png");
        if (rutaLogo != null) {
            ImageIcon icono = new ImageIcon(rutaLogo);
            if (icono.getIconHeight() > 0) {
                int alto = 90;
                int ancho = icono.getIconWidth() * alto / icono.getIconHeight();
                Image escalada = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                gbc.gridy = 0;
                panel.add(new JLabel(new ImageIcon(escalada)), gbc);
            }
        }

        JLabel titulo = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(COLOR_PRIMARIO);
        gbc.gridy = 1;
        panel.add(titulo, gbc);

        campoUsuario = new JTextField(18);
        campoContrasena = new JPasswordField(18); // Muestra puntos en vez de letras
        agregarCampo(panel, gbc, 2, "Usuario:", campoUsuario);
        agregarCampo(panel, gbc, 3, "Contraseña:", campoContrasena);

        // Mensaje de error debajo de los campos
        labelMensaje = new JLabel(" ", SwingConstants.CENTER);
        labelMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelMensaje.setForeground(VistaEstudianteSwing.COLOR_ACENTO);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(labelMensaje, gbc);

        JButton botonIngresar = VistaEstudianteSwing.crearBoton("Ingresar", COLOR_EXITO);
        botonIngresar.addActionListener(e -> ingresar());

        JButton botonSalir = VistaEstudianteSwing.crearBoton("Salir", COLOR_GRIS);
        botonSalir.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(COLOR_SECUNDARIO);
        panelBotones.add(botonIngresar);
        panelBotones.add(botonSalir);

        gbc.gridy = 5;
        gbc.insets = new Insets(15, 6, 6, 6);
        panel.add(panelBotones, gbc);

        getRootPane().setDefaultButton(botonIngresar); // Enter = Ingresar
        return panel;
    }

    /**
     * Agrega una fila: etiqueta a la izquierda y campo a la derecha.
     */
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
            String texto, JComponent campo) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(COLOR_PRIMARIO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(220, 30));

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
     * Intenta iniciar sesión con lo que escribió el usuario.
     */
    private void ingresar() {
        char[] contrasena = campoContrasena.getPassword();
        try {
            usuarioAutenticado = controlador.iniciarSesion(campoUsuario.getText(), contrasena);
            dispose(); // Entró: se cierra el login y Main abre la ventana principal
        } catch (IllegalArgumentException ex) {
            // Datos incorrectos (los valida el Servicio)
            intentosFallidos++;
            int restantes = MAXIMO_INTENTOS - intentosFallidos;

            if (restantes <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Superó el número máximo de intentos. El sistema se cerrará.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
            labelMensaje.setText(ex.getMessage() + " Intentos restantes: " + restantes);
            campoContrasena.setText("");
            campoContrasena.requestFocusInWindow();
        } catch (RuntimeException ex) {
            // Errores de conexión con la base de datos
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Borrar la contraseña de la memoria
            Arrays.fill(contrasena, '\0');
        }
    }

    /**
     * Devuelve el usuario que inició sesión, o null si cerró la ventana sin entrar.
     */
    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}
