/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectolab;

import Clases.conectar;
import Clases.Paciente;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author miche
 */
public class Facturacion extends javax.swing.JFrame {

    conectar cc = new conectar();
    Connection cn =cc.conexion(); 
    Paciente paciente= new Paciente();
    Principal p;
    double Total = 0;
    int idF =0;
    public Facturacion() {
        initComponents();
        this.setLocationRelativeTo(null);
        panelInicio.setVisible(true);
        panelVer.setVisible(false);
        panelFacturacion.setVisible(false);
    }


    
    public String Descripcion()
    {
        String sql = "SELECT Descripcion FROM Estudio where id ="+idEstudio()+";";
                 try {
                     Statement st = cn.createStatement();
                     ResultSet rs = st.executeQuery(sql);
                     if(rs.next())
                     {
                        sql = rs.getString("Descripcion");
                        return sql;
                     }
                } catch (SQLException ex) {
                     JOptionPane.showMessageDialog(null, ex);
             }
        return sql;
    }
    
    public int idEstudio(){
        int est = 0;
    String sql = "SELECT id FROM Estudio where codigo ='"+txtCodigoE.getText()+"'";
                 try {
                     Statement st = cn.createStatement();
                     ResultSet rs = st.executeQuery(sql);
                     if(rs.next())
                     {
                        est = rs.getInt("id");
                     }
                } catch (SQLException ex) {
                     JOptionPane.showMessageDialog(null, ex);
             }
        return est;
    }
  
    public int buscarEncargado(){
        int en = 0;
        String sql = "SELECT id FROM Encargado where Usuario ='"+txtUsuario.getText()+"'";
                 try {
                     Statement st = cn.createStatement();
                     ResultSet rs = st.executeQuery(sql);
                     if(rs.next())
                     {
                        en = rs.getInt("id");
                     }
                } catch (SQLException ex) {
                     JOptionPane.showMessageDialog(null, ex);
             }
        return en;
    }
    
        public int buscarTalonario(){
        int tal = 0;
        String sql = "SELECT count(id) FROM Talonario;";
                 try {
                     Statement st = cn.createStatement();
                     ResultSet rs = st.executeQuery(sql);
                     if(rs.next())
                     {
                        tal = rs.getInt("count(id)");
                     }
                } catch (SQLException ex) {
                     JOptionPane.showMessageDialog(null, ex);
             }
        return tal;
    }
    
    public void existencias() throws SQLException
    {
        DefaultTableModel model;
        String [] registros = new String[5];
        String [] titulo = {"Codigo","Grupo","Nombre","Descripcion","Precio"};
        model = new DefaultTableModel(null, titulo);
        registros[0] = txtCodigoE.getText();
        registros[1] = txtGrupoE.getText();
        registros[2] = txtNombreE.getText();
        registros[3] = Descripcion();
        registros[4] = txtPrecioE.getText();
        Total = Total + Double.parseDouble(txtPrecioE.getText());
        txtTotal.setText(Double.toString(Total));
        model.addRow(registros);
        jTable1.setModel(model);
        detalle(idF);        
    }
    
    public int buscarPrecio(){
        int idprecio = 0;
        String sql = "SELECT id FROM Precio where Estudio_id ='"+idEstudio()+"'";
                 try {
                     Statement st = cn.createStatement();
                     ResultSet rs = st.executeQuery(sql);
                     if(rs.next())
                     {
                        idprecio = rs.getInt("id");
                     }
                } catch (SQLException ex) {
                     JOptionPane.showMessageDialog(null, ex);
             }
        return idprecio;
    }
    
    public void detalle(int fact) throws SQLException{
        String sql;
        int idPrecio;
        idPrecio = buscarPrecio();
        sql = "INSERT INTO DetalleFactura(PrecioVenta, Factura_id, Precio_id)VALUES (?,?,?);";
        PreparedStatement pst = cn.prepareStatement(sql);
            pst.setDouble(1, Double.parseDouble(txtPrecioE.getText()));
            pst.setInt(2, fact);
            pst.setInt(3, idPrecio);
            int n = pst.executeUpdate();
            if(n>0)
            {
                System.out.println("Detalle nuevo,  factura: "+ fact);
            }
    }
    
    public int facturaNula(int cliente) throws SQLException{
        Total =0;
        int idFactura = 0;
        String sql;
        java.util.Date date = new java.util.Date();
        //Object param = new java.sql.Timestamp(date.getTime());    
        sql = "INSERT INTO Factura(FechaHora,NoFactura,Total,Creditos,Impresa,Anulada,Talonario_id,Paciente_id,Encargado_id)"
                + " VALUES (?,?,?,?,?,?,?,?,?)";
        int idP = buscarCliente();
        int idEn = buscarEncargado();
        int tal = buscarTalonario();
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setObject(1, date);
            pst.setInt(2, 0);
            pst.setDouble(3, 0);
            pst.setString(4, "");
            pst.setString(5, "");
            pst.setString(6, "");
            pst.setInt(7, tal);
            pst.setInt(8, idP);
            pst.setInt(9, idEn);
            int n = pst.executeUpdate();
            if(n>0)
            {
                System.out.println("Factura nueva");
                sql = "SELECT id FROM factura WHERE id = (SELECT MAX(id) from factura);";
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                if(rs.next())
                     {
                        idFactura = rs.getInt("id");
                        //idF = idFactura;
                        System.out.println("Factura id: " + idFactura);
                     }
            }
      return idFactura;
    } 
    
        public int buscarCliente()
    {
        int cid = 0;
        String sql = "SELECT id FROM Paciente where nit='"+ txtNIT.getText() +"'";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next())
                     {
                        cid = Integer.parseInt(rs.getString("id"));
                     }           
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error de Conexion", JOptionPane.ERROR_MESSAGE);
        }
        return cid;
    } 
        
    public void insertarPaciente(){
        try {
            String sql = "INSERT INTO Paciente (Nombre, Apellido, Direccion, Nit, Activo) VALUES (?,?,?,?,?)";
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1,txtNombreP.getText());
            pst.setString(2,txtApellidoP.getText());
            pst.setString(3,txtDireccion.getText());
            pst.setString(4,txtNIT.getText());
            pst.setBoolean(5,true);
        } catch (SQLException ex) {
            Logger.getLogger(Facturacion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFacturacion = new javax.swing.JPanel();
        btnSalir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNIT = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNombreP = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        txtApellidoP = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        labelFecha = new javax.swing.JLabel();
        btnFacturar = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtPago = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtVuelto = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txtCodigoE = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtGrupoE = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtPrecioE = new javax.swing.JTextField();
        txtNombreE = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        panelVer = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnAnular = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        btnBuscarFactura = new javax.swing.JButton();
        panelInicio = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenuFacturar = new javax.swing.JMenuItem();
        MenuVer = new javax.swing.JMenuItem();
        MenuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        panelFacturacion.setBackground(new java.awt.Color(0, 0, 51));

        btnSalir.setBackground(new java.awt.Color(0, 0, 0));
        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 0, 51));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 255));
        jLabel1.setText("NIT:");

        txtNIT.setBackground(new java.awt.Color(0, 0, 51));
        txtNIT.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtNIT.setForeground(new java.awt.Color(255, 255, 255));
        txtNIT.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNIT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNITKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 255));
        jLabel2.setText("Nombre:");

        txtNombreP.setBackground(new java.awt.Color(0, 0, 51));
        txtNombreP.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtNombreP.setForeground(new java.awt.Color(255, 255, 255));
        txtNombreP.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 255));
        jLabel3.setText("Dirección: ");

        txtDireccion.setBackground(new java.awt.Color(0, 0, 51));
        txtDireccion.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtDireccion.setForeground(new java.awt.Color(255, 255, 255));
        txtDireccion.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 255));
        jLabel4.setText("Usuario:");

        txtUsuario.setBackground(new java.awt.Color(0, 0, 51));
        txtUsuario.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtUsuario.setForeground(new java.awt.Color(255, 255, 255));
        txtUsuario.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 255));
        jLabel5.setText("Fecha:");

        btnGuardar.setBackground(new java.awt.Color(0, 0, 0));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        txtApellidoP.setBackground(new java.awt.Color(0, 0, 51));
        txtApellidoP.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtApellidoP.setForeground(new java.awt.Color(255, 255, 255));
        txtApellidoP.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(153, 153, 255));
        jLabel15.setText("Apellido:");

        labelFecha.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btnFacturar.setBackground(new java.awt.Color(0, 0, 0));
        btnFacturar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFacturar.setText("Facturación");
        btnFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNIT, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNombreP, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApellidoP, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFacturar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(txtApellidoP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtNIT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(txtNombreP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFecha, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 13, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addComponent(btnFacturar, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(153, 153, 255));
        jLabel11.setText("Pago (Q):");

        txtPago.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtPago.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPago.setText("0");
        txtPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPagoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPagoKeyTyped(evt);
            }
        });

        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotal.setText("0");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(153, 153, 255));
        jLabel12.setText("Total (Q):");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 153, 255));
        jLabel13.setText("Vuelto (Q):");

        txtVuelto.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtVuelto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtVuelto.setText("0");

        jPanel1.setBackground(new java.awt.Color(0, 0, 51));

        txtCodigoE.setBackground(new java.awt.Color(0, 0, 51));
        txtCodigoE.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtCodigoE.setForeground(new java.awt.Color(255, 255, 255));
        txtCodigoE.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 153, 255));
        jLabel6.setText("Nombre");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 153, 255));
        jLabel7.setText("Grupo");

        txtGrupoE.setBackground(new java.awt.Color(0, 0, 51));
        txtGrupoE.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtGrupoE.setForeground(new java.awt.Color(255, 255, 255));
        txtGrupoE.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(153, 153, 255));
        jLabel14.setText("Precio");

        txtPrecioE.setBackground(new java.awt.Color(0, 0, 51));
        txtPrecioE.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtPrecioE.setForeground(new java.awt.Color(255, 255, 255));
        txtPrecioE.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtNombreE.setBackground(new java.awt.Color(0, 0, 51));
        txtNombreE.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtNombreE.setForeground(new java.awt.Color(255, 255, 255));
        txtNombreE.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 255));
        jLabel8.setText("Código");

        btnAgregar.setBackground(new java.awt.Color(0, 0, 0));
        btnAgregar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        jTable1.setBackground(new java.awt.Color(0, 0, 0));
        jTable1.setForeground(new java.awt.Color(153, 153, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(153, 153, 255));
        jLabel16.setText("Nombre del estudio");

        txtBuscar.setBackground(new java.awt.Color(0, 0, 51));
        txtBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(255, 255, 255));
        txtBuscar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtGrupoE, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoE, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtPrecioE, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtNombreE, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigoE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPrecioE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(btnAgregar))
                    .addComponent(txtGrupoE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelFacturacionLayout = new javax.swing.GroupLayout(panelFacturacion);
        panelFacturacion.setLayout(panelFacturacionLayout);
        panelFacturacionLayout.setHorizontalGroup(
            panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFacturacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFacturacionLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFacturacionLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFacturacionLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelFacturacionLayout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtPago, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelFacturacionLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFacturacionLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(26, 26, 26))))
        );
        panelFacturacionLayout.setVerticalGroup(
            panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFacturacionLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFacturacionLayout.createSequentialGroup()
                        .addComponent(btnSalir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel11)
                        .addGroup(panelFacturacionLayout.createSequentialGroup()
                            .addComponent(txtPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(3, 3, 3))))
                .addGroup(panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFacturacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(149, 149, 149))
        );

        getContentPane().add(panelFacturacion, "card2");

        panelVer.setBackground(new java.awt.Color(153, 0, 255));

        jScrollPane2.setViewportView(jTable2);

        btnAnular.setText("Anular");

        btnBuscarFactura.setText("Buscar");

        javax.swing.GroupLayout panelVerLayout = new javax.swing.GroupLayout(panelVer);
        panelVer.setLayout(panelVerLayout);
        panelVerLayout.setHorizontalGroup(
            panelVerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerLayout.createSequentialGroup()
                .addGroup(panelVerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelVerLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelVerLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96)
                        .addComponent(btnBuscarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelVerLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(btnAnular, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(230, Short.MAX_VALUE))
        );
        panelVerLayout.setVerticalGroup(
            panelVerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(panelVerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelVerLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jTextField1))
                    .addComponent(btnBuscarFactura))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAnular)
                .addContainerGap(377, Short.MAX_VALUE))
        );

        getContentPane().add(panelVer, "card3");

        javax.swing.GroupLayout panelInicioLayout = new javax.swing.GroupLayout(panelInicio);
        panelInicio.setLayout(panelInicioLayout);
        panelInicioLayout.setHorizontalGroup(
            panelInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 839, Short.MAX_VALUE)
        );
        panelInicioLayout.setVerticalGroup(
            panelInicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 796, Short.MAX_VALUE)
        );

        getContentPane().add(panelInicio, "card4");

        jMenu1.setText("Opciones");

        MenuFacturar.setText("Facturar");
        MenuFacturar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuFacturarMouseClicked(evt);
            }
        });
        MenuFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuFacturarActionPerformed(evt);
            }
        });
        jMenu1.add(MenuFacturar);

        MenuVer.setText("Ver/Anular facturas");
        MenuVer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuVerMouseClicked(evt);
            }
        });
        MenuVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuVerActionPerformed(evt);
            }
        });
        jMenu1.add(MenuVer);

        MenuSalir.setText("Salir");
        MenuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuSalirActionPerformed(evt);
            }
        });
        jMenu1.add(MenuSalir);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturarActionPerformed
        //panelFacturacion.setVisible(true);
        try {
            idF = facturaNula(buscarCliente());
        } catch (SQLException ex) {
            Logger.getLogger(Facturacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnFacturarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        this.dispose();
        Login inicio = new Login();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void txtNITKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNITKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){   
            cargarPaciente(txtNIT.getText());
        }
    }//GEN-LAST:event_txtNITKeyPressed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try {
            paciente.Insertar(txtNombreP.getText(), txtApellidoP.getText(),txtDireccion.getText(),txtNIT.getText());
//            txtNombreP.setText("");
//            txtApellidoP.setText("");
//            txtDireccion.setText("");
//            txtNIT.setText("");
            txtNombreP.setEnabled(false);
            txtApellidoP.setEnabled(false);
            txtDireccion.setEnabled(false);
            txtNIT.setEnabled(true);
            insertarPaciente();
            btnGuardar.setEnabled(false);
        }catch (SQLException ex) {
            Logger.getLogger(Facturacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtPagoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPagoKeyPressed
            double Pago = 0, vuelto = 0;
            Pago = Double.parseDouble(txtPago.getText());
            vuelto = Pago - Total;
            txtVuelto.setText(Double.toString(vuelto));
    }//GEN-LAST:event_txtPagoKeyPressed

    private void txtPagoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPagoKeyTyped

    }//GEN-LAST:event_txtPagoKeyTyped

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            cargarEstudio(txtBuscar.getText());
            txtBuscar.setText("");
        }
    }//GEN-LAST:event_txtBuscarKeyPressed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        try {
            existencias();
        } catch (SQLException ex) {
            Logger.getLogger(Facturacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void MenuFacturarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuFacturarMouseClicked

    }//GEN-LAST:event_MenuFacturarMouseClicked

    private void MenuVerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuVerMouseClicked

    }//GEN-LAST:event_MenuVerMouseClicked

    private void MenuFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuFacturarActionPerformed
       this.setSize(900, 700);
       panelFacturacion.setVisible(false);
       panelInicio.setVisible(false);
       panelVer.setVisible(false);
       panelFacturacion.setSize(this.getWidth(),this.getHeight());
    }//GEN-LAST:event_MenuFacturarActionPerformed

    private void MenuVerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuVerActionPerformed
        this.setSize(697, 456);
       panelFacturacion.setVisible(false);
       panelInicio.setVisible(false);
       panelVer.setVisible(true);
       panelVer.setSize(this.getWidth(),this.getHeight());
    }//GEN-LAST:event_MenuVerActionPerformed

    private void MenuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuSalirActionPerformed
        p = new Principal();
        this.dispose();
        p.setVisible(true);
    }//GEN-LAST:event_MenuSalirActionPerformed
    
     public void cargarEstudio (String Busca){
        int Grupo_id;
        int id;
        float precio;
        String sql = "SELECT id, Codigo, Nombre, Grupo_id FROM Estudio WHERE Codigo = '"+ Busca +"' "
                  + "OR Nombre = '"+Busca+"';";
        String sql2;
            try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                rs.next();
                id = rs.getInt("id");
                Grupo_id = rs.getInt("Grupo_id");
                if(Grupo_id == 1){txtGrupoE.setText("Examen de sangre");}
                if(Grupo_id == 2){ txtGrupoE.setText("Radiografía");}
                if(Grupo_id == 3){ txtGrupoE.setText("Examen de glucosa");}
                txtCodigoE.setText(rs.getString("Codigo"));
                txtNombreE.setText(rs.getString("Nombre")); 
                txtCodigoE.setEnabled(false);
                txtNombreE.setEnabled(false);
                
                sql2 = "SELECT Precio FROM Precio WHERE Estudio_id = " + id +";";
                rs = st.executeQuery(sql2);
                rs.next();
                precio = rs.getFloat("Precio");
                txtPrecioE.setText(Double.toString(precio));
                txtPrecioE.setEnabled(false);
               
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
    }
     
    public void cargarPaciente (String Busca){
        int resp, n;
    
        String sql = "SELECT paciente.Nombre, paciente.Nit, paciente.Apellido, paciente.Direccion FROM Paciente "
                  + "WHERE  paciente.Nit = '"+ Busca +"' ";
            try {
                
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                
                rs.next();
                if(rs.getString("Nit").equals(Busca)){
                    txtNIT.setText(rs.getString("Nit"));
                    txtNombreP.setText(rs.getString("Nombre"));
                    txtApellidoP.setText(rs.getString("Apellido"));
                    txtDireccion.setText(rs.getString("Direccion"));
                    txtNIT.setEnabled(false);
                    txtNombreP.setEnabled(false);
                    txtApellidoP.setEnabled(false);
                    txtDireccion.setEnabled(false);
                }
            } catch (SQLException ex) {
                txtNIT.setEnabled(true);
                txtNombreP.setEnabled(true);
                txtApellidoP.setEnabled(true);
                txtDireccion.setEnabled(true);
                JOptionPane.showMessageDialog(null, "El paciente no se encuentra registrado");
            }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Facturacion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenuFacturar;
    private javax.swing.JMenuItem MenuSalir;
    private javax.swing.JMenuItem MenuVer;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAnular;
    private javax.swing.JButton btnBuscarFactura;
    private javax.swing.JButton btnFacturar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelFecha;
    private javax.swing.JPanel panelFacturacion;
    private javax.swing.JPanel panelInicio;
    private javax.swing.JPanel panelVer;
    private javax.swing.JTextField txtApellidoP;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigoE;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtGrupoE;
    private javax.swing.JTextField txtNIT;
    private javax.swing.JTextField txtNombreE;
    private javax.swing.JTextField txtNombreP;
    private javax.swing.JTextField txtPago;
    private javax.swing.JTextField txtPrecioE;
    private javax.swing.JTextField txtTotal;
    public static javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txtVuelto;
    // End of variables declaration//GEN-END:variables
}
