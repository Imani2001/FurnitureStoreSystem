package reports;

import database.DatabaseConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;

public class ReportRunner {

    public static void show(Component parent, String jrxmlFile) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            InputStream is = ReportRunner.class.getResourceAsStream("/reports/" + jrxmlFile);
            if (is == null) {
                UIHelper.showError(parent, "Report file not found: /reports/" + jrxmlFile);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(is);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, new HashMap<>(), conn);

            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Report - " + jrxmlFile);
            viewer.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            UIHelper.showError(parent, "Report failed:\n" + ex.getMessage());
        }
    }
}