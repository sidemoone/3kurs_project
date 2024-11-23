import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginWindow();
        });
    }
}

class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        setTitle("Вхід (Гринюк Олександр АС-221)");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Логін:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        loginButton = new JButton("Вхід");
        loginButton.setBounds(10, 80, 255, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });

        add(panel);
        setVisible(true);
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("Dispatcher") && password.equals("0000")) {
            new DispatcherWindow();
            this.dispose();
        } else if (username.equals("Driver") && password.equals("1111")) {
            new DriverWindow();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Невірний логін або пароль", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class DispatcherWindow extends JFrame {
    private JTable clientsTable;
    private JTable ordersTable;
    private JTable driversTable;
    private JButton manageDriversButton;
    private JButton maxDistanceButton;
    private JButton maxWeightButton;
    private JButton avgCostButton;

    private DataHandler dataHandler;

    public DispatcherWindow() {
        setTitle("Інтерфейс диспетчера");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dataHandler = new DataHandler();

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Таблиця клієнтів
        clientsTable = new JTable();
        updateClientsTable();
        JScrollPane clientsScrollPane = new JScrollPane(clientsTable);
        clientsScrollPane.setBounds(10, 10, 370, 250);
        panel.add(clientsScrollPane);

        // Таблиця замовлень
        ordersTable = new JTable();
        updateOrdersTable();
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBounds(400, 10, 370, 250);
        panel.add(ordersScrollPane);

        // Кнопки управління
        manageDriversButton = new JButton("Таблиця водія");
        manageDriversButton.setBounds(10, 270, 180, 25);
        panel.add(manageDriversButton);

        maxDistanceButton = new JButton("Найбільша відстань");
        maxDistanceButton.setBounds(200, 270, 180, 25);
        panel.add(maxDistanceButton);

        maxWeightButton = new JButton("Найбільш вагоме перевезення");
        maxWeightButton.setBounds(400, 270, 180, 25);
        panel.add(maxWeightButton);

        avgCostButton = new JButton("Середня вартість");
        avgCostButton.setBounds(590, 270, 180, 25);
        panel.add(avgCostButton);

        // Додавання обробників подій
        manageDriversButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логіка для перегляду таблиці водіїв
                new DriversTableWindow();
            }
        });

        maxDistanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логіка для знаходження замовлення з найбільшою відстанню
                String[] order = dataHandler.getMaxDistanceOrder();
                JOptionPane.showMessageDialog(null, "Замовлення з найбільшою відстанню:\n" + orderToString(order));
            }
        });

        maxWeightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логіка для знаходження замовлення з найбільшою вагою
                String[] order = dataHandler.getMaxWeightOrder();
                JOptionPane.showMessageDialog(null, "Замовлення з найбільшою вагою:\n" + orderToString(order));
            }
        });

        avgCostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логіка для знаходження середньої вартості замовлень
                double avgCost = dataHandler.getAverageCost();
                JOptionPane.showMessageDialog(null, "Середня вартість замовлень: " + avgCost + " грн");
            }
        });

        add(panel);
        setVisible(true);
    }

    private void updateClientsTable() {
        List<String[]> clientsData = dataHandler.readClients();
        String[] columnNames = {"ID_client", "Назва компанії", "ФІО клієнта", "адреса", "телефон"};
        String[][] data = new String[clientsData.size()][5];

        for (int i = 0; i < clientsData.size(); i++) {
            data[i] = clientsData.get(i);
        }

        clientsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void updateOrdersTable() {
        List<String[]> ordersData = dataHandler.readOrders();
        String[] columnNames = {"ID_order", "адреса1", "адреса2", "ціна", "вага", "відстань"};
        String[][] data = new String[ordersData.size()][6];

        for (int i = 0; i < ordersData.size(); i++) {
            data[i] = ordersData.get(i);
        }

        ordersTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private String orderToString(String[] order) {
        if (order == null || order.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID_order: ").append(order[0]).append("\n");
        sb.append("адреса1: ").append(order[1]).append("\n");
        sb.append("адреса2: ").append(order[2]).append("\n");
        sb.append("ціна: ").append(order[3]).append("\n");
        sb.append("вага: ").append(order[4]).append("\n");
        sb.append("відстань: ").append(order[5]).append("\n");
        return sb.toString();
    }
}

class DriversTableWindow extends JFrame {
    private JTable driversTable;
    private DataHandler dataHandler;

    public DriversTableWindow() {
        setTitle("Таблиця водія");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        dataHandler = new DataHandler();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        // Таблиця водіїв
        driversTable = new JTable();
        updateDriversTable();
        JScrollPane driversScrollPane = new JScrollPane(driversTable);
        panel.add(driversScrollPane);

        add(panel);
        setVisible(true);
    }

    private void updateDriversTable() {
        List<String[]> driversData = dataHandler.readDrivers();
        String[] columnNames = {"Назва компанії", "ФІО водія", "номер тел", "ID_client", "ID_order"};
        String[][] data = new String[driversData.size()][5];

        for (int i = 0; i < driversData.size(); i++) {
            data[i] = driversData.get(i);
        }

        driversTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
}

class DriverWindow extends JFrame
{
    private JTable clientsTable;
    private JTable ordersTable;
    private JTable driversTable;
    private DataHandler dataHandler;

    public DriverWindow() {
        setTitle("Інтерфейс водія");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dataHandler = new DataHandler();

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Таблиця клієнтів
        clientsTable = new JTable();
        updateClientsTable();
        JScrollPane clientsScrollPane = new JScrollPane(clientsTable);
        clientsScrollPane.setBounds(10, 10, 370, 250);
        panel.add(clientsScrollPane);

        // Таблиця замовлень
        ordersTable = new JTable();
        updateOrdersTable();
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBounds(400, 10, 370, 250);
        panel.add(ordersScrollPane);

        // Таблиця водіїв
        driversTable = new JTable();
        updateDriversTable();
        JScrollPane driversScrollPane = new JScrollPane(driversTable);
        driversScrollPane.setBounds(10, 270, 760, 250);
        panel.add(driversScrollPane);

        add(panel);
        setVisible(true);
    }

    private void updateClientsTable() {
        List<String[]> clientsData = dataHandler.readClients();
        String[] columnNames = {"ID_client", "Назва компанії", "ФІО клієнта", "адреса", "телефон"};
        String[][] data = new String[clientsData.size()][5];

        for (int i = 0; i < clientsData.size(); i++) {
            data[i] = clientsData.get(i);
        }

        clientsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void updateOrdersTable() {
        List<String[]> ordersData = dataHandler.readOrders();
        String[] columnNames = {"ID_order", "адреса1", "адреса2", "ціна", "вага", "відстань"};
        String[][] data = new String[ordersData.size()][6];

        for (int i = 0; i < ordersData.size(); i++) {
            data[i] = ordersData.get(i);
        }

        ordersTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void updateDriversTable() {
        List<String[]> driversData = dataHandler.readDrivers();
        String[] columnNames = {"Назва компанії", "ФІО водія", "номер тел", "ID_client", "ID_order"};
        String[][] data = new String[driversData.size()][5];

        for (int i = 0; i < driversData.size(); i++) {
            data[i] = driversData.get(i);
        }

        driversTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
}

class DataHandler {
    private final String clientsFileName = "clients.txt";
    private final String ordersFileName = "orders.txt";
    private final String driversFileName = "drivers.txt";

    public List<String[]> readClients() {
        return readData(clientsFileName);
    }

    public List<String[]> readOrders() {
        return readData(ordersFileName);
    }

    public List<String[]> readDrivers() {
        return readData(driversFileName);
    }

    public List<String[]> readOrdersForDriver() {
        List<String[]> orders = readData(ordersFileName);
        List<String[]> ordersForDriver = new ArrayList<>();

        // Assuming for Driver, orders are filtered based on some criteria, here all orders are returned
        for (String[] order : orders) {
            ordersForDriver.add(order);
        }

        return ordersForDriver;
    }

    private List<String[]> readData(String fileName) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                data.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public String[] getMaxDistanceOrder() {
        List<String[]> orders = readOrders();
        String[] maxDistanceOrder = null;
        double maxDistance = 0.0;

        for (String[] order : orders) {
            double distance = Double.parseDouble(order[6].replaceAll("[^\\d.]", ""));
            if (distance > maxDistance) {
                maxDistance = distance;
                maxDistanceOrder = order;
            }
        }

        return maxDistanceOrder;
    }

    public String[] getMaxWeightOrder() {
        List<String[]> orders = readOrders();
        String[] maxWeightOrder = null;
        int maxWeight = 0;

        for (String[] order : orders) {
            int weight = Integer.parseInt(order[5].replaceAll("[^\\d]", ""));
            if (weight > maxWeight) {
                maxWeight = weight;
                maxWeightOrder = order;
            }
        }

        return maxWeightOrder;
    }

    public double getAverageCost() {
        List<String[]> orders = readOrders();
        int totalCost = 0;
        int count = 0;

        for (String[] order : orders) {
            int cost = Integer.parseInt(order[4].replaceAll("[^\\d]", ""));
            totalCost += cost;
            count++;
        }

        if (count > 0) {
            return (double) totalCost / count;
        } else {
            return 0.0;
        }
    }
}
