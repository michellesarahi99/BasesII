/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sun.security.x509.IPAddressName;

/**
 *
 * @author USUARIO
 */
public class Paciente {
    
    String Nombre = "";
    String Apellido = "";
    String Direccion = "";
    String Nit = "";
    Boolean Activo;
    conectar cc = new conectar();
    Connection cn =cc.conexion();
    DefaultTableModel model;

    public Paciente(){}
    
    public void Insertar (String Nombre, String Apellido, String Direccion, String Nit) throws SQLException{
        String sql = "INSERT INTO paciente (Nombre, Apellido, Direccion, Nit, Activo) VALUES (?,?,?,?,?)";     
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, Nombre);
            pst.setString(2, Apellido);
            pst.setString(3, Direccion);
            pst.setString(4, Nit);
            pst.setBoolean(5, true);
            int n = pst.executeUpdate();
            if(n>0)
            {
                JOptionPane.showMessageDialog(null, "Paciente Ingresado");
            }
       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }
   }
    public void InsertarTelefono (String Tipo,String Num, String NIT) throws SQLException{
        String idpaciente = "", TipoTel = "";
        String [] titulos = {"Nombre","Telefono","Tipo"};
        String [] registros = new String[3];
        model = new DefaultTableModel(null, titulos);
        String sql = "SELECT paciente.id FROM paciente Where paciente.Nit = '"+NIT+"'";
         try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                idpaciente= rs.getString("id");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
         sql = "SELECT tipotel.id FROM tipotel WHERE tipotel.TIpo = '"+Tipo+"'";
         try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                TipoTel= rs.getString("id");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        sql = "Insert INTO telefono (telefono.Numero, telefono.Paciente_id, telefono.TipoTel_id) VALUES (?,?,?);";
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, Num);
            pst.setString(2, idpaciente);
            pst.setString(3, TipoTel);
            int n = pst.executeUpdate();
            if(n>0)
            {
                JOptionPane.showMessageDialog(null, "Numero Ingresado Ingresado");
            }
       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }

    }
    
    public DefaultTableModel cargartelefonos (String Busca, String NIT){
        String [] titulos = {"Nombre", "Apellido", "Telefono", "Tipo"};
        String [] registros = new String[4];
        model = new DefaultTableModel(null, titulos);
        if (Busca.compareTo("")==0)
        {
        String sql = "SELECT paciente.Nombre, paciente.Apellido, telefono.Numero, tipotel.TIpo FROM paciente" +
" INNER JOIN telefono ON telefono.Paciente_id = paciente.id" +
" INNER Join tipotel ON telefono.TipoTel_id = tipotel.id" +
" WHERE paciente.Nit = '"+NIT+"';";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Numero");
                registros[3] = rs.getString("Tipo");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        }else 
        {
          String sql = "SELECT paciente.Nombre, paciente.Apellido, telefono.Numero, tipotel.TIpo FROM paciente" +
            "INNER JOIN telefono ON telefono.Paciente_id = paciente.id" +
            "INNER Join tipotel ON telefono.TipoTel_id = tipotel.id" +
            "WHERE paciente.Nit = '"+NIT+"'AND (telefono.Numero = '"+ Busca+"' OR tipotel.TIpo = '"+Busca+"' OR paciente.Nombre = '"+ Busca+"' OR paciente.Apellido = '"+Busca+"')";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Numero");
                registros[3] = rs.getString("Tipo");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }  
        }
        return model;
    }
    
   
    public void Modificar(String Nombre, String Apellido, String Direccion, String Nit){
       String sql = "UPDATE paciente SET paciente.Nombre = '" + Nombre + 
                    "', paciente.Apellido = '" + Apellido +"', paciente.Direccion = '"+ Direccion
                    + "' WHERE Nit = '" + Nit + "';";
       try{
            PreparedStatement pps = cn.prepareStatement(sql);
            pps.executeUpdate();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS");
       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }
   }
   
    public void Deshabilitar(String Nit){
       String sql = "UPDATE paciente SET paciente.Activo= 0 WHERE Nit = '"+ Nit + "'";
       try{
            PreparedStatement pps = cn.prepareStatement(sql);
            pps.executeUpdate();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS");
       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }
   }
    
     public DefaultTableModel cargar (String Busca){
        String [] titulos = {"Nombre", "Apellido", "Direccion", "NIT"};
        String [] registros = new String[4];
        model = new DefaultTableModel(null, titulos);
        if (Busca.compareTo("")==0)
        {
        String sql = "SELECT paciente.Nombre, paciente.Apellido, paciente.Direccion, paciente.Nit FROM paciente WHERE paciente.Activo = '1'";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Direccion");
                registros[3] = rs.getString("Nit");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        }else 
        {
          String sql = "SELECT paciente.Nombre, paciente.Apellido, paciente.Direccion, paciente.Nit FROM paciente "
                  + "WHERE paciente.Activo = '1' AND "
                  + "(paciente.Nombre = '"+ Busca +"' "
                  + "OR paciente.Apellido = '"+Busca+"' "
                  + "OR paciente.Direccion = '"+Busca+"' "
                  + "OR paciente.Nit = '"+Busca+"')";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Direccion");
                registros[3] = rs.getString("Nit");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }  
        }
        return model;
    }
}
