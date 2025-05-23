/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mycom.gui.manager;

import java.util.LinkedHashMap;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import mycom.config.SystemConfig;
import mycom.controllers.UserController;
import mycom.models.User;
import mycom.services.PPEManagement;
import mycom.services.UserManagement;
import mycom.utils.WordProcessing;

/**
 *
 * @author sheaw
 */
public class ManagerDashboard extends javax.swing.JInternalFrame {
    User manager;
    UserController usrCtrl;
    /**
     * Creates new form AdminUserManagement
     */
    public ManagerDashboard(UserController usrCtrl) {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null); // set the top frame thingy null
        
        this.manager = usrCtrl.getActiveUser();
        this.usrCtrl = usrCtrl;
        if (this.usrCtrl.getActiveUser().type.equalsIgnoreCase("super admin")) {
            this.usrCtrl.updateActiveUser(UserManagement.getSuperAdmin());
        } else {
            this.usrCtrl.updateActiveUser(UserManagement.getUserById(this.usrCtrl.getActiveUser().getId()));
        }
        
        showTotalUsr();
        showManagerDetails();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            protected void done() {
                lowStockAlert();
            }
        }.execute();
        if (this.usrCtrl.getUserServices().getCRUDManager() || this.usrCtrl.getUserServices().getCRUDStaff()) {
            addUsrButton.setVisible(true);
            addUsrButton.setEnabled(true);
        } else {
            addUsrButton.setVisible(false);
            addUsrButton.setEnabled(false);
        }
        
        if (this.usrCtrl.getUserServices().getInitializeInventory()) {
            initializeInventoryQuickButton.setVisible(true);
            initializeInventoryQuickButton.setEnabled(true);
        } else {
            initializeInventoryQuickButton.setVisible(false);
            initializeInventoryQuickButton.setEnabled(false);
        }
        
        if (this.usrCtrl.getUserServices().getResetInventory()) {
            resetInventoryQuickButton.setVisible(true);
            resetInventoryQuickButton.setEnabled(true);
        } else {
            resetInventoryQuickButton.setVisible(false);
            resetInventoryQuickButton.setEnabled(false);
        }
    }
  
    
    private void lowStockAlert() {
        if (PPEManagement.getLowStockItems().size() > 0) {
            String msg = String.valueOf(PPEManagement.getLowStockItems().size()) + " PPE items are below the threshold quantity of " + String.valueOf(SystemConfig.thresholdQuantity) + " boxes!";
            JOptionPane.showMessageDialog(this, msg, "LOW STOCK ALERT",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showTotalUsr() {
        int totalStaff, totalItems, totalLowStockItems;
//        UserManagement usrServices = usrCtrl.getUserServices();
        totalStaff = mycom.services.UserManagement.getStaffs().size();
        totalItems = this.usrCtrl.getPPEController().getTotalInventoryItems();
        totalLowStockItems = this.usrCtrl.getPPEController().lowStockAlert().size();
        totalStff.setText(String.valueOf(totalStaff)) ;
        totalInventory.setText(String.valueOf(totalItems));
        totalLowStock.setText(String.valueOf(totalLowStockItems));
    }
    
    private void showManagerDetails() {
        managerName.setText(this.manager.getName());
        managerId.setText(this.manager.getId());
        managerRole.setText(WordProcessing.capitalizeEachWord(this.manager.type));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        totalMngPanel = new javax.swing.JPanel();
        totalMngLabel = new javax.swing.JLabel();
        totalMng = new javax.swing.JLabel();
        totalInventory = new javax.swing.JLabel();
        totalUsrPanel = new javax.swing.JPanel();
        totalUsrLabel = new javax.swing.JLabel();
        totalStff = new javax.swing.JLabel();
        totalStfPanel = new javax.swing.JPanel();
        totalStfLabel = new javax.swing.JLabel();
        totalLowStock = new javax.swing.JLabel();
        totalMng1 = new javax.swing.JLabel();
        adminCredentialsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        managerName = new javax.swing.JTextField();
        separator1 = new javax.swing.JLabel();
        separator2 = new javax.swing.JLabel();
        managerId = new javax.swing.JTextField();
        separator3 = new javax.swing.JLabel();
        managerRole = new javax.swing.JTextField();
        changeNameButton = new javax.swing.JButton();
        changePwdButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        resetInventoryQuickButton = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        addUsrButton = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        initializeInventoryQuickButton = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(770, 642));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setAutoscrolls(true);
        jPanel1.setPreferredSize(new java.awt.Dimension(770, 642));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Quick Actions");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 440, -1, -1));

        totalMngPanel.setBackground(new java.awt.Color(211, 229, 246));
        totalMngPanel.setPreferredSize(new java.awt.Dimension(250, 176));

        totalMngLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        totalMngLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalMngLabel.setText("Total Inventory");
        totalMngLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        totalMng.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        totalMng.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalMng.setText("items");
        totalMng.setAlignmentY(1.0F);
        totalMng.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        totalInventory.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        totalInventory.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalInventory.setText("3");
        totalInventory.setAlignmentY(1.0F);
        totalInventory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout totalMngPanelLayout = new javax.swing.GroupLayout(totalMngPanel);
        totalMngPanel.setLayout(totalMngPanelLayout);
        totalMngPanelLayout.setHorizontalGroup(
            totalMngPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(totalMngLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(totalMngPanelLayout.createSequentialGroup()
                .addComponent(totalInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(totalMng, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        totalMngPanelLayout.setVerticalGroup(
            totalMngPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalMngPanelLayout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(totalMngLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(totalMngPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(totalMng, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        jPanel1.add(totalMngPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 90, 180, 150));

        totalUsrPanel.setBackground(new java.awt.Color(211, 229, 246));
        totalUsrPanel.setPreferredSize(new java.awt.Dimension(250, 176));

        totalUsrLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        totalUsrLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalUsrLabel.setText("Total Staff");

        totalStff.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        totalStff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalStff.setText("3");
        totalStff.setAlignmentY(1.0F);

        javax.swing.GroupLayout totalUsrPanelLayout = new javax.swing.GroupLayout(totalUsrPanel);
        totalUsrPanel.setLayout(totalUsrPanelLayout);
        totalUsrPanelLayout.setHorizontalGroup(
            totalUsrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(totalUsrLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(totalStff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        totalUsrPanelLayout.setVerticalGroup(
            totalUsrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalUsrPanelLayout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addComponent(totalUsrLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(totalStff, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jPanel1.add(totalUsrPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 180, 150));

        totalStfPanel.setBackground(new java.awt.Color(211, 229, 246));
        totalStfPanel.setPreferredSize(new java.awt.Dimension(510, 188));

        totalStfLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        totalStfLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalStfLabel.setText("Low Stock Items");
        totalStfLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        totalLowStock.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        totalLowStock.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLowStock.setText("10");
        totalLowStock.setAlignmentY(1.0F);

        totalMng1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        totalMng1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalMng1.setText("items");
        totalMng1.setAlignmentY(1.0F);
        totalMng1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout totalStfPanelLayout = new javax.swing.GroupLayout(totalStfPanel);
        totalStfPanel.setLayout(totalStfPanelLayout);
        totalStfPanelLayout.setHorizontalGroup(
            totalStfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(totalStfLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(totalStfPanelLayout.createSequentialGroup()
                .addComponent(totalLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(totalMng1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        totalStfPanelLayout.setVerticalGroup(
            totalStfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalStfPanelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(totalStfLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(totalStfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(totalLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalMng1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel1.add(totalStfPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, 370, 150));

        adminCredentialsPanel.setBackground(java.awt.SystemColor.controlHighlight);
        adminCredentialsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Calibri Light", 1, 18)); // NOI18N
        jLabel4.setText("Manager Name");
        jLabel4.setAlignmentY(1.0F);
        jLabel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 8, 1, 8));

        jLabel5.setFont(new java.awt.Font("Calibri Light", 1, 18)); // NOI18N
        jLabel5.setText("Manager ID");
        jLabel5.setAlignmentY(1.0F);
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 8, 1, 8));

        jLabel6.setFont(new java.awt.Font("Calibri Light", 1, 18)); // NOI18N
        jLabel6.setText("Role");
        jLabel6.setAlignmentY(1.0F);
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 8, 1, 8));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("User Details");

        managerName.setEditable(false);
        managerName.setBackground(java.awt.SystemColor.controlHighlight);
        managerName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        managerName.setText("hahaha");
        managerName.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));

        separator1.setFont(new java.awt.Font("Tahoma", 1, 21)); // NOI18N
        separator1.setText(":");

        separator2.setFont(new java.awt.Font("Tahoma", 1, 21)); // NOI18N
        separator2.setText(":");

        managerId.setEditable(false);
        managerId.setBackground(java.awt.SystemColor.controlHighlight);
        managerId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        managerId.setText("#MNG00001");
        managerId.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        managerId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managerIdActionPerformed(evt);
            }
        });

        separator3.setFont(new java.awt.Font("Tahoma", 1, 21)); // NOI18N
        separator3.setText(":");

        managerRole.setEditable(false);
        managerRole.setBackground(java.awt.SystemColor.controlHighlight);
        managerRole.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        managerRole.setText("Manager");
        managerRole.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        managerRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managerRoleActionPerformed(evt);
            }
        });

        changeNameButton.setText("Change Name");
        changeNameButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changeNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeNameButtonActionPerformed(evt);
            }
        });

        changePwdButton.setText("Change Password");
        changePwdButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changePwdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePwdButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout adminCredentialsPanelLayout = new javax.swing.GroupLayout(adminCredentialsPanel);
        adminCredentialsPanel.setLayout(adminCredentialsPanelLayout);
        adminCredentialsPanelLayout.setHorizontalGroup(
            adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                        .addComponent(changeNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(changePwdButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                        .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(separator1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(managerName))
                            .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                                .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addGap(33, 33, 33)
                                .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                                        .addComponent(separator2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(managerId, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(11, 11, 11))
                                    .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                                        .addComponent(separator3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(managerRole)))))
                        .addGap(12, 12, 12))))
        );
        adminCredentialsPanelLayout.setVerticalGroup(
            adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminCredentialsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(managerName, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separator1))
                .addGap(26, 26, 26)
                .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(managerId, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separator2))
                .addGap(32, 32, 32)
                .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(managerRole, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separator3))
                .addGap(18, 18, 18)
                .addGroup(adminCredentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(changeNameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changePwdButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(89, 89, 89))
        );

        jPanel1.add(adminCredentialsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 90, 310, 320));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Dashboard");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));

        resetInventoryQuickButton.setBackground(new java.awt.Color(153, 182, 226));
        resetInventoryQuickButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        resetInventoryQuickButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetInventoryQuickButtonMouseClicked(evt);
            }
        });
        resetInventoryQuickButton.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Reset Inventory");
        resetInventoryQuickButton.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, -1, 190, 100));

        jPanel1.add(resetInventoryQuickButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 490, 200, 100));

        addUsrButton.setBackground(new java.awt.Color(153, 182, 226));
        addUsrButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addUsrButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addUsrButtonMouseClicked(evt);
            }
        });
        addUsrButton.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Add User");
        addUsrButton.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, -1, 190, 100));

        jPanel1.add(addUsrButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 490, 200, 100));

        initializeInventoryQuickButton.setBackground(new java.awt.Color(153, 182, 226));
        initializeInventoryQuickButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        initializeInventoryQuickButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                initializeInventoryQuickButtonMouseClicked(evt);
            }
        });
        initializeInventoryQuickButton.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Initialize Inventory");
        initializeInventoryQuickButton.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, -1, 190, 100));

        jPanel1.add(initializeInventoryQuickButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 490, 200, 100));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void managerRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_managerRoleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_managerRoleActionPerformed

    private void changeNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeNameButtonActionPerformed
        String newName = JOptionPane.showInputDialog(this, "New Name", "Modify Manager Name", JOptionPane.QUESTION_MESSAGE);
        
        if (newName != null) {
            LinkedHashMap<String, String> msg = this.usrCtrl.modifyUserName(newName, this.manager.getId());
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(this, "Manager name is updated!");
                User updatedManager = UserManagement.getUserById(this.manager.getId());
                this.usrCtrl = new UserController();
                this.usrCtrl.updateActiveUser(updatedManager);
                this.manager = this.usrCtrl.getActiveUser();
                managerName.setText(this.manager.getName());
            } else {
                JOptionPane.showMessageDialog(this, "Manager name update failed :(\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
                System.out.println("User canceled input.");
        }
    }//GEN-LAST:event_changeNameButtonActionPerformed

    private void changePwdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePwdButtonActionPerformed
        JPasswordField newPwd = new JPasswordField();
        JPasswordField confirmPwd = new JPasswordField();

        Object[] message = {
            "New Password:", newPwd,
            "Confirm Password:", confirmPwd
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Modify Manager Paassword", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String newPassword = newPwd.getText();
            String confirmPassword = confirmPwd.getText();
            if (newPassword.equals(confirmPassword)) {
                LinkedHashMap<String, String> msg = this.usrCtrl.modifyPwd(newPassword, this.manager.getId());
                if (msg.get("success") != null) {
                    JOptionPane.showMessageDialog(this, "Manager password is updated!");
                    User updatedManager = UserManagement.getUserById(this.usrCtrl.getActiveUser().getId());
                    this.usrCtrl = new UserController();
                    this.usrCtrl.updateActiveUser(updatedManager);
                    this.manager = this.usrCtrl.getActiveUser();
                    managerName.setText(this.manager.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Manager password update failed :(\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
                } 
            } else {
                JOptionPane.showMessageDialog(this, "New password incorrect :(", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
                System.out.println("User canceled input.");
        }
    }//GEN-LAST:event_changePwdButtonActionPerformed

    private void addUsrButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addUsrButtonMouseClicked
        String[] roles = {"Staff", "Manager"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
    
        JTextField field1 = new JTextField();

        Object[] message = {
            "User Name:", field1,
            "User Role", roleCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New User", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String tempUserPwd = UserManagement.generateTemporaryPassword();
            String usrName = field1.getText();
            String usrRole = roleCombo.getSelectedItem().toString().toLowerCase();
            LinkedHashMap<String, String> msg = this.usrCtrl.createUser(usrName, tempUserPwd, usrRole);
            User newUsr = UserManagement.getUserById(msg.get("newUserId"));
            if (newUsr != null) {
                JOptionPane.showMessageDialog(this, String.format("New User ('%s', '%s') is created!", newUsr.getId(), newUsr.getName()));
                String newUsrPwd = newUsr.getPwd();
                String showNewUsr = String.format("User ID: %s\nUser Password: %s\n\nPlease notfy the user to update the password for security", newUsr.getId(), newUsr.getPwd());
                JOptionPane.showMessageDialog(this, showNewUsr, "New User Credentials", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, String.format("Fail to create new User '%s' :(\n", usrName) + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_addUsrButtonMouseClicked

    private void initializeInventoryQuickButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_initializeInventoryQuickButtonMouseClicked
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to initialize the inventory?\nThis action cannot be undone!",
            "Confirm Inventory Initialization",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
            );

        if (choice == JOptionPane.YES_OPTION) {
            LinkedHashMap<String, String> msg = this.usrCtrl.getPPEController().initializeInventory();
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(this, "Inventory has been initialized successfully!", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to initialize inventory :(\n" + msg.get("msg"), "Reset Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Inventory initialization canceled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_initializeInventoryQuickButtonMouseClicked

    private void resetInventoryQuickButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetInventoryQuickButtonMouseClicked
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset the inventory?\nThis action cannot be undone!",
            "Confirm Inventory Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
            );

        if (choice == JOptionPane.YES_OPTION) {
            LinkedHashMap<String, String> msg = this.usrCtrl.getPPEController().resetInventory();
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(this, "Inventory has been reset successfully!", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset inventory :(\n" + msg.get("msg"), "Reset Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Inventory reset canceled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_resetInventoryQuickButtonMouseClicked

    private void managerIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_managerIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_managerIdActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addUsrButton;
    private javax.swing.JPanel adminCredentialsPanel;
    private javax.swing.JButton changeNameButton;
    private javax.swing.JButton changePwdButton;
    private javax.swing.JPanel initializeInventoryQuickButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField managerId;
    private javax.swing.JTextField managerName;
    private javax.swing.JTextField managerRole;
    private javax.swing.JPanel resetInventoryQuickButton;
    private javax.swing.JLabel separator1;
    private javax.swing.JLabel separator2;
    private javax.swing.JLabel separator3;
    private javax.swing.JLabel totalInventory;
    private javax.swing.JLabel totalLowStock;
    private javax.swing.JLabel totalMng;
    private javax.swing.JLabel totalMng1;
    private javax.swing.JLabel totalMngLabel;
    private javax.swing.JPanel totalMngPanel;
    private javax.swing.JLabel totalStfLabel;
    private javax.swing.JPanel totalStfPanel;
    private javax.swing.JLabel totalStff;
    private javax.swing.JLabel totalUsrLabel;
    private javax.swing.JPanel totalUsrPanel;
    // End of variables declaration//GEN-END:variables
}
