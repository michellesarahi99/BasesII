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

/**
 *
 * @author USUARIO
 */
public class Usuario {
    
    conectar cc = new conectar();
    Connection cn =cc.conexion();
    DefaultTableModel model;

    public Usuario() {
    }
    
     public void Insertar (String Nombre, String Apellido, String Contraseña, String Usuario, Boolean Admin) throws SQLException{
        String sql = "INSERT INTO encargado (encargado.Nombre, encargado.Apellido, encargado.Contrasena, encargado.Usuario, encargado.Admin, encargado.Activo) VALUES (?,?,?,?,?,?);";     
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, Nombre);
            pst.setString(2, Apellido);
            pst.setString(3, Contraseña);
            pst.setString(4, Usuario);
            pst.setBoolean(5, Admin);
            pst.setBoolean(6, true);
            int n = pst.executeUpdate();
            if(n>0)
            {
                JOptionPane.showMessageDialog(null, "Usuario Ingresado");
            }
       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }
   }
   
    public void Modificar(String Nombre, String Apellido, String Contraseña, String Usuario){
       String sql = "UPDATE encargado SET encargado.Contrasena = '"+Contraseña+"', encargado.Usuario = '"+Usuario+"' WHERE encargado.Nombre = '"+Nombre+"' AND encargado.Apellido = '"+Apellido+"';";
       try{
            PreparedStatement pps = cn.prepareStatement(sql);
            pps.executeUpdate();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS");
       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }
   }
   
    public void Deshabilitar(String Nombre, String Apellido, String User){
       String sql = "UPDATE encargado Set encargado.Activo = '0' Where encargado.Nombre = '"+Nombre+"' AND encargado.Apellido = '"+Apellido+"' AND encargado.Usuario = '"+User+"';";
       try{
            PreparedStatement pps = cn.prepareStatement(sql);
            pps.executeUpdate();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS");
       } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }
   }
    
     public DefaultTableModel cargar (String Busca){
        String [] titulos = {"Nombre", "Apellido", "Usuario"};
        String [] registros = new String[3];
        model = new DefaultTableModel(null, titulos);
        if (Busca.compareTo("")==0)
        {
        String sql = "SELECT encargado.Nombre, encargado.Apellido, encargado.Usuario FROM encargado WHERE encargado.Activo = '1'";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Usuario");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        }else 
        {
          String sql = "SELECT encargado.Nombre, encargado.Apellido, encargado.Usuario FROM encargado"
                  + " WHERE encargado.Activo = '1' AND"
                  + "(encargado.Nombre = '"+ Busca +"' "
                  + "OR encargado.Apellido = '"+Busca+"' "
                  + "OR encargado.Usuario = '"+Busca+"')";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Usuario");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }  
        }
        return model;
    }
     public void InsertarTelefono (String Tipo,String Num, String Usuario) throws SQLException{
        String idUsuario = "", TipoTel = "";
        String [] titulos = {"Nombre","Telefono","Tipo"};
        String [] registros = new String[3];
        model = new DefaultTableModel(null, titulos);
        String sql = "SELECT encargado.id FROM encargado Where encargado.Usuario = '"+Usuario+"'";
         try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                idUsuario= rs.getString("id");
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
        sql = "Insert INTO telefono (telefono.Numero, telefono.Encargado_id, telefono.TipoTel_id) VALUES (?,?,?);";
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, Num);
            pst.setString(2, idUsuario);
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
      public DefaultTableModel cargartelefonos (String Busca, String Usuario){
        String [] titulos = {"Nombre", "Apellido", "Usuario","Telefono", "Tipo"};
        String [] registros = new String[5];
        model = new DefaultTableModel(null, titulos);
        if (Busca.compareTo("")==0)
        {
        String sql = "SELECT encargado.Nombre, encargado.Apellido, encargado.Usuario, telefono.Numero, tipotel.TIpo FROM encargado " +
" INNER JOIN telefono ON telefono.Encargado_id = encargado.id" +
" INNER JOIN tipotel ON tipotel.id = telefono.TipoTel_id " +
" WHERE encargado.Usuario = '"+Usuario+"';";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Usuario");
                registros[3] = rs.getString("Numero");
                registros[4] = rs.getString("Tipo");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        }else 
        {
          String sql = "SELECT encargado.Nombre, encargado.Apellido, encargado.Usuario, telefono.Numero, tipotel.TIpo FROM encargado" +
" INNER JOIN telefono ON telefono.Encargado_id = encargado.id" +
" INNER JOIN tipotel ON tipotel.id = telefono.TipoTel_id" +
" WHERE encargado.Usuario = '"+Usuario+"' AND (tipotel.TIpo = '"+Busca+"' OR telefono.Numero = '"+Busca+"');";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                registros[0] = rs.getString("Nombre");
                registros[1] = rs.getString("Apellido");
                registros[2] = rs.getString("Usuario");
                registros[3] = rs.getString("Numero");
                registros[4] = rs.getString("Tipo");
                model.addRow(registros);                
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }  
        }
        return model;
    }
}
    

