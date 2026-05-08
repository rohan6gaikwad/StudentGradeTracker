package src;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Optional;

/**
 * Production-grade Swing GUI for the Student Grade Tracker.
 *
 * Demonstrates:
 *  - JFrame, JPanel layout management (BorderLayout, GridBagLayout, CardLayout)
 *  - JTable with custom DefaultTableModel
 *  - JTextField, JButton, JLabel, JScrollPane, JOptionPane
 *  - Event handling via ActionListeners
 *  - Custom color theming and fonts
 *  - Separation of UI from business logic (GradeTracker service)
 *
 * @author Senior Java Developer
 * @version 1.0
 */
public class GradeTrackerGUI extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DARK     = new Color(15, 20, 35);
    private static final Color BG_PANEL    = new Color(22, 30, 50);
    private static final Color BG_CARD     = new Color(30, 42, 68);
    private static final Color ACCENT_BLUE = new Color(64, 156, 255);
    private static final Color ACCENT_GREEN= new Color(52, 211, 153);
    private static final Color ACCENT_RED  = new Color(248, 113, 113);
    private static final Color ACCENT_GOLD = new Color(251, 191, 36);
    private static final Color TEXT_PRIMARY= new Color(226, 232, 240);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);
    private static final Color BORDER_CLR  = new Color(45, 60, 90);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 12);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Backend ───────────────────────────────────────────────────────────────
    private final GradeTracker tracker = new GradeTracker();

    // ── Table & Model ─────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable studentTable;

    // ── Input Fields ──────────────────────────────────────────────────────────
    private JTextField tfName, tfId, tfGrade;

    // ── Stat Labels ──────────────────────────────────────────────────────────
    private JLabel lblTotal, lblClassAvg, lblTopStudent, lblPassFail;

    // ── Console / Report ─────────────────────────────────────────────────────
    private JTextArea reportArea;

    // ── Constructor ───────────────────────────────────────────────────────────
    public GradeTrackerGUI() {
        setTitle("Student Grade Tracker");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        buildUI();
        loadSampleData();
        refreshAll();
    }

    // ── UI Construction ───────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildLeftPanel(), buildRightPanel());
        center.setDividerLocation(420);
        center.setDividerSize(4);
        center.setBorder(null);
        center.setBackground(BG_DARK);
        add(center, BorderLayout.CENTER);

        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ─── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setPreferredSize(new Dimension(0, 64));

        // Left: icon + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        left.setOpaque(false);

        JLabel icon = new JLabel("🎓");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JLabel title = new JLabel("Student Grade Tracker");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);

        JLabel version = new JLabel("v1.0");
        version.setFont(FONT_SMALL);
        version.setForeground(TEXT_MUTED);

        left.add(icon);
        left.add(title);
        left.add(version);

        // Right: stat chips
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 18));
        right.setOpaque(false);

        lblTotal      = createChip("0 Students", ACCENT_BLUE);
        lblClassAvg   = createChip("Avg: 0.00%", ACCENT_GOLD);
        lblTopStudent = createChip("Top: —", ACCENT_GREEN);
        lblPassFail   = createChip("P: 0 | F: 0", TEXT_MUTED);

        right.add(lblTotal);
        right.add(lblClassAvg);
        right.add(lblTopStudent);
        right.add(lblPassFail);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JLabel createChip(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1, true),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        return lbl;
    }

    // ─── Left Panel: Form + Table ─────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        panel.add(buildInputForm(), BorderLayout.NORTH);
        panel.add(buildTableSection(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildInputForm() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1, true),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // Section title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel sec = new JLabel("Add / Manage Student");
        sec.setFont(FONT_HEADER);
        sec.setForeground(ACCENT_BLUE);
        card.add(sec, gbc);

        // Row: Name
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.35;
        card.add(formLabel("Full Name"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        tfName = styledField("e.g. Rahul Sharma");
        card.add(tfName, gbc);

        // Row: ID
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.35;
        card.add(formLabel("Student ID"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        tfId = styledField("e.g. STU001");
        card.add(tfId, gbc);

        // Row: Grade
        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0.35;
        card.add(formLabel("Grade (0-100)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        tfGrade = styledField("e.g. 85.5");
        card.add(tfGrade, gbc);

        // Buttons row
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 4, 4, 4);
        card.add(buildButtonRow(), gbc);

        return card;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);

        row.add(makeButton("➕ Add Student",  ACCENT_GREEN, e -> onAddStudent()));
        row.add(makeButton("📊 Add Grade",    ACCENT_BLUE,  e -> onAddGrade()));
        row.add(makeButton("🗑 Remove",       ACCENT_RED,   e -> onRemoveStudent()));
        row.add(makeButton("📋 Report",       ACCENT_GOLD,  e -> onShowReport()));

        return row;
    }

    private JPanel buildTableSection() {
        // Columns
        String[] columns = {"ID", "Name", "Grades #", "Average", "Highest", "Lowest", "Letter", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        studentTable = new JTable(tableModel);
        styleTable(studentTable);

        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.setBackground(BG_PANEL);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);
        wrapper.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JLabel hdr = new JLabel("  Student Roster");
        hdr.setFont(FONT_HEADER);
        hdr.setForeground(TEXT_MUTED);
        hdr.setBorder(BorderFactory.createEmptyBorder(4, 0, 6, 0));

        wrapper.add(hdr, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ─── Right Panel: Report Area ─────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        JLabel hdr = new JLabel("  Summary Report");
        hdr.setFont(FONT_HEADER);
        hdr.setForeground(TEXT_MUTED);
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(FONT_MONO);
        reportArea.setBackground(BG_PANEL);
        reportArea.setForeground(ACCENT_GREEN);
        reportArea.setCaretColor(ACCENT_GREEN);
        reportArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        reportArea.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(hdr, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 5));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JLabel status = new JLabel("Ready  |  Select a student and click 'Add Grade' to record a score.");
        status.setFont(FONT_SMALL);
        status.setForeground(TEXT_MUTED);
        bar.add(status);
        return bar;
    }

    // ── Event Handlers ────────────────────────────────────────────────────────

    private void onAddStudent() {
        String name = tfName.getText().trim();
        String id   = tfId.getText().trim();
        if (name.isEmpty() || id.isEmpty()) {
            showError("Name and ID are required.");
            return;
        }
        try {
            Student s = new Student(name, id);
            // If a grade is pre-filled, add it immediately
            String gradeText = tfGrade.getText().trim();
            if (!gradeText.isEmpty()) {
                double g = Double.parseDouble(gradeText);
                s.addGrade(g);
            }
            tracker.addStudent(s);
            clearForm();
            refreshAll();
            showSuccess("Student '" + name + "' added.");
        } catch (NumberFormatException ex) {
            showError("Grade must be a valid number.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onAddGrade() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            showError("Select a student from the table first.");
            return;
        }
        String gradeText = tfGrade.getText().trim();
        if (gradeText.isEmpty()) {
            showError("Enter a grade value (0–100).");
            return;
        }
        try {
            double grade = Double.parseDouble(gradeText);
            String id = (String) tableModel.getValueAt(row, 0);
            Optional<Student> opt = tracker.findById(id);
            opt.ifPresent(s -> {
                s.addGrade(grade);
                refreshAll();
                showSuccess(String.format("Grade %.1f added to %s.", grade, s.getName()));
            });
            tfGrade.setText("");
        } catch (NumberFormatException ex) {
            showError("Grade must be a valid number.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onRemoveStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            showError("Select a student to remove.");
            return;
        }
        String id   = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove student " + name + " (" + id + ")?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            tracker.removeStudent(id);
            refreshAll();
            showSuccess("Student removed.");
        }
    }

    private void onShowReport() {
        refreshReport();
    }

    // ── Refresh Helpers ───────────────────────────────────────────────────────

    private void refreshAll() {
        refreshTable();
        refreshStats();
        refreshReport();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student s : tracker.getAllStudents()) {
            tableModel.addRow(new Object[]{
                s.getStudentId(),
                s.getName(),
                s.getGradeCount(),
                s.getGradeCount() == 0 ? "—" : String.format("%.2f", s.calculateAverage()),
                s.getGradeCount() == 0 ? "—" : String.format("%.2f", s.getHighestGrade()),
                s.getGradeCount() == 0 ? "—" : String.format("%.2f", s.getLowestGrade()),
                s.getGradeCount() == 0 ? "—" : s.getLetterGrade(),
                s.getGradeCount() == 0 ? "—" : s.getStatus()
            });
        }
    }

    private void refreshStats() {
        int total = tracker.getStudentCount();
        lblTotal.setText(total + " Student" + (total == 1 ? "" : "s"));
        lblClassAvg.setText(String.format("Avg: %.2f%%", tracker.getClassAverage()));
        tracker.getTopStudent().ifPresentOrElse(
            t -> lblTopStudent.setText("Top: " + t.getName()),
            () -> lblTopStudent.setText("Top: —"));
        lblPassFail.setText("P: " + tracker.getPassCount() + " | F: " + tracker.getFailCount());
    }

    private void refreshReport() {
        reportArea.setText(tracker.generateSummaryReport());
        reportArea.setCaretPosition(0);
    }

    // ── Style Helpers ─────────────────────────────────────────────────────────

    private void styleTable(JTable table) {
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(64, 156, 255, 60));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFocusable(false);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_MUTED);
        header.setFont(FONT_SMALL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setPreferredSize(new Dimension(0, 34));

        // Row coloring via renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? new Color(64, 156, 255, 60) :
                              row % 2 == 0 ? BG_PANEL : BG_CARD);
                setForeground(colorForCell(col, val));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setFont(col == 6 ? FONT_HEADER : FONT_BODY);
                return this;
            }
        });

        // Column widths
        int[] widths = {70, 160, 70, 75, 75, 65, 60, 65};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private Color colorForCell(int col, Object val) {
        if (val == null) return TEXT_PRIMARY;
        String s = val.toString();
        if (col == 6) { // Letter grade
            return switch (s) {
                case "A" -> ACCENT_GREEN;
                case "B" -> ACCENT_BLUE;
                case "C" -> ACCENT_GOLD;
                case "F" -> ACCENT_RED;
                default  -> TEXT_PRIMARY;
            };
        }
        if (col == 7) { // Status
            return "PASS".equals(s) ? ACCENT_GREEN : ACCENT_RED;
        }
        return TEXT_PRIMARY;
    }

    private JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField(14) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2,
                        (getHeight() - g2.getFontMetrics().getHeight()) / 2
                        + g2.getFontMetrics().getAscent());
                    g2.dispose();
                }
            }
        };
        tf.setBackground(BG_PANEL);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT_BLUE);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    private JButton makeButton(String text, Color accent, ActionListener al) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()
                    ? accent.darker()
                    : getModel().isRollover()
                        ? accent.brighter()
                        : new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_SMALL);
        btn.setForeground(accent);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(al);
        return btn;
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        // Subtle: just print to status — keep dialogs minimal
        System.out.println("[OK] " + msg);
    }

    private void clearForm() {
        tfName.setText("");
        tfId.setText("");
        tfGrade.setText("");
    }

    // ── Sample Data ───────────────────────────────────────────────────────────

    private void loadSampleData() {
        Object[][] data = {
            {"Aarav Mehta",     "STU001", new double[]{92, 88, 95, 91}},
            {"Priya Sharma",    "STU002", new double[]{78, 82, 75, 80}},
            {"Rahul Patel",     "STU003", new double[]{55, 60, 58, 62}},
            {"Anjali Singh",    "STU004", new double[]{97, 95, 98, 99}},
            {"Rohan Desai",     "STU005", new double[]{45, 50, 42}},
            {"Neha Joshi",      "STU006", new double[]{70, 73, 68, 72}},
        };

        for (Object[] row : data) {
            Student s = new Student((String) row[0], (String) row[1]);
            for (double g : (double[]) row[2]) {
                s.addGrade(g);
            }
            tracker.addStudent(s);
        }
    }
}
