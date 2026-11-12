package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

public class VistaMateriaSwing extends JFrame {

    private final ControladorMateria controlador =
        new ControladorMateria(new MateriaService(new MateriaDao()));

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
        new String[] {"ID", "Materia", "Créditos"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextField txtBuscar = new JTextField(20);

    public VistaMateriaSwing() {
        setTitle("Gestión de Materias");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);

        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        cargar();
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargar());
        panel.add(new JLabel("Buscar:"));
        panel.add(txtBuscar);
        panel.add(btnBuscar);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> registrar());

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editar());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminar());

        JButton btnActualizar = new JButton("Actualizar lista");
        btnActualizar.addActionListener(e -> {
            txtBuscar.setText("");
            cargar();
        });

        panel.add(btnRegistrar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);
        return panel;
    }

    private void cargar() {
        modeloTabla.setRowCount(0);
        String filtro = txtBuscar.getText().trim().toLowerCase();
        try {
            List<Materia> lista = controlador.listarMaterias();
            for (Materia m : lista) {
                boolean coincide = filtro.isEmpty()
                    || String.valueOf(m.getIdMateria()).contains(filtro)
                    || m.getNombreMateria().toLowerCase().contains(filtro);
                if (coincide) {
                    modeloTabla.addRow(new Object[] {
                        m.getIdMateria(), m.getNombreMateria(), m.getCreditos()});
                }
            }
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

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
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la materia con ID " + id + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminarMateria(id);
                exito("Materia eliminada.");
            } catch (RuntimeException ex) {
                mostrarError(ex.getMessage());
            }
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
        cargar();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Solo para probar esta vista sin tocar Main. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VistaMateriaSwing().setVisible(true));
    }
}