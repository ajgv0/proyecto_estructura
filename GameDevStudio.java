import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Pattern;

// ================================
//  GAMEDEV STUDIO - Equipo 6
// ================================

// ---------- cola: bugs criticos que congelan la version actual ----------
class BugCritico {
    int idBug;
    String moduloAfectado;
    String severidad;
    String programadorAsignado;

    BugCritico(int idBug, String moduloAfectado, String severidad, String programadorAsignado) {
        this.idBug = idBug;
        this.moduloAfectado = moduloAfectado;
        this.severidad = severidad;
        this.programadorAsignado = programadorAsignado;
    }

    public String toString() {
        return "Bug #" + idBug + " - modulo: " + moduloAfectado + " - severidad: " + severidad
                + " - asignado a: " + programadorAsignado;
    }
}

// ---------- pila: tareas de renderizado/arte programadas en el servidor ----------
class TareaRenderizado {
    int idTarea;
    String nombreAsset;
    int tiempoEstimadoMin;
    String formatoOutput;

    TareaRenderizado(int idTarea, String nombreAsset, int tiempoEstimadoMin, String formatoOutput) {
        this.idTarea = idTarea;
        this.nombreAsset = nombreAsset;
        this.tiempoEstimadoMin = tiempoEstimadoMin;
        this.formatoOutput = formatoOutput;
    }

    public String toString() {
        return "Tarea #" + idTarea + " - asset: " + nombreAsset + " - tiempo est: " + tiempoEstimadoMin
                + " min - formato: " + formatoOutput;
    }
}

// ---------- lista: catalogo de personajes, habilidades y objetos ----------
class EntidadJuego {
    int idEntidad;
    String nombre;
    String tipo;
    int nivelPoder;
    boolean estadoActivo;

    EntidadJuego(int idEntidad, String nombre, String tipo, int nivelPoder, boolean estadoActivo) {
        this.idEntidad = idEntidad;
        this.nombre = nombre;
        this.tipo = tipo;
        this.nivelPoder = nivelPoder;
        this.estadoActivo = estadoActivo;
    }

    public String toString() {
        String estado = estadoActivo ? "activo" : "inactivo";
        return "#" + idEntidad + " " + nombre + " (" + tipo + ") - nivel poder: " + nivelPoder
                + " - " + estado;
    }
}

// =====================================================================
//  COMPONENTES VISUALES REUTILIZABLES
// =====================================================================

// ---------- fondo con lineas estilo HUD (el mismo de tu version original) ----------
class HudPanel extends JPanel {
    private final Color accent;

    HudPanel(Color accent) {
        super(new BorderLayout(10, 10));
        this.accent = accent;
        setOpaque(true);
        setBackground(new Color(12, 18, 28));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 12;

        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80));
        g2.setStroke(new BasicStroke(1f));

        g2.drawLine(w / 2, margin, w / 2, h - margin);
        g2.drawLine(margin, h / 2, w - margin, h / 2);

        g2.drawLine(margin, margin, margin + 28, margin);
        g2.drawLine(w - margin, margin, w - margin - 28, margin);
        g2.drawLine(margin, h - margin, margin + 28, h - margin);
        g2.drawLine(w - margin, h - margin, w - margin - 28, h - margin);

        for (int i = 0; i < 7; i++) {
            int offset = 28 + i * 18;
            g2.drawLine(margin + offset, margin, w - margin, margin + offset);
            g2.drawLine(margin, h - margin - offset, w - margin - offset, h - margin);
        }

        g2.setColor(new Color(255, 255, 255, 18));
        g2.drawRoundRect(6, 6, w - 12, h - 12, 12, 12);
        g2.dispose();
    }
}

// ---------- boton redondo con efecto neon (se dibuja solo, sin depender del Look&Feel) ----------
class BotonNeon extends JButton {
    private final Color color;
    private boolean hover = false;
    private boolean seleccionado = false;

    BotonNeon(String texto, Color color) {
        super(texto);
        this.color = color;
        setFont(Tema.FUENTE_BOTON);
        setForeground(Tema.TEXTO);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 16, 10, 16));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    void setSeleccionado(boolean valor) {
        this.seleccionado = valor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int alpha = getModel().isPressed() ? 130 : (seleccionado ? 100 : (hover ? 80 : 40));
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(color);
        g2.setStroke(new BasicStroke((hover || seleccionado) ? 2.2f : 1.5f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}

// ---------- tarjeta de formulario: etiqueta a la izquierda, campo a la derecha ----------
class Formulario extends JPanel {
    private int fila = 0;

    Formulario(Color acento, String titulo) {
        super(new GridBagLayout());
        setOpaque(true);
        setBackground(Tema.PANEL_ALT);
        setBorder(BorderFactory.createCompoundBorder(Tema.borde(titulo, acento), new EmptyBorder(12, 16, 12, 16)));
    }

    void agregar(String etiqueta, JComponent campo) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = fila;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(8, 4, 8, 14);
        add(Tema.etiqueta(etiqueta), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 0, 8, 4);
        add(campo, gc);
        fila++;
    }
}

// ---------- render de celdas para las tablas (filas alternadas + encabezado neon) ----------
class CeldaTabla extends DefaultTableCellRenderer {
    private final Color acento;
    private final boolean cabecera;

    CeldaTabla(Color acento, boolean cabecera) {
        this.acento = acento;
        this.cabecera = cabecera;
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean foco, int fila, int columna) {
        super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
        setOpaque(true);
        if (cabecera) {
            setFont(Tema.FUENTE_BOTON);
            setBackground(new Color(10, 17, 26));
            setForeground(acento);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, acento), new EmptyBorder(0, 10, 0, 10)));
        } else {
            setFont(Tema.FUENTE_NORMAL);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            if (seleccionada) {
                setBackground(Tema.mezclar(acento, Tema.PANEL, 0.35));
                setForeground(Color.WHITE);
            } else {
                setBackground(fila % 2 == 0 ? Tema.PANEL : Tema.PANEL_ALT);
                setForeground("Inactivo".equals(valor) ? Tema.TEXTO_SEC : Tema.TEXTO);
            }
        }
        return this;
    }
}

// ---------- paleta, fuentes y "fabrica" de componentes con el estilo del sistema ----------
final class Tema {
    static final Color FONDO = new Color(4, 9, 15);
    static final Color PANEL = new Color(11, 18, 28);
    static final Color PANEL_ALT = new Color(15, 24, 36);
    static final Color BUGS = new Color(255, 80, 80);
    static final Color RENDER = new Color(64, 206, 255);
    static final Color CATALOGO = new Color(88, 255, 178);
    static final Color HUD = new Color(104, 221, 255);
    static final Color TEXTO = new Color(230, 239, 247);
    static final Color TEXTO_SEC = new Color(164, 182, 201);
    static final Color OK = new Color(88, 255, 178);
    static final Color ERROR = new Color(255, 110, 110);
    static final Color CAMPO_FONDO = new Color(20, 28, 38);
    static final Color BORDE_CAMPO = new Color(90, 110, 140);

    static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 15);
    static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 13);

    private Tema() {
    }

    // colores globales para que dialogos y barras de scroll tambien sean oscuros
    static void aplicarGlobal() {
        UIManager.put("OptionPane.background", PANEL_ALT);
        UIManager.put("Panel.background", PANEL_ALT);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("ScrollBar.thumb", new Color(40, 60, 85));
        UIManager.put("ScrollBar.thumbShadow", HUD);
        UIManager.put("ScrollBar.thumbHighlight", new Color(60, 90, 120));
        UIManager.put("ScrollBar.track", FONDO);
    }

    static Color mezclar(Color a, Color b, double t) {
        return new Color((int) (a.getRed() * t + b.getRed() * (1 - t)),
                (int) (a.getGreen() * t + b.getGreen() * (1 - t)),
                (int) (a.getBlue() * t + b.getBlue() * (1 - t)));
    }

    static TitledBorder borde(String titulo, Color color) {
        TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color, 1, true), titulo);
        b.setTitleFont(FUENTE_TITULO);
        b.setTitleColor(color);
        return b;
    }

    static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_NORMAL);
        l.setForeground(TEXTO_SEC);
        return l;
    }

    static JLabel estado() {
        JLabel l = new JLabel(" ");
        l.setFont(FUENTE_NORMAL);
        l.setForeground(TEXTO_SEC);
        return l;
    }

    static void ok(JLabel l, String texto) {
        l.setForeground(OK);
        l.setText(texto);
    }

    static void error(JLabel l, String texto) {
        l.setForeground(ERROR);
        l.setText(texto);
    }

    private static void estilizarCampo(final JTextField campo) {
        campo.setFont(FUENTE_NORMAL);
        campo.setBackground(CAMPO_FONDO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(HUD);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO, 1, true), new EmptyBorder(6, 8, 6, 8)));
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HUD, 2, true), new EmptyBorder(5, 7, 5, 7)));
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE_CAMPO, 1, true), new EmptyBorder(6, 8, 6, 8)));
            }
        });
    }

    static JTextField campo(int columnas) {
        JTextField c = new JTextField(columnas);
        estilizarCampo(c);
        return c;
    }

    static JPasswordField password(int columnas) {
        JPasswordField c = new JPasswordField(columnas);
        estilizarCampo(c);
        c.setEchoChar('\u2022');
        return c;
    }

    static JComboBox<String> combo(String... items) {
        JComboBox<String> c = new JComboBox<String>(items);
        c.setFont(FUENTE_NORMAL);
        c.setBackground(CAMPO_FONDO);
        c.setForeground(TEXTO);
        return c;
    }

    static JCheckBox check(String texto, boolean marcado) {
        JCheckBox c = new JCheckBox(texto, marcado);
        c.setFont(FUENTE_NORMAL);
        c.setForeground(TEXTO);
        c.setOpaque(false);
        c.setFocusPainted(false);
        return c;
    }

    static JTable tabla(DefaultTableModel modelo, Color acento) {
        JTable t = new JTable(modelo);
        t.setFont(FUENTE_NORMAL);
        t.setRowHeight(28);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setBackground(PANEL);
        t.setForeground(TEXTO);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pintarTabla(t, acento);
        return t;
    }

    static void pintarTabla(JTable t, Color acento) {
        t.setDefaultRenderer(Object.class, new CeldaTabla(acento, false));
        JTableHeader h = t.getTableHeader();
        h.setDefaultRenderer(new CeldaTabla(acento, true));
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 34));
        h.repaint();
    }

    static JScrollPane scroll(JComponent contenido, String titulo, Color acento) {
        JScrollPane sp = new JScrollPane(contenido);
        sp.getViewport().setBackground(PANEL);
        sp.setBackground(PANEL_ALT);
        sp.setBorder(borde(titulo, acento));
        JPanel esquina = new JPanel();
        esquina.setBackground(new Color(10, 17, 26));
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, esquina);
        return sp;
    }
}

// =====================================================================
//  PANTALLA 1: LOGIN (usuario y password)
// =====================================================================
class LoginFrame extends JFrame {
    private static final String USUARIO_OK = "admin";
    private static final String PASSWORD_OK = "1234";
    private static final int MAX_INTENTOS = 3;
    private int intentos = 0;

    LoginFrame() {
        setTitle("GameDev Studio - Inicio de sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setResizable(false);
        setLocationRelativeTo(null);

        HudPanel fondo = new HudPanel(Tema.HUD);
        fondo.setLayout(new GridBagLayout());
        setContentPane(fondo);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setOpaque(true);
        tarjeta.setBackground(Tema.PANEL_ALT);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.HUD, 2, true), new EmptyBorder(26, 38, 26, 38)));
        tarjeta.setPreferredSize(new Dimension(410, 470));

        JLabel titulo = new JLabel("// GAMEDEV STUDIO //", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Tema.HUD);

        JLabel subtitulo = new JLabel("Inicio de sesion - Equipo 6", SwingConstants.CENTER);
        subtitulo.setFont(Tema.FUENTE_NORMAL);
        subtitulo.setForeground(Tema.TEXTO_SEC);

        final JTextField campoUsuario = Tema.campo(18);
        final JPasswordField campoPassword = Tema.password(18);

        final JCheckBox mostrar = Tema.check("Mostrar password", false);
        mostrar.addActionListener(e ->
                campoPassword.setEchoChar(mostrar.isSelected() ? (char) 0 : '\u2022'));

        final JLabel mensaje = new JLabel(" ", SwingConstants.CENTER);
        mensaje.setFont(Tema.FUENTE_NORMAL);
        mensaje.setForeground(Tema.TEXTO_SEC);

        BotonNeon btnIngresar = new BotonNeon("INGRESAR", Tema.HUD);
        BotonNeon btnSalir = new BotonNeon("SALIR", Tema.BUGS);

        JLabel pista = new JLabel("Demo: usuario admin / password 1234", SwingConstants.CENTER);
        pista.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        pista.setForeground(Tema.TEXTO_SEC);

        int y = 0;
        agregar(tarjeta, titulo, y++, 0);
        agregar(tarjeta, subtitulo, y++, 4);
        agregar(tarjeta, Tema.etiqueta("Usuario"), y++, 26);
        agregar(tarjeta, campoUsuario, y++, 4);
        agregar(tarjeta, Tema.etiqueta("Password"), y++, 14);
        agregar(tarjeta, campoPassword, y++, 4);
        agregar(tarjeta, mostrar, y++, 8);
        agregar(tarjeta, mensaje, y++, 10);
        agregar(tarjeta, btnIngresar, y++, 8);
        agregar(tarjeta, btnSalir, y++, 8);
        agregar(tarjeta, pista, y++, 12);

        fondo.add(tarjeta);
        getRootPane().setDefaultButton(btnIngresar);

        btnIngresar.addActionListener(e -> {
            String usuario = campoUsuario.getText().trim();
            String pass = new String(campoPassword.getPassword());

            if (usuario.isEmpty() || pass.isEmpty()) {
                Tema.error(mensaje, "Escribe tu usuario y tu password.");
                return;
            }
            if (usuario.equals(USUARIO_OK) && pass.equals(PASSWORD_OK)) {
                dispose();
                new GameDevStudio(usuario).setVisible(true);
            } else {
                intentos++;
                if (intentos >= MAX_INTENTOS) {
                    JOptionPane.showMessageDialog(this, "Demasiados intentos fallidos. El sistema se cerrara.",
                            "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                Tema.error(mensaje, "Datos incorrectos (intento " + intentos + " de " + MAX_INTENTOS + ").");
                campoPassword.setText("");
                campoPassword.requestFocus();
            }
        });

        btnSalir.addActionListener(e -> System.exit(0));
    }

    private void agregar(JPanel tarjeta, JComponent c, int fila, int espacioArriba) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = fila;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(espacioArriba, 0, 0, 0);
        tarjeta.add(c, gc);
    }
}

// =====================================================================
//  VENTANA PRINCIPAL: menu lateral + pantallas
// =====================================================================
public class GameDevStudio extends JFrame {

    // las tres estructuras del equipo
    Queue<BugCritico> colaBugs = new LinkedList<BugCritico>();
    Stack<TareaRenderizado> pilaRenderizado = new Stack<TareaRenderizado>();
    ArrayList<EntidadJuego> catalogo = new ArrayList<EntidadJuego>();

    // modelos de tabla (se rellenan desde las estructuras en refrescar())
    DefaultTableModel modeloEntidades = modelo("ID", "Nombre", "Tipo", "Nivel de poder", "Estado");
    DefaultTableModel modeloBugs = modelo("ID", "Modulo afectado", "Severidad", "Programador");
    DefaultTableModel modeloTareas = modelo("ID", "Asset", "Tiempo est (min)", "Formato");

    int contadorBug = 1;
    int contadorTarea = 1;
    int contadorEntidad = 1;

    final String usuario;
    static final String[] CLAVES = {"Inicio", "Altas", "Bajas", "Cambios", "Consultas", "Salida"};
    static final String[] TEXTOS_NAV = {"Inicio", "Altas", "Bajas", "Cambios", "Consultas", "Salir"};
    static final Color[] COLORES_NAV = {Tema.HUD, Tema.CATALOGO, Tema.BUGS, Tema.RENDER, Tema.HUD, Tema.BUGS};

    CardLayout layout = new CardLayout();
    JPanel contenido = new JPanel(layout);
    BotonNeon[] nav = new BotonNeon[CLAVES.length];
    JLabel lblEntidades = new JLabel("0");
    JLabel lblBugs = new JLabel("0");
    JLabel lblTareas = new JLabel("0");
    JLabel lblResumen = new JLabel(" ", SwingConstants.CENTER);
    Runnable actualizarConsulta = null;

    public GameDevStudio(String usuario) {
        this.usuario = usuario;
        cargarDatosDemo();

        setTitle("GameDev Studio - Equipo 6");
        setSize(1120, 720);
        setMinimumSize(new Dimension(1000, 640));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mostrar("Salida");
            }
        });

        JPanel raiz = new JPanel(new BorderLayout(12, 12));
        raiz.setBackground(Tema.FONDO);
        raiz.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(raiz);

        contenido.setOpaque(false);
        contenido.add(panelInicio(), "Inicio");
        contenido.add(panelAltas(), "Altas");
        contenido.add(panelBajas(), "Bajas");
        contenido.add(panelCambios(), "Cambios");
        contenido.add(panelConsultas(), "Consultas");
        contenido.add(panelSalida(), "Salida");

        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearMenuLateral(), BorderLayout.WEST);
        raiz.add(contenido, BorderLayout.CENTER);

        mostrar("Inicio");
    }

    // ---------- datos de ejemplo para que las pantallas se vean llenas al revisarlas ----------
    // (borra la llamada a este metodo si quieres empezar con todo vacio)
    void cargarDatosDemo() {
        catalogo.add(new EntidadJuego(contadorEntidad++, "Arthas", "Personaje", 85, true));
        catalogo.add(new EntidadJuego(contadorEntidad++, "Bola de fuego", "Habilidad", 60, true));
        catalogo.add(new EntidadJuego(contadorEntidad++, "Espada runica", "Objeto", 72, true));
        catalogo.add(new EntidadJuego(contadorEntidad++, "Escudo roto", "Objeto", 15, false));
        colaBugs.offer(new BugCritico(contadorBug++, "Fisica", "Alta", "Carlos"));
        colaBugs.offer(new BugCritico(contadorBug++, "Red", "Critica", "Ana"));
        pilaRenderizado.push(new TareaRenderizado(contadorTarea++, "Dragon_Boss", 45, "FBX"));
        pilaRenderizado.push(new TareaRenderizado(contadorTarea++, "Mapa_Bosque", 30, "PNG"));
    }

    static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    // vuelve a llenar las tablas desde las tres estructuras
    void refrescar() {
        modeloEntidades.setRowCount(0);
        for (EntidadJuego e : catalogo) {
            modeloEntidades.addRow(new Object[]{e.idEntidad, e.nombre, e.tipo, e.nivelPoder,
                    e.estadoActivo ? "Activo" : "Inactivo"});
        }
        modeloBugs.setRowCount(0);
        for (BugCritico b : colaBugs) { // el frente de la cola aparece primero
            modeloBugs.addRow(new Object[]{b.idBug, b.moduloAfectado, b.severidad, b.programadorAsignado});
        }
        modeloTareas.setRowCount(0);
        for (int i = pilaRenderizado.size() - 1; i >= 0; i--) { // el tope de la pila aparece primero
            TareaRenderizado t = pilaRenderizado.get(i);
            modeloTareas.addRow(new Object[]{t.idTarea, t.nombreAsset, t.tiempoEstimadoMin, t.formatoOutput});
        }
        lblEntidades.setText(String.valueOf(catalogo.size()));
        lblBugs.setText(String.valueOf(colaBugs.size()));
        lblTareas.setText(String.valueOf(pilaRenderizado.size()));
        lblResumen.setText("Resumen de la sesion:  " + catalogo.size() + " entidades  |  "
                + colaBugs.size() + " bugs pendientes  |  " + pilaRenderizado.size() + " tareas en cola");
    }

    void mostrar(String clave) {
        refrescar();
        layout.show(contenido, clave);
        for (int i = 0; i < CLAVES.length; i++) {
            nav[i].setSeleccionado(CLAVES[i].equals(clave));
        }
        if (actualizarConsulta != null) {
            actualizarConsulta.run();
        }
    }

    // =================== ENCABEZADO Y MENU LATERAL ===================

    JPanel crearEncabezado() {
        JPanel enc = new JPanel(new BorderLayout());
        enc.setOpaque(true);
        enc.setBackground(new Color(10, 17, 26));
        enc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.HUD, 2, true), new EmptyBorder(10, 16, 10, 16)));

        JLabel titulo = new JLabel("// GAMEDEV STUDIO //");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Tema.HUD);

        final JLabel sesion = new JLabel();
        sesion.setFont(Tema.FUENTE_NORMAL);
        sesion.setForeground(Tema.TEXTO_SEC);
        final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        sesion.setText("Usuario: " + usuario + "   |   " + formato.format(new Date()));
        new javax.swing.Timer(1000, e ->
                sesion.setText("Usuario: " + usuario + "   |   " + formato.format(new Date()))).start();

        enc.add(titulo, BorderLayout.WEST);
        enc.add(sesion, BorderLayout.EAST);
        return enc;
    }

    JPanel crearMenuLateral() {
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setOpaque(true);
        lateral.setBackground(Tema.PANEL);
        lateral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(35, 49, 70), 1, true), new EmptyBorder(16, 14, 16, 14)));
        lateral.setPreferredSize(new Dimension(200, 0));

        JLabel menu = new JLabel("MENU PRINCIPAL", SwingConstants.CENTER);
        menu.setFont(Tema.FUENTE_TITULO);
        menu.setForeground(Tema.TEXTO_SEC);
        menu.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel botones = new JPanel(new GridLayout(CLAVES.length, 1, 0, 10));
        botones.setOpaque(false);
        for (int i = 0; i < CLAVES.length; i++) {
            final String clave = CLAVES[i];
            nav[i] = new BotonNeon(TEXTOS_NAV[i], COLORES_NAV[i]);
            nav[i].addActionListener(e -> mostrar(clave));
            botones.add(nav[i]);
        }

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(menu, BorderLayout.NORTH);
        arriba.add(botones, BorderLayout.CENTER);

        JLabel version = new JLabel("v1.0 - Equipo 6", SwingConstants.CENTER);
        version.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        version.setForeground(Tema.TEXTO_SEC);

        lateral.add(arriba, BorderLayout.NORTH);
        lateral.add(version, BorderLayout.SOUTH);
        return lateral;
    }

    // =================== HELPERS DE LAYOUT ===================

    JPanel pantalla(String titulo, String subtitulo, Color acento, JComponent cuerpo) {
        JPanel p = new HudPanel(acento);
        p.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(acento);

        JPanel cab = new JPanel(new GridLayout(2, 1, 0, 2));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(0, 0, 8, 0));
        cab.add(t);
        cab.add(Tema.etiqueta(subtitulo));

        p.add(cab, BorderLayout.NORTH);
        p.add(cuerpo, BorderLayout.CENTER);
        return p;
    }

    // botones tipo "pestana" que cambian entre sub-pantallas
    JPanel submenu(Color[] colores, String[] nombres, JComponent[] paneles) {
        JPanel raiz = new JPanel(new BorderLayout(0, 12));
        raiz.setOpaque(false);
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        final CardLayout cl = new CardLayout();
        final JPanel cartas = new JPanel(cl);
        cartas.setOpaque(false);
        final BotonNeon[] botones = new BotonNeon[nombres.length];
        for (int i = 0; i < nombres.length; i++) {
            final int idx = i;
            final String nombre = nombres[i];
            botones[i] = new BotonNeon(nombre, colores[i]);
            botones[i].addActionListener(e -> {
                cl.show(cartas, nombre);
                for (int j = 0; j < botones.length; j++) {
                    botones[j].setSeleccionado(j == idx);
                }
            });
            barra.add(botones[i]);
            cartas.add(paneles[i], nombre);
        }
        botones[0].setSeleccionado(true);
        raiz.add(barra, BorderLayout.NORTH);
        raiz.add(cartas, BorderLayout.CENTER);
        return raiz;
    }

    // columna vertical de componentes (formulario, botones, mensaje)
    JPanel columna(JComponent... hijos) {
        JPanel c = new JPanel();
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        for (JComponent h : hijos) {
            h.setAlignmentX(Component.LEFT_ALIGNMENT);
            h.setMaximumSize(new Dimension(Integer.MAX_VALUE, h.getPreferredSize().height));
            c.add(h);
            c.add(Box.createVerticalStrut(10));
        }
        c.add(Box.createVerticalGlue());
        c.setPreferredSize(new Dimension(440, 0));
        return c;
    }

    JPanel dividir(JComponent izquierda, JComponent derecha) {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.add(izquierda, BorderLayout.WEST);
        p.add(derecha, BorderLayout.CENTER);
        return p;
    }

    JPanel botonera(BotonNeon... botones) {
        JPanel p = new JPanel(new GridLayout(1, botones.length, 10, 0));
        p.setOpaque(false);
        for (BotonNeon b : botones) {
            p.add(b);
        }
        return p;
    }

    // tabla arriba + fila de botones y mensaje abajo (usado en Bajas)
    JPanel tablaConAcciones(JTable tabla, String titulo, Color acento, String nota,
                            JLabel estado, BotonNeon... botones) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pie.setOpaque(false);
        for (BotonNeon b : botones) {
            pie.add(b);
        }
        pie.add(estado);
        p.add(Tema.etiqueta(nota), BorderLayout.NORTH);
        p.add(Tema.scroll(tabla, titulo, acento), BorderLayout.CENTER);
        p.add(pie, BorderLayout.SOUTH);
        return p;
    }

    Integer entero(String texto) {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    // =================== PANTALLA 2: MENU Y BIENVENIDA ===================

    JPanel panelInicio() {
        JPanel p = new HudPanel(Tema.HUD);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel bienvenida = new JLabel("Bienvenido, " + usuario);
        bienvenida.setFont(new Font("Segoe UI", Font.BOLD, 32));
        bienvenida.setForeground(Tema.TEXTO);

        JPanel cab = new JPanel(new GridLayout(2, 1, 0, 4));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(0, 0, 14, 0));
        cab.add(bienvenida);
        cab.add(Tema.etiqueta("Panel de control del estudio: bugs criticos, pila de renderizado y catalogo del juego."));

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(tarjetaStat(lblEntidades, "Entidades en el catalogo", Tema.CATALOGO));
        stats.add(tarjetaStat(lblBugs, "Bugs criticos pendientes", Tema.BUGS));
        stats.add(tarjetaStat(lblTareas, "Tareas de renderizado en pila", Tema.RENDER));

        BotonNeon btnAlta = new BotonNeon("Registrar (Altas)", Tema.CATALOGO);
        BotonNeon btnBaja = new BotonNeon("Eliminar (Bajas)", Tema.BUGS);
        BotonNeon btnCambio = new BotonNeon("Modificar (Cambios)", Tema.RENDER);
        BotonNeon btnConsulta = new BotonNeon("Consultar", Tema.HUD);
        btnAlta.addActionListener(e -> mostrar("Altas"));
        btnBaja.addActionListener(e -> mostrar("Bajas"));
        btnCambio.addActionListener(e -> mostrar("Cambios"));
        btnConsulta.addActionListener(e -> mostrar("Consultas"));

        JPanel acceso = new JPanel(new GridLayout(1, 4, 14, 0));
        acceso.setOpaque(true);
        acceso.setBackground(Tema.PANEL_ALT);
        acceso.setBorder(BorderFactory.createCompoundBorder(
                Tema.borde("Acceso rapido", Tema.HUD), new EmptyBorder(14, 14, 14, 14)));
        acceso.add(btnAlta);
        acceso.add(btnBaja);
        acceso.add(btnCambio);
        acceso.add(btnConsulta);

        JPanel medio = new JPanel(new BorderLayout(0, 22));
        medio.setOpaque(false);
        medio.add(stats, BorderLayout.NORTH);
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(acceso, BorderLayout.NORTH);
        medio.add(envoltorio, BorderLayout.CENTER);

        p.add(cab, BorderLayout.NORTH);
        p.add(medio, BorderLayout.CENTER);
        return p;
    }

    JPanel tarjetaStat(JLabel numero, String texto, Color color) {
        JPanel t = new JPanel(new BorderLayout(0, 4));
        t.setOpaque(true);
        t.setBackground(Tema.PANEL_ALT);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1, true), new EmptyBorder(18, 22, 18, 22)));
        numero.setFont(new Font("Segoe UI", Font.BOLD, 44));
        numero.setForeground(color);
        t.add(numero, BorderLayout.CENTER);
        t.add(Tema.etiqueta(texto), BorderLayout.SOUTH);
        return t;
    }

    // =================== PANTALLA 3A: ALTAS ===================

    JPanel panelAltas() {
        JPanel sub = submenu(
                new Color[]{Tema.CATALOGO, Tema.BUGS, Tema.RENDER},
                new String[]{"Nueva entidad", "Nuevo bug (push)", "Nueva tarea (enqueue)"},
                new JComponent[]{altaEntidad(), altaBug(), altaTarea()});
        return pantalla("Altas - Registrar informacion", "Da de alta entidades, bugs criticos y tareas de renderizado.",
                Tema.CATALOGO, sub);
    }

    JPanel altaEntidad() {
        Color ac = Tema.CATALOGO;
        final JTextField nombre = Tema.campo(20);
        final JComboBox<String> tipo = Tema.combo("Personaje", "Habilidad", "Objeto");
        final JTextField nivel = Tema.campo(20);
        final JCheckBox activo = Tema.check("Estado activo", true);
        final JLabel estado = Tema.estado();

        Formulario f = new Formulario(ac, "Nueva entidad del catalogo");
        f.agregar("Nombre:", nombre);
        f.agregar("Tipo:", tipo);
        f.agregar("Nivel de poder:", nivel);
        f.agregar("", activo);

        final Runnable limpiar = () -> {
            nombre.setText("");
            nivel.setText("");
            tipo.setSelectedIndex(0);
            activo.setSelected(true);
            nombre.requestFocus();
        };

        BotonNeon guardar = new BotonNeon("Guardar (insert)", ac);
        BotonNeon btnLimpiar = new BotonNeon("Limpiar", Tema.HUD);
        guardar.addActionListener(e -> {
            String n = nombre.getText().trim();
            Integer nv = entero(nivel.getText());
            if (n.isEmpty()) {
                Tema.error(estado, "Escribe el nombre de la entidad.");
                return;
            }
            if (nv == null || nv < 0) {
                Tema.error(estado, "El nivel de poder debe ser un numero entero.");
                return;
            }
            EntidadJuego en = new EntidadJuego(contadorEntidad++, n, (String) tipo.getSelectedItem(),
                    nv, activo.isSelected());
            catalogo.add(en);
            refrescar();
            Tema.ok(estado, "Entidad #" + en.idEntidad + " agregada al catalogo.");
            limpiar.run();
        });
        btnLimpiar.addActionListener(e -> {
            limpiar.run();
            estado.setText(" ");
        });

        return dividir(columna(f, botonera(guardar, btnLimpiar), estado),
                Tema.scroll(Tema.tabla(modeloEntidades, ac), "Catalogo actual", ac));
    }

    JPanel altaBug() {
        Color ac = Tema.BUGS;
        final JTextField modulo = Tema.campo(20);
        final JComboBox<String> severidad = Tema.combo("Baja", "Media", "Alta", "Critica");
        final JTextField programador = Tema.campo(20);
        final JLabel estado = Tema.estado();

        Formulario f = new Formulario(ac, "Nuevo bug critico");
        f.agregar("Modulo afectado:", modulo);
        f.agregar("Severidad:", severidad);
        f.agregar("Programador asignado:", programador);

        final Runnable limpiar = () -> {
            modulo.setText("");
            programador.setText("");
            severidad.setSelectedIndex(0);
            modulo.requestFocus();
        };

        BotonNeon guardar = new BotonNeon("Registrar bug (enqueue)", ac);
        BotonNeon btnLimpiar = new BotonNeon("Limpiar", Tema.HUD);
        guardar.addActionListener(e -> {
            String m = modulo.getText().trim();
            String p = programador.getText().trim();
            if (m.isEmpty() || p.isEmpty()) {
                Tema.error(estado, "Llena el modulo y el programador.");
                return;
            }
            BugCritico bug = new BugCritico(contadorBug++, m, (String) severidad.getSelectedItem(), p);
            colaBugs.offer(bug);
            refrescar();
            Tema.ok(estado, "Bug #" + bug.idBug + " registrado en la cola.");
            limpiar.run();
        });
        btnLimpiar.addActionListener(e -> {
            limpiar.run();
            estado.setText(" ");
        });

        return dividir(columna(f, botonera(guardar, btnLimpiar), estado),
                Tema.scroll(Tema.tabla(modeloBugs, ac), "Cola de bugs (el frente aparece primero)", ac));
    }

    JPanel altaTarea() {
        Color ac = Tema.RENDER;
        final JTextField asset = Tema.campo(20);
        final JTextField tiempo = Tema.campo(20);
        final JComboBox<String> formato = Tema.combo("PNG", "FBX", "MP4", "EXR", "OBJ");
        final JLabel estado = Tema.estado();

        Formulario f = new Formulario(ac, "Nueva tarea de renderizado");
        f.agregar("Nombre del asset:", asset);
        f.agregar("Tiempo estimado (min):", tiempo);
        f.agregar("Formato de salida:", formato);

        final Runnable limpiar = () -> {
            asset.setText("");
            tiempo.setText("");
            formato.setSelectedIndex(0);
            asset.requestFocus();
        };

        BotonNeon guardar = new BotonNeon("Agregar a la pila (push)", ac);
        BotonNeon btnLimpiar = new BotonNeon("Limpiar", Tema.HUD);
        guardar.addActionListener(e -> {
            String a = asset.getText().trim();
            Integer t = entero(tiempo.getText());
            if (a.isEmpty()) {
                Tema.error(estado, "Escribe el nombre del asset.");
                return;
            }
            if (t == null || t <= 0) {
                Tema.error(estado, "El tiempo estimado debe ser un numero mayor a 0.");
                return;
            }
            TareaRenderizado tarea = new TareaRenderizado(contadorTarea++, a, t, (String) formato.getSelectedItem());
            pilaRenderizado.push(tarea);
            refrescar();
            Tema.ok(estado, "Tarea #" + tarea.idTarea + " agregada a la pila.");
            limpiar.run();
        });
        btnLimpiar.addActionListener(e -> {
            limpiar.run();
            estado.setText(" ");
        });

        return dividir(columna(f, botonera(guardar, btnLimpiar), estado),
                Tema.scroll(Tema.tabla(modeloTareas, ac), "Pila de renderizado (el tope aparece primero)", ac));
    }

    // =================== PANTALLA 3B: BAJAS ===================

    JPanel panelBajas() {
        JPanel sub = submenu(
                new Color[]{Tema.CATALOGO, Tema.BUGS, Tema.RENDER},
                new String[]{"Eliminar entidad", "Resolver bug (pop)", "Procesar tarea (dequeue)"},
                new JComponent[]{bajaEntidad(), bajaBug(), bajaTarea()});
        return pantalla("Bajas - Eliminar informacion", "Elimina registros del sistema. Las bajas piden confirmacion.",
                Tema.BUGS, sub);
    }

    JPanel bajaEntidad() {
        Color ac = Tema.CATALOGO;
        final JTable tabla = Tema.tabla(modeloEntidades, ac);
        final JLabel estado = Tema.estado();
        BotonNeon eliminar = new BotonNeon("Eliminar seleccionada (delete)", ac);
        eliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                Tema.error(estado, "Selecciona una entidad de la tabla.");
                return;
            }
            EntidadJuego en = catalogo.get(fila);
            int r = JOptionPane.showConfirmDialog(this, "Eliminar \"" + en.nombre + "\" del catalogo?",
                    "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r != JOptionPane.YES_OPTION) {
                return;
            }
            catalogo.remove(fila);
            refrescar();
            Tema.ok(estado, "Entidad eliminada: " + en.nombre);
        });
        return tablaConAcciones(tabla, "Catalogo", ac,
                "Selecciona una fila y presiona eliminar.", estado, eliminar);
    }

    JPanel bajaBug() {
        Color ac = Tema.BUGS;
        final JTable tabla = Tema.tabla(modeloBugs, ac);
        final JLabel estado = Tema.estado();
        BotonNeon dequeue = new BotonNeon("Resolver mas antiguo (dequeue)", ac);
        BotonNeon front = new BotonNeon("Ver bug pendiente (front)", ac);
        dequeue.addActionListener(e -> {
            if (colaBugs.isEmpty()) {
                Tema.error(estado, "No hay bugs criticos pendientes.");
                return;
            }
            BugCritico b = colaBugs.poll();
            refrescar();
            Tema.ok(estado, "Resuelto: bug #" + b.idBug + " (" + b.moduloAfectado + ")");
        });
        front.addActionListener(e -> {
            if (colaBugs.isEmpty()) {
                Tema.error(estado, "No hay bugs criticos en este momento.");
                return;
            }
            tabla.setRowSelectionInterval(0, 0);
            Tema.ok(estado, "Bug pendiente: " + colaBugs.peek());
        });
        return tablaConAcciones(tabla, "Cola de bugs criticos", ac,
                "Cola (FIFO): solo se puede resolver el bug mas antiguo, el de la primera fila.",
                estado, dequeue, front);
    }

    JPanel bajaTarea() {
        Color ac = Tema.RENDER;
        final JTable tabla = Tema.tabla(modeloTareas, ac);
        final JLabel estado = Tema.estado();
        BotonNeon pop = new BotonNeon("Procesar mas reciente (pop)", ac);
        BotonNeon peek = new BotonNeon("Ver siguiente (peek)", ac);
        pop.addActionListener(e -> {
            if (pilaRenderizado.isEmpty()) {
                Tema.error(estado, "No hay tareas de renderizado pendientes.");
                return;
            }
            TareaRenderizado t = pilaRenderizado.pop();
            refrescar();
            Tema.ok(estado, "Procesada: tarea #" + t.idTarea + " (" + t.nombreAsset + ")");
        });
        peek.addActionListener(e -> {
            if (pilaRenderizado.isEmpty()) {
                Tema.error(estado, "No hay tareas de renderizado en este momento.");
                return;
            }
            tabla.setRowSelectionInterval(0, 0);
            Tema.ok(estado, "Siguiente en pila: " + pilaRenderizado.peek());
        });
        return tablaConAcciones(tabla, "Pila de renderizado", ac,
                "Pila (LIFO): solo se procesa la tarea mas reciente, la de la primera fila.",
                estado, pop, peek);
    }

    // =================== PANTALLA 3C: CAMBIOS ===================

    JPanel panelCambios() {
        JPanel sub = submenu(
                new Color[]{Tema.CATALOGO, Tema.BUGS, Tema.RENDER},
                new String[]{"Modificar entidad", "Modificar bug", "Modificar tarea"},
                new JComponent[]{cambioEntidad(), cambioBug(), cambioTarea()});
        return pantalla("Cambios - Modificar informacion", "Selecciona un registro de la tabla, edita sus datos y guarda.",
                Tema.RENDER, sub);
    }

    JPanel cambioEntidad() {
        Color ac = Tema.CATALOGO;
        final JTable tabla = Tema.tabla(modeloEntidades, ac);
        final JTextField nombre = Tema.campo(20);
        final JComboBox<String> tipo = Tema.combo("Personaje", "Habilidad", "Objeto");
        final JTextField nivel = Tema.campo(20);
        final JCheckBox activo = Tema.check("Estado activo", true);
        final JLabel estado = Tema.estado();

        Formulario f = new Formulario(ac, "Modificar entidad seleccionada");
        f.agregar("Nombre:", nombre);
        f.agregar("Tipo:", tipo);
        f.agregar("Nivel de poder:", nivel);
        f.agregar("", activo);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (e.getValueIsAdjusting() || fila < 0 || fila >= catalogo.size()) {
                return;
            }
            EntidadJuego en = catalogo.get(fila);
            nombre.setText(en.nombre);
            tipo.setSelectedItem(en.tipo);
            nivel.setText(String.valueOf(en.nivelPoder));
            activo.setSelected(en.estadoActivo);
            estado.setText(" ");
        });

        BotonNeon guardar = new BotonNeon("Guardar cambios (update)", ac);
        guardar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                Tema.error(estado, "Selecciona una entidad de la tabla.");
                return;
            }
            String n = nombre.getText().trim();
            Integer nv = entero(nivel.getText());
            if (n.isEmpty() || nv == null || nv < 0) {
                Tema.error(estado, "Revisa el nombre y que el nivel sea un numero entero.");
                return;
            }
            EntidadJuego en = catalogo.get(fila);
            en.nombre = n;
            en.tipo = (String) tipo.getSelectedItem();
            en.nivelPoder = nv;
            en.estadoActivo = activo.isSelected();
            refrescar();
            tabla.setRowSelectionInterval(fila, fila);
            Tema.ok(estado, "Entidad #" + en.idEntidad + " actualizada.");
        });

        return dividir(columna(f, botonera(guardar), estado),
                Tema.scroll(tabla, "Catalogo (selecciona una fila)", ac));
    }

    JPanel cambioBug() {
        Color ac = Tema.BUGS;
        final JTable tabla = Tema.tabla(modeloBugs, ac);
        final JTextField modulo = Tema.campo(20);
        final JComboBox<String> severidad = Tema.combo("Baja", "Media", "Alta", "Critica");
        final JTextField programador = Tema.campo(20);
        final JLabel estado = Tema.estado();

        Formulario f = new Formulario(ac, "Modificar bug seleccionado");
        f.agregar("Modulo afectado:", modulo);
        f.agregar("Severidad:", severidad);
        f.agregar("Programador asignado:", programador);

        // la fila 0 de la tabla es el frente de la cola, o sea el primer indice
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (e.getValueIsAdjusting() || fila < 0 || fila >= colaBugs.size()) {
                return;
            }
            BugCritico b = new ArrayList<BugCritico>(colaBugs).get(fila);
            modulo.setText(b.moduloAfectado);
            severidad.setSelectedItem(b.severidad);
            programador.setText(b.programadorAsignado);
            estado.setText(" ");
        });

        BotonNeon guardar = new BotonNeon("Guardar cambios (update)", ac);
        guardar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                Tema.error(estado, "Selecciona un bug de la tabla.");
                return;
            }
            String m = modulo.getText().trim();
            String p = programador.getText().trim();
            if (m.isEmpty() || p.isEmpty()) {
                Tema.error(estado, "Llena el modulo y el programador.");
                return;
            }
            BugCritico b = new ArrayList<BugCritico>(colaBugs).get(fila);
            b.moduloAfectado = m;
            b.severidad = (String) severidad.getSelectedItem();
            b.programadorAsignado = p;
            refrescar();
            tabla.setRowSelectionInterval(fila, fila);
            Tema.ok(estado, "Bug #" + b.idBug + " actualizado.");
        });

        return dividir(columna(f, botonera(guardar), estado),
                Tema.scroll(tabla, "Cola de bugs (selecciona una fila)", ac));
    }

    JPanel cambioTarea() {
        Color ac = Tema.RENDER;
        final JTable tabla = Tema.tabla(modeloTareas, ac);
        final JTextField asset = Tema.campo(20);
        final JTextField tiempo = Tema.campo(20);
        final JComboBox<String> formato = Tema.combo("PNG", "FBX", "MP4", "EXR", "OBJ");
        final JLabel estado = Tema.estado();

        Formulario f = new Formulario(ac, "Modificar tarea seleccionada");
        f.agregar("Nombre del asset:", asset);
        f.agregar("Tiempo estimado (min):", tiempo);
        f.agregar("Formato de salida:", formato);

        // la fila 0 de la tabla es el tope de la pila, o sea el ultimo indice
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (e.getValueIsAdjusting() || fila < 0 || fila >= pilaRenderizado.size()) {
                return;
            }
            TareaRenderizado t = pilaRenderizado.get(pilaRenderizado.size() - 1 - fila);
            asset.setText(t.nombreAsset);
            tiempo.setText(String.valueOf(t.tiempoEstimadoMin));
            formato.setSelectedItem(t.formatoOutput);
            estado.setText(" ");
        });

        BotonNeon guardar = new BotonNeon("Guardar cambios (update)", ac);
        guardar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                Tema.error(estado, "Selecciona una tarea de la tabla.");
                return;
            }
            String a = asset.getText().trim();
            Integer t = entero(tiempo.getText());
            if (a.isEmpty() || t == null || t <= 0) {
                Tema.error(estado, "Revisa el asset y que el tiempo sea un numero mayor a 0.");
                return;
            }
            TareaRenderizado tarea = pilaRenderizado.get(pilaRenderizado.size() - 1 - fila);
            tarea.nombreAsset = a;
            tarea.tiempoEstimadoMin = t;
            tarea.formatoOutput = (String) formato.getSelectedItem();
            refrescar();
            tabla.setRowSelectionInterval(fila, fila);
            Tema.ok(estado, "Tarea #" + tarea.idTarea + " actualizada.");
        });

        return dividir(columna(f, botonera(guardar), estado),
                Tema.scroll(tabla, "Pila de renderizado (selecciona una fila)", ac));
    }

    // =================== PANTALLA 4: CONSULTAS ===================

    JPanel panelConsultas() {
        final JComboBox<String> modulo = Tema.combo("Catalogo (Lista)", "Bugs criticos (Cola)", "Renderizado (Pila)");
        final JTextField buscar = Tema.campo(18);
        final JLabel conteo = new JLabel(" ");
        conteo.setFont(Tema.FUENTE_TITULO);
        conteo.setForeground(Tema.HUD);

        final JTable tabla = Tema.tabla(modeloEntidades, Tema.CATALOGO);
        tabla.setAutoCreateRowSorter(true);
        final JScrollPane scroll = Tema.scroll(tabla, "Resultados (click en un encabezado para ordenar)", Tema.CATALOGO);

        final Runnable aplicar = () -> {
            String txt = buscar.getText().trim();
            @SuppressWarnings("unchecked")
            TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tabla.getRowSorter();
            sorter.setRowFilter(txt.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(txt)));
            conteo.setText(tabla.getRowCount() + " registro(s)");
        };
        actualizarConsulta = aplicar;

        modulo.addActionListener(e -> {
            int i = modulo.getSelectedIndex();
            DefaultTableModel m = (i == 0) ? modeloEntidades : (i == 1 ? modeloBugs : modeloTareas);
            Color c = (i == 0) ? Tema.CATALOGO : (i == 1 ? Tema.BUGS : Tema.RENDER);
            tabla.setModel(m);
            Tema.pintarTabla(tabla, c);
            scroll.setBorder(Tema.borde("Resultados (click en un encabezado para ordenar)", c));
            aplicar.run();
        });
        buscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                aplicar.run();
            }
        });

        BotonNeon limpiar = new BotonNeon("Limpiar filtro", Tema.HUD);
        limpiar.addActionListener(e -> {
            buscar.setText("");
            aplicar.run();
        });

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        filtros.setOpaque(true);
        filtros.setBackground(Tema.PANEL_ALT);
        filtros.setBorder(Tema.borde("Filtros de busqueda", Tema.HUD));
        filtros.add(Tema.etiqueta("Consultar en:"));
        filtros.add(modulo);
        filtros.add(Tema.etiqueta("Buscar:"));
        filtros.add(buscar);
        filtros.add(limpiar);
        filtros.add(conteo);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.add(filtros, BorderLayout.NORTH);
        cuerpo.add(scroll, BorderLayout.CENTER);
        return pantalla("Consultas - Buscar informacion",
                "Consulta cualquiera de las tres estructuras y filtra por cualquier dato.", Tema.HUD, cuerpo);
    }

    // =================== PANTALLA 5: SALIDA ===================

    JPanel panelSalida() {
        JPanel p = new HudPanel(Tema.BUGS);
        p.setLayout(new GridBagLayout());

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setOpaque(true);
        tarjeta.setBackground(Tema.PANEL_ALT);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BUGS, 2, true), new EmptyBorder(30, 44, 30, 44)));

        JLabel titulo = new JLabel("Salir del sistema", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Tema.BUGS);

        JLabel pregunta = new JLabel("Deseas cerrar tu sesion o salir por completo?", SwingConstants.CENTER);
        pregunta.setFont(Tema.FUENTE_NORMAL);
        pregunta.setForeground(Tema.TEXTO);

        lblResumen.setFont(Tema.FUENTE_NORMAL);
        lblResumen.setForeground(Tema.TEXTO_SEC);

        BotonNeon cerrarSesion = new BotonNeon("Cerrar sesion", Tema.HUD);
        BotonNeon salir = new BotonNeon("Salir del sistema", Tema.BUGS);
        BotonNeon cancelar = new BotonNeon("Cancelar", Tema.CATALOGO);

        cerrarSesion.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        salir.addActionListener(e -> System.exit(0));
        cancelar.addActionListener(e -> mostrar("Inicio"));

        JPanel botones = new JPanel(new GridLayout(1, 3, 12, 0));
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(cerrarSesion);
        botones.add(salir);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridy = 0;
        tarjeta.add(titulo, gc);
        gc.gridy = 1;
        gc.insets = new Insets(10, 0, 0, 0);
        tarjeta.add(pregunta, gc);
        gc.gridy = 2;
        gc.insets = new Insets(18, 0, 0, 0);
        tarjeta.add(lblResumen, gc);
        gc.gridy = 3;
        gc.insets = new Insets(28, 0, 0, 0);
        tarjeta.add(botones, gc);

        p.add(tarjeta);
        return p;
    }

    // =================== MAIN ===================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.aplicarGlobal();
            new LoginFrame().setVisible(true);
        });
    }
}
