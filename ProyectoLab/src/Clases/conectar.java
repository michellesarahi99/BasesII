package Clases;
 
import java.sql.*;
import javax.swing.*;
public class conectar {
    Connection conect = null;
    public Connection conexion()
        {
            try {
                //Cargamos el Driver MySQL
                Class.forName("com.mysql.jdbc.Driver");
                //Class.forName("org.gjt.mm.mysql.Driver");
                conect = DriverManager.getConnection("jdbc:mysql://localhost:3306/Clinica","root","kirby777");
                System.out.println("conexion establecida");
                //JOptionPane.showMessageDialog(null,"Conectado");
                //Cargamos el Driver Access
                //Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                //Conectar en red base
                //String strConect = "jdbc:odbc:Driver=Microsoft Access Driver (*.mdb)";DBQ=//servidor/bd_cw/cw.mdb";
                //Conectar Localmente
                //String strConect = "jdbc:odbc:Driver=Microsoft Access Driver (*.mdb)";DBQ=:/cwnetbeans/cw.mdb";
                //conect = DriverManager.getConnection(strConect,"","");
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("error de conexion");
                JOptionPane.showMessageDialog(null,"Error de conexion"+e);
            }
            return conect;
        }
     
}
