import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;

public class SWINGCalculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SWINGCalculator::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        Font f = new Font("SansSerif", Font.BOLD, 20);

        JTextField aField = new JTextField("2");
        aField.setHorizontalAlignment(SwingConstants.RIGHT);
        aField.setFont(f);

        JTextField bField = new JTextField("3");
        bField.setHorizontalAlignment(SwingConstants.RIGHT);
        bField.setFont(f);

        JTextField ansField = new JTextField();
        ansField.setHorizontalAlignment(SwingConstants.RIGHT);
        ansField.setFont(f);

        JComboBox<Operation> opCombo = new JComboBox<>();
        opCombo.setFont(f);
        opCombo.addItem(new OpAdd());
        opCombo.addItem(new OpSub());
        opCombo.addItem(new OpMul());
        opCombo.addItem(new OpDiv());

        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(f);
        calcButton.addActionListener(e -> {
            int a = Integer.parseInt(aField.getText());
            int b = Integer.parseInt(bField.getText());
            ansField.setText("" + ((Operation) opCombo.getSelectedItem()).operate(a, b));
        });

        JFrame frame = new JFrame("SWINGCalculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container pane = frame.getContentPane();
        BoxLayout boxLayout = new BoxLayout(pane, BoxLayout.Y_AXIS);
        pane.setLayout(boxLayout);

        pane.add(aField);
        pane.add(bField);
        pane.add(opCombo);
        pane.add(calcButton);
        pane.add(ansField);

        pane.add(createLFArea(frame));

        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel createLFArea(JFrame rootFrame) {
        JPanel lfPanel = new JPanel();
        lfPanel.add(new Label("Look & Feel"));
        JComboBox<LFMgr> lfMgrJComboBox = new JComboBox<>();
        JComboBox<ThemeMgr> themeMgrJComboBox = new JComboBox<>();

        for (LFMgr lfMgr : getInstalledLFItems()) {
            lfMgrJComboBox.addItem(lfMgr);
        }
        lfMgrJComboBox.addItemListener(il -> {
            reApplySelectedLF((LFMgr) il.getItem(), rootFrame, themeMgrJComboBox);
        });

        for (ThemeMgr themeMgr : getMetalThemes()) {
            themeMgrJComboBox.addItem(themeMgr);
        }
        themeMgrJComboBox.setSelectedIndex(-1);
        themeMgrJComboBox.addItemListener(il -> {
            reApplySelectedLF(lfMgrJComboBox, rootFrame, themeMgrJComboBox);
        });

        lfPanel.add(lfMgrJComboBox);
        lfPanel.add(themeMgrJComboBox);
        return lfPanel;
    }

    private static void reApplySelectedLF(LFMgr lfMgr, JFrame rootFrame, JComboBox<ThemeMgr> themeMgrJComboBox) {
        lfMgr.apply(rootFrame, themeMgrJComboBox);
    }

    private static void reApplySelectedLF(JComboBox<LFMgr> lfMgrJComboBox, JFrame rootFrame, JComboBox<ThemeMgr> themeMgrJComboBox) {
        reApplySelectedLF((LFMgr) lfMgrJComboBox.getSelectedItem(), rootFrame, themeMgrJComboBox);
    }

    private static ThemeMgr[] getMetalThemes() {
        return new ThemeMgr[] {
                new ThemeMgr(new DefaultMetalTheme()),
                new ThemeMgr(new OceanTheme())
        };
    }

    private static LFMgr[] getInstalledLFItems() {
        UIManager.LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
        LFMgr[] retVal = new LFMgr[installedLookAndFeels.length];
        for (int i = 0 ; i < installedLookAndFeels.length ; i++) {
            UIManager.LookAndFeelInfo lookAndFeelInfo = installedLookAndFeels[i];
            retVal[i] = new LFMgr(lookAndFeelInfo.getName(), lookAndFeelInfo.getClassName());
        }
        return retVal;
    }

    static abstract class Operation {
        private final String title;
        Operation(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

        abstract int operate(int a, int b);
    }
    static class OpAdd extends Operation {
        OpAdd() {
            super("+");
        }
        int operate(int a, int b) {
            return a + b;
        }
    }
    static class OpSub extends Operation {
        OpSub() {
            super("-");
        }
        int operate(int a, int b) {
            return a - b;
        }
    }
    static class OpMul extends Operation {
        OpMul() {
            super("*");
        }
        int operate(int a, int b) {
            return a * b;
        }
    }
    static class OpDiv extends Operation {
        OpDiv() {
            super("-");
        }
        int operate(int a, int b) {
            return a / b;
        }
    }

    static class LFMgr {
        private final String title;
        private final String className;
        LFMgr(String title, String className) {
            this.title = title;
            this.className = className;
        }
        void apply(JFrame frame, JComboBox<ThemeMgr> themeMgrJComboBox) {
            try {
                if (title.equals("Metal")) {
                    themeMgrJComboBox.setEnabled(true);
                    MetalLookAndFeel.setCurrentTheme((
                            (ThemeMgr) themeMgrJComboBox.getSelectedItem()).theme);
                } else {
                    themeMgrJComboBox.setEnabled(false);
                }
                UIManager.setLookAndFeel(className);
                SwingUtilities.updateComponentTreeUI(frame);
                frame.pack();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return title;
        }
    }

    static class ThemeMgr {
        MetalTheme theme;
        public ThemeMgr(MetalTheme metalTheme) {
            this.theme = metalTheme;
        }

        @Override
        public String toString() {
            return theme.getName();
        }
    }
}
